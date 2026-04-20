package com.demo.ltud_n10.presentation.ui.schedule;

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
import com.demo.ltud_n10.databinding.FragmentScheduleDetailBinding;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.presentation.ui.employee.EmployeeViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkShiftDetailFragment extends Fragment {

    private FragmentScheduleDetailBinding binding;
    private WorkShiftViewModel viewModel;
    private EmployeeViewModel employeeViewModel;
    private WorkShift currentShift;
    private String title;
    private boolean isViewOnly = false;

    private List<Employee> allEmployees = new ArrayList<>();
    private List<WorkShift.EmployeeAssignment> selectedAssignments = new ArrayList<>();
    private String[] shiftOptions = {"07:00 - 11:00", "13:00 - 17:00", "18:00 - 22:00"};
    private String[] positionOptions = {"Pha chế", "Phục vụ", "Giữ xe", "Thu ngân"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScheduleDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WorkShiftViewModel.class);
        employeeViewModel = new ViewModelProvider(requireActivity()).get(EmployeeViewModel.class);

        if (getArguments() != null) {
            currentShift = (WorkShift) getArguments().getSerializable("shift");
            title = getArguments().getString("title");
            isViewOnly = getArguments().getBoolean("isViewOnly", false);
        }

        setupUI();
        loadEmployees();
        
        if (currentShift != null) {
            populateData();
        }

        if (isViewOnly) {
            applyViewOnlyMode();
        }
    }

    private void setupUI() {
        binding.tvTitle.setText(title);
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, shiftOptions);
        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerShift.setAdapter(shiftAdapter);

        binding.spinnerPosition.setVisibility(View.GONE);
        binding.tvSelectedEmployees.setOnClickListener(v -> {
            if (!isViewOnly) showEmployeeSelectionDialog();
        });

        binding.btnDatePicker.setOnClickListener(v -> {
            if (!isViewOnly) showDatePicker();
        });

        binding.btnSave.setOnClickListener(v -> saveShift());
        binding.btnCancel.setOnClickListener(v -> handleCancel());
        binding.btnConfirm.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
    }

    private void applyViewOnlyMode() {
        // Khóa các ô nhập liệu
        binding.spinnerShift.setEnabled(false);
        binding.tvSelectedEmployees.setEnabled(false);
        binding.btnDatePicker.setEnabled(false);
        
        // Ẩn nút Lưu/Hủy, hiện nút Xác nhận
        binding.btnSave.setVisibility(View.GONE);
        binding.btnCancel.setVisibility(View.GONE);
        binding.btnConfirm.setVisibility(View.VISIBLE);
    }

    private void loadEmployees() {
        employeeViewModel.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                allEmployees = resource.data;
            }
        });
    }

    private void showEmployeeSelectionDialog() {
        if (allEmployees.isEmpty()) {
            Toast.makeText(requireContext(), "Đang tải danh sách nhân viên...", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] employeeNames = new String[allEmployees.size()];
        boolean[] checkedItems = new boolean[allEmployees.size()];

        for (int i = 0; i < allEmployees.size(); i++) {
            employeeNames[i] = allEmployees.get(i).getName();
            checkedItems[i] = isEmployeeSelected(allEmployees.get(i).getId());
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn nhân viên")
                .setMultiChoiceItems(employeeNames, checkedItems, (dialog, which, isChecked) -> {
                    Employee emp = allEmployees.get(which);
                    if (isChecked) {
                        if (!isEmployeeSelected(emp.getId())) {
                            showPositionSelectionDialog(emp);
                        }
                    } else {
                        removeEmployeeAssignment(emp.getId());
                        updateSelectedEmployeesText();
                    }
                })
                .setPositiveButton("Xác nhận", (dialog, which) -> updateSelectedEmployeesText())
                .show();
    }

    private boolean isEmployeeSelected(String id) {
        for (WorkShift.EmployeeAssignment a : selectedAssignments) {
            if (a.getEmployeeId().equals(id)) return true;
        }
        return false;
    }

    private void removeEmployeeAssignment(String id) {
        selectedAssignments.removeIf(a -> a.getEmployeeId().equals(id));
    }

    private void showPositionSelectionDialog(Employee emp) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn vị trí cho " + emp.getName())
                .setItems(positionOptions, (dialog, which) -> {
                    selectedAssignments.add(new WorkShift.EmployeeAssignment(emp.getId(), emp.getName(), positionOptions[which]));
                    updateSelectedEmployeesText();
                })
                .setCancelable(false)
                .show();
    }

    private void updateSelectedEmployeesText() {
        if (selectedAssignments.isEmpty()) {
            binding.tvSelectedEmployees.setText("Nhấn để chọn nhân viên");
        } else {
            List<String> displayList = new ArrayList<>();
            for (WorkShift.EmployeeAssignment a : selectedAssignments) {
                displayList.add(a.getEmployeeName() + " (" + a.getPosition() + ")");
            }
            binding.tvSelectedEmployees.setText(String.join(", ", displayList));
        }
    }

    private void populateData() {
        binding.tvDob.setText(currentShift.getDate());
        selectedAssignments = new ArrayList<>(currentShift.getEmployeeAssignments());
        updateSelectedEmployeesText();
        
        String shiftTime = currentShift.getStartTime() + " - " + currentShift.getEndTime();
        for (int i = 0; i < shiftOptions.length; i++) {
            if (shiftOptions[i].equals(shiftTime)) {
                binding.spinnerShift.setSelection(i);
                break;
            }
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            String date = String.format("%02d/%02d/%04d", day, month + 1, year);
            binding.tvDob.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveShift() {
        String date = binding.tvDob.getText().toString();
        String shiftTime = binding.spinnerShift.getSelectedItem().toString();

        if (date.isEmpty() || selectedAssignments.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngày và ít nhất 1 nhân viên", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] times = shiftTime.split(" - ");
        String startTime = times[0];
        String endTime = times[1];

        WorkShift shift = currentShift;
        if (shift == null) {
            shift = new WorkShift();
            shift.setId(null);
        }

        shift.setEmployeeAssignments(selectedAssignments);
        shift.setEmployeeName("Nhiều nhân viên");
        shift.setDate(date);
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);
        shift.setPosition("Nhiều vị trí");
        shift.setSent(false);

        if (currentShift == null) {
            viewModel.addWorkShift(shift).observe(getViewLifecycleOwner(), resource -> handleResult(resource, "Thêm lịch làm việc thành công"));
        } else {
            viewModel.updateWorkShift(shift).observe(getViewLifecycleOwner(), resource -> handleResult(resource, "Cập nhật lịch làm việc thành công"));
        }
    }

    private void handleResult(Resource<?> resource, String successMsg) {
        if (resource.status == Resource.Status.SUCCESS) {
            Toast.makeText(requireContext(), successMsg, Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).popBackStack();
        } else if (resource.status == Resource.Status.ERROR) {
            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCancel() {
        new AlertDialog.Builder(requireContext())
                .setTitle("XÁC NHẬN HỦY")
                .setMessage("Bạn có thông tin chưa lưu, xác nhận hủy?")
                .setPositiveButton("Đồng ý", (d, w) -> Navigation.findNavController(requireView()).popBackStack())
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
