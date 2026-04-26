package com.demo.ltud_n10.presentation.ui.approval;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.FragmentApprovalBinding;
import com.demo.ltud_n10.databinding.LayoutSuccessNotificationBinding;
import com.demo.ltud_n10.domain.model.Request;
import com.demo.ltud_n10.domain.repository.RequestRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ApprovalFragment extends Fragment {

    private FragmentApprovalBinding binding;
    private ApprovalRequestAdapter adapter;
    private List<Request> allRequests = new ArrayList<>();
    private String currentType = "Đăng ký ca làm";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar filterStartDate = Calendar.getInstance();
    private Calendar filterEndDate = Calendar.getInstance();

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

        // Mặc định lọc trong khoảng thời gian rộng để thấy dữ liệu
        filterStartDate.add(Calendar.MONTH, -6);
        filterEndDate.add(Calendar.MONTH, 6);

        setupToolbar();
        setupRecyclerView();
        setupTabs();
        setupTimeFilter();
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
        adapter = new ApprovalRequestAdapter(new ApprovalRequestAdapter.OnActionListener() {
            @Override
            public void onApprove(Request request) {
                handleUpdateRequest(request, "Đã duyệt");
            }

            @Override
            public void onReject(Request request) {
                showRejectDialog(request);
            }

            @Override
            public void onTimeChanged(Request request) {
                handleUpdateRequest(request, request.getStatus());
            }
        });

        binding.rvRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRequests.setAdapter(adapter);
    }

    private void setupTimeFilter() {
        updateTimeLabels();
        binding.tvStartDate.setOnClickListener(v -> showDatePicker(true));
        binding.tvEndDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void updateTimeLabels() {
        binding.tvStartDate.setText(sdf.format(filterStartDate.getTime()));
        binding.tvEndDate.setText(sdf.format(filterEndDate.getTime()));
        filterData();
    }

    private void showDatePicker(boolean isStart) {
        Calendar c = isStart ? filterStartDate : filterEndDate;
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            c.set(year, month, dayOfMonth);
            updateTimeLabels();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupTabs() {
        binding.tabRegister.setOnClickListener(v -> {
            currentType = "Đăng ký ca làm";
            updateTabUI();
            filterData();
        });

        binding.tabLeave.setOnClickListener(v -> {
            currentType = "Nghỉ phép";
            updateTabUI();
            filterData();
        });
        
        updateTabUI(); // Khởi tạo UI tab ban đầu
    }

    private void updateTabUI() {
        int activeBg = Color.WHITE;
        int inactiveBg = Color.TRANSPARENT;
        int activeText = Color.parseColor("#1A1A1A");
        int inactiveText = Color.parseColor("#757575");

        if ("Đăng ký ca làm".equals(currentType)) {
            binding.tabRegister.setCardBackgroundColor(activeBg);
            binding.tabRegister.setCardElevation(2f);
            binding.tabLeave.setCardBackgroundColor(inactiveBg);
            binding.tabLeave.setCardElevation(0f);
            
            binding.tvTabRegister.setTextColor(activeText);
            binding.ivTabRegister.setColorFilter(activeText);
            binding.tvTabLeave.setTextColor(inactiveText);
            binding.ivTabLeave.setColorFilter(inactiveText);
        } else {
            binding.tabLeave.setCardBackgroundColor(activeBg);
            binding.tabLeave.setCardElevation(2f);
            binding.tabRegister.setCardBackgroundColor(inactiveBg);
            binding.tabRegister.setCardElevation(0f);

            binding.tvTabLeave.setTextColor(activeText);
            binding.ivTabLeave.setColorFilter(activeText);
            binding.tvTabRegister.setTextColor(inactiveText);
            binding.ivTabRegister.setColorFilter(inactiveText);
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
        String startStr = sdf.format(filterStartDate.getTime());
        String endStr = sdf.format(filterEndDate.getTime());

        List<Request> filtered = allRequests.stream()
                .filter(r -> r.getType().equals(currentType))
                .filter(r -> {
                    String reqDate = r.getStartDate();
                    return reqDate != null && reqDate.compareTo(startStr) >= 0 && reqDate.compareTo(endStr) <= 0;
                })
                .collect(Collectors.toList());
        adapter.setData(filtered);
    }

    private void handleUpdateRequest(Request request, String newStatus) {
        request.setStatus(newStatus);
        requestRepository.updateRequest(request).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                String message = "Đã duyệt".equals(newStatus) ? "Duyệt yêu cầu thành công" : 
                                "Bị từ chối".equals(newStatus) ? "Từ chối yêu cầu thành công" : "Cập nhật thành công";
                showSuccessNotification(message);
                loadData();
            }
        });
    }

    private void showSuccessNotification(String message) {
        if (getView() == null) return;
        LayoutSuccessNotificationBinding navBinding = LayoutSuccessNotificationBinding.inflate(getLayoutInflater());
        navBinding.tvMessage.setText(message);
        FrameLayout rootLayout = (FrameLayout) requireActivity().findViewById(android.R.id.content);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        // CHỈNH SỬA: Đưa thông báo lên phía trên cùng
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, 150, 0, 0); // Cách mép trên một khoảng để không bị che

        View notifyView = navBinding.getRoot();
        notifyView.setLayoutParams(params);
        rootLayout.addView(notifyView);
        new Handler(Looper.getMainLooper()).postDelayed(() -> rootLayout.removeView(notifyView), 3000);
    }

    private void showRejectDialog(Request request) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Từ chối yêu cầu")
                .setMessage("Xác nhận từ chối yêu cầu của nhân viên?")
                .setPositiveButton("Từ chối", (d, w) -> handleUpdateRequest(request, "Bị từ chối"))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
