package com.demo.ltud_n10.presentation.ui.employee;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemRegistrationHistoryBinding;
import com.demo.ltud_n10.domain.model.WorkShift;

import java.util.ArrayList;
import java.util.List;

public class RegistrationHistoryAdapter extends RecyclerView.Adapter<RegistrationHistoryAdapter.ViewHolder> {

    private List<WorkShift> items = new ArrayList<>();

    public void setItems(List<WorkShift> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRegistrationHistoryBinding binding = ItemRegistrationHistoryBinding.inflate(
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
        private final ItemRegistrationHistoryBinding binding;

        ViewHolder(ItemRegistrationHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(WorkShift shift) {
            binding.tvDateTitle.setText(shift.getDate());
            
            if ("Nghỉ phép".equals(shift.getType())) {
                binding.tvShiftDetail.setText("Xin nghỉ phép");
                binding.tvReason.setVisibility(View.VISIBLE);
                binding.tvReason.setText(shift.getPosition()); // Note is in position field for mock
            } else {
                binding.tvShiftDetail.setText("Ca " + shift.getPosition() + " • " + shift.getStartTime() + " - " + shift.getEndTime());
                binding.tvReason.setVisibility(View.GONE);
            }

            binding.tvStatus.setText(shift.getStatus());
            if ("Chờ duyệt".equals(shift.getStatus())) {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#FFF3CD"));
                binding.tvStatus.setTextColor(Color.parseColor("#856404"));
            } else if ("Đã duyệt".equals(shift.getStatus())) {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#E8F8EF"));
                binding.tvStatus.setTextColor(Color.parseColor("#2ECC71"));
            } else {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#F8D7DA"));
                binding.tvStatus.setTextColor(Color.parseColor("#721C24"));
            }
        }
    }
}
