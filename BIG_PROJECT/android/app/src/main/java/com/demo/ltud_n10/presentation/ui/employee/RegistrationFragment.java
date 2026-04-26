package com.demo.ltud_n10.presentation.ui.employee;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.DialogConfirmDeleteBinding;
import com.demo.ltud_n10.databinding.DialogEditRegistrationBinding;
import com.demo.ltud_n10.databinding.FragmentRegistrationBinding;
import com.demo.ltud_n10.databinding.LayoutSuccessNotificationBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;
    private RegistrationHistoryAdapter adapter;
    private String currentMode = "SHIFT";

    private long selectedShiftDate;
    private long selectedStartDate;
    private long selectedEndDate;

    @Inject
    WorkShiftRepository workShiftRepository;

    @Inject
    AuthRepository authRepository;

    @Override
    public void onAttach(@NonNull Context context) {
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDates();
        setupTabs();
        setupShiftSelection();
        setupSubmitButtons();
        setupRecyclerView();
        setupToolbar();
        loadHistory();
    }

    private void initDates() {
        Calendar cal = Calendar.getInstance();
        selectedShiftDate = cal.getTimeInMillis();
        selectedStartDate = cal.getTimeInMillis();
        selectedEndDate = cal.getTimeInMillis();

        binding.calendarShift.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            selectedShiftDate = c.getTimeInMillis();
        });

        binding.calendarStart.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            selectedStartDate = c.getTimeInMillis();
        });

        binding.calendarEnd.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            selectedEndDate = c.getTimeInMillis();
        });
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void setupTabs() {
        binding.tabShift.setOnClickListener(v -> {
            currentMode = "SHIFT";
            updateTabUI();
        });
        binding.tabLeave.setOnClickListener(v -> {
            currentMode = "LEAVE";
            updateTabUI();
        });
    }

    private void updateTabUI() {
        if ("SHIFT".equals(currentMode)) {
            binding.tabShift.setCardBackgroundColor(Color.WHITE);
            binding.tabShift.setCardElevation(4f);
            binding.tabLeave.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tabLeave.setCardElevation(0f);
            binding.layoutShiftContent.setVisibility(View.VISIBLE);
            binding.layoutLeaveContent.setVisibility(View.GONE);
        } else {
            binding.tabLeave.setCardBackgroundColor(Color.WHITE);
            binding.tabLeave.setCardElevation(4f);
            binding.tabShift.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tabShift.setCardElevation(0f);
            binding.layoutLeaveContent.setVisibility(View.VISIBLE);
            binding.layoutShiftContent.setVisibility(View.GONE);
        }
        loadHistory();
    }

    private void setupShiftSelection() {
        View.OnClickListener listener = v -> {
            binding.rbMorning.setChecked(v == binding.cvShiftMorning);
            binding.rbAfternoon.setChecked(v == binding.cvShiftAfternoon);
            binding.rbEvening.setChecked(v == binding.cvShiftEvening);
            updateShiftUI();
        };

        binding.cvShiftMorning.setOnClickListener(listener);
        binding.cvShiftAfternoon.setOnClickListener(listener);
        binding.cvShiftEvening.setOnClickListener(listener);

        binding.rbMorning.setChecked(true);
        updateShiftUI();
    }

    private void updateShiftUI() {
        int selectedColor = Color.parseColor("#E8F5E9");
        int selectedStroke = Color.parseColor("#2E7D32");
        int defaultColor = Color.WHITE;
        int defaultStroke = Color.parseColor("#E2E8F0");

        binding.cvShiftMorning.setCardBackgroundColor(binding.rbMorning.isChecked() ? selectedColor : defaultColor);
        binding.cvShiftMorning.setStrokeColor(binding.rbMorning.isChecked() ? selectedStroke : defaultStroke);
        binding.cvShiftMorning.setStrokeWidth(binding.rbMorning.isChecked() ? 4 : 2);

        binding.cvShiftAfternoon.setCardBackgroundColor(binding.rbAfternoon.isChecked() ? selectedColor : defaultColor);
        binding.cvShiftAfternoon.setStrokeColor(binding.rbAfternoon.isChecked() ? selectedStroke : defaultStroke);
        binding.cvShiftAfternoon.setStrokeWidth(binding.rbAfternoon.isChecked() ? 4 : 2);

        binding.cvShiftEvening.setCardBackgroundColor(binding.rbEvening.isChecked() ? selectedColor : defaultColor);
        binding.cvShiftEvening.setStrokeColor(binding.rbEvening.isChecked() ? selectedStroke : defaultStroke);
        binding.cvShiftEvening.setStrokeWidth(binding.rbEvening.isChecked() ? 4 : 2);
    }

    private void setupRecyclerView() {
        adapter = new RegistrationHistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHistory.setAdapter(adapter);

        adapter.setOnActionClickListener(new RegistrationHistoryAdapter.OnActionClickListener() {
            @Override
            public void onEdit(WorkShift shift) {
                showEditDialog(shift);
            }

            @Override
            public void onDelete(WorkShift shift) {
                showDeleteConfirmDialog(shift);
            }
        });
    }

    private void showEditDialog(WorkShift shift) {
        DialogEditRegistrationBinding dialogBinding = DialogEditRegistrationBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        final long[] editDates = new long[3];
        editDates[0] = Calendar.getInstance().getTimeInMillis();
        editDates[1] = Calendar.getInstance().getTimeInMillis();
        editDates[2] = Calendar.getInstance().getTimeInMillis();

        dialogBinding.calendarEditShift.setOnDateChangeListener((v, y, m, d) -> {
            Calendar c = Calendar.getInstance(); c.set(y, m, d);
            editDates[0] = c.getTimeInMillis();
        });
        dialogBinding.calendarEditStart.setOnDateChangeListener((v, y, m, d) -> {
            Calendar c = Calendar.getInstance(); c.set(y, m, d);
            editDates[1] = c.getTimeInMillis();
        });
        dialogBinding.calendarEditEnd.setOnDateChangeListener((v, y, m, d) -> {
            Calendar c = Calendar.getInstance(); c.set(y, m, d);
            editDates[2] = c.getTimeInMillis();
        });

        if ("Đăng ký ca làm".equals(shift.getType())) {
            dialogBinding.layoutEditShift.setVisibility(View.VISIBLE);
            dialogBinding.layoutEditLeave.setVisibility(View.GONE);
            dialogBinding.tvDialogTitle.setText("Chỉnh sửa đăng ký ca");
            try {
                Date date = sdf.parse(shift.getDate());
                if (date != null) {
                    dialogBinding.calendarEditShift.setDate(date.getTime());
                    editDates[0] = date.getTime();
                }
            } catch (Exception ignored) {}
            if (shift.getPosition().contains("08:00")) dialogBinding.rbEditMorning.setChecked(true);
            else if (shift.getPosition().contains("13:00")) dialogBinding.rbEditAfternoon.setChecked(true);
            else dialogBinding.rbEditEvening.setChecked(true);
        } else {
            dialogBinding.layoutEditShift.setVisibility(View.GONE);
            dialogBinding.layoutEditLeave.setVisibility(View.VISIBLE);
            dialogBinding.tvDialogTitle.setText("Chỉnh sửa nghỉ phép");
            dialogBinding.etEditLeaveReason.setText(shift.getPosition());
            String[] dates = shift.getDate().split(" - ");
            if (dates.length == 2) {
                try {
                    Date start = sdf.parse(dates[0]);
                    Date end = sdf.parse(dates[1]);
                    if (start != null) {
                        dialogBinding.calendarEditStart.setDate(start.getTime());
                        editDates[1] = start.getTime();
                    }
                    if (end != null) {
                        dialogBinding.calendarEditEnd.setDate(end.getTime());
                        editDates[2] = end.getTime();
                    }
                } catch (Exception ignored) {}
            }
        }

        dialogBinding.btnCancelEdit.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnSaveEdit.setOnClickListener(v -> {
            if ("Đăng ký ca làm".equals(shift.getType())) {
                shift.setDate(sdf.format(new Date(editDates[0])));
                if (dialogBinding.rbEditMorning.isChecked()) {
                    shift.setPosition("08:00 - 12:00"); shift.setStartTime("08:00"); shift.setEndTime("12:00");
                } else if (dialogBinding.rbEditAfternoon.isChecked()) {
                    shift.setPosition("13:00 - 17:00"); shift.setStartTime("13:00"); shift.setEndTime("17:00");
                } else {
                    shift.setPosition("18:00 - 22:00"); shift.setStartTime("18:00"); shift.setEndTime("22:00");
                }
            } else {
                shift.setDate(sdf.format(new Date(editDates[1])) + " - " + sdf.format(new Date(editDates[2])));
                shift.setPosition(dialogBinding.etEditLeaveReason.getText().toString());
            }
            workShiftRepository.updateWorkShift(shift).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessNotification("Cập nhật đăng ký thành công");
                    loadHistory();
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }

    private void showDeleteConfirmDialog(WorkShift shift) {
        DialogConfirmDeleteBinding dialogBinding = DialogConfirmDeleteBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnDelete.setOnClickListener(v -> {
            workShiftRepository.deleteWorkShift(shift.getId()).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessNotification("Xóa đăng ký thành công");
                    loadHistory();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void setupSubmitButtons() {
        binding.btnSubmitShift.setOnClickListener(v -> handleShiftSubmit());
        binding.btnSubmitLeave.setOnClickListener(v -> handleLeaveSubmit());
    }

    private void handleShiftSubmit() {
        User user = authRepository.getCurrentUser().getValue();
        if (user == null) return;

        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTimeInMillis(selectedShiftDate);
        selectedCal.set(Calendar.HOUR_OF_DAY, 0);
        selectedCal.set(Calendar.MINUTE, 0);
        selectedCal.set(Calendar.SECOND, 0);
        selectedCal.set(Calendar.MILLISECOND, 0);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if (selectedCal.before(today)) {
            Toast.makeText(getContext(), "Không thể đăng ký ca làm cho ngày trong quá khứ", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnSubmitShift.setEnabled(false);
        binding.btnSubmitShift.setText("Đang gửi...");

        WorkShift shift = new WorkShift();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        shift.setDate(sdf.format(new Date(selectedShiftDate)));

        String pos = binding.rbAfternoon.isChecked() ? "Chiều" : binding.rbEvening.isChecked() ? "Tối" : "Sáng";
        shift.setPosition(pos.equals("Sáng") ? "08:00 - 12:00" : pos.equals("Chiều") ? "13:00 - 17:00" : "18:00 - 22:00");
        shift.setStartTime(pos.equals("Sáng") ? "08:00" : pos.equals("Chiều") ? "13:00" : "18:00");
        shift.setEndTime(pos.equals("Sáng") ? "12:00" : pos.equals("Chiều") ? "17:00" : "22:00");

        shift.setEmployeeId(user.getId());
        shift.setEmployeeName(user.getName());
        shift.setStatus("Chờ duyệt");
        shift.setType("Đăng ký ca làm");

        // Lấy thời gian gửi hiện tại (Giờ:Phút:Giây Ngày/Tháng/Năm)
        String currentTime = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date());
        shift.setSentTime(currentTime);

        workShiftRepository.addWorkShift(shift).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessNotification("Gửi đăng ký lịch thành công");
                    binding.btnSubmitShift.setEnabled(true);
                    binding.btnSubmitShift.setText("Gửi đăng ký");
                    loadHistory();
                } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                    binding.btnSubmitShift.setEnabled(true);
                    binding.btnSubmitShift.setText("Gửi đăng ký");
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleLeaveSubmit() {
        User user = authRepository.getCurrentUser().getValue();
        if (user == null) return;

        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(selectedStartDate);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if (startCal.before(today)) {
            Toast.makeText(getContext(), "Ngày bắt đầu nghỉ phép không thể ở quá khứ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedEndDate < selectedStartDate) {
            Toast.makeText(getContext(), "Ngày kết thúc không thể trước ngày bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }

        String reason = binding.etLeaveReason.getText().toString();
        if (reason.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập lý do nghỉ phép", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnSubmitLeave.setEnabled(false);
        binding.btnSubmitLeave.setText("Đang gửi...");

        WorkShift leave = new WorkShift();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        leave.setDate(sdf.format(new Date(selectedStartDate)) + " - " + sdf.format(new Date(selectedEndDate)));
        leave.setPosition(reason);
        leave.setEmployeeId(user.getId());
        leave.setEmployeeName(user.getName());
        leave.setStatus("Chờ duyệt");
        leave.setType("Nghỉ phép");

        // Lấy thời gian gửi hiện tại
        String currentTime = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date());
        leave.setSentTime(currentTime);

        workShiftRepository.addWorkShift(leave).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessNotification("Gửi đơn xin nghỉ phép thành công");
                    binding.etLeaveReason.setText("");
                    binding.btnSubmitLeave.setEnabled(true);
                    binding.btnSubmitLeave.setText("Gửi đơn xin nghỉ");
                    loadHistory();
                } else if (resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                    binding.btnSubmitLeave.setEnabled(true);
                    binding.btnSubmitLeave.setText("Gửi đơn xin nghỉ");
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showSuccessNotification(String message) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            try {
                LayoutSuccessNotificationBinding navBinding = LayoutSuccessNotificationBinding.inflate(getLayoutInflater());
                navBinding.tvMessage.setText(message);

                FrameLayout rootLayout = (FrameLayout) getActivity().findViewById(android.R.id.content);
                if (rootLayout == null) return;

                float density = getResources().getDisplayMetrics().density;
                int marginEnd = (int) (24 * density);
                int marginBottom = (int) (100 * density);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.gravity = Gravity.BOTTOM | Gravity.END;
                params.setMargins(0, 0, marginEnd, marginBottom);

                View notifyView = navBinding.getRoot();
                notifyView.setLayoutParams(params);

                notifyView.setElevation(100f);
                notifyView.setTranslationZ(100f);
                rootLayout.addView(notifyView);
                notifyView.bringToFront();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (notifyView.getParent() != null) {
                        rootLayout.removeView(notifyView);
                    }
                }, 4000);
            } catch (Exception e) {
                Log.e("RegistrationFragment", "Error showing notification", e);
            }
        });
    }

    private void loadHistory() {
        User user = authRepository.getCurrentUser().getValue();
        if (user == null) return;
        workShiftRepository.getWorkShifts().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                String targetType = "SHIFT".equals(currentMode) ? "Đăng ký ca làm" : "Nghỉ phép";
                List<WorkShift> filtered = resource.data.stream()
                        .filter(s -> s.getEmployeeId().equals(user.getId()) && s.getType().equals(targetType))
                        .collect(Collectors.toList());
                adapter.setItems(filtered);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}