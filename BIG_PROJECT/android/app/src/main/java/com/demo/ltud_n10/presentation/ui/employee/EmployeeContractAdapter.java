package com.demo.ltud_n10.presentation.ui.employee;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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
            binding.tvContractId.setText("Mã hợp đồng: " + contract.getId());
            binding.tvEmployeeName.setText("Tên nhân viên: " + contract.getEmployeeName());
            binding.tvType.setText("Loại hợp đồng: " + contract.getType());
            binding.tvStartDate.setText("Ngày bắt đầu: " + contract.getStartDate());
            binding.tvEndDate.setText("Ngày kết thúc: " + contract.getEndDate());
            
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            binding.tvSalary.setText("Mức lương: " + formatter.format(contract.getSalary()) + " VNĐ");
            
            binding.tvStatus.setText(contract.getStatus());
            if ("Còn hiệu lực".equals(contract.getStatus()) || "Đang hiệu lực".equals(contract.getStatus())) {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#2ECC71"));
            } else {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#E74C3C"));
            }
        }
    }
}
