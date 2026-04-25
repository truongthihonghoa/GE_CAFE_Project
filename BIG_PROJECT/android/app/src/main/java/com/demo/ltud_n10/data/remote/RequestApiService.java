package com.demo.ltud_n10.data.remote;

import com.demo.ltud_n10.data.remote.model.RequestDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestApiService {
    @GET("api/requests/dangkylich/")
    Call<List<RequestDto>> getScheduleRequests();

    @GET("api/requests/nghiphep/")
    Call<List<RequestDto>> getLeaveRequests();
}
