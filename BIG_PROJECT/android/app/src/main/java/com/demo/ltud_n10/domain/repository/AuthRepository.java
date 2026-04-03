package com.demo.ltud_n10.domain.repository;

import androidx.lifecycle.LiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.User;

public interface AuthRepository {
    LiveData<Resource<User>> login(String email, String password);
    LiveData<Resource<String>> resetPassword(String phone);
    void logout();
    LiveData<User> getCurrentUser();
    boolean isLoggedIn();
}
