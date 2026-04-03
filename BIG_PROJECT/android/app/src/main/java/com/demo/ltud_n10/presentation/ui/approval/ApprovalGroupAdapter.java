package com.demo.ltud_n10.presentation.ui.approval;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemApprovalGroupBinding;
import com.demo.ltud_n10.domain.model.WorkShift;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ApprovalGroupAdapter extends RecyclerView.Adapter<ApprovalGroupAdapter.ViewHolder> {

    private List<String> days = new ArrayList<>();
    private Map<String, List<WorkShift>> groupedItems = new TreeMap<>();
    private ApprovalRequestAdapter.OnActionListener actionListener;

    public void setOnActionListener(ApprovalRequestAdapter.OnActionListener listener) {
        this.actionListener = listener;
    }

    public void setData(List<WorkShift> shifts) {
        groupedItems.clear();
        days.clear();
        for (WorkShift shift : shifts) {
            String day = shift.getDate();
            if (!groupedItems.containsKey(day)) {
                groupedItems.put(day, new ArrayList<>());
                days.add(day);
            }
            groupedItems.get(day).add(shift);
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

        void bind(String date, List<WorkShift> shifts) {
            binding.tvDayOfMonth.setText(date.split("/")[0]);
            // Simplified day of week for demo
            binding.tvDayOfWeek.setText("Thứ " + (getAdapterPosition() + 2));
            
            ApprovalRequestAdapter childAdapter = new ApprovalRequestAdapter();
            childAdapter.setOnActionListener(actionListener);
            childAdapter.setItems(shifts);
            
            binding.rvRequestsInDay.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            binding.rvRequestsInDay.setAdapter(childAdapter);
            
            binding.tvNoRequests.setVisibility(shifts.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
