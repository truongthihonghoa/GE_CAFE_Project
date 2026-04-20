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
        // Mock data for employee "Lê Văn C" (NV001) across multiple weeks in March 2026
        
        // Week 1: 2/3 - 8/3
        shiftList.add(new WorkShift("S1", "NV001", "Lê Văn C", "02/03/2026", "08:00", "16:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
        shiftList.add(new WorkShift("S2", "NV001", "Lê Văn C", "03/03/2026", "14:00", "22:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
        shiftList.add(new WorkShift("S3", "NV001", "Lê Văn C", "05/03/2026", "08:00", "16:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
        shiftList.add(new WorkShift("S4", "NV001", "Lê Văn C", "07/03/2026", "14:00", "22:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));

        // Week 2: 9/3 - 15/3
        shiftList.add(new WorkShift("S5", "NV001", "Lê Văn C", "09/03/2026", "08:00", "16:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
        shiftList.add(new WorkShift("S6", "NV001", "Lê Văn C", "11/03/2026", "14:00", "22:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
        shiftList.add(new WorkShift("S7", "NV001", "Lê Văn C", "13/03/2026", "08:00", "16:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));

        // Week 0: 23/2 - 1/3 (Previous period)
        shiftList.add(new WorkShift("S0", "NV001", "Lê Văn C", "25/02/2026", "08:00", "16:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));

        // Other employees
        shiftList.add(new WorkShift("S8", "NV002", "Phạm Thị D", "02/03/2026", "14:00", "22:00", "Phục vụ", true, "Đã duyệt", "Đăng ký ca"));
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
