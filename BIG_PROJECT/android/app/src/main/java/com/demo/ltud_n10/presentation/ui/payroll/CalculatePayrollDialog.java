package com.demo.ltud_n10.presentation.ui.payroll;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.databinding.DialogCalculatePayrollBinding;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;
import com.demo.ltud_n10.domain.repository.PayrollRepository;

import java.util.ArrayList;
import java.util.List;

public class CalculatePayrollDialog extends Dialog {

    private DialogCalculatePayrollBinding binding;
    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;
    private final String month;
    private final String year;
    private OnCalculateSuccessListener listener;
    private EmployeeSalarySelectAdapter adapter;

    public interface OnCalculateSuccessListener {
        void onSuccess();
    }

    public CalculatePayrollDialog(@NonNull Context context, 
                                  EmployeeRepository employeeRepository,
                                  PayrollRepository payrollRepository,
                                  String month, String year,
                                  OnCalculateSuccessListener listener) {
        super(context);
        this.employeeRepository = employeeRepository;
        this.payrollRepository = payrollRepository;
        this.month = month;
        this.year = year;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogCalculatePayrollBinding.inflate(LayoutInflater.from(getContext()));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

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
            
            payrollRepository.calculatePayroll(month, year, selectedIds).observeForever(resource -> {
                if (resource != null && resource.data != null) {
                    Toast.makeText(getContext(), "Tính lương thành công!", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onSuccess();
                    dismiss();
                }
            });
        });
    }

    private void loadEmployees() {
        employeeRepository.getEmployees().observeForever(resource -> {
            if (resource != null && resource.data != null) {
                adapter.setEmployees(resource.data);
                // Mặc định chọn tất cả khi mới load
                binding.cbSelectAll.setChecked(true);
            }
        });
    }
}
