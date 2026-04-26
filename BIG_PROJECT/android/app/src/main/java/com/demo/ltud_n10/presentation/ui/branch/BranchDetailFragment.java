package com.demo.ltud_n10.presentation.ui.branch;

import android.graphics.Color;
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

        setupTextWatchers();

        binding.ivBack.setOnClickListener(v -> handleCancel());
        binding.btnCancel.setOnClickListener(v -> handleCancel());
        
        // SỬA LỖI: Cho phép nhấn vào Dropdown để chọn trạng thái
        binding.cvStatus.setOnClickListener(v -> {
            if (isEditMode) showStatusDialog();
        });
    }

    private void setupTextWatchers() {
        binding.etBranchName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvErrorName.setVisibility(View.GONE);
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
                    if (branch != null) {
                        branch.setStatus(selectedStatus); // Cập nhật vào đối tượng
                    }
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

        binding.btnSubmit.setEnabled(false);
        branchRepository.addBranch(newBranch).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                binding.btnSubmit.setEnabled(true);
            }
        });
    }

    private void handleUpdate() {
        if (!validate()) return;
        branch.setName(binding.etBranchName.getText().toString());
        branch.setAddress(binding.etAddress.getText().toString());
        branch.setPhoneNumber(binding.etPhoneNumber.getText().toString());
        branch.setManagerName(binding.etManagerName.getText().toString());
        branch.setStatus(binding.tvStatus.getText().toString()); // LẤY TỪ Dropdown

        binding.btnSubmit.setEnabled(false);
        branchRepository.updateBranch(branch).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                binding.btnSubmit.setEnabled(true);
                Toast.makeText(requireContext(), "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCancel() {
        Navigation.findNavController(requireView()).navigateUp();
    }

    private boolean validate() {
        return !binding.etBranchName.getText().toString().isEmpty();
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
