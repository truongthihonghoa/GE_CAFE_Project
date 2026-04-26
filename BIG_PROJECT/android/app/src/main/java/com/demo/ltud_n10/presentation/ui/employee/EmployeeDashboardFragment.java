package com.demo.ltud_n10.presentation.ui.employee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.FragmentEmployeeDashboardBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.ContractRepository;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EmployeeDashboardFragment extends Fragment {

    private FragmentEmployeeDashboardBinding binding;

    @Inject
    AuthRepository authRepository;

    @Inject
    WorkShiftRepository workShiftRepository;

    @Inject
    ContractRepository contractRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmployeeDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupToolbar();
        loadRealData();
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void loadRealData() {
        User user = authRepository.getCurrentUser().getValue();
        if (user != null) {
            binding.tvGreeting.setText("Xin chào, " + user.getName());
            
            // 1. Load Lịch làm việc hôm nay của đúng nhân viên này
            workShiftRepository.getWorkShifts().observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.data != null && !resource.data.isEmpty()) {
                    // Hiển thị tên và giờ làm của chính mình
                    binding.tvEmployeeName.setText(user.getName());
                    binding.tvShiftTime.setText(resource.data.get(0).getStartTime() + " - " + resource.data.get(0).getEndTime());
                    binding.tvShiftCount.setText(String.valueOf(resource.data.size()));
                } else {
                    binding.tvEmployeeName.setText(user.getName());
                    binding.tvShiftTime.setText("Không có ca làm");
                    binding.tvShiftCount.setText("0");
                }
            });

            // 2. Load Hợp đồng của đúng nhân viên này
            contractRepository.getContracts().observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.data != null && !resource.data.isEmpty()) {
                    binding.tvContractUser.setText(user.getName());
                    binding.tvContractId.setText(resource.data.get(0).getId());
                    binding.tvContractDate.setText(resource.data.get(0).getEndDate());
                } else {
                    binding.tvContractUser.setText(user.getName());
                    binding.tvContractId.setText("Chưa có hợp đồng");
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
