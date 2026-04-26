package com.demo.ltud_n10.presentation.ui.employee;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.local.SharedPrefsManager;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.AccountDto;
import com.demo.ltud_n10.databinding.FragmentProfileBinding;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Inject
    AuthRepository authRepository;

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    ApiService apiService;

    @Inject
    SharedPrefsManager prefsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        loadData();

        binding.btnCancel.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void loadData() {
        String token = prefsManager.getToken();
        if (token == null) return;

        // Gọi API accounts/taikhoan để lấy mã nhân viên thực tế
        apiService.getMyAccount("Bearer " + token).enqueue(new Callback<List<AccountDto>>() {
            @Override
            public void onResponse(Call<List<AccountDto>> call, Response<List<AccountDto>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    AccountDto myAccount = response.body().get(0);
                    String maNv = myAccount.getMaNvId(); // Đã sửa từ getMaNv() thành getMaNvId()
                    if (maNv != null) {
                        fetchEmployeeDetail(maNv);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AccountDto>> call, Throwable t) {
                Log.e("ProfileFragment", "Error fetching account info: " + t.getMessage());
            }
        });
    }

    private void fetchEmployeeDetail(String maNv) {
        employeeRepository.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                for (Employee e : resource.data) {
                    if (maNv.equals(e.getId())) {
                        updateUI(e);
                        break;
                    }
                }
            }
        });
    }

    private void updateUI(Employee employee) {
        binding.etFullName.setText(employee.getName());
        binding.tvGender.setText(employee.getGender());
        binding.tvDob.setText(employee.getDob());
        binding.etCccd.setText(employee.getCccd());
        binding.etPhone.setText(employee.getPhone());
        binding.etAddress.setText(employee.getAddress());
        binding.etPosition.setText(employee.getPosition());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
