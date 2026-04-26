package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.model.ContractDetailDto;
import com.demo.ltud_n10.data.remote.model.ContractDto;
import com.demo.ltud_n10.data.remote.model.EmployeeDto;
import com.demo.ltud_n10.domain.model.Contract;
import com.demo.ltud_n10.domain.repository.ContractRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ContractRepositoryImpl implements ContractRepository {

    private final ApiService apiService;
    private final Map<String, ContractDto> originalDtoCache = new HashMap<>();

    @Inject
    public ContractRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Contract>>> getContracts() {
        MutableLiveData<Resource<List<Contract>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getEmployees().enqueue(new Callback<List<EmployeeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<EmployeeDto>> callE, @NonNull Response<List<EmployeeDto>> resE) {
                final List<EmployeeDto> employeeList = resE.body();

                apiService.getContracts().enqueue(new Callback<List<ContractDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ContractDto>> call,
                            @NonNull Response<List<ContractDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Contract> contracts = new ArrayList<>();
                            originalDtoCache.clear();
                            for (ContractDto dto : response.body()) {
                                originalDtoCache.put(dto.getMaHd(), dto);
                                Contract contract = mapDtoToDomain(dto);
                                if (employeeList != null) {
                                    for (EmployeeDto emp : employeeList) {
                                        if (emp.getMaNv() != null && emp.getMaNv().equals(dto.getMaNv())) {
                                            contract.setEmployeeName(emp.getHoTen());
                                            break;
                                        }
                                    }
                                }
                                contracts.add(contract);
                            }
                            result.setValue(Resource.success(contracts));
                        } else {
                            result.setValue(Resource.error("Lỗi tải hợp đồng: " + response.code(), null));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ContractDto>> call, @NonNull Throwable t) {
                        result.setValue(Resource.error(t.getMessage(), null));
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<EmployeeDto>> callE, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối nhân viên", null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Contract>> addContract(Contract contract) {
        MutableLiveData<Resource<Contract>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        
        // Chuyển sang DTO
        ContractDto dto = mapDomainToDto(contract);

        apiService.addContract(dto).enqueue(new Callback<ContractDto>() {
            @Override
            public void onResponse(@NonNull Call<ContractDto> call, @NonNull Response<ContractDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Contract res = mapDtoToDomain(response.body());
                    res.setEmployeeName(contract.getEmployeeName());
                    result.setValue(Resource.success(res));
                } else {
                    result.setValue(Resource.error(parseError(response), null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContractDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Contract>> updateContract(Contract contract) {
        MutableLiveData<Resource<Contract>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        ContractDto updateDto = originalDtoCache.get(contract.getId());

        if (updateDto != null) {
            updateDto.setLoaiHd(contract.getType());
            updateDto.setChucVu(contract.getPosition());
            updateDto.setNgayBatDau(contract.getStartDate());
            updateDto.setNgayKetThuc(contract.getEndDate());
            updateDto.setTrangThai(contract.getStatus());

            if (updateDto.getChiTiet() == null) {
                updateDto.setChiTiet(new ContractDetailDto());
            }
            updateDto.getChiTiet().setLuongCoBan(contract.getSalary());
            updateDto.getChiTiet().setLuongTheoGio(contract.getHourlyRate());
            updateDto.getChiTiet().setSoGioLam(contract.getRequiredHours());
            updateDto.getChiTiet().setMaHd(contract.getId());
        } else {
            updateDto = mapDomainToDto(contract);
        }

        apiService.updateContract(contract.getId(), updateDto).enqueue(new Callback<ContractDto>() {
            @Override
            public void onResponse(@NonNull Call<ContractDto> call, @NonNull Response<ContractDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Contract res = mapDtoToDomain(response.body());
                    res.setEmployeeName(contract.getEmployeeName());
                    result.setValue(Resource.success(res));
                } else {
                    result.setValue(Resource.error(parseError(response), null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContractDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                return "Lỗi " + response.code() + ": " + response.errorBody().string();
            }
        } catch (Exception ignored) {
        }
        return "Lỗi hệ thống (" + response.code() + ")";
    }

    @Override
    public LiveData<Resource<Boolean>> deleteContract(String contractId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        apiService.deleteContract(contractId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful())
                    result.setValue(Resource.success(true));
                else
                    result.setValue(Resource.error("Lỗi khi xóa", false));
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
            }
        });
        return result;
    }

    private Contract mapDtoToDomain(ContractDto dto) {
        Contract contract = new Contract();
        contract.setId(dto.getMaHd() != null ? dto.getMaHd().trim() : null);
        contract.setEmployeeId(dto.getMaNv() != null ? dto.getMaNv().trim() : null);
        contract.setEmployeeName(contract.getEmployeeId());
        contract.setType(dto.getLoaiHd());
        contract.setStartDate(dto.getNgayBatDau());
        contract.setEndDate(dto.getNgayKetThuc());
        ContractDetailDto detail = dto.getChiTiet();
        if (detail != null) {
            contract.setSalary(detail.getLuongCoBan());
            contract.setHourlyRate(detail.getLuongTheoGio());
            contract.setRequiredHours(detail.getSoGioLam());
        }
        contract.setPosition(dto.getChucVu());
        contract.setStatus(dto.getTrangThai());
        contract.setBranchId(dto.getMaChiNhanh() != null ? dto.getMaChiNhanh().trim() : null);
        return contract;
    }

    private ContractDto mapDomainToDto(Contract contract) {
        ContractDto dto = new ContractDto();
        dto.setMaHd(contract.getId() != null ? contract.getId().trim() : null);
        dto.setMaNv(contract.getEmployeeId() != null ? contract.getEmployeeId().trim() : null);
        dto.setLoaiHd(contract.getType());
        dto.setChucVu(contract.getPosition());
        dto.setNgayBatDau(contract.getStartDate());
        dto.setNgayKetThuc(contract.getEndDate());
        dto.setTrangThai(contract.getStatus());
        
        // Tránh lỗi FK khi mã chi nhánh rỗng hoặc chỉ có khoảng trắng
        String bId = contract.getBranchId();
        dto.setMaChiNhanh((bId == null || bId.trim().isEmpty()) ? null : bId.trim());

        ContractDetailDto detail = new ContractDetailDto();
        detail.setMaHd(dto.getMaHd());
        detail.setLuongCoBan(contract.getSalary());
        detail.setLuongTheoGio(contract.getHourlyRate());
        detail.setSoGioLam(contract.getRequiredHours());
        dto.setChiTiet(detail);

        return dto;
    }
}
