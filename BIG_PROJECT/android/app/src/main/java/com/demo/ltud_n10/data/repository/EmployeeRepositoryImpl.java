package com.demo.ltud_n10.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;
import com.demo.ltud_n10.data.remote.dto.BranchDto;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final com.demo.ltud_n10.data.remote.ApiService apiService;
    private final java.util.List<Employee> employeeList = new ArrayList<>();

    @Inject
    public EmployeeRepositoryImpl(com.demo.ltud_n10.data.remote.ApiService apiService) {
        this.apiService = apiService;
    }

    private Employee mapToDomain(com.demo.ltud_n10.data.remote.dto.EmployeeDto dto) {
        Employee employee = new Employee(
                dto.getMaNv(),
                dto.getHoTen(),
                "", // Email not in backend NhanVien model yet
                dto.getSdt(),
                dto.getCccd(),
                dto.getGioiTinh(),
                dto.getNgaySinh(),
                dto.getDiaChi(),
                dto.getChucVu(),
                "Đang làm" // Default status
        );
        employee.setBranchId(dto.getMaChiNhanh());
        return employee;
    }

    private String formatToBackendDate(String dateStr) {
        try {
            // Chuyển từ DD/MM/YYYY hoặc D/M/YYYY sang YYYY-MM-DD
            String[] parts = dateStr.split("/");
            if (parts.length == 3) {
                String day = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
                String month = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                String year = parts[2];
                return year + "-" + month + "-" + day;
            }
        } catch (Exception e) {
            return dateStr;
        }
        return dateStr;
    }

    private com.demo.ltud_n10.data.remote.dto.EmployeeDto mapToDto(Employee domain) {
        com.demo.ltud_n10.data.remote.dto.EmployeeDto dto = new com.demo.ltud_n10.data.remote.dto.EmployeeDto();
        dto.setMaNv(domain.getId());
        dto.setHoTen(domain.getName());
        dto.setSdt(domain.getPhone());
        dto.setCccd(domain.getCccd());
        dto.setGioiTinh(domain.getGender());
        dto.setNgaySinh(formatToBackendDate(domain.getDob()));
        dto.setDiaChi(domain.getAddress());
        dto.setChucVu(domain.getPosition());
        dto.setMaChiNhanh(domain.getBranchId()); 
        
        return dto;
    }

    @Override
    public LiveData<Resource<List<Employee>>> getEmployees() {
        MutableLiveData<Resource<List<Employee>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        apiService.getEmployees().enqueue(new retrofit2.Callback<List<com.demo.ltud_n10.data.remote.dto.EmployeeDto>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.demo.ltud_n10.data.remote.dto.EmployeeDto>> call, retrofit2.Response<List<com.demo.ltud_n10.data.remote.dto.EmployeeDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Employee> employees = new ArrayList<>();
                    for (com.demo.ltud_n10.data.remote.dto.EmployeeDto dto : response.body()) {
                        employees.add(mapToDomain(dto));
                    }
                    result.setValue(Resource.success(employees));
                } else {
                    result.setValue(Resource.error("Lỗi lấy danh sách: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.demo.ltud_n10.data.remote.dto.EmployeeDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<List<com.demo.ltud_n10.data.remote.dto.BranchDto>>> getBranches() {
        MutableLiveData<Resource<List<com.demo.ltud_n10.data.remote.dto.BranchDto>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        apiService.getBranches().enqueue(new retrofit2.Callback<List<com.demo.ltud_n10.data.remote.dto.BranchDto>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.demo.ltud_n10.data.remote.dto.BranchDto>> call, retrofit2.Response<List<com.demo.ltud_n10.data.remote.dto.BranchDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Lỗi lấy chi nhánh: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.demo.ltud_n10.data.remote.dto.BranchDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Employee>> addEmployee(Employee employee) {
        MutableLiveData<Resource<Employee>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        apiService.addEmployee(mapToDto(employee)).enqueue(new retrofit2.Callback<com.demo.ltud_n10.data.remote.dto.EmployeeDto>() {
            @Override
            public void onResponse(retrofit2.Call<com.demo.ltud_n10.data.remote.dto.EmployeeDto> call, retrofit2.Response<com.demo.ltud_n10.data.remote.dto.EmployeeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi thêm nhân viên: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.demo.ltud_n10.data.remote.dto.EmployeeDto> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Employee>> updateEmployee(Employee employee) {
        MutableLiveData<Resource<Employee>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        apiService.updateEmployee(employee.getId(), mapToDto(employee)).enqueue(new retrofit2.Callback<com.demo.ltud_n10.data.remote.dto.EmployeeDto>() {
            @Override
            public void onResponse(retrofit2.Call<com.demo.ltud_n10.data.remote.dto.EmployeeDto> call, retrofit2.Response<com.demo.ltud_n10.data.remote.dto.EmployeeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi cập nhật: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.demo.ltud_n10.data.remote.dto.EmployeeDto> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteEmployee(String employeeId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        apiService.deleteEmployee(employeeId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Lỗi xóa: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
        return result;
    }
}
