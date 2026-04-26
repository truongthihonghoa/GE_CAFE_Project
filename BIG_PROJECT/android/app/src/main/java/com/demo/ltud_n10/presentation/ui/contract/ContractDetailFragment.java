package com.demo.ltud_n10.presentation.ui.contract;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContractDetailFragment extends Fragment {

    private FragmentContractDetailBinding binding;
    private ContractViewModel viewModel;
    private EmployeeViewModel employeeViewModel;
    private Contract currentContract;
    private String title;
    private List<Employee> employeeList = new ArrayList<>();

    private final Map<String, String> typeMap = new HashMap<>();
    private final Map<String, String> positionMap = new HashMap<>();

    public ContractDetailFragment() {
        typeMap.put("Full time", "FULLTIME");
        typeMap.put("Part time", "PARTTIME");

        positionMap.put("Quản lý", "QUAN_LY");
        positionMap.put("Pha chế", "PHA_CHE");
        positionMap.put("Phục vụ", "PHUC_VU");
        positionMap.put("Thu ngân", "THU_NGAN");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

        String[] types = {"Part time", "Full time"};
        setupSpinner(binding.spinnerType, types);

        String[] positions = {"Quản lý", "Pha chế", "Phục vụ", "Thu ngân"};
        setupSpinner(binding.spinnerPosition, positions);

        binding.btnStartDatePicker.setOnClickListener(v -> showDatePicker(true));
        binding.btnEndDatePicker.setOnClickListener(v -> showDatePicker(false));
        binding.btnSave.setOnClickListener(v -> saveContract());
        binding.btnCancel.setOnClickListener(v -> handleCancel());
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private List<String> hiredIds = new ArrayList<>();

    private void loadEmployees() {
        viewModel.getContracts().observe(getViewLifecycleOwner(), contractRes -> {
            if (contractRes.status == Resource.Status.SUCCESS && contractRes.data != null) {
                hiredIds.clear();
                for (Contract c : contractRes.data) {
                    if (c.getEmployeeId() != null) hiredIds.add(c.getEmployeeId().trim());
                }
                fetchEmployeeList();
            }
        });
    }

    private void fetchEmployeeList() {
        employeeViewModel.getEmployees().observe(getViewLifecycleOwner(), empRes -> {
            if (empRes.status == Resource.Status.SUCCESS && empRes.data != null) {
                employeeList.clear();
                List<String> names = new ArrayList<>();
                names.add("Chọn nhân viên");

                for (Employee e : empRes.data) {
                    String empId = e.getId() != null ? e.getId().trim() : "";
                    if (!hiredIds.contains(empId)) {
                        employeeList.add(e);
                        names.add(e.getName() + " (" + empId + ")");
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerEmployee.setAdapter(adapter);
            }
        });
    }

    private void populateData() {
        binding.tvContractIdReadOnly.setText(currentContract.getId());
        binding.tvEmployeeIdReadOnly.setText(currentContract.getEmployeeId());
        binding.tvEmployeeNameReadOnly.setText(currentContract.getEmployeeName());
        binding.tvStartDate.setText(currentContract.getStartDate());
        binding.tvEndDate.setText(currentContract.getEndDate());
        binding.etSalary.setText(String.valueOf((int) currentContract.getSalary()));
        binding.etHourlyRate.setText(String.valueOf((int) currentContract.getHourlyRate()));
        binding.etRequiredHours.setText(String.valueOf((int) currentContract.getRequiredHours()));

        setSpinnerSelection(binding.spinnerType, findDisplayByValue(typeMap, currentContract.getType()));
        setSpinnerSelection(binding.spinnerPosition, findDisplayByValue(positionMap, currentContract.getPosition()));
    }

    private String findDisplayByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) return entry.getKey();
        }
        return value;
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
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
        if (binding.spinnerType.getSelectedItem() == null || binding.spinnerPosition.getSelectedItem() == null) return;

        String displayType = binding.spinnerType.getSelectedItem().toString();
        String displayPos = binding.spinnerPosition.getSelectedItem().toString();
        String startDate = binding.tvStartDate.getText().toString();
        String endDate = binding.tvEndDate.getText().toString();
        String salaryStr = binding.etSalary.getText().toString();

        if (validateInputs(startDate, salaryStr)) {
            Contract contract = currentContract != null ? currentContract : new Contract();
            
            if (currentContract == null) {
                contract.setId("HD" + (System.currentTimeMillis() / 1000));
                int empPos = binding.spinnerEmployee.getSelectedItemPosition();
                Employee selectedEmp = employeeList.get(empPos - 1);
                contract.setEmployeeId(selectedEmp.getId());
                contract.setEmployeeName(selectedEmp.getName());
                contract.setBranchId(selectedEmp.getBranchId());
                contract.setStatus("CON_HAN");
            }

            contract.setType(typeMap.get(displayType));
            contract.setPosition(positionMap.get(displayPos));
            contract.setStartDate(startDate);
            contract.setEndDate(endDate.equals("YYYY-MM-DD") || endDate.isEmpty() ? null : endDate);
            
            try {
                String loai = displayType;
                // Xóa bỏ mọi dấu chấm/phẩy ngăn cách hàng nghìn trước khi parse số
                String salaryVal = binding.etSalary.getText().toString().trim().replaceAll("[.,]", "");
                String hourlyVal = binding.etHourlyRate.getText().toString().trim().replaceAll("[.,]", "");
                String requiredVal = binding.etRequiredHours.getText().toString().trim().replaceAll("[.,]", "");
                
                // Lấy và gán trực tiếp toàn bộ các trường lương từ giao diện
                contract.setSalary(salaryVal.isEmpty() ? 0 : Double.parseDouble(salaryVal));
                contract.setHourlyRate(hourlyVal.isEmpty() ? 0 : Double.parseDouble(hourlyVal));
                contract.setRequiredHours(requiredVal.isEmpty() ? 0 : Double.parseDouble(requiredVal));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (currentContract == null) {
                viewModel.addContract(contract).observe(getViewLifecycleOwner(), r -> handleResult(r, "Tạo hợp đồng lao động thành công"));
            } else {
                viewModel.updateContract(contract).observe(getViewLifecycleOwner(), r -> handleResult(r, "Chỉnh sửa hợp đồng lao động thành công"));
            }
        }
    }

    private boolean validateInputs(String start, String salary) {
        boolean valid = true;
        binding.tvEmployeeError.setVisibility(View.GONE);
        if (currentContract == null && binding.spinnerEmployee.getSelectedItemPosition() <= 0) {
            binding.tvEmployeeError.setVisibility(View.VISIBLE);
            valid = false;
        }
        if (start.isEmpty() || start.equals("YYYY-MM-DD")) {
            binding.tvStartDateError.setVisibility(View.VISIBLE);
            valid = false;
        }
        if (salary.isEmpty()) {
            binding.tvSalaryError.setVisibility(View.VISIBLE);
            valid = false;
        }
        return valid;
    }

    private void handleResult(Resource<?> resource, String msg) {
        if (resource.status == Resource.Status.SUCCESS) {
            showSuccessToast(msg);
            viewModel.getContracts();
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
        showConfirmDialog("XÁC NHẬN", "Hủy bỏ thao tác?", () -> NavHostFragment.findNavController(this).popBackStack());
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCustomConfirmBinding dialogBinding = DialogCustomConfirmBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBinding.tvTitle.setText(title);
        dialogBinding.tvMessage.setText(message);
        dialogBinding.btnPositive.setOnClickListener(v -> { dialog.dismiss(); onConfirm.run(); });
        dialogBinding.btnNegative.setOnClickListener(v -> dialog.dismiss());
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
        dialogBinding.btnPositive.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnNegative.setVisibility(View.GONE);
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
