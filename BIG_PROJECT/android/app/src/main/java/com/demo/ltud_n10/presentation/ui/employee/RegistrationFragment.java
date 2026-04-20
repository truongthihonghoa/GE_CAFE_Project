package com.demo.ltud_n10.presentation.ui.employee;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.DialogConfirmDeleteBinding;
import com.demo.ltud_n10.databinding.DialogEditRegistrationBinding;
import com.demo.ltud_n10.databinding.FragmentRegistrationBinding;
import com.demo.ltud_n10.databinding.LayoutSuccessNotificationBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import java.text.SimpleDateFormat;
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
    private String currentMode = "SHIFT";

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
        setupSubmitButtons();
        setupRecyclerView();
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
        View.OnClickListener listener = v -> {
            if (v == binding.cvShiftMorning || v == binding.rbMorning) {
                binding.rbMorning.setChecked(true);
                binding.rbAfternoon.setChecked(false);
                binding.rbEvening.setChecked(false);
            } else if (v == binding.cvShiftAfternoon || v == binding.rbAfternoon) {
                binding.rbMorning.setChecked(false);
                binding.rbAfternoon.setChecked(true);
                binding.rbEvening.setChecked(false);
            } else if (v == binding.cvShiftEvening || v == binding.rbEvening) {
                binding.rbMorning.setChecked(false);
                binding.rbAfternoon.setChecked(false);
                binding.rbEvening.setChecked(true);
            }
            updateShiftUI();
        };

        binding.cvShiftMorning.setOnClickListener(listener);
        binding.rbMorning.setOnClickListener(listener);
        binding.cvShiftAfternoon.setOnClickListener(listener);
        binding.rbAfternoon.setOnClickListener(listener);
        binding.cvShiftEvening.setOnClickListener(listener);
        binding.rbEvening.setOnClickListener(listener);
        
        // Mặc định chọn ca sáng
        binding.rbMorning.setChecked(true);
        updateShiftUI();
    }

    private void updateShiftUI() {
        int selectedColor = Color.parseColor("#E8F5E9"); // Xanh lá nhạt
        int defaultColor = Color.parseColor("#F8F9FA"); // Xám nhạt mặc định
        
        binding.cvShiftMorning.setCardBackgroundColor(binding.rbMorning.isChecked() ? selectedColor : defaultColor);
        binding.cvShiftAfternoon.setCardBackgroundColor(binding.rbAfternoon.isChecked() ? selectedColor : defaultColor);
        binding.cvShiftEvening.setCardBackgroundColor(binding.rbEvening.isChecked() ? selectedColor : defaultColor);
    }

    private void setupRecyclerView() {
        adapter = new RegistrationHistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHistory.setAdapter(adapter);

        adapter.setOnActionClickListener(new RegistrationHistoryAdapter.OnActionClickListener() {
            @Override
            public void onEdit(WorkShift shift) {
                showEditDialog(shift);
            }

            @Override
            public void onDelete(WorkShift shift) {
                showDeleteConfirmDialog(shift);
            }
        });
    }

    private void showEditDialog(WorkShift shift) {
        DialogEditRegistrationBinding dialogBinding = DialogEditRegistrationBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if ("Đăng ký ca".equals(shift.getType())) {
            dialogBinding.layoutEditShift.setVisibility(View.VISIBLE);
            dialogBinding.layoutEditLeave.setVisibility(View.GONE);
            dialogBinding.tvDialogTitle.setText("Chỉnh sửa đăng ký ca");
            try {
                Date date = sdf.parse(shift.getDate());
                if (date != null) dialogBinding.calendarEditShift.setDate(date.getTime());
            } catch (Exception ignored) {}
            if ("Sáng".equals(shift.getPosition())) dialogBinding.rbEditMorning.setChecked(true);
            else if ("Chiều".equals(shift.getPosition())) dialogBinding.rbEditAfternoon.setChecked(true);
            else dialogBinding.rbEditEvening.setChecked(true);
        } else {
            dialogBinding.layoutEditShift.setVisibility(View.GONE);
            dialogBinding.layoutEditLeave.setVisibility(View.VISIBLE);
            dialogBinding.tvDialogTitle.setText("Chỉnh sửa nghỉ phép");
            dialogBinding.etEditLeaveReason.setText(shift.getPosition());
            String[] dates = shift.getDate().split(" - ");
            if (dates.length == 2) {
                try {
                    Date start = sdf.parse(dates[0]);
                    Date end = sdf.parse(dates[1]);
                    if (start != null) dialogBinding.calendarEditStart.setDate(start.getTime());
                    if (end != null) dialogBinding.calendarEditEnd.setDate(end.getTime());
                } catch (Exception ignored) {}
            }
        }

        dialogBinding.btnCancelEdit.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnSaveEdit.setOnClickListener(v -> {
            if ("Đăng ký ca".equals(shift.getType())) {
                shift.setDate(sdf.format(new Date(dialogBinding.calendarEditShift.getDate())));
                if (dialogBinding.rbEditMorning.isChecked()) {
                    shift.setPosition("Sáng"); shift.setStartTime("08:00"); shift.setEndTime("12:00");
                } else if (dialogBinding.rbEditAfternoon.isChecked()) {
                    shift.setPosition("Chiều"); shift.setStartTime("13:00"); shift.setEndTime("17:00");
                } else {
                    shift.setPosition("Tối"); shift.setStartTime("18:00"); shift.setEndTime("22:00");
                }
            } else {
                shift.setDate(sdf.format(new Date(dialogBinding.calendarEditStart.getDate())) + " - " + sdf.format(new Date(dialogBinding.calendarEditEnd.getDate())));
                shift.setPosition(dialogBinding.etEditLeaveReason.getText().toString());
            }
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
            shift.setSentTime(timeSdf.format(new Date()));
            workShiftRepository.updateWorkShift(shift).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessNotification("Cập nhật đăng ký thành công");
                    loadHistory();
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }

    private void showDeleteConfirmDialog(WorkShift shift) {
        DialogConfirmDeleteBinding dialogBinding = DialogConfirmDeleteBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnDelete.setOnClickListener(v -> {
            workShiftRepository.deleteWorkShift(shift.getId()).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessNotification("Xóa đăng ký thành công");
                    loadHistory();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void setupSubmitButtons() {
        binding.btnSubmitShift.setOnClickListener(v -> handleShiftSubmit());
        binding.btnSubmitLeave.setOnClickListener(v -> handleLeaveSubmit());
    }

    private void handleShiftSubmit() {
        User user = authRepository.getCurrentUser().getValue();
        if (user == null) return;
        WorkShift shift = new WorkShift();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        shift.setDate(sdf.format(new Date(binding.calendarShift.getDate())));
        String pos = binding.rbAfternoon.isChecked() ? "Chiều" : binding.rbEvening.isChecked() ? "Tối" : "Sáng";
        shift.setPosition(pos);
        shift.setStartTime(pos.equals("Sáng") ? "08:00" : pos.equals("Chiều") ? "13:00" : "18:00");
        shift.setEndTime(pos.equals("Sáng") ? "12:00" : pos.equals("Chiều") ? "17:00" : "22:00");
        shift.setEmployeeId(user.getId());
        shift.setEmployeeName(user.getName());
        shift.setStatus("Chờ duyệt");
        shift.setType("Đăng ký ca");
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        shift.setSentTime(timeSdf.format(new Date()));
        workShiftRepository.addWorkShift(shift).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                showSuccessNotification("Gửi đăng ký lịch thành công");
                loadHistory();
            }
        });
    }

    private void handleLeaveSubmit() {
        User user = authRepository.getCurrentUser().getValue();
        if (user == null) return;
        String reason = binding.etLeaveReason.getText().toString();
        if (reason.isEmpty()) return;
        WorkShift leave = new WorkShift();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        leave.setDate(sdf.format(new Date(binding.calendarStart.getDate())) + " - " + sdf.format(new Date(binding.calendarEnd.getDate())));
        leave.setPosition(reason);
        leave.setEmployeeId(user.getId());
        leave.setEmployeeName(user.getName());
        leave.setStatus("Chờ duyệt");
        leave.setType("Nghỉ phép");
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        leave.setSentTime(timeSdf.format(new Date()));

        workShiftRepository.addWorkShift(leave).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                showSuccessNotification("Gửi đăng ký nghỉ phép thành công");
                binding.etLeaveReason.setText("");
                loadHistory();
            }
        });
    }

    private void showSuccessNotification(String message) {
        if (getView() == null) return;
        LayoutSuccessNotificationBinding navBinding = LayoutSuccessNotificationBinding.inflate(getLayoutInflater());
        navBinding.tvMessage.setText(message);
        FrameLayout rootLayout = (FrameLayout) requireActivity().findViewById(android.R.id.content);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.setMargins(0, 0, 40, 100);
        View notifyView = navBinding.getRoot();
        notifyView.setLayoutParams(params);
        rootLayout.addView(notifyView);
        new Handler(Looper.getMainLooper()).postDelayed(() -> rootLayout.removeView(notifyView), 3000);
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
