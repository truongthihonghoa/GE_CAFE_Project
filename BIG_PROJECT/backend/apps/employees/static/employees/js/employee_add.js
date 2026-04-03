document.addEventListener('DOMContentLoaded', function() {
    const employeeForm = document.querySelector('.employee-form');
    const saveBtn = document.querySelector('.save-btn');
    
    // --- Generic Error Popup ---
    const errorPopup = document.getElementById('error-popup');
    const errorPopupTitle = document.getElementById('error-popup-title');
    const errorPopupMsg1 = document.getElementById('error-popup-message1');
    const errorPopupMsg2 = document.getElementById('error-popup-message2');
    const errorPopupExitBtn = document.getElementById('error-popup-exit-btn');
    const errorPopupBackBtn = document.getElementById('error-popup-back-btn');

    function showErrorPopup(title, msg1, msg2) {
        if (errorPopup) {
            errorPopupTitle.textContent = title;
            errorPopupMsg1.textContent = msg1;
            errorPopupMsg2.textContent = msg2;
            errorPopup.style.display = 'flex';
        }
    }

    function hideErrorPopup() {
        if (errorPopup) errorPopup.style.display = 'none';
    }

    // --- Client-side Validation ---
    if (saveBtn) {
        saveBtn.addEventListener('click', function(event) {
            if (!employeeForm.checkValidity()) {
                event.preventDefault();
                showErrorPopup(
                    'THÔNG BÁO LỖI', 
                    'Xin vui lòng nhập đầy đủ thông tin!', 
                    ''
                );
            }
        });
    }

    // --- Server-side Error Handling ---
    const autoShowDuplicate = document.getElementById('auto-show-duplicate-popup');
    if (autoShowDuplicate) {
        showErrorPopup(
            'THÔNG BÁO LỖI', 
            'CCCD hoặc số điện thoại đã tồn tại?', 
            'Xin vui lòng nhập lại thông tin chính xác!'
        );
    }

    const autoShowInvalidInfo = document.getElementById('auto-show-invalid-info-popup');
    if (autoShowInvalidInfo) {
        showErrorPopup(
            'THÔNG BÁO LỖI', 
            'Thông tin không hợp lệ?', 
            'Xin vui lòng nhập lại thông tin chính xác!'
        );
    }

    // --- Popup Button Listeners ---
    if (errorPopupExitBtn) {
        errorPopupExitBtn.addEventListener('click', function() {
            window.location.href = employeeForm.dataset.employeeListUrl;
        });
    }

    if (errorPopupBackBtn) {
        errorPopupBackBtn.addEventListener('click', hideErrorPopup);
    }
    
    if (errorPopup) {
        errorPopup.addEventListener('click', function(event) {
            if (event.target === errorPopup) hideErrorPopup();
        });
    }
});
