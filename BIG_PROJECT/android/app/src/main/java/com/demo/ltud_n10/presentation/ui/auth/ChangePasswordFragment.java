package com.demo.ltud_n10.presentation.ui.auth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.FragmentChangePasswordBinding;
import com.google.android.material.button.MaterialButton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCancel.setOnClickListener(v -> showCancelConfirmDialog());
        binding.tvBackToHome.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        binding.btnConfirm.setOnClickListener(v -> {
            if (validateInputs()) {
                showSuccessDialog("Đổi mật khẩu thành công");
            }
        });

        // Xóa lỗi khi người dùng bắt đầu nhập lại
        setupErrorClearing();
    }

    private void showSuccessDialog(String message) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_success_notification);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.TOP);
            
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.y = 50;
            window.setAttributes(layoutParams);
        }

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                Navigation.findNavController(requireView()).navigateUp();
            }
        }, 2000);
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String oldPass = binding.etOldPassword.getText().toString().trim();
        String newPass = binding.etNewPassword.getText().toString().trim();
        String confirmPass = binding.etConfirmPassword.getText().toString().trim();

        // Kiểm tra mật khẩu cũ
        if (oldPass.isEmpty()) {
            binding.tvOldPasswordError.setText("Vui lòng nhập mật khẩu cũ");
            binding.tvOldPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!oldPass.equals("password123")) {
            binding.tvOldPasswordError.setText("*Mật khẩu không chính xác, vui lòng thử lại*");
            binding.tvOldPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            binding.tvOldPasswordError.setVisibility(View.GONE);
        }

        // Kiểm tra mật khẩu mới
        if (newPass.isEmpty()) {
            binding.tvNewPasswordError.setText("Vui lòng nhập mật khẩu mới");
            binding.tvNewPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!newPass.matches(".*[A-Z].*") || !newPass.matches(".*[a-z].*") || !newPass.matches(".*\\d.*") || newPass.length() < 8) {
            binding.tvNewPasswordError.setText("*Mật khẩu phải từ 8 kí tự bao gồm chữ số, chữ hoa và chữ thường*");
            binding.tvNewPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            binding.tvNewPasswordError.setVisibility(View.GONE);
        }

        // Kiểm tra xác nhận mật khẩu
        if (confirmPass.isEmpty()) {
            binding.tvConfirmPasswordError.setText("Vui lòng nhập mật khẩu xác nhận");
            binding.tvConfirmPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!confirmPass.equals(newPass)) {
            binding.tvConfirmPasswordError.setText("*Mật khẩu xác nhận không khớp*");
            binding.tvConfirmPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            binding.tvConfirmPasswordError.setVisibility(View.GONE);
        }

        return isValid;
    }

    private void setupErrorClearing() {
        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                binding.tvOldPasswordError.setVisibility(View.GONE);
                binding.tvNewPasswordError.setVisibility(View.GONE);
                binding.tvConfirmPasswordError.setVisibility(View.GONE);
            }
        };
        binding.etOldPassword.addTextChangedListener(clearErrorWatcher);
        binding.etNewPassword.addTextChangedListener(clearErrorWatcher);
        binding.etConfirmPassword.addTextChangedListener(clearErrorWatcher);
    }

    private void showCancelConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_cancel, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        MaterialButton btnNo = dialogView.findViewById(R.id.btnDialogCancel);
        MaterialButton btnYes = dialogView.findViewById(R.id.btnDialogConfirm);

        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            Navigation.findNavController(requireView()).navigateUp();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
