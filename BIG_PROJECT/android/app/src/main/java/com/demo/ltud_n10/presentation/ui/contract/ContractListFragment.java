package com.demo.ltud_n10.presentation.ui.contract;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContractListFragment extends Fragment {

    private FragmentContractListBinding binding;
    private ContractViewModel viewModel;
    private ContractAdapter adapter;
    private List<Contract> fullList = new ArrayList<>();

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
        setupSearch();
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

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String query) {
        List<Contract> filteredList = new ArrayList<>();
        for (Contract contract : fullList) {
            if (contract.getEmployeeName().toLowerCase().contains(query.toLowerCase()) ||
                contract.getId().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(contract);
            }
        }
        adapter.setContracts(filteredList);
    }

    private void setupRecyclerView() {
        adapter = new ContractAdapter(new ContractAdapter.OnContractClickListener() {
            @Override
            public void onItemClick(Contract contract) {
                Bundle args = new Bundle();
                args.putSerializable("contract", contract);
                args.putString("title", "Xem hợp đồng lao động");
                args.putBoolean("isViewOnly", true);
                Navigation.findNavController(requireView()).navigate(R.id.action_contractListFragment_to_contractDetailFragment, args);
            }

            @Override
            public void onEdit(Contract contract) {
                Bundle args = new Bundle();
                args.putSerializable("contract", contract);
                args.putString("title", "Chỉnh sửa hợp đồng lao động");
                args.putBoolean("isViewOnly", false);
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
                fullList = resource.data;
                adapter.setContracts(fullList);
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
