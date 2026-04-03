package com.demo.ltud_n10.presentation.ui.employee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.FragmentEmployeeContractBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.ContractRepository;

import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EmployeeContractFragment extends Fragment {

    private FragmentEmployeeContractBinding binding;
    private EmployeeContractAdapter adapter;

    @Inject
    ContractRepository contractRepository;

    @Inject
    AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmployeeContractBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
        loadData();
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new EmployeeContractAdapter();
        binding.rvContracts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvContracts.setAdapter(adapter);
    }

    private void loadData() {
        User currentUser = authRepository.getCurrentUser().getValue();
        if (currentUser == null) return;

        contractRepository.getContracts().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                // Filter contracts only for the current user
                // Using name for simple mock matching, in real app use employeeId
                adapter.setItems(resource.data.stream()
                        .filter(c -> c.getEmployeeName().equals(currentUser.getName()))
                        .collect(Collectors.toList()));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
