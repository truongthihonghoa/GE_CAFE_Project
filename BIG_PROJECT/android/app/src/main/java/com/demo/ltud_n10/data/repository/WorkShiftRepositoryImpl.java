package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
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

    @Inject
    public WorkShiftRepositoryImpl(ApiService apiService) {
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
                    List<WorkShift> shifts = new ArrayList<>();
                    for (ScheduleDto dto : response.body()) {
                        shifts.add(mapToDomain(dto));
                    }
                    result.setValue(Resource.success(shifts));
                } else {
                    result.setValue(Resource.error("Lỗi lấy lịch làm: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<List<ScheduleDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<WorkShift>> addWorkShift(WorkShift shift) {
        MutableLiveData<Resource<WorkShift>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.addSchedule(mapToDto(shift)).enqueue(new Callback<ScheduleDto>() {
            @Override
            public void onResponse(Call<ScheduleDto> call, Response<ScheduleDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi thêm ca làm: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<ScheduleDto> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<WorkShift>> updateWorkShift(WorkShift shift) {
        MutableLiveData<Resource<WorkShift>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.updateSchedule(shift.getId(), mapToDto(shift)).enqueue(new Callback<ScheduleDto>() {
            @Override
            public void onResponse(Call<ScheduleDto> call, Response<ScheduleDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi cập nhật ca làm: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<ScheduleDto> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteWorkShift(String shiftId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.deleteSchedule(shiftId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Lỗi xóa ca làm: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> sendNotifications(List<String> shiftIds) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.sendScheduleNotifications(shiftIds).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Lỗi gửi thông báo: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    private WorkShift mapToDomain(ScheduleDto dto) {
        // ... (date/time parsing remains same)
        String start = "08:00";
        String end = "16:00";
        if (dto.getCaLam() != null && dto.getCaLam().contains(" - ")) {
            String[] times = dto.getCaLam().split(" - ");
            if (times.length == 2) {
                start = times[0];
                end = times[1];
            }
        }

        String date = dto.getNgayLam();
        if (date != null && date.contains("-")) {
            String[] p = date.split("-");
            if (p.length == 3) {
                date = p[2] + "/" + p[1] + "/" + p[0];
            }
        }

        List<WorkShift.EmployeeAssignment> assignments = new ArrayList<>();
        if (dto.getChiTietNhanVien() != null) {
            for (ScheduleDto.ScheduleDetailDto detail : dto.getChiTietNhanVien()) {
                assignments.add(new WorkShift.EmployeeAssignment(detail.getMaNv(), "", detail.getViTri()));
            }
        }

        return new WorkShift(
                dto.getMaLlv(),
                assignments,
                dto.getHoTenNv() != null ? dto.getHoTenNv() : "Nhiều nhân viên",
                date,
                start,
                end,
                "Nhân viên",
                "Đã gửi".equals(dto.getTrangThai()),
                dto.getTrangThai(),
                dto.getGhiChu()
        );
    }

    private ScheduleDto mapToDto(WorkShift domain) {
        ScheduleDto dto = new ScheduleDto();
        dto.setMaLlv(domain.getId());
        
        List<ScheduleDto.ScheduleDetailDto> details = new ArrayList<>();
        for (WorkShift.EmployeeAssignment assignment : domain.getEmployeeAssignments()) {
            details.add(new ScheduleDto.ScheduleDetailDto(assignment.getEmployeeId(), assignment.getPosition()));
        }
        dto.setChiTietNhanVien(details);

        dto.setCaLam(domain.getStartTime() + " - " + domain.getEndTime());
        dto.setTrangThai(domain.getStatus());
        dto.setGhiChu(domain.getType());
        dto.setMaChiNhanh("CN01");

        // Chuyển DD/MM/YYYY sang YYYY-MM-DD
        String date = domain.getDate();
        if (date != null && date.contains("/")) {
            String[] p = date.split("/");
            if (p.length == 3) {
                dto.setNgayLam(p[2] + "-" + p[1] + "-" + p[0]);
            }
        } else {
            dto.setNgayLam(date);
        }
        
        dto.setNgayTao(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        return dto;
    }
}
