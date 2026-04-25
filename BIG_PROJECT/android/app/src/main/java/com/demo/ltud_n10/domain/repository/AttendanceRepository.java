package com.demo.ltud_n10.domain.repository;

import androidx.lifecycle.LiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Attendance;
import java.util.List;

public interface AttendanceRepository {
    LiveData<Resource<List<Attendance>>> getAttendances();
}
