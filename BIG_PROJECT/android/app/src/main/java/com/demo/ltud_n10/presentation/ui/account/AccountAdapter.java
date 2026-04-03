package com.demo.ltud_n10.presentation.ui.account;

import android.graphics.Color;
import android.view.LayoutInflater;
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

    public interface OnItemClickListener {
        void onEditClick(User user);
        void onItemClick(User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<User> items) {
        this.items = items;
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
            binding.tvStatus.setText(user.getStatus());

            if ("Ngưng hoạt động".equals(user.getStatus())) {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#F8D7DA"));
                binding.tvStatus.setTextColor(Color.parseColor("#721C24"));
            } else {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#E8F8EF"));
                binding.tvStatus.setTextColor(Color.parseColor("#2ECC71"));
            }

            binding.ivEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(user);
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(user);
            });
        }
    }
}
