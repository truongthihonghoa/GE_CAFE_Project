package com.demo.ltud_n10.presentation.ui.employee;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemRegistrationHistoryBinding;
import com.demo.ltud_n10.domain.model.Request;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        private String formatDisplayDate(String dateStr) {
            try {
                SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                // Định dạng: Thứ..., ngày... tháng...
                SimpleDateFormat to = new SimpleDateFormat("EEEE, dd 'tháng' MM", new Locale("vi", "VN"));
                Date date = from.parse(dateStr);
                String result = to.format(date);
                // Viết hoa chữ cái đầu (ví dụ: thứ hai -> Thứ hai)
                return result.substring(0, 1).toUpperCase() + result.substring(1);
            } catch (Exception e) {
                return dateStr;
            }
        }

        private boolean isDateInPast(String dateStr) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(dateStr);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return date != null && date.before(cal.getTime());
            } catch (Exception e) {
                return false;
            }
        }

        void bind(Request request) {
            // Hiển thị ngày tháng theo định dạng "Thứ..., ngày... tháng..."
            binding.tvDateTitle.setText(formatDisplayDate(request.getStartDate()));

            if (!"Nghỉ phép".equals(request.getType())) {
                // XỬ LÝ CHO ĐĂNG KÝ CA LÀM
                binding.tvShiftDetail.setVisibility(View.VISIBLE);
                
                String shiftLabel = request.getReason();
                String timeRange = "";
                if ("Sáng".equals(shiftLabel)) timeRange = "08:00 - 12:00";
                else if ("Chiều".equals(shiftLabel)) timeRange = "13:00 - 17:00";
                else if ("Tối".equals(shiftLabel)) timeRange = "18:00 - 22:00";
                
                binding.tvShiftDetail.setText("Ca " + shiftLabel + " • " + timeRange);
                binding.tvReason.setVisibility(View.GONE);
            } else {
                // XỬ LÝ CHO NGHỈ PHÉP
                binding.tvShiftDetail.setVisibility(View.GONE);
                binding.tvReason.setVisibility(View.VISIBLE);
                binding.tvReason.setText(request.getReason());
                
                // Nếu nghỉ phép nhiều ngày, cập nhật lại title hiển thị khoảng ngày
                try {
                    SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat to = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    if (request.getEndDate() != null && !request.getEndDate().equals(request.getStartDate())) {
                        binding.tvDateTitle.setText(to.format(from.parse(request.getStartDate())) + " - " + to.format(from.parse(request.getEndDate())));
                    }
                } catch (Exception ignored) {}
            }

            // HIỂN THỊ THỜI GIAN GỬI
            if (request.getCreatedAt() != null) {
                binding.tvSentTime.setVisibility(View.VISIBLE);
                binding.tvSentTime.setText("Gửi lúc: " + request.getCreatedAt());
            }

            binding.tvStatus.setText(request.getStatus());

            // CHẶN CHỈNH SỬA NẾU NGÀY Ở QUÁ KHỨ
            boolean isPast = isDateInPast(request.getStartDate());
            boolean canAction = "Chờ duyệt".equals(request.getStatus()) && !isPast;

            binding.layoutActions.setVisibility(canAction ? View.VISIBLE : View.GONE);
            if (canAction) {
                binding.btnEdit.setOnClickListener(v -> { if (listener != null) listener.onEdit(request); });
                binding.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDelete(request); });
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
