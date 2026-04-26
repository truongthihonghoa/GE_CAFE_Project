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

            setBoldLabel(binding.tvContractId, "Mã hợp đồng: ", contract.getId());
            setBoldLabel(binding.tvEmployeeName, "Tên nhân viên: ", contract.getEmployeeName());
            setBoldLabel(binding.tvType, "Loại hợp đồng: ", contract.getType());
            setBoldLabel(binding.tvStartDate, "Ngày bắt đầu: ", contract.getStartDate());

            String endDate = contract.getEndDate();
            if (endDate == null || endDate.trim().isEmpty()) {
                endDate = "Không thời hạn";
            }
            setBoldLabel(binding.tvEndDate, "Ngày kết thúc: ", endDate);

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            String salaryStr = formatter.format(contract.getSalary()) + " VNĐ";
            setBoldLabel(binding.tvSalary, "Mức lương: ", salaryStr);

            String status = contract.getStatus();
            String displayStatus = status;
            int statusColor = Color.parseColor("#C62828"); // Mặc định màu đỏ

            // Chuyển đổi mã trạng thái sang tiếng Việt và set màu xanh
            if ("CON_HAN".equals(status) || "Còn hiệu lực".equals(status) || "Đang hiệu lực".equals(status)) {
                displayStatus = "Còn hiệu lực";
                statusColor = Color.parseColor("#0A4D1E"); // Màu xanh lá đậm
            } else if ("HET_HAN".equals(status)) {
                displayStatus = "Hết hiệu lực";
            } else if (status == null || status.trim().isEmpty()) {
                displayStatus = "Không xác định";
            }

            binding.tvStatus.setText(displayStatus);
            binding.cvStatus.setCardBackgroundColor(statusColor);
        }

        private void setBoldLabel(TextView textView, String label, String value) {

            if (value == null || value.trim().isEmpty()) {
                value = "N/A";
            }

            SpannableStringBuilder builder = new SpannableStringBuilder();

            builder.append(label);

            builder.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    0,
                    label.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            builder.append(value);

            textView.setText(builder);
        }
    }
}
