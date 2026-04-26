package com.demo.ltud_n10.presentation.ui.employee;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemEmployeeContractBinding;
import com.demo.ltud_n10.domain.model.Contract;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmployeeContractAdapter extends RecyclerView.Adapter<EmployeeContractAdapter.ViewHolder> {

    private List<Contract> items = new ArrayList<>();

    public void setItems(List<Contract> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEmployeeContractBinding binding = ItemEmployeeContractBinding.inflate(
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
        private final ItemEmployeeContractBinding binding;

        ViewHolder(ItemEmployeeContractBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Contract contract) {
            // Định dạng: Chỉ in đậm phần nhãn (Label), nội dung (Value) giữ bình thường
            setBoldLabel(binding.tvContractId, "Mã hợp đồng: ", contract.getId());
            setBoldLabel(binding.tvEmployeeName, "Tên nhân viên: ", contract.getEmployeeName());
            setBoldLabel(binding.tvType, "Loại hợp đồng: ", contract.getType());
            setBoldLabel(binding.tvStartDate, "Ngày bắt đầu: ", contract.getStartDate());
            setBoldLabel(binding.tvEndDate, "Ngày kết thúc: ", contract.getEndDate());
            
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            String salaryStr = formatter.format(contract.getSalary()) + " VNĐ";
            setBoldLabel(binding.tvSalary, "Mức lương: ", salaryStr);
            
            // Trạng thái
            binding.tvStatus.setText(contract.getStatus());
            if ("Còn hiệu lực".equals(contract.getStatus()) || "Đang hiệu lực".equals(contract.getStatus())) {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#0A4D1E")); // Xanh lá đậm thương hiệu
            } else {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#C62828")); // Đỏ cho hết hạn
            }
        }

        /**
         * Hàm hỗ trợ in đậm phần nhãn (label) và giữ nguyên phần giá trị (value)
         */
        private void setBoldLabel(TextView textView, String label, String value) {
            if (value == null) value = "N/A";
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(label);
            // Áp dụng kiểu BOLD cho phần label (từ vị trí 0 đến hết chiều dài label)
            builder.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(value);
            textView.setText(builder);
        }
    }
}
