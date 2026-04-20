package com.demo.ltud_n10.presentation.ui.payroll;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.DialogCalculatePayrollBinding;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;
import com.demo.ltud_n10.domain.repository.PayrollRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CalculatePayrollDialog extends DialogFragment {

    private DialogCalculatePayrollBinding binding;
    
    @Inject
    EmployeeRepository employeeRepository;
    
    @Inject
    PayrollRepository payrollRepository;
    
    private int month;
    private int year;
    private OnCalculateSuccessListener listener;
    private EmployeeSalarySelectAdapter adapter;

    public interface OnCalculateSuccessListener {
        void onSuccess();
    }

    public CalculatePayrollDialog() {}

    public CalculatePayrollDialog(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public void setOnCalculateSuccessListener(OnCalculateSuccessListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogCalculatePayrollBinding.inflate(inflater, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        loadEmployees();
    }

    private void setupUI() {
        binding.tvSelectedMonth.setText(month + "/" + year);
        binding.ivClose.setOnClickListener(v -> dismiss());
        binding.btnCancel.setOnClickListener(v -> dismiss());
        
        adapter = new EmployeeSalarySelectAdapter();
        binding.rvEmployeeSelection.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvEmployeeSelection.setAdapter(adapter);

        binding.cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.selectAll(isChecked);
        });
        
        binding.btnCalculate.setOnClickListener(v -> {
            List<String> selectedIds = adapter.getSelectedEmployeeIds();
            
            if (selectedIds.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một nhân viên", Toast.LENGTH_SHORT).show();
                return;
            }
            
            binding.btnCalculate.setEnabled(false);
            binding.btnCalculate.setText("Đang xử lý...");
            
            payrollRepository.calculatePayroll(String.valueOf(month), String.valueOf(year), selectedIds).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.SUCCESS) {
                    Toast.makeText(getContext(), "Tính lương thành công!", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onSuccess();
                    dismiss();
                } else if (resource.status == Resource.Status.ERROR) {
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    binding.btnCalculate.setEnabled(true);
                    binding.btnCalculate.setText("Bắt đầu tính lương");
                }
            });
        });
    }

    private void loadEmployees() {
        employeeRepository.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.setEmployees(resource.data);
                binding.cbSelectAll.setChecked(true);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
