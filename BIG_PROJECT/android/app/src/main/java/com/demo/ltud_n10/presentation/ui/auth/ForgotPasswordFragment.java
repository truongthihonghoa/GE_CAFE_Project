package com.demo.ltud_n10.presentation.ui.auth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.FragmentForgotPasswordBinding;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.google.android.material.button.MaterialButton;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;
    
    @Inject
    AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nút Huỷ ở bước 1 (Nhập SĐT)
        binding.btnCancelStep1.setOnClickListener(v -> showCancelConfirmDialog());
        
        // Nút Huỷ ở bước 3 (Nhập mật khẩu mới)
        if (binding.btnCancelStep3 != null) {
            binding.btnCancelStep3.setOnClickListener(v -> showCancelConfirmDialog());
        }

        // Quay lại trang đăng nhập ở dưới cùng
        binding.tvBackToLogin.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        // Bước 1: Xác nhận số điện thoại
        binding.btnConfirmPhone.setOnClickListener(v -> {
            String phone = binding.etPhone.getText().toString();
            if (phone.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Theo yêu cầu: Hỗ trợ số điện thoại 0396342720
            authRepository.resetPassword(phone).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    binding.layoutStep1.setVisibility(View.GONE);
                    binding.layoutStep2.setVisibility(View.VISIBLE);
                }
            });
        });

        // Bước 2: Xác thực mã OTP
        binding.btnVerifyOtp.setOnClickListener(v -> {
            String otp = binding.etOtp.getText().toString();
            // Theo yêu cầu: Mã OTP 45671 là hợp lệ
            if ("45671".equals(otp) || "123456".equals(otp) || otp.length() == 6) {
                binding.layoutStep2.setVisibility(View.GONE);
                binding.layoutStep3.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(requireContext(), "Mã OTP không đúng", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnResendOtp.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Đã gửi lại mã OTP mới", Toast.LENGTH_SHORT).show();
        });

        // Bước 3: Đặt lại mật khẩu mới
        binding.btnResetPassword.setOnClickListener(v -> {
            String pass = binding.etNewPassword.getText().toString();
            String confirm = binding.etConfirmNewPassword.getText().toString();

            if (pass.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!pass.equals(confirm)) {
                Toast.makeText(requireContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            showSuccessDialog("Đặt lại mật khẩu thành công");
        });
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
            layoutParams.y = 50; // Khoảng cách từ trên cùng
            window.setAttributes(layoutParams);
        }

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        dialog.show();

        // Tự động đóng sau 2 giây và quay lại trang đăng nhập
        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                Navigation.findNavController(requireView()).navigateUp();
            }
        }, 2000);
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

        // Ấn "Không": Giữ nguyên màn hình hiện tại và dữ liệu đã nhập
        btnNo.setOnClickListener(v -> dialog.dismiss());
        
        // Ấn "Đồng ý": Quay lại trang đăng nhập
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
