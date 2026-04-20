package com.demo.ltud_n10.presentation.ui.approval;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.FragmentApprovalBinding;
import com.demo.ltud_n10.domain.model.Request;
import com.demo.ltud_n10.domain.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ApprovalFragment extends Fragment {

    private FragmentApprovalBinding binding;
    private ApprovalGroupAdapter adapter;
    private List<Request> allRequests = new ArrayList<>();
    private String currentType = "Đăng ký ca";

    @Inject
    RequestRepository requestRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentApprovalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
        setupTabs();
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
        adapter = new ApprovalGroupAdapter();
        adapter.setOnActionListener(new ApprovalRequestAdapter.OnActionListener() {
            @Override
            public void onApprove(Request request) {
                handleApprove(request);
            }

            @Override
            public void onReject(Request request) {
                handleReject(request);
            }
        });

        binding.rvRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRequests.setAdapter(adapter);
    }

    private void setupTabs() {
        binding.tabRegister.setOnClickListener(v -> {
            currentType = "Đăng ký ca";
            updateTabUI();
            filterData();
        });

        binding.tabLeave.setOnClickListener(v -> {
            currentType = "Nghỉ phép";
            updateTabUI();
            filterData();
        });
    }

    private void updateTabUI() {
        if ("Đăng ký ca".equals(currentType)) {
            binding.tabRegister.setCardBackgroundColor(Color.WHITE);
            binding.tabRegister.setCardElevation(4f);
            binding.tabLeave.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tabLeave.setCardElevation(0f);
        } else {
            binding.tabLeave.setCardBackgroundColor(Color.WHITE);
            binding.tabLeave.setCardElevation(4f);
            binding.tabRegister.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tabRegister.setCardElevation(0f);
        }
    }

    private void loadData() {
        requestRepository.getRequests().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                allRequests = resource.data;
                filterData();
            }
        });
    }

    private void filterData() {
        List<Request> filtered = allRequests.stream()
                .filter(r -> r.getType().contains(currentType))
                .collect(Collectors.toList());
        adapter.setData(filtered);
    }

    private void handleApprove(Request request) {
        requestRepository.updateRequestStatus(request.getId(), "Đã duyệt").observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                Toast.makeText(requireContext(), "Đã duyệt yêu cầu của " + request.getEmployeeName(), Toast.LENGTH_SHORT).show();
                loadData(); // Reload to get fresh status
            }
        });
    }

    private void handleReject(Request request) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Từ chối yêu cầu")
                .setMessage("Bạn có chắc chắn muốn từ chối yêu cầu này?")
                .setPositiveButton("Từ chối", (d, w) -> {
                    requestRepository.updateRequestStatus(request.getId(), "Bị từ chối").observe(getViewLifecycleOwner(), resource -> {
                        if (resource != null && resource.data != null) {
                            Toast.makeText(requireContext(), "Đã từ chối yêu cầu", Toast.LENGTH_SHORT).show();
                            loadData();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
