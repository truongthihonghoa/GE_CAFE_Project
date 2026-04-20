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
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onEdit(WorkShift shift);
        void onDelete(WorkShift shift);
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        this.listener = listener;
    }

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
                binding.tvReason.setText(shift.getPosition()); 
            } else {
                binding.tvShiftDetail.setText("Ca " + shift.getPosition() + " • " + shift.getStartTime() + " - " + shift.getEndTime());
                binding.tvReason.setVisibility(View.GONE);
            }

            binding.tvStatus.setText(shift.getStatus());
            
            // Hiển thị thời gian gửi thực tế
            if (shift.getSentTime() != null) {
                binding.tvSentTime.setText("Gửi lúc: " + shift.getSentTime());
            }

            // Show actions only for "Chờ duyệt"
            if ("Chờ duyệt".equals(shift.getStatus())) {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#FFF3CD"));
                binding.tvStatus.setTextColor(Color.parseColor("#856404"));
                binding.layoutActions.setVisibility(View.VISIBLE);
                
                binding.btnEdit.setOnClickListener(v -> {
                    if (listener != null) listener.onEdit(shift);
                });
                
                binding.btnDelete.setOnClickListener(v -> {
                    if (listener != null) listener.onDelete(shift);
                });
            } else {
                binding.layoutActions.setVisibility(View.GONE);
                if ("Đã duyệt".equals(shift.getStatus())) {
                    binding.cvStatus.setCardBackgroundColor(Color.parseColor("#E8F8EF"));
                    binding.tvStatus.setTextColor(Color.parseColor("#2ECC71"));
                } else {
                    binding.cvStatus.setCardBackgroundColor(Color.parseColor("#F8D7DA"));
                    binding.tvStatus.setTextColor(Color.parseColor("#721C24"));
                }
            }
        }
    }
}
