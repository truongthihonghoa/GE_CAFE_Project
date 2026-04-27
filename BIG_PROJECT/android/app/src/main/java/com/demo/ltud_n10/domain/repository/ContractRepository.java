package com.demo.ltud_n10.domain.repository;

import androidx.lifecycle.LiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Contract;
import java.util.List;

public interface ContractRepository {
    LiveData<Resource<List<Contract>>> getContracts();
    LiveData<Resource<Contract>> addContract(Contract contract);
    LiveData<Resource<Contract>> updateContract(Contract contract);
    LiveData<Resource<Boolean>> deleteContract(String contractId);
}

