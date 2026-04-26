package com.demo.ltud_n10.presentation.ui.schedule;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.DialogConfirmCancelBinding;
import com.demo.ltud_n10.databinding.FragmentScheduleDetailBinding;
import com.demo.ltud_n10.databinding.LayoutSuccessNotificationBinding;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkShiftDetailFragment extends Fragment {

    private FragmentScheduleDetailBinding binding;
    private WorkShiftViewModel viewModel;
    private WorkShift currentShift;
    private String title;
    private List<Employee> employeeList = new ArrayList<>();

    @Inject
    EmployeeRepository employeeRepository;

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
        loadEmployees();
    }

    private void setupUI() {
        binding.tvTitle.setText(title);
        binding.btnBack.setOnClickListener(v -> handleCancel());

        String[] positions = {"Phục vụ", "Pha chế", "Giữ xe"};
        ArrayAdapter<String> posAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, positions);
        posAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPosition.setAdapter(posAdapter);

        String[] shifts = {"Tự chọn", "06:00 - 12:00", "12:00 - 17:00", "17:00 - 22:00"};
        ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, shifts);
        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerShift.setAdapter(shiftAdapter);

        binding.spinnerShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) { binding.etStartTime.setText("06:00"); binding.etEndTime.setText("12:00"); }
                else if (position == 2) { binding.etStartTime.setText("12:00"); binding.etEndTime.setText("17:00"); }
                else if (position == 3) { binding.etStartTime.setText("17:00"); binding.etEndTime.setText("22:00"); }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.spinnerEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < employeeList.size()) {
                    binding.etBranchId.setText(employeeList.get(position).getBranchId());
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.btnDatePicker.setOnClickListener(v -> showDatePicker());
        binding.btnSave.setOnClickListener(v -> saveShift());
        binding.btnCancel.setOnClickListener(v -> handleCancel());

        // XỬ LÝ KHI ĐÃ GỬI THÔNG BÁO THÀNH CÔNG (XEM CHI TIẾT)
        if (currentShift != null && "Đã gửi".equals(currentShift.getStatus())) {
            // 1. Ẩn nút Lưu
            binding.btnSave.setVisibility(View.GONE);
            
            // 2. Thay nút Hủy bằng nút Đóng
            binding.btnCancel.setText("Đóng");
            
            // 3. Vô hiệu hóa các ô nhập liệu
            binding.spinnerEmployee.setEnabled(false);
            binding.spinnerShift.setEnabled(false);
            binding.spinnerPosition.setEnabled(false);
            binding.btnDatePicker.setEnabled(false);
            binding.etStartTime.setEnabled(false);
            binding.etEndTime.setEnabled(false);
            binding.etBranchId.setEnabled(false);
            
            // Thay đổi tiêu đề nút đóng để không hiện dialog xác nhận khi nhấn "Đóng"
            binding.btnCancel.setOnClickListener(v -> Navigation.findNavController(requireView()).popBackStack());
        }
    }

    private void loadEmployees() {
        employeeRepository.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                employeeList = resource.data;
                List<String> names = new ArrayList<>();
                for (Employee e : employeeList) names.add(e.getName());
                ArrayAdapter<String> empAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
                empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerEmployee.setAdapter(empAdapter);
                if (currentShift != null) populateData();
            }
        });
    }

    private void populateData() {
        binding.tvDob.setText(currentShift.getDate());
        binding.etStartTime.setText(currentShift.getStartTime());
        binding.etEndTime.setText(currentShift.getEndTime());
        binding.etBranchId.setText(currentShift.getBranchId());
        
        ArrayAdapter<String> empAdapter = (ArrayAdapter<String>) binding.spinnerEmployee.getAdapter();
        if (empAdapter != null) {
            int empPos = empAdapter.getPosition(currentShift.getEmployeeName());
            if (empPos >= 0) binding.spinnerEmployee.setSelection(empPos);
        }
        
        // Populate position spinner
        ArrayAdapter<String> posAdapter = (ArrayAdapter<String>) binding.spinnerPosition.getAdapter();
        if (posAdapter != null) {
            int posIdx = posAdapter.getPosition(currentShift.getPosition());
            if (posIdx >= 0) binding.spinnerPosition.setSelection(posIdx);
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            binding.tvDob.setText(String.format("%04d-%02d-%02d", year, month + 1, day));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private String generateShortId() {
        Random random = new Random();
        int number = random.nextInt(900000) + 100000; 
        return "LLV" + number; 
    }

    private void saveShift() {
        if (!validateInput()) return;

        int empIdx = binding.spinnerEmployee.getSelectedItemPosition();
        if (empIdx < 0) return;
        Employee selectedEmp = employeeList.get(empIdx);
        
        WorkShift shift = currentShift;
        if (shift == null) {
            shift = new WorkShift();
            shift.setId(generateShortId());
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            shift.setSentTime(sdf.format(new Date()));
        }

        shift.setEmployeeName(selectedEmp.getName());
        shift.setEmployeeId(selectedEmp.getId());
        shift.setBranchId(selectedEmp.getBranchId());
        shift.setDate(binding.tvDob.getText().toString());
        shift.setStartTime(binding.etStartTime.getText().toString());
        shift.setEndTime(binding.etEndTime.getText().toString());
        shift.setPosition(binding.spinnerPosition.getSelectedItem().toString());
        
        // MẶC ĐỊNH LÀ "Chưa gửi" KHI TẠO MỚI
        if (currentShift == null) {
            shift.setStatus("Chưa gửi");
        } else {
            shift.setStatus(currentShift.getStatus());
        }

        if (currentShift == null) {
            viewModel.addWorkShift(shift).observe(getViewLifecycleOwner(), resource -> handleResult(resource, "Tạo lịch làm việc thành công"));
        } else {
            viewModel.updateWorkShift(shift).observe(getViewLifecycleOwner(), resource -> handleResult(resource, "Cập nhật lịch làm việc thành công"));
        }
    }

    private boolean validateInput() {
        if (binding.tvDob.getText().toString().isEmpty()) { 
            Toast.makeText(requireContext(), "Vui lòng chọn ngày làm việc", Toast.LENGTH_SHORT).show();
            return false; 
        }
        return true;
    }

    private void handleResult(Resource<?> resource, String successMsg) {
        if (resource.status == Resource.Status.SUCCESS) {
            showSuccessNotification(successMsg);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (isAdded()) Navigation.findNavController(requireView()).popBackStack();
            }, 1000);
        } else if (resource.status == Resource.Status.ERROR) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Lỗi hệ thống")
                    .setMessage(resource.message)
                    .setPositiveButton("Đã rõ", null)
                    .show();
        }
    }

    private void showSuccessNotification(String message) {
        if (getView() == null) return;
        LayoutSuccessNotificationBinding navBinding = LayoutSuccessNotificationBinding.inflate(getLayoutInflater());
        navBinding.tvMessage.setText(message);
        FrameLayout rootLayout = (FrameLayout) requireActivity().findViewById(android.R.id.content);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, 150, 0, 0); 
        View notifyView = navBinding.getRoot();
        notifyView.setLayoutParams(params);
        rootLayout.addView(notifyView);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (rootLayout.indexOfChild(notifyView) != -1) rootLayout.removeView(notifyView);
        }, 3000);
    }

    private void handleCancel() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
