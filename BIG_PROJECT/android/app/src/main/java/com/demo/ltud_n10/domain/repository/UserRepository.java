package com.demo.ltud_n10.domain.repository;

import androidx.lifecycle.LiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.User;
import java.util.List;

public interface UserRepository {
    LiveData<Resource<List<User>>> getUsers();
    LiveData<Resource<User>> addUser(User user);
    LiveData<Resource<User>> updateUser(User user);
    LiveData<Resource<Boolean>> toggleUserStatus(String userId);
}
