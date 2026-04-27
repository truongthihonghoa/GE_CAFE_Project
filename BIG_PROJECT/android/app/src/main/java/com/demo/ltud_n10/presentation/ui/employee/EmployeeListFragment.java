package com.demo.ltud_n10.presentation.ui.employee;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.DialogCustomConfirmBinding;
import com.demo.ltud_n10.databinding.FragmentEmployeeListBinding;
import com.demo.ltud_n10.domain.model.Contract;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.presentation.ui.contract.ContractViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EmployeeListFragment extends Fragment {

    private FragmentEmployeeListBinding binding;
    private EmployeeViewModel viewModel;
    private ContractViewModel contractViewModel;
    private EmployeeAdapter employeeAdapter;
    private List<Employee> fullEmployeeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmployeeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EmployeeViewModel.class);
        contractViewModel = new ViewModelProvider(this).get(ContractViewModel.class);

        setupUI();
        setupRecyclerView();
        observeViewModel();
        setupFilters();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadEmployees();
        }
    }

    private void setupUI() {
        if (binding == null) return;

        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        binding.btnAddEmployee.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("title", "Thêm nhân viên");
            NavHostFragment.findNavController(this).navigate(R.id.action_employeeListFragment_to_employeeDetailFragment, args);
        });

        String[] positions = {"Tất cả chức vụ", "Quản lý", "Pha chế", "Phục vụ"};
        ArrayAdapter<String> posAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, positions);
        posAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPosition.setAdapter(posAdapter);

        updateStatusSpinner(new ArrayList<>());
    }

    private void updateStatusSpinner(List<String> statuses) {
        if (!isAdded() || binding == null) return;

        List<String> displayStatuses = new ArrayList<>();
        displayStatuses.add("Tất cả trạng thái");
        displayStatuses.add("Đang làm");
        displayStatuses.add("Ngừng hoạt động");

        for (String s : statuses) {
            if (s != null && !displayStatuses.contains(s)) {
                displayStatuses.add(s);
            }
        }

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, displayStatuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerStatus.setAdapter(statusAdapter);
    }

    private void setupFilters() {
        if (binding == null) return;

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        binding.spinnerPosition.setOnItemSelectedListener(filterListener);
        binding.spinnerStatus.setOnItemSelectedListener(filterListener);
    }

    private void applyFilters() {
        if (binding == null || fullEmployeeList == null) return;

        String query = binding.etSearch.getText().toString().toLowerCase().trim();
        Object selectedPos = binding.spinnerPosition.getSelectedItem();
        Object selectedStatus = binding.spinnerStatus.getSelectedItem();

        String positionFilter = selectedPos != null ? selectedPos.toString().trim() : "Tất cả chức vụ";
        String statusFilter = selectedStatus != null ? selectedStatus.toString().trim() : "Tất cả trạng thái";

        List<Employee> filteredList = fullEmployeeList.stream()
                .filter(e -> {
                    if (e == null) return false;

                    String name = e.getName() != null ? e.getName().toLowerCase().trim() : "";
                    String pos = e.getPosition() != null ? e.getPosition().trim() : "";

                    String status = e.getStatus();
                    if (status == null || status.trim().isEmpty()) {
                        status = "Đang làm";
                    } else {
                        status = status.trim();
                    }

                    boolean matchesQuery = query.isEmpty() || name.contains(query);
                    boolean matchesPos = positionFilter.equals("Tất cả chức vụ") || pos.equalsIgnoreCase(positionFilter);
                    boolean matchesStatus = statusFilter.equals("Tất cả trạng thái") || status.equalsIgnoreCase(statusFilter);

                    return matchesQuery && matchesPos && matchesStatus;
                })
                .collect(Collectors.toList());

        employeeAdapter.submitList(filteredList);
    }

    private void setupRecyclerView() {
        if (binding == null) return;

        employeeAdapter = new EmployeeAdapter(new EmployeeAdapter.OnEmployeeClickListener() {
            @Override
            public void onEditClick(Employee employee) {
                if (!isAdded()) return;
                Bundle args = new Bundle();
                args.putSerializable("employee", employee);
                args.putString("title", "Chỉnh sửa nhân viên");
                NavHostFragment.findNavController(EmployeeListFragment.this).navigate(R.id.action_employeeListFragment_to_employeeDetailFragment, args);
            }

            @Override
            public void onDeleteClick(Employee employee) {
                showDeleteConfirmation(employee);
            }
        });
        binding.rvEmployees.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvEmployees.setAdapter(employeeAdapter);
    }

    private void observeViewModel() {
        viewModel.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                fullEmployeeList = resource.data != null ? resource.data : new ArrayList<>();
                applyFilters();
            } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                showErrorDialog("THÔNG BÁO LỖI", "Không thể tải danh sách: " + resource.message);
            }
        });

        contractViewModel.getContracts().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS && resource.data != null) {
                Set<String> statuses = new HashSet<>();
                for (Contract c : resource.data) {
                    if (c.getStatus() != null) statuses.add(c.getStatus());
                }
                updateStatusSpinner(new ArrayList<>(statuses));
            }
        });
    }

    private void showDeleteConfirmation(Employee employee) {
        showConfirmDialog("XÁC NHẬN XÓA", "Bạn có chắc chắn muốn xóa thông tin nhân viên này không ?", "Hủy", "Xóa", () -> {
            viewModel.deleteEmployee(employee.getId()).observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Đã xóa nhân viên thành công", Toast.LENGTH_SHORT).show();
                    viewModel.loadEmployees();
                } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                    showErrorToast("Không thể xóa nhân viên: " + resource.message);
                }
            });
        });
    }

    private void showConfirmDialog(String title, String message, String negativeText, String positiveText, Runnable onConfirm) {
        if (!isAdded() || binding == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCustomConfirmBinding dialogBinding = DialogCustomConfirmBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogBinding.tvTitle.setText(title);
        dialogBinding.tvMessage.setText(message);
        dialogBinding.ivIcon.setImageResource(R.drawable.ic_warning_outline);
        dialogBinding.btnNegative.setText(negativeText);
        dialogBinding.btnPositive.setText(positiveText);
        dialogBinding.btnNegative.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnPositive.setOnClickListener(v -> {
            dialog.dismiss();
            if (onConfirm != null) onConfirm.run();
        });
        dialog.show();
    }

    private void showErrorDialog(String title, String message) {
        if (!isAdded() || binding == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCustomConfirmBinding dialogBinding = DialogCustomConfirmBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogBinding.tvTitle.setText(title);
        dialogBinding.tvMessage.setText(message);
        dialogBinding.ivIcon.setImageResource(R.drawable.ic_error_x);
        dialogBinding.btnNegative.setText("Thoát");
        dialogBinding.btnPositive.setText("Quay lại");
        dialogBinding.btnNegative.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnPositive.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showErrorToast(String msg) {
        if (!isAdded()) return;
        View layout = getLayoutInflater().inflate(R.layout.layout_custom_toast_error, null);
        TextView tvMessage = layout.findViewById(R.id.tvMessage);
        tvMessage.setText(msg);
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}