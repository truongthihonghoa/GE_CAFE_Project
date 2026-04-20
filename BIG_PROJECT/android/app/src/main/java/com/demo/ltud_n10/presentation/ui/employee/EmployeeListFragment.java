package com.demo.ltud_n10.presentation.ui.employee;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.FragmentEmployeeListBinding;
import com.demo.ltud_n10.domain.model.Employee;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EmployeeListFragment extends Fragment {

    private FragmentEmployeeListBinding binding;
    private EmployeeViewModel viewModel;
    private EmployeeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmployeeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private com.demo.ltud_n10.data.local.SharedPrefsManager prefsManager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EmployeeViewModel.class);
        prefsManager = new com.demo.ltud_n10.data.local.SharedPrefsManager(requireContext());

        setupUI();
        setupRecyclerView();
        observeViewModel();
        
        // Phân quyền giao diện
        String role = prefsManager.getUserRole();
        boolean isAdmin = !"STAFF".equals(role);
        
        if (!isAdmin) {
            binding.btnAddEmployee.setVisibility(View.GONE);
        }
        adapter.setIsAdmin(isAdmin);
    }

    private void setupUI() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        binding.btnAddEmployee.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("title", "Thêm nhân viên");
            Navigation.findNavController(v).navigate(R.id.action_employeeListFragment_to_employeeDetailFragment, args);
        });

        // Setup Spinners
        String[] positions = {"Tất cả chức vụ", "Quản lý", "Pha chế", "Phục vụ"};
        ArrayAdapter<String> posAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, positions);
        posAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPosition.setAdapter(posAdapter);

        String[] statuses = {"Tất cả trạng thái", "Đang làm", "Ngừng hoạt động"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerStatus.setAdapter(statusAdapter);
    }

    private void setupRecyclerView() {
        adapter = new EmployeeAdapter(new EmployeeAdapter.OnEmployeeClickListener() {
            @Override
            public void onEditClick(Employee employee) {
                Bundle args = new Bundle();
                args.putSerializable("employee", employee);
                args.putString("title", "Chỉnh sửa nhân viên");
                Navigation.findNavController(requireView()).navigate(R.id.action_employeeListFragment_to_employeeDetailFragment, args);
            }

            @Override
            public void onDeleteClick(Employee employee) {
                showDeleteConfirmation(employee);
            }

            @Override
            public void onViewClick(Employee employee) {
                Bundle args = new Bundle();
                args.putSerializable("employee", employee);
                args.putString("title", "Chi tiết nhân viên");
                args.putBoolean("isReadOnly", true);
                Navigation.findNavController(requireView()).navigate(R.id.action_employeeListFragment_to_employeeDetailFragment, args);
            }
        });
        binding.rvEmployees.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvEmployees.setAdapter(adapter);
    }

    private java.util.List<Employee> fullList = new java.util.ArrayList<>();

    private void observeViewModel() {
        viewModel.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case SUCCESS:
                    fullList = resource.data;
                    adapter.submitList(fullList);
                    break;
                case ERROR:
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        binding.etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEmployees(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filterEmployees(String query) {
        if (query.isEmpty()) {
            adapter.submitList(fullList);
            return;
        }
        
        java.util.List<Employee> filtered = new java.util.ArrayList<>();
        for (Employee e : fullList) {
            if (e.getName().toLowerCase().contains(query.toLowerCase()) || 
                e.getId().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(e);
            }
        }
        adapter.submitList(filtered);
    }

    private void showDeleteConfirmation(Employee employee) {
        new AlertDialog.Builder(requireContext())
                .setTitle("XÁC NHẬN XÓA")
                .setMessage("Bạn có chắc chắn muốn xóa thông tin nhân viên này không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    viewModel.deleteEmployee(employee.getId()).observe(getViewLifecycleOwner(), resource -> {
                        if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                            Toast.makeText(requireContext(), "Đã xóa thông tin nhân viên", Toast.LENGTH_SHORT).show();
                            observeViewModel(); // Refresh list
                        }
                    });
                })
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
