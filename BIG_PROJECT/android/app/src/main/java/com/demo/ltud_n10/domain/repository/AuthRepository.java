package com.demo.ltud_n10.domain.repository;

import androidx.lifecycle.LiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.User;
import java.util.List;

public interface AuthRepository {
    LiveData<Resource<User>> login(String username, String password);
    LiveData<Resource<String>> resetPassword(String phone);
    void logout();
    LiveData<User> getCurrentUser();
    boolean isLoggedIn();

    // Các phương thức CRUD tài khoản
    LiveData<Resource<List<User>>> getAccounts();
    LiveData<Resource<User>> addUser(User user);
    LiveData<Resource<User>> updateUser(User user);
    LiveData<Resource<Boolean>> deleteUser(String id);
}
