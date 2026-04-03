package com.demo.ltud_n10.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final List<Employee> employeeList = new ArrayList<>();

    @Inject
    public EmployeeRepositoryImpl() {
        // Mock initial data
        employeeList.add(new Employee("1", "Lê Văn C", "nhanvien@coffee.com", "0923456789", "123456789012", "Nam", "15/01/2000", "TP. Hồ Chí Minh", "Phục vụ", "Đang làm"));
        employeeList.add(new Employee("2", "Phạm Thị D", "pham.d@coffee.com", "0923456789", "123456789012", "Nữ", "20/05/1998", "Hà Nội", "Pha chế", "Đang làm"));
    }

    @Override
    public LiveData<Resource<List<Employee>>> getEmployees() {
        MutableLiveData<Resource<List<Employee>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            result.setValue(Resource.success(new ArrayList<>(employeeList)));
        }, 1000);
        return result;
    }

    @Override
    public LiveData<Resource<Employee>> addEmployee(Employee employee) {
        MutableLiveData<Resource<Employee>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            employee.setId(String.valueOf(employeeList.size() + 1));
            employeeList.add(employee);
            result.setValue(Resource.success(employee));
        }, 1000);
        return result;
    }

    @Override
    public LiveData<Resource<Employee>> updateEmployee(Employee employee) {
        MutableLiveData<Resource<Employee>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (int i = 0; i < employeeList.size(); i++) {
                if (employeeList.get(i).getId().equals(employee.getId())) {
                    employeeList.set(i, employee);
                    result.setValue(Resource.success(employee));
                    return;
                }
            }
            result.setValue(Resource.error("Không tìm thấy nhân viên", null));
        }, 1000);
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteEmployee(String employeeId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            employeeList.removeIf(e -> e.getId().equals(employeeId));
            result.setValue(Resource.success(true));
        }, 1000);
        return result;
    }
}
