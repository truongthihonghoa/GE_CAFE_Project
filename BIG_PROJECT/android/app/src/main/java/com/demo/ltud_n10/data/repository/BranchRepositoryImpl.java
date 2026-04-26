package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.BranchDto;
import com.demo.ltud_n10.domain.model.Branch;
import com.demo.ltud_n10.domain.repository.BranchRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class BranchRepositoryImpl implements BranchRepository {

    private final ApiService apiService;

    @Inject
    public BranchRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Branch>>> getBranches() {
        MutableLiveData<Resource<List<Branch>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));

        apiService.getBranches().enqueue(new Callback<List<BranchDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<BranchDto>> call, @NonNull Response<List<BranchDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Branch> branches = new ArrayList<>();
                    for (BranchDto dto : response.body()) {
                        branches.add(mapDtoToDomain(dto));
                    }
                    data.setValue(Resource.success(branches));
                } else {
                    data.setValue(Resource.error("Lỗi: " + response.code(), null));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<BranchDto>> call, @NonNull Throwable t) {
                data.setValue(Resource.error("Lỗi kết nối mạng", null));
            }
        });
        return data;
    }

    @Override
    public LiveData<Resource<Branch>> addBranch(Branch branch) {
        MutableLiveData<Resource<Branch>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));

        apiService.getBranches().enqueue(new Callback<List<BranchDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<BranchDto>> call, @NonNull Response<List<BranchDto>> response) {
                int nextNum = 1;
                if (response.isSuccessful() && response.body() != null) {
                    nextNum = response.body().size() + 1;
                }
                String nextId = String.format("CN%02d", nextNum);

                BranchDto dto = new BranchDto();
                dto.setId(nextId);
                dto.setName(branch.getName());
                dto.setAddress(branch.getAddress());
                dto.setPhoneNumber(branch.getPhoneNumber());
                
                // Server Django yêu cầu key 'active' hoặc 'inactive'
                dto.setStatus("active"); 
                
                String managerId = branch.getManagerName();
                dto.setManagerName((managerId == null || managerId.trim().isEmpty()) ? null : managerId);

                apiService.addBranch(dto).enqueue(new Callback<BranchDto>() {
                    @Override
                    public void onResponse(@NonNull Call<BranchDto> call, @NonNull Response<BranchDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(Resource.success(mapDtoToDomain(response.body())));
                        } else {
                            handleError(response, data);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<BranchDto> call, @NonNull Throwable t) {
                        data.setValue(Resource.error(t.getMessage(), null));
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call<List<BranchDto>> call, @NonNull Throwable t) {
                data.setValue(Resource.error("Lỗi kết nối", null));
            }
        });
        return data;
    }

    @Override
    public LiveData<Resource<Branch>> updateBranch(Branch branch) {
        MutableLiveData<Resource<Branch>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));

        BranchDto dto = new BranchDto();
        dto.setId(branch.getId());
        dto.setName(branch.getName());
        dto.setAddress(branch.getAddress());
        dto.setPhoneNumber(branch.getPhoneNumber());
        
        // Chuyển đổi từ Label UI sang Key API
        String statusKey = "active";
        if ("Ngưng hoạt động".equals(branch.getStatus()) || "inactive".equals(branch.getStatus())) {
            statusKey = "inactive";
        }
        dto.setStatus(statusKey);
        
        String managerId = branch.getManagerName();
        dto.setManagerName((managerId == null || managerId.trim().isEmpty()) ? null : managerId);

        apiService.updateBranch(branch.getId(), dto).enqueue(new Callback<BranchDto>() {
            @Override
            public void onResponse(@NonNull Call<BranchDto> call, @NonNull Response<BranchDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    handleError(response, data);
                }
            }
            @Override
            public void onFailure(@NonNull Call<BranchDto> call, @NonNull Throwable t) {
                data.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return data;
    }

    private void handleError(Response<?> response, MutableLiveData<Resource<Branch>> data) {
        String errorMsg = "Lỗi " + response.code();
        try {
            if (response.errorBody() != null) {
                errorMsg += ": " + response.errorBody().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        data.setValue(Resource.error(errorMsg, null));
    }

    @Override
    public LiveData<Resource<Boolean>> deleteBranch(String branchId) {
        return new MutableLiveData<>(Resource.success(true));
    }

    private Branch mapDtoToDomain(BranchDto dto) {
        // Chuyển đổi từ Key API sang Label UI
        String uiStatus = "Đang hoạt động";
        if ("inactive".equals(dto.getStatus())) {
            uiStatus = "Ngưng hoạt động";
        }
        
        return new Branch(
                dto.getId(),
                dto.getName(),
                dto.getAddress(),
                dto.getPhoneNumber(),
                dto.getManagerName(),
                uiStatus
        );
    }
}
