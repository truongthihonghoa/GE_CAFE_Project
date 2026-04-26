package com.demo.ltud_n10.presentation.ui.employee;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EmployeeViewModel extends ViewModel {

    private final EmployeeRepository repository;
    private final MediatorLiveData<Resource<List<Employee>>> employeesResult = new MediatorLiveData<>();
    private final MutableLiveData<Employee> selectedEmployee = new MutableLiveData<>();

    @Inject
    public EmployeeViewModel(EmployeeRepository repository) {
        this.repository = repository;
        loadEmployees(); // Tải dữ liệu lần đầu
    }

    public void loadEmployees() {
        LiveData<Resource<List<Employee>>> source = repository.getEmployees();
        employeesResult.addSource(source, resource -> {
            employeesResult.setValue(resource);
            if (resource.status != Resource.Status.LOADING) {
                employeesResult.removeSource(source);
            }
        });
    }

    public LiveData<Resource<List<Employee>>> getEmployees() {
        return employeesResult;
    }

    public LiveData<Resource<Employee>> addEmployee(Employee employee) {
        return repository.addEmployee(employee);
    }

    public LiveData<Resource<Employee>> updateEmployee(Employee employee) {
        return repository.updateEmployee(employee);
    }

    public LiveData<Resource<Boolean>> deleteEmployee(String id) {
        return repository.deleteEmployee(id);
    }

    public void selectEmployee(Employee employee) {
        selectedEmployee.setValue(employee);
    }

    public LiveData<Employee> getSelectedEmployee() {
        return selectedEmployee;
    }
}
