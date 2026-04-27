package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.local.SharedPrefsManager;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.AccountDto;
import com.demo.ltud_n10.data.remote.dto.LoginRequest;
import com.demo.ltud_n10.data.remote.dto.LoginResponse;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthRepositoryImpl implements AuthRepository {

    private final SharedPrefsManager prefsManager;
    private final ApiService apiService;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>(null);

    @Inject
    public AuthRepositoryImpl(SharedPrefsManager prefsManager, ApiService apiService) {
        this.prefsManager = prefsManager;
        this.apiService = apiService;
        
        String role = prefsManager.getUserRole();
        if (role != null) {
            currentUser.setValue(new User("0", "user@coffee.com", "User", role));
        }
    }

    @Override
    public LiveData<Resource<User>> login(String username, String password) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.login(new LoginRequest(username, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccess();
                    String bearerToken = "Bearer " + token;
                    prefsManager.saveToken(token);
                    
                    apiService.getMyAccount(bearerToken).enqueue(new Callback<List<AccountDto>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<AccountDto>> call, @NonNull Response<List<AccountDto>> accountResponse) {
                            if (accountResponse.isSuccessful() && accountResponse.body() != null) {
                                String maNv = null;
                                boolean isStaff = false;
                                String role = "EMPLOYEE";
                                String fullName = username;

                                for (AccountDto acc : accountResponse.body()) {
                                    String apiUser = acc.getUsername().toLowerCase();
                                    String loginUser = username.toLowerCase();
                                    
                                    if (loginUser.contains(apiUser) || apiUser.contains(loginUser)) {
                                        maNv = acc.getMaNvId();
                                        isStaff = acc.checkIsStaff();
                                        fullName = acc.getFullName();
                                        if (isStaff) {
                                            role = "ADMIN";
                                        }
                                        break;
                                    }
                                }
                                
                                prefsManager.saveMaNv(maNv);
                                prefsManager.saveIsStaff(isStaff);
                                prefsManager.saveUserRole(role);
                                
                                User user = new User(maNv != null ? maNv : "0", username + "@coffee.com", fullName, role);
                                user.setMaNv(maNv);
                                currentUser.setValue(user);
                                result.setValue(Resource.success(user));
                            } else {
                                result.setValue(Resource.error("Không thể lấy thông tin tài khoản", null));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<AccountDto>> call, @NonNull Throwable t) {
                            result.setValue(Resource.error("Lỗi lấy thông tin: " + t.getMessage(), null));
                        }
                    });
                } else {
                    result.setValue(Resource.error("Tài khoản hoặc mật khẩu không chính xác", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public void logout() {
        prefsManager.clear();
        currentUser.setValue(null);
    }

    @Override
    public LiveData<User> getCurrentUser() { return currentUser; }

    @Override
    public boolean isLoggedIn() { return prefsManager.getToken() != null; }

    @Override
    public LiveData<Resource<String>> resetPassword(String phone) {
        return new MutableLiveData<>(Resource.success("Chức năng đang phát triển"));
    }

    // --- LOGIC CRUD TÀI KHOẢN (TỪ ACCOUNT REPOSITORY) ---

    public LiveData<Resource<List<User>>> getAccounts() {
        MutableLiveData<Resource<List<User>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getAccounts(null).enqueue(new Callback<List<AccountDto>>() {
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
                    result.setValue(Resource.error("Lỗi tải danh sách", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AccountDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<User>> addUser(User user) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        AccountDto dto = new AccountDto();
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getName());
        dto.setPassword(user.getPassword());
        dto.setRole("ADMIN".equals(user.getRole()) ? "Quản lý" : "Nhân viên");
        dto.setStatus(user.getStatus());
        dto.setMaNvId(user.getMaNv());

        apiService.createAccount(dto).enqueue(new Callback<AccountDto>() {
            @Override
            public void onResponse(@NonNull Call<AccountDto> call, @NonNull Response<AccountDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccountDto body = response.body();
                    User newUser = new User(body.getId(), body.getUsername(), body.getFullName(), body.checkIsStaff() ? "ADMIN" : "EMPLOYEE");
                    newUser.setStatus(body.getStatus());
                    newUser.setMaNv(body.getMaNvId());
                    result.setValue(Resource.success(newUser));
                } else {
                    result.setValue(Resource.error("Thêm thất bại", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<User>> updateUser(User user) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        AccountDto dto = new AccountDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getName());
        dto.setRole("ADMIN".equals(user.getRole()) ? "Quản lý" : "Nhân viên");
        dto.setStatus(user.getStatus());
        dto.setMaNvId(user.getMaNv());

        apiService.updateAccount(user.getId(), dto).enqueue(new Callback<AccountDto>() {
            @Override
            public void onResponse(@NonNull Call<AccountDto> call, @NonNull Response<AccountDto> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(user));
                } else {
                    result.setValue(Resource.error("Cập nhật thất bại", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<Boolean>> deleteUser(String id) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        apiService.deleteAccount(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Xóa thất bại", false));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
            }
        });
        return result;
    }
}
