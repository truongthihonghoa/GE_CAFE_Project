package com.demo.ltud_n10.presentation.ui.schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.domain.model.WorkShift;

public class WorkShiftAdapter extends ListAdapter<WorkShift, WorkShiftAdapter.ViewHolder> {

    private final OnShiftClickListener listener;

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
                return oldItem.isSent() == newItem.isSent() && 
                       oldItem.getEmployeeName().equals(newItem.getEmployeeName()) &&
                       oldItem.getStartTime().equals(newItem.getStartTime());
            }
        });
        this.listener = listener;
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
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_sent);
        } else {
            holder.tvStatus.setText("Chưa gửi");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_unsent);
        }

        holder.cbSelect.setOnCheckedChangeListener(null);
        // holder.cbSelect.setChecked(...); // Handle selection state from ViewModel later
        
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
