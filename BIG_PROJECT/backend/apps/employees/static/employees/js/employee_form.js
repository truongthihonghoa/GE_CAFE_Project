document.addEventListener('DOMContentLoaded', function() {
    const employeeForm = document.getElementById('employee-form');
    const saveBtn = document.getElementById('save-btn');
    const cancelBtn = document.getElementById('cancel-btn');
    const confirmCancelPopup = document.getElementById('confirm-cancel-popup');
    const successPopup = document.getElementById('success-popup');
    const errorPopup = document.getElementById('error-popup');

    // Form validation
    function validateForm() {
        const hoTen = document.getElementById('ho_ten')?.value?.trim();
        const ngaySinh = document.getElementById('ngay_sinh')?.value;
        const cccd = document.getElementById('cccd')?.value?.trim();
        const sdt = document.getElementById('sdt')?.value?.trim();
        const chucVu = document.getElementById('chuc_vu')?.value?.trim();
        const viTri = document.getElementById('vi_tri_viec_lam')?.value?.trim();
        const diaChiThuongTru = document.getElementById('dia_chi_thuong_tru')?.value?.trim();

        // Validate Họ tên
        if (!hoTen) {
            showErrorPopup('Họ tên không được để trống');
            return false;
        }

        // Validate Ngày sinh
        if (!ngaySinh) {
            showErrorPopup('Ngày sinh không hợp lệ');
            return false;
        }
        
        const birthDate = new Date(ngaySinh);
        const today = new Date();
        const age = today.getFullYear() - birthDate.getFullYear();
        if (age < 18) {
            showErrorPopup('Nhân viên phải từ 18 tuổi trở lên');
            return false;
        }

        // Validate CCCD (12 số)
        if (!cccd || !/^\d{12}$/.test(cccd)) {
            showErrorPopup('CCCD không hợp lệ');
            return false;
        }

        // Validate SĐT (10 số, bắt đầu bằng 0)
        if (!sdt || !/^0\d{9}$/.test(sdt)) {
            showErrorPopup('Số điện thoại không hợp lệ');
            return false;
        }

        // Validate Chức vụ
        if (!chucVu) {
            showErrorPopup('Chức vụ không được để trống');
            return false;
        }

        // Validate Vị trí việc làm
        if (!viTri) {
            showErrorPopup('Vị trí việc làm không được để trống');
            return false;
        }

        // Validate Địa chỉ thường trú
        if (!diaChiThuongTru) {
            showErrorPopup('Địa chỉ không được để trống');
            return false;
        }

        return true;
    }

    // Mock form submission
    function handleSubmit(event) {
        event.preventDefault();
        
        if (!validateForm()) {
            return;
        }

        // Show loading if available
        if (saveBtn) {
            saveBtn.disabled = true;
            saveBtn.textContent = 'Đang lưu...';
        }

        // Mock success after 500ms
        setTimeout(() => {
            if (saveBtn) {
                saveBtn.disabled = false;
                saveBtn.textContent = 'Lưu';
            }
            
            // Show success popup
            const successMessage = employeeForm.dataset.successMessage || 'Thao tác thành công';
            showSuccessPopup(successMessage);
            
            // Reset form
            employeeForm.reset();
        }, 500);
    }

    // Show error popup
    function showErrorPopup(message) {
        if (errorPopup) {
            const messageEl = errorPopup.querySelector('.popup-message');
            if (messageEl) messageEl.textContent = message;
            errorPopup.style.display = 'flex';
        } else {
            alert('Lỗi: ' + message);
        }
    }

    // Show success popup
    function showSuccessPopup(message) {
        if (successPopup) {
            const messageEl = successPopup.querySelector('.popup-message');
            if (messageEl) messageEl.textContent = message;
            successPopup.style.display = 'flex';
        } else {
            alert('Thành công: ' + message);
        }
    }

    // Hide popups
    function hidePopup(popup) {
        if (popup) popup.style.display = 'none';
    }

    // Event listeners
    if (employeeForm) {
        employeeForm.addEventListener('submit', handleSubmit);
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            if (confirmCancelPopup) {
                confirmCancelPopup.style.display = 'flex';
            } else {
                window.location.href = employeeForm.dataset.cancelUrl || '/employees/';
            }
        });
    }

    // Confirm cancel popup buttons
    const confirmNoBtn = document.getElementById('confirm-no-btn');
    const confirmYesBtn = document.getElementById('confirm-yes-btn');
    
    if (confirmNoBtn) {
        confirmNoBtn.addEventListener('click', function() {
            hidePopup(confirmCancelPopup);
        });
    }

    if (confirmYesBtn) {
        confirmYesBtn.addEventListener('click', function() {
            window.location.href = employeeForm.dataset.cancelUrl || '/employees/';
        });
    }

    // Success popup OK button
    const successOkBtn = document.getElementById('success-ok-btn');
    if (successOkBtn) {
        successOkBtn.addEventListener('click', function() {
            hidePopup(successPopup);
            // Stay on page, don't redirect
        });
    }

    // Error popup OK button
    const errorOkBtn = document.getElementById('error-ok-btn');
    if (errorOkBtn) {
        errorOkBtn.addEventListener('click', function() {
            hidePopup(errorPopup);
        });
    }

    // Close popups on overlay click
    [confirmCancelPopup, successPopup, errorPopup].forEach(popup => {
        if (popup) {
            popup.addEventListener('click', function(event) {
                if (event.target === popup) {
                    hidePopup(popup);
                }
            });
        }
    });

    // Prevent form submission on Enter key for certain fields
    const formInputs = employeeForm?.querySelectorAll('input, select, textarea');
    formInputs?.forEach(input => {
        input.addEventListener('keypress', function(event) {
            if (event.key === 'Enter' && event.target.tagName !== 'TEXTAREA') {
                event.preventDefault();
            }
        });
    });
});
