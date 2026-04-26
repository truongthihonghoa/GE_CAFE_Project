package com.demo.ltud_n10.presentation.ui.branch;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.DialogConfirmCancelBinding;
import com.demo.ltud_n10.databinding.FragmentBranchDetailBinding;
import com.demo.ltud_n10.domain.model.Branch;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.repository.BranchRepository;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BranchDetailFragment extends Fragment {

    private FragmentBranchDetailBinding binding;
    private Branch branch;
    private String title;
    private boolean isEditMode = false;
    private List<Employee> staffEmployees = new ArrayList<>();
    private String selectedManagerId = null;

    @Inject
    BranchRepository branchRepository;

    @Inject
    EmployeeRepository employeeRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBranchDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            branch = (Branch) getArguments().getSerializable("branch");
            title = getArguments().getString("title");
        }

        binding.tvTitle.setText(title);
        
        loadStaffEmployees();

        if (branch != null) {
            setupViewMode();
        } else {
            setupAddMode();
        }

        setupTextWatchers();

        binding.ivBack.setOnClickListener(v -> handleCancel());
        binding.btnCancel.setOnClickListener(v -> handleCancel());
        
        binding.cvStatus.setOnClickListener(v -> {
            if (isEditMode) showStatusDialog();
        });

        binding.cvManager.setOnClickListener(v -> {
            if (isEditMode) showManagerDialog();
        });
    }

    private void loadStaffEmployees() {
        employeeRepository.getStaffEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                staffEmployees = resource.data;
                if (branch != null) {
                    updateManagerUI(selectedManagerId);
                }
            }
        });
    }

    private void setupTextWatchers() {
        binding.etBranchName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvErrorName.setVisibility(View.GONE);
            }
        });
        binding.etAddress.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvErrorAddress.setVisibility(View.GONE);
            }
        });
        binding.etPhoneNumber.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvErrorPhone.setVisibility(View.GONE);
            }
        });
    }

    private void showStatusDialog() {
        String[] statuses = {"Đang hoạt động", "Ngưng hoạt động"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn trạng thái")
                .setItems(statuses, (dialog, which) -> {
                    String selectedStatus = statuses[which];
                    updateStatusUI(selectedStatus);
                })
                .show();
    }

    private void showManagerDialog() {
        List<String> namesList = new ArrayList<>();
        namesList.add("--- Bỏ chọn quản lý ---");
        if (staffEmployees != null) {
            for (Employee emp : staffEmployees) {
                namesList.add(emp.getName());
            }
        }

        String[] names = namesList.toArray(new String[0]);

        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn quản lý")
                .setItems(names, (dialog, which) -> {
                    if (which == 0) {
                        selectedManagerId = null;
                        updateManagerUI(null);
                    } else {
                        Employee selected = staffEmployees.get(which - 1);
                        selectedManagerId = selected.getId();
                        updateManagerUI(selectedManagerId);
                    }
                })
                .show();
    }

    private void updateManagerUI(String managerId) {
        if (managerId == null || managerId.isEmpty()) {
            binding.tvManagerName.setText("Chọn quản lý");
            binding.tvManagerName.setTextColor(Color.parseColor("#64748B"));
            return;
        }

        boolean found = false;
        if (staffEmployees != null) {
            for (Employee emp : staffEmployees) {
                if (emp.getId().equals(managerId)) {
                    binding.tvManagerName.setText(emp.getName());
                    binding.tvManagerName.setTextColor(Color.parseColor("#1B431C"));
                    found = true;
                    break;
                }
            }
        }
        
        if (!found) {
            binding.tvManagerName.setText(managerId);
            binding.tvManagerName.setTextColor(Color.parseColor("#1B431C"));
        }
    }

    private void updateStatusUI(String status) {
        binding.tvStatus.setText(status);
        if ("Ngưng hoạt động".equals(status)) {
            binding.cvStatus.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
            binding.cvStatus.setStrokeColor(Color.parseColor("#721C24"));
            binding.tvStatus.setTextColor(Color.parseColor("#721C24"));
        } else {
            binding.cvStatus.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#B3C5B5")));
            binding.cvStatus.setStrokeColor(Color.parseColor("#1B431C"));
            binding.tvStatus.setTextColor(Color.parseColor("#1B431C"));
        }
    }

    private void setupAddMode() {
        isEditMode = true;
        selectedManagerId = null;
        binding.layoutStatus.setVisibility(View.GONE);
        binding.btnSubmit.setText("LƯU");
        binding.btnSubmit.setOnClickListener(v -> handleSave());
        enableFields(true);
    }

    private void setupViewMode() {
        isEditMode = false;
        binding.etBranchName.setText(branch.getName());
        binding.etAddress.setText(branch.getAddress());
        binding.etPhoneNumber.setText(branch.getPhoneNumber());
        
        selectedManagerId = branch.getManagerName();
        updateManagerUI(selectedManagerId);

        updateStatusUI(branch.getStatus());
        enableFields(false);
        binding.layoutStatus.setVisibility(View.GONE); 
        
        binding.btnSubmit.setText("CHỈNH SỬA");
        binding.btnSubmit.setOnClickListener(v -> setupEditMode());
    }

    private void setupEditMode() {
        isEditMode = true;
        binding.tvTitle.setText("CHỈNH SỬA CHI NHÁNH");
        enableFields(true);
        binding.layoutStatus.setVisibility(View.VISIBLE);
        binding.btnSubmit.setText("LƯU");
        binding.btnSubmit.setOnClickListener(v -> handleUpdate());
    }

    private void enableFields(boolean enabled) {
        binding.etBranchName.setEnabled(enabled);
        binding.etAddress.setEnabled(enabled);
        binding.etPhoneNumber.setEnabled(enabled);
        binding.cvManager.setClickable(enabled);
        binding.cvManager.setFocusable(enabled);
    }

    private void handleSave() {
        if (!validate()) return;
        Branch newBranch = new Branch();
        newBranch.setName(binding.etBranchName.getText().toString().trim());
        newBranch.setAddress(binding.etAddress.getText().toString().trim());
        newBranch.setPhoneNumber(binding.etPhoneNumber.getText().toString().trim());
        newBranch.setManagerName(selectedManagerId);
        newBranch.setStatus("Đang hoạt động");

        binding.btnSubmit.setEnabled(false);
        branchRepository.addBranch(newBranch).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessToast("Thêm chi nhánh thành công");
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    binding.btnSubmit.setEnabled(true);
//                    showErrorToast(resource.message != null ? resource.message : "Lỗi hệ thống");
                }
            }
        });
    }

    private void handleUpdate() {
        if (!validate()) return;
        branch.setName(binding.etBranchName.getText().toString().trim());
        branch.setAddress(binding.etAddress.getText().toString().trim());
        branch.setPhoneNumber(binding.etPhoneNumber.getText().toString().trim());
        branch.setManagerName(selectedManagerId);
        branch.setStatus(binding.tvStatus.getText().toString());

        binding.btnSubmit.setEnabled(false);
        branchRepository.updateBranch(branch).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessToast("Cập nhật thông tin thành công");
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    binding.btnSubmit.setEnabled(true);
//                    showErrorToast(resource.message != null ? resource.message : "Lỗi hệ thống");
                }
            }
        });
    }

    private void showSuccessToast(String message) {
        View layout = getLayoutInflater().inflate(R.layout.layout_custom_toast, null);
        TextView tvMessage = layout.findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setView(layout);
        toast.show();
    }

    private void showErrorToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void handleCancel() {
        if (!isEditMode) {
            Navigation.findNavController(requireView()).navigateUp();
            return;
        }

        DialogConfirmCancelBinding dialogBinding = DialogConfirmCancelBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogBinding.btnDialogConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            Navigation.findNavController(requireView()).navigateUp();
        });

        dialogBinding.btnDialogCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private boolean validate() {
        boolean isValid = true;
        
        if (binding.etBranchName.getText().toString().trim().isEmpty()) {
            binding.tvErrorName.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            binding.tvErrorName.setVisibility(View.GONE);
        }

        if (binding.etAddress.getText().toString().trim().isEmpty()) {
            binding.tvErrorAddress.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            binding.tvErrorAddress.setVisibility(View.GONE);
        }

        if (binding.etPhoneNumber.getText().toString().trim().isEmpty()) {
            binding.tvErrorPhone.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            binding.tvErrorPhone.setVisibility(View.GONE);
        }

        return isValid;
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
