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

    private final SharedPrefsManager prefsManager;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>(null);

    @Inject
    public AuthRepositoryImpl(SharedPrefsManager prefsManager) {
        this.prefsManager = prefsManager;
        String role = prefsManager.getUserRole();
        if (role != null) {
            String name = "ADMIN".equals(role) ? "Admin User" : "Lê Văn C";
            currentUser.setValue(new User("0", "saved@user.com", name, role));
        }
    }

    @Override
    public LiveData<Resource<User>> login(String email, String password) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if ("owner@coffee.com".equals(email) && "password123".equals(password)) {
                User user = new User("1", email, "Admin User", "ADMIN");
                prefsManager.saveToken("mock_admin_token");
                prefsManager.saveUserRole("ADMIN");
                currentUser.setValue(user);
                result.setValue(Resource.success(user));
            } else if ("staff@coffee.com".equals(email) && "password123".equals(password)) {
                User user = new User("2", email, "Lê Văn C", "EMPLOYEE");
                prefsManager.saveToken("mock_employee_token");
                prefsManager.saveUserRole("EMPLOYEE");
                currentUser.setValue(user);
                result.setValue(Resource.success(user));
            } else {
                result.setValue(Resource.error("Email hoặc mật khẩu không đúng", null));
            }
        }, 1000);

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
