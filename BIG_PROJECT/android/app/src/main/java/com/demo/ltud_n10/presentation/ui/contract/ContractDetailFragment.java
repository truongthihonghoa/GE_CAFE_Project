package com.demo.ltud_n10.presentation.ui.contract;

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
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.DialogCustomConfirmBinding;
import com.demo.ltud_n10.databinding.FragmentContractDetailBinding;
import com.demo.ltud_n10.domain.model.Contract;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.presentation.ui.employee.EmployeeViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContractDetailFragment extends Fragment {

    private FragmentContractDetailBinding binding;
    private ContractViewModel viewModel;
    private EmployeeViewModel employeeViewModel;
    private Contract currentContract;
    private String title;
    private List<Employee> employeeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContractDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ContractViewModel.class);
        employeeViewModel = new ViewModelProvider(requireActivity()).get(EmployeeViewModel.class);

        if (getArguments() != null) {
            currentContract = (Contract) getArguments().getSerializable("contract");
            title = getArguments().getString("title");
        }

        setupUI();
        if (currentContract == null) {
            loadEmployees();
        } else {
            populateData();
        }
    }

    private void setupUI() {
        if (title != null) {
            binding.tvTitle.setText(title);
        }

        // Chế độ chỉnh sửa: Hiển thị trường Read-Only, ẩn Spinner chọn nhân viên
        if (currentContract != null) {
            binding.llReadOnlyFields.setVisibility(View.VISIBLE);
            binding.tvEmployeeLabel.setVisibility(View.GONE);
            binding.spinnerEmployee.setVisibility(View.GONE);
            binding.btnSave.setText("Chỉnh sửa");
        } else {
            binding.llReadOnlyFields.setVisibility(View.GONE);
            binding.tvEmployeeLabel.setVisibility(View.VISIBLE);
            binding.spinnerEmployee.setVisibility(View.VISIBLE);
            binding.btnSave.setText("Lưu hợp đồng");
        }

        // Loại hợp đồng thực tế
        String[] types = {"Part time", "Full time"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerType.setAdapter(typeAdapter);

        // Chức vụ thực tế
        String[] positions = {"Quản lý", "Pha chế", "Phục vụ"};
        ArrayAdapter<String> posAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, positions);
        posAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPosition.setAdapter(posAdapter);

        binding.btnStartDatePicker.setOnClickListener(v -> showDatePicker(true));
        binding.btnEndDatePicker.setOnClickListener(v -> showDatePicker(false));

        binding.btnSave.setOnClickListener(v -> saveContract());
        binding.btnCancel.setOnClickListener(v -> handleCancel());

        binding.tvEmployeeError.setVisibility(View.GONE);
        binding.tvTypeError.setVisibility(View.GONE);
        binding.tvStartDateError.setVisibility(View.GONE);
        binding.tvEndDateError.setVisibility(View.GONE);
        binding.tvSalaryError.setVisibility(View.GONE);
        binding.tvPositionError.setVisibility(View.GONE);
    }

    private void loadEmployees() {
        employeeViewModel.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                employeeList = resource.data;
                List<String> names = new ArrayList<>();
                names.add("Chọn nhân viên");
                for (Employee e : employeeList) {
                    names.add(e.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerEmployee.setAdapter(adapter);
            }
        });
    }

    private void populateData() {
        // Điền dữ liệu vào các trường Read-Only
        binding.tvContractIdReadOnly.setText(currentContract.getId());
        binding.tvEmployeeIdReadOnly.setText(currentContract.getEmployeeId());
        binding.tvEmployeeNameReadOnly.setText(currentContract.getEmployeeName());

        // Điền dữ liệu vào các trường cho phép sửa
        binding.tvStartDate.setText(currentContract.getStartDate());
        binding.tvEndDate.setText(currentContract.getEndDate());
        binding.etSalary.setText(String.valueOf((long)currentContract.getSalary()));

        setSpinnerSelection(binding.spinnerType, currentContract.getType());
        setSpinnerSelection(binding.spinnerPosition, currentContract.getPosition());
    }

    private void setSpinnerSelection(android.widget.Spinner spinner, String value) {
        if (value == null) return;
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void showDatePicker(boolean isStart) {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            if (isStart) binding.tvStartDate.setText(date);
            else binding.tvEndDate.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveContract() {
        binding.tvEmployeeError.setVisibility(View.GONE);
        binding.tvTypeError.setVisibility(View.GONE);
        binding.tvStartDateError.setVisibility(View.GONE);
        binding.tvEndDateError.setVisibility(View.GONE);
        binding.tvSalaryError.setVisibility(View.GONE);
        binding.tvPositionError.setVisibility(View.GONE);

        String type = binding.spinnerType.getSelectedItem() != null ? binding.spinnerType.getSelectedItem().toString() : "";
        String startDate = binding.tvStartDate.getText().toString();
        String endDate = binding.tvEndDate.getText().toString();
        String salaryStr = binding.etSalary.getText().toString();
        String pos = binding.spinnerPosition.getSelectedItem() != null ? binding.spinnerPosition.getSelectedItem().toString() : "";

        boolean hasError = false;
        
        // Nếu là thêm mới thì mới kiểm tra Spinner nhân viên
        if (currentContract == null) {
            int empPos = binding.spinnerEmployee.getSelectedItemPosition();
            if (empPos <= 0) { binding.tvEmployeeError.setVisibility(View.VISIBLE); hasError = true; }
        }

        if (type.isEmpty()) { binding.tvTypeError.setVisibility(View.VISIBLE); hasError = true; }
        if (startDate.equals("YYYY-MM-DD") || startDate.isEmpty()) { binding.tvStartDateError.setVisibility(View.VISIBLE); hasError = true; }
        if (endDate.equals("YYYY-MM-DD") || endDate.isEmpty()) { binding.tvEndDateError.setVisibility(View.VISIBLE); hasError = true; }
        if (salaryStr.isEmpty()) { binding.tvSalaryError.setVisibility(View.VISIBLE); hasError = true; }
        if (pos.isEmpty()) { binding.tvPositionError.setVisibility(View.VISIBLE); hasError = true; }

        if (hasError) return;

        Contract contract = currentContract != null ? currentContract : new Contract();
        
        // Chỉ gán nhân viên nếu là thêm mới
        if (currentContract == null) {
            int empPos = binding.spinnerEmployee.getSelectedItemPosition();
            Employee selectedEmp = employeeList.get(empPos - 1);
            contract.setEmployeeId(selectedEmp.getId());
            contract.setEmployeeName(selectedEmp.getName());
        }

        contract.setType(type);
        contract.setStartDate(startDate);
        contract.setEndDate(endDate);
        contract.setSalary(Double.parseDouble(salaryStr));
        contract.setPosition(pos);
        contract.setStatus("Còn hiệu lực");

        if (currentContract == null) {
            viewModel.addContract(contract).observe(getViewLifecycleOwner(), r -> handleResult(r, "Tạo hợp đồng thành công"));
        } else {
            viewModel.updateContract(contract).observe(getViewLifecycleOwner(), r -> handleResult(r, "Cập nhật hợp đồng thành công"));
        }
    }

    private void handleResult(Resource<?> resource, String msg) {
        if (resource.status == Resource.Status.SUCCESS) {
            showSuccessToast(msg);
            viewModel.getContracts(); // Trigger refresh if managed centrally
            NavHostFragment.findNavController(this).popBackStack();
        } else if (resource.status == Resource.Status.ERROR) {
            showErrorDialog("LỖI", resource.message);
        }
    }

    private void showSuccessToast(String msg) {
        View layout = getLayoutInflater().inflate(R.layout.layout_custom_toast, null);
        ((TextView) layout.findViewById(R.id.tvMessage)).setText(msg);
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setView(layout);
        toast.show();
    }

    private void handleCancel() {
        showConfirmDialog("XÁC NHẬN HỦY", "Bạn có thông tin chưa lưu, xác nhận hủy ?", () -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCustomConfirmBinding dialogBinding = DialogCustomConfirmBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBinding.tvTitle.setText(title);
        dialogBinding.tvMessage.setText(message);
        dialogBinding.btnNegative.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnPositive.setOnClickListener(v -> { dialog.dismiss(); onConfirm.run(); });
        dialog.show();
    }

    private void showErrorDialog(String title, String message) {
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
