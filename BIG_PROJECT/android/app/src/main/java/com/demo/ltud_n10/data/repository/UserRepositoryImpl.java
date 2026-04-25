package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.AccountApiService;
import com.demo.ltud_n10.data.remote.model.UserDto;
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

    private final AccountApiService apiService;

    @Inject
    public UserRepositoryImpl(AccountApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<User>>> getUsers() {
        MutableLiveData<Resource<List<User>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getAccounts().enqueue(new Callback<List<UserDto>>() {
            @Override
            public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = new ArrayList<>();
                    for (UserDto dto : response.body()) {
                        users.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(users));
                } else {
                    result.setValue(Resource.error("Lỗi khi tải danh sách tài khoản: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<List<UserDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối API tài khoản: " + t.getMessage(), null));
            }
        });

        return result;
    }

    private User mapDtoToDomain(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getTenDangNhap());
        user.setName(dto.getHoTen());
        user.setStatus(dto.getTrangThai());
        
        // Map role
        String vaiTro = dto.getVaiTro();
        if ("Chủ".equals(vaiTro)) {
            user.setRole("ADMIN");
        } else {
            user.setRole("EMPLOYEE");
        }
        
        return user;
    }

    @Override
    public LiveData<Resource<User>> addUser(User user) {
        // Tạm thời trả về thành công
        return new MutableLiveData<>(Resource.success(user));
    }

    @Override
    public LiveData<Resource<User>> updateUser(User user) {
        // Tạm thời trả về thành công
        return new MutableLiveData<>(Resource.success(user));
    }

    @Override
    public LiveData<Resource<Boolean>> toggleUserStatus(String userId) {
        // Tạm thời trả về thành công
        return new MutableLiveData<>(Resource.success(true));
    }
}
