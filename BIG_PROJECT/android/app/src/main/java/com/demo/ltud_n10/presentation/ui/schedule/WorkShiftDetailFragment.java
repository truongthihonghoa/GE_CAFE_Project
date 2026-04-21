package com.demo.ltud_n10.presentation.ui.schedule;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.FragmentScheduleDetailBinding;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkShiftDetailFragment extends Fragment {

    private FragmentScheduleDetailBinding binding;
    private WorkShiftViewModel viewModel;
    private WorkShift currentShift;
    private String title;
    private boolean isViewOnly = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScheduleDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WorkShiftViewModel.class);

        if (getArguments() != null) {
            currentShift = (WorkShift) getArguments().getSerializable("shift");
            title = getArguments().getString("title");
            isViewOnly = getArguments().getBoolean("isViewOnly", false);
        }

        setupUI();
        if (currentShift != null) {
            populateData();
        }
    }

    private void setupUI() {
        binding.tvTitle.setText(title);

        // Cập nhật danh sách nhân viên đầy đủ
        List<String> employeeList = new ArrayList<>(Arrays.asList("Lê Văn C", "Phạm Thị D", "Lê Văn D", "Trần Thị E"));
        
        if (currentShift != null && !employeeList.contains(currentShift.getEmployeeName())) {
            employeeList.add(currentShift.getEmployeeName());
        }

        ArrayAdapter<String> empAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, employeeList);
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEmployee.setAdapter(empAdapter);

        String[] positions = {"Phục vụ", "Pha chế", "Giữ xe"};
        ArrayAdapter<String> posAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, positions);
        posAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPosition.setAdapter(posAdapter);

        if (isViewOnly) {
            binding.btnSave.setText("Thoát");
            binding.btnCancel.setVisibility(View.GONE);
            binding.btnSave.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
            
            binding.spinnerEmployee.setEnabled(false);
            binding.btnDatePicker.setEnabled(false);
            binding.etStartTime.setEnabled(false);
            binding.etEndTime.setEnabled(false);
            binding.spinnerPosition.setEnabled(false);
        } else {
            binding.btnSave.setText("Lưu");
            binding.btnCancel.setVisibility(View.VISIBLE);
            binding.btnSave.setOnClickListener(v -> saveShift());
            binding.btnCancel.setOnClickListener(v -> showCancelConfirmDialog());
            
            binding.btnDatePicker.setOnClickListener(v -> showDatePicker());
            binding.etStartTime.setOnClickListener(v -> showTimePicker(true));
            binding.etEndTime.setOnClickListener(v -> showTimePicker(false));
        }
    }

    private void populateData() {
        binding.tvDob.setText(currentShift.getDate());
        binding.etStartTime.setText(currentShift.getStartTime());
        binding.etEndTime.setText(currentShift.getEndTime());
        
        ArrayAdapter<String> empAdapter = (ArrayAdapter<String>) binding.spinnerEmployee.getAdapter();
        int empPos = empAdapter.getPosition(currentShift.getEmployeeName());
        if (empPos >= 0) {
            binding.spinnerEmployee.setSelection(empPos);
        }

        ArrayAdapter<String> posAdapter = (ArrayAdapter<String>) binding.spinnerPosition.getAdapter();
        int posIdx = posAdapter.getPosition(currentShift.getPosition());
        if (posIdx >= 0) {
            binding.spinnerPosition.setSelection(posIdx);
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            String date = String.format("%02d/%02d/%04d", day, month + 1, year);
            binding.tvDob.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(boolean isStart) {
        final Calendar c = Calendar.getInstance();
        new TimePickerDialog(requireContext(), (view, hour, minute) -> {
            String time = String.format("%02d:%02d", hour, minute);
            if (isStart) binding.etStartTime.setText(time);
            else binding.etEndTime.setText(time);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void saveShift() {
        String employeeName = binding.spinnerEmployee.getSelectedItem().toString();
        String date = binding.tvDob.getText().toString();
        String startTime = binding.etStartTime.getText().toString();
        String endTime = binding.etEndTime.getText().toString();
        String position = binding.spinnerPosition.getSelectedItem().toString();

        if (date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        WorkShift shift = (currentShift != null) ? currentShift : new WorkShift();
        if (currentShift == null) {
            shift.setId(UUID.randomUUID().toString());
        }

        shift.setEmployeeName(employeeName);
        shift.setEmployeeId("EMP01"); 
        shift.setDate(date);
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);
        shift.setPosition(position);
        shift.setSent(false);

        String successMessage = (currentShift == null) ? "Đã tạo lịch làm việc thành công" : "Cập nhật lịch làm việc thành công";

        if (currentShift == null) {
            viewModel.addWorkShift(shift).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.SUCCESS) {
                    showSuccessDialog(successMessage);
                }
            });
        } else {
            viewModel.updateWorkShift(shift).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.SUCCESS) {
                    showSuccessDialog(successMessage);
                }
            });
        }
    }

    private void showSuccessDialog(String message) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_success_notification);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.TOP);
            
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.y = 50; 
            window.setAttributes(layoutParams);
        }

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                Navigation.findNavController(requireView()).popBackStack();
            }
        }, 2000);
    }

    private void showCancelConfirmDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_cancel);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        MaterialButton btnNo = dialog.findViewById(R.id.btnDialogCancel);
        MaterialButton btnYes = dialog.findViewById(R.id.btnDialogConfirm);

        btnNo.setOnClickListener(v -> dialog.dismiss());
        
        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            Navigation.findNavController(requireView()).navigateUp();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
