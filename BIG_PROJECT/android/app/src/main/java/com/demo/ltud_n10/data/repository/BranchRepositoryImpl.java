package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.BranchDto;
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

    private final ApiService apiService;

    @Inject
    public BranchRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Branch>>> getBranches() {
        MutableLiveData<Resource<List<Branch>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));

        // CHUYỂN SANG GET ĐỂ KHỚP VỚI POSTMAN CỦA BẠN
        apiService.getBranches().enqueue(new Callback<List<BranchDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<BranchDto>> call, @NonNull Response<List<BranchDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Branch> branches = new ArrayList<>();
                    for (BranchDto dto : response.body()) {
                        branches.add(new Branch(
                                dto.getId(),
                                dto.getName(),
                                dto.getAddress(),
                                dto.getPhoneNumber(),
                                dto.getManagerName(),
                                dto.getStatus()
                        ));
                    }
                    data.setValue(Resource.success(branches));
                } else {
                    data.setValue(Resource.error("Lỗi server: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BranchDto>> call, @NonNull Throwable t) {
                data.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return data;
    }

    @Override
    public LiveData<Resource<Branch>> addBranch(Branch branch) { return null; }
    @Override
    public LiveData<Resource<Branch>> updateBranch(Branch branch) { return null; }
    @Override
    public LiveData<Resource<Boolean>> deleteBranch(String branchId) { return null; }
}
