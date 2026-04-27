package com.demo.ltud_n10.presentation.ui.schedule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkShiftViewModel extends ViewModel {

    private final WorkShiftRepository repository;
    private final EmployeeRepository employeeRepository;
    private final MutableLiveData<List<String>> selectedShiftIds = new MutableLiveData<>(new ArrayList<>());

    @Inject
    public WorkShiftViewModel(WorkShiftRepository repository, EmployeeRepository employeeRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
    }

    public LiveData<Resource<List<Employee>>> getEmployees() {
        return employeeRepository.getEmployees();
    }

    public LiveData<Resource<List<WorkShift>>> getWorkShifts() {
        return repository.getWorkShifts();
    }

    public LiveData<Resource<WorkShift>> addWorkShift(WorkShift shift) {
        return repository.addWorkShift(shift);
    }

    public LiveData<Resource<WorkShift>> updateWorkShift(WorkShift shift) {
        return repository.updateWorkShift(shift);
    }

    public LiveData<Resource<Boolean>> deleteWorkShift(String id) {
        return repository.deleteWorkShift(id);
    }

    public LiveData<Resource<Boolean>> sendNotifications() {
        List<String> ids = selectedShiftIds.getValue();
        if (ids == null || ids.isEmpty()) {
            MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
            result.setValue(Resource.error("Vui lòng chọn ca làm để gửi thông báo", null));
            return result;
        }
        return repository.sendNotifications(ids);
    }

    public void toggleShiftSelection(String id) {
        List<String> current = selectedShiftIds.getValue();
        if (current == null) current = new ArrayList<>();
        
        if (current.contains(id)) {
            current.remove(id);
        } else {
            current.add(id);
        }
        selectedShiftIds.setValue(current);
    }

    public LiveData<List<String>> getSelectedShiftIds() {
        return selectedShiftIds;
    }
    
    public void clearSelections() {
        selectedShiftIds.setValue(new ArrayList<>());
    }
}
