package com.demo.ltud_n10.data.remote;

import com.demo.ltud_n10.data.remote.dto.AccountDto;
import com.demo.ltud_n10.data.remote.dto.BranchDto;
import com.demo.ltud_n10.data.remote.dto.ContractDto;
import com.demo.ltud_n10.data.remote.dto.PayrollDto;
import com.demo.ltud_n10.data.remote.dto.ScheduleDto;
import com.demo.ltud_n10.data.remote.dto.EmployeeDto;
import com.demo.ltud_n10.data.remote.dto.LoginRequest;
import com.demo.ltud_n10.data.remote.dto.LoginResponse;
import com.demo.ltud_n10.data.remote.dto.RequestDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api-token-auth/")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/employees/")
    Call<List<EmployeeDto>> getEmployees();

    // Branch
    @GET("api/branches/")
    Call<List<BranchDto>> getBranches();

    @POST("api/branches/")
    @Headers("Content-Type: application/json")
    Call<BranchDto> addBranch(@Body BranchDto branch);

    @PUT("api/branches/{id}/")
    @Headers("Content-Type: application/json")
    Call<BranchDto> updateBranch(@Path("id") String id, @Body BranchDto branch);

    @DELETE("api/branches/{id}/")
    Call<Void> deleteBranch(@Path("id") String id);

    // Accounts
    @GET("api/accounts/")
    Call<List<AccountDto>> getAccounts();

    @POST("api/accounts/")
    @Headers("Content-Type: application/json")
    Call<AccountDto> createAccount(@Body AccountDto account);

    @PUT("api/accounts/{id}/")
    @Headers("Content-Type: application/json")
    Call<AccountDto> updateAccount(@Path("id") String id, @Body AccountDto account);

    @DELETE("api/accounts/{id}/")
    Call<Void> deleteAccount(@Path("id") String id);

    @POST("api/accounts/{id}/change-password/")
    @Headers("Content-Type: application/json")
    Call<Void> changePassword(@Path("id") String id, @Body java.util.Map<String, String> body);

    // Schedule Endpoints
    @GET("api/schedules/data/api/")
    Call<List<ScheduleDto>> getSchedules();

    @POST("api/schedules/data/api/")
    Call<ScheduleDto> addSchedule(@Body ScheduleDto schedule);

    @PUT("api/schedules/data/api/{id}/")
    Call<ScheduleDto> updateSchedule(@Path("id") String id, @Body ScheduleDto schedule);

    @DELETE("api/schedules/data/api/{id}/")
    Call<Void> deleteSchedule(@Path("id") String id);

    @POST("api/schedules/send-notification/")
    Call<Void> sendScheduleNotifications(@Body List<String> scheduleIds);

    // Contract Endpoints
    @GET("api/contracts/")
    Call<List<ContractDto>> getContracts();

    @POST("api/contracts/")
    Call<ContractDto> addContract(@Body ContractDto contract);

    @PUT("api/contracts/{id}/")
    Call<ContractDto> updateContract(@Path("id") String id, @Body ContractDto contract);

    @DELETE("api/contracts/{id}/")
    Call<Void> deleteContract(@Path("id") String id);

    // Payroll Endpoints
    @GET("api/payroll/data/api/")
    Call<List<PayrollDto>> getPayrolls(@retrofit2.http.Query("month") String month, @retrofit2.http.Query("year") String year);

    @POST("api/payroll/data/api/")
    Call<PayrollDto> addPayroll(@Body PayrollDto payroll);

    @PUT("api/payroll/data/api/{id}/")
    Call<PayrollDto> updatePayroll(@Path("id") String id, @Body PayrollDto payroll);

    @DELETE("api/payroll/data/api/{id}/")
    Call<Void> deletePayroll(@Path("id") String id);

    @POST("api/employees/")
    Call<EmployeeDto> addEmployee(@Body EmployeeDto employee);

    @PUT("api/employees/{id}/")
    Call<EmployeeDto> updateEmployee(@Path("id") String id, @Body EmployeeDto employee);

    @DELETE("api/employees/{id}/")
    Call<Void> deleteEmployee(@Path("id") String id);
    // Requests
    @GET("api/requests/yeu-cau/")
    Call<List<RequestDto>> getRequests();

    @POST("api/requests/yeu-cau/")
    Call<RequestDto> addRequest(@Body RequestDto request);

    @PATCH("api/requests/yeu-cau/{id}/")
    Call<RequestDto> updateRequest(@Path("id") String id, @Body RequestDto request);
}
