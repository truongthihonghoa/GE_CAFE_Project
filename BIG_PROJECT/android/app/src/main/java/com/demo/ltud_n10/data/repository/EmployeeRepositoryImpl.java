package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
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

    private final ApiService apiService;

    @Inject
    public EmployeeRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Employee>>> getEmployees() {
        MutableLiveData<Resource<List<Employee>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getEmployees().enqueue(new Callback<List<EmployeeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<EmployeeDto>> call, @NonNull Response<List<EmployeeDto>> response) {
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
            public void onFailure(@NonNull Call<List<EmployeeDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<List<Employee>>> getStaffEmployees() {
        MutableLiveData<Resource<List<Employee>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getEmployees().enqueue(new Callback<List<EmployeeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<EmployeeDto>> call, @NonNull Response<List<EmployeeDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Employee> staffOnly = new ArrayList<>();
                    for (EmployeeDto dto : response.body()) {
                        if (dto.getIsStaff() != null && dto.getIsStaff() == 1) {
                            staffOnly.add(mapDtoToDomain(dto));
                        }
                    }
                    result.setValue(Resource.success(staffOnly));
                } else {
                    result.setValue(Resource.error("Lỗi lấy danh sách quản lý", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<EmployeeDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
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
            public void onResponse(@NonNull Call<EmployeeDto> call, @NonNull Response<EmployeeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    String errorMsg = "Lỗi khi thêm: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    result.setValue(Resource.error(errorMsg, null));
                }
            }
            @Override
            public void onFailure(@NonNull Call<EmployeeDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
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
            public void onResponse(@NonNull Call<EmployeeDto> call, @NonNull Response<EmployeeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    String errorMsg = "Lỗi khi cập nhật: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    result.setValue(Resource.error(errorMsg, null));
                }
            }
            @Override
            public void onFailure(@NonNull Call<EmployeeDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
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
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Lỗi khi xóa: " + response.code(), false));
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
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
        employee.setStatus(dto.getTrangThai());
        employee.setBankAccount(dto.getTkNganHang());
        employee.setBranchId(dto.getMaChiNhanh());
        return employee;
    }

    private EmployeeDto mapDomainToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setMaNv(employee.getId());
        dto.setHoTen(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setSoDienThoai(employee.getPhone());
        dto.setSdt(employee.getPhone()); // Mapping both as API requires 'sdt'
        dto.setCccd(employee.getCccd());
        dto.setGioiTinh(employee.getGender());
        dto.setNgaySinh(employee.getDob());
        dto.setDiaChi(employee.getAddress());
        dto.setChucVu(employee.getPosition());
        dto.setTrangThai(employee.getStatus());
        dto.setTkNganHang(employee.getBankAccount());
        dto.setMaChiNhanh(employee.getBranchId());
        return dto;
    }
}
