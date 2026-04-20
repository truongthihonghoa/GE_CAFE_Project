package com.demo.ltud_n10.data.remote;

import com.demo.ltud_n10.data.remote.model.ContractDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ContractApiService {
    // Đã có Interceptor tự động thêm Token, không cần @Header ở đây
    @GET("api/contracts/")
    Call<List<ContractDto>> getContracts();
}
