package com.demo.ltud_n10.presentation.ui.approval;

import android.app.DatePickerDialog;
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
    private final OnActionListener listener;

    public interface OnActionListener {
        void onApprove(Request request);
        void onReject(Request request);
        void onTimeChanged(Request request);
    }

    public ApprovalRequestAdapter(OnActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<Request> items) {
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
            binding.tvEmployeeName.setText(request.getEmployeeName() != null ? request.getEmployeeName() : request.getEmployeeId());
            binding.tvTime.setText(request.getStartDate() + " -> " + request.getEndDate());
            binding.tvStatus.setText(request.getStatus());
            
            String reason = request.getReason();
            if (reason != null && !reason.isEmpty()) {
                binding.tvReason.setText("Lý do: " + reason);
                binding.tvReason.setVisibility(View.VISIBLE);
            } else {
                binding.tvReason.setVisibility(View.GONE);
            }

            binding.layoutActions.setVisibility(View.GONE);
            binding.ivEditTime.setVisibility(View.GONE);

            if ("Chờ duyệt".equals(request.getStatus())) {
                binding.cvContainer.setCardBackgroundColor(Color.parseColor("#FFF3CD"));
                binding.cvContainer.setStrokeColor(Color.parseColor("#FFE69C"));
                binding.tvStatus.setTextColor(Color.parseColor("#856404"));
                binding.layoutActions.setVisibility(View.VISIBLE);
                binding.ivEditTime.setVisibility(View.VISIBLE);
            } else if ("Đã duyệt".equals(request.getStatus())) {
                binding.cvContainer.setCardBackgroundColor(Color.parseColor("#E8F8EF"));
                binding.cvContainer.setStrokeColor(Color.parseColor("#B7EBCA"));
                binding.tvStatus.setTextColor(Color.parseColor("#2ECC71"));
            } else if ("Bị từ chối".equals(request.getStatus())) {
                binding.cvContainer.setCardBackgroundColor(Color.parseColor("#F8D7DA"));
                binding.cvContainer.setStrokeColor(Color.parseColor("#F5C6CB"));
                binding.tvStatus.setTextColor(Color.parseColor("#721C24"));
            }

            binding.btnApprove.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(request);
            });

            binding.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(request);
            });

            binding.layoutTimeEdit.setOnClickListener(v -> {
                if ("Chờ duyệt".equals(request.getStatus())) {
                    showDateRangePicker(request);
                }
            });
        }

        private void showDateRangePicker(Request request) {
            final Calendar c = Calendar.getInstance();
            DatePickerDialog startDatePicker = new DatePickerDialog(itemView.getContext(), (view, y, m, d) -> {
                String startDate = String.format("%04d-%02d-%02d", y, m + 1, d);
                DatePickerDialog endDatePicker = new DatePickerDialog(itemView.getContext(), (view2, y2, m2, d2) -> {
                    String endDate = String.format("%04d-%02d-%02d", y2, m2 + 1, d2);
                    request.setStartDate(startDate);
                    request.setEndDate(endDate);
                    binding.tvTime.setText(startDate + " -> " + endDate);
                    if (listener != null) listener.onTimeChanged(request);
                }, y, m, d);
                endDatePicker.setTitle("Chọn ngày kết thúc");
                endDatePicker.show();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            startDatePicker.setTitle("Chọn ngày bắt đầu");
            startDatePicker.show();
        }
    }
}
