package com.demo.ltud_n10.presentation.ui.contract;

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

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.FragmentContractDetailBinding;
import com.demo.ltud_n10.domain.model.Contract;

import java.util.Calendar;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContractDetailFragment extends Fragment {

    private FragmentContractDetailBinding binding;
    private ContractViewModel viewModel;
    private Contract currentContract;
    private String title;

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

        if (getArguments() != null) {
            currentContract = (Contract) getArguments().getSerializable("contract");
            title = getArguments().getString("title");
        }

        setupUI();
        if (currentContract != null) {
            populateData();
        }
    }

    private void setupUI() {
        binding.tvTitle.setText(title);
        binding.btnBack.setOnClickListener(v -> handleCancel());

        // Mock Data for Spinners
        String[] employees = {"Lê Văn C", "Phạm Thị D", "Hoàng Văn E"};
        ArrayAdapter<String> empAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, employees);
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEmployee.setAdapter(empAdapter);

        String[] types = {"Part time", "Full time"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerType.setAdapter(typeAdapter);

        String[] positions = {"Nhân viên pha chế", "Nhân viên phục vụ", "Giữ xe"};
        ArrayAdapter<String> posAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, positions);
        posAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPosition.setAdapter(posAdapter);

        binding.btnStartDatePicker.setOnClickListener(v -> showDatePicker(true));
        binding.btnEndDatePicker.setOnClickListener(v -> showDatePicker(false));

        binding.btnSave.setOnClickListener(v -> saveContract());
        binding.btnCancel.setOnClickListener(v -> handleCancel());
    }

    private void populateData() {
        binding.tvStartDate.setText(currentContract.getStartDate());
        binding.tvEndDate.setText(currentContract.getEndDate());
        binding.etSalary.setText(String.valueOf((long)currentContract.getSalary()));

        setSpinnerSelection(binding.spinnerEmployee, currentContract.getEmployeeName());
        setSpinnerSelection(binding.spinnerType, currentContract.getType());
        setSpinnerSelection(binding.spinnerPosition, currentContract.getPosition());
    }

    private void setSpinnerSelection(android.widget.Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int pos = adapter.getPosition(value);
        if (pos >= 0) spinner.setSelection(pos);
    }

    private void showDatePicker(boolean isStart) {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            String date = String.format("%02d/%02d/%04d", day, month + 1, year);
            if (isStart) binding.tvStartDate.setText(date);
            else binding.tvEndDate.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveContract() {
        String empName = binding.spinnerEmployee.getSelectedItem().toString();
        String type = binding.spinnerType.getSelectedItem().toString();
        String startDate = binding.tvStartDate.getText().toString();
        String endDate = binding.tvEndDate.getText().toString();
        String salaryStr = binding.etSalary.getText().toString();
        String pos = binding.spinnerPosition.getSelectedItem().toString();

        if (startDate.isEmpty() || endDate.isEmpty() || salaryStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông int", Toast.LENGTH_SHORT).show();
            return;
        }

        double salary = Double.parseDouble(salaryStr);
        if (salary <= 0) {
            Toast.makeText(requireContext(), "Mức lương phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        Contract contract = currentContract;
        if (contract == null) {
            contract = new Contract();
            contract.setId("HD" + System.currentTimeMillis() / 1000);
        }

        contract.setEmployeeName(empName);
        contract.setEmployeeId("NV" + UUID.randomUUID().toString().substring(0, 4));
        contract.setType(type);
        contract.setStartDate(startDate);
        contract.setEndDate(endDate);
        contract.setSalary(salary);
        contract.setPosition(pos);
        contract.setStatus("Còn hiệu lực");

        if (currentContract == null) {
            viewModel.addContract(contract).observe(getViewLifecycleOwner(), resource -> handleResult(resource, "Tạo hợp đồng lao động thành công"));
        } else {
            viewModel.updateContract(contract).observe(getViewLifecycleOwner(), resource -> handleResult(resource, "Chỉnh sửa hợp đồng lao động thành công"));
        }
    }

    private void handleResult(Resource<?> resource, String msg) {
        if (resource.status == Resource.Status.SUCCESS) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
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
