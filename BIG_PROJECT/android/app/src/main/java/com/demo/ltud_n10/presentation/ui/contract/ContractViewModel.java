package com.demo.ltud_n10.presentation.ui.contract;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Contract;
import com.demo.ltud_n10.domain.repository.ContractRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ContractViewModel extends ViewModel {

    private final ContractRepository repository;

    @Inject
    public ContractViewModel(ContractRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<Contract>>> getContracts() {
        return repository.getContracts();
    }

    public LiveData<Resource<Contract>> addContract(Contract contract) {
        return repository.addContract(contract);
    }

    public LiveData<Resource<Contract>> updateContract(Contract contract) {
        return repository.updateContract(contract);
    }

    public LiveData<Resource<Boolean>> deleteContract(String id) {
        return repository.deleteContract(id);
    }
}
