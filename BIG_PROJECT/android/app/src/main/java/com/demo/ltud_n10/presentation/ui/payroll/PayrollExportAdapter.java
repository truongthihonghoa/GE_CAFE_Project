package com.demo.ltud_n10.presentation.ui.payroll;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemPayrollExportBinding;
import com.demo.ltud_n10.domain.model.PayrollDetail;

import java.util.ArrayList;
import java.util.List;

public class PayrollExportAdapter extends RecyclerView.Adapter<PayrollExportAdapter.ViewHolder> {

    private List<PayrollDetail> items = new ArrayList<>();

    public void setItems(List<PayrollDetail> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPayrollExportBinding binding = ItemPayrollExportBinding.inflate(
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
        private final ItemPayrollExportBinding binding;

        ViewHolder(ItemPayrollExportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PayrollDetail item) {
            binding.tvEmployeeId.setText(item.getEmployeeId());
            binding.tvEmployeeName.setText(item.getEmployeeName());
            binding.tvSalary.setText(String.format("%,.0fđ", item.getTotalSalary()));
            binding.cbSelect.setChecked(true); // Default selected
        }
    }
}
