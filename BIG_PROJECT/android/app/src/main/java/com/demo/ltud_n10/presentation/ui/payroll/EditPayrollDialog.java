package com.demo.ltud_n10.presentation.ui.payroll;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.demo.ltud_n10.databinding.DialogEditPayrollBinding;
import com.demo.ltud_n10.domain.model.PayrollDetail;
import com.demo.ltud_n10.domain.repository.PayrollRepository;

import java.util.Locale;

public class EditPayrollDialog extends Dialog {

    private DialogEditPayrollBinding binding;
    private final PayrollDetail detail;
    private final PayrollRepository repository;
    private final OnEditSuccessListener listener;

    public interface OnEditSuccessListener {
        void onSuccess();
    }

    public EditPayrollDialog(@NonNull Context context, PayrollDetail detail, PayrollRepository repository, OnEditSuccessListener listener) {
        super(context);
        this.detail = detail;
        this.repository = repository;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogEditPayrollBinding.inflate(LayoutInflater.from(getContext()));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        binding.tvPayrollCode.setText("Mã lương: ML" + detail.getId());
        binding.tvEmployeeDisplay.setText(detail.getEmployeeId() + " - " + detail.getEmployeeName());
        binding.tvMonthDisplay.setText(detail.getMonth() + "/" + detail.getYear());

        binding.etBaseSalary.setText(String.valueOf((long)detail.getBaseSalary()));
        binding.etFactor.setText(String.valueOf(2.0)); // Placeholder
        binding.etHourlyRate.setText(String.valueOf(20)); // Placeholder
        binding.etHoursWorked.setText(String.valueOf(100)); // Placeholder
        binding.etBonus.setText(String.valueOf((long)detail.getBonus()));
        binding.etPenalty.setText(String.valueOf((long)detail.getPenalty()));

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
        binding.etBonus.addTextChangedListener(watcher);
        binding.etPenalty.addTextChangedListener(watcher);

        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnSave.setOnClickListener(v -> {
            // Update model and call repository
            detail.setBaseSalary(Double.parseDouble(binding.etBaseSalary.getText().toString().replaceAll("[^0-9]", "")));
            detail.setBonus(Double.parseDouble(binding.etBonus.getText().toString().replaceAll("[^0-9]", "")));
            detail.setPenalty(Double.parseDouble(binding.etPenalty.getText().toString().replaceAll("[^0-9]", "")));
            
            repository.updatePayrollDetail(detail).observeForever(resource -> {
                if (resource != null && resource.data != null) {
                    Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onSuccess();
                    dismiss();
                }
            });
        });
    }

    private void updateTotal() {
        try {
            double base = Double.parseDouble(binding.etBaseSalary.getText().toString().replaceAll("[^0-9]", ""));
            double bonus = Double.parseDouble(binding.etBonus.getText().toString().replaceAll("[^0-9]", ""));
            double penalty = Double.parseDouble(binding.etPenalty.getText().toString().replaceAll("[^0-9]", ""));
            double total = base + bonus - penalty;
            binding.tvTotalSalary.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", total));
        } catch (Exception e) {
            binding.tvTotalSalary.setText("0 VNĐ");
        }
    }
}
