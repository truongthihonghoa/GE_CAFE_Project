package com.demo.ltud_n10.presentation.ui.schedule;

import android.content.res.ColorStateList;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkShiftAdapter extends ListAdapter<WorkShift, WorkShiftAdapter.ViewHolder> {

    private final OnShiftClickListener listener;
    private Set<String> selectedIds = new HashSet<>();

    public interface OnShiftClickListener {
        void onShiftClick(WorkShift shift);
        void onMoreClick(WorkShift shift, View view);
        void onToggleSelect(String shiftId, boolean isChecked);
    }

    public WorkShiftAdapter(OnShiftClickListener listener) {
        super(new DiffUtil.ItemCallback<WorkShift>() {
            @Override
            public boolean areItemsTheSame(@NonNull WorkShift oldItem, @NonNull WorkShift newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull WorkShift oldItem, @NonNull WorkShift newItem) {
                return oldItem.isSent() == newItem.isSent() && 
                       oldItem.getEmployeeName().equals(newItem.getEmployeeName()) &&
                       oldItem.getStartTime().equals(newItem.getStartTime());
            }
        });
        this.listener = listener;
    }

    public void setSelectedIds(List<String> ids) {
        this.selectedIds = new HashSet<>(ids);
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
        
        if (shift.isSent()) {
            holder.tvStatus.setText("Đã gửi");
            holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0A4D1E")));
        } else {
            holder.tvStatus.setText("Chưa gửi");
            holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
        }

        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(selectedIds.contains(shift.getId()));
        
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onToggleSelect(shift.getId(), isChecked);
        });

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
