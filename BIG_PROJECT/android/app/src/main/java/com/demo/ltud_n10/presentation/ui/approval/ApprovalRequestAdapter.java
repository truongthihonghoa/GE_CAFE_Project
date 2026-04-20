package com.demo.ltud_n10.presentation.ui.approval;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemApprovalRequestBinding;
import com.demo.ltud_n10.domain.model.WorkShift;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class ApprovalRequestAdapter extends RecyclerView.Adapter<ApprovalRequestAdapter.ViewHolder> {

    private List<WorkShift> items = new ArrayList<>();
    private OnActionListener listener;

    public interface OnActionListener {
        void onApprove(WorkShift shift);
        void onReject(WorkShift shift);
        void onTimeChanged(WorkShift shift);
    }

    public void setOnActionListener(OnActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<WorkShift> items) {
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

        void bind(WorkShift shift) {
            binding.tvEmployeeName.setText(shift.getEmployeeName());
            
            if ("Nghỉ phép".equals(shift.getType())) {
                binding.tvTime.setText("Xin nghỉ phép");
                binding.ivEditTime.setVisibility(View.GONE);
            } else {
                binding.tvTime.setText(shift.getStartTime() + " - " + shift.getEndTime());
            }
            
            binding.tvStatus.setText(shift.getStatus());

            // Default
            binding.layoutActions.setVisibility(View.GONE);
            binding.ivEditTime.setVisibility(View.GONE);

            if ("Chờ duyệt".equals(shift.getStatus())) {
                binding.cvContainer.setCardBackgroundColor(Color.parseColor("#FFF3CD")); // Yellow
                binding.cvContainer.setStrokeColor(Color.parseColor("#FFE69C"));
                binding.tvStatus.setTextColor(Color.parseColor("#856404"));
                binding.layoutActions.setVisibility(View.VISIBLE);
                if (!"Nghỉ phép".equals(shift.getType())) {
                    binding.ivEditTime.setVisibility(View.VISIBLE);
                }
            } else if ("Đã duyệt".equals(shift.getStatus())) {
                binding.cvContainer.setCardBackgroundColor(Color.parseColor("#E8F8EF")); // Green
                binding.cvContainer.setStrokeColor(Color.parseColor("#B7EBCA"));
                binding.tvStatus.setTextColor(Color.parseColor("#2ECC71"));
            } else if ("Bị từ chối".equals(shift.getStatus())) {
                binding.cvContainer.setCardBackgroundColor(Color.parseColor("#F8D7DA")); // Red
                binding.cvContainer.setStrokeColor(Color.parseColor("#F5C6CB"));
                binding.tvStatus.setTextColor(Color.parseColor("#721C24"));
            }

            binding.btnApprove.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(shift);
            });

            binding.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(shift);
            });

            binding.layoutTimeEdit.setOnClickListener(v -> {
                if ("Chờ duyệt".equals(shift.getStatus()) && !"Nghỉ phép".equals(shift.getType())) {
                    showTimeRangePicker(shift);
                }
            });
        }

        private void showTimeRangePicker(WorkShift shift) {
            try {
                String[] startParts = shift.getStartTime().split(":");
                int startH = Integer.parseInt(startParts[0]);
                int startM = Integer.parseInt(startParts[1]);

                TimePickerDialog startTimePicker = new TimePickerDialog(itemView.getContext(), (view, h, m) -> {
                    String startTime = String.format("%02d:%02d", h, m);
                    
                    String[] endParts = shift.getEndTime().split(":");
                    int endH = Integer.parseInt(endParts[0]);
                    int endM = Integer.parseInt(endParts[1]);

                    TimePickerDialog endTimePicker = new TimePickerDialog(itemView.getContext(), (view2, h2, m2) -> {
                        String endTime = String.format("%02d:%02d", h2, m2);
                        shift.setStartTime(startTime);
                        shift.setEndTime(endTime);
                        binding.tvTime.setText(startTime + " - " + endTime);
                        if (listener != null) listener.onTimeChanged(shift);
                    }, endH, endM, true);
                    
                    endTimePicker.setTitle("Chọn giờ kết thúc");
                    endTimePicker.show();
                    
                }, startH, startM, true);
                
                startTimePicker.setTitle("Chọn giờ bắt đầu");
                startTimePicker.show();
            } catch (Exception e) {
                // Fallback to current time if format is wrong
                Calendar c = Calendar.getInstance();
                TimePickerDialog fallback = new TimePickerDialog(itemView.getContext(), (view, h, m) -> {
                    String startTime = String.format("%02d:%02d", h, m);
                    shift.setStartTime(startTime);
                    shift.setEndTime(startTime); // dummy
                    if (listener != null) listener.onTimeChanged(shift);
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                fallback.show();
            }
        }
    }
}
