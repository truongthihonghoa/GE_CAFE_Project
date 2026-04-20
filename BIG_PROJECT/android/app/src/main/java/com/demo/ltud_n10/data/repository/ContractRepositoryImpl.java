package com.demo.ltud_n10.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Contract;
import com.demo.ltud_n10.domain.repository.ContractRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ContractRepositoryImpl implements ContractRepository {

    private final List<Contract> contractList = new ArrayList<>();

    @Inject
    public ContractRepositoryImpl() {
        // Dữ liệu mẫu khớp với tên nhân viên "Lê Văn C" và giao diện yêu cầu
        contractList.add(new Contract("HD2026-001", "NV001", "Lê Văn C", "Full-time", "01/01/2026", "31/12/2026", 8000000, "Nhân viên pha chế", "Đang hiệu lực"));
        contractList.add(new Contract("HD2025-001", "NV001", "Lê Văn C", "Full-time", "01/01/2025", "31/12/2025", 10000000, "Nhân viên phục vụ", "Hết hiệu lực"));
        
        // Các nhân viên khác
        contractList.add(new Contract("HD2024002", "NV002", "Phạm Thị D", "Full time", "01/02/2024", "01/02/2025", 7500000, "Nhân viên phục vụ", "Còn hiệu lực"));
    }

    @Override
    public LiveData<Resource<List<Contract>>> getContracts() {
        MutableLiveData<Resource<List<Contract>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            result.setValue(Resource.success(new ArrayList<>(contractList)));
        }, 600);
        return result;
    }

    @Override
    public LiveData<Resource<Contract>> addContract(Contract contract) {
        MutableLiveData<Resource<Contract>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            contractList.add(0, contract);
            result.setValue(Resource.success(contract));
        }, 600);
        return result;
    }

    @Override
    public LiveData<Resource<Contract>> updateContract(Contract contract) {
        MutableLiveData<Resource<Contract>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (int i = 0; i < contractList.size(); i++) {
                if (contractList.get(i).getId().equals(contract.getId())) {
                    contractList.set(i, contract);
                    result.setValue(Resource.success(contract));
                    return;
                }
            }
            result.setValue(Resource.error("Không tìm thấy hợp đồng", null));
        }, 600);
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteContract(String contractId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            boolean removed = contractList.removeIf(c -> c.getId().equals(contractId));
            if (removed) {
                result.setValue(Resource.success(true));
            } else {
                result.setValue(Resource.error("Xóa thất bại", false));
            }
        }, 600);
        return result;
    }
}
