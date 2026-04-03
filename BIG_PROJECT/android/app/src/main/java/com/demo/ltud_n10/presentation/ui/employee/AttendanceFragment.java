package com.demo.ltud_n10.presentation.ui.employee;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.FragmentAttendanceBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AttendanceFragment extends Fragment {

    private FragmentAttendanceBinding binding;
    private String currentAction = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAttendanceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupListeners();
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void setupListeners() {
        binding.btnStartShift.setOnClickListener(v -> startScanning("BẮT ĐẦU CA"));
        binding.btnEndShift.setOnClickListener(v -> startScanning("KẾT THÚC CA"));
        binding.btnCancelScan.setOnClickListener(v -> stopScanning());
    }

    private void startScanning(String action) {
        currentAction = action;
        binding.layoutSelection.setVisibility(View.GONE);
        binding.layoutScanning.setVisibility(View.VISIBLE);
        binding.tvScanTitle.setText("ĐANG QUÉT MÃ QR (" + action + ")...");

        // Giả lập quét thành công sau 2 giây
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (binding != null && binding.layoutScanning.getVisibility() == View.VISIBLE) {
                Toast.makeText(requireContext(), "Chấm công thành công: " + action, Toast.LENGTH_LONG).show();
                stopScanning();
            }
        }, 2500);
    }

    private void stopScanning() {
        binding.layoutScanning.setVisibility(View.GONE);
        binding.layoutSelection.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
