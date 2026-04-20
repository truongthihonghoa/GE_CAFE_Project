package com.demo.ltud_n10.domain.repository;

import androidx.lifecycle.LiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.WorkShift;
import java.util.List;

public interface WorkShiftRepository {
    LiveData<Resource<List<WorkShift>>> getWorkShifts();
    LiveData<Resource<WorkShift>> addWorkShift(WorkShift shift);
    LiveData<Resource<WorkShift>> updateWorkShift(WorkShift shift);
    LiveData<Resource<Boolean>> deleteWorkShift(String shiftId);
    LiveData<Resource<Boolean>> sendNotifications(List<String> shiftIds);
}
