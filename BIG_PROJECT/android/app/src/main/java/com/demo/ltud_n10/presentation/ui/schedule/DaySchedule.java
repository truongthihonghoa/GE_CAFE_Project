package com.demo.ltud_n10.presentation.ui.schedule;

import com.demo.ltud_n10.domain.model.WorkShift;
import java.util.List;

public class DaySchedule {
    private String dayName;
    private String dayNumber;
    private List<WorkShift> shifts;

    public DaySchedule(String dayName, String dayNumber, List<WorkShift> shifts) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.shifts = shifts;
    }

    public String getDayName() { return dayName; }
    public String getDayNumber() { return dayNumber; }
    public List<WorkShift> getShifts() { return shifts; }
}
