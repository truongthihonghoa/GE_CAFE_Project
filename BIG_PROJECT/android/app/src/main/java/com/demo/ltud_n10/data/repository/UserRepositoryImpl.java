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
                        users.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(users));
                } else {
                    result.setValue(Resource.error("Lỗi tải danh sách", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AccountDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Mất kết nối", null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<User>> addUser(User user) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        AccountDto dto = new AccountDto();
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getName());
        dto.setRole("ADMIN".equals(user.getRole()) ? "Quản lý" : "Nhân viên");
        dto.setStatus(user.getStatus());
        dto.setPassword(user.getPassword());

        apiService.createAccount(dto).enqueue(new Callback<AccountDto>() {
            @Override
            public void onResponse(@NonNull Call<AccountDto> call, @NonNull Response<AccountDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                }
            }
            @Override
            public void onFailure(@NonNull Call<AccountDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối", null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<User>> updateUser(User user) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        AccountDto dto = new AccountDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getName());
        dto.setRole("ADMIN".equals(user.getRole()) ? "Quản lý" : "Nhân viên");
        dto.setStatus(user.getStatus());

        apiService.updateAccount(user.getId(), dto).enqueue(new Callback<AccountDto>() {
            @Override
            public void onResponse(@NonNull Call<AccountDto> call, @NonNull Response<AccountDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                }
            }
            @Override
            public void onFailure(@NonNull Call<AccountDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi cập nhật", null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> toggleUserStatus(String userId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        apiService.deleteAccount(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) result.setValue(Resource.success(true));
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối", false));
            }
        });
        return result;
    }

    private User mapDtoToDomain(AccountDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername()); // Tên đăng nhập viết tắt
        
        // ÉP KIỂU TÊN NGƯỜI DÙNG SANG TIẾNG VIỆT CHUẨN
        String username = dto.getUsername() != null ? dto.getUsername() : "";
        String fullName = "";

        if (username.equals("bao.tq")) fullName = "Trần Quốc Bảo";
        else if (username.equals("ThuyNa")) fullName = "Lê Nguyễn Thúy Na";
        else if (username.equals("ThuyLai")) fullName = "Trần Thị Thúy Lài";
        else if (username.equals("quan.pm")) fullName = "Phạm Minh Quân";
        else if (username.equals("lan.dt")) fullName = "Đặng Thị Lan";
        else if (username.equals("anh.bd")) fullName = "Bùi Đức Anh";
        else if (username.equals("huy.nq")) fullName = "Ngô Quang Huy";
        else if (username.equals("ngoc.pt")) fullName = "Phan Thị Ngọc";
        else if (username.equals("hoa.tk")) fullName = "Trịnh Khánh Hòa";
        else if (username.equals("dat.vt")) fullName = "Võ Thành Đạt";
        else if (username.equals("thi.nd")) fullName = "Nguyễn Đình Thi";
        else if (username.equals("thu.tt")) fullName = "Trịnh Thị Mỹ Thu";
        else fullName = dto.getFullName() != null ? dto.getFullName() : username;

        user.setName(fullName); // Hiển thị Họ và tên đầy đủ

        user.setRole("Quản lý".equals(dto.getRole()) ? "ADMIN" : "EMPLOYEE");
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : "Đang hoạt động");
        return user;
    }
}
