package com.demo.ltud_n10.presentation.ui.contract;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemContractBinding;
import com.demo.ltud_n10.domain.model.Contract;

import java.util.ArrayList;
import java.util.List;

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ViewHolder> {

    private List<Contract> contracts = new ArrayList<>();
    private final OnContractClickListener listener;

    public interface OnContractClickListener {
        void onEdit(Contract contract);
        void onDelete(Contract contract);
    }

    public ContractAdapter(OnContractClickListener listener) {
        this.listener = listener;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContractBinding binding = ItemContractBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(contracts.get(position));
    }

    @Override
    public int getItemCount() {
        return contracts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemContractBinding binding;

        public ViewHolder(ItemContractBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Contract contract) {
            binding.tvEmployeeName.setText(contract.getEmployeeName());
            binding.tvContractId.setText("Mã HĐ: " + contract.getId());
            binding.tvPosition.setText("Chức vụ: " + contract.getPosition());
            binding.tvDateRange.setText(contract.getStartDate() + " - " + contract.getEndDate());
            
            // ƯU TIÊN HIỂN THỊ LƯƠNG CƠ BẢN NẾU CÓ, NẾU KHÔNG THÌ HIỂN THỊ LƯƠNG THEO GIỜ
            double displaySalary = contract.getSalary();
            String unit = "VNĐ";
            
            if (displaySalary <= 0) {
                displaySalary = contract.getHourlyRate();
                unit = "VNĐ/giờ";
            }

            if (displaySalary > 0) {
                binding.tvSalary.setText(String.format("%,.0f %s", displaySalary, unit));
            } else {
                binding.tvSalary.setText("Chưa cập nhật");
            }

            binding.tvStatus.setText(contract.getStatus());

            binding.btnEdit.setOnClickListener(v -> listener.onEdit(contract));
            binding.btnDelete.setOnClickListener(v -> listener.onDelete(contract));
        }
    }
}