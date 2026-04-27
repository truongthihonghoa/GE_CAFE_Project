package com.demo.ltud_n10.presentation.ui.employee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.domain.model.Employee;

import java.util.Objects;

public class EmployeeAdapter extends ListAdapter<Employee, EmployeeAdapter.ViewHolder> {

    private final OnEmployeeClickListener listener;

    public interface OnEmployeeClickListener {
        void onEditClick(Employee employee);
        void onDeleteClick(Employee employee);
    }

    public EmployeeAdapter(OnEmployeeClickListener listener) {
        super(new DiffUtil.ItemCallback<Employee>() {
            @Override
            public boolean areItemsTheSame(@NonNull Employee oldItem, @NonNull Employee newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Employee oldItem, @NonNull Employee newItem) {
                return Objects.equals(oldItem.getName(), newItem.getName()) &&
                        Objects.equals(oldItem.getStatus(), newItem.getStatus()) &&
                        Objects.equals(oldItem.getPosition(), newItem.getPosition());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = getItem(position);
        holder.tvName.setText(employee.getName() != null ? employee.getName() : "N/A");
        holder.tvCccd.setText("CCCD: " + (employee.getCccd() != null ? employee.getCccd() : ""));
        holder.tvPhone.setText("SĐT: " + (employee.getPhone() != null ? employee.getPhone() : ""));
        holder.tvPosition.setText(employee.getPosition() != null ? employee.getPosition() : "");

        String status = employee.getStatus() != null ? employee.getStatus() : "Đang làm";
        holder.tvStatus.setText(status);

        // Cập nhật giao diện trạng thái dựa trên nội dung
        if ("Đang làm".equals(status) || "Còn hiệu lực".equals(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_green_light);
            holder.tvStatus.setTextColor(0xFF2E7D32); // Màu xanh lá
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_red_light);
            holder.tvStatus.setTextColor(0xFFD32F2F); // Màu đỏ
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(employee));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(employee));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCccd, tvPhone, tvPosition, tvStatus;
        View btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEmployeeName);
            tvCccd = itemView.findViewById(R.id.tvCccd);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}