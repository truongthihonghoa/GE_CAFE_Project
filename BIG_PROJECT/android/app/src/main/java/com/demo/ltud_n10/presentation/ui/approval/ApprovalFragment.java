package com.demo.ltud_n10.presentation.ui.approval;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.FragmentApprovalBinding;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ApprovalFragment extends Fragment {

    private FragmentApprovalBinding binding;
    private ApprovalGroupAdapter adapter;
    private List<WorkShift> allShifts = new ArrayList<>();
    private String currentType = "Đăng ký ca";

    @Inject
    WorkShiftRepository workShiftRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentApprovalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
        setupTabs();
        loadData();
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ApprovalGroupAdapter();
        adapter.setOnActionListener(new ApprovalRequestAdapter.OnActionListener() {
            @Override
            public void onApprove(WorkShift shift) {
                handleApprove(shift);
            }

            @Override
            public void onReject(WorkShift shift) {
                handleReject(shift);
            }

            @Override
            public void onTimeChanged(WorkShift shift) {
                handleTimeChanged(shift);
            }
        });

        binding.rvRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRequests.setAdapter(adapter);
    }

    private void setupTabs() {
        binding.tabRegister.setOnClickListener(v -> {
            currentType = "Đăng ký ca";
            updateTabUI();
            filterData();
        });

        binding.tabLeave.setOnClickListener(v -> {
            currentType = "Nghỉ phép";
            updateTabUI();
            filterData();
        });
    }

    private void updateTabUI() {
        if ("Đăng ký ca".equals(currentType)) {
            binding.tabRegister.setCardBackgroundColor(Color.WHITE);
            binding.tabRegister.setCardElevation(4f);
            binding.tabLeave.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tabLeave.setCardElevation(0f);
        } else {
            binding.tabLeave.setCardBackgroundColor(Color.WHITE);
            binding.tabLeave.setCardElevation(4f);
            binding.tabRegister.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tabRegister.setCardElevation(0f);
        }
    }

    private void loadData() {
        workShiftRepository.getWorkShifts().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                allShifts = resource.data;
                filterData();
            }
        });
    }

    private void filterData() {
        List<WorkShift> filtered = allShifts.stream()
                .filter(s -> s.getType().equals(currentType))
                .collect(Collectors.toList());
        adapter.setData(filtered);
    }

    private void handleApprove(WorkShift shift) {
        shift.setStatus("Đã duyệt");
        workShiftRepository.updateWorkShift(shift).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                Toast.makeText(requireContext(), "Đã duyệt yêu cầu của " + shift.getEmployeeName(), Toast.LENGTH_SHORT).show();
                filterData();
            }
        });
    }

    private void handleReject(WorkShift shift) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Từ chối yêu cầu")
                .setMessage("Bạn có chắc chắn muốn từ chối yêu cầu này?")
                .setPositiveButton("Từ chối", (d, w) -> {
                    shift.setStatus("Bị từ chối");
                    workShiftRepository.updateWorkShift(shift).observe(getViewLifecycleOwner(), resource -> {
                        if (resource != null && resource.data != null) {
                            Toast.makeText(requireContext(), "Đã từ chối yêu cầu", Toast.LENGTH_SHORT).show();
                            filterData();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void handleTimeChanged(WorkShift shift) {
        workShiftRepository.updateWorkShift(shift).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                Toast.makeText(requireContext(), "Đã cập nhật thời gian làm việc", Toast.LENGTH_SHORT).show();
                // Data already updated in local list via observer or re-filter
                filterData();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
