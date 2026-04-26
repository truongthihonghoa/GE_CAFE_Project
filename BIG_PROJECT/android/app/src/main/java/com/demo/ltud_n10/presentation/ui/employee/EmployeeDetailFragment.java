package com.demo.ltud_n10.presentation.ui.employee;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.DialogCustomConfirmBinding;
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
        // Quan trọng: Dùng requireActivity() để dùng chung ViewModel với EmployeeListFragment
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
        if (title != null) {
            binding.tvTitle.setText(title);
        }

        String[] genders = {"Nam", "Nữ"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGender.setAdapter(genderAdapter);

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

        binding.tvNameError.setVisibility(View.GONE);
        binding.tvEmailError.setVisibility(View.GONE);
        binding.tvDobError.setVisibility(View.GONE);
        binding.tvCccdError.setVisibility(View.GONE);
        binding.tvPhoneError.setVisibility(View.GONE);
        binding.tvAddressError.setVisibility(View.GONE);
        binding.tvPositionError.setVisibility(View.GONE);
        binding.tvBankAccountError.setVisibility(View.GONE);
        binding.tvBranchIdError.setVisibility(View.GONE);
    }

    private void populateData() {
        binding.etName.setText(currentEmployee.getName());
        binding.etEmail.setText(currentEmployee.getEmail());
        binding.tvDob.setText(currentEmployee.getDob());
        binding.etCccd.setText(currentEmployee.getCccd());
        binding.etPhone.setText(currentEmployee.getPhone());
        binding.etAddress.setText(currentEmployee.getAddress());
        binding.etBankAccount.setText(currentEmployee.getBankAccount());
        binding.etBranchId.setText(currentEmployee.getBranchId());
        
        if ("Nữ".equals(currentEmployee.getGender())) binding.spinnerGender.setSelection(1);
        
        for (int i = 0; i < binding.spinnerPosition.getAdapter().getCount(); i++) {
            if (binding.spinnerPosition.getAdapter().getItem(i).toString().equals(currentEmployee.getPosition())) {
                binding.spinnerPosition.setSelection(i);
                break;
            }
        }
    }

    private void showDatePicker() {
        if (!isAdded()) return;
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    binding.tvDob.setText(date);
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveEmployee() {
        if (binding == null) return;
        
        binding.tvNameError.setVisibility(View.GONE);
        binding.tvEmailError.setVisibility(View.GONE);
        binding.tvDobError.setVisibility(View.GONE);
        binding.tvCccdError.setVisibility(View.GONE);
        binding.tvPhoneError.setVisibility(View.GONE);
        binding.tvAddressError.setVisibility(View.GONE);
        binding.tvBankAccountError.setVisibility(View.GONE);
        binding.tvBranchIdError.setVisibility(View.GONE);

        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String cccd = binding.etCccd.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String dob = binding.tvDob.getText().toString().trim();
        String bankAccount = binding.etBankAccount.getText().toString().trim();
        String branchId = binding.etBranchId.getText().toString().trim();
        String gender = binding.spinnerGender.getSelectedItem().toString();
        String position = binding.spinnerPosition.getSelectedItem().toString();

        boolean hasError = false;
        if (name.isEmpty()) { binding.tvNameError.setVisibility(View.VISIBLE); hasError = true; }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.tvEmailError.setVisibility(View.VISIBLE); hasError = true; }
        if (dob.isEmpty() || dob.equals("YYYY-MM-DD")) { binding.tvDobError.setVisibility(View.VISIBLE); hasError = true; }
        if (cccd.length() != 12) { binding.tvCccdError.setVisibility(View.VISIBLE); hasError = true; }
        if (phone.length() != 10) { binding.tvPhoneError.setVisibility(View.VISIBLE); hasError = true; }
        if (address.isEmpty()) { binding.tvAddressError.setVisibility(View.VISIBLE); hasError = true; }
        if (bankAccount.isEmpty()) { binding.tvBankAccountError.setVisibility(View.VISIBLE); hasError = true; }
        if (branchId.isEmpty()) { binding.tvBranchIdError.setVisibility(View.VISIBLE); hasError = true; }

        if (hasError) return;

        Employee employee = (currentEmployee != null) ? currentEmployee : new Employee();
        if (employee.getId() == null) {
            employee.setId("NV" + (System.currentTimeMillis() % 1000000));
        }
        employee.setName(name);
        employee.setEmail(email);
        employee.setCccd(cccd);
        employee.setPhone(phone);
        employee.setAddress(address);
        employee.setDob(dob);
        employee.setGender(gender);
        employee.setPosition(position);
        employee.setBankAccount(bankAccount);
        employee.setBranchId(branchId);
        employee.setStatus("Đang làm");

        if (currentEmployee == null) {
            viewModel.addEmployee(employee).observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessToast("Thêm nhân viên thành công");
                    viewModel.loadEmployees(); // Cập nhật lại danh sách tập trung
                    NavHostFragment.findNavController(this).popBackStack();
                } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                    showErrorDialog("THÔNG BÁO LỖI", "Lỗi: " + resource.message);
                }
            });
        } else {
            viewModel.updateEmployee(employee).observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessToast("Đã chỉnh sửa thông tin thành công");
                    viewModel.loadEmployees(); // Cập nhật lại danh sách tập trung
                    NavHostFragment.findNavController(this).popBackStack();
                } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                    showErrorDialog("THÔNG BÁO LỖI", "Lỗi: " + resource.message);
                }
            });
        }
    }

    private void handleBackAction() {
        showConfirmDialog("XÁC NHẬN HỦY", "Bạn có thông tin chưa lưu, xác nhận hủy ?", "Không", "Đồng ý", () -> {
            if (isAdded()) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        });
    }

    private void showSuccessToast(String msg) {
        if (!isAdded()) return;
        View layout = getLayoutInflater().inflate(R.layout.layout_custom_toast, null);
        ((TextView) layout.findViewById(R.id.tvMessage)).setText(msg);
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setView(layout);
        toast.show();
    }

    private void showConfirmDialog(String title, String message, String negativeText, String positiveText, Runnable onConfirm) {
        if (!isAdded()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCustomConfirmBinding dialogBinding = DialogCustomConfirmBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBinding.tvTitle.setText(title);
        dialogBinding.tvMessage.setText(message);
        dialogBinding.btnNegative.setText(negativeText);
        dialogBinding.btnPositive.setText(positiveText);
        dialogBinding.btnNegative.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnPositive.setOnClickListener(v -> { dialog.dismiss(); if (onConfirm != null) onConfirm.run(); });
        dialog.show();
    }

    private void showErrorDialog(String title, String message) {
        if (!isAdded()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCustomConfirmBinding dialogBinding = DialogCustomConfirmBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBinding.tvTitle.setText(title);
        dialogBinding.tvMessage.setText(message);
        dialogBinding.ivIcon.setImageResource(R.drawable.ic_error_x);
        dialogBinding.btnNegative.setText("Thoát");
        dialogBinding.btnPositive.setText("Quay lại");
        dialogBinding.btnNegative.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnPositive.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}
