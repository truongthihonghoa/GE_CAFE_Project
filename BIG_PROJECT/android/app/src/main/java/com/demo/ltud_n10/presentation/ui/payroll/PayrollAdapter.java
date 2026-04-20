package com.demo.ltud_n10.presentation.ui.payroll;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemPayrollPeriodBinding;
import com.demo.ltud_n10.domain.model.PayrollPeriod;

import java.util.ArrayList;
import java.util.List;

public class PayrollAdapter extends RecyclerView.Adapter<PayrollAdapter.ViewHolder> {

    private List<PayrollPeriod> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onViewClick(PayrollPeriod period);
        void onExportClick(PayrollPeriod period);
        void onReviewClick(PayrollPeriod period);
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
        ItemPayrollPeriodBinding binding = ItemPayrollPeriodBinding.inflate(
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
        private final ItemPayrollPeriodBinding binding;

        ViewHolder(ItemPayrollPeriodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PayrollPeriod period) {
            String status = period.getStatus();
            
            // ID và Trạng thái ở dòng trên cùng
            String idText = String.format("ML%s • NV%s", period.getId(), period.getEmployeeId());
            if ("Đã duyệt".equals(status)) {
                idText += " • Đã duyệt";
            } else if ("Từ chối".equals(status)) {
                idText += " • Từ chối";
                binding.tvId.setTextColor(Color.parseColor("#E74C3C"));
            } else {
                binding.tvId.setTextColor(Color.parseColor("#BDBDBD"));
            }
            binding.tvId.setText(idText);

            // Tên nhân viên
            binding.tvEmployeeName.setText(period.getEmployeeName() != null ? period.getEmployeeName() : "Nhân viên");

            // Xử lý giao diện theo trạng thái
            if ("Đã duyệt".equals(status)) {
                setupApprovedUI(period);
            } else if ("Từ chối".equals(status)) {
                setupRejectedUI(period);
            } else {
                setupPendingUI(period);
            }

            // Click listeners
            binding.ivApprove.setOnClickListener(v -> {
                if (listener != null) listener.onViewClick(period);
            });
            
            binding.tvReview.setOnClickListener(v -> {
                if (listener != null) listener.onReviewClick(period);
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onViewClick(period);
            });
        }

        private void setupApprovedUI(PayrollPeriod period) {
            binding.ivApprovedStatus.setVisibility(View.VISIBLE);
            binding.ivRejectedStatus.setVisibility(View.GONE);
            binding.layoutActions.setVisibility(View.GONE);
            binding.ivEdit.setVisibility(View.GONE);
            binding.ivDelete.setVisibility(View.GONE);
            binding.tvReview.setVisibility(View.GONE);
            
            binding.divider.setVisibility(View.GONE);
            binding.row1.setVisibility(View.GONE);
            binding.divider2.setVisibility(View.GONE);
            binding.tvLabelTotal.setVisibility(View.GONE);
            
            binding.tvSubInfo.setText("Kỳ lương " + period.getMonth() + "/" + period.getYear());
            binding.tvTotalSalary.setVisibility(View.VISIBLE);
            binding.tvTotalSalary.setText(String.format("%,.0fđ", period.getTotalAmount()));
            binding.tvTotalSalary.setTextColor(Color.parseColor("#555555"));
            
            binding.container.setBackgroundColor(Color.WHITE);
            binding.cardView.setStrokeColor(Color.parseColor("#F0F0F0"));
        }

        private void setupRejectedUI(PayrollPeriod period) {
            binding.ivApprovedStatus.setVisibility(View.GONE);
            binding.ivRejectedStatus.setVisibility(View.VISIBLE);
            binding.layoutActions.setVisibility(View.GONE);
            binding.ivEdit.setVisibility(View.GONE);
            binding.ivDelete.setVisibility(View.GONE);
            binding.tvTotalSalary.setVisibility(View.GONE);
            binding.tvReview.setVisibility(View.VISIBLE);
            
            binding.divider.setVisibility(View.GONE);
            binding.row1.setVisibility(View.GONE);
            binding.divider2.setVisibility(View.GONE);
            binding.tvLabelTotal.setVisibility(View.GONE);
            
            binding.tvSubInfo.setText(period.getNote() != null ? period.getNote() : "Sai lệch ngày công");
            
            // Màu nền nhạt cho trạng thái từ chối
            binding.container.setBackgroundColor(Color.parseColor("#FFF5F5"));
            binding.cardView.setStrokeColor(Color.parseColor("#FFEBEE"));
        }

        private void setupPendingUI(PayrollPeriod period) {
            binding.ivApprovedStatus.setVisibility(View.GONE);
            binding.ivRejectedStatus.setVisibility(View.GONE);
            binding.layoutActions.setVisibility(View.VISIBLE);
            binding.ivEdit.setVisibility(View.VISIBLE);
            binding.ivDelete.setVisibility(View.VISIBLE);
            binding.tvTotalSalary.setVisibility(View.VISIBLE);
            binding.tvReview.setVisibility(View.GONE);
            
            binding.divider.setVisibility(View.VISIBLE);
            binding.row1.setVisibility(View.VISIBLE);
            binding.divider2.setVisibility(View.VISIBLE);
            binding.tvLabelTotal.setVisibility(View.VISIBLE);
            
            binding.tvSubInfo.setText("Kỳ lương " + period.getMonth() + "/" + period.getYear());
            binding.tvTotalSalary.setText(String.format("%,.0fđ", period.getTotalAmount()));
            
            binding.container.setBackgroundColor(Color.WHITE);
            binding.cardView.setStrokeColor(Color.parseColor("#F0F0F0"));

            // Mock values for details
            binding.tvBaseSalary.setText(String.format("%,.0f", period.getTotalAmount() * 0.4));
            double bonus = period.getTotalAmount() * 0.6;
            binding.tvBonus.setText(bonus >= 0 ? "+" + String.format("%,.0f", bonus) : String.format("%,.0f", bonus));
        }
    }
}
