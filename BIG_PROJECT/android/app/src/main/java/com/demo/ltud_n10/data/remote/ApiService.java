package com.demo.ltud_n10.data.remote;

import com.demo.ltud_n10.data.remote.dto.AccountDto;
import com.demo.ltud_n10.data.remote.dto.BranchDto;
import com.demo.ltud_n10.data.remote.model.ContractDto;
import com.demo.ltud_n10.data.remote.dto.ScheduleDto;
import com.demo.ltud_n10.data.remote.model.EmployeeDto;
import com.demo.ltud_n10.data.remote.dto.LoginRequest;
import com.demo.ltud_n10.data.remote.dto.LoginResponse;
import com.demo.ltud_n10.data.remote.dto.RequestDto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("accounts/api/login/")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("accounts/taikhoan")
    Call<List<AccountDto>> getMyAccount(@Header("Authorization") String token);

    @GET("api/accounts/")
    Call<List<AccountDto>> getAccounts(@Query("ma_nv") String maNv);

    @POST("api/accounts/")
    Call<AccountDto> createAccount(@Body AccountDto account);

    @PUT("api/accounts/{id}/")
    Call<AccountDto> updateAccount(@Path("id") String id, @Body AccountDto account);

    @DELETE("api/accounts/{id}/")
    Call<Void> deleteAccount(@Path("id") String id);

    @POST("api/accounts/{id}/change-password/")
    Call<Void> changePassword(@Path("id") String id, @Body Map<String, String> body);

    @GET("api/contracts/")
    Call<List<ContractDto>> getContracts(@Query("ma_nv") String maNv);

    @POST("api/contracts/")
    Call<ContractDto> addContract(@Body ContractDto contract);

    @PUT("api/contracts/{id}/")
    Call<ContractDto> updateContract(@Path("id") String id, @Body ContractDto contract);

    @DELETE("api/contracts/{id}/")
    Call<Void> deleteContract(@Path("id") String id);

    @GET("api/schedules/data/api/")
    Call<List<ScheduleDto>> getSchedules(@Query("ma_nv") String maNv);

    @POST("api/schedules/data/api/")
    Call<ScheduleDto> addSchedule(@Body ScheduleDto schedule);

    @PUT("api/schedules/data/api/{id}/")
    Call<ScheduleDto> updateSchedule(@Path("id") String id, @Body ScheduleDto schedule);

    @DELETE("api/schedules/data/api/{id}/")
    Call<Void> deleteSchedule(@Path("id") String id);

    @GET("api/requests/dangkylich/")
    Call<List<RequestDto>> getRequests(@Query("ma_nv") String maNv);

    @GET("api/requests/nghiphep/")
    Call<List<RequestDto>> getLeaveRequests(@Query("ma_nv") String maNv);

    @POST("api/requests/dangkylich/")
    Call<RequestDto> addRequest(@Body RequestDto request);

    @POST("api/requests/nghiphep/")
    Call<RequestDto> addLeaveRequest(@Body RequestDto request);

    @PUT("api/requests/dangkylich/{id}/")
    Call<RequestDto> updateRequest(@Path("id") String id, @Body RequestDto request);

    @PUT("api/requests/nghiphep/{id}/")
    Call<RequestDto> updateLeaveRequest(@Path("id") String id, @Body RequestDto request);

    @DELETE("api/requests/dangkylich/{id}/")
    Call<Void> deleteRequest(@Path("id") String id);

    @DELETE("api/requests/nghiphep/{id}/")
    Call<Void> deleteLeaveRequest(@Path("id") String id);

    @GET("api/branches/")
    Call<List<BranchDto>> getBranches();

    @POST("api/branches/")
    Call<BranchDto> addBranch(@Body BranchDto branch);

    @PUT("api/branches/{id}/")
    Call<BranchDto> updateBranch(@Path("id") String id, @Body BranchDto branch);

    @DELETE("api/branches/{id}/")
    Call<Void> deleteBranch(@Path("id") String id);

    @GET("api/employees/")
    Call<List<EmployeeDto>> getEmployees(@Query("ma_nv") String maNv);

    @POST("api/employees/")
    Call<EmployeeDto> addEmployee(@Body EmployeeDto employee);

    @PUT("api/employees/{id}/")
    Call<EmployeeDto> updateEmployee(@Path("id") String id, @Body EmployeeDto employee);

    @DELETE("api/employees/{id}/")
    Call<Void> deleteEmployee(@Path("id") String id);

    // Schedules
    @GET("api/schedules/data/api/")
    Call<List<ScheduleDto>> getSchedules();

    // Attendances
    @GET("api/attendances/")
    Call<List<Map<String, Object>>> getAttendances();

    @POST("api/attendances/")
    Call<Map<String, Object>> checkIn(@Body Map<String, String> body);
}
