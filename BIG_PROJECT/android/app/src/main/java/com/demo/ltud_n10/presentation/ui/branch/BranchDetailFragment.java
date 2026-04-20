package com.demo.ltud_n10.presentation.ui.branch;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
    private boolean isReadOnly = false;
    private String selectedManagerId;
    private List<Employee> employeeList = new ArrayList<>();

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
            isReadOnly = getArguments().getBoolean("isReadOnly", false);
        }

        binding.tvTitle.setText(title);
        
        loadEmployees();

        if (branch != null) {
            if (isReadOnly) {
                setupReadOnlyMode();
            } else {
                setupEditMode();
            }
        } else {
            setupAddMode();
        }

        binding.ivBack.setOnClickListener(v -> handleCancel());
        binding.btnCancel.setOnClickListener(v -> handleCancel());
        binding.cvStatus.setOnClickListener(v -> showStatusDialog());
        binding.btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadEmployees() {
        employeeRepository.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                employeeList = new ArrayList<>();
                for (Employee e : resource.data) {
                    if (e.getPosition() != null && 
                        (e.getPosition().equalsIgnoreCase("Quản lý") || e.getPosition().equalsIgnoreCase("Chủ"))) {
                        employeeList.add(e);
                    }
                }
                setupManagerDropdown();
            }
        });
    }

    private void setupManagerDropdown() {
        List<String> names = new ArrayList<>();
        for (Employee e : employeeList) {
            names.add(e.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, names);
        binding.actvManager.setAdapter(adapter);
        binding.actvManager.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            for (Employee e : employeeList) {
                if (e.getName().equals(selectedName)) {
                    selectedManagerId = e.getId();
                    break;
                }
            }
        });
    }

    private void showStatusDialog() {
        if (!isEditMode) return;
        
        String[] statuses = {"Đang hoạt động", "Ngưng hoạt động"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn trạng thái")
                .setItems(statuses, (dialog, which) -> {
                    updateStatusUI(statuses[which]);
                })
                .show();
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
        binding.layoutStatus.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.btnSubmit.setText("LƯU");
        binding.btnSubmit.setOnClickListener(v -> handleSave());
    }

    private void setupReadOnlyMode() {
        isEditMode = false;
        binding.etBranchName.setText(branch.getName());
        binding.etAddress.setText(branch.getAddress());
        binding.etPhoneNumber.setText(branch.getPhoneNumber());
        binding.actvManager.setText(branch.getManagerName(), false);
        selectedManagerId = branch.getManagerId();
        updateStatusUI(branch.getStatus());
        
        enableFields(false);
        binding.layoutStatus.setVisibility(View.VISIBLE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.btnCancel.setVisibility(View.GONE);
        
        binding.btnSubmit.setText("XÁC NHẬN");
        binding.btnSubmit.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
    }

    private void setupEditMode() {
        isEditMode = true;
        binding.etBranchName.setText(branch.getName());
        binding.etAddress.setText(branch.getAddress());
        binding.etPhoneNumber.setText(branch.getPhoneNumber());
        binding.actvManager.setText(branch.getManagerName(), false);
        selectedManagerId = branch.getManagerId();
        updateStatusUI(branch.getStatus());

        enableFields(true);
        binding.layoutStatus.setVisibility(View.VISIBLE);
        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.btnSubmit.setText("LƯU");
        binding.btnSubmit.setOnClickListener(v -> handleUpdate());
    }

    private void enableFields(boolean enabled) {
        binding.etBranchName.setEnabled(enabled);
        binding.etAddress.setEnabled(enabled);
        binding.etPhoneNumber.setEnabled(enabled);
        binding.actvManager.setEnabled(enabled);
    }

    private void handleSave() {
        if (!validate()) return;

        Branch newBranch = new Branch();
        newBranch.setName(binding.etBranchName.getText().toString());
        newBranch.setAddress(binding.etAddress.getText().toString());
        newBranch.setPhoneNumber(binding.etPhoneNumber.getText().toString());
        newBranch.setManagerId(selectedManagerId);
        newBranch.setStatus("Đang hoạt động");

        branchRepository.addBranch(newBranch).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                if (resource.data != null) {
                    Toast.makeText(requireContext(), "Đã thêm chi nhánh mới thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else if (resource.message != null) {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleUpdate() {
        if (!validate()) return;

        branch.setName(binding.etBranchName.getText().toString());
        branch.setAddress(binding.etAddress.getText().toString());
        branch.setPhoneNumber(binding.etPhoneNumber.getText().toString());
        branch.setManagerId(selectedManagerId);
        branch.setStatus(binding.tvStatus.getText().toString());

        branchRepository.updateBranch(branch).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                if (resource.data != null) {
                    Toast.makeText(requireContext(), "Đã cập nhật chi nhánh thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else if (resource.message != null) {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa chi nhánh này?")
                .setPositiveButton("Xóa", (d, w) -> handleDelete())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void handleDelete() {
        branchRepository.deleteBranch(branch.getId()).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null && resource.data) {
                Toast.makeText(requireContext(), "Đã xóa chi nhánh thành công", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else if (resource != null && resource.message != null) {
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCancel() {
        if (isEditMode) {
            String message = branch == null ? "Bạn có thông tin chưa lưu, xác nhận hủy?" : "Bạn có thông tin chỉnh sửa, xác nhận hủy?";
            new AlertDialog.Builder(requireContext())
                    .setMessage(message)
                    .setPositiveButton("Đồng ý", (d, w) -> Navigation.findNavController(requireView()).navigateUp())
                    .setNegativeButton("Không", null)
                    .show();
        } else {
            Navigation.findNavController(requireView()).navigateUp();
        }
    }

    private boolean validate() {
        String name = binding.etBranchName.getText().toString();
        String address = binding.etAddress.getText().toString();
        String phone = binding.etPhoneNumber.getText().toString();
        String manager = binding.actvManager.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Tên chi nhánh không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (address.isEmpty()) {
            Toast.makeText(requireContext(), "Địa chỉ không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.isEmpty()) {
            Toast.makeText(requireContext(), "SDT không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (manager.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn người quản lý", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
