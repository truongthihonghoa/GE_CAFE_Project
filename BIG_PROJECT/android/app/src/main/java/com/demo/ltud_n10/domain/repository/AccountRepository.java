package com.demo.ltud_n10.domain.repository;

import androidx.lifecycle.LiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Account;
import java.util.List;

public interface AccountRepository {
    LiveData<Resource<List<Account>>> getAccounts();
    LiveData<Resource<Account>> createAccount(Account account, String password, boolean isStaff);
    LiveData<Resource<Account>> updateAccount(Account account, String password, Boolean isStaff, Boolean isActive);
    LiveData<Resource<Boolean>> deleteAccount(String accountId);
    LiveData<Resource<Boolean>> changePassword(String accountId, String newPassword);
}