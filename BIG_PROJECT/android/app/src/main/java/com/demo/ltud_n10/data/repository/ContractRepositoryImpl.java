package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.local.SharedPrefsManager;
import com.demo.ltud_n10.data.remote.ApiService;
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

    private final ApiService apiService;
    private final SharedPrefsManager prefsManager;

    @Inject
    public ContractRepositoryImpl(ApiService apiService, SharedPrefsManager prefsManager) {
        this.apiService = apiService;
        this.prefsManager = prefsManager;
    }

    @Override
    public LiveData<Resource<List<Contract>>> getContracts() {
        MutableLiveData<Resource<List<Contract>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        String maNv = prefsManager.getMaNv();
        // LOGIC PHÂN QUYỀN CHUẨN: 
        // - Nhân viên: Chỉ load của chính mình (maNv)
        // - Quản lý: Load tất cả (null)
        String filterMaNv = prefsManager.isStaff() ? null : maNv;

        apiService.getContracts(filterMaNv).enqueue(new Callback<List<ContractDto>>() {
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

        return result;
    }

    private Contract mapDtoToDomain(ContractDto dto) {
        Contract contract = new Contract();
        contract.setId(dto.getMaHd());
        contract.setEmployeeId(dto.getMaNv());
        
        // Hiển thị tên nhân viên (Ưu tiên lấy tên thật nếu có, không thì lấy mã NV)
        String empName = (dto.getMaNv() != null) ? dto.getMaNv() : "Nhân viên";
        contract.setEmployeeName(empName);
        
        contract.setType(dto.getLoaiHd());
        contract.setStartDate(dto.getNgayBatDau());
        contract.setEndDate(dto.getNgayKetThuc());
        
        if (dto.getChiTiet() != null) {
            contract.setSalary(dto.getChiTiet().getLuongCoBan());
        }

        contract.setPosition(dto.getChucVu());
        contract.setStatus("CON_HAN".equals(dto.getTrangThai()) ? "Còn hiệu lực" : "Hết hạn");
        return contract;
    }

    @Override public LiveData<Resource<Contract>> addContract(Contract contract) { return new MutableLiveData<>(Resource.success(contract)); }
    @Override public LiveData<Resource<Contract>> updateContract(Contract contract) { return new MutableLiveData<>(Resource.success(contract)); }
    @Override public LiveData<Resource<Boolean>> deleteContract(String contractId) { return new MutableLiveData<>(Resource.success(true)); }
}
