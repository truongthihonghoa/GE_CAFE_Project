package com.demo.ltud_n10.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkShiftRepositoryImpl implements WorkShiftRepository {

    private final List<WorkShift> shiftList = new ArrayList<>();

    @Inject
    public WorkShiftRepositoryImpl() {
        // Dữ liệu mẫu cho nhân viên "Lê Văn C" (NV001) trong tháng 4/2026 như trong hình
        
        // Tuần từ 13/4 - 19/4
        shiftList.add(new WorkShift("S1", "NV001", "Lê Văn C", "13/04/2026", "08:00", "16:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
        shiftList.add(new WorkShift("S2", "NV001", "Lê Văn C", "14/04/2026", "14:00", "22:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
        shiftList.add(new WorkShift("S3", "NV001", "Lê Văn C", "16/04/2026", "08:00", "16:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
        shiftList.add(new WorkShift("S4", "NV001", "Lê Văn C", "18/04/2026", "14:00", "22:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));

        // Các tuần khác
        shiftList.add(new WorkShift("S5", "NV001", "Lê Văn C", "06/04/2026", "08:00", "16:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
        shiftList.add(new WorkShift("S6", "NV001", "Lê Văn C", "08/04/2026", "14:00", "22:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
    }

    @Override
    public LiveData<Resource<List<WorkShift>>> getWorkShifts() {
        MutableLiveData<Resource<List<WorkShift>>> result = new MutableLiveData<>();
        result.setValue(Resource.success(new ArrayList<>(shiftList)));
        return result;
    }

    @Override
    public LiveData<Resource<WorkShift>> addWorkShift(WorkShift shift) {
        MutableLiveData<Resource<WorkShift>> result = new MutableLiveData<>();
        shift.setId(String.valueOf(System.currentTimeMillis()));
        shiftList.add(shift);
        result.setValue(Resource.success(shift));
        return result;
    }

    @Override
    public LiveData<Resource<WorkShift>> updateWorkShift(WorkShift shift) {
        MutableLiveData<Resource<WorkShift>> result = new MutableLiveData<>();
        for (int i = 0; i < shiftList.size(); i++) {
            if (shiftList.get(i).getId().equals(shift.getId())) {
                shiftList.set(i, shift);
                result.setValue(Resource.success(shift));
                return result;
            }
        }
        result.setValue(Resource.error("Không tìm thấy ca làm", null));
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteWorkShift(String shiftId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        shiftList.removeIf(s -> s.getId().equals(shiftId));
        result.setValue(Resource.success(true));
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> sendNotifications(List<String> shiftIds) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        for (WorkShift shift : shiftList) {
            if (shiftIds.contains(shift.getId())) {
                shift.setSent(true);
            }
        }
        result.setValue(Resource.success(true));
        return result;
    }
}
