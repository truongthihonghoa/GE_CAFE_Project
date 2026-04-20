package com.demo.ltud_n10.presentation.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.demo.ltud_n10.R;
import com.google.android.material.textfield.TextInputEditText;
import com.demo.ltud_n10.databinding.FragmentAccountDetailBinding;
import com.demo.ltud_n10.domain.model.Account;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.repository.AccountRepository;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountDetailFragment extends Fragment {

    private FragmentAccountDetailBinding binding;
    private Account account;
    private String title;
    private boolean isReadOnly = false;
    private String selectedEmployeeId;
    private List<Employee> employeeList = new ArrayList<>();

    @Inject
    AccountRepository accountRepository;

    @Inject
    EmployeeRepository employeeRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            account = (Account) getArguments().getSerializable("account");
            title = getArguments().getString("title");
            isReadOnly = getArguments().getBoolean("isReadOnly", false);
        }

        binding.tvTitle.setText(title);
        
        loadEmployees();

        if (account != null) {
            if (isReadOnly) {
                setupReadOnlyMode();
            } else {
                setupEditMode();
            }
        } else {
            setupAddMode();
        }

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
        binding.btnCancel.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
    }

    private void loadEmployees() {
        employeeRepository.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                employeeList = resource.data;
                setupEmployeeDropdown();
            }
        });
    }

    private void setupEmployeeDropdown() {
        List<String> names = employeeList.stream().map(Employee::getName).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, names);
        binding.actvEmployee.setAdapter(adapter);
        binding.actvEmployee.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            for (Employee e : employeeList) {
                if (e.getName().equals(selectedName)) {
                    selectedEmployeeId = e.getId();
                    break;
                }
            }
        });
    }

    private void setupAddMode() {
        binding.layoutStatus.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.btnChangePassword.setVisibility(View.GONE);
        binding.btnSubmit.setOnClickListener(v -> handleCreate());
    }

    private void setupReadOnlyMode() {
        fillData();
        enableFields(false);
        binding.btnSubmit.setText("XÁC NHẬN");
        binding.btnSubmit.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
        binding.btnCancel.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.btnChangePassword.setVisibility(View.GONE);
        binding.tilPassword.setVisibility(View.GONE);
    }

    private void setupEditMode() {
        fillData();
        binding.tilEmployee.setEnabled(false);
        binding.etUsername.setEnabled(false);
        binding.btnSubmit.setOnClickListener(v -> handleUpdate());
        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        binding.btnChangePassword.setVisibility(View.VISIBLE);
        binding.btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        binding.tilPassword.setVisibility(View.GONE);
    }

    private void fillData() {
        binding.etUsername.setText(account.getUsername());
        binding.actvEmployee.setText(account.getEmployeeName(), false);
        binding.switchIsStaff.setChecked("Quản lý".equals(account.getRole()) || "Chủ".equals(account.getRole()));
        binding.switchIsActive.setChecked("Đang hoạt động".equals(account.getStatus()));
    }

    private void enableFields(boolean enabled) {
        binding.tilEmployee.setEnabled(enabled);
        binding.etUsername.setEnabled(enabled);
        binding.etPassword.setEnabled(enabled);
        binding.switchIsStaff.setEnabled(enabled);
        binding.switchIsActive.setEnabled(enabled);
    }

    private void handleCreate() {
        String username = binding.etUsername.getText().toString();
        String password = binding.etPassword.getText().toString();
        boolean isStaff = binding.switchIsStaff.isChecked();

        if (username.isEmpty() || password.isEmpty() || selectedEmployeeId == null) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Account newAcc = new Account();
        newAcc.setUsername(username);
        newAcc.setEmployeeId(selectedEmployeeId);

        accountRepository.createAccount(newAcc, password, isStaff).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                if (resource.data != null) {
                    Toast.makeText(requireContext(), "Cấp tài khoản thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else if (resource.message != null) {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleUpdate() {
        boolean isStaff = binding.switchIsStaff.isChecked();
        boolean isActive = binding.switchIsActive.isChecked();

        accountRepository.updateAccount(account, null, isStaff, isActive).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                if (resource.data != null) {
                    Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                }
            }
        });
    }

    private void showChangePasswordDialog() {
        View view = LayoutInflater.from(requireContext()).inflate(com.demo.ltud_n10.R.layout.dialog_change_password, null);
        TextInputEditText etNewPass = view.findViewById(com.demo.ltud_n10.R.id.etNewPassword);

        new AlertDialog.Builder(requireContext())
                .setTitle("Đổi mật khẩu")
                .setView(view)
                .setPositiveButton("Cập nhật", (d, w) -> {
                    String newPass = etNewPass.getText().toString();
                    if (!newPass.isEmpty()) {
                        accountRepository.changePassword(account.getId(), newPass).observe(getViewLifecycleOwner(), resource -> {
                            if (resource != null && resource.data != null && resource.data) {
                                Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản này?")
                .setPositiveButton("Xóa", (d, w) -> {
                    accountRepository.deleteAccount(account.getId()).observe(getViewLifecycleOwner(), resource -> {
                        if (resource != null && resource.data != null && resource.data) {
                            Toast.makeText(requireContext(), "Đã xóa tài khoản", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigateUp();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
