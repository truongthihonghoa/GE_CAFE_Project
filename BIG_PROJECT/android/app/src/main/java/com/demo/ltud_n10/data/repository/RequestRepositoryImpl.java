package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.local.SharedPrefsManager;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.RequestDto;
import com.demo.ltud_n10.domain.model.Request;
import com.demo.ltud_n10.domain.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class RequestRepositoryImpl implements RequestRepository {

    private final ApiService apiService;
    private final SharedPrefsManager prefsManager;

    @Inject
    public RequestRepositoryImpl(ApiService apiService, SharedPrefsManager prefsManager) {
        this.apiService = apiService;
        this.prefsManager = prefsManager;
    }

    @Override
    public LiveData<Resource<List<Request>>> getRequests() {
        MutableLiveData<Resource<List<Request>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        String maNv = prefsManager.getMaNv();
        String filterMaNv = prefsManager.isStaff() ? null : maNv;

        List<Request> allRequests = new ArrayList<>();
        AtomicInteger remainingRequests = new AtomicInteger(2);

        Callback<List<RequestDto>> callback = new Callback<List<RequestDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RequestDto>> call, @NonNull Response<List<RequestDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (RequestDto dto : response.body()) {
                        allRequests.add(mapDtoToDomain(dto));
                    }
                }
                if (remainingRequests.decrementAndGet() == 0) {
                    result.setValue(Resource.success(allRequests));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RequestDto>> call, @NonNull Throwable t) {
                if (remainingRequests.decrementAndGet() == 0) {
                    result.setValue(Resource.success(allRequests));
                }
            }
        };

        apiService.getRequests(filterMaNv).enqueue(callback);
        apiService.getLeaveRequests(filterMaNv).enqueue(callback);

        return result;
    }

    @Override
    public LiveData<Resource<Request>> addRequest(Request request) {
        MutableLiveData<Resource<Request>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        
        // FIX TRIỆT ĐỂ: Gửi toàn bộ dữ liệu bao gồm cả ID (ma_yc) mà Fragment đã tạo
        // để thỏa mãn điều kiện "Required" của Server
        apiService.addRequest(mapDomainToDto(request)).enqueue(new Callback<RequestDto>() {
            @Override
            public void onResponse(@NonNull Call<RequestDto> call, @NonNull Response<RequestDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    String error = "Lỗi gửi yêu cầu";
                    try {
                        if (response.errorBody() != null) error = response.errorBody().string();
                    } catch (Exception ignored) {}
                    result.setValue(Resource.error(error, null));
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
        result.setValue(Resource.loading(null));

        apiService.updateRequest(request.getId(), mapDomainToDto(request)).enqueue(new Callback<RequestDto>() {
            @Override
            public void onResponse(@NonNull Call<RequestDto> call, @NonNull Response<RequestDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Cập nhật thất bại", null));
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
                else result.setValue(Resource.error("Xóa thất bại", false));
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
        return null; 
    }

    private Request mapDtoToDomain(RequestDto dto) {
        Request request = new Request();
        request.setId(dto.getId());
        request.setEmployeeId(dto.getEmployeeId());
        request.setEmployeeName(dto.getEmployeeName());
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
        dto.setEmployeeName(request.getEmployeeName());
        dto.setType(request.getType());
        dto.setStartDate(request.getStartDate());
        dto.setEndDate(request.getEndDate());
        dto.setReason(request.getReason());
        dto.setStatus(request.getStatus());
        return dto;
    }
}
