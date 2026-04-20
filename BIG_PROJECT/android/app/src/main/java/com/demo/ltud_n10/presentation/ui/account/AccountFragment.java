package com.demo.ltud_n10.presentation.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.ltud_n10.MainActivity;
import com.demo.ltud_n10.R;
import com.demo.ltud_n10.databinding.FragmentAccountBinding;
import com.demo.ltud_n10.domain.model.Account;
import com.demo.ltud_n10.domain.repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private AccountAdapter adapter;
    private List<Account> allAccounts = new ArrayList<>();

    @Inject
    AccountRepository accountRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
        setupSearch();

        binding.btnAddAccount.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "CẤP TÀI KHOẢN MỚI");
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
        adapter.setOnAccountActionListener(new AccountAdapter.OnAccountActionListener() {
            @Override
            public void onView(Account account) {
                navigateToDetail(account, "XEM CHI TIẾT TÀI KHOẢN", true);
            }

            @Override
            public void onEdit(Account account) {
                navigateToDetail(account, "CHỈNH SỬA TÀI KHOẢN", false);
            }

            @Override
            public void onDelete(Account account) {
                showDeleteConfirmation(account);
            }
        });

        binding.rvAccounts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAccounts.setAdapter(adapter);
    }

    private void navigateToDetail(Account account, String title, boolean isReadOnly) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("account", account);
        bundle.putString("title", title);
        bundle.putBoolean("isReadOnly", isReadOnly);
        Navigation.findNavController(requireView()).navigate(R.id.action_accountFragment_to_accountDetailFragment, bundle);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAccounts(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filterAccounts(String query) {
        if (query.isEmpty()) {
            adapter.setItems(allAccounts);
        } else {
            String lowerQuery = query.toLowerCase();
            List<Account> filtered = allAccounts.stream()
                    .filter(a -> a.getUsername().toLowerCase().contains(lowerQuery) || 
                                (a.getEmployeeName() != null && a.getEmployeeName().toLowerCase().contains(lowerQuery)))
                    .collect(Collectors.toList());
            adapter.setItems(filtered);
        }
    }

    private void loadData() {
        accountRepository.getAccounts().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                allAccounts = resource.data;
                adapter.setItems(allAccounts);
            }
        });
    }

    private void showDeleteConfirmation(Account account) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản '" + account.getUsername() + "'?")
                .setPositiveButton("Xóa", (d, w) -> {
                    accountRepository.deleteAccount(account.getId()).observe(getViewLifecycleOwner(), resource -> {
                        if (resource != null && resource.data != null && resource.data) {
                            Toast.makeText(requireContext(), "Đã xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                            loadData();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
