package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.AccountDto;
import com.demo.ltud_n10.domain.model.Account;
import com.demo.ltud_n10.domain.repository.AccountRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AccountRepositoryImpl implements AccountRepository {

    private final ApiService apiService;

    @Inject
    public AccountRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Account>>> getAccounts() {
        MutableLiveData<Resource<List<Account>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getAccounts().enqueue(new Callback<List<AccountDto>>() {
            @Override
            public void onResponse(Call<List<AccountDto>> call, Response<List<AccountDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Account> accounts = response.body().stream()
                            .map(AccountRepositoryImpl::mapToDomain)
                            .collect(Collectors.toList());
                    result.setValue(Resource.success(accounts));
                } else {
                    result.setValue(Resource.error("Lỗi lấy danh sách tài khoản", null));
                }
            }

            @Override
            public void onFailure(Call<List<AccountDto>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
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
        dto.setIsStaff(isStaff);

        apiService.createAccount(dto).enqueue(new Callback<AccountDto>() {
            @Override
            public void onResponse(Call<AccountDto> call, Response<AccountDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDomain(response.body())));
                } else {
                    String errorMsg = "Lỗi tạo tài khoản";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<AccountDto> call, Throwable t) {
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
        if (password != null && !password.isEmpty()) dto.setPassword(password);
        if (isStaff != null) dto.setIsStaff(isStaff);
        if (isActive != null) dto.setIsActive(isActive);

        apiService.updateAccount(account.getId(), dto).enqueue(new Callback<AccountDto>() {
            @Override
            public void onResponse(Call<AccountDto> call, Response<AccountDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDomain(response.body())));
                } else {
                    String errorMsg = "Lỗi cập nhật tài khoản";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<AccountDto> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteAccount(String accountId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.deleteAccount(accountId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Lỗi xóa tài khoản", false));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> changePassword(String accountId, String newPassword) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> body = new HashMap<>();
        body.put("new_password", newPassword);

        apiService.changePassword(accountId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Lỗi đổi mật khẩu", false));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
            }
        });

        return result;
    }

    private static Account mapToDomain(AccountDto dto) {
        Account account = new Account(
                dto.getId(),
                dto.getDisplayUsername(),
                dto.getFullName(),
                null, 
                dto.getRole(),
                dto.getStatus()
        );
        return account;
    }
}
