package com.demo.ltud_n10.data.remote;

import com.demo.ltud_n10.data.remote.model.EmployeeDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EmployeeApiService {
    @GET("api/employees/")
    Call<List<EmployeeDto>> getEmployees();

    @POST("api/employees/")
    Call<EmployeeDto> addEmployee(@Body EmployeeDto employeeDto);

    @PUT("api/employees/{id}/")
    Call<EmployeeDto> updateEmployee(@Path("id") String id, @Body EmployeeDto employeeDto);

    @DELETE("api/employees/{id}/")
    Call<Void> deleteEmployee(@Path("id") String id);
}
