package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.BranchDto;
import com.demo.ltud_n10.domain.model.Branch;
import com.demo.ltud_n10.domain.repository.BranchRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        MutableLiveData<Resource<List<Branch>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getBranches().enqueue(new Callback<List<BranchDto>>() {
            @Override
            public void onResponse(Call<List<BranchDto>> call, Response<List<BranchDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Branch> branches = response.body().stream()
                            .map(BranchRepositoryImpl::mapToDomain)
                            .collect(Collectors.toList());
                    result.setValue(Resource.success(branches));
                } else {
                    result.setValue(Resource.error("Lỗi lấy danh sách chi nhánh", null));
                }
            }

            @Override
            public void onFailure(Call<List<BranchDto>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Branch>> addBranch(Branch branch) {
        MutableLiveData<Resource<Branch>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.addBranch(mapToDto(branch)).enqueue(new Callback<BranchDto>() {
            @Override
            public void onResponse(Call<BranchDto> call, Response<BranchDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi thêm chi nhánh", null));
                }
            }

            @Override
            public void onFailure(Call<BranchDto> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Branch>> updateBranch(Branch branch) {
        MutableLiveData<Resource<Branch>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.updateBranch(branch.getId(), mapToDto(branch)).enqueue(new Callback<BranchDto>() {
            @Override
            public void onResponse(Call<BranchDto> call, Response<BranchDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi cập nhật chi nhánh", null));
                }
            }

            @Override
            public void onFailure(Call<BranchDto> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteBranch(String branchId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.deleteBranch(branchId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Lỗi xóa chi nhánh", false));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
            }
        });

        return result;
    }

    private static Branch mapToDomain(BranchDto dto) {
        return new Branch(
                dto.getMaChiNhanh(),
                dto.getTenChiNhanh(),
                dto.getDiaChi(),
                dto.getSdt(),
                dto.getMaNvQl(),
                dto.getManagerName(),
                dto.getTrangThai()
        );
    }

    private static BranchDto mapToDto(Branch branch) {
        BranchDto dto = new BranchDto();
        dto.setMaChiNhanh(branch.getId());
        dto.setTenChiNhanh(branch.getName());
        dto.setDiaChi(branch.getAddress());
        dto.setSdt(branch.getPhoneNumber());
        dto.setMaNvQl(branch.getManagerId());
        dto.setTrangThai(branch.getStatus());
        return dto;
    }
}
