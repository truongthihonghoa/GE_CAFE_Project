package com.demo.ltud_n10.presentation.ui.auth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.demo.ltud_n10.databinding.FragmentForgotPasswordBinding;
import com.demo.ltud_n10.domain.repository.AuthRepository;

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

        // Click listeners for going back
        binding.btnCancelStep1.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());
        if (binding.btnCancelStep3 != null) {
            binding.btnCancelStep3.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());
        }
        binding.tvBackToLogin.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        // Step 1: Confirm Phone
        binding.btnConfirmPhone.setOnClickListener(v -> {
            String phone = binding.etPhone.getText().toString();
            if (phone.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.length() != 10 || !phone.startsWith("0")) {
                Toast.makeText(requireContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            
            authRepository.resetPassword(phone).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    binding.layoutStep1.setVisibility(View.GONE);
                    binding.layoutStep2.setVisibility(View.VISIBLE);
                }
            });
        });

        // Step 2: Verify OTP
        binding.btnVerifyOtp.setOnClickListener(v -> {
            String otp = binding.etOtp.getText().toString();
            if ("123456".equals(otp) || otp.length() == 6) {
                binding.layoutStep2.setVisibility(View.GONE);
                binding.layoutStep3.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(requireContext(), "OTP không đúng", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnResendOtp.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Đã gửi lại mã OTP mới", Toast.LENGTH_SHORT).show();
        });

        // Step 3: Reset Password
        binding.btnResetPassword.setOnClickListener(v -> {
            String pass = binding.etNewPassword.getText().toString();
            String confirm = binding.etConfirmNewPassword.getText().toString();

            if (pass.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
                return;
            }
            // Regex check for: at least 8 chars, one uppercase, one lowercase, one digit
            if (pass.length() < 8 || !pass.matches(".*[A-Z].*") || !pass.matches(".*[a-z].*") || !pass.matches(".*\\d.*")) {
                Toast.makeText(requireContext(), "Mật khẩu không hợp lệ (≥ 8 ký tự, có chữ hoa, thường và số)", Toast.LENGTH_LONG).show();
                return;
            }
            if (!pass.equals(confirm)) {
                Toast.makeText(requireContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(requireContext(), "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
