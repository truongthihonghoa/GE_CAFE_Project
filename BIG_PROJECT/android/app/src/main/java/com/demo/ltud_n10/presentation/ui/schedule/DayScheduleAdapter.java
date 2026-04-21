package com.demo.ltud_n10.presentation.ui.schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.domain.model.WorkShift;

import java.util.ArrayList;
import java.util.List;

public class DayScheduleAdapter extends RecyclerView.Adapter<DayScheduleAdapter.ViewHolder> {

    private List<DaySchedule> daySchedules = new ArrayList<>();
    private List<String> selectedIds = new ArrayList<>();
    private final WorkShiftAdapter.OnShiftClickListener shiftClickListener;

    public DayScheduleAdapter(WorkShiftAdapter.OnShiftClickListener shiftClickListener) {
        this.shiftClickListener = shiftClickListener;
    }

    public void setDaySchedules(List<DaySchedule> daySchedules) {
        this.daySchedules = daySchedules;
        notifyDataSetChanged();
    }

    public void setSelectedIds(List<String> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DaySchedule day = daySchedules.get(position);
        holder.tvDayName.setText(day.getDayName());
        holder.tvDayNumber.setText(day.getDayNumber());

        if (day.getShifts().isEmpty()) {
            holder.tvNoShift.setVisibility(View.VISIBLE);
            holder.rvShifts.setVisibility(View.GONE);
        } else {
            holder.tvNoShift.setVisibility(View.GONE);
            holder.rvShifts.setVisibility(View.VISIBLE);
            
            WorkShiftAdapter adapter = new WorkShiftAdapter(shiftClickListener);
            holder.rvShifts.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.rvShifts.setAdapter(adapter);
            adapter.setSelectedIds(selectedIds);
            adapter.submitList(day.getShifts());
        }
    }

    @Override
    public int getItemCount() {
        return daySchedules.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayNumber, tvNoShift;
        RecyclerView rvShifts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            tvNoShift = itemView.findViewById(R.id.tvNoShift);
            rvShifts = itemView.findViewById(R.id.rvDayShifts);
        }
    }
}
