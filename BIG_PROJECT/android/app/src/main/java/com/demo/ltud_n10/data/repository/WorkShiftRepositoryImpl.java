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

import java.util.ArrayList;
import java.util.List;

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
                        // Ánh xạ từ DTO sang Domain Model
                        WorkShift shift = new WorkShift();
                        shift.setId(dto.getId());
                        shift.setEmployeeId(dto.getEmployeeId());
                        shift.setEmployeeName(dto.getEmployeeName());
                        shift.setDate(dto.getDate());
                        shift.setStartTime(dto.getStartTime());
                        shift.setEndTime(dto.getEndTime());
                        shift.setPosition(dto.getPosition());
                        shift.setStatus(dto.getStatus());
                        shifts.add(shift);
                    }
                    result.setValue(Resource.success(shifts));
                } else {
                    result.setValue(Resource.error("Lỗi tải lịch làm việc", null));
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
        return new MutableLiveData<>();
    }

    @Override
    public LiveData<Resource<WorkShift>> updateWorkShift(WorkShift shift) {
        return new MutableLiveData<>();
    }

    @Override
    public LiveData<Resource<Boolean>> deleteWorkShift(String shiftId) {
        return new MutableLiveData<>();
    }

    @Override
    public LiveData<Resource<Boolean>> sendNotifications(List<String> shiftIds) {
        return new MutableLiveData<>(Resource.success(true));
    }
}
