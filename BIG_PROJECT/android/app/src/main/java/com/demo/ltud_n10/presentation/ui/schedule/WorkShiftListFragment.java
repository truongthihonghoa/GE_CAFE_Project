package com.demo.ltud_n10.presentation.ui.schedule;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.demo.ltud_n10.databinding.FragmentScheduleListBinding;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkShiftListFragment extends Fragment {

    private FragmentScheduleListBinding binding;
    private WorkShiftViewModel viewModel;
    private DayScheduleAdapter adapter;
    private Calendar currentWeekStart = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM", new Locale("vi", "VN"));
    private List<WorkShift> allShifts = new ArrayList<>();

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
        if (binding.tvWeekRange != null) {
            binding.tvWeekRange.setText(label);
        }
    }

    private void setupUI() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        binding.btnCreateShift.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("title", "Tạo lịch làm việc mới");
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

        if (binding.tvWeekRange != null) {
            binding.tvWeekRange.setOnClickListener(v -> showDatePicker());
        }

        // Setup Spinner
        String[] employees = {"Tất cả nhân viên", "Lê Văn C", "Phạm Thị D", "Lê Văn D"};
        ArrayAdapter<String> empAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, employees);
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEmployee.setAdapter(empAdapter);

        // Initial button state
        updateSendButtonState(false);

        binding.btnSend.setOnClickListener(v -> {
            viewModel.sendNotifications().observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Gửi thông báo thành công", Toast.LENGTH_SHORT).show();
                    viewModel.clearSelections();
                    loadData();
                } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnCancel.setOnClickListener(v -> {
            viewModel.clearSelections();
            Toast.makeText(requireContext(), "Đã huỷ chọn", Toast.LENGTH_SHORT).show();
        });
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
            @Override
            public void onShiftClick(WorkShift shift) {
                // Navigate to view details directly on row click
                Bundle args = new Bundle();
                args.putSerializable("shift", shift);
                args.putString("title", "Xem chi tiết ca làm việc");
                args.putBoolean("isViewOnly", true);
                Navigation.findNavController(requireView()).navigate(R.id.action_scheduleListFragment_to_scheduleDetailFragment, args);
            }

            @Override
            public void onMoreClick(WorkShift shift, View view) {
                showOptionsDialog(shift);
            }

            @Override
            public void onToggleSelect(String shiftId, boolean isChecked) {
                if (isChecked) {
                    viewModel.addShiftToSelection(shiftId);
                } else {
                    viewModel.removeShiftFromSelection(shiftId);
                }
            }
        });
        binding.rvSchedule.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSchedule.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getWorkShifts().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                allShifts = resource.data;
                adapter.setDaySchedules(groupShiftsByDay(allShifts));
            }
        });

        viewModel.getSelectedShiftIds().observe(getViewLifecycleOwner(), ids -> {
            updateSendButtonState(!ids.isEmpty());
            if (adapter != null) {
                adapter.setSelectedIds(ids);
            }
        });

        loadData();
    }

    private void updateSendButtonState(boolean active) {
        binding.btnSend.setEnabled(active);
        if (active) {
            binding.btnSend.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1B431C")));
            binding.btnSend.setAlpha(1.0f);
        } else {
            binding.btnSend.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B2CBB9")));
            binding.btnSend.setAlpha(0.6f);
        }
    }

    private void loadData() {
        viewModel.getWorkShifts().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                allShifts = resource.data;
                adapter.setDaySchedules(groupShiftsByDay(allShifts));
            }
        });
    }

    private List<DaySchedule> groupShiftsByDay(List<WorkShift> shifts) {
        List<DaySchedule> daySchedules = new ArrayList<>();
        String[] daysVi = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        
        Calendar temp = (Calendar) currentWeekStart.clone();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String dateStr = dayFormat.format(temp.getTime());
            List<WorkShift> dayShifts = new ArrayList<>();
            for (WorkShift s : shifts) {
                if (s.getDate().equals(dateStr)) {
                    dayShifts.add(s);
                }
            }
            daySchedules.add(new DaySchedule(daysVi[i], String.valueOf(temp.get(Calendar.DAY_OF_MONTH)), dayShifts));
            temp.add(Calendar.DAY_OF_YEAR, 1);
        }
        return daySchedules;
    }

    private void showOptionsDialog(WorkShift shift) {
        String[] options = {"Xem chi tiết", "Chỉnh sửa", "Xóa"};
        new AlertDialog.Builder(requireContext())
                .setItems(options, (dialog, which) -> {
                    Bundle args = new Bundle();
                    args.putSerializable("shift", shift);
                    if (which == 0) {
                        args.putString("title", "Xem chi tiết ca làm việc");
                        args.putBoolean("isViewOnly", true);
                        Navigation.findNavController(requireView()).navigate(R.id.action_scheduleListFragment_to_scheduleDetailFragment, args);
                    } else if (which == 1) {
                        args.putString("title", "Chỉnh sửa lịch làm việc");
                        Navigation.findNavController(requireView()).navigate(R.id.action_scheduleListFragment_to_scheduleDetailFragment, args);
                    } else {
                        showDeleteDialog(shift);
                    }
                }).show();
    }

    private void showDeleteDialog(WorkShift shift) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_delete);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        MaterialButton btnNo = dialog.findViewById(R.id.btnDialogCancel);
        MaterialButton btnYes = dialog.findViewById(R.id.btnDialogConfirm);

        btnNo.setOnClickListener(v -> dialog.dismiss());

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            viewModel.deleteWorkShift(shift.getId()).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Xóa lịch làm việc thành công", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
