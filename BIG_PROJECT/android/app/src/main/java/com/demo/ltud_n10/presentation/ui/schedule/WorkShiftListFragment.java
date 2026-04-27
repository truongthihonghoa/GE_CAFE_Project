package com.demo.ltud_n10.presentation.ui.schedule;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.databinding.DialogConfirmDeleteBinding;
import com.demo.ltud_n10.databinding.FragmentScheduleListBinding;
import com.demo.ltud_n10.databinding.LayoutSuccessNotificationBinding;
import com.demo.ltud_n10.domain.model.Employee;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.EmployeeRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkShiftListFragment extends Fragment {

    private FragmentScheduleListBinding binding;
    private WorkShiftViewModel viewModel;
    private DayScheduleAdapter adapter;
    private Calendar currentWeekStart = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM", new Locale("vi", "VN"));
    
    private List<WorkShift> allShifts = new ArrayList<>();
    private List<Employee> employeeList = new ArrayList<>();
    private String selectedEmployeeId = null;

    @Inject
    EmployeeRepository employeeRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScheduleListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WorkShiftViewModel.class);

        setupWeekStart();
        setupUI();
        setupRecyclerView();
        loadEmployees();
        observeViewModel();
    }

    private void setupWeekStart() {
        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        updateWeekLabel();
    }

    private void updateWeekLabel() {
        Calendar end = (Calendar) currentWeekStart.clone();
        end.add(Calendar.DAY_OF_YEAR, 6);
        String label = dateFormat.format(currentWeekStart.getTime()) + " - " + dateFormat.format(end.getTime()) + ", " + end.get(Calendar.YEAR);
        binding.tvWeekRange.setText(label);
    }

    private void loadEmployees() {
        employeeRepository.getEmployees().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                employeeList = resource.data;
                List<String> names = new ArrayList<>();
                names.add("Tất cả nhân viên");
                for (Employee e : employeeList) names.add(e.getName());
                ArrayAdapter<String> empAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
                empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerEmployee.setAdapter(empAdapter);
            }
        });
    }

    private void setupUI() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        binding.btnCreateShift.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("title", "Tạo lịch làm việc");
            Navigation.findNavController(v).navigate(R.id.action_scheduleListFragment_to_scheduleDetailFragment, args);
        });

        binding.btnPrevWeek.setOnClickListener(v -> {
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, -1);
            updateWeekLabel();
            loadData();
        });

        binding.btnNextWeek.setOnClickListener(v -> {
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, 1);
            updateWeekLabel();
            loadData();
        });

        binding.tvWeekRange.setOnClickListener(v -> showDatePicker());

        binding.spinnerEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEmployeeId = (position == 0) ? null : employeeList.get(position - 1).getId();
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.btnSend.setOnClickListener(v -> {
            List<String> selectedIds = viewModel.getSelectedShiftIds().getValue();
            if (selectedIds == null || selectedIds.isEmpty()) return;

            for (String id : selectedIds) {
                WorkShift shiftToUpdate = null;
                for (WorkShift s : allShifts) { if (s.getId().equals(id)) { shiftToUpdate = s; break; } }
                if (shiftToUpdate != null) {
                    shiftToUpdate.setStatus("Đã gửi");
                    viewModel.updateWorkShift(shiftToUpdate).observe(getViewLifecycleOwner(), res -> {});
                }
            }
            
            showSuccessNotification("Gửi thông báo thành công");
            viewModel.clearSelections();
            new Handler(Looper.getMainLooper()).postDelayed(this::loadData, 500);
        });

        binding.btnCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("XÁC NHẬN HỦY")
                    .setMessage("Bạn có thông tin chưa lưu, xác nhận hủy?")
                    .setPositiveButton("Đồng ý", (d, w) -> viewModel.clearSelections())
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    private void showSuccessNotification(String message) {
        if (getView() == null) return;
        LayoutSuccessNotificationBinding navBinding = LayoutSuccessNotificationBinding.inflate(getLayoutInflater());
        navBinding.tvMessage.setText(message);
        FrameLayout rootLayout = (FrameLayout) requireActivity().findViewById(android.R.id.content);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, 150, 0, 0); 
        View notifyView = navBinding.getRoot();
        notifyView.setLayoutParams(params);
        rootLayout.addView(notifyView);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (rootLayout.indexOfChild(notifyView) != -1) rootLayout.removeView(notifyView);
        }, 3000);
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            currentWeekStart.set(year, month, dayOfMonth);
            currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            updateWeekLabel();
            loadData();
        }, currentWeekStart.get(Calendar.YEAR), currentWeekStart.get(Calendar.MONTH), currentWeekStart.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void setupRecyclerView() {
        adapter = new DayScheduleAdapter(new WorkShiftAdapter.OnShiftClickListener() {
            @Override public void onShiftClick(WorkShift shift) {
                Bundle args = new Bundle();
                args.putSerializable("shift", shift);
                args.putString("title", "Xem chi tiết ca làm việc");
                Navigation.findNavController(requireView()).navigate(R.id.action_scheduleListFragment_to_scheduleDetailFragment, args);
            }
            @Override public void onMoreClick(WorkShift shift, View view) { showOptionsDialog(shift); }
            @Override public void onToggleSelect(WorkShift shift) { viewModel.toggleShiftSelection(shift.getId()); }
        });
        binding.rvSchedule.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSchedule.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getSelectedShiftIds().observe(getViewLifecycleOwner(), ids -> {
            adapter.setWorkShiftAdapterSelectedIds(ids);
            if (ids != null && !ids.isEmpty()) {
                binding.btnSend.setEnabled(true);
                binding.btnSend.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0A4D1E")));
            } else {
                binding.btnSend.setEnabled(false);
                binding.btnSend.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#8DB099")));
            }
        });
        loadData();
    }

    private void loadData() {
        viewModel.getWorkShifts().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS) {
                allShifts = resource.data;
                applyFilters();
            }
        });
    }

    private void applyFilters() {
        List<WorkShift> filteredShifts = (selectedEmployeeId == null) ? allShifts : 
            allShifts.stream().filter(s -> s.getEmployeeId().equals(selectedEmployeeId)).collect(Collectors.toList());
        adapter.setDaySchedules(groupShiftsByDay(filteredShifts));
    }

    private List<DaySchedule> groupShiftsByDay(List<WorkShift> shifts) {
        List<DaySchedule> daySchedules = new ArrayList<>();
        String[] daysVi = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        Calendar temp = (Calendar) currentWeekStart.clone();
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            String dateStr = apiFormat.format(temp.getTime());
            List<WorkShift> dayShifts = shifts.stream().filter(s -> dateStr.equals(s.getDate())).collect(Collectors.toList());
            daySchedules.add(new DaySchedule(daysVi[i], String.valueOf(temp.get(Calendar.DAY_OF_MONTH)), dayShifts));
            temp.add(Calendar.DAY_OF_YEAR, 1);
        }
        return daySchedules;
    }

    private void showOptionsDialog(WorkShift shift) {
        // KIỂM TRA TRẠNG THÁI: Nếu đã gửi thì chỉ cho "Xem chi tiết"
        if ("Đã gửi".equals(shift.getStatus())) {
            String[] options = {"Xem chi tiết"};
            new AlertDialog.Builder(requireContext()).setItems(options, (dialog, which) -> {
                if (which == 0) {
                    Bundle args = new Bundle(); args.putSerializable("shift", shift); args.putString("title", "Xem chi tiết ca làm việc");
                    Navigation.findNavController(requireView()).navigate(R.id.action_scheduleListFragment_to_scheduleDetailFragment, args);
                }
            }).show();
        } else {
            // Nếu chưa gửi, cho phép cả Sửa và Xóa
            String[] options = {"Xem chi tiết", "Chỉnh sửa", "Xóa"};
            new AlertDialog.Builder(requireContext()).setItems(options, (dialog, which) -> {
                if (which == 0) {
                    Bundle args = new Bundle(); args.putSerializable("shift", shift); args.putString("title", "Xem chi tiết ca làm việc");
                    Navigation.findNavController(requireView()).navigate(R.id.action_scheduleListFragment_to_scheduleDetailFragment, args);
                } else if (which == 1) {
                    Bundle args = new Bundle(); args.putSerializable("shift", shift); args.putString("title", "Chỉnh sửa lịch làm việc");
                    Navigation.findNavController(requireView()).navigate(R.id.action_scheduleListFragment_to_scheduleDetailFragment, args);
                } else showDeleteDialog(shift);
            }).show();
        }
    }

    private void showDeleteDialog(WorkShift shift) {
        DialogConfirmDeleteBinding dialogBinding = DialogConfirmDeleteBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogBinding.btnDelete.setOnClickListener(v -> {
            viewModel.deleteWorkShift(shift.getId()).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessNotification("Xóa ca làm thành công");
                    loadData();
                    dialog.dismiss();
                }
            });
        });

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
