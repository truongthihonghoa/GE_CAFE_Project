package com.demo.ltud_n10.presentation.ui.payroll;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.FragmentPayrollListBinding;
import com.demo.ltud_n10.domain.model.PayrollPeriod;
import com.demo.ltud_n10.domain.repository.PayrollRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PayrollListFragment extends Fragment {

    private FragmentPayrollListBinding binding;
    private PayrollPeriodMainAdapter adapter;

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

        setupToolbar();
        setupRecyclerView();
        loadData();
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new PayrollPeriodMainAdapter();
        // Không còn điều hướng sang chi tiết
        adapter.setOnItemClickListener(new PayrollPeriodMainAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(PayrollPeriod period) {
                // Chỉ xem, không xử lý gì thêm theo yêu cầu
            }

            @Override
            public void onDownloadClick(PayrollPeriod period) {
                // Chỉ xem giao diện
            }
        });

        binding.rvPayrollPeriods.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPayrollPeriods.setAdapter(adapter);
    }

    private void loadData() {
        payrollRepository.getPayrollPeriods("02", "2026").observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                adapter.setItems(resource.data);
                
                double total = 0;
                for (PayrollPeriod p : resource.data) {
                    total += p.getTotalAmount();
                }
                binding.tvTotalAmount.setText(String.format("%,.0f đ", total));
                binding.tvPeriodCount.setText(resource.data.size() + " kỳ lương");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
