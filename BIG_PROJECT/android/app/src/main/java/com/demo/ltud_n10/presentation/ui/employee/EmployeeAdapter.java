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

public class EmployeeAdapter extends ListAdapter<Employee, EmployeeAdapter.ViewHolder> {

    private final OnEmployeeClickListener listener;
    private boolean isAdmin = true;

    public interface OnEmployeeClickListener {
        void onEditClick(Employee employee);
        void onDeleteClick(Employee employee);
        void onViewClick(Employee employee);
    }

    public EmployeeAdapter(OnEmployeeClickListener listener) {
        super(new DiffUtil.ItemCallback<Employee>() {
            @Override
            public boolean areItemsTheSame(@NonNull Employee oldItem, @NonNull Employee newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Employee oldItem, @NonNull Employee newItem) {
                return oldItem.getName().equals(newItem.getName()) && 
                       oldItem.getStatus().equals(newItem.getStatus()) &&
                       oldItem.getPosition().equals(newItem.getPosition());
            }
        });
        this.listener = listener;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        notifyDataSetChanged();
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
        holder.tvName.setText(employee.getName());
        holder.tvEmail.setText(employee.getEmail());
        holder.tvCccd.setText(employee.getCccd());
        holder.tvPhone.setText(employee.getPhone());
        holder.tvPosition.setText(employee.getPosition());
        holder.tvStatus.setText(employee.getStatus());

        holder.btnEdit.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        holder.btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(employee));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(employee));
        holder.btnView.setOnClickListener(v -> listener.onViewClick(employee));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvCccd, tvPhone, tvPosition, tvStatus;
        View btnEdit, btnDelete, btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvCccd = itemView.findViewById(R.id.tvCccd);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}
