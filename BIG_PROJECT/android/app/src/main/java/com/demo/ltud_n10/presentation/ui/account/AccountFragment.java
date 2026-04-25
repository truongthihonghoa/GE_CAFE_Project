package com.demo.ltud_n10.presentation.ui.account;

import android.os.Bundle;
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
import com.demo.ltud_n10.databinding.FragmentAccountListBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountFragment extends Fragment {

    private FragmentAccountListBinding binding;
    private AccountAdapter adapter;

    @Inject
    UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
        
        binding.btnAddAccount.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "TẠO TÀI KHOẢN");
            Navigation.findNavController(requireView()).navigate(R.id.action_accountFragment_to_accountDetailFragment, bundle);
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
        adapter = new AccountAdapter();
        adapter.setOnItemClickListener(new AccountAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(User user) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                bundle.putString("title", "CẬP NHẬT TÀI KHOẢN");
                Navigation.findNavController(requireView()).navigate(R.id.action_accountFragment_to_accountDetailFragment, bundle);
            }

            @Override
            public void onItemClick(User user) {
                // View detail if needed
            }

            @Override
            public void onChangePasswordClick(User user) {
                // Điều hướng sang trang đổi mật khẩu (đã được tạo ở bước trước)
                Navigation.findNavController(requireView()).navigate(R.id.changePasswordFragment);
            }
        });

        binding.rvAccounts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAccounts.setAdapter(adapter);
    }

    private void loadData() {
        userRepository.getUsers().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                adapter.setItems(resource.data);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
