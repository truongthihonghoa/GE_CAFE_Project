package com.demo.ltud_n10.data.repository;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.local.SharedPrefsManager;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.AccountDto;
import com.demo.ltud_n10.domain.model.Account;
import com.demo.ltud_n10.domain.repository.AccountRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AccountRepositoryImpl implements AccountRepository {

    private final ApiService apiService;
    private final SharedPrefsManager prefsManager;

    @Inject
    public AccountRepositoryImpl(ApiService apiService, SharedPrefsManager prefsManager) {
        this.apiService = apiService;
        this.prefsManager = prefsManager;
    }

    @Override
    public LiveData<Resource<List<Account>>> getAccounts() {
        MutableLiveData<Resource<List<Account>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        // Đã chuyển sang GET khớp với Postman của bạn, Token tự động được gắn vào header
        apiService.getAccounts().enqueue(new Callback<List<AccountDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<AccountDto>> call, @NonNull Response<List<AccountDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Account> accounts = new ArrayList<>();
                    for (AccountDto dto : response.body()) {
                        accounts.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(accounts));
                } else {
                    result.setValue(Resource.error("Lỗi lấy danh sách tài khoản: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AccountDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Account>> createAccount(Account account, String password, boolean isStaff) {
        MutableLiveData<Resource<Account>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        AccountDto dto = new AccountDto();
        dto.setUsername(account.getUsername());
        dto.setPassword(password);
        dto.setMaNvId(account.getEmployeeId());
        dto.setRole(isStaff ? "Quản lý" : "Nhân viên");

        apiService.createAccount(dto).enqueue(new Callback<AccountDto>() {
            @Override
            public void onResponse(@NonNull Call<AccountDto> call, @NonNull Response<AccountDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi tạo tài khoản", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Account>> updateAccount(Account account, String password, Boolean isStaff, Boolean isActive) {
        MutableLiveData<Resource<Account>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        AccountDto dto = new AccountDto();
        if (password != null) dto.setPassword(password);
        if (isStaff != null) dto.setRole(isStaff ? "Quản lý" : "Nhân viên");
        if (isActive != null) dto.setStatus(isActive ? "Đang hoạt động" : "Ngừng hoạt động");

        apiService.updateAccount(account.getId(), dto).enqueue(new Callback<AccountDto>() {
            @Override
            public void onResponse(@NonNull Call<AccountDto> call, @NonNull Response<AccountDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi cập nhật", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteAccount(String accountId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        apiService.deleteAccount(accountId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) result.setValue(Resource.success(true));
                else result.setValue(Resource.error("Xóa thất bại", false));
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> changePassword(String accountId, String newPassword) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        Map<String, String> body = new HashMap<>();
        body.put("new_password", newPassword);
        apiService.changePassword(accountId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) result.setValue(Resource.success(true));
                else result.setValue(Resource.error("Lỗi đổi mật khẩu", false));
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
            }
        });
        return result;
    }

    private Account mapDtoToDomain(AccountDto dto) {
        return new Account(
                dto.getId(),
                dto.getUsername(),
                dto.getFullName(),
                dto.getMaNvId(),
                dto.getRole(),
                dto.getStatus()
        );
    }
}
