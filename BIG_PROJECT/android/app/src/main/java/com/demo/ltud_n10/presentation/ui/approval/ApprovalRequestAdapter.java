package com.demo.ltud_n10.presentation.ui.approval;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemApprovalRequestBinding;
import com.demo.ltud_n10.domain.model.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class ApprovalRequestAdapter extends RecyclerView.Adapter<ApprovalRequestAdapter.ViewHolder> {

    private List<Request> items = new ArrayList<>();
    private OnActionListener listener;

    public interface OnActionListener {
        void onApprove(Request request);
        void onReject(Request request);
    }

    public void setOnActionListener(OnActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Request> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemApprovalRequestBinding binding = ItemApprovalRequestBinding.inflate(
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
        private final ItemApprovalRequestBinding binding;

        ViewHolder(ItemApprovalRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Request request) {
            binding.tvEmployeeName.setText(request.getEmployeeName());
            
            if ("Nghỉ phép".equals(request.getType())) {
                binding.tvTime.setText("Nghỉ từ " + request.getStartDate() + " đến " + request.getEndDate());
                binding.ivEditTime.setVisibility(View.GONE);
            } else {
                binding.tvTime.setText(request.getType() + ": " + request.getStartDate());
                binding.ivEditTime.setVisibility(View.GONE);
            }
            
            binding.tvStatus.setText(request.getStatus());

            // Default
            binding.layoutActions.setVisibility(View.GONE);
            binding.ivEditTime.setVisibility(View.GONE);

            if ("Chờ duyệt".equals(request.getStatus()) || "pending".equals(request.getStatus())) {
                binding.cvContainer.setCardBackgroundColor(Color.parseColor("#FFF3CD")); // Yellow
                binding.cvContainer.setStrokeColor(Color.parseColor("#FFE69C"));
                binding.tvStatus.setTextColor(Color.parseColor("#856404"));
                binding.layoutActions.setVisibility(View.VISIBLE);
            } else if ("Đã duyệt".equals(request.getStatus()) || "approved".equals(request.getStatus())) {
                binding.cvContainer.setCardBackgroundColor(Color.parseColor("#E8F8EF")); // Green
                binding.cvContainer.setStrokeColor(Color.parseColor("#B7EBCA"));
                binding.tvStatus.setTextColor(Color.parseColor("#2ECC71"));
            } else if ("Bị từ chối".equals(request.getStatus()) || "rejected".equals(request.getStatus())) {
                binding.cvContainer.setCardBackgroundColor(Color.parseColor("#F8D7DA")); // Red
                binding.cvContainer.setStrokeColor(Color.parseColor("#F5C6CB"));
                binding.tvStatus.setTextColor(Color.parseColor("#721C24"));
            }

            binding.btnApprove.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(request);
            });

            binding.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(request);
            });
        }
    }
}
