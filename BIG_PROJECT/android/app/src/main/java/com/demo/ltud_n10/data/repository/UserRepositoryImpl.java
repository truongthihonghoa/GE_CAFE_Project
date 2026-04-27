package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.local.SharedPrefsManager;
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
    private final SharedPrefsManager prefsManager;

    @Inject
    public UserRepositoryImpl(ApiService apiService, SharedPrefsManager prefsManager) {
        this.apiService = apiService;
        this.prefsManager = prefsManager;
    }

    @Override
    public LiveData<Resource<List<User>>> getUsers() {
        MutableLiveData<Resource<List<User>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        String maNv = prefsManager.getMaNv();
        String filterMaNv = prefsManager.isStaff() ? null : maNv;

        apiService.getAccounts(filterMaNv).enqueue(new Callback<List<AccountDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<AccountDto>> call, @NonNull Response<List<AccountDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = new ArrayList<>();
                    for (AccountDto dto : response.body()) {
                        User user = new User(
                                dto.getId(),
                                dto.getUsername(),
                                dto.getFullName(),
                                dto.checkIsStaff() ? "ADMIN" : "EMPLOYEE"
                        );
                        user.setStatus(dto.getStatus());
                        user.setMaNv(dto.getMaNvId());
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
    public LiveData<Resource<User>> addUser(User user) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        AccountDto dto = new AccountDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getName());
        dto.setPassword(user.getPassword());
        dto.setRole("ADMIN".equals(user.getRole()) ? "Quản lý" : "Nhân viên");
        dto.setStatus(user.getStatus() != null ? user.getStatus() : "Đang hoạt động");
        dto.setMaNvId(user.getMaNv());

        apiService.createAccount(dto).enqueue(new Callback<AccountDto>() {
            @Override
            public void onResponse(@NonNull Call<AccountDto> call,
                                   @NonNull Response<AccountDto> response) {

                if (response.isSuccessful() && response.body() != null) {
                    AccountDto responseDto = response.body();

                    User newUser = new User(
                            responseDto.getId(),
                            responseDto.getUsername(),
                            responseDto.getFullName(),
                            responseDto.checkIsStaff() ? "ADMIN" : "EMPLOYEE"
                    );

                    newUser.setStatus(responseDto.getStatus());
                    newUser.setMaNv(responseDto.getMaNvId());

                    result.setValue(Resource.success(newUser));
                } else {
                    result.setValue(Resource.error(
                            "Không thể thêm tài khoản. Mã lỗi: " + response.code(),
                            null
                    ));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountDto> call,
                                  @NonNull Throwable t) {
                result.setValue(Resource.error(
                        "Lỗi kết nối: " + t.getMessage(),
                        null
                ));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<User>> updateUser(User user) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        AccountDto dto = new AccountDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getName());
        dto.setRole("ADMIN".equals(user.getRole()) ? "Quản lý" : "Nhân viên");
        dto.setStatus(user.getStatus() != null ? user.getStatus() : "Đang hoạt động");
        dto.setMaNvId(user.getMaNv());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            dto.setPassword(user.getPassword());
        }

        apiService.updateAccount(user.getId(), dto).enqueue(new Callback<AccountDto>() {
            @Override
            public void onResponse(@NonNull Call<AccountDto> call,
                                   @NonNull Response<AccountDto> response) {

                if (response.isSuccessful() && response.body() != null) {
                    AccountDto updatedDto = response.body();

                    User updatedUser = new User(
                            updatedDto.getId(),
                            updatedDto.getUsername(),
                            updatedDto.getFullName(),
                            updatedDto.checkIsStaff() ? "ADMIN" : "EMPLOYEE"
                    );

                    updatedUser.setStatus(updatedDto.getStatus());
                    updatedUser.setMaNv(updatedDto.getMaNvId());

                    result.setValue(Resource.success(updatedUser));
                } else {
                    result.setValue(Resource.error(
                            "Không thể cập nhật tài khoản. Mã lỗi: " + response.code(),
                            null
                    ));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountDto> call,
                                  @NonNull Throwable t) {
                result.setValue(Resource.error(
                        "Lỗi cập nhật: " + t.getMessage(),
                        null
                ));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> toggleUserStatus(String userId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(false));

        apiService.deleteAccount(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {

                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error(
                            "Không thể vô hiệu hóa tài khoản. Mã lỗi: " + response.code(),
                            false
                    ));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call,
                                  @NonNull Throwable t) {
                result.setValue(Resource.error(
                        "Lỗi kết nối: " + t.getMessage(),
                        false
                ));
            }
        });

        return result;
    }
}
