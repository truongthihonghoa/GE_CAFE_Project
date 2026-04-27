package com.demo.ltud_n10.presentation.ui.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.FragmentScheduleDetailBinding;
import com.demo.ltud_n10.domain.model.WorkShift;

import java.util.Calendar;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkShiftDetailFragment extends Fragment {

    private FragmentScheduleDetailBinding binding;
    private WorkShiftViewModel viewModel;
    private WorkShift currentShift;
    private String title;
    private java.util.List<com.demo.ltud_n10.domain.model.Employee> employeeList = new java.util.ArrayList<>();

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
        }

        setupUI();
        if (currentShift != null) {
            populateData();
        }
    }

    private void setupUI() {
        binding.tvTitle.setText(title);
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Fetch real employees for dropdown
        viewModel.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                employeeList = resource.data;
                java.util.List<String> names = new java.util.ArrayList<>();
                for (com.demo.ltud_n10.domain.model.Employee e : employeeList) {
                    names.add(e.getName());
                }
                ArrayAdapter<String> empAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
                empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerEmployee.setAdapter(empAdapter);

                // Re-select if editing
                if (currentShift != null) {
                    int pos = names.indexOf(currentShift.getEmployeeName());
                    if (pos >= 0) binding.spinnerEmployee.setSelection(pos);
                }
            }
        });

        // Positions dropdown
        String[] positions = {"Phục vụ", "Pha chế", "Giữ xe"};
        ArrayAdapter<String> posAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, positions);
        posAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPosition.setAdapter(posAdapter);

        binding.btnDatePicker.setOnClickListener(v -> showDatePicker());
        binding.etStartTime.setOnClickListener(v -> showTimePicker(true));
        binding.etEndTime.setOnClickListener(v -> showTimePicker(false));

        binding.btnSave.setOnClickListener(v -> saveShift());
        binding.btnCancel.setOnClickListener(v -> handleCancel());
    }

    private void populateData() {
        binding.tvDob.setText(currentShift.getDate());
        binding.etStartTime.setText(currentShift.getStartTime());
        binding.etEndTime.setText(currentShift.getEndTime());
        
        // Set spinner selections based on currentShift data
        ArrayAdapter<String> empAdapter = (ArrayAdapter<String>) binding.spinnerEmployee.getAdapter();
        int empPos = empAdapter.getPosition(currentShift.getEmployeeName());
        if (empPos >= 0) binding.spinnerEmployee.setSelection(empPos);

        ArrayAdapter<String> posAdapter = (ArrayAdapter<String>) binding.spinnerPosition.getAdapter();
        int posIdx = posAdapter.getPosition(currentShift.getPosition());
        if (posIdx >= 0) binding.spinnerPosition.setSelection(posIdx);
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

        WorkShift shift = currentShift;
        if (shift == null) {
            shift = new WorkShift();
            // ma_llv max 20 chars
            String shortId = "S" + (System.currentTimeMillis() % 10000000000L); 
            shift.setId(shortId);
        }

        int selectedEmpIdx = binding.spinnerEmployee.getSelectedItemPosition();
        if (selectedEmpIdx >= 0 && selectedEmpIdx < employeeList.size()) {
            com.demo.ltud_n10.domain.model.Employee selectedEmp = employeeList.get(selectedEmpIdx);
            shift.setEmployeeName(selectedEmp.getName());
            shift.setEmployeeId(selectedEmp.getId());
        }
        shift.setDate(date);
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);
        shift.setPosition(position);
        shift.setSent(false);

        if (currentShift == null) {
            viewModel.addWorkShift(shift).observe(getViewLifecycleOwner(), resource -> handleResult(resource, "Thêm lịch làm việc thành công"));
        } else {
            viewModel.updateWorkShift(shift).observe(getViewLifecycleOwner(), resource -> handleResult(resource, "Cập nhật lịch làm việc thành công"));
        }
    }

    private void handleResult(Resource<?> resource, String successMsg) {
        if (resource.status == Resource.Status.SUCCESS) {
            Toast.makeText(requireContext(), successMsg, Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).popBackStack();
        } else if (resource.status == Resource.Status.ERROR) {
            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCancel() {
        new AlertDialog.Builder(requireContext())
                .setTitle("XÁC NHẬN HỦY")
                .setMessage("Bạn có thông tin chưa lưu, xác nhận hủy?")
                .setPositiveButton("Đồng ý", (d, w) -> Navigation.findNavController(requireView()).popBackStack())
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
