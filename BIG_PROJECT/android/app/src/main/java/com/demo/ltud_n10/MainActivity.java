package com.demo.ltud_n10;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.demo.ltud_n10.databinding.ActivityMainBinding;
import com.demo.ltud_n10.databinding.DialogLogoutConfirmBinding;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Inject
    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.navView, navController);
            
            binding.navView.setNavigationItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_logout) {
                    showLogoutDialog();
                    return true;
                }
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
                return handled;
            });
        }

        // Lock drawer initially (at login)
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.forgotPasswordFragment) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                updateNavMenuBasedOnRole();
            }
        });

        View headerView = binding.navView.getHeaderView(0);
        headerView.findViewById(R.id.ivCloseDrawer).setOnClickListener(v -> 
                binding.drawerLayout.closeDrawer(GravityCompat.START));

        setupLogoutItem();
    }

    private void updateNavMenuBasedOnRole() {
        User user = authRepository.getCurrentUser().getValue();
        if (user == null) return;

        int menuRes = "ADMIN".equals(user.getRole()) ? R.menu.nav_menu : R.menu.nav_menu_employee;
        
        // Only re-inflate if the menu has changed
        if (binding.navView.getTag() == null || (int)binding.navView.getTag() != menuRes) {
            binding.navView.getMenu().clear();
            binding.navView.inflateMenu(menuRes);
            binding.navView.setTag(menuRes);
            
            // Re-setup with NavController
            NavigationUI.setupWithNavController(binding.navView, navController);
            
            // Re-setup logout listener
            binding.navView.setNavigationItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_logout) {
                    showLogoutDialog();
                    return true;
                }
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
                return handled;
            });
            
            setupLogoutItem();
        }

        // Update Header
        View headerView = binding.navView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tvNavUserName);
        TextView tvEmail = headerView.findViewById(R.id.tvNavUserEmail);
        TextView tvRole = headerView.findViewById(R.id.tvNavUserRole);

        tvName.setText(user.getName());
        tvEmail.setText(user.getUsername());
        if ("ADMIN".equals(user.getRole())) {
            tvRole.setText("Quản trị viên");
            tvRole.setTextColor(Color.parseColor("#0A4D1E"));
        } else {
            tvRole.setText("Nhân viên");
            tvRole.setTextColor(Color.parseColor("#2ECC71"));
        }
    }

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(this);
        DialogLogoutConfirmBinding dialogBinding = DialogLogoutConfirmBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogBinding.btnNo.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            authRepository.logout();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            navController.navigate(R.id.loginFragment);
        });

        dialog.show();
    }

    private void setupLogoutItem() {
        MenuItem logoutItem = binding.navView.getMenu().findItem(R.id.nav_logout);
        if (logoutItem != null) {
            SpannableString s = new SpannableString(logoutItem.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
            logoutItem.setTitle(s);
        }
    }

    public void openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }
}
