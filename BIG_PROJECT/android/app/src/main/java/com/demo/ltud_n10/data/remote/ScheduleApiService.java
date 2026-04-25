package com.demo.ltud_n10.data.remote;

import com.demo.ltud_n10.data.remote.model.ScheduleDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ScheduleApiService {
    // Sử dụng đường dẫn tuyệt đối tính từ Base URL
    @GET("api/schedules/data/api/")
    Call<List<ScheduleDto>> getSchedules();
}
