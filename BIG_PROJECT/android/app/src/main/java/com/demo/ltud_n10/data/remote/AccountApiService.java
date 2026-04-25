package com.demo.ltud_n10.data.remote;

import com.demo.ltud_n10.data.remote.model.UserDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AccountApiService {
    @GET("api/accounts/")
    Call<List<UserDto>> getAccounts();
}
