package com.demo.ltud_n10.presentation.ui.payroll;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemPayrollDetailBinding;
import com.demo.ltud_n10.domain.model.PayrollDetail;

import java.util.ArrayList;
import java.util.List;

public class PayrollDetailAdapter extends RecyclerView.Adapter<PayrollDetailAdapter.ViewHolder> {

    private List<PayrollDetail> items = new ArrayList<>();
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(PayrollDetail detail);
        void onDelete(PayrollDetail detail);
        void onApprove(PayrollDetail detail);
        void onReject(PayrollDetail detail);
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PayrollDetail> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPayrollDetailBinding binding = ItemPayrollDetailBinding.inflate(
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
        private final ItemPayrollDetailBinding binding;

        ViewHolder(ItemPayrollDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PayrollDetail detail) {
            binding.tvCode.setText(String.format("ML%s • %s", detail.getId(), detail.getEmployeeId()));
            binding.tvEmployeeName.setText(detail.getEmployeeName());
            binding.tvBaseSalary.setText(String.format("%,.0f", detail.getBaseSalary()));
            
            double bonusPenalty = detail.getBonus() - detail.getPenalty();
            if (bonusPenalty >= 0) {
                binding.tvBonus.setText(String.format("+%,.0f", bonusPenalty));
                binding.tvBonus.setTextColor(android.graphics.Color.parseColor("#2ECC71"));
            } else {
                binding.tvBonus.setText(String.format("-%,.0f", Math.abs(bonusPenalty)));
                binding.tvBonus.setTextColor(android.graphics.Color.parseColor("#FF4D4D"));
            }

            binding.tvTotalSalary.setText(String.format("%,.0fđ", detail.getTotalSalary()));

            binding.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(detail);
            });
            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(detail);
            });
            binding.btnApprove.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(detail);
            });
            binding.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(detail);
            });

            // If already approved, hide or change UI
            if ("Đã duyệt".equals(detail.getStatus())) {
                binding.btnApprove.setVisibility(android.view.View.GONE);
                binding.btnReject.setVisibility(android.view.View.GONE);
            } else {
                binding.btnApprove.setVisibility(android.view.View.VISIBLE);
                binding.btnReject.setVisibility(android.view.View.VISIBLE);
            }
        }
    }
}
