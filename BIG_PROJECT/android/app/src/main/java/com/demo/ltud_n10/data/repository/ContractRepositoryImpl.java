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
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ContractRepositoryImpl implements ContractRepository {

    private final ApiService apiService;

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
            public void onResponse(@NonNull Call<List<EmployeeDto>> callEmp, @NonNull Response<List<EmployeeDto>> resEmp) {
                List<EmployeeDto> employeeDtos = resEmp.body();
                
                apiService.getContracts().enqueue(new Callback<List<ContractDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ContractDto>> call, @NonNull Response<List<ContractDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Contract> contracts = new ArrayList<>();
                            for (ContractDto dto : response.body()) {
                                Contract contract = mapDtoToDomain(dto);
                                if (employeeDtos != null) {
                                    for (EmployeeDto emp : employeeDtos) {
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
                            result.setValue(Resource.error("Lỗi khi tải hợp đồng: " + response.code(), null));
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<ContractDto>> call, @NonNull Throwable t) {
                        result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call<List<EmployeeDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi tải danh sách nhân viên", null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Contract>> addContract(Contract contract) {
        MutableLiveData<Resource<Contract>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        
        ContractDto dto = mapDomainToDto(contract);
        // Khi THÊM MỚI: Luôn cần gửi ma_hd trong chi_tiet
        if (dto.getChiTiet() != null) {
            dto.getChiTiet().setMaHd(contract.getId());
        }

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
        
        ContractDto dto = mapDomainToDto(contract);
        // QUAN TRỌNG: Khi CHỈNH SỬA, không gửi ma_hd bên trong chi_tiet để tránh lỗi "already exists"
        if (dto.getChiTiet() != null) {
            dto.getChiTiet().setMaHd(null);
        }

        apiService.updateContract(contract.getId(), dto).enqueue(new Callback<ContractDto>() {
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
        String msg = "Lỗi " + response.code();
        try {
            if (response.errorBody() != null) {
                msg += ": " + response.errorBody().string();
            }
        } catch (Exception ignored) {}
        return msg;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteContract(String contractId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        apiService.deleteContract(contractId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) result.setValue(Resource.success(true));
                else result.setValue(Resource.error("Lỗi khi xóa", false));
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
        contract.setId(dto.getMaHd());
        contract.setEmployeeId(dto.getMaNv());
        contract.setEmployeeName(dto.getMaNv());
        contract.setType(mapTypeToDisplay(dto.getLoaiHd()));
        contract.setStartDate(dto.getNgayBatDau());
        contract.setEndDate(dto.getNgayKetThuc());
        if (dto.getChiTiet() != null) {
            contract.setSalary(dto.getChiTiet().getLuongCoBan());
        }
        contract.setPosition(mapPositionToDisplay(dto.getChucVu()));
        contract.setStatus(mapStatusToDisplay(dto.getTrangThai()));
        return contract;
    }

    private ContractDto mapDomainToDto(Contract contract) {
        ContractDto dto = new ContractDto();
        dto.setMaHd(contract.getId());
        dto.setMaNv(contract.getEmployeeId());
        dto.setLoaiHd(mapTypeToApi(contract.getType()));
        dto.setChucVu(mapPositionToApi(contract.getPosition()));
        dto.setNgayBatDau(contract.getStartDate());
        dto.setNgayKetThuc(contract.getEndDate());
        dto.setTrangThai(mapStatusToApi(contract.getStatus()));
        
        ContractDetailDto detail = new ContractDetailDto();
        detail.setLuongCoBan(contract.getSalary());
        dto.setChiTiet(detail);
        
        return dto;
    }

    private String mapTypeToApi(String displayType) {
        if (displayType == null) return "BAN_THOI_GIAN";
        if (displayType.equalsIgnoreCase("Full time")) return "TOAN_THOI_GIAN";
        return "BAN_THOI_GIAN";
    }

    private String mapTypeToDisplay(String apiType) {
        if (apiType == null) return "Part time";
        if (apiType.equalsIgnoreCase("TOAN_THOI_GIAN")) return "Full time";
        return "Part time";
    }

    private String mapPositionToApi(String displayPos) {
        if (displayPos == null) return "PHUC_VU";
        if (displayPos.contains("Quản lý")) return "QUAN_LY";
        if (displayPos.contains("Pha chế")) return "PHA_CHE";
        return "PHUC_VU";
    }

    private String mapPositionToDisplay(String apiPos) {
        if (apiPos == null) return "Phục vụ";
        if (apiPos.equalsIgnoreCase("QUAN_LY")) return "Quản lý";
        if (apiPos.equalsIgnoreCase("PHA_CHE")) return "Pha chế";
        return "Phục vụ";
    }

    private String mapStatusToApi(String displayStatus) {
        if (displayStatus == null) return "CON_HAN";
        if (displayStatus.contains("Còn hiệu lực")) return "CON_HAN";
        if (displayStatus.contains("Hết hạn")) return "HET_HAN";
        return "CON_HAN";
    }

    private String mapStatusToDisplay(String apiStatus) {
        if (apiStatus == null) return "Còn hiệu lực";
        if (apiStatus.equalsIgnoreCase("CON_HAN")) return "Còn hiệu lực";
        if (apiStatus.equalsIgnoreCase("HET_HAN")) return "Hết hạn";
        return apiStatus;
    }
}
