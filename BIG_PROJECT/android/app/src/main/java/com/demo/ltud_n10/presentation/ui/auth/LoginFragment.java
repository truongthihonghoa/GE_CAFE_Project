package com.demo.ltud_n10.presentation.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.FragmentLoginBinding;

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

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();
            
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
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });

        viewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
