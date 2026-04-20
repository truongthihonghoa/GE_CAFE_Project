package com.demo.ltud_n10.di;

import com.demo.ltud_n10.data.repository.AuthRepositoryImpl;
import com.demo.ltud_n10.data.repository.BranchRepositoryImpl;
import com.demo.ltud_n10.data.repository.ContractRepositoryImpl;
import com.demo.ltud_n10.data.repository.EmployeeRepositoryImpl;
import com.demo.ltud_n10.data.repository.PayrollRepositoryImpl;
import com.demo.ltud_n10.data.repository.UserRepositoryImpl;
import com.demo.ltud_n10.data.repository.WorkShiftRepositoryImpl;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.BranchRepository;
import com.demo.ltud_n10.domain.repository.ContractRepository;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;
import com.demo.ltud_n10.domain.repository.PayrollRepository;
import com.demo.ltud_n10.domain.repository.UserRepository;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    @Singleton
    public abstract AuthRepository bindAuthRepository(AuthRepositoryImpl authRepositoryImpl);

    @Binds
    @Singleton
    public abstract EmployeeRepository bindEmployeeRepository(EmployeeRepositoryImpl employeeRepositoryImpl);

    @Binds
    @Singleton
    public abstract WorkShiftRepository bindWorkShiftRepository(WorkShiftRepositoryImpl workShiftRepositoryImpl);

    @Binds
    @Singleton
    public abstract ContractRepository bindContractRepository(ContractRepositoryImpl contractRepositoryImpl);

    @Binds
    @Singleton
    public abstract PayrollRepository bindPayrollRepository(PayrollRepositoryImpl payrollRepositoryImpl);

    @Binds
    @Singleton
    public abstract BranchRepository bindBranchRepository(BranchRepositoryImpl branchRepositoryImpl);

    @Binds
    @Singleton
    public abstract UserRepository bindUserRepository(UserRepositoryImpl userRepositoryImpl);
}
