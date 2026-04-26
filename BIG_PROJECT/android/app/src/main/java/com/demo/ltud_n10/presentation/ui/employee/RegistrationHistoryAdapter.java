package com.demo.ltud_n10.presentation.ui.employee;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemRegistrationHistoryBinding;
import com.demo.ltud_n10.domain.model.Request;

import java.util.ArrayList;
import java.util.List;

public class RegistrationHistoryAdapter extends RecyclerView.Adapter<RegistrationHistoryAdapter.ViewHolder> {

    private List<Request> items = new ArrayList<>();
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onEdit(Request request);
        void onDelete(Request request);
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Request> items) {
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

        void bind(Request request) {
            String dateDisplay = request.getStartDate();
            if (request.getEndDate() != null && !request.getEndDate().isEmpty() && !request.getEndDate().equals(request.getStartDate())) {
                dateDisplay += " - " + request.getEndDate();
            }
            binding.tvDateTitle.setText(dateDisplay);

            if ("Nghỉ phép".equals(request.getType())) {
                binding.tvShiftDetail.setText("Xin nghỉ phép");
                binding.tvReason.setVisibility(View.VISIBLE);
                binding.tvReason.setText(request.getReason());
            } else {
                binding.tvShiftDetail.setText("Đăng ký ca: " + request.getReason());
                binding.tvReason.setVisibility(View.GONE);
            }

            binding.tvStatus.setText(request.getStatus());
            binding.tvSentTime.setVisibility(View.GONE); // RequestDto/Model current don't have sentTime field explicitly shown in UI here

            boolean canAction = "Chờ duyệt".equals(request.getStatus());

            if (canAction) {
                binding.layoutActions.setVisibility(View.VISIBLE);
                binding.btnEdit.setOnClickListener(v -> {
                    if (listener != null) listener.onEdit(request);
                });
                binding.btnDelete.setOnClickListener(v -> {
                    if (listener != null) listener.onDelete(request);
                });
            } else {
                binding.layoutActions.setVisibility(View.GONE);
            }

            // Status colors
            if ("Chờ duyệt".equals(request.getStatus())) {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#FFF3CD"));
                binding.tvStatus.setTextColor(Color.parseColor("#856404"));
            } else if ("Đã duyệt".equals(request.getStatus())) {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#E8F8EF"));
                binding.tvStatus.setTextColor(Color.parseColor("#2ECC71"));
            } else {
                binding.cvStatus.setCardBackgroundColor(Color.parseColor("#F8D7DA"));
                binding.tvStatus.setTextColor(Color.parseColor("#721C24"));
            }
        }
    }
}
