package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
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
    private final Map<String, String> employeeNames = new HashMap<>();

    @Inject
    public ContractRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Contract>>> getContracts() {
        MutableLiveData<Resource<List<Contract>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        // Lấy danh sách nhân viên trước để map Tên thật
        apiService.getEmployees().enqueue(new Callback<List<EmployeeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<EmployeeDto>> call, @NonNull Response<List<EmployeeDto>> responseEmp) {
                if (responseEmp.isSuccessful() && responseEmp.body() != null) {
                    for (EmployeeDto emp : responseEmp.body()) {
                        employeeNames.put(emp.getMaNv(), emp.getHoTen());
                    }
                }
                fetchContractsFromApi(result);
            }

            @Override
            public void onFailure(@NonNull Call<List<EmployeeDto>> call, @NonNull Throwable t) {
                fetchContractsFromApi(result);
            }
        });

        return result;
    }

    private void fetchContractsFromApi(MutableLiveData<Resource<List<Contract>>> result) {
        apiService.getContracts().enqueue(new Callback<List<ContractDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ContractDto>> call, @NonNull Response<List<ContractDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Contract> contracts = new ArrayList<>();
                    for (ContractDto dto : response.body()) {
                        contracts.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(contracts));
                } else {
                    result.setValue(Resource.error("Lỗi khi tải hợp đồng: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ContractDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối mạng: " + t.getMessage(), null));
            }
        });
    }

    private Contract mapDtoToDomain(ContractDto dto) {
        Contract contract = new Contract();
        contract.setId(dto.getMaHd());
        contract.setEmployeeId(dto.getMaNv());
        
        // Lấy tên thật từ map (ví dụ: NV00003 -> Trần Quốc Bảo)
        String name = employeeNames.get(dto.getMaNv());
        contract.setEmployeeName(name != null ? name : dto.getMaNv());
        
        contract.setType(dto.getLoaiHd());
        contract.setStartDate(dto.getNgayBatDau());
        contract.setEndDate(dto.getNgayKetThuc());
        
        if (dto.getChiTiet() != null) {
            contract.setSalary(dto.getChiTiet().getLuongCoBan());
        }
        
        contract.setPosition(dto.getChucVu());
        
        // Map trạng thái theo giao diện
        if ("CON_HAN".equals(dto.getTrangThai())) {
            contract.setStatus("Đang hiệu lực");
        } else {
            contract.setStatus("Hết hiệu lực");
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
