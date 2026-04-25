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
    private final List<WorkShift> shiftList = new ArrayList<>();

    @Inject
    public WorkShiftRepositoryImpl(ScheduleApiService apiService) {
        this.apiService = apiService;
        
        // Dữ liệu mẫu khớp với hình ảnh (Tháng 2/2025)
        // Thứ 3 - 11/02/2025
        shiftList.add(new WorkShift("S1", "NV001", "Lê Văn C", "11/02/2025", "08:00", "16:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
        shiftList.add(new WorkShift("S2", "NV002", "Phạm Thị D", "11/02/2025", "14:00", "22:00", "Phục vụ", false, "Đã duyệt", "Đăng ký ca"));

        // Thứ 4 - 12/02/2025
        shiftList.add(new WorkShift("S3", "NV003", "Lê Văn D", "12/02/2025", "08:00", "16:00", "Giữ xe", true, "Đã duyệt", "Đăng ký ca"));

        // Thứ 5 - 13/02/2025
        shiftList.add(new WorkShift("S4", "NV001", "Lê Văn C", "13/02/2025", "08:00", "16:00", "Pha chế", false, "Đã duyệt", "Đăng ký ca"));

        // Dữ liệu khác
        shiftList.add(new WorkShift("S5", "NV004", "Trần Thị E", "14/02/2025", "08:00", "16:00", "Phục vụ", true, "Đã duyệt", "Đăng ký ca"));
    }

    @Override
    public LiveData<Resource<List<WorkShift>>> getWorkShifts() {
        // Tạm thời ưu tiên hiển thị dữ liệu mẫu (shiftList) để khớp với giao diện của bạn.
        // Nếu muốn dùng dữ liệu từ API server, hãy sử dụng đoạn code gọi apiService bên dưới.
        MutableLiveData<Resource<List<WorkShift>>> result = new MutableLiveData<>();
        result.setValue(Resource.success(new ArrayList<>(shiftList)));
        return result;
        
        /* 
        // Code gọi API (Bỏ comment khi backend đã sẵn sàng):
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
        */
    }

    private WorkShift mapDtoToDomain(ScheduleDto dto) {
        WorkShift shift = new WorkShift();
        shift.setId(dto.getMaLlv());
        shift.setEmployeeId(dto.getMaNv());
        shift.setEmployeeName("Nhân viên " + dto.getMaNv());

        String rawDate = dto.getNgayLam();
        if (rawDate != null && rawDate.contains("-")) {
            String[] parts = rawDate.split("-");
            shift.setDate(parts[2] + "/" + parts[1] + "/" + parts[0]);
        } else {
            shift.setDate(rawDate);
        }

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

        shift.setPosition("Pha chế");
        shift.setStatus(dto.getTrangThai() != null ? dto.getTrangThai() : "Đã duyệt");
        shift.setType("Đăng ký ca");
        shift.setSent(true);

        return shift;
    }

    @Override
    public LiveData<Resource<WorkShift>> addWorkShift(WorkShift shift) {
        shiftList.add(shift);
        return new MutableLiveData<>(Resource.success(shift));
    }

    @Override
    public LiveData<Resource<WorkShift>> updateWorkShift(WorkShift shift) {
        for (int i = 0; i < shiftList.size(); i++) {
            if (shiftList.get(i).getId().equals(shift.getId())) {
                shiftList.set(i, shift);
                return new MutableLiveData<>(Resource.success(shift));
            }
        }
        return new MutableLiveData<>(Resource.success(shift));
    }

    @Override
    public LiveData<Resource<Boolean>> deleteWorkShift(String shiftId) {
        shiftList.removeIf(s -> s.getId().equals(shiftId));
        return new MutableLiveData<>(Resource.success(true));
    }

    @Override
    public LiveData<Resource<Boolean>> sendNotifications(List<String> shiftIds) {
        for (WorkShift shift : shiftList) {
            if (shiftIds.contains(shift.getId())) {
                shift.setSent(true);
            }
        }
        return new MutableLiveData<>(Resource.success(true));
    }
}
