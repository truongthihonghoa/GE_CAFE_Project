package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ContractApiService;
import com.demo.ltud_n10.data.remote.model.ContractDto;
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

    private final ContractApiService apiService;

    @Inject
    public ContractRepositoryImpl(ContractApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Contract>>> getContracts() {
        MutableLiveData<Resource<List<Contract>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getContracts().enqueue(new Callback<List<ContractDto>>() {
            @Override
            public void onResponse(Call<List<ContractDto>> call, Response<List<ContractDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Contract> contracts = new ArrayList<>();
                    for (ContractDto dto : response.body()) {
                        contracts.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(contracts));
                } else {
                    result.setValue(Resource.error("Lỗi khi tải dữ liệu: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<List<ContractDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    private Contract mapDtoToDomain(ContractDto dto) {
        Contract contract = new Contract();
        contract.setId(dto.getMaHd());
        contract.setEmployeeId(dto.getMaNv());
        contract.setEmployeeName(dto.getMaNv()); 
        contract.setType(dto.getLoaiHd());
        contract.setStartDate(dto.getNgayBatDau());
        contract.setEndDate(dto.getNgayKetThuc());
        if (dto.getChiTiet() != null) {
            contract.setSalary(dto.getChiTiet().getLuongCoBan());
        }
        contract.setPosition(dto.getChucVu());
        
        String status = dto.getTrangThai();
        if ("CON_HAN".equals(status)) {
            contract.setStatus("Còn hiệu lực");
        } else if ("HET_HAN".equals(status)) {
            contract.setStatus("Hết hạn");
        } else {
            contract.setStatus(status);
        }
        
        return contract;
    }

    @Override
    public LiveData<Resource<Contract>> addContract(Contract contract) {
        return new MutableLiveData<>(Resource.success(contract));
    }

    @Override
    public LiveData<Resource<Contract>> updateContract(Contract contract) {
        return new MutableLiveData<>(Resource.success(contract));
    }

    @Override
    public LiveData<Resource<Boolean>> deleteContract(String contractId) {
        return new MutableLiveData<>(Resource.success(true));
    }
}
