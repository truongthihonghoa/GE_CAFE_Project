package com.demo.ltud_n10.presentation.ui.employee;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.local.SharedPrefsManager;
import com.demo.ltud_n10.databinding.FragmentProfileBinding;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    SharedPrefsManager prefsManager;

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
        loadEmployeeData();

        binding.btnCancel.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void loadEmployeeData() {
        String maNv = prefsManager.getMaNv();
        if (maNv == null) {
            Log.e("ProfileFragment", "MaNv is null in SharedPrefs");
            return;
        }

        employeeRepository.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                for (Employee e : resource.data) {
                    if (maNv.equals(e.getId())) {
                        updateUI(e);
                        break;
                    }
                }
            } else if (resource != null && resource.status == Resource.Status.ERROR) {
                Log.e("ProfileFragment", "Error loading employee: " + resource.message);
            }
        });
    }

    private void updateUI(Employee employee) {
        binding.etFullName.setText(employee.getName());
        binding.tvGender.setText(employee.getGender());
        binding.tvDob.setText(employee.getDob());
        binding.etCccd.setText(employee.getCccd());
        binding.etPhone.setText(employee.getPhone());
        binding.etAddress.setText(employee.getAddress());
        binding.etPosition.setText(employee.getPosition());
        binding.etBankAccount.setText(employee.getBankAccount());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
