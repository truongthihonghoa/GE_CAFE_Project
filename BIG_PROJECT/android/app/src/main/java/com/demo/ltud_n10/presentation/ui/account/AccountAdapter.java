package com.demo.ltud_n10.presentation.ui.account;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemAccountBinding;
import com.demo.ltud_n10.domain.model.User;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private List<User> items = new ArrayList<>();
    private OnItemClickListener listener;
    private String currentUserRole = "ADMIN"; 

    public interface OnItemClickListener {
        void onEditClick(User user);
        void onDeleteClick(User user);
        void onItemClick(User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<User> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setCurrentUserRole(String role) {
        this.currentUserRole = role;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAccountBinding binding = ItemAccountBinding.inflate(
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
        private final ItemAccountBinding binding;

        ViewHolder(ItemAccountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(User user) {
            binding.tvName.setText(user.getName());
            binding.tvUsername.setText(user.getUsername());
            binding.tvRole.setText(user.getRole());

            boolean isLài = user.getName() != null && user.getName().contains("Thúy Lài");
            boolean isCurrentUserAdmin = "ADMIN".equals(currentUserRole);

            // 1. Chỉnh nhãn Trạng thái/Chức vụ
            String statusLabel;
            if (isLài) {
                statusLabel = "Quản lý";
            } else {
                statusLabel = "Nhân viên";
            }
            binding.tvStatus.setText(statusLabel);
            
            // 2. Màu sắc nhãn (Hồng cho cả Quản lý và Nhân viên theo hình)
            binding.cvStatus.setCardBackgroundColor(Color.parseColor("#F8D7DA"));
            binding.tvStatus.setTextColor(Color.parseColor("#721C24"));

            // 3. Hiển thị Icon (Xóa + Bút chì)
            if (isCurrentUserAdmin) {
                if (isLài) {
                    // Trần Thị Thúy Lài: XÓA CẢ 2 ICON
                    binding.ivDelete.setVisibility(View.GONE);
                    binding.ivEdit.setVisibility(View.GONE);
                } else {
                    // Nhân viên khác: HIỆN CẢ 2 ICON
                    binding.ivDelete.setVisibility(View.VISIBLE);
                    binding.ivEdit.setVisibility(View.VISIBLE);
                }
            } else {
                // Nếu là Nhân viên đăng nhập: ẨN HẾT ICON
                binding.ivDelete.setVisibility(View.GONE);
                binding.ivEdit.setVisibility(View.GONE);
            }

            // Sự kiện click
            binding.ivEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(user);
            });

            binding.ivDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(user);
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(user);
            });
        }
    }
}
