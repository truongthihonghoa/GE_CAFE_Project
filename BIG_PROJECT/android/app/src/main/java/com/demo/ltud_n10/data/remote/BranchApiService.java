package com.demo.ltud_n10.data.remote;

import com.demo.ltud_n10.data.remote.model.BranchDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BranchApiService {
    @GET("api/branches/")
    Call<List<BranchDto>> getBranches();
}
