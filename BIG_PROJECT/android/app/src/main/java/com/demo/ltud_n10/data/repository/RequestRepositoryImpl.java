package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.RequestDto;
import com.demo.ltud_n10.domain.model.Request;
import com.demo.ltud_n10.domain.repository.RequestRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class RequestRepositoryImpl implements RequestRepository {

    private final ApiService apiService;

    @Inject
    public RequestRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Request>>> getRequests() {
        MutableLiveData<Resource<List<Request>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getRequests().enqueue(new Callback<List<RequestDto>>() {
            @Override
            public void onResponse(Call<List<RequestDto>> call, Response<List<RequestDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Request> requests = response.body().stream()
                            .map(RequestRepositoryImpl::mapToDomain)
                            .collect(Collectors.toList());
                    result.setValue(Resource.success(requests));
                } else {
                    result.setValue(Resource.error("Lỗi lấy danh sách yêu cầu", null));
                }
            }

            @Override
            public void onFailure(Call<List<RequestDto>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Request>> updateRequestStatus(String requestId, String status) {
        MutableLiveData<Resource<Request>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        RequestDto updateDto = new RequestDto();
        updateDto.setStatus(status);

        apiService.updateRequest(requestId, updateDto).enqueue(new Callback<RequestDto>() {
            @Override
            public void onResponse(Call<RequestDto> call, Response<RequestDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi cập nhật yêu cầu", null));
                }
            }

            @Override
            public void onFailure(Call<RequestDto> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    private static Request mapToDomain(RequestDto dto) {
        return new Request(
                dto.getId(),
                dto.getType(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getReason(),
                dto.getStatus(),
                dto.getEmployeeId(),
                dto.getEmployeeName()
        );
    }
}
