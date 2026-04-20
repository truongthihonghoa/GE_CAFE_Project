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
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Branch branch);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
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
            binding.tvPhoneNumber.setText(branch.getPhoneNumber());

            if ("Ngưng hoạt động".equals(branch.getStatus())) {
                binding.tvBranchName.setTextColor(Color.RED);
                binding.tvAddress.setTextColor(Color.RED);
                binding.tvPhoneNumber.setTextColor(Color.RED);
            } else {
                binding.tvBranchName.setTextColor(Color.parseColor("#64748B"));
                binding.tvAddress.setTextColor(Color.parseColor("#1E293B"));
                binding.tvPhoneNumber.setTextColor(Color.parseColor("#1B431C"));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(branch);
                }
            });
        }
    }
}
