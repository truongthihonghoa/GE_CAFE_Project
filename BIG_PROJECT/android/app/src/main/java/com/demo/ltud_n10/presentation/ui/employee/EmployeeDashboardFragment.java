package com.demo.ltud_n10.presentation.ui.employee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.FragmentEmployeeDashboardBinding;
import com.demo.ltud_n10.domain.model.Contract;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.ContractRepository;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    EmployeeRepository employeeRepository;

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
        if (user == null) return;

        binding.tvGreeting.setText("Xin chào, " + user.getName());
        binding.tvEmployeeName.setText(user.getName());
        binding.tvContractUser.setText(user.getName());

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // 1. Lấy thông tin Ca làm việc hôm nay của chính nhân viên này
        workShiftRepository.getWorkShifts().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                List<WorkShift> todayShifts = resource.data.stream()
                        .filter(s -> s.getEmployeeId().equals(user.getId()) && today.equals(s.getDate()))
                        .collect(java.util.stream.Collectors.toList());

                if (!todayShifts.isEmpty()) {
                    WorkShift shift = todayShifts.get(0);
                    binding.tvShiftTime.setText(shift.getStartTime() + " - " + shift.getEndTime());
                    binding.tvShiftStatus.setText("Đã lên lịch");
                } else {
                    binding.tvShiftTime.setText("Không có ca làm");
                    binding.tvShiftStatus.setText("Nghỉ");
                }
            }
        });

        // 2. Lấy thông tin Hợp đồng
        contractRepository.getContracts().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                List<Contract> myContracts = resource.data.stream()
                        .filter(c -> c.getEmployeeId().equals(user.getId()))
                        .collect(java.util.stream.Collectors.toList());

                if (!myContracts.isEmpty()) {
                    Contract latest = myContracts.get(myContracts.size() - 1);
                    binding.tvContractId.setText(latest.getId());
                    binding.tvContractDate.setText(latest.getEndDate());
                    binding.tvContractStatus.setText("Còn hiệu lực");
                }
            }
        });

        // 3. Đếm số lượng nhân viên đang hoạt động (cho mục hiển thị chung)
        employeeRepository.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                long activeCount = resource.data.stream()
                        .filter(e -> "Đang làm việc".equals(e.getStatus()))
                        .count();
                binding.tvStaffCount.setText(String.valueOf(activeCount));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}