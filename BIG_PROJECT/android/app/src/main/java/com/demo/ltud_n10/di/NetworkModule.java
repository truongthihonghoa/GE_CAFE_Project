package com.demo.ltud_n10.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public static Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient(com.demo.ltud_n10.data.local.SharedPrefsManager prefsManager) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    String token = prefsManager.getToken();
                    if (token == null) {
                        token = "e9b0a9b8bc695e19e74f71f270505a5ac2d6ff48"; // Fallback token for testing (Chu)
                    }
                    okhttp3.Request.Builder builder = chain.request().newBuilder();
                    builder.addHeader("Authorization", "Token " + token);
                    return chain.proceed(builder.build());
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
    public static com.demo.ltud_n10.data.remote.ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(com.demo.ltud_n10.data.remote.ApiService.class);
    }
}
