package com.demo.ltud_n10.presentation.ui.account;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.FragmentAccountListBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;
import com.demo.ltud_n10.domain.repository.UserRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountFragment extends Fragment {

    private FragmentAccountListBinding binding;
    private AccountAdapter adapter;
    private String loggedInUserRole = "EMPLOYEE";
    private User loggedInUser;

    @Inject
    UserRepository userRepository;

    @Inject
    AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser != null) {
                loggedInUser = currentUser;
                loggedInUserRole = currentUser.getRole();
                
                if (adapter != null) {
                    adapter.setCurrentUserRole(loggedInUserRole);
                }
                
                if (!"ADMIN".equals(loggedInUserRole)) {
                    // NẾU LÀ NHÂN VIÊN: Chuyển sang trang thông tin cá nhân và xóa trang này khỏi Backstack
                    navigateToDetail(loggedInUser, "THÔNG TIN TÀI KHOẢN", true);
                } else {
                    setupToolbar();
                    setupRecyclerView();
                    
                    if (binding.btnAddAccount != null) {
                        binding.btnAddAccount.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("title", "TẠO TÀI KHOẢN");
                            bundle.putBoolean("isReadOnly", false);
                            Navigation.findNavController(v).navigate(R.id.action_accountFragment_to_accountDetailFragment, bundle);
                        });
                    }
                    
                    loadData();
                }
            }
        });
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
        adapter.setCurrentUserRole(loggedInUserRole);
        adapter.setOnItemClickListener(new AccountAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(User user) {
                navigateToDetail(user, "CẬP NHẬT TÀI KHOẢN", false);
            }

            @Override
            public void onDeleteClick(User user) {
                showDeleteConfirmDialog(user);
            }

            @Override
            public void onItemClick(User user) {
                // Nhấn vào tên:
                // Nếu là Quản lý Lài -> Chỉ xem. Nếu là nhân viên khác -> Sửa.
                if (user.getName() != null && user.getName().equals("Trần Thị Thúy Lài")) {
                    navigateToDetail(user, "THÔNG TIN TÀI KHOẢN", true);
                } else {
                    navigateToDetail(user, "CẬP NHẬT TÀI KHOẢN", false);
                }
            }
        });

        binding.rvAccounts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAccounts.setAdapter(adapter);
    }

    private void navigateToDetail(User user, String title, boolean isReadOnly) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        bundle.putString("title", title);
        bundle.putBoolean("isReadOnly", isReadOnly);
        
        if (!"ADMIN".equals(loggedInUserRole)) {
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.accountFragment, true)
                    .build();
            Navigation.findNavController(requireView()).navigate(R.id.action_accountFragment_to_accountDetailFragment, bundle, navOptions);
        } else {
            Navigation.findNavController(requireView()).navigate(R.id.action_accountFragment_to_accountDetailFragment, bundle);
        }
    }

    private void showDeleteConfirmDialog(User user) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_delete_account);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
        MaterialButton btnDelete = dialog.findViewById(R.id.btnDelete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            userRepository.toggleUserStatus(user.getId()).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Đã xóa nhân viên: " + user.getName(), Toast.LENGTH_SHORT).show();
                    loadData();
                }
            });
        });

        dialog.show();
    }

    private void loadData() {
        userRepository.getUsers().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            if (resource.status == com.demo.ltud_n10.core.Resource.Status.SUCCESS && resource.data != null) {
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
