package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.local.SharedPrefsManager;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.LoginRequest;
import com.demo.ltud_n10.data.remote.dto.LoginResponse;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthRepositoryImpl implements AuthRepository {

    private final SharedPrefsManager prefsManager;
    private final ApiService apiService;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>(null);

    @Inject
    public AuthRepositoryImpl(SharedPrefsManager prefsManager, ApiService apiService) {
        this.prefsManager = prefsManager;
        this.apiService = apiService;
        
        String role = prefsManager.getUserRole();
        if (role != null) {
            currentUser.setValue(new User("0", "user@coffee.com", "User", role));
        }
    }

    @Override
    public LiveData<Resource<User>> login(String username, String password) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.login(new LoginRequest(username, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccess();
                    prefsManager.saveToken(token);
                    
                    // Logic phân quyền dựa trên logic Django của bạn:
                    String role = "EMPLOYEE";
                    if (username.equals("ThuyLai") || username.contains("admin")) {
                        role = "ADMIN"; // Tương ứng is_superuser
                    } else if (username.contains("manager")) {
                        role = "MANAGER"; // Tương ứng is_staff
                    }
                    
                    prefsManager.saveUserRole(role);
                    User user = new User("1", username, username, role);
                    currentUser.setValue(user);
                    result.setValue(Resource.success(user));
                } else {
                    result.setValue(Resource.error("Tài khoản hoặc mật khẩu không chính xác", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<String>> resetPassword(String phone) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.success("Chức năng đang phát triển"));
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
