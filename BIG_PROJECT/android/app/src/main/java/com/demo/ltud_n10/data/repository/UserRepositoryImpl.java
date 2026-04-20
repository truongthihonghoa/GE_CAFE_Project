package com.demo.ltud_n10.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepositoryImpl implements UserRepository {

    private final List<User> userList = new ArrayList<>();

    @Inject
    public UserRepositoryImpl() {
        // Mock data
        userList.add(new User("TK001", "owner@coffee.com", "password123", "Admin User", "ADMIN", "Đang hoạt động"));
        userList.add(new User("TK002", "staff@coffee.com", "password123", "Staff User", "EMPLOYEE", "Đang hoạt động"));
        userList.add(new User("TK003", "levanc@coffee.com", "password123", "Lê Văn C", "EMPLOYEE", "Ngưng hoạt động"));
    }

    @Override
    public LiveData<Resource<List<User>>> getUsers() {
        MutableLiveData<Resource<List<User>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            result.setValue(Resource.success(new ArrayList<>(userList)));
        }, 800);
        return result;
    }

    @Override
    public LiveData<Resource<User>> addUser(User user) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check username uniqueness
            for (User u : userList) {
                if (u.getUsername().equals(user.getUsername())) {
                    result.setValue(Resource.error("Tên đăng nhập đã tồn tại", null));
                    return;
                }
            }
            user.setId("TK00" + (userList.size() + 1));
            userList.add(user);
            result.setValue(Resource.success(user));
        }, 800);
        return result;
    }

    @Override
    public LiveData<Resource<User>> updateUser(User user) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getId().equals(user.getId())) {
                    userList.set(i, user);
                    result.setValue(Resource.success(user));
                    return;
                }
            }
            result.setValue(Resource.error("Không tìm thấy tài khoản", null));
        }, 800);
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> toggleUserStatus(String userId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (User u : userList) {
                if (u.getId().equals(userId)) {
                    u.setStatus(u.getStatus().equals("Đang hoạt động") ? "Ngưng hoạt động" : "Đang hoạt động");
                    result.setValue(Resource.success(true));
                    return;
                }
            }
            result.setValue(Resource.error("Lỗi", false));
        }, 500);
        return result;
    }
}
