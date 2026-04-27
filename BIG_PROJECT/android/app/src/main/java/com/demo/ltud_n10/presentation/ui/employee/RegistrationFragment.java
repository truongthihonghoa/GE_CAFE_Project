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
import com.demo.ltud_n10.data.local.SharedPrefsManager;
import com.demo.ltud_n10.databinding.DialogConfirmDeleteBinding;
import com.demo.ltud_n10.databinding.DialogEditRegistrationBinding;
import com.demo.ltud_n10.databinding.FragmentRegistrationBinding;
import com.demo.ltud_n10.databinding.LayoutSuccessNotificationBinding;
import com.demo.ltud_n10.domain.model.Request;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.RequestRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
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
    RequestRepository requestRepository;

    @Inject
    AuthRepository authRepository;

    @Inject
    SharedPrefsManager prefsManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Locale locale = new Locale("vi", "VN");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
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

        binding.calendarShift.setOnDateChangeListener((v, y, m, d) -> {
            Calendar c = Calendar.getInstance(); c.set(y, m, d);
            selectedShiftDate = c.getTimeInMillis();
        });
        binding.calendarStart.setOnDateChangeListener((v, y, m, d) -> {
            Calendar c = Calendar.getInstance(); c.set(y, m, d);
            selectedStartDate = c.getTimeInMillis();
        });
        binding.calendarEnd.setOnDateChangeListener((v, y, m, d) -> {
            Calendar c = Calendar.getInstance(); c.set(y, m, d);
            selectedEndDate = c.getTimeInMillis();
        });
    }

    private String generateRequestId(String prefix) {
        return prefix + System.currentTimeMillis() + (new Random().nextInt(900) + 100);
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).openDrawer();
        });
    }

    private void setupTabs() {
        binding.tabShift.setOnClickListener(v -> { currentMode = "SHIFT"; updateTabUI(); });
        binding.tabLeave.setOnClickListener(v -> { currentMode = "LEAVE"; updateTabUI(); });
    }

    private void updateTabUI() {
        if ("SHIFT".equals(currentMode)) {
            binding.tabShift.setCardBackgroundColor(Color.WHITE);
            binding.layoutShiftContent.setVisibility(View.VISIBLE);
            binding.layoutLeaveContent.setVisibility(View.GONE);
        } else {
            binding.tabLeave.setCardBackgroundColor(Color.WHITE);
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
        int activeColor = Color.parseColor("#E8F5E9");
        int activeStroke = Color.parseColor("#2E7D32");
        binding.cvShiftMorning.setCardBackgroundColor(binding.rbMorning.isChecked() ? activeColor : Color.WHITE);
        binding.cvShiftMorning.setStrokeColor(binding.rbMorning.isChecked() ? activeStroke : Color.parseColor("#E2E8F0"));
        binding.cvShiftAfternoon.setCardBackgroundColor(binding.rbAfternoon.isChecked() ? activeColor : Color.WHITE);
        binding.cvShiftAfternoon.setStrokeColor(binding.rbAfternoon.isChecked() ? activeStroke : Color.parseColor("#E2E8F0"));
        binding.cvShiftEvening.setCardBackgroundColor(binding.rbEvening.isChecked() ? activeColor : Color.WHITE);
        binding.cvShiftEvening.setStrokeColor(binding.rbEvening.isChecked() ? activeStroke : Color.parseColor("#E2E8F0"));
    }

    private void setupRecyclerView() {
        adapter = new RegistrationHistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHistory.setAdapter(adapter);
        adapter.setOnActionClickListener(new RegistrationHistoryAdapter.OnActionClickListener() {
            @Override public void onEdit(Request request) { 
                if (isDateInPast(request.getStartDate())) {
                    Toast.makeText(getContext(), "Không thể chỉnh sửa yêu cầu trong quá khứ", Toast.LENGTH_SHORT).show();
                    return;
                }
                showEditDialog(request); 
            }
            @Override public void onDelete(Request request) { showDeleteConfirmDialog(request); }
        });
    }

    private boolean isDateInPast(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return date != null && date.before(cal.getTime());
        } catch (Exception e) {
            return false;
        }
    }

    private void setupSubmitButtons() {
        binding.btnSubmitShift.setOnClickListener(v -> handleShiftSubmit());
        binding.btnSubmitLeave.setOnClickListener(v -> handleLeaveSubmit());
    }

    private void handleShiftSubmit() {
        if (isDateInPast(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selectedShiftDate)))) {
            Toast.makeText(getContext(), "Không thể đăng ký cho ngày trong quá khứ", Toast.LENGTH_SHORT).show();
            return;
        }

        String maNv = prefsManager.getMaNv();
        User user = authRepository.getCurrentUser().getValue();
        if (maNv == null) {
            Toast.makeText(getContext(), "Không tìm thấy mã nhân viên", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnSubmitShift.setEnabled(false);
        Request req = new Request();
        req.setId(generateRequestId("YC")); 
        req.setEmployeeId(maNv); 
        req.setEmployeeName(user != null ? user.getName() : maNv);
        req.setStatus("Chờ duyệt");
        req.setType("Đăng ký ca làm");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        req.setStartDate(sdf.format(new Date(selectedShiftDate)));
        req.setEndDate(req.getStartDate());
        String shiftLabel = binding.rbMorning.isChecked() ? "Sáng" : binding.rbAfternoon.isChecked() ? "Chiều" : "Tối";
        req.setReason(shiftLabel);

        requestRepository.addRequest(req).observe(getViewLifecycleOwner(), resource -> {
            binding.btnSubmitShift.setEnabled(true);
            if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                showSuccessNotification("Gửi đăng ký thành công");
                loadHistory();
            } else if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleLeaveSubmit() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        if (isDateInPast(sdf.format(new Date(selectedStartDate)))) {
            Toast.makeText(getContext(), "Ngày bắt đầu không thể ở quá khứ", Toast.LENGTH_SHORT).show();
            return;
        }

        String maNv = prefsManager.getMaNv();
        User user = authRepository.getCurrentUser().getValue();
        if (maNv == null) return;
        String reason = binding.etLeaveReason.getText().toString().trim();
        if (reason.isEmpty()) { Toast.makeText(getContext(), "Vui lòng nhập lý do", Toast.LENGTH_SHORT).show(); return; }

        binding.btnSubmitLeave.setEnabled(false);
        Request request = new Request();
        request.setId(generateRequestId("NP"));
        request.setStartDate(sdf.format(new Date(selectedStartDate)));
        request.setEndDate(sdf.format(new Date(selectedEndDate)));
        request.setReason(reason);
        request.setEmployeeId(maNv);
        request.setEmployeeName(user != null ? user.getName() : maNv);
        request.setStatus("Chờ duyệt");
        request.setType("Nghỉ phép");

        requestRepository.addRequest(request).observe(getViewLifecycleOwner(), resource -> {
            binding.btnSubmitLeave.setEnabled(true);
            if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                showSuccessNotification("Gửi đơn nghỉ phép thành công");
                binding.etLeaveReason.setText("");
                loadHistory();
            }
        });
    }

    private void showEditDialog(Request request) {
        DialogEditRegistrationBinding dialogBinding = DialogEditRegistrationBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogBinding.getRoot()).create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final long[] editDates = new long[3]; // 0: shift, 1: start, 2: end
        
        // Khởi tạo ngày hiện tại từ yêu cầu đang sửa
        try {
            Date dStart = sdf.parse(request.getStartDate());
            if (dStart != null) { editDates[0] = dStart.getTime(); editDates[1] = dStart.getTime(); }
            if (request.getEndDate() != null) {
                Date dEnd = sdf.parse(request.getEndDate());
                if (dEnd != null) editDates[2] = dEnd.getTime();
            } else {
                editDates[2] = editDates[1];
            }
        } catch (Exception e) {
            long now = System.currentTimeMillis();
            editDates[0] = now; editDates[1] = now; editDates[2] = now;
        }

        dialogBinding.calendarEditShift.setOnDateChangeListener((v, y, m, d) -> {
            Calendar c = Calendar.getInstance(); c.set(y, m, d); editDates[0] = c.getTimeInMillis();
        });
        dialogBinding.calendarEditStart.setOnDateChangeListener((v, y, m, d) -> {
            Calendar c = Calendar.getInstance(); c.set(y, m, d); editDates[1] = c.getTimeInMillis();
        });
        dialogBinding.calendarEditEnd.setOnDateChangeListener((v, y, m, d) -> {
            Calendar c = Calendar.getInstance(); c.set(y, m, d); editDates[2] = c.getTimeInMillis();
        });

        if ("Nghỉ phép".equals(request.getType())) {
            dialogBinding.layoutEditShift.setVisibility(View.GONE);
            dialogBinding.layoutEditLeave.setVisibility(View.VISIBLE);
            dialogBinding.etEditLeaveReason.setText(request.getReason());
            dialogBinding.calendarEditStart.setDate(editDates[1]);
            dialogBinding.calendarEditEnd.setDate(editDates[2]);
        } else {
            dialogBinding.layoutEditShift.setVisibility(View.VISIBLE);
            dialogBinding.layoutEditLeave.setVisibility(View.GONE);
            dialogBinding.calendarEditShift.setDate(editDates[0]);
            if (request.getReason() != null) {
                if (request.getReason().contains("Sáng")) dialogBinding.rbEditMorning.setChecked(true);
                else if (request.getReason().contains("Chiều")) dialogBinding.rbEditAfternoon.setChecked(true);
                else dialogBinding.rbEditEvening.setChecked(true);
            }
        }

        dialogBinding.btnSaveEdit.setOnClickListener(v -> {
            // Kiểm tra ngày mới có ở quá khứ không
            long targetDate = "Nghỉ phép".equals(request.getType()) ? editDates[1] : editDates[0];
            if (isDateInPast(sdf.format(new Date(targetDate)))) {
                Toast.makeText(getContext(), "Không thể chuyển yêu cầu về ngày trong quá khứ", Toast.LENGTH_SHORT).show();
                return;
            }

            String maNv = prefsManager.getMaNv();
            User user = authRepository.getCurrentUser().getValue();
            
            // CẬP NHẬT ĐẦY ĐỦ THÔNG TIN
            request.setEmployeeId(maNv);
            request.setEmployeeName(user != null ? user.getName() : maNv);
            request.setStatus("Chờ duyệt");
            
            if ("Nghỉ phép".equals(request.getType())) {
                request.setStartDate(sdf.format(new Date(editDates[1])));
                request.setEndDate(sdf.format(new Date(editDates[2])));
                request.setReason(dialogBinding.etEditLeaveReason.getText().toString());
            } else {
                request.setStartDate(sdf.format(new Date(editDates[0])));
                request.setEndDate(request.getStartDate());
                request.setReason(dialogBinding.rbEditMorning.isChecked() ? "Sáng" : dialogBinding.rbEditAfternoon.isChecked() ? "Chiều" : "Tối");
            }

            // SỬA TẠI ĐÂY: Sử dụng ID mới để cập nhật thời gian "Gửi lúc" nếu bạn đã chọn ID theo timestamp trước đó
            // Tuy nhiên, vì yêu cầu của bạn là "ngày đã qua không được chỉnh sửa", 
            // tôi sẽ giữ nguyên ID cũ nếu logic server yêu cầu.
            
            // Tạo ID mới để cập nhật thời gian gửi
            String prefix = "Nghỉ phép".equals(request.getType()) ? "NP" : "YC";
            String oldId = request.getId();
            request.setId(prefix + System.currentTimeMillis() + (new Random().nextInt(900) + 100));

            requestRepository.updateRequest(oldId, request).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessNotification("Cập nhật thành công");
                    loadHistory();
                    dialog.dismiss();
                } else if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                    Toast.makeText(getContext(), "Lỗi cập nhật: " + resource.message, Toast.LENGTH_LONG).show();
                }
            });
        });
        dialogBinding.btnCancelEdit.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showDeleteConfirmDialog(Request request) {
        DialogConfirmDeleteBinding db = DialogConfirmDeleteBinding.inflate(getLayoutInflater());
        AlertDialog d = new AlertDialog.Builder(getContext()).setView(db.getRoot()).create();
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        db.btnDelete.setOnClickListener(v -> {
            requestRepository.deleteRequest(request.getId(), request.getType()).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    showSuccessNotification("Đã xóa yêu cầu");
                    loadHistory();
                    d.dismiss();
                } else if (resource != null && resource.status == com.demo.ltud_n10.core.Resource.Status.ERROR) {
                    Toast.makeText(getContext(), "Xóa thất bại: " + resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        });
        db.btnCancel.setOnClickListener(v -> d.dismiss());
        d.show();
    }

    private void showSuccessNotification(String msg) {
        LayoutSuccessNotificationBinding b = LayoutSuccessNotificationBinding.inflate(getLayoutInflater());
        b.tvMessage.setText(msg);
        FrameLayout root = getActivity().findViewById(android.R.id.content);
        if (root == null) return;
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL; p.topMargin = 100;
        View v = b.getRoot(); v.setLayoutParams(p); root.addView(v);
        new Handler(Looper.getMainLooper()).postDelayed(() -> root.removeView(v), 3000);
    }

    private void loadHistory() {
        String maNv = prefsManager.getMaNv();
        if (maNv == null) return;
        requestRepository.getRequests().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                String type = "SHIFT".equals(currentMode) ? "Đăng ký ca làm" : "Nghỉ phép";
                List<Request> filtered = resource.data.stream()
                        .filter(r -> type.equals(r.getType()) && maNv.equals(r.getEmployeeId()))
                        .collect(Collectors.toList());
                adapter.setItems(filtered);
            }
        });
    }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
