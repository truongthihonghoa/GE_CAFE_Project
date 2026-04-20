package com.demo.ltud_n10.presentation.ui.payroll;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.FragmentPayrollDetailBinding;
import com.demo.ltud_n10.domain.model.PayrollDetail;
import com.demo.ltud_n10.domain.model.PayrollPeriod;
import com.demo.ltud_n10.domain.repository.PayrollRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PayrollDetailFragment extends Fragment {

    private FragmentPayrollDetailBinding binding;
    private PayrollDetailAdapter adapter;
    private PayrollPeriod currentPeriod;
    private List<PayrollDetail> fullList = new ArrayList<>();
    private String currentStatusFilter = "cho_duyet";

    @Inject
    PayrollRepository payrollRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPayrollDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            currentPeriod = (PayrollPeriod) getArguments().getSerializable("period");
        }

        setupUI();
        setupRecyclerView();
        loadData();
    }

    private void setupUI() {
        if (currentPeriod != null) {
            binding.tvDateFilter.setText("Tháng " + currentPeriod.getMonth() + "/" + currentPeriod.getYear());
        }

        binding.ivMenu.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.tvTabPending.setOnClickListener(v -> updateTab("cho_duyet"));
        binding.tvTabApproved.setOnClickListener(v -> updateTab("da_duyet"));
        
        binding.btnExport.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_payrollDetailFragment_to_payrollExportFragment);
        });
    }

    private void updateTab(String status) {
        currentStatusFilter = status;
        if (status.equals("cho_duyet")) {
            binding.tvTabPending.setBackgroundResource(R.drawable.bg_tab_selected);
            binding.tvTabPending.setTextColor(getResources().getColor(android.R.color.white));
            binding.tvTabApproved.setBackgroundResource(R.drawable.bg_tab_unselected);
            binding.tvTabApproved.setTextColor(getResources().getColor(R.color.black));
        } else {
            binding.tvTabApproved.setBackgroundResource(R.drawable.bg_tab_selected);
            binding.tvTabApproved.setTextColor(getResources().getColor(android.R.color.white));
            binding.tvTabPending.setBackgroundResource(R.drawable.bg_tab_unselected);
            binding.tvTabPending.setTextColor(getResources().getColor(R.color.black));
        }
        filter(binding.etSearch.getText().toString());
    }

    private void setupRecyclerView() {
        adapter = new PayrollDetailAdapter();
        adapter.setOnItemActionListener(new PayrollDetailAdapter.OnItemActionListener() {
            @Override
            public void onApprove(PayrollDetail detail) {
                detail.setStatus("da_duyet");
                updateDetail(detail);
            }

            @Override
            public void onReject(PayrollDetail detail) {
                detail.setStatus("da_tu_choi");
                updateDetail(detail);
            }

            @Override
            public void onEdit(PayrollDetail detail) {
                EditPayrollDialog dialog = new EditPayrollDialog(detail);
                dialog.setOnSavedListener(updated -> updateDetail(updated));
                dialog.show(getChildFragmentManager(), "EditPayroll");
            }

            @Override
            public void onDelete(PayrollDetail detail) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa bản ghi lương này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            payrollRepository.deletePayrollDetail(detail.getId()).observe(getViewLifecycleOwner(), resource -> {
                                if (resource.status == Resource.Status.SUCCESS) {
                                    Toast.makeText(requireContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                                    loadData();
                                }
                            });
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }

            @Override
            public void onView(PayrollDetail detail) {
                EditPayrollDialog dialog = new EditPayrollDialog(detail, true);
                dialog.show(getChildFragmentManager(), "ViewPayroll");
            }
        });

        binding.rvPayrollDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPayrollDetails.setAdapter(adapter);
    }

    private void updateDetail(PayrollDetail detail) {
        payrollRepository.updatePayrollDetail(detail).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                loadData();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        if (currentPeriod == null) return;
        
        payrollRepository.getPayrollDetails(currentPeriod.getId()).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                fullList = resource.data;
                filter(binding.etSearch.getText().toString());
            }
        });
    }

    private void filter(String query) {
        List<PayrollDetail> filtered = new ArrayList<>();
        for (PayrollDetail detail : fullList) {
            boolean matchesSearch = detail.getEmployeeName().toLowerCase().contains(query.toLowerCase()) ||
                                   detail.getId().toLowerCase().contains(query.toLowerCase());
            boolean matchesTab = detail.getStatus().equals(currentStatusFilter);
            
            if (matchesSearch && matchesTab) {
                filtered.add(detail);
            }
        }
        adapter.setItems(filtered);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
