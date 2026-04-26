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
                                        fullName = acc.getFullName(); // Tên thật: Trần Quốc Bảo
                                        if (isStaff) {
                                            role = "ADMIN";
                                        }
                                        break;
                                    }
                                }
                                
                                prefsManager.saveMaNv(maNv);
                                prefsManager.saveIsStaff(isStaff);
                                prefsManager.saveUserRole(role);
                                
                                // FIX THỨ TỰ THAM SỐ: id, email, name, role
                                User user = new User(maNv != null ? maNv : "0", username + "@coffee.com", fullName, role);
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
}
