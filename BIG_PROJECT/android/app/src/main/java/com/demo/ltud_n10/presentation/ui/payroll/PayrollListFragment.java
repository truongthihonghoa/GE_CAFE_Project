package com.demo.ltud_n10.presentation.ui.payroll;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.R;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.FragmentPayrollListBinding;
import com.demo.ltud_n10.domain.model.PayrollPeriod;
import com.demo.ltud_n10.domain.repository.PayrollRepository;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PayrollListFragment extends Fragment {

    private FragmentPayrollListBinding binding;
    private PayrollPeriodMainAdapter adapter;
    private int selectedMonth;
    private int selectedYear;

    @Inject
    PayrollRepository payrollRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPayrollListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar cal = Calendar.getInstance();
        selectedMonth = cal.get(Calendar.MONTH) + 1;
        selectedYear = cal.get(Calendar.YEAR);

        setupUI();
        setupRecyclerView();
        loadData();
    }

    private void setupUI() {
        updateDateUI();
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        binding.cvMonthSelector.setOnClickListener(v -> showMonthPicker());
        binding.cvYearSelector.setOnClickListener(v -> showYearPicker());
        
        binding.btnCalculateNew.setOnClickListener(v -> {
            // Hiển thị dialog tính lương mới
            CalculatePayrollDialog dialog = new CalculatePayrollDialog(selectedMonth, selectedYear);
            dialog.setOnCalculateSuccessListener(() -> loadData());
            dialog.show(getChildFragmentManager(), "CalculatePayroll");
        });
    }

    private void updateDateUI() {
        binding.tvSelectedMonth.setText("Tháng " + selectedMonth);
        binding.tvSelectedYear.setText(String.valueOf(selectedYear));
    }

    private void showMonthPicker() {
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) months[i] = "Tháng " + (i + 1);
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn tháng")
                .setItems(months, (d, which) -> {
                    selectedMonth = which + 1;
                    updateDateUI();
                    loadData();
                }).show();
    }

    private void showYearPicker() {
        String[] years = {"2024", "2025", "2026"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn năm")
                .setItems(years, (d, which) -> {
                    selectedYear = Integer.parseInt(years[which]);
                    updateDateUI();
                    loadData();
                }).show();
    }

    private void setupRecyclerView() {
        adapter = new PayrollPeriodMainAdapter();
        adapter.setOnItemClickListener(new PayrollPeriodMainAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(PayrollPeriod period) {
                Bundle args = new Bundle();
                args.putSerializable("period", period);
                Navigation.findNavController(requireView()).navigate(R.id.action_payrollListFragment_to_payrollDetailFragment, args);
            }

            @Override
            public void onDownloadClick(PayrollPeriod period) {
                Toast.makeText(requireContext(), "Đang tải bảng lương...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvPayrollPeriods.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPayrollPeriods.setAdapter(adapter);
    }

    private void loadData() {
        String m = String.valueOf(selectedMonth);
        String y = String.valueOf(selectedYear);
        
        payrollRepository.getPayrollPeriods(m, y).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.setItems(resource.data);
                
                double total = 0;
                for (PayrollPeriod p : resource.data) {
                    total += p.getTotalAmount();
                }
                binding.tvTotalAmount.setText(String.format("%,.0f đ", total));
                binding.tvPeriodCount.setText(resource.data.size() + " kỳ lương");
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
