package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.model.ContractDetailDto;
import com.demo.ltud_n10.data.remote.model.ContractDto;
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

        apiService.getContracts(null).enqueue(new Callback<List<ContractDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ContractDto>> call, @NonNull Response<List<ContractDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Contract> contracts = new ArrayList<>();
                    originalDtoCache.clear();
                    for (ContractDto dto : response.body()) {
                        originalDtoCache.put(dto.getMaHd(), dto);
                        contracts.add(mapDtoToDomain(dto));
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

        return result;
    }

    @Override
    public LiveData<Resource<Contract>> addContract(Contract contract) {
        MutableLiveData<Resource<Contract>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        ContractDto dto = mapDomainToDto(contract);

        apiService.addContract(dto).enqueue(new Callback<ContractDto>() {
            @Override
            public void onResponse(@NonNull Call<ContractDto> call, @NonNull Response<ContractDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
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

        ContractDto updateDto = mapDomainToDto(contract);

        apiService.updateContract(contract.getId(), updateDto).enqueue(new Callback<ContractDto>() {
            @Override
            public void onResponse(@NonNull Call<ContractDto> call, @NonNull Response<ContractDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
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
        contract.setId(dto.getMaHd());
        contract.setEmployeeId(dto.getMaNv());
        contract.setEmployeeName(dto.getTenNv());
        contract.setType(dto.getLoaiHd());
        contract.setStartDate(dto.getNgayBatDau());
        contract.setEndDate(dto.getNgayKetThuc());
        contract.setSalary(dto.getLuongCoBan());
        contract.setHourlyRate(dto.getLuongTheoGio());
        contract.setRequiredHours(dto.getSoGioLam());
        contract.setPosition(dto.getChucVu());
        
        // Chuyển đổi trạng thái từ mã sang tiếng Việt
        String status = dto.getTrangThai();
        if ("CON_HAN".equals(status)) {
            contract.setStatus("Còn hiệu lực");
        } else if ("HET_HAN".equals(status)) {
            contract.setStatus("Hết hiệu lực");
        } else {
            contract.setStatus(status);
        }
        
        contract.setBranchId(dto.getMaChiNhanh());
        return contract;
    }

    private ContractDto mapDomainToDto(Contract contract) {
        ContractDto dto = new ContractDto();
        dto.setMaHd(contract.getId());
        dto.setMaNv(contract.getEmployeeId());
        dto.setTenNv(contract.getEmployeeName());
        dto.setLoaiHd(contract.getType());
        dto.setChucVu(contract.getPosition());
        dto.setNgayBatDau(contract.getStartDate());
        dto.setNgayKetThuc(contract.getEndDate());
        
        // Chuyển ngược lại từ tiếng Việt sang mã khi gửi lên server
        String status = contract.getStatus();
        if ("Còn hiệu lực".equals(status)) {
            dto.setTrangThai("CON_HAN");
        } else if ("Hết hiệu lực".equals(status)) {
            dto.setTrangThai("HET_HAN");
        } else {
            dto.setTrangThai(status);
        }

        dto.setMaChiNhanh(contract.getBranchId());
        dto.setLuongCoBan(contract.getSalary());
        dto.setLuongTheoGio(contract.getHourlyRate());
        dto.setSoGioLam(contract.getRequiredHours());

        ContractDetailDto detail = new ContractDetailDto();
        detail.setMaHd(dto.getMaHd());
        detail.setLuongCoBan(contract.getSalary());
        detail.setLuongTheoGio(contract.getHourlyRate());
        detail.setSoGioLam(contract.getRequiredHours());
        dto.setChiTiet(detail);

        return dto;
    }
}
