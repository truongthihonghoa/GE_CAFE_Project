package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.AttendanceApiService;
import com.demo.ltud_n10.data.remote.model.AttendanceDto;
import com.demo.ltud_n10.domain.model.Attendance;
import com.demo.ltud_n10.domain.repository.AttendanceRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AttendanceRepositoryImpl implements AttendanceRepository {

    private final AttendanceApiService apiService;

    @Inject
    public AttendanceRepositoryImpl(AttendanceApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Attendance>>> getAttendances() {
        MutableLiveData<Resource<List<Attendance>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getAttendances().enqueue(new Callback<List<AttendanceDto>>() {
            @Override
            public void onResponse(Call<List<AttendanceDto>> call, Response<List<AttendanceDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Attendance> list = new ArrayList<>();
                    for (AttendanceDto dto : response.body()) {
                        list.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(list));
                } else {
                    result.setValue(Resource.error("Lỗi khi tải dữ liệu chấm công: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối API chấm công: " + t.getMessage(), null));
            }
        });

        return result;
    }

    private Attendance mapDtoToDomain(AttendanceDto dto) {
        Attendance attendance = new Attendance();
        attendance.setId(dto.getMaCc());
        attendance.setEmployeeId(dto.getMaNv());
        attendance.setEmployeeName(dto.getMaNv()); // API chưa trả về tên
        attendance.setDate(dto.getNgayLam());
        attendance.setCheckIn(dto.getGioVao());
        attendance.setCheckOut(dto.getGioRa());
        attendance.setHoursWorked(dto.getSoGioLam());
        attendance.setStatus(dto.getTrangThai());
        attendance.setNote(dto.getGhiChu());
        return attendance;
    }
}
