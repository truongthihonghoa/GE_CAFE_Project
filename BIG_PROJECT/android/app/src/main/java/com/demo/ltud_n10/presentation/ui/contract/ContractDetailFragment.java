package com.demo.ltud_n10.presentation.ui.contract;

import android.app.DatePickerDialog;
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

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.FragmentContractDetailBinding;
import com.demo.ltud_n10.domain.model.Contract;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.presentation.ui.employee.EmployeeViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContractDetailFragment extends Fragment {

    private FragmentContractDetailBinding binding;
    private ContractViewModel viewModel;
    private EmployeeViewModel employeeViewModel;
    private Contract currentContract;
    private String title;
    private boolean isViewOnly = false;

    private List<Employee> allEmployees = new ArrayList<>();
    private final String[] typeOptions = {"FULLTIME", "PARTTIME"};
    private final String[] positionOptions = {"THU_NGAN", "QUAN_LY", "PHUC_VU", "PHA_CHE"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContractDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ContractViewModel.class);
        employeeViewModel = new ViewModelProvider(requireActivity()).get(EmployeeViewModel.class);

        if (getArguments() != null) {
            currentContract = (Contract) getArguments().getSerializable("contract");
            title = getArguments().getString("title");
            isViewOnly = getArguments().getBoolean("isViewOnly", false);
        }

        setupUI();
        loadEmployees();
    }

    private void setupUI() {
        binding.tvTitle.setText(title);
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Setup Type Spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, typeOptions);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerType.setAdapter(typeAdapter);

        // Setup Position Spinner
        ArrayAdapter<String> posAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, positionOptions);
        posAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPosition.setAdapter(posAdapter);

        binding.btnStartDatePicker.setOnClickListener(v -> { if (!isViewOnly) showDatePicker(true); });
        binding.btnEndDatePicker.setOnClickListener(v -> { if (!isViewOnly) showDatePicker(false); });

        binding.btnSave.setOnClickListener(v -> saveContract());
        binding.btnCancel.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        binding.btnConfirm.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        if (isViewOnly) {
            applyViewOnlyMode();
        }
    }

    private void applyViewOnlyMode() {
        binding.spinnerEmployee.setEnabled(false);
        binding.spinnerType.setEnabled(false);
        binding.spinnerPosition.setEnabled(false);
        binding.etSalary.setEnabled(false);
        binding.etHourlySalary.setEnabled(false);
        binding.etWorkHours.setEnabled(false);
        binding.etTerms.setEnabled(false);
        binding.etResponsibilities.setEnabled(false);
        binding.etNotes.setEnabled(false);
        binding.btnStartDatePicker.setEnabled(false);
        binding.btnEndDatePicker.setEnabled(false);

        binding.btnSave.setVisibility(View.GONE);
        binding.btnCancel.setVisibility(View.GONE);
        binding.btnConfirm.setVisibility(View.VISIBLE);
    }

    private void loadEmployees() {
        employeeViewModel.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                allEmployees = resource.data;
                setupEmployeeSpinner();
                if (currentContract != null) {
                    populateData();
                }
            }
        });
    }

    private void setupEmployeeSpinner() {
        List<String> names = new ArrayList<>();
        for (Employee e : allEmployees) names.add(e.getName() + " (" + e.getId() + ")");
        
        ArrayAdapter<String> empAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEmployee.setAdapter(empAdapter);
    }

    private void populateData() {
        binding.tvStartDate.setText(currentContract.getStartDate());
        binding.tvEndDate.setText(currentContract.getEndDate());
        binding.etSalary.setText(String.format("%.0f", currentContract.getSalary()));
        binding.etHourlySalary.setText(String.format("%.0f", currentContract.getHourlySalary()));
        binding.etWorkHours.setText(String.valueOf(currentContract.getWorkHours()));
        binding.etTerms.setText(currentContract.getTerms());
        binding.etResponsibilities.setText(currentContract.getResponsibilities());
        binding.etNotes.setText(currentContract.getNotes());

        // Set Employee Selection
        for (int i = 0; i < allEmployees.size(); i++) {
            if (allEmployees.get(i).getId().equals(currentContract.getEmployeeId())) {
                binding.spinnerEmployee.setSelection(i);
                break;
            }
        }

        // Set Type Selection
        for (int i = 0; i < typeOptions.length; i++) {
            if (typeOptions[i].equals(currentContract.getType())) {
                binding.spinnerType.setSelection(i);
                break;
            }
        }

        // Set Position Selection
        for (int i = 0; i < positionOptions.length; i++) {
            if (positionOptions[i].equals(currentContract.getPosition())) {
                binding.spinnerPosition.setSelection(i);
                break;
            }
        }
    }

    private void showDatePicker(boolean isStart) {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            if (isStart) binding.tvStartDate.setText(date);
            else binding.tvEndDate.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveContract() {
        if (allEmployees.isEmpty()) return;
        
        int empPos = binding.spinnerEmployee.getSelectedItemPosition();
        Employee selectedEmp = allEmployees.get(empPos);
        
        String type = binding.spinnerType.getSelectedItem().toString();
        String pos = binding.spinnerPosition.getSelectedItem().toString();
        String startDate = binding.tvStartDate.getText().toString();
        String endDate = binding.tvEndDate.getText().toString();
        String salaryStr = binding.etSalary.getText().toString();
        String hourlyStr = binding.etHourlySalary.getText().toString();
        String hoursStr = binding.etWorkHours.getText().toString();

        if (startDate.isEmpty() || salaryStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập ngày bắt đầu và lương", Toast.LENGTH_SHORT).show();
            return;
        }

        Contract contract = currentContract != null ? currentContract : new Contract();
        if (currentContract == null) {
            contract.setId("HD" + System.currentTimeMillis() / 10000);
        }

        contract.setEmployeeId(selectedEmp.getId());
        contract.setEmployeeName(selectedEmp.getName());
        contract.setType(type);
        contract.setPosition(pos);
        contract.setStartDate(startDate);
        contract.setEndDate(endDate.isEmpty() ? null : endDate);
        contract.setSalary(Double.parseDouble(salaryStr));
        contract.setHourlySalary(hourlyStr.isEmpty() ? 0 : Double.parseDouble(hourlyStr));
        contract.setWorkHours(hoursStr.isEmpty() ? 0 : Double.parseDouble(hoursStr));
        contract.setTerms(binding.etTerms.getText().toString());
        contract.setResponsibilities(binding.etResponsibilities.getText().toString());
        contract.setNotes(binding.etNotes.getText().toString());
        contract.setStatus("CON_HAN");

        if (currentContract == null) {
            viewModel.addContract(contract).observe(getViewLifecycleOwner(), resource -> handleResult(resource, "Tạo hợp đồng thành công"));
        } else {
            viewModel.updateContract(contract).observe(getViewLifecycleOwner(), resource -> handleResult(resource, "Cập nhật hợp đồng thành công"));
        }
    }

    private void handleResult(Resource<?> resource, String msg) {
        if (resource.status == Resource.Status.SUCCESS) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).popBackStack();
        } else if (resource.status == Resource.Status.ERROR) {
            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
