package com.demo.ltud_n10.presentation.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.FragmentAdminDashboardBinding;
import com.demo.ltud_n10.domain.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;
    
    @Inject
    AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        setupUI();
    }

    private void setupUI() {
        // Mock UI data already set in XML for preview
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
