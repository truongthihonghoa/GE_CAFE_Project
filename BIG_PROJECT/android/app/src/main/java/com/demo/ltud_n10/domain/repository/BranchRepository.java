package com.demo.ltud_n10.domain.repository;

import androidx.lifecycle.LiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Branch;
import java.util.List;

public interface BranchRepository {
    LiveData<Resource<List<Branch>>> getBranches();
    LiveData<Resource<Branch>> addBranch(Branch branch);
    LiveData<Resource<Branch>> updateBranch(Branch branch);
    LiveData<Resource<Boolean>> deleteBranch(String branchId);
}
