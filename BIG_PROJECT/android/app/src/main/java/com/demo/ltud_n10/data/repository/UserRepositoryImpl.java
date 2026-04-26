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

        // Lấy thông tin từ bộ nhớ máy để thực hiện lọc dữ liệu
        String maNv = prefsManager.getMaNv();
        // LOGIC PHÂN QUYỀN: Nếu là Staff (Sếp) -> Không lọc (null) để xem tất cả
        // Nếu không phải Staff -> Lọc theo maNv để chỉ xem chính mình
        String filterMaNv = prefsManager.isStaff() ? null : maNv;

        // Cập nhật tham số truyền vào apiService.getAccounts() để khớp với ApiService.java
        apiService.getAccounts(filterMaNv).enqueue(new Callback<List<AccountDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<AccountDto>> call, @NonNull Response<List<AccountDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = new ArrayList<>();
                    for (AccountDto dto : response.body()) {
                        User user = new User(
                                dto.getId(),
                                dto.getUsername() + "@coffee.com",
                                dto.getFullName(),
                                dto.checkIsStaff() ? "ADMIN" : "EMPLOYEE"
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
