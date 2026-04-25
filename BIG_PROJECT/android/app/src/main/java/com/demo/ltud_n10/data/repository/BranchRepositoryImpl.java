package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.BranchApiService;
import com.demo.ltud_n10.data.remote.model.BranchDto;
import com.demo.ltud_n10.domain.model.Branch;
import com.demo.ltud_n10.domain.repository.BranchRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class BranchRepositoryImpl implements BranchRepository {

    private final BranchApiService apiService;

    @Inject
    public BranchRepositoryImpl(BranchApiService apiService) {
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
                    List<Branch> branches = new ArrayList<>();
                    for (BranchDto dto : response.body()) {
                        branches.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(branches));
                } else {
                    result.setValue(Resource.error("Lỗi khi tải dữ liệu chi nhánh: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<List<BranchDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối API chi nhánh: " + t.getMessage(), null));
            }
        });

        return result;
    }

    private Branch mapDtoToDomain(BranchDto dto) {
        Branch branch = new Branch();
        branch.setId(dto.getMaChiNhanh());
        branch.setName(dto.getTenChiNhanh());
        branch.setAddress(dto.getDiaChi());
        branch.setPhoneNumber(dto.getSdt());
        branch.setManagerName(dto.getMaNvQl()); // API đang trả về mã NV quản lý
        branch.setStatus("Đang hoạt động"); // Mặc định do API chưa trả về trạng thái
        return branch;
    }

    @Override
    public LiveData<Resource<Branch>> addBranch(Branch branch) {
        // Tạm thời trả về thành công, bạn có thể bổ sung API POST sau
        return new MutableLiveData<>(Resource.success(branch));
    }

    @Override
    public LiveData<Resource<Branch>> updateBranch(Branch branch) {
        // Tạm thời trả về thành công
        return new MutableLiveData<>(Resource.success(branch));
    }

    @Override
    public LiveData<Resource<Boolean>> deleteBranch(String branchId) {
        // Tạm thời trả về thành công
        return new MutableLiveData<>(Resource.success(true));
    }
}
