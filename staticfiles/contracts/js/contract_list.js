document.addEventListener('DOMContentLoaded', function () {
    const detailPopup = document.getElementById('contract-detail-popup');
    const detailCloseBtn = document.getElementById('contract-detail-close-btn');
    const detailButtons = document.querySelectorAll('.detail-btn');
    const deletePopup = document.getElementById('confirm-delete-popup');
    const deleteButtons = document.querySelectorAll('.delete-btn');
    const deleteNoBtn = document.getElementById('delete-popup-no-btn');
    const deleteYesBtn = document.getElementById('delete-popup-yes-btn');

    function populateDetailModal(data) {
        const elements = {
            'detail-contract-id': data.ma_hd,
            'detail-contract-number': data.so_hd_hien_thi || data.ma_hd,
            'detail-signed-date': data.ngay_ky || data.ngay_bd,
            'detail-company-representative': data.dai_dien_ben_a || 'Truong Thi Hong Hoa',
            'detail-company-position': data.chuc_vu_ben_a || 'Quan ly GE CAFE - Chi nhanh Le Hong Phong',
            'detail-employee-name': (data.ten_nv || '').toUpperCase(),
            'detail-employee-code': data.ma_nv,
            'detail-birth-date': data.ngay_sinh || '15/05/1998',
            'detail-contract-type': data.loai_hd,
            'detail-start-date': data.ngay_bd,
            'detail-end-date': data.ngay_kt,
            'detail-salary-display': data.luong_hien_thi || data.tong_luong || data.muc_luong,
            'detail-position': data.chuc_vu,
            'detail-trang-thai': data.trang_thai || 'CO HIEU LUC',
            'detail-sign-a': data.dai_dien_ben_a || 'Truong Thi Hong Hoa',
            'detail-sign-b': data.ten_nv
        };

        Object.entries(elements).forEach(([id, value]) => {
            const element = document.getElementById(id);
            if (element) {
                element.textContent = value || '';
            }
        });
    }

    function openDetailFromButton(button) {
        populateDetailModal({
            ma_hd: button.dataset.contractId,
            so_hd_hien_thi: button.dataset.contractId,
            ngay_ky: button.dataset.startDate,
            ten_nv: button.dataset.employeeName,
            ma_nv: button.dataset.employeeId,
            loai_hd: button.dataset.contractType,
            ngay_bd: button.dataset.startDate,
            ngay_kt: button.dataset.endDate,
            chuc_vu: button.dataset.position,
            luong_hien_thi: button.dataset.salary,
            trang_thai: 'CO HIEU LUC'
        });

        if (detailPopup) {
            detailPopup.style.display = 'flex';
        }
    }

    detailButtons.forEach((button) => {
        button.addEventListener('click', function () {
            openDetailFromButton(this);
        });
    });

    if (detailCloseBtn) {
        detailCloseBtn.addEventListener('click', function () {
            detailPopup.style.display = 'none';
        });
    }

    if (detailPopup) {
        detailPopup.addEventListener('click', function (event) {
            if (event.target === detailPopup) {
                detailPopup.style.display = 'none';
            }
        });
    }

    deleteButtons.forEach((button) => {
        button.addEventListener('click', function () {
            if (deletePopup) {
                deletePopup.dataset.deleteId = this.dataset.deleteId || '';
                deletePopup.style.display = 'flex';
            }
        });
    });

    if (deleteNoBtn) {
        deleteNoBtn.addEventListener('click', function () {
            deletePopup.style.display = 'none';
        });
    }

    if (deleteYesBtn) {
        deleteYesBtn.addEventListener('click', function () {
            alert('Thanh cong: Da xoa hop dong thanh cong');
            deletePopup.style.display = 'none';
        });
    }
});
