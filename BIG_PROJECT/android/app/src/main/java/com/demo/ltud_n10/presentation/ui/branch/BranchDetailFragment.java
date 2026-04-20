package com.demo.ltud_n10.presentation.ui.branch;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.databinding.FragmentBranchDetailBinding;
import com.demo.ltud_n10.domain.model.Branch;
import com.demo.ltud_n10.domain.repository.BranchRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BranchDetailFragment extends Fragment {

    private FragmentBranchDetailBinding binding;
    private Branch branch;
    private String title;
    private boolean isEditMode = false;

    @Inject
    BranchRepository branchRepository;

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
        
        if (branch != null) {
            setupViewMode();
        } else {
            setupAddMode();
        }

        binding.ivBack.setOnClickListener(v -> handleCancel());
        binding.btnCancel.setOnClickListener(v -> handleCancel());
        
        binding.cvStatus.setOnClickListener(v -> showStatusDialog());
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
        binding.btnSubmit.setText("LƯU");
        binding.btnSubmit.setOnClickListener(v -> handleSave());
    }

    private void setupViewMode() {
        isEditMode = false;
        binding.etBranchName.setText(branch.getName());
        binding.etAddress.setText(branch.getAddress());
        binding.etPhoneNumber.setText(branch.getPhoneNumber());
        binding.etManagerName.setText(branch.getManagerName());
        updateStatusUI(branch.getStatus());
        
        enableFields(false);
        binding.layoutStatus.setVisibility(View.VISIBLE);
        binding.btnSubmit.setText("CHỈNH SỬA");
        binding.btnSubmit.setOnClickListener(v -> setupEditMode());
    }

    private void setupEditMode() {
        isEditMode = true;
        binding.tvTitle.setText("CHỈNH SỬA CHI NHÁNH");
        enableFields(true);
        binding.btnSubmit.setText("LƯU");
        binding.btnSubmit.setOnClickListener(v -> handleUpdate());
    }

    private void enableFields(boolean enabled) {
        binding.etBranchName.setEnabled(enabled);
        binding.etAddress.setEnabled(enabled);
        binding.etPhoneNumber.setEnabled(enabled);
        binding.etManagerName.setEnabled(enabled);
    }

    private void handleSave() {
        if (!validate()) return;

        Branch newBranch = new Branch();
        newBranch.setName(binding.etBranchName.getText().toString());
        newBranch.setAddress(binding.etAddress.getText().toString());
        newBranch.setPhoneNumber(binding.etPhoneNumber.getText().toString());
        newBranch.setManagerName(binding.etManagerName.getText().toString());
        newBranch.setStatus("Đang hoạt động");

        branchRepository.addBranch(newBranch).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                Toast.makeText(requireContext(), "Đã thêm chi nhánh mới thành công", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }

    private void handleUpdate() {
        if (!validate()) return;

        branch.setName(binding.etBranchName.getText().toString());
        branch.setAddress(binding.etAddress.getText().toString());
        branch.setPhoneNumber(binding.etPhoneNumber.getText().toString());
        branch.setManagerName(binding.etManagerName.getText().toString());
        branch.setStatus(binding.tvStatus.getText().toString());

        branchRepository.updateBranch(branch).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                Toast.makeText(requireContext(), "Đã cập nhật chi nhánh thành công", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
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
        if (phone.length() != 10 || !phone.startsWith("0")) {
            Toast.makeText(requireContext(), "Số điện thoại không hợp lệ. Vui lòng nhập 10 chữ số và bắt đầu bằng 0", Toast.LENGTH_SHORT).show();
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
