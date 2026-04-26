package com.demo.ltud_n10.data.repository;

import android.os.Handler;
import android.os.Looper;

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

import java.util.HashMap;
import java.util.Map;

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
    private final Map<String, String> userFullNameMap = new HashMap<>();

    @Inject
    public AuthRepositoryImpl(SharedPrefsManager prefsManager, ApiService apiService) {
        this.prefsManager = prefsManager;
        this.apiService = apiService;
        setupUserNames();

        String role = prefsManager.getUserRole();
        if (role != null) {
            // Khởi tạo user tạm thời từ prefs, sẽ được cập nhật khi login lại
            currentUser.setValue(new User("0", "user", "User", role));
        }
    }

    private void setupUserNames() {
        userFullNameMap.put("ThuyLai", "Trần Thị Thúy Lài");
        userFullNameMap.put("ThuyNa", "Lê Nguyễn Thúy Na");
        userFullNameMap.put("bao.tq", "Trần Quốc Bảo");
        userFullNameMap.put("quan.pm", "Phạm Minh Quân");
        userFullNameMap.put("lan.dt", "Đặng Thị Lan");
        userFullNameMap.put("anh.bd", "Bùi Đức Anh");
        userFullNameMap.put("huy.nq", "Ngô Quang Huy");
        userFullNameMap.put("ngoc.pt", "Phan Thị Ngọc");
        userFullNameMap.put("hoa.tk", "Trịnh Khánh Hòa");
        userFullNameMap.put("dat.vt", "Võ Thành Đạt");
        userFullNameMap.put("thi.nd", "Nguyễn Đình Thi");
        userFullNameMap.put("thu.tt", "Trịnh Thị Mỹ Thu");
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
                    
                    String role = "EMPLOYEE";
                    if (username.equals("ThuyLai")) role = "ADMIN";
                    
                    prefsManager.saveUserRole(role);
                    
                    // Lấy tên đầy đủ từ bản đồ theo yêu cầu
                    String fullName = userFullNameMap.getOrDefault(username, username);
                    
                    User user = new User("1", username, fullName, role);
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
        result.setValue(Resource.success("OTP đã được gửi"));
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
