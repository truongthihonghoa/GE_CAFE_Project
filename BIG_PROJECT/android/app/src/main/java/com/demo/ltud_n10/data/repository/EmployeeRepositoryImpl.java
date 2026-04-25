package com.demo.ltud_n10.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.EmployeeApiService;
import com.demo.ltud_n10.data.remote.model.EmployeeDto;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private static final String TAG = "EmployeeRepo";
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
                    result.setValue(Resource.error("Lỗi tải: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<List<EmployeeDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối", null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Employee>> addEmployee(Employee employee) {
        MutableLiveData<Resource<Employee>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        EmployeeDto dto = mapDomainToDto(employee);
        apiService.addEmployee(dto).enqueue(new Callback<EmployeeDto>() {
            @Override
            public void onResponse(Call<EmployeeDto> call, Response<EmployeeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    String errorMsg = "Lỗi " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (IOException e) { e.printStackTrace(); }
                    Log.e(TAG, "Add failed: " + errorMsg);
                    result.setValue(Resource.error("Thêm thất bại. Có thể mã NV bị trùng hoặc thiếu thông tin.", null));
                }
            }

            @Override
            public void onFailure(Call<EmployeeDto> call, Throwable t) {
                result.setValue(Resource.error("Lỗi mạng", null));
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
                    result.setValue(Resource.error("Cập nhật thất bại. Vui lòng kiểm tra lại thông tin.", null));
                }
            }

            @Override
            public void onFailure(Call<EmployeeDto> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối", null));
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
                    result.setValue(Resource.error("Không thể xóa. NV có thể đang có hợp đồng hoặc lịch làm việc.", false));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối", false));
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
        dto.setTrangThai(employee.getStatus());
        return dto;
    }
}
