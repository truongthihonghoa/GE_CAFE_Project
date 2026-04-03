document.addEventListener('DOMContentLoaded', function () {
    const contractForm = document.querySelector('.contract-form');
    const cancelBtn = document.querySelector('.contract-cancel-btn');
    const contractTypeSelect = document.getElementById('loai_hd');
    const luongCoBanInput = document.getElementById('luong_co_ban');
    const luongTheoGioInput = document.getElementById('luong_theo_gio');

    const errorPopup = document.getElementById('error-popup');
    const errorPopupTitle = document.getElementById('error-popup-title');
    const errorPopupMessage1 = document.getElementById('error-popup-message1');
    const errorPopupMessage2 = document.getElementById('error-popup-message2');
    const errorPopupExitBtn = document.getElementById('error-popup-exit-btn');
    const errorPopupBackBtn = document.getElementById('error-popup-back-btn');

    const confirmCancelPopup = document.getElementById('confirm-cancel-popup');
    const confirmNoBtn = document.getElementById('confirm-no-btn');
    const confirmYesBtn = document.getElementById('confirm-yes-btn');

    const successPopup = document.getElementById('success-popup');
    const successPopupTitle = document.getElementById('success-popup-title');
    const successPopupMessage = document.getElementById('success-popup-message');
    const successPopupConfirmBtn = document.getElementById('success-popup-confirm-btn');

    // Handle contract type change
    if (contractTypeSelect) {
        contractTypeSelect.addEventListener('change', function() {
            const contractType = this.value;
            
            if (contractType === 'Full-time') {
                luongCoBanInput.required = true;
                luongTheoGioInput.required = false;
                luongTheoGioInput.value = '';
            } else if (contractType === 'Part-time') {
                luongCoBanInput.required = false;
                luongTheoGioInput.required = true;
                luongCoBanInput.value = '';
            } else {
                luongCoBanInput.required = false;
                luongTheoGioInput.required = false;
            }
        });
    }

    function showErrorPopup(title, message1, message2) {
        errorPopupTitle.textContent = title;
        errorPopupMessage1.textContent = message1;
        errorPopupMessage2.textContent = message2;
        errorPopup.style.display = 'flex';
    }

    function hideErrorPopup() {
        errorPopup.style.display = 'none';
    }

    function showSuccessPopup() {
        successPopupTitle.textContent = contractForm.dataset.successTitle;
        successPopupMessage.textContent = contractForm.dataset.successMessage;
        successPopup.style.display = 'flex';
    }

    function hideSuccessPopup() {
        successPopup.style.display = 'none';
        window.location.href = contractForm.dataset.contractListUrl;
    }

    function handleFormSubmit(event) {
        // Ngăn submit thật - chỉ xử lý UI
        event.preventDefault();
        
        const contractType = contractTypeSelect.value;
        const luongCoBan = parseFloat(luongCoBanInput.value) || 0;
        const luongTheoGio = parseFloat(luongTheoGioInput.value) || 0;
        
        // Client-side validation
        if (contractType === 'Full-time' && luongTheoGio > 0) {
            showErrorPopup('LỖI', 'Full time không có lương/giờ', 'Vui lòng chỉ nhập lương cơ bản cho hợp đồng Full time.');
            return;
        }
        
        if (contractType === 'Part-time' && luongCoBan > 0) {
            showErrorPopup('LỖI', 'Part time không có lương cơ bản', 'Vui lòng chỉ nhập lương/giờ cho hợp đồng Part time.');
            return;
        }
        
        if (contractType === 'Full-time' && luongCoBan <= 0) {
            showErrorPopup('LỖI', 'Full time phải có lương cơ bản', 'Vui lòng nhập lương cơ bản cho hợp đồng Full time.');
            return;
        }
        
        if (contractType === 'Part-time' && luongTheoGio <= 0) {
            showErrorPopup('LỖI', 'Part time phải có lương/giờ', 'Vui lòng nhập lương/giờ cho hợp đồng Part time.');
            return;
        }
        
        // Hiển thị popup success
        const successTitle = contractForm.dataset.successTitle || 'Thành công';
        const successMessage = contractForm.dataset.successMessage || 'Thao tác thành công';
        showSuccessPopup(successTitle, successMessage);
        
        // Reset form sau 2 giây
        setTimeout(() => {
            contractForm.reset();
        }, 2000);
    }

    // Gán handler cho form
    contractForm.addEventListener('submit', handleFormSubmit);

    cancelBtn.addEventListener('click', () => {
        confirmCancelPopup.style.display = 'flex';
    });

    confirmNoBtn.addEventListener('click', () => {
        confirmCancelPopup.style.display = 'none';
    });

    confirmYesBtn.addEventListener('click', () => {
        window.location.href = contractForm.dataset.contractListUrl;
    });

    confirmCancelPopup.addEventListener('click', (event) => {
        if (event.target === confirmCancelPopup) {
            confirmCancelPopup.style.display = 'none';
        }
    });

    errorPopupExitBtn.addEventListener('click', () => {
        window.location.href = contractForm.dataset.contractListUrl;
    });

    errorPopupBackBtn.addEventListener('click', hideErrorPopup);

    errorPopup.addEventListener('click', (event) => {
        if (event.target === errorPopup) {
            hideErrorPopup();
        }
    });

    successPopupConfirmBtn.addEventListener('click', hideSuccessPopup);
});
