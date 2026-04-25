package com.demo.ltud_n10.presentation.ui.employee;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.DialogCustomConfirmBinding;
import com.demo.ltud_n10.databinding.FragmentEmployeeDetailBinding;
import com.demo.ltud_n10.domain.model.Employee;
import com.google.android.material.button.MaterialButton;

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
        binding.btnBack.setOnClickListener(v -> showCancelConfirmDialog());
        if (title != null) {
            binding.tvTitle.setText(title);
        }

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
        binding.btnCancel.setOnClickListener(v -> showCancelConfirmDialog());
        
        if (currentEmployee != null) {
            binding.btnSave.setText("Chỉnh sửa");
        }

        // Hide errors by default
        binding.tvNameError.setVisibility(View.GONE);
        binding.tvDobError.setVisibility(View.GONE);
        binding.tvCccdError.setVisibility(View.GONE);
        binding.tvPhoneError.setVisibility(View.GONE);
        binding.tvAddressError.setVisibility(View.GONE);
        binding.tvPositionError.setVisibility(View.GONE);
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
                    String date = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
                    binding.tvDob.setText(date);
                }, year, month, day);
        datePickerDialog.show();
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

    private void saveEmployee() {
        // Reset errors
        binding.tvNameError.setVisibility(View.GONE);
        binding.tvDobError.setVisibility(View.GONE);
        binding.tvCccdError.setVisibility(View.GONE);
        binding.tvPhoneError.setVisibility(View.GONE);
        binding.tvAddressError.setVisibility(View.GONE);
        binding.tvPositionError.setVisibility(View.GONE);

        String name = binding.etName.getText().toString();
        String cccd = binding.etCccd.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String address = binding.etAddress.getText().toString();
        String dob = binding.tvDob.getText().toString();
        String gender = binding.spinnerGender.getSelectedItem().toString();
        String position = binding.spinnerPosition.getSelectedItem().toString();

        boolean hasError = false;

        if (name.isEmpty()) {
            binding.tvNameError.setVisibility(View.VISIBLE);
            hasError = true;
        }
        if (dob.isEmpty() || dob.equals("06/02/2026")) {
            binding.tvDobError.setVisibility(View.VISIBLE);
            hasError = true;
        }
        if (cccd.isEmpty() || cccd.length() != 12) {
            binding.tvCccdError.setVisibility(View.VISIBLE);
            hasError = true;
        }
        if (phone.isEmpty() || phone.length() != 10) {
            binding.tvPhoneError.setVisibility(View.VISIBLE);
            hasError = true;
        }
        if (address.isEmpty()) {
            binding.tvAddressError.setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (hasError) return;

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
                    Toast.makeText(requireContext(), "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                    showSuccessDialog("Thêm nhân viên thành công");
                } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                    showErrorDialog("THÔNG BÁO LỖI", "Lỗi hệ thống. Vui lòng thử lại sau !");
                }
            });
        } else {
            viewModel.updateEmployee(employee).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessDialog("Đã chỉnh sửa thông tin nhân viên thành công");
                } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                    showErrorDialog("THÔNG BÁO LỖI", "Lỗi hệ thống. Vui lòng thử lại sau !");
                }
            });
        }
    }

    private void showSuccessDialog(String msg) {
        View layout = getLayoutInflater().inflate(R.layout.layout_custom_toast, null);
        TextView tvMessage = layout.findViewById(R.id.tvMessage);
        tvMessage.setText(msg);

        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setView(layout);
        toast.show();

        Navigation.findNavController(requireView()).popBackStack();
    }

    private void handleBackAction() {
        showConfirmDialog("XÁC NHẬN HỦY", "Bạn có thông tin chưa lưu, xác nhận hủy ?", () -> {
            Navigation.findNavController(requireView()).popBackStack();
        });
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCustomConfirmBinding dialogBinding = DialogCustomConfirmBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogBinding.tvTitle.setText(title);
        dialogBinding.tvMessage.setText(message);
        dialogBinding.ivIcon.setImageResource(R.drawable.ic_warning_outline);

        dialogBinding.btnNegative.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnPositive.setOnClickListener(v -> {
            dialog.dismiss();
            onConfirm.run();
        });

        dialog.show();
    }

    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCustomConfirmBinding dialogBinding = DialogCustomConfirmBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
