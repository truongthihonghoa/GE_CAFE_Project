package com.demo.ltud_n10.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.local.SharedPrefsManager;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepositoryImpl implements AuthRepository {

    private final com.demo.ltud_n10.data.remote.ApiService apiService;
    private final SharedPrefsManager prefsManager;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>(null);

    @Inject
    public AuthRepositoryImpl(SharedPrefsManager prefsManager, com.demo.ltud_n10.data.remote.ApiService apiService) {
        this.prefsManager = prefsManager;
        this.apiService = apiService;
        String role = prefsManager.getUserRole();
        if (role != null) {
            String name = "ADMIN".equals(role) ? "Admin User" : "Nhân viên";
            currentUser.setValue(new User("0", "saved@user.com", name, role));
        }
    }

    @Override
    public LiveData<Resource<User>> login(String email, String password) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        com.demo.ltud_n10.data.remote.dto.LoginRequest loginRequest = new com.demo.ltud_n10.data.remote.dto.LoginRequest(email, password);
        apiService.login(loginRequest).enqueue(new retrofit2.Callback<com.demo.ltud_n10.data.remote.dto.LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.demo.ltud_n10.data.remote.dto.LoginResponse> call, retrofit2.Response<com.demo.ltud_n10.data.remote.dto.LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    prefsManager.saveToken(token);
                    String role = response.body().getRole();
                    prefsManager.saveUserRole(role);
                    
                    User user = new User("1", email, role.equals("ADMIN") ? "Admin User" : "Nhân viên", role);
                    currentUser.setValue(user);
                    result.setValue(Resource.success(user));
                } else {
                    result.setValue(Resource.error("Đăng nhập thất bại: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.demo.ltud_n10.data.remote.dto.LoginResponse> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<String>> resetPassword(String phone) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            result.setValue(Resource.success("OTP đã được gửi tới " + phone));
        }, 1000);
        return result;
    }

    @Override
    public void logout() {
        prefsManager.clear();
        currentUser.setValue(null);
    }

    @Override
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isLoggedIn() {
        return prefsManager.getToken() != null;
    }
}
