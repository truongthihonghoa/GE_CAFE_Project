package com.demo.ltud_n10.presentation.ui.employee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.FragmentEmployeeDashboardBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EmployeeDashboardFragment extends Fragment {

    private FragmentEmployeeDashboardBinding binding;

    @Inject
    AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmployeeDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupToolbar();
        updateUI();
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void updateUI() {
        User user = authRepository.getCurrentUser().getValue();
        if (user != null) {
            binding.tvGreeting.setText("Xin chào, " + user.getName());
            binding.tvEmployeeName.setText(user.getName());
            binding.tvContractUser.setText(user.getName());
        }
        
        // Dữ liệu mẫu khác đã được thiết lập trong XML hoặc có thể cập nhật thêm ở đây
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
