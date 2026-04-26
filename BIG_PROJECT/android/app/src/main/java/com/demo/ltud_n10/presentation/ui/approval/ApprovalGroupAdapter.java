package com.demo.ltud_n10.presentation.ui.approval;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemApprovalGroupBinding;
import com.demo.ltud_n10.domain.model.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ApprovalGroupAdapter extends RecyclerView.Adapter<ApprovalGroupAdapter.ViewHolder> {
    private List<String> days = new ArrayList<>();
    private Map<String, List<Request>> groupedItems = new TreeMap<>();
    private ApprovalRequestAdapter.OnActionListener actionListener;

    public void setOnActionListener(ApprovalRequestAdapter.OnActionListener listener) {
        this.actionListener = listener;
    }

    public void setData(List<Request> requests) {
        groupedItems.clear();
        days.clear();
        if (requests != null) {
            for (Request request : requests) {
                String day = request.getStartDate(); // Lấy ngày bắt đầu để nhóm
                if (day == null) day = "Chưa xác định";
                if (!groupedItems.containsKey(day)) {
                    groupedItems.put(day, new ArrayList<>());
                    days.add(day);
                }
                groupedItems.get(day).add(request);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemApprovalGroupBinding binding = ItemApprovalGroupBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String date = days.get(position);
        holder.bind(date, groupedItems.get(date));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemApprovalGroupBinding binding;

        ViewHolder(ItemApprovalGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String date, List<Request> requests) {
            // Hiển thị ngày (ví dụ: 2026-02-08 -> 08)
            try {
                String[] parts = date.split("-");
                binding.tvDayOfMonth.setText(parts[2]);
                binding.tvDayOfWeek.setText("Ngày " + parts[2]);
            } catch (Exception e) {
                binding.tvDayOfMonth.setText("??");
                binding.tvDayOfWeek.setText(date);
            }
            
            // SỬA LỖI: Truyền actionListener vào constructor của childAdapter
            ApprovalRequestAdapter childAdapter = new ApprovalRequestAdapter(actionListener);
            childAdapter.setData(requests);
            
            binding.rvRequestsInDay.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            binding.rvRequestsInDay.setAdapter(childAdapter);
            
            binding.tvNoRequests.setVisibility(requests.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
