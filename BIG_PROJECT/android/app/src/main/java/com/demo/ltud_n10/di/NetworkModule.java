package com.demo.ltud_n10.di;

import com.demo.ltud_n10.data.remote.AccountApiService;
import com.demo.ltud_n10.data.remote.AttendanceApiService;
import com.demo.ltud_n10.data.remote.BranchApiService;
import com.demo.ltud_n10.data.remote.ContractApiService;
import com.demo.ltud_n10.data.remote.EmployeeApiService;
import com.demo.ltud_n10.data.remote.RequestApiService;
import com.demo.ltud_n10.data.remote.ScheduleApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    // QUAN TRỌNG: Hãy đảm bảo Token này được lấy từ Admin của link ge-cafe-project.onrender.com
    private static final String AUTH_TOKEN = "9f81a8e737e6a5e6f8b0305623c6fe86efd6603b"; 

    @Provides
    @Singleton
    public static Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor authInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                
                // Luôn gán Token và Accept header cho mọi request
                Request requestWithHeaders = originalRequest.newBuilder()
                        .header("Authorization", "Token " + AUTH_TOKEN)
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .header("User-Agent", "Android-App")
                        .removeHeader("Cookie") 
                        .build();
                return chain.proceed(requestWithHeaders);
            }
        };

        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(90, TimeUnit.SECONDS) // Tăng lên 90s cho Render Free
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {}
                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return Collections.emptyList();
                    }
                })
                .build();
    }

    @Provides
    @Singleton
    public static Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl("https://ge-cafe-project-1.onrender.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @Singleton
    public static ContractApiService provideContractApiService(Retrofit retrofit) {
        return retrofit.create(ContractApiService.class);
    }

    @Provides
    @Singleton
    public static EmployeeApiService provideEmployeeApiService(Retrofit retrofit) {
        return retrofit.create(EmployeeApiService.class);
    }

    @Provides
    @Singleton
    public static BranchApiService provideBranchApiService(Retrofit retrofit) {
        return retrofit.create(BranchApiService.class);
    }

    @Provides
    @Singleton
    public static AccountApiService provideAccountApiService(Retrofit retrofit) {
        return retrofit.create(AccountApiService.class);
    }

    @Provides
    @Singleton
    public static AttendanceApiService provideAttendanceApiService(Retrofit retrofit) {
        return retrofit.create(AttendanceApiService.class);
    }

    @Provides
    @Singleton
    public static RequestApiService provideRequestApiService(Retrofit retrofit) {
        return retrofit.create(RequestApiService.class);
    }

    @Provides
    @Singleton
    public static ScheduleApiService provideScheduleApiService(Retrofit retrofit) {
        return retrofit.create(ScheduleApiService.class);
    }
}
