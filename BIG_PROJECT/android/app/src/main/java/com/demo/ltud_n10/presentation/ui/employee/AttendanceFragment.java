package com.demo.ltud_n10.presentation.ui.employee;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.databinding.DialogAttendanceFailureBinding;
import com.demo.ltud_n10.databinding.FragmentAttendanceBinding;
import com.demo.ltud_n10.databinding.ItemAttendanceHistoryBinding;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.core.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AttendanceFragment extends Fragment {

    private FragmentAttendanceBinding binding;
    private String currentAction = "";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ProcessCameraProvider cameraProvider;
    private boolean isProcessing = false;
    private HistoryAdapter adapter;

    @Inject
    ApiService apiService;

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
        setupHistoryList();
    }

    private void setupToolbar() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
    }

    private void setupListeners() {
        binding.btnStartShift.setOnClickListener(v -> showScanner("Bắt đầu ca"));
        binding.btnEndShift.setOnClickListener(v -> showScanner("Kết thúc ca"));
        binding.btnCancelScanner.setOnClickListener(v -> hideScanner());
        binding.btnViewAttendance.setOnClickListener(v -> showHistory());
        binding.btnCancelHistory.setOnClickListener(v -> hideHistory());
    }

    private void setupHistoryList() {
        adapter = new HistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(adapter);
        loadHistory();
    }

    private void loadHistory() {
        apiService.getAttendances().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HistoryItem> items = new ArrayList<>();
                    for (Map<String, Object> data : response.body()) {
                        Object nameObj = data.get("ten_nhan_vien_that");
                        if (nameObj == null) nameObj = data.get("ho_ten");
                        if (nameObj == null) nameObj = data.get("ma_nv");

                        String name = String.valueOf(nameObj);

                        // Nếu đã có giờ ra, hiện dòng "Kết thúc ca"
                        if (data.get("gio_ra") != null) {
                            items.add(new HistoryItem(name, "Kết thúc ca"));
                        }

                        // Luôn hiện dòng "Bắt đầu ca"
                        if (data.get("gio_vao") != null) {
                            items.add(new HistoryItem(name, "Bắt đầu ca"));
                        }
                    }
                    adapter.setItems(items);
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void showScanner(String title) {
        currentAction = title;
        binding.topBar.setVisibility(View.GONE);
        binding.layoutMain.setVisibility(View.GONE);
        binding.layoutHistory.setVisibility(View.GONE);
        binding.layoutScanner.setVisibility(View.VISIBLE);

        binding.tvScannerTitle.setText(title);
        binding.btnCancelScanner.setVisibility(View.VISIBLE);
        binding.btnViewAttendance.setVisibility(View.GONE);
        binding.cvSuccess.setVisibility(View.GONE);
        binding.cvFailure.setVisibility(View.GONE);

        isProcessing = false;
        checkCameraPermission();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(getContext(), "Bạn cần cấp quyền Camera để chấm công", Toast.LENGTH_SHORT).show();
            hideScanner();
        }
    }

    @androidx.annotation.OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraPreview();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Không thể mở camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @androidx.annotation.OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private void bindCameraPreview() {
        if (cameraProvider == null) return;

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        // Cấu hình quét mã QR
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), imageProxy -> {
            android.media.Image mediaImage = imageProxy.getImage();
            if (mediaImage != null && !isProcessing) {
                InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                scanner.process(image)
                        .addOnSuccessListener(barcodes -> {
                            for (Barcode barcode : barcodes) {
                                String rawValue = barcode.getRawValue();
                                if (rawValue != null) {
                                    onQrScanned(rawValue);
                                    break;
                                }
                            }
                        })
                        .addOnCompleteListener(task -> imageProxy.close());
            } else {
                imageProxy.close();
            }
        });

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onQrScanned(String content) {
        if (isProcessing) return;
        isProcessing = true;

        // Dừng camera khi quét thành công
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }

        // Gọi API chấm công thật
        Map<String, String> body = new HashMap<>();
        body.put("ma_chi_nhanh", content); // content là mã quán quét được từ QR

        apiService.checkIn(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    handleScanResult(true);
                    loadHistory(); // Tải lại lịch sử sau khi chấm công
                } else {
                    handleScanResult(false);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                handleScanResult(false);
            }
        });
    }

    private void handleScanResult(boolean success) {
        if (success) {
            binding.cvSuccess.setVisibility(View.VISIBLE);
            binding.cvFailure.setVisibility(View.GONE);
            binding.btnCancelScanner.setVisibility(View.GONE);
            binding.btnViewAttendance.setVisibility(View.VISIBLE);

            updateMainStatus();
        } else {
            binding.cvSuccess.setVisibility(View.GONE);
            binding.cvFailure.setVisibility(View.VISIBLE);

            handler.postDelayed(() -> {
                if (binding != null && binding.cvFailure.getVisibility() == View.VISIBLE) {
                    showFailurePopup();
                }
            }, 5000);
        }
    }

    private void showFailurePopup() {
        if (getContext() == null) return;

        DialogAttendanceFailureBinding dialogBinding = DialogAttendanceFailureBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogBinding.btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            showScanner(currentAction);
        });

        dialogBinding.btnNo.setOnClickListener(v -> {
            dialog.dismiss();
            hideScanner();
        });

        dialog.show();
    }

    private void showHistory() {
        binding.layoutScanner.setVisibility(View.GONE);
        binding.layoutHistory.setVisibility(View.VISIBLE);
    }

    private void hideHistory() {
        binding.layoutHistory.setVisibility(View.GONE);
        hideScanner();
    }

    private void updateMainStatus() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        String status = currentAction.contains("Bắt đầu") ?
                "Đã vào ca lúc " + currentTime :
                "Đã kết thúc ca lúc " + currentTime;

        binding.tvAttendanceStatus.setText(status);
        binding.tvAttendanceStatus.setTextColor(Color.WHITE);
    }

    private void hideScanner() {
        binding.topBar.setVisibility(View.VISIBLE);
        binding.layoutMain.setVisibility(View.VISIBLE);
        binding.layoutScanner.setVisibility(View.GONE);
        binding.layoutHistory.setVisibility(View.GONE);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }

    // Inner classes for History List
    static class HistoryItem {
        String name, action;
        HistoryItem(String name, String action) { this.name = name; this.action = action; }
    }

    class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private List<HistoryItem> items = new ArrayList<>();
        void setItems(List<HistoryItem> items) { this.items = items; notifyDataSetChanged(); }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ItemAttendanceHistoryBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HistoryItem item = items.get(position);
            holder.binding.tvName.setText(item.name);
            holder.binding.tvAction.setText(item.action);
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ItemAttendanceHistoryBinding binding;
            ViewHolder(ItemAttendanceHistoryBinding binding) { super(binding.getRoot()); this.binding = binding; }
        }
    }
}