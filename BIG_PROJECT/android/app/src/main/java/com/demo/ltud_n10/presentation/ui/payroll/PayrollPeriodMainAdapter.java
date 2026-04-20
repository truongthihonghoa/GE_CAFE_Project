package com.demo.ltud_n10.presentation.ui.payroll;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemPayrollPeriodMainBinding;
import com.demo.ltud_n10.domain.model.PayrollPeriod;

import java.util.ArrayList;
import java.util.List;

public class PayrollPeriodMainAdapter extends RecyclerView.Adapter<PayrollPeriodMainAdapter.ViewHolder> {

    private List<PayrollPeriod> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onViewClick(PayrollPeriod period);
        void onDownloadClick(PayrollPeriod period);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PayrollPeriod> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPayrollPeriodMainBinding binding = ItemPayrollPeriodMainBinding.inflate(
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
        private final ItemPayrollPeriodMainBinding binding;

        ViewHolder(ItemPayrollPeriodMainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PayrollPeriod period) {
            binding.tvTitle.setText("Kỳ lương tháng " + period.getMonth() + "/" + period.getYear());
            binding.tvStatus.setText(period.getStatus());
            binding.tvSubtitle.setText(period.getEmployeeCount() + " nhân viên • Tạo ngày " + period.getCreatedAt());
            binding.tvAmount.setText(String.format("%,.0f đ", period.getTotalAmount()));
            
            if (period.getApprovedAt() != null) {
                binding.tvApproveDate.setText("Duyệt: " + period.getApprovedAt());
            } else {
                binding.tvApproveDate.setText("");
            }

            binding.btnView.setOnClickListener(v -> {
                if (listener != null) listener.onViewClick(period);
            });

            binding.btnDownload.setOnClickListener(v -> {
                if (listener != null) listener.onDownloadClick(period);
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onViewClick(period);
            });
        }
    }
}
