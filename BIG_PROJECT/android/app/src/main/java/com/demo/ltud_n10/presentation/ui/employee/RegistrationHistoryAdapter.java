package com.demo.ltud_n10.presentation.ui.employee;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemRegistrationHistoryBinding;
import com.demo.ltud_n10.domain.model.WorkShift;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
                String detail = "Ca " + shift.getPosition();
                if (shift.getStartTime() != null && !shift.getStartTime().equals("null") &&
                    shift.getEndTime() != null && !shift.getEndTime().equals("null")) {
                    detail += " • " + shift.getStartTime() + " - " + shift.getEndTime();
                }
                binding.tvShiftDetail.setText(detail);
                binding.tvReason.setVisibility(View.GONE);
            }

            binding.tvStatus.setText(shift.getStatus());
            
            if (shift.getSentTime() != null) {
                binding.tvSentTime.setText("Gửi lúc: " + shift.getSentTime());
            }

            // Kiểm tra xem ngày đăng ký có ở quá khứ không
            boolean isPast = false;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateStr = shift.getDate();
                if (dateStr.contains(" - ")) {
                    dateStr = dateStr.split(" - ")[0]; // Lấy ngày bắt đầu nếu là nghỉ phép
                }
                Date date = sdf.parse(dateStr);
                if (date != null) {
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);
                    isPast = date.before(today.getTime());
                }
            } catch (Exception ignored) {}

            // Chỉ cho phép chỉnh sửa/xóa nếu trạng thái là "Chờ duyệt" hoặc "Đã duyệt" và ngày không ở quá khứ
            boolean canAction = !isPast && ("Chờ duyệt".equals(shift.getStatus()) || "Đã duyệt".equals(shift.getStatus()));

            if (canAction) {
                binding.layoutActions.setVisibility(View.VISIBLE);
                binding.btnEdit.setOnClickListener(v -> {
                    if (listener != null) listener.onEdit(shift);
                });
                binding.btnDelete.setOnClickListener(v -> {
                    if (listener != null) listener.onDelete(shift);
                });
            } else {
                binding.layoutActions.setVisibility(View.GONE);
            }

            // Cập nhật màu sắc trạng thái
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
