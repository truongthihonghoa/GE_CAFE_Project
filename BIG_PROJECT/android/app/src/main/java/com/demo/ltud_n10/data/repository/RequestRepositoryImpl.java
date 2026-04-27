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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
        
        Call<RequestDto> call;
        if ("Nghỉ phép".equals(request.getType())) {
            call = apiService.addLeaveRequest(mapDomainToDto(request));
        } else {
            call = apiService.addRequest(mapDomainToDto(request));
        }

        call.enqueue(new Callback<RequestDto>() {
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
    public LiveData<Resource<Request>> updateRequest(String oldId, Request request) {
        MutableLiveData<Resource<Request>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        // NẾU ID THAY ĐỔI (Chỉnh sửa từ phía nhân viên để cập nhật thời gian gửi)
        if (!oldId.equals(request.getId())) {
            // Bước 1: Xóa yêu cầu cũ
            Call<Void> deleteCall = "Nghỉ phép".equals(request.getType()) ? 
                    apiService.deleteLeaveRequest(oldId) : apiService.deleteRequest(oldId);
            
            deleteCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    // Bước 2: Sau khi xóa thành công (hoặc kể cả thất bại do không tìm thấy), thêm yêu cầu mới
                    addRequest(request).observeForever(resource -> {
                        if (resource != null) result.setValue(resource);
                    });
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    // Nếu lỗi mạng không xóa được, vẫn thử thêm mới nhưng báo lỗi
                    addRequest(request).observeForever(resource -> {
                        if (resource != null) result.setValue(resource);
                    });
                }
            });
        } else {
            // NẾU ID KHÔNG ĐỔI (Chỉnh sửa từ phía quản trị viên hoặc không đổi giờ)
            Call<RequestDto> call;
            if ("Nghỉ phép".equals(request.getType())) {
                call = apiService.updateLeaveRequest(oldId, mapDomainToDto(request));
            } else {
                call = apiService.updateRequest(oldId, mapDomainToDto(request));
            }

            call.enqueue(new Callback<RequestDto>() {
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
        }
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteRequest(String requestId, String type) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        
        Call<Void> call;
        if ("Nghỉ phép".equals(type)) {
            call = apiService.deleteLeaveRequest(requestId);
        } else {
            call = apiService.deleteRequest(requestId);
        }

        call.enqueue(new Callback<Void>() {
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
        
        try {
            String id = dto.getId();
            if (id != null && id.length() >= 15) {
                String timestampStr = id.substring(2, 15);
                long timestamp = Long.parseLong(timestampStr);
                request.setCreatedAt(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date(timestamp)));
            } else {
                request.setCreatedAt(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date()));
            }
        } catch (Exception e) {
            request.setCreatedAt(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        }

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
