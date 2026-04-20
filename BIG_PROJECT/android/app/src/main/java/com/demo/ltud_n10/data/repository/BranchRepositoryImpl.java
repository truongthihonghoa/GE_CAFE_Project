package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Branch;
import com.demo.ltud_n10.domain.repository.BranchRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BranchRepositoryImpl implements BranchRepository {

    private final List<Branch> branches = new ArrayList<>();

    @Inject
    public BranchRepositoryImpl() {
        // Mock data ban đầu
        branches.add(new Branch("CN001", "Chi nhánh Hải Châu", "186 Đường 2/9, Hải Châu", "0328811989", "Nguyễn Văn Nam", "Đang hoạt động"));
        branches.add(new Branch("CN002", "Chi nhánh NHS", "39 Ngũ Hành Sơn", "0906256241", "Trần Thị Bé", "Đang hoạt động"));
    }

    @Override
    public LiveData<Resource<List<Branch>>> getBranches() {
        MutableLiveData<Resource<List<Branch>>> data = new MutableLiveData<>();
        data.setValue(Resource.success(new ArrayList<>(branches)));
        return data;
    }

    @Override
    public LiveData<Resource<Branch>> addBranch(Branch branch) {
        MutableLiveData<Resource<Branch>> data = new MutableLiveData<>();
        // Tự động sinh mã chi nhánh (UC 9.1 - Step 6)
        branch.setId("CN00" + (branches.size() + 1));
        branches.add(branch);
        data.setValue(Resource.success(branch));
        return data;
    }

    @Override
    public LiveData<Resource<Branch>> updateBranch(Branch branch) {
        MutableLiveData<Resource<Branch>> data = new MutableLiveData<>();
        for (int i = 0; i < branches.size(); i++) {
            if (branches.get(i).getId().equals(branch.getId())) {
                branches.set(i, branch);
                data.setValue(Resource.success(branch));
                return data;
            }
        }
        data.setValue(Resource.error("Không tìm thấy chi nhánh", null));
        return data;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteBranch(String branchId) {
        MutableLiveData<Resource<Boolean>> data = new MutableLiveData<>();
        // Giả lập Business Rules (UC 9.3 - Step 5a)
        // Nếu là chi nhánh NHS (CN002) thì giả lập còn ràng buộc dữ liệu
        if ("CN002".equals(branchId)) {
            data.setValue(Resource.error("Không thể ngưng hoạt động chi nhánh do còn dữ liệu liên quan. Vui lòng thử lại sau.", false));
        } else {
            branches.removeIf(b -> b.getId().equals(branchId));
            data.setValue(Resource.success(true));
        }
        return data;
    }
}
