package com.demo.ltud_n10.presentation.ui.payroll;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemEmployeeSalarySelectBinding;
import com.demo.ltud_n10.domain.model.Employee;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmployeeSalarySelectAdapter extends RecyclerView.Adapter<EmployeeSalarySelectAdapter.ViewHolder> {

    private List<Employee> employees = new ArrayList<>();
    private final Set<String> selectedEmployeeIds = new HashSet<>();

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
        notifyDataSetChanged();
    }

    public void selectAll(boolean selectAll) {
        selectedEmployeeIds.clear();
        if (selectAll) {
            for (Employee e : employees) {
                selectedEmployeeIds.add(e.getId());
            }
        }
        notifyDataSetChanged();
    }

    public List<String> getSelectedEmployeeIds() {
        return new ArrayList<>(selectedEmployeeIds);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEmployeeSalarySelectBinding binding = ItemEmployeeSalarySelectBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(employees.get(position));
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemEmployeeSalarySelectBinding binding;

        ViewHolder(ItemEmployeeSalarySelectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Employee employee) {
            binding.tvEmployeeId.setText(employee.getId());
            binding.tvEmployeeName.setText(employee.getName());
            
            // Mặc định hoặc có thể lấy từ dữ liệu công nếu có
            binding.tvHours.setText("160"); 
            binding.tvFactor.setText("1.0");

            binding.cbSelect.setOnCheckedChangeListener(null);
            binding.cbSelect.setChecked(selectedEmployeeIds.contains(employee.getId()));
            binding.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedEmployeeIds.add(employee.getId());
                } else {
                    selectedEmployeeIds.remove(employee.getId());
                }
            });
        }
    }
}
