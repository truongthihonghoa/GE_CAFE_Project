package com.demo.ltud_n10.data.repository;

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
        // Mock data matching the UI screenshot (Feb 2025)
        // Week: 10 Feb - 16 Feb, 2025

        // Tuesday (Th 3) - 11/02/2025
        shiftList.add(new WorkShift("S1", "NV001", "Lê Văn C", "11/02/2026", "08:00", "16:00", "Pha chế", true, "Đã duyệt", "Đăng ký ca"));
        shiftList.add(new WorkShift("S2", "NV002", "Phạm Thị D", "11/02/2026", "14:00", "22:00", "Phục vụ", false, "Đã duyệt", "Đăng ký ca"));

        // Wednesday (Th 4) - 12/02/2025
        shiftList.add(new WorkShift("S3", "NV003", "Lê Văn D", "12/02/2026", "08:00", "16:00", "Giữ xe", true, "Đã duyệt", "Đăng ký ca"));
        
        // Thursday (Th 5) - 13/02/2025
        shiftList.add(new WorkShift("S4", "NV001", "Lê Văn C", "13/02/2026", "08:00", "16:00", "Pha chế", false, "Đã duyệt", "Đăng ký ca"));

        // Other mock data
        shiftList.add(new WorkShift("S5", "NV004", "Trần Thị E", "14/02/2026", "08:00", "16:00", "Phục vụ", true, "Đã duyệt", "Đăng ký ca"));
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
