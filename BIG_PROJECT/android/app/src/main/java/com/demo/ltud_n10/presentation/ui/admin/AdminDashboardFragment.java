package com.demo.ltud_n10.presentation.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.FragmentAdminDashboardBinding;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.ContractRepository;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;
    
    @Inject
    AuthRepository authRepository;

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    WorkShiftRepository workShiftRepository;

    @Inject
    ContractRepository contractRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        loadDashboardData();
    }

    private void loadDashboardData() {
        // 1. Lấy số lượng nhân viên thực tế
        employeeRepository.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.tvStaffCount.setText(String.valueOf(resource.data.size()));
            }
        });

        // 2. Lấy ca làm việc hôm nay
        workShiftRepository.getWorkShifts().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.tvShiftCount.setText(String.valueOf(resource.data.size()));
                if (!resource.data.isEmpty()) {
                    // Lấy ca làm việc đầu tiên để hiển thị mẫu ở Dashboard
                    binding.tvEmployeeName.setText(resource.data.get(0).getEmployeeName());
                    binding.tvShiftTime.setText(resource.data.get(0).getStartTime() + " - " + resource.data.get(0).getEndTime());
                } else {
                    binding.tvEmployeeName.setText("Không có ca làm");
                }
            }
        });

        // 3. Lấy hợp đồng gần hết hạn
        contractRepository.getContracts().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                if (!resource.data.isEmpty()) {
                    binding.tvContractEmployee.setText(resource.data.get(0).getEmployeeName());
                    binding.tvContractId.setText(resource.data.get(0).getId());
                    binding.tvContractDate.setText(resource.data.get(0).getEndDate());
                } else {
                    binding.tvContractEmployee.setText("Không có hợp đồng");
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
