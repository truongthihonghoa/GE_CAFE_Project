package com.demo.ltud_n10.presentation.ui.account;

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

import com.demo.ltud_n10.databinding.FragmentAccountDetailBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountDetailFragment extends Fragment {

    private FragmentAccountDetailBinding binding;
    private User user;
    private String title;
    private boolean isEditMode = false;

    @Inject
    UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            title = getArguments().getString("title");
        }

        binding.tvTitle.setText(title);

        if (user != null) {
            setupEditMode();
        } else {
            setupAddMode();
        }

        binding.ivBack.setOnClickListener(v -> handleCancel());
        binding.btnCancel.setOnClickListener(v -> handleCancel());
        binding.cvRole.setOnClickListener(v -> showRoleDialog());
        binding.cvStatus.setOnClickListener(v -> showStatusDialog());
    }

    private void setupAddMode() {
        isEditMode = true;
        binding.layoutStatus.setVisibility(View.GONE);
        binding.btnSave.setOnClickListener(v -> handleSave());
    }

    private void setupEditMode() {
        isEditMode = true;
        binding.etUsername.setText(user.getUsername());
        binding.etPassword.setText(user.getPassword());
        binding.etName.setText(user.getName());
        binding.tvRole.setText(user.getRole());
        updateStatusUI(user.getStatus());
        
        binding.layoutStatus.setVisibility(View.VISIBLE);
        binding.btnSave.setOnClickListener(v -> handleUpdate());
    }

    private void updateStatusUI(String status) {
        binding.tvStatus.setText(status);
        if ("Ngưng hoạt động".equals(status)) {
            binding.cvStatus.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
            binding.tvStatus.setTextColor(Color.parseColor("#721C24"));
        } else {
            binding.cvStatus.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#E8F8EF")));
            binding.tvStatus.setTextColor(Color.parseColor("#2ECC71"));
        }
    }

    private void showRoleDialog() {
        String[] roles = {"ADMIN", "EMPLOYEE"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn quyền hạn")
                .setItems(roles, (dialog, which) -> {
                    binding.tvRole.setText(roles[which]);
                })
                .show();
    }

    private void showStatusDialog() {
        String[] statuses = {"Đang hoạt động", "Ngưng hoạt động"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn trạng thái")
                .setItems(statuses, (dialog, which) -> {
                    if ("Ngưng hoạt động".equals(statuses[which])) {
                        confirmSuspendAccount();
                    } else {
                        updateStatusUI(statuses[which]);
                    }
                })
                .show();
    }

    private void confirmSuspendAccount() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn ngưng hoạt động tài khoản này không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    updateStatusUI("Ngưng hoạt động");
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void handleSave() {
        if (!validate()) return;

        User newUser = new User();
        newUser.setUsername(binding.etUsername.getText().toString());
        newUser.setPassword(binding.etPassword.getText().toString());
        newUser.setName(binding.etName.getText().toString());
        newUser.setRole(binding.tvRole.getText().toString());
        newUser.setStatus("Đang hoạt động");

        userRepository.addUser(newUser).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Thêm thông tin thành công", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUpdate() {
        if (!validate()) return;

        user.setUsername(binding.etUsername.getText().toString());
        user.setPassword(binding.etPassword.getText().toString());
        user.setName(binding.etName.getText().toString());
        user.setRole(binding.tvRole.getText().toString());
        user.setStatus(binding.tvStatus.getText().toString());

        userRepository.updateUser(user).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Cập nhật tài khoản thành công", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }

    private void handleCancel() {
        new AlertDialog.Builder(requireContext())
                .setMessage("Bạn có thông tin thay đổi chưa được lưu, xác nhận hủy?")
                .setPositiveButton("Đồng ý", (d, w) -> Navigation.findNavController(requireView()).navigateUp())
                .setNegativeButton("Không", null)
                .show();
    }

    private boolean validate() {
        String username = binding.etUsername.getText().toString();
        String password = binding.etPassword.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(requireContext(), "Tên đăng nhập trống. vui lòng nhập lại.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(requireContext(), "Mật khẩu không hợp lệ. Vui lòng nhập theo quy định.", Toast.LENGTH_SHORT).show();
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
