package com.demo.ltud_n10.presentation.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.User;
import com.demo.ltud_n10.domain.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    @Inject
    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<Resource<User>> login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            _error.setValue("Vui lòng nhập đầy đủ thông tin");
            return new MutableLiveData<>(null);
        }
        return authRepository.login(email, password);
    }
}
