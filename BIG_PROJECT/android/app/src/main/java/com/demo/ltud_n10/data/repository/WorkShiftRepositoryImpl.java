package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.local.SharedPrefsManager;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.ScheduleDto;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkShiftRepositoryImpl implements WorkShiftRepository {

    private final ApiService apiService;
    private final SharedPrefsManager prefsManager;

    @Inject
    public WorkShiftRepositoryImpl(ApiService apiService, SharedPrefsManager prefsManager) {
        this.apiService = apiService;
        this.prefsManager = prefsManager;
    }

    @Override
    public LiveData<Resource<List<WorkShift>>> getWorkShifts() {
        MutableLiveData<Resource<List<WorkShift>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        String maNv = prefsManager.getMaNv();
        String filterMaNv = prefsManager.isStaff() ? null : maNv;

        apiService.getSchedules(filterMaNv).enqueue(new Callback<List<ScheduleDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ScheduleDto>> call, @NonNull Response<List<ScheduleDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WorkShift> shifts = new ArrayList<>();
                    for (ScheduleDto dto : response.body()) {
                        shifts.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(shifts));
                } else {
                    result.setValue(Resource.error("Lỗi: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ScheduleDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối", null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<WorkShift>> addWorkShift(WorkShift shift) {
        MutableLiveData<Resource<WorkShift>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        
        ScheduleDto dto = mapDomainToDto(shift);
        
        apiService.addSchedule(dto).enqueue(new Callback<ScheduleDto>() {
            @Override
            public void onResponse(@NonNull Call<ScheduleDto> call, @NonNull Response<ScheduleDto> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Lỗi từ Server (400)";
                        result.setValue(Resource.error(errorMsg, null));
                    } catch (Exception e) {
                        result.setValue(Resource.error("Lỗi hệ thống khi thêm", null));
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ScheduleDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<WorkShift>> updateWorkShift(WorkShift shift) {
        MutableLiveData<Resource<WorkShift>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        
        ScheduleDto dto = mapDomainToDto(shift);

        apiService.updateSchedule(shift.getId(), dto).enqueue(new Callback<ScheduleDto>() {
            @Override
            public void onResponse(@NonNull Call<ScheduleDto> call, @NonNull Response<ScheduleDto> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Lỗi cập nhật";
                        result.setValue(Resource.error(errorMsg, null));
                    } catch (Exception e) {
                        result.setValue(Resource.error("Lỗi khi cập nhật yêu cầu", null));
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ScheduleDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteWorkShift(String shiftId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        apiService.deleteSchedule(shiftId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) result.setValue(Resource.success(true));
                else result.setValue(Resource.error("Không thể xóa yêu cầu này", false));
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> sendNotifications(List<String> shiftIds) {
        return new MutableLiveData<>(Resource.success(true));
    }

    private WorkShift mapDtoToDomain(ScheduleDto dto) {
        WorkShift shift = new WorkShift();
        shift.setId(dto.getId());
        shift.setEmployeeId(dto.getEmployeeId());
        shift.setEmployeeName(dto.getEmployeeName() != null ? dto.getEmployeeName() : dto.getEmployeeId());
        shift.setDate(dto.getWorkDate());
        shift.setBranchId(dto.getBranchId());
        shift.setSentTime(dto.getCreatedAt());
        
        String caLam = dto.getShift();
        if (caLam != null && caLam.contains("-")) {
            String[] parts = caLam.split("-");
            shift.setStartTime(parts[0].trim());
            shift.setEndTime(parts.length > 1 ? parts[1].trim() : "");
        } else {
            shift.setStartTime(caLam != null ? caLam : "");
            shift.setEndTime("");
        }
        
        shift.setStatus(dto.getStatus() != null ? dto.getStatus() : "Chờ duyệt");
        shift.setPosition(dto.getNote());
        
        // QUAN TRỌNG: Tự động gán Type để không bị crash khi filter
        if (dto.getNote() != null && (dto.getNote().contains("Nghỉ") || dto.getNote().contains("nghỉ") || dto.getWorkDate().contains(" - "))) {
            shift.setType("Nghỉ phép");
        } else {
            shift.setType("Đăng ký ca làm");
        }
        
        return shift;
    }

    private ScheduleDto mapDomainToDto(WorkShift shift) {
        ScheduleDto dto = new ScheduleDto();
        dto.setId(shift.getId());
        dto.setEmployeeId(shift.getEmployeeId());
        dto.setWorkDate(shift.getDate());
        
        String shiftStr = (shift.getStartTime() != null ? shift.getStartTime() : "") + 
                         " - " + (shift.getEndTime() != null ? shift.getEndTime() : "");
        dto.setShift(shiftStr);
        
        dto.setStatus(shift.getStatus());
        dto.setBranchId(shift.getBranchId());
        
        String createdAt = shift.getSentTime();
        if (createdAt == null || createdAt.isEmpty() || "null".equals(createdAt)) {
            createdAt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
        dto.setCreatedAt(createdAt);
        
        // Giữ nguyên ghi chú (Lý do nghỉ hoặc Tên ca)
        dto.setNote(shift.getPosition());

        return dto;
    }
}
