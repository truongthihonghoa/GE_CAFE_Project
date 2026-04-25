package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.RequestApiService;
import com.demo.ltud_n10.data.remote.model.RequestDto;
import com.demo.ltud_n10.domain.model.Request;
import com.demo.ltud_n10.domain.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class RequestRepositoryImpl implements RequestRepository {

    private final RequestApiService apiService;

    @Inject
    public RequestRepositoryImpl(RequestApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Request>>> getRequests() {
        MutableLiveData<Resource<List<Request>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        List<Request> combinedList = new ArrayList<>();
        
        // Gọi API Đăng ký lịch
        apiService.getScheduleRequests().enqueue(new Callback<List<RequestDto>>() {
            @Override
            public void onResponse(Call<List<RequestDto>> call, Response<List<RequestDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (RequestDto dto : response.body()) {
                        combinedList.add(mapDtoToDomain(dto));
                    }
                    
                    // Sau khi xong API 1, gọi tiếp API Nghỉ phép
                    fetchLeaveRequests(result, combinedList);
                } else {
                    result.setValue(Resource.error("Lỗi tải yêu cầu đăng ký lịch: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<List<RequestDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối API đăng ký lịch", null));
            }
        });

        return result;
    }

    private void fetchLeaveRequests(MutableLiveData<Resource<List<Request>>> result, List<Request> combinedList) {
        apiService.getLeaveRequests().enqueue(new Callback<List<RequestDto>>() {
            @Override
            public void onResponse(Call<List<RequestDto>> call, Response<List<RequestDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (RequestDto dto : response.body()) {
                        combinedList.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(combinedList));
                } else {
                    result.setValue(Resource.error("Lỗi tải yêu cầu nghỉ phép: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<List<RequestDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối API nghỉ phép", null));
            }
        });
    }

    private Request mapDtoToDomain(RequestDto dto) {
        Request request = new Request();
        request.setId(dto.getMaYc());
        request.setType(dto.getLoaiYeuCau());
        request.setStartDate(dto.getNgayBd());
        request.setEndDate(dto.getNgayKt());
        request.setReason(dto.getLyDo());
        request.setStatus(dto.getTrangThai());
        request.setEmployeeId(dto.getMaNv());
        return request;
    }
}
