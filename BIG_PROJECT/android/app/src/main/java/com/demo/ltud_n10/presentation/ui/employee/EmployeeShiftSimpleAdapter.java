package com.demo.ltud_n10.presentation.ui.employee;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemEmployeeShiftSimpleBinding;
import com.demo.ltud_n10.domain.model.WorkShift;

import java.util.ArrayList;
import java.util.List;

public class EmployeeShiftSimpleAdapter extends RecyclerView.Adapter<EmployeeShiftSimpleAdapter.ViewHolder> {

    private List<WorkShift> items = new ArrayList<>();

    public void setItems(List<WorkShift> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEmployeeShiftSimpleBinding binding = ItemEmployeeShiftSimpleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemEmployeeShiftSimpleBinding binding;

        ViewHolder(ItemEmployeeShiftSimpleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(WorkShift shift) {
            binding.tvEmployeeName.setText(shift.getEmployeeName());
            binding.tvShiftTime.setText(shift.getStartTime() + " - " + shift.getEndTime());
        }
    }
}
