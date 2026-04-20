package com.demo.ltud_n10.presentation.ui.employee;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.FragmentEmployeePayrollBinding;
import com.demo.ltud_n10.domain.model.PayrollDetail;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.PayrollRepository;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EmployeePayrollFragment extends Fragment {

    private FragmentEmployeePayrollBinding binding;
    private Calendar calendar = Calendar.getInstance();

    @Inject
    PayrollRepository payrollRepository;

    @Inject
    AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmployeePayrollBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupListeners();
        loadData();
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void setupListeners() {
        binding.ivCalendar.setOnClickListener(v -> showMonthYearPicker());
        binding.btnClose.setOnClickListener(v -> {
            // Quay lại dashboard
            requireActivity().onBackPressed();
        });
    }

    private void showMonthYearPicker() {
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            loadData();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        
        dialog.setTitle("Chọn tháng lương");
        dialog.show();
    }

    private void loadData() {
        User user = authRepository.getCurrentUser().getValue();
        if (user == null) return;

        String month = String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.MONTH) + 1);
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        binding.tvMonthYear.setText(month + "/" + year);
        binding.tvEmployeeInfo.setText(user.getId() + " - " + user.getName());

        payrollRepository.getPayrollDetails("any").observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                PayrollDetail match = null;
                for (PayrollDetail d : resource.data) {
                    // Kiểm tra đúng tên nhân viên, đúng tháng và đúng năm
                    if (d.getEmployeeName().equals(user.getName()) && 
                        d.getMonth().equals(month) && 
                        d.getYear().equals(year)) {
                        match = d;
                        break;
                    }
                }
                
                if (match != null) {
                    updateUI(match);
                } else {
                    showEmptyUI();
                }
            }
        });
    }

    private void updateUI(PayrollDetail detail) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        binding.tvPayrollCode.setText("Mã lương: ML" + detail.getId());
        binding.tvBaseSalary.setText(formatter.format(detail.getBaseSalary()) + " VNĐ");
        binding.tvFactor.setText(String.format(Locale.getDefault(), "%.1f", detail.getFactor()));
        binding.tvHourlyRate.setText(String.valueOf((int)detail.getHourlyRate()));
        binding.tvHoursWorked.setText(String.valueOf((int)detail.getHoursWorked()));
        binding.tvBonus.setText(formatter.format(detail.getBonus()) + " VNĐ");
        binding.tvPenalty.setText(formatter.format(detail.getPenalty()) + " VNĐ");
        binding.tvTotalSalary.setText(formatter.format(detail.getTotalSalary()) + " VNĐ");
    }

    private void showEmptyUI() {
        binding.tvPayrollCode.setText("Mã lương: N/A");
        binding.tvBaseSalary.setText("0 VNĐ");
        binding.tvFactor.setText("0.0");
        binding.tvHourlyRate.setText("0");
        binding.tvHoursWorked.setText("0");
        binding.tvBonus.setText("0 VNĐ");
        binding.tvPenalty.setText("0 VNĐ");
        binding.tvTotalSalary.setText("0 VNĐ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
