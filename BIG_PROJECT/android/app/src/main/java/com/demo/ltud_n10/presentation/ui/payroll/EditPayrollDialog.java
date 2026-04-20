package com.demo.ltud_n10.presentation.ui.payroll;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.DialogEditPayrollBinding;
import com.demo.ltud_n10.domain.model.PayrollDetail;

import java.util.Locale;

public class EditPayrollDialog extends DialogFragment {

    private DialogEditPayrollBinding binding;
    private PayrollDetail detail;
    private OnSavedListener listener;
    private boolean isViewOnly = false;

    public interface OnSavedListener {
        void onSaved(PayrollDetail updated);
    }

    public EditPayrollDialog() {}

    public EditPayrollDialog(PayrollDetail detail) {
        this.detail = detail;
    }

    public EditPayrollDialog(PayrollDetail detail, boolean isViewOnly) {
        this.detail = detail;
        this.isViewOnly = isViewOnly;
    }

    public void setOnSavedListener(OnSavedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogEditPayrollBinding.inflate(inflater, container, false);
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
        setupListeners();
    }

    private void setupUI() {
        binding.tvPayrollCode.setText("Mã lương: ML" + detail.getId());
        binding.tvEmployeeDisplay.setText(detail.getEmployeeId() + " - " + detail.getEmployeeName());
        binding.tvMonthDisplay.setText(detail.getMonth() + "/" + detail.getYear());

        binding.etBaseSalary.setText(String.format("%.0f", detail.getBaseSalary()));
        binding.etHourlyRate.setText(String.format("%.0f", detail.getHourlyRate()));
        binding.etHoursWorked.setText(String.format("%.1f", detail.getHoursWorked()));
        binding.etBonus.setText(String.format("%.0f", detail.getBonus()));
        binding.etPenalty.setText(String.format("%.0f", detail.getPenalty()));

        if (isViewOnly) {
            binding.etBaseSalary.setEnabled(false);
            binding.etHourlyRate.setEnabled(false);
            binding.etHoursWorked.setEnabled(false);
            binding.etBonus.setEnabled(false);
            binding.etPenalty.setEnabled(false);
            binding.btnSave.setVisibility(View.GONE);
            binding.btnCancel.setText("Đóng");
        }

        updateTotal();
    }

    private void setupListeners() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateTotal();
            }
        };

        binding.etBaseSalary.addTextChangedListener(watcher);
        binding.etHourlyRate.addTextChangedListener(watcher);
        binding.etHoursWorked.addTextChangedListener(watcher);
        binding.etBonus.addTextChangedListener(watcher);
        binding.etPenalty.addTextChangedListener(watcher);

        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnSave.setOnClickListener(v -> {
            try {
                detail.setBaseSalary(Double.parseDouble(binding.etBaseSalary.getText().toString()));
                detail.setHourlyRate(Double.parseDouble(binding.etHourlyRate.getText().toString()));
                detail.setHoursWorked(Double.parseDouble(binding.etHoursWorked.getText().toString()));
                detail.setBonus(Double.parseDouble(binding.etBonus.getText().toString()));
                detail.setPenalty(Double.parseDouble(binding.etPenalty.getText().toString()));
                
                if (listener != null) listener.onSaved(detail);
                dismiss();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotal() {
        try {
            double base = getDouble(binding.etBaseSalary.getText().toString());
            double hourly = getDouble(binding.etHourlyRate.getText().toString());
            double hours = getDouble(binding.etHoursWorked.getText().toString());
            double bonus = getDouble(binding.etBonus.getText().toString());
            double penalty = getDouble(binding.etPenalty.getText().toString());
            
            double total = base + (hourly * hours) + bonus - penalty;
            binding.tvTotalSalary.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", total));
        } catch (Exception e) {
            binding.tvTotalSalary.setText("0 VNĐ");
        }
    }

    private double getDouble(String s) {
        if (s == null || s.isEmpty()) return 0;
        try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
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
