package com.demo.ltud_n10.presentation.ui.contract;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.demo.ltud_n10.databinding.DialogCustomConfirmBinding;
import com.demo.ltud_n10.databinding.FragmentContractListBinding;
import com.demo.ltud_n10.domain.model.Contract;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContractListFragment extends Fragment {

    private FragmentContractListBinding binding;
    private ContractViewModel viewModel;
    private ContractAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContractListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ContractViewModel.class);

        setupUI();
        setupRecyclerView();
        observeViewModel();
    }

    private void setupUI() {
        binding.ivMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        binding.btnCreateContract.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("title", "Tạo hợp đồng lao động");
            Navigation.findNavController(v).navigate(R.id.action_contractListFragment_to_contractDetailFragment, args);
        });
    }

    private void setupRecyclerView() {
        adapter = new ContractAdapter(new ContractAdapter.OnContractClickListener() {
            @Override
            public void onEdit(Contract contract) {
                Bundle args = new Bundle();
                args.putSerializable("contract", contract);
                args.putString("title", "Chỉnh sửa hợp đồng lao động");
                Navigation.findNavController(requireView()).navigate(R.id.action_contractListFragment_to_contractDetailFragment, args);
            }

            @Override
            public void onDelete(Contract contract) {
                showDeleteDialog(contract);
            }
        });
        binding.rvContracts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvContracts.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getContracts().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case SUCCESS:
                    adapter.setContracts(resource.data);
                    break;
                case ERROR:
                    showErrorDialog("THÔNG BÁO LỖI", "Lỗi hệ thống. Vui lòng thử lại sau !");
                    break;
            }
        });
    }

    private void showDeleteDialog(Contract contract) {
        showConfirmDialog("XÁC NHẬN XÓA", "Bạn có chắc chắn muốn xóa hợp đồng lao động này không?", "Không", "Xóa", () -> {
            viewModel.deleteContract(contract.getId()).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.SUCCESS) {
                    observeViewModel(); // Refresh list
                } else if (resource.status == Resource.Status.ERROR) {
                    showErrorToast("Không thể xóa hợp đồng này");
                }
            });
        });
    }

    private void showConfirmDialog(String title, String message, String negativeText, String positiveText, Runnable onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCustomConfirmBinding dialogBinding = DialogCustomConfirmBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogBinding.tvTitle.setText(title);
        dialogBinding.tvMessage.setText(message);
        dialogBinding.ivIcon.setImageResource(R.drawable.ic_warning_outline);

        dialogBinding.btnNegative.setText(negativeText);
        dialogBinding.btnPositive.setText(positiveText);

        dialogBinding.btnNegative.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnPositive.setOnClickListener(v -> {
            dialog.dismiss();
            onConfirm.run();
        });

        dialog.show();
    }

    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCustomConfirmBinding dialogBinding = DialogCustomConfirmBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogBinding.tvTitle.setText(title);
        dialogBinding.tvMessage.setText(message);
        dialogBinding.ivIcon.setImageResource(R.drawable.ic_error_x);

        dialogBinding.btnNegative.setText("Thoát");
        dialogBinding.btnPositive.setText("Quay lại");

        dialogBinding.btnNegative.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnPositive.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showErrorToast(String msg) {
        View layout = getLayoutInflater().inflate(R.layout.layout_custom_toast_error, null);
        TextView tvMessage = layout.findViewById(R.id.tvMessage);
        tvMessage.setText(msg);

        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}