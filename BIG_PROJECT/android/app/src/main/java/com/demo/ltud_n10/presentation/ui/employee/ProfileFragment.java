package com.demo.ltud_n10.presentation.ui.employee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.FragmentProfileBinding;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Inject
    AuthRepository authRepository;

    @Inject
    EmployeeRepository employeeRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        loadData();

        binding.ivChangePassword.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_changePasswordFragment);
        });
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void loadData() {
        User currentUser = authRepository.getCurrentUser().getValue();
        if (currentUser == null) {
            // Hiển thị dữ liệu mẫu Lê Văn C theo hình ảnh
            binding.tvName.setText("Lê Văn C");
            binding.tvUsername.setText("levanc@coffee.com");
            binding.tvRole.setText("EMPLOYEE");
            return;
        }

        binding.tvName.setText(currentUser.getName());
        binding.tvUsername.setText(currentUser.getUsername());
        binding.tvRole.setText(currentUser.getRole());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
