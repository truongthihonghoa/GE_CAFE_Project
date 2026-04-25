package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ScheduleApiService;
import com.demo.ltud_n10.data.remote.model.ScheduleDto;
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

    private final ScheduleApiService apiService;

    @Inject
    public WorkShiftRepositoryImpl(ScheduleApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<WorkShift>>> getWorkShifts() {
        MutableLiveData<Resource<List<WorkShift>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getSchedules().enqueue(new Callback<List<ScheduleDto>>() {
            @Override
            public void onResponse(Call<List<ScheduleDto>> call, Response<List<ScheduleDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WorkShift> list = new ArrayList<>();
                    for (ScheduleDto dto : response.body()) {
                        list.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(list));
                } else {
                    result.setValue(Resource.error("Lỗi: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<List<ScheduleDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    private WorkShift mapDtoToDomain(ScheduleDto dto) {
        WorkShift shift = new WorkShift();
        shift.setId(dto.getMaLlv());
        shift.setEmployeeId(dto.getMaNv());
        shift.setEmployeeName("Nhân viên " + dto.getMaNv()); // Mặc định hiển thị mã nếu chưa có tên
        
        // Parse ngày lam
        String rawDate = dto.getNgayLam();
        if (rawDate != null && rawDate.contains("-")) {
            String[] parts = rawDate.split("-");
            shift.setDate(parts[2] + "/" + parts[1] + "/" + parts[0]);
        } else {
            shift.setDate(rawDate);
        }

        // Xử lý ca làm
        String caLam = dto.getCaLam();
        if (caLam != null && caLam.contains("-")) {
            try {
                String[] times = caLam.replaceAll("[^0-9:-]", "").split("-");
                if (times.length >= 2) {
                    shift.setStartTime(times[0]);
                    shift.setEndTime(times[1]);
                }
            } catch (Exception e) {
                shift.setStartTime("08:00");
                shift.setEndTime("17:00");
            }
        } else {
            shift.setStartTime("08:00");
            shift.setEndTime("17:00");
        }
        
        shift.setPosition("Pha chế"); // Mặc định
        shift.setStatus(dto.getTrangThai() != null ? dto.getTrangThai() : "Đã duyệt");
        shift.setType("Đăng ký ca");
        shift.setSent(true);
        
        return shift;
    }

    @Override
    public LiveData<Resource<WorkShift>> addWorkShift(WorkShift shift) {
        return new MutableLiveData<>(Resource.success(shift));
    }

    @Override
    public LiveData<Resource<WorkShift>> updateWorkShift(WorkShift shift) {
        return new MutableLiveData<>(Resource.success(shift));
    }

    @Override
    public LiveData<Resource<Boolean>> deleteWorkShift(String shiftId) {
        return new MutableLiveData<>(Resource.success(true));
    }

    @Override
    public LiveData<Resource<Boolean>> sendNotifications(List<String> shiftIds) {
        return new MutableLiveData<>(Resource.success(true));
    }
}
