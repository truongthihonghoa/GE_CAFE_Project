package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.RequestDto;
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
            public void onResponse(@NonNull Call<List<RequestDto>> call, @NonNull Response<List<RequestDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Request> requests = new ArrayList<>();
                    for (RequestDto dto : response.body()) {
                        requests.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(requests));
                } else {
                    result.setValue(Resource.error("Lỗi khi tải đăng ký: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RequestDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Request>> addRequest(Request request) {
        MutableLiveData<Resource<Request>> result = new MutableLiveData<>();
        apiService.addRequest(mapDomainToDto(request)).enqueue(new Callback<RequestDto>() {
            @Override
            public void onResponse(@NonNull Call<RequestDto> call, @NonNull Response<RequestDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi khi thêm yêu cầu", null));
                }
            }
            @Override
            public void onFailure(@NonNull Call<RequestDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Request>> updateRequest(Request request) {
        MutableLiveData<Resource<Request>> result = new MutableLiveData<>();
        apiService.updateRequest(request.getId(), mapDomainToDto(request)).enqueue(new Callback<RequestDto>() {
            @Override
            public void onResponse(@NonNull Call<RequestDto> call, @NonNull Response<RequestDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi khi cập nhật yêu cầu", null));
                }
            }
            @Override
            public void onFailure(@NonNull Call<RequestDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteRequest(String requestId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        apiService.deleteRequest(requestId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) result.setValue(Resource.success(true));
                else result.setValue(Resource.error("Lỗi khi xóa yêu cầu", false));
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Request>> updateRequestStatus(String requestId, String status) {
        MutableLiveData<Resource<Request>> result = new MutableLiveData<>();

        RequestDto updateDto = new RequestDto();
        updateDto.setStatus(status);

        apiService.updateRequest(requestId, updateDto).enqueue(new Callback<RequestDto>() {
            @Override
            public void onResponse(@NonNull Call<RequestDto> call, @NonNull Response<RequestDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi khi cập nhật trạng thái", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RequestDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    private Request mapDtoToDomain(RequestDto dto) {
        Request request = new Request();
        request.setId(dto.getId());
        request.setEmployeeId(dto.getEmployeeId());
        request.setType(dto.getType());
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());
        request.setStatus(dto.getStatus());
        return request;
    }

    private RequestDto mapDomainToDto(Request request) {
        RequestDto dto = new RequestDto();
        dto.setId(request.getId());
        dto.setEmployeeId(request.getEmployeeId());
        dto.setType(request.getType());
        dto.setStartDate(request.getStartDate());
        dto.setEndDate(request.getEndDate());
        dto.setReason(request.getReason());
        dto.setStatus(request.getStatus());
        return dto;
    }
}
