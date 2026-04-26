package com.demo.ltud_n10.presentation.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.FragmentLoginBinding;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Đảm bảo ban đầu không có text
        binding.etEmail.setText("");
        binding.etPassword.setText("");

        setupLiveValidation();

        binding.btnLogin.setOnClickListener(v -> {
            boolean hasError = validateAll();
            if (hasError) return;

            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            viewModel.login(email, password).observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                
                switch (resource.status) {
                    case LOADING:
                        binding.btnLogin.setEnabled(false);
                        binding.btnLogin.setText("Đang đăng nhập...");
                        break;
                    case SUCCESS:
                        binding.btnLogin.setEnabled(true);
                        binding.btnLogin.setText("Đăng nhập");
                        if (resource.data != null) {
                            if ("ADMIN".equals(resource.data.getRole())) {
                                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_adminDashboardFragment);
                            } else {
                                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_employeeDashboardFragment);
                            }
                        }
                        break;
                    case ERROR:
                        binding.btnLogin.setEnabled(true);
                        binding.btnLogin.setText("Đăng nhập");
                        
                        // Xử lý thông báo lỗi từ Server nếu logic local chưa bắt hết
                        if (resource.message != null) {
                            String msg = resource.message.toLowerCase();
                            if (msg.contains("mật khẩu") || msg.contains("password")) {
                                binding.tvPasswordError.setText("*Mật khẩu không đúng, vui lòng nhập lại");
                                binding.tvPasswordError.setVisibility(View.VISIBLE);
                            } else {
                                binding.tvEmailError.setText("*Tên đăng nhập không đúng, vui lòng nhập lại");
                                binding.tvEmailError.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                }
            });
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });
    }

    private void setupLiveValidation() {
        // Kiểm tra Email khi rời khỏi ô nhập
        binding.etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateEmail();
            }
        });

        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvEmailError.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Kiểm tra Mật khẩu khi rời khỏi ô nhập
        binding.etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePassword();
            }
        });

        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvPasswordError.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean validateEmail() {
        String email = binding.etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            binding.tvEmailError.setText("*Vui lòng nhập tên đăng nhập");
            binding.tvEmailError.setVisibility(View.VISIBLE);
            return true;
        }
        
        // Danh sách các tên đăng nhập hợp lệ (từ database Django)
        List<String> validUsernames = Arrays.asList(
            "owner@coffee.com", "staff@coffee.com", "staff@gmail.com",
            "ThuyLai", "ThuyNa", "bao.tq", "quan.pm", "lan.dt", 
            "anh.bd", "huy.nq", "ngoc.pt", "hoa.tk", "dat.vt", "thi.nd", "thu.tt"
        );
        
        if (!validUsernames.contains(email)) {
            binding.tvEmailError.setText("*Tên đăng nhập không đúng, vui lòng nhập lại");
            binding.tvEmailError.setVisibility(View.VISIBLE);
            return true;
        }

        binding.tvEmailError.setVisibility(View.GONE);
        return false;
    }

    private boolean validatePassword() {
        String password = binding.etPassword.getText().toString().trim();
        if (password.isEmpty()) {
            binding.tvPasswordError.setText("*Vui lòng nhập mật khẩu");
            binding.tvPasswordError.setVisibility(View.VISIBLE);
            return true;
        }
        binding.tvPasswordError.setVisibility(View.GONE);
        return false;
    }

    private boolean validateAll() {
        boolean emailError = validateEmail();
        boolean passwordError = validatePassword();
        return emailError || passwordError;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
