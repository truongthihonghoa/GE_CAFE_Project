package com.demo.ltud_n10.domain.repository;

import androidx.lifecycle.LiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Employee;
import java.util.List;

public interface EmployeeRepository {
    LiveData<Resource<List<Employee>>> getEmployees();
    LiveData<Resource<List<Employee>>> getStaffEmployees(); // Phương thức mới
    LiveData<Resource<Employee>> addEmployee(Employee employee);
    LiveData<Resource<Employee>> updateEmployee(Employee employee);
    LiveData<Resource<Boolean>> deleteEmployee(String employeeId);
}
