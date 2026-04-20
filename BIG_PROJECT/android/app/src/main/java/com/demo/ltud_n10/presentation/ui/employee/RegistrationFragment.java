package com.demo.ltud_n10.presentation.ui.employee;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.FragmentRegistrationBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;
    private RegistrationHistoryAdapter adapter;
    private String currentMode = "SHIFT"; // "SHIFT" or "LEAVE"
    private String selectedShift = "Sáng";
    private String selectedStartTime = "08:00";
    private String selectedEndTime = "12:00";

    @Inject
    WorkShiftRepository workShiftRepository;

    @Inject
    AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTabs();
        setupShiftSelection();
        setupRecyclerView();
        setupSubmitButtons();
        setupToolbar();
        loadHistory();
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void setupTabs() {
        binding.tabShift.setOnClickListener(v -> {
            currentMode = "SHIFT";
            updateTabUI();
        });

        binding.tabLeave.setOnClickListener(v -> {
            currentMode = "LEAVE";
            updateTabUI();
        });
    }

    private void updateTabUI() {
        if ("SHIFT".equals(currentMode)) {
            binding.tabShift.setCardBackgroundColor(Color.WHITE);
            binding.tabShift.setCardElevation(4f);
            binding.tabLeave.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tabLeave.setCardElevation(0f);
            binding.layoutShiftContent.setVisibility(View.VISIBLE);
            binding.layoutLeaveContent.setVisibility(View.GONE);
        } else {
            binding.tabLeave.setCardBackgroundColor(Color.WHITE);
            binding.tabLeave.setCardElevation(4f);
            binding.tabShift.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tabShift.setCardElevation(0f);
            binding.layoutLeaveContent.setVisibility(View.VISIBLE);
            binding.layoutShiftContent.setVisibility(View.GONE);
        }
        loadHistory();
    }

    private void setupShiftSelection() {
        binding.rbMorning.setOnClickListener(v -> {
            selectedShift = "Sáng";
            selectedStartTime = "08:00";
            selectedEndTime = "12:00";
            updateShiftUI();
        });
        binding.rbAfternoon.setOnClickListener(v -> {
            selectedShift = "Chiều";
            selectedStartTime = "13:00";
            selectedEndTime = "17:00";
            updateShiftUI();
        });
        binding.rbEvening.setOnClickListener(v -> {
            selectedShift = "Tối";
            selectedStartTime = "18:00";
            selectedEndTime = "22:00";
            updateShiftUI();
        });
        updateShiftUI();
    }

    private void updateShiftUI() {
        binding.cvShiftMorning.setCardBackgroundColor(selectedShift.equals("Sáng") ? Color.parseColor("#E8F5E9") : Color.parseColor("#F8F9FA"));
        binding.cvShiftAfternoon.setCardBackgroundColor(selectedShift.equals("Chiều") ? Color.parseColor("#E8F5E9") : Color.parseColor("#F8F9FA"));
        binding.cvShiftEvening.setCardBackgroundColor(selectedShift.equals("Tối") ? Color.parseColor("#E8F5E9") : Color.parseColor("#F8F9FA"));
    }

    private void setupRecyclerView() {
        adapter = new RegistrationHistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHistory.setAdapter(adapter);
    }

    private void setupSubmitButtons() {
        binding.btnSubmitShift.setOnClickListener(v -> handleShiftSubmit());
        binding.btnSubmitLeave.setOnClickListener(v -> handleLeaveSubmit());
    }

    private void handleShiftSubmit() {
        User user = authRepository.getCurrentUser().getValue();
        if (user == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateStr = sdf.format(new Date(binding.calendarShift.getDate()));

        WorkShift shift = new WorkShift();
        List<WorkShift.EmployeeAssignment> assignments = new ArrayList<>();
        assignments.add(new WorkShift.EmployeeAssignment(user.getId(), user.getName(), selectedShift));
        shift.setEmployeeAssignments(assignments);
        shift.setEmployeeName(user.getName());
        shift.setDate(dateStr);
        shift.setStartTime(selectedStartTime);
        shift.setEndTime(selectedEndTime);
        shift.setPosition(selectedShift);
        shift.setStatus("Chờ duyệt");
        shift.setType("Đăng ký ca");

        workShiftRepository.addWorkShift(shift).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Gửi đăng ký lịch thành công", Toast.LENGTH_SHORT).show();
                loadHistory();
            }
        });
    }

    private void handleLeaveSubmit() {
        User user = authRepository.getCurrentUser().getValue();
        if (user == null) return;

        String reason = binding.etLeaveReason.getText().toString();
        if (reason.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập lý do nghỉ phép", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String startDate = sdf.format(new Date(binding.calendarStart.getDate()));
        String endDate = sdf.format(new Date(binding.calendarEnd.getDate()));

        WorkShift leave = new WorkShift();
        List<WorkShift.EmployeeAssignment> assignments = new ArrayList<>();
        assignments.add(new WorkShift.EmployeeAssignment(user.getId(), user.getName(), reason));
        leave.setEmployeeAssignments(assignments);
        leave.setEmployeeName(user.getName());
        leave.setDate(startDate + " - " + endDate);
        leave.setStartTime("00:00");
        leave.setEndTime("00:00");
        leave.setPosition(reason);
        leave.setStatus("Chờ duyệt");
        leave.setType("Nghỉ phép");

        workShiftRepository.addWorkShift(leave).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Gửi đơn xin nghỉ phép thành công", Toast.LENGTH_SHORT).show();
                binding.etLeaveReason.setText("");
                loadHistory();
            }
        });
    }

    private void loadHistory() {
        User user = authRepository.getCurrentUser().getValue();
        if (user == null) return;

        workShiftRepository.getWorkShifts().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                String targetType = "SHIFT".equals(currentMode) ? "Đăng ký ca" : "Nghỉ phép";
                List<WorkShift> filtered = resource.data.stream()
                        .filter(s -> s.getEmployeeName().equals(user.getName()) && s.getType().equals(targetType))
                        .collect(Collectors.toList());
                adapter.setItems(filtered);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
