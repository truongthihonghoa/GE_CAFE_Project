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
import com.demo.ltud_n10.databinding.FragmentEmployeeScheduleBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EmployeeScheduleFragment extends Fragment {

    private FragmentEmployeeScheduleBinding binding;
    private EmployeeScheduleGroupAdapter adapter;
    private Calendar currentWeekStart;

    @Inject
    WorkShiftRepository workShiftRepository;

    @Inject
    AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmployeeScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initCalendar();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadData();
    }

    private void initCalendar() {
        currentWeekStart = Calendar.getInstance();
        // Set to Monday of current week
        while (currentWeekStart.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            currentWeekStart.add(Calendar.DAY_OF_YEAR, -1);
        }
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new EmployeeScheduleGroupAdapter();
        binding.rvSchedule.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSchedule.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnPrevWeek.setOnClickListener(v -> {
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, -1);
            loadData();
        });

        binding.btnNextWeek.setOnClickListener(v -> {
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, 1);
            loadData();
        });
    }

    private void loadData() {
        updateWeekLabel();
        User user = authRepository.getCurrentUser().getValue();
        if (user == null) return;

        workShiftRepository.getWorkShifts().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                // Filter only for current user and the ones already approved
                adapter.setWeekData(currentWeekStart, resource.data.stream()
                        .filter(s -> s.getEmployeeName().equals(user.getName()))
                        .collect(Collectors.toList()));
            }
        });
    }

    private void updateWeekLabel() {
        SimpleDateFormat rangeFormat = new SimpleDateFormat("d/M", Locale.getDefault());
        Calendar endOfWeek = (Calendar) currentWeekStart.clone();
        endOfWeek.add(Calendar.DAY_OF_YEAR, 6);

        String range = rangeFormat.format(currentWeekStart.getTime()) + " - " + rangeFormat.format(endOfWeek.getTime());
        binding.tvWeekRange.setText(range);

        SimpleDateFormat monthYearFormat = new SimpleDateFormat("'Tháng' M, yyyy", Locale.getDefault());
        binding.tvMonthYearLabel.setText(monthYearFormat.format(currentWeekStart.getTime()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
