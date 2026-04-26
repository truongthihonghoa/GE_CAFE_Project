package com.demo.ltud_n10.presentation.ui.branch;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private List<Branch> allBranches = new ArrayList<>();

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
        setupSearch();
        
        binding.btnAddBranch.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "THÊM CHI NHÁNH");
            Navigation.findNavController(requireView()).navigate(R.id.action_branchListFragment_to_branchDetailFragment, bundle);
        });

        loadData();
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
        adapter.setOnItemClickListener(branch -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("branch", branch);
            bundle.putString("title", "XEM CHI TIẾT CHI NHÁNH");
            Navigation.findNavController(requireView()).navigate(R.id.action_branchListFragment_to_branchDetailFragment, bundle);
        });

        binding.rvBranches.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBranches.setAdapter(adapter);
    }

    private void setupSearch() {
        // Tìm kiếm khi nhấn nút
        binding.btnSearch.setOnClickListener(v -> {
            String query = binding.etSearch.getText().toString().trim();
            filter(query);
        });

        // Tìm kiếm thời gian thực khi nhập (tùy chọn, giúp trải nghiệm tốt hơn)
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

    private void filter(String text) {
        List<Branch> filteredList = new ArrayList<>();
        for (Branch item : allBranches) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getAddress().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.setItems(filteredList);
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
