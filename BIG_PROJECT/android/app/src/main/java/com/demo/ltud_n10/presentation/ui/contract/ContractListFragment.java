package com.demo.ltud_n10.presentation.ui.contract;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            if (resource.status == Resource.Status.SUCCESS) {
                adapter.setContracts(resource.data);
            }
        });
    }

    private void showDeleteDialog(Contract contract) {
        new AlertDialog.Builder(requireContext())
                .setTitle("XÁC NHẬN XÓA")
                .setMessage("Bạn có chắc chắn muốn xóa hợp đồng lao động này không?")
                .setPositiveButton("Đồng ý", (d, w) -> {
                    viewModel.deleteContract(contract.getId()).observe(getViewLifecycleOwner(), resource -> {
                        if (resource.status == Resource.Status.SUCCESS) {
                            Toast.makeText(requireContext(), "Đã xóa hợp đồng lao động", Toast.LENGTH_SHORT).show();
                            observeViewModel(); // Refresh list
                        } else if (resource.status == Resource.Status.ERROR) {
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
