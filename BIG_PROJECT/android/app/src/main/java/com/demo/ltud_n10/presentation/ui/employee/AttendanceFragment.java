package com.demo.ltud_n10.presentation.ui.employee;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.DialogAttendanceFailureBinding;
import com.demo.ltud_n10.databinding.FragmentAttendanceBinding;
import com.demo.ltud_n10.databinding.ItemAttendanceHistoryBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AttendanceFragment extends Fragment {

    private FragmentAttendanceBinding binding;
    private String currentAction = "";
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAttendanceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupListeners();
        setupHistoryList();
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void setupListeners() {
        binding.btnStartShift.setOnClickListener(v -> showScanner("Bắt đầu ca"));
        binding.btnEndShift.setOnClickListener(v -> showScanner("Kết thúc ca"));
        binding.btnCancelScanner.setOnClickListener(v -> hideScanner());
        binding.btnViewAttendance.setOnClickListener(v -> showHistory());
        binding.btnCancelHistory.setOnClickListener(v -> hideHistory());
    }

    private void setupHistoryList() {
        HistoryAdapter adapter = new HistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(adapter);

        // Dữ liệu mẫu như trong hình
        List<HistoryItem> items = new ArrayList<>();
        items.add(new HistoryItem("Lê Văn C", "Bắt đầu ca"));
        items.add(new HistoryItem("Phạm Thị D", "Kết thúc ca"));
        items.add(new HistoryItem("Nguyễn Văn A", "Bắt đầu ca"));
        adapter.setItems(items);
    }

    private void showScanner(String title) {
        currentAction = title;
        binding.topBar.setVisibility(View.GONE);
        binding.layoutMain.setVisibility(View.GONE);
        binding.layoutHistory.setVisibility(View.GONE);
        binding.layoutScanner.setVisibility(View.VISIBLE);
        
        binding.tvScannerTitle.setText(title);
        binding.btnCancelScanner.setVisibility(View.VISIBLE);
        binding.btnViewAttendance.setVisibility(View.GONE);
        binding.cvSuccess.setVisibility(View.GONE);
        binding.cvFailure.setVisibility(View.GONE);

        handler.postDelayed(() -> {
            if (binding != null && binding.layoutScanner.getVisibility() == View.VISIBLE) {
                simulateScanResult();
            }
        }, 8000);
    }

    private void simulateScanResult() {
        boolean success = Math.random() > 0.3;

        if (success) {
            binding.cvSuccess.setVisibility(View.VISIBLE);
            binding.cvFailure.setVisibility(View.GONE);
            binding.btnCancelScanner.setVisibility(View.GONE);
            binding.btnViewAttendance.setVisibility(View.VISIBLE);
            
            updateMainStatus();
        } else {
            binding.cvSuccess.setVisibility(View.GONE);
            binding.cvFailure.setVisibility(View.VISIBLE);
            
            handler.postDelayed(() -> {
                if (binding != null && binding.cvFailure.getVisibility() == View.VISIBLE) {
                    showFailurePopup();
                }
            }, 5000);
        }
    }

    private void showFailurePopup() {
        if (getContext() == null) return;

        DialogAttendanceFailureBinding dialogBinding = DialogAttendanceFailureBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogBinding.btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            showScanner(currentAction);
        });

        dialogBinding.btnNo.setOnClickListener(v -> {
            dialog.dismiss();
            hideScanner();
        });

        dialog.show();
    }

    private void showHistory() {
        binding.layoutScanner.setVisibility(View.GONE);
        binding.layoutHistory.setVisibility(View.VISIBLE);
    }

    private void hideHistory() {
        binding.layoutHistory.setVisibility(View.GONE);
        hideScanner();
    }

    private void updateMainStatus() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        String status = currentAction.contains("Bắt đầu") ? 
                "Đã vào ca lúc " + currentTime : 
                "Đã kết thúc ca lúc " + currentTime;
        
        binding.tvAttendanceStatus.setText(status);
        binding.tvAttendanceStatus.setTextColor(Color.WHITE);
    }

    private void hideScanner() {
        binding.topBar.setVisibility(View.VISIBLE);
        binding.layoutMain.setVisibility(View.VISIBLE);
        binding.layoutScanner.setVisibility(View.GONE);
        binding.layoutHistory.setVisibility(View.GONE);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }

    // Inner classes for History List
    static class HistoryItem {
        String name, action;
        HistoryItem(String name, String action) { this.name = name; this.action = action; }
    }

    class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private List<HistoryItem> items = new ArrayList<>();
        void setItems(List<HistoryItem> items) { this.items = items; notifyDataSetChanged(); }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ItemAttendanceHistoryBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HistoryItem item = items.get(position);
            holder.binding.tvName.setText(item.name);
            holder.binding.tvAction.setText(item.action);
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ItemAttendanceHistoryBinding binding;
            ViewHolder(ItemAttendanceHistoryBinding binding) { super(binding.getRoot()); this.binding = binding; }
        }
    }
}
