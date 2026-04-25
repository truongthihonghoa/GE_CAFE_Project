package com.demo.ltud_n10.data.api;

import com.demo.ltud_n10.domain.model.Branch;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BranchApiService {
    @GET("api/branches/")
    Call<List<Branch>> getBranches();

    @POST("api/branches/")
    Call<Branch> addBranch(@Body Branch branch);

    @PUT("api/branches/{id}/")
    Call<Branch> updateBranch(@Path("id") String id, @Body Branch branch);

    @DELETE("api/branches/{id}/")
    Call<Void> deleteBranch(@Path("id") String id);
}
