package com.demo.ltud_n10.presentation.ui.employee;

import android.app.DatePickerDialog;
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

import com.demo.ltud_n10.databinding.FragmentEmployeeDetailBinding;
import com.demo.ltud_n10.domain.model.Employee;

import java.util.Calendar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EmployeeDetailFragment extends Fragment {

    private FragmentEmployeeDetailBinding binding;
    private EmployeeViewModel viewModel;
    private Employee currentEmployee;
    private String title;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmployeeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EmployeeViewModel.class);

        if (getArguments() != null) {
            currentEmployee = (Employee) getArguments().getSerializable("employee");
            title = getArguments().getString("title");
        }

        setupUI();
        if (currentEmployee != null) {
            populateData();
        }
    }

    private void setupUI() {
        binding.tvTitle.setText(title);
        binding.btnBack.setOnClickListener(v -> handleBackAction());

        // Setup Gender Spinner
        String[] genders = {"Nam", "Nữ"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGender.setAdapter(genderAdapter);

        // Setup Position Spinner
        String[] positions = {"Quản lý", "Pha chế", "Phục vụ"};
        ArrayAdapter<String> posAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, positions);
        posAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPosition.setAdapter(posAdapter);

        binding.btnDatePicker.setOnClickListener(v -> showDatePicker());

        binding.btnSave.setOnClickListener(v -> saveEmployee());
        binding.btnCancel.setOnClickListener(v -> handleBackAction());
        
        if (currentEmployee != null) {
            binding.btnSave.setText("Chỉnh sửa");
        }
    }

    private void populateData() {
        binding.etName.setText(currentEmployee.getName());
        binding.tvDob.setText(currentEmployee.getDob());
        binding.etCccd.setText(currentEmployee.getCccd());
        binding.etPhone.setText(currentEmployee.getPhone());
        binding.etAddress.setText(currentEmployee.getAddress());
        
        // Set spinner selections
        if ("Nữ".equals(currentEmployee.getGender())) binding.spinnerGender.setSelection(1);
        
        for (int i = 0; i < binding.spinnerPosition.getAdapter().getCount(); i++) {
            if (binding.spinnerPosition.getAdapter().getItem(i).toString().equals(currentEmployee.getPosition())) {
                binding.spinnerPosition.setSelection(i);
                break;
            }
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    binding.tvDob.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveEmployee() {
        String name = binding.etName.getText().toString();
        String cccd = binding.etCccd.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String address = binding.etAddress.getText().toString();
        String dob = binding.tvDob.getText().toString();
        String gender = binding.spinnerGender.getSelectedItem().toString();
        String position = binding.spinnerPosition.getSelectedItem().toString();

        if (name.isEmpty() || cccd.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Employee employee = (currentEmployee != null) ? currentEmployee : new Employee();
        employee.setName(name);
        employee.setCccd(cccd);
        employee.setPhone(phone);
        employee.setAddress(address);
        employee.setDob(dob);
        employee.setGender(gender);
        employee.setPosition(position);
        employee.setStatus("Đang làm");

        if (currentEmployee == null) {
            viewModel.addEmployee(employee).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessDialog("Thêm nhân viên thành công");
                }
            });
        } else {
            viewModel.updateEmployee(employee).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Cập nhật thông tin nhân viên thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                }
            });
        }
    }

    private void showSuccessDialog(String msg) {
        // Simple Toast for now, can be replaced with custom dialog if needed
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).popBackStack();
    }

    private void handleBackAction() {
        new AlertDialog.Builder(requireContext())
                .setTitle("XÁC NHẬN HỦY")
                .setMessage("Bạn có thông tin thay đổi chưa được lưu, xác nhận hủy?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    Navigation.findNavController(requireView()).popBackStack();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
