package com.demo.ltud_n10.presentation.ui.schedule;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.domain.model.WorkShift;

import java.util.ArrayList;
import java.util.List;

public class WorkShiftAdapter extends ListAdapter<WorkShift, WorkShiftAdapter.ViewHolder> {

    private final OnShiftClickListener listener;
    private List<String> selectedIds = new ArrayList<>();

    public interface OnShiftClickListener {
        void onShiftClick(WorkShift shift);
        void onMoreClick(WorkShift shift, View view);
        void onToggleSelect(WorkShift shift);
    }

    public WorkShiftAdapter(OnShiftClickListener listener) {
        super(new DiffUtil.ItemCallback<WorkShift>() {
            @Override
            public boolean areItemsTheSame(@NonNull WorkShift oldItem, @NonNull WorkShift newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull WorkShift oldItem, @NonNull WorkShift newItem) {
                return oldItem.getStatus().equals(newItem.getStatus()) && 
                       oldItem.getEmployeeName().equals(newItem.getEmployeeName()) &&
                       oldItem.getStartTime().equals(newItem.getStartTime());
            }
        });
        this.listener = listener;
    }

    public void setSelectedIds(List<String> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_shift, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkShift shift = getItem(position);
        holder.tvName.setText(shift.getEmployeeName());
        holder.tvTime.setText(shift.getStartTime() + " - " + shift.getEndTime());
        
        // TRẠNG THÁI "Đã gửi"
        if ("Đã gửi".equals(shift.getStatus())) {
            holder.tvStatus.setText("Đã gửi");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_sent);
            // Màu xanh lá đậm như hình
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1B431C"))); 
            holder.cbSelect.setVisibility(View.GONE); // Ẩn checkbox
            
            // Màu nền card cho "Đã gửi" (Xanh lá nhạt)
            ((com.google.android.material.card.MaterialCardView) holder.itemView).setCardBackgroundColor(Color.parseColor("#E8F8EF"));
        } 
        // TRẠNG THÁI "Chưa gửi"
        else {
            holder.tvStatus.setText("Chưa gửi");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_unsent);
            // Màu đỏ rực như hình
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF0000"))); 
            holder.cbSelect.setVisibility(View.VISIBLE); // Hiện checkbox
            
            // Màu nền card cho "Chưa gửi" (Trắng hoặc Xám rất nhạt)
            ((com.google.android.material.card.MaterialCardView) holder.itemView).setCardBackgroundColor(Color.WHITE);
        }

        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(selectedIds.contains(shift.getId()));
        
        holder.cbSelect.setOnClickListener(v -> listener.onToggleSelect(shift));
        holder.btnMore.setOnClickListener(v -> listener.onMoreClick(shift, v));
        holder.itemView.setOnClickListener(v -> listener.onShiftClick(shift));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvStatus;
        CheckBox cbSelect;
        View btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEmployeeName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
