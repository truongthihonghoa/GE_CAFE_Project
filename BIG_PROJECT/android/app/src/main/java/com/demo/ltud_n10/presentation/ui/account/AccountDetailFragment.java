package com.demo.ltud_n10.presentation.ui.account;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.FragmentAccountDetailBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.UserRepository;
import com.google.android.material.button.MaterialButton;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountDetailFragment extends Fragment {

    private FragmentAccountDetailBinding binding;
    private User user;
    private String title;
    private boolean isReadOnly = false;
    private String loggedInUserName = "";
    private String loggedInUserRole = "EMPLOYEE";

    @Inject
    UserRepository userRepository;

    @Inject
    AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser != null) {
                loggedInUserName = currentUser.getName();
                loggedInUserRole = currentUser.getRole();
                if (user != null) {
                    applyPermissions();
                }
            }
        });

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            title = getArguments().getString("title");
            isReadOnly = getArguments().getBoolean("isReadOnly", false);
        }

        binding.tvTitle.setText(title);

        if (user != null) {
            setupEditMode();
        } else {
            setupAddMode();
        }

        setupTextWatchers();

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        binding.btnCancel.setOnClickListener(v -> handleBackAction(v));

        binding.cvRole.setOnClickListener(v -> {
            if (binding.cvRole.isEnabled() && !isReadOnly) showRoleDialog();
        });

        binding.cvStatus.setOnClickListener(v -> {
            if (binding.cvStatus.isEnabled() && !isReadOnly) showStatusDialog();
        });
    }

    private void setupAddMode() {
        binding.layoutStatus.setVisibility(View.GONE);
        binding.layoutSwitches.setVisibility(View.VISIBLE);
        binding.btnSave.setOnClickListener(v -> handleSave());
    }

    private void setupEditMode() {
        binding.etUsername.setText(user.getUsername());
        binding.etPassword.setText("******");
        binding.etPassword.setEnabled(false);
        binding.etPassword.setAlpha(0.6f);

        binding.etName.setText(user.getName());
        binding.etMaNv.setText(user.getMaNv());
        binding.tvRole.setText(user.getRole().equals("ADMIN") ? "Quản lý" : "Nhân viên");
        updateStatusUI(user.getStatus());
        binding.layoutStatus.setVisibility(View.VISIBLE);
        binding.layoutSwitches.setVisibility(View.GONE);

        // KIỂM TRA CHỈ XEM (TÊN LÀI HOẶC FLAG READONLY)
        if (isReadOnly || (user.getName() != null && user.getName().equals("Trần Thị Thúy Lài"))) {
            setFieldsDisabledDarkText();
            binding.btnSave.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.GONE);
        } else {
            applyPermissions();
            binding.btnSave.setOnClickListener(v -> handleUpdate());
        }
    }

    private void setFieldsDisabledDarkText() {
        int darkColor = Color.parseColor("#333333");

        binding.etUsername.setEnabled(false);
        binding.etUsername.setTextColor(darkColor);
        binding.etUsername.setAlpha(1.0f);

        binding.etPassword.setEnabled(false);
        binding.etPassword.setTextColor(darkColor);
        binding.etPassword.setAlpha(1.0f);

        binding.etName.setEnabled(false);
        binding.etName.setTextColor(darkColor);
        binding.etName.setAlpha(1.0f);

        binding.etMaNv.setEnabled(false);
        binding.etMaNv.setTextColor(darkColor);
        binding.etMaNv.setAlpha(1.0f);

        binding.tvRole.setTextColor(darkColor);
        binding.cvRole.setEnabled(false);
        binding.cvRole.setAlpha(1.0f);

        binding.tvStatus.setTextColor(darkColor);
        binding.cvStatus.setEnabled(false);
        binding.cvStatus.setAlpha(1.0f);
    }

    private void setupTextWatchers() {
        binding.etUsername.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.tvUsernameError != null) binding.tvUsernameError.setVisibility(View.GONE);
            }
        });
        binding.etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.tvPasswordError != null) binding.tvPasswordError.setVisibility(View.GONE);
            }
        });
        binding.etName.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.tvNameError != null) binding.tvNameError.setVisibility(View.GONE);
            }
        });
    }

    private void applyPermissions() {
        if (user == null) return;
        boolean isEditingSelf = (loggedInUserName != null && loggedInUserName.equals(user.getName()));

        setFieldsEnabled(true);
        binding.etUsername.setEnabled(false);
        binding.etUsername.setAlpha(0.6f);
        binding.etPassword.setEnabled(false);
        binding.etPassword.setAlpha(0.6f);

        if (isEditingSelf) {
            binding.cvRole.setEnabled(false);
            binding.cvRole.setAlpha(0.6f);
            binding.cvStatus.setEnabled(false);
            binding.cvStatus.setAlpha(0.6f);
        } else {
            binding.cvRole.setEnabled(true);
            binding.cvRole.setAlpha(1.0f);
            binding.cvStatus.setEnabled(true);
            binding.cvStatus.setAlpha(1.0f);
        }

        binding.btnSave.setText("LƯU");
        binding.btnCancel.setVisibility(View.VISIBLE);
    }

    private void setFieldsEnabled(boolean enabled) {
        float alpha = enabled ? 1.0f : 0.6f;
        binding.etUsername.setEnabled(enabled);
        binding.etUsername.setAlpha(alpha);
        binding.etPassword.setEnabled(enabled);
        binding.etPassword.setAlpha(alpha);
        binding.etName.setEnabled(enabled);
        binding.etName.setAlpha(alpha);
        binding.etMaNv.setEnabled(enabled);
        binding.etMaNv.setAlpha(alpha);
        binding.cvRole.setEnabled(enabled);
        binding.cvRole.setAlpha(alpha);
        binding.cvStatus.setEnabled(enabled);
        binding.cvStatus.setAlpha(alpha);
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
        String[] roles = {"Quản lý", "Nhân viên"};
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

    private void showSuccessNotification(String message) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_success_notification);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.TOP);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.y = 50;
            window.setAttributes(lp);
        }
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        dialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                Navigation.findNavController(requireView()).popBackStack();
            }
        }, 2000);
    }

    private void handleSave() {
        if (!validateFields()) return;
        User newUser = new User();
        newUser.setUsername(binding.etUsername.getText().toString().trim());
        newUser.setPassword(binding.etPassword.getText().toString().trim());
        newUser.setName(binding.etName.getText().toString().trim());
        newUser.setMaNv(binding.etMaNv.getText().toString().trim());
        newUser.setRole(binding.tvRole.getText().toString().equals("Quản lý") ? "ADMIN" : "EMPLOYEE");
        newUser.setStatus(binding.switchActive.isChecked() ? "Đang hoạt động" : "Ngưng hoạt động");
        newUser.setStaff(binding.switchStaff.isChecked());

        userRepository.addUser(newUser).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                showSuccessNotification("Thêm tài khoản thành công");
            } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleUpdate() {
        if (!validateFields()) return;
        user.setName(binding.etName.getText().toString().trim());
        user.setMaNv(binding.etMaNv.getText().toString().trim());
        user.setRole(binding.tvRole.getText().toString().equals("Quản lý") ? "ADMIN" : "EMPLOYEE");
        user.setStatus(binding.tvStatus.getText().toString());
        userRepository.updateUser(user).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                showSuccessNotification("Cập nhật tài khoản thành công");
            } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleBackAction(View view) {
        if (isReadOnly || binding.btnCancel.getVisibility() == View.GONE) {
            Navigation.findNavController(view).popBackStack();
        } else {
            handleCancel(view);
        }
    }

    private void handleCancel(View view) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_cancel);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        MaterialButton btnNo = dialog.findViewById(R.id.btnDialogCancel);
        MaterialButton btnYes = dialog.findViewById(R.id.btnDialogConfirm);
        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            Navigation.findNavController(view).popBackStack();
        });
        dialog.show();
    }

    private boolean validateFields() {
        boolean isValid = true;
        if (binding.etUsername.getText().toString().trim().isEmpty()) {
            if (binding.tvUsernameError != null) binding.tvUsernameError.setVisibility(View.VISIBLE);
            isValid = false;
        }
        // Khi thêm mới thì bắt buộc mật khẩu, khi sửa thì không (vì mật khẩu bị disable)
        if (user == null && binding.etPassword.getText().toString().trim().isEmpty()) {
            if (binding.tvPasswordError != null) binding.tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (binding.etName.getText().toString().trim().isEmpty()) {
            if (binding.tvNameError != null) binding.tvNameError.setVisibility(View.VISIBLE);
            isValid = false;
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
