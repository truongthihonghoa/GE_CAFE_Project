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
            User user = new User();
            user.setRole(role);
            user.setName("Loading...");
            currentUser.setValue(user);
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
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getAccess();
                    prefsManager.saveToken(token);
                    
                    // Sau khi login thành công, lấy chi tiết account để xác định is_staff thật
                    apiService.getMyAccount("Bearer " + token).enqueue(new Callback<List<AccountDto>>() {
                        @Override
                        public void onResponse(Call<List<AccountDto>> callAccount, Response<List<AccountDto>> responseAccount) {
                            boolean isStaff = false;
                            String maNv = loginResponse.getMaNv();
                            String hoTen = username;

                            if (responseAccount.isSuccessful() && responseAccount.body() != null && !responseAccount.body().isEmpty()) {
                                AccountDto account = responseAccount.body().get(0);
                                isStaff = account.isStaff();
                                maNv = account.getMaNv();
                                hoTen = account.getFullName();
                            } else {
                                // Fallback dựa trên isStaff trong login response nếu getMyAccount fail
                                isStaff = loginResponse.isStaff();
                            }

                            String role = isStaff ? "ADMIN" : "EMPLOYEE";
                            prefsManager.saveUserRole(role);
                            
                            User user = new User();
                            user.setId(maNv);
                            user.setEmail(username);
                            user.setName(hoTen);
                            user.setRole(role);
                            user.setStaff(isStaff);
                            
                            currentUser.setValue(user);
                            result.setValue(Resource.success(user));
                        }

                        @Override
                        public void onFailure(Call<List<AccountDto>> callAccount, Throwable t) {
                            // Fallback
                            String role = loginResponse.isStaff() ? "ADMIN" : "EMPLOYEE";
                            prefsManager.saveUserRole(role);
                            User user = new User();
                            user.setId(loginResponse.getMaNv());
                            user.setName(username);
                            user.setRole(role);
                            user.setStaff(loginResponse.isStaff());
                            currentUser.setValue(user);
                            result.setValue(Resource.success(user));
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
    public LiveData<Resource<String>> resetPassword(String phone) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.success("Chức năng đang phát triển"));
        return result;
    }

    @Override
    public void logout() {
        prefsManager.clear();
        currentUser.setValue(null);
    }

    @Override
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isLoggedIn() {
        return prefsManager.getToken() != null;
    }
}
