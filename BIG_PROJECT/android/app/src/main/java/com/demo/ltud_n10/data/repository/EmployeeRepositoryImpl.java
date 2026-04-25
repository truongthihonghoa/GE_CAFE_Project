package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.EmployeeApiService;
import com.demo.ltud_n10.data.remote.model.EmployeeDto;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final EmployeeApiService apiService;

    @Inject
    public EmployeeRepositoryImpl(EmployeeApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Employee>>> getEmployees() {
        MutableLiveData<Resource<List<Employee>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getEmployees().enqueue(new Callback<List<EmployeeDto>>() {
            @Override
            public void onResponse(Call<List<EmployeeDto>> call, Response<List<EmployeeDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Employee> employees = new ArrayList<>();
                    for (EmployeeDto dto : response.body()) {
                        employees.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(employees));
                } else {
                    result.setValue(Resource.error("Lỗi khi tải dữ liệu: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<List<EmployeeDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Employee>> addEmployee(Employee employee) {
        MutableLiveData<Resource<Employee>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.addEmployee(mapDomainToDto(employee)).enqueue(new Callback<EmployeeDto>() {
            @Override
            public void onResponse(Call<EmployeeDto> call, Response<EmployeeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi khi thêm nhân viên: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<EmployeeDto> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Employee>> updateEmployee(Employee employee) {
        MutableLiveData<Resource<Employee>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.updateEmployee(employee.getId(), mapDomainToDto(employee)).enqueue(new Callback<EmployeeDto>() {
            @Override
            public void onResponse(Call<EmployeeDto> call, Response<EmployeeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi khi cập nhật nhân viên: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<EmployeeDto> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteEmployee(String employeeId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.deleteEmployee(employeeId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Lỗi khi xóa nhân viên: " + response.code(), false));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), false));
            }
        });

        return result;
    }

    private Employee mapDtoToDomain(EmployeeDto dto) {
        Employee employee = new Employee();
        employee.setId(dto.getMaNv());
        employee.setName(dto.getHoTen());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getSoDienThoai());
        employee.setCccd(dto.getCccd());
        employee.setGender(dto.getGioiTinh());
        employee.setDob(dto.getNgaySinh());
        employee.setAddress(dto.getDiaChi());
        employee.setPosition(dto.getChucVu());
        
        String status = dto.getTrangThai();
        if ("DANG_LAM".equals(status)) {
            employee.setStatus("Đang làm");
        } else if ("NGUNG_HOAT_DONG".equals(status)) {
            employee.setStatus("Ngừng hoạt động");
        } else {
            employee.setStatus(status);
        }
        
        return employee;
    }

    private EmployeeDto mapDomainToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setMaNv(employee.getId());
        dto.setHoTen(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setSoDienThoai(employee.getPhone());
        dto.setCccd(employee.getCccd());
        dto.setGioiTinh(employee.getGender());
        dto.setNgaySinh(employee.getDob());
        dto.setDiaChi(employee.getAddress());
        dto.setChucVu(employee.getPosition());
        
        String status = employee.getStatus();
        if ("Đang làm".equals(status)) {
            dto.setTrangThai("DANG_LAM");
        } else if ("Ngừng hoạt động".equals(status)) {
            dto.setTrangThai("NGUNG_HOAT_DONG");
        } else {
            dto.setTrangThai(status);
        }
        
        return dto;
    }
}
