package com.demo.ltud_n10.data.remote;

import com.demo.ltud_n10.data.remote.model.AttendanceDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AttendanceApiService {
    @GET("api/attendances/")
    Call<List<AttendanceDto>> getAttendances();
}
