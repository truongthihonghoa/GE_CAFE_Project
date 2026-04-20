package com.demo.ltud_n10.presentation.ui.branch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.FragmentBranchListBinding;
import com.demo.ltud_n10.domain.model.Branch;
import com.demo.ltud_n10.domain.repository.BranchRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BranchListFragment extends Fragment {

    private FragmentBranchListBinding binding;
    private BranchAdapter adapter;

    @Inject
    BranchRepository branchRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBranchListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
        
        binding.btnAddBranch.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "THÊM CHI NHÁNH");
            Navigation.findNavController(requireView()).navigate(R.id.action_branchListFragment_to_branchDetailFragment, bundle);
        });

        binding.etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBranches(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        loadData();
    }

    private List<Branch> allBranches = new ArrayList<>();

    private void filterBranches(String query) {
        if (query.isEmpty()) {
            adapter.setItems(allBranches);
        } else {
            List<Branch> filtered = allBranches.stream()
                    .filter(b -> b.getName().toLowerCase().contains(query.toLowerCase()) || 
                                b.getAddress().toLowerCase().contains(query.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            adapter.setItems(filtered);
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
        adapter = new BranchAdapter();
        adapter.setOnBranchActionListener(new BranchAdapter.OnBranchActionListener() {
            @Override
            public void onView(Branch branch) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("branch", branch);
                bundle.putString("title", "XEM CHI TIẾT CHI NHÁNH");
                bundle.putBoolean("isReadOnly", true);
                Navigation.findNavController(requireView()).navigate(R.id.action_branchListFragment_to_branchDetailFragment, bundle);
            }

            @Override
            public void onEdit(Branch branch) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("branch", branch);
                bundle.putString("title", "CHỈNH SỬA CHI NHÁNH");
                bundle.putBoolean("isReadOnly", false);
                Navigation.findNavController(requireView()).navigate(R.id.action_branchListFragment_to_branchDetailFragment, bundle);
            }

            @Override
            public void onDelete(Branch branch) {
                showDeleteConfirmation(branch);
            }
        });

        binding.rvBranches.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBranches.setAdapter(adapter);
    }

    private void showDeleteConfirmation(Branch branch) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa chi nhánh '" + branch.getName() + "'?")
                .setPositiveButton("Xóa", (d, w) -> {
                    branchRepository.deleteBranch(branch.getId()).observe(getViewLifecycleOwner(), resource -> {
                        if (resource != null && resource.data != null && resource.data) {
                            Toast.makeText(requireContext(), "Đã xóa chi nhánh thành công", Toast.LENGTH_SHORT).show();
                            loadData();
                        } else if (resource != null && resource.message != null) {
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void loadData() {
        branchRepository.getBranches().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                allBranches = resource.data;
                adapter.setItems(allBranches);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
