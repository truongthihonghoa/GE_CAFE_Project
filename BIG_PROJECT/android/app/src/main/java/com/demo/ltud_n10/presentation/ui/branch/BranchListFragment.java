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
    private List<Branch> fullBranchList = new ArrayList<>();

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

        binding.btnSearch.setOnClickListener(v -> {
            filter(binding.etSearch.getText().toString());
        });
    }

    private void filter(String query) {
        if (fullBranchList == null) return;
        
        String lowerQuery = query.toLowerCase().trim();
        List<Branch> filtered = new ArrayList<>();
        
        for (Branch b : fullBranchList) {
            if (b.getName().toLowerCase().contains(lowerQuery) || 
                b.getAddress().toLowerCase().contains(lowerQuery) ||
                b.getId().toLowerCase().contains(lowerQuery)) {
                filtered.add(b);
            }
        }
        adapter.setItems(filtered);
    }

    private void loadData() {
        branchRepository.getBranches().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                fullBranchList = resource.data;
                filter(binding.etSearch.getText().toString());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
