package com.demo.ltud_n10.presentation.ui.branch;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
    private String selectedManagerId = null; // Lưu ID quản lý để gửi lên API

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
                // Sau khi load xong list nhân viên, nếu đang ở ViewMode thì hiển thị tên quản lý đúng theo ID
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

        // Tìm tên nhân viên dựa trên ID
        boolean found = false;
        for (Employee emp : staffEmployees) {
            if (emp.getId().equals(managerId)) {
                binding.tvManagerName.setText(emp.getName());
                binding.tvManagerName.setTextColor(Color.parseColor("#1B431C"));
                found = true;
                break;
            }
        }
        
        if (!found) {
            binding.tvManagerName.setText(managerId); // Nếu chưa load kịp thì hiện tạm ID
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
        binding.layoutStatus.setVisibility(View.GONE); // Ẩn trạng thái khi thêm mới
        binding.btnSubmit.setText("LƯU");
        binding.btnSubmit.setOnClickListener(v -> handleSave());
        enableFields(true);
    }

    private void setupViewMode() {
        isEditMode = false;
        binding.etBranchName.setText(branch.getName());
        binding.etAddress.setText(branch.getAddress());
        binding.etPhoneNumber.setText(branch.getPhoneNumber());
        
        selectedManagerId = branch.getManagerName(); // ManagerName trong Domain đang chứa ID
        updateManagerUI(selectedManagerId);

        updateStatusUI(branch.getStatus());
        enableFields(false);
        
        // Theo yêu cầu: Chỉ khi nhấn chỉnh sửa mới hiển thị trạng thái
        binding.layoutStatus.setVisibility(View.GONE); 
        
        binding.btnSubmit.setText("CHỈNH SỬA");
        binding.btnSubmit.setOnClickListener(v -> setupEditMode());
    }

    private void setupEditMode() {
        isEditMode = true;
        binding.tvTitle.setText("CHỈNH SỬA CHI NHÁNH");
        enableFields(true);
        
        // Hiển thị trạng thái khi vào chế độ chỉnh sửa
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
        if (!validateAndFocus()) return;
        Branch newBranch = new Branch();
        newBranch.setName(binding.etBranchName.getText().toString().trim());
        newBranch.setAddress(binding.etAddress.getText().toString().trim());
        newBranch.setPhoneNumber(binding.etPhoneNumber.getText().toString().trim());
        newBranch.setManagerName(selectedManagerId); // Gửi ID quản lý
        newBranch.setStatus("Đang hoạt động");

        binding.btnSubmit.setEnabled(false);
        branchRepository.addBranch(newBranch).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    binding.btnSubmit.setEnabled(true);
                    Toast.makeText(requireContext(), resource.message != null ? resource.message : "Lỗi thêm chi nhánh", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void handleUpdate() {
        if (!validateAndFocus()) return;
        branch.setName(binding.etBranchName.getText().toString().trim());
        branch.setAddress(binding.etAddress.getText().toString().trim());
        branch.setPhoneNumber(binding.etPhoneNumber.getText().toString().trim());
        branch.setManagerName(selectedManagerId); // Gửi ID quản lý

        branch.setStatus(binding.tvStatus.getText().toString());

        binding.btnSubmit.setEnabled(false);
        branchRepository.updateBranch(branch).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    binding.btnSubmit.setEnabled(true);
                    Toast.makeText(requireContext(), resource.message != null ? resource.message : "Lỗi cập nhật", Toast.LENGTH_LONG).show();
                }
            }
        });
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

    private boolean validateAndFocus() {
        // Tên chi nhánh
        String name = binding.etBranchName.getText().toString().trim();
        if (name.isEmpty()) {
            binding.tvErrorName.setVisibility(View.VISIBLE);
            binding.etBranchName.requestFocus();
            return false;
        } else {
            binding.tvErrorName.setVisibility(View.GONE);
        }

        // Địa chỉ
        String address = binding.etAddress.getText().toString().trim();
        if (address.isEmpty()) {
            binding.tvErrorAddress.setVisibility(View.VISIBLE);
            binding.etAddress.requestFocus();
            return false;
        } else {
            binding.tvErrorAddress.setVisibility(View.GONE);
        }

        // Số điện thoại
        String phone = binding.etPhoneNumber.getText().toString().trim();
        if (phone.isEmpty()) {
            binding.tvErrorPhone.setVisibility(View.VISIBLE);
            binding.etPhoneNumber.requestFocus();
            return false;
        } else {
            binding.tvErrorPhone.setVisibility(View.GONE);
        }

        return true;
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
