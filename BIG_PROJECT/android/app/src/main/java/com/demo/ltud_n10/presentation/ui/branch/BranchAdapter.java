package com.demo.ltud_n10.presentation.ui.branch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemBranchBinding;
import com.demo.ltud_n10.domain.model.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.ViewHolder> {

    private List<Branch> items = new ArrayList<>();
    private OnBranchActionListener listener;

    public interface OnBranchActionListener {
        void onView(Branch branch);
        void onEdit(Branch branch);
        void onDelete(Branch branch);
    }

    public void setOnBranchActionListener(OnBranchActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Branch> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBranchBinding binding = ItemBranchBinding.inflate(
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
        private final ItemBranchBinding binding;

        ViewHolder(ItemBranchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Branch branch) {
            binding.tvBranchName.setText(branch.getName());
            binding.tvAddress.setText(branch.getAddress());
            binding.tvManagerName.setText("Quản lý: " + (branch.getManagerName() != null ? branch.getManagerName() : "Chưa có"));
            binding.tvStatusLabel.setText(branch.getStatus());

            if ("Ngưng hoạt động".equals(branch.getStatus())) {
                binding.tvStatusLabel.setBackgroundResource(com.demo.ltud_n10.R.drawable.bg_status_branch);
                binding.tvStatusLabel.setTextColor(Color.parseColor("#721C24"));
            } else {
                binding.tvStatusLabel.setBackgroundResource(com.demo.ltud_n10.R.drawable.bg_status_branch);
                binding.tvStatusLabel.setTextColor(Color.parseColor("#1B431C"));
            }

            binding.ivView.setOnClickListener(v -> {
                if (listener != null) listener.onView(branch);
            });

            binding.ivEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(branch);
            });

            binding.ivDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(branch);
            });
            
            // Mặc định nhấn vào card là xem chi tiết
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onView(branch);
            });
        }
    }
}
