package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.AccountDto;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class UserRepositoryImpl implements UserRepository {

    private final ApiService apiService;

    @Inject
    public UserRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<User>>> getUsers() {
        MutableLiveData<Resource<List<User>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getAccounts().enqueue(new Callback<List<AccountDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<AccountDto>> call, @NonNull Response<List<AccountDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = new ArrayList<>();
                    for (AccountDto dto : response.body()) {
                        // Bỏ đuôi @coffee.com, chỉ lấy username gốc
                        String username = dto.getUsername();
                        
                        // Chỉnh Trần Thị Thúy Lài là Quản lý (ADMIN)
                        String role = "EMPLOYEE";
                        if (dto.getFullName().equals("Trần Thị Thúy Lài") || "Quản lý".equals(dto.getRole())) {
                            role = "ADMIN";
                        }

                        User user = new User(
                                dto.getId(),
                                username,
                                dto.getFullName(),
                                role
                        );
                        user.setStatus(dto.getStatus());
                        users.add(user);
                    }
                    result.setValue(Resource.success(users));
                } else {
                    result.setValue(Resource.error("Lỗi: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AccountDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<User>> addUser(User user) { return null; }
    @Override
    public LiveData<Resource<User>> updateUser(User user) { return null; }
    @Override
    public LiveData<Resource<Boolean>> toggleUserStatus(String userId) { return null; }
}
