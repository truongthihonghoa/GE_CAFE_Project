package com.demo.ltud_n10.presentation.ui.account;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemAccountBinding;
import com.demo.ltud_n10.domain.model.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private List<Account> items = new ArrayList<>();
    private OnAccountActionListener listener;

    public interface OnAccountActionListener {
        void onView(Account account);
        void onEdit(Account account);
        void onDelete(Account account);
    }

    public void setOnAccountActionListener(OnAccountActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Account> items) {
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

        void bind(Account account) {
            binding.tvUsername.setText(account.getUsername());
            binding.tvEmployeeName.setText(account.getEmployeeName());
            binding.tvRole.setText(account.getRole());
            binding.tvStatus.setText(account.getStatus());

            if ("Ngưng hoạt động".equals(account.getStatus())) {
                binding.tvStatus.setTextColor(Color.parseColor("#721C24"));
            } else {
                binding.tvStatus.setTextColor(Color.parseColor("#1B431C"));
            }

            binding.ivView.setOnClickListener(v -> {
                if (listener != null) listener.onView(account);
            });

            binding.ivEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(account);
            });

            binding.ivDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(account);
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onView(account);
            });
        }
    }
}
