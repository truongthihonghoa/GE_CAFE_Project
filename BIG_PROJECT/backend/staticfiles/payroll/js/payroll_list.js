/* Biến toàn cục để quản lý danh sách nhân viên đang được tính lương */
let selectedEmployees = [];
let currentProcessingIndex = 0;
let editingRow = null; // Biến lưu trữ hàng đang được chỉnh sửa

/**
 * Logic Lọc bảng lương theo Tab
 */
document.addEventListener('DOMContentLoaded', function() {
    const filterButtons = document.querySelectorAll('.btn-filter');
    filterButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            filterButtons.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            
            const filterText = this.innerText.trim();
            let statusKey = 'cho-duyet';
            if (filterText === 'Đã duyệt') statusKey = 'da-duyet';
            if (filterText === 'Đã từ chối') statusKey = 'da-tu-choi';
            
            applyFilter(statusKey);
        });
    });

    // Phê duyệt / Từ chối trực tiếp trên bảng
    document.querySelector('.ge-main-table').addEventListener('click', function(e) {
        const btn = e.target.closest('.btn-action-ge');
        if (!btn) return;

        const row = btn.closest('tr');
        if (btn.classList.contains('btn-approve-ge')) {
            updateRowStatus(row, 'da-duyet', 'Đã duyệt', 'fa-circle-check');
        } else if (btn.classList.contains('btn-reject-ge')) {
            updateRowStatus(row, 'da-tu-choi', 'Đã từ chối', 'fa-circle-xmark');
        }
    });

    // Mặc định lọc "Chờ duyệt" khi tải trang
    applyFilter('cho-duyet');
});

// --- LOGIC XÓA BẢNG LƯƠNG ---
function openDeletePayrollPopup(payrollId) {
    const popup = document.getElementById('confirm-delete-payroll-popup');
    const deleteBtn = document.getElementById('confirm-delete-payroll-btn');
    const infoDisplay = document.getElementById('delete-payroll-info');

    if (popup && deleteBtn && infoDisplay) {
        const row = document.querySelector(`tr[data-ma-luong="${payrollId}"]`);
        const empName = row ? row.cells[2].innerText : "";

        infoDisplay.innerText = `${payrollId} - ${empName}`; 
        deleteBtn.setAttribute('data-payroll-id', payrollId);
        popup.style.display = 'flex';
    }
}

function closeDeletePayrollPopup() {
    const popup = document.getElementById('confirm-delete-payroll-popup');
    if (popup) popup.style.display = 'none';
}

function confirmDeletePayroll() {
    const deleteBtn = document.getElementById('confirm-delete-payroll-btn');
    const payrollIdToDelete = deleteBtn.getAttribute('data-payroll-id');

    if (payrollIdToDelete) {
        const rowToDelete = document.querySelector(`tr[data-ma-luong="${payrollIdToDelete}"]`);
        if (rowToDelete) {
            rowToDelete.remove();
            
            // Cập nhật lại số STT
            const tbody = document.querySelector('.ge-main-table tbody');
            Array.from(tbody.rows).forEach((row, index) => {
                if (row.cells.length > 1) row.cells[0].innerText = index + 1;
            });

            if (tbody.rows.length === 0) {
                tbody.innerHTML = '<tr><td colspan="12">Không có dữ liệu bảng lương.</td></tr>';
            }
            showPayrollToast("Xóa bảng lương thành công");
        }
    }
    closeDeletePayrollPopup();
}

// --- LOGIC XÁC NHẬN HỦY ---
function openCancelConfirmPopup() {
    const popup = document.getElementById('confirm-cancel-payroll-popup');
    if (popup) popup.style.display = 'flex';
}

function closeCancelConfirmPopup() {
    const popup = document.getElementById('confirm-cancel-payroll-popup');
    if (popup) popup.style.display = 'none';
}

function confirmCancel() {
    closeCancelConfirmPopup(); // Đóng popup xác nhận
    closeDetailModal();        // Đóng modal tính lương chính
}


/**
 * Mở Modal Chỉnh sửa và đổ dữ liệu từ hàng vào Form
 */
function openEditPayrollModal(btn) {
    const row = btn.closest('tr');
    editingRow = row; // Lưu lại hàng đang chọn để sửa
    const maNV = row.getAttribute('data-ma-nv') || "";

    // Lấy dữ liệu từ các ô (Cell) của hàng
    const maLuong = row.cells[1].innerText;
    const tenNV = row.cells[2].innerText;
    const kyLuong = row.cells[3].innerText;
    const lcb = row.cells[4].innerText;
    const ltg = row.cells[5].innerText;
    const sgl = row.cells[6].innerText;
    const thuong = row.cells[7].innerText;
    const phat = row.cells[8].innerText;

    // Đổ dữ liệu vào Modal chi tiết
    document.getElementById('display-ma-luong').innerText = `Mã lương: ${maLuong}`;
    document.getElementById('display-nv-info').innerText = `Mã NV: ${maNV} - ${tenNV}`;
    document.getElementById('display-month-info').innerText = `Tháng: ${kyLuong}`;

    document.getElementById('detail-lcb').value = lcb.trim();
    document.getElementById('detail-ltg').value = ltg.trim();
    document.getElementById('detail-sgl').value = sgl;
    // Làm sạch dữ liệu trước khi đưa vào ô input number
    document.getElementById('detail-thuong').value = thuong.replace(/[^0-9]/g, '');
    document.getElementById('detail-phat').value = phat.replace(/[^0-9]/g, '');

    // Đổi tiêu đề Modal để người dùng biết là đang sửa
    document.querySelector('#salaryDetailModal .detail-title').innerText = "Chỉnh sửa lương";
    recalculateTotal();

    document.getElementById('salaryDetailModal').style.display = 'flex';
}

function applyFilter(statusKey) {
    const rows = document.querySelectorAll('.ge-main-table tbody tr');
    const actionHeader = document.querySelector('.ge-main-table thead th:last-child');

    // Xác định xem có nên ẩn cột hành động hay không (Ẩn ở tab Đã duyệt và Đã từ chối)
    const shouldHideAction = (statusKey === 'da-duyet' || statusKey === 'da-tu-choi');

    // Ẩn/Hiện tiêu đề cột "Hành động"
    if (actionHeader) {
        actionHeader.style.display = shouldHideAction ? 'none' : '';
    }
    
    // Ẩn/Hiện khu vực nút Xuất (Chỉ hiện khi statusKey là 'da-duyet')
    const exportContainer = document.getElementById('export-approved-container');
    if (exportContainer) {
        exportContainer.style.display = (statusKey === 'da-duyet') ? 'flex' : 'none';
    }

    rows.forEach(row => {
        if (row.cells.length === 1) return; // Bỏ qua dòng "Không có dữ liệu"
        const isMatch = row.getAttribute('data-status') === statusKey;
        row.style.display = isMatch ? '' : 'none';

        // Ẩn/Hiện ô nội dung hành động tương ứng trong hàng
        const actionCell = row.cells[row.cells.length - 1];
        if (actionCell) {
            actionCell.style.display = shouldHideAction ? 'none' : '';
        }
    });
}

/**
 * Xử lý hành động xuất file
 */
function handleExportApproved() {
    const format = document.getElementById('export-format-select').value;
    showPayrollToast(`Xuất bảng lương thành công ${format.toUpperCase()}`);
}

function updateRowStatus(row, statusKey, statusText, iconClass) {
    row.setAttribute('data-status', statusKey);
    const badge = row.querySelector('.status-badge');
    badge.className = `status-badge ${statusKey}`;
    badge.innerHTML = `<i class="fas ${iconClass}"></i> ${statusText}`;

    // Xóa các nút Duyệt/Từ chối sau khi đã xử lý
    const approveBtn = row.querySelector('.btn-approve-ge');
    const rejectBtn = row.querySelector('.btn-reject-ge');
    if (approveBtn) approveBtn.remove();
    if (rejectBtn) rejectBtn.remove();

    // Ẩn hàng đó khỏi view hiện tại (vì nó đã chuyển trạng thái)
    row.style.display = 'none';
    let message = `Đã chuyển trạng thái: ${statusText}`;
    if (statusKey === 'da-duyet') {
        message = "Duyệt bảng lương thành công";
    } else if (statusKey === 'da-tu-choi') {
        message = "Từ chối bảng lương thành công";
    }

    showPayrollToast(message);
}

/**
 * Điều khiển đóng/mở Modal chính
 */
function openSalaryModal() {
    const modal = document.getElementById('salaryModal');
    if (modal) modal.style.display = 'flex';
}

function closeSalaryModal() {
    document.getElementById('salaryModal').style.display = 'none';
}

/**
 * Xử lý khi chọn tháng ở Modal 1
 */
function handlePeriodChange() {
    const month = document.getElementById('salaryMonth').value;
    const year = document.getElementById('salaryYear').value;
    const btnConfirm = document.getElementById('btnConfirmCalculate');
    const warning = document.getElementById('processedWarning');

    if (month && year) {
        btnConfirm.disabled = false;
        // Giả lập danh sách nhiều nhân viên
        const employees = [
            { id: 'NV00001', name: 'Nguyễn Văn An' },
            { id: 'NV00002', name: 'Trần Thị Bình' },
            { id: 'NV00003', name: 'Lê Quang Cường' },
            { id: 'NV00004', name: 'Phạm Minh Đức' },
            { id: 'NV00005', name: 'Hoàng Thị Thanh' }
        ];

        const tbody = document.getElementById('employeeTableBody');
        tbody.innerHTML = employees.map(emp => `
            <tr>
                <td><input type="checkbox"></td>
                <td>${emp.id}</td>
                <td>${emp.name}</td>
            </tr>
        `).join('');
    } else {
        btnConfirm.disabled = true;
    }
}

function toggleSelectAll(source) {
    const checkboxes = document.querySelectorAll('#employeeTableBody input[type="checkbox"]');
    checkboxes.forEach(cb => cb.checked = source.checked);
}

/**
 * Lưu thông tin lương chi tiết và cập nhật bảng danh sách
 */
function saveSalaryDetail() {
    // 1. Lấy dữ liệu từ Form (sử dụng các ID đã định nghĩa trong HTML)
    const maLuong = document.getElementById('display-ma-luong').innerText.replace('Mã lương: ', '').trim();
    const nvInfo = document.getElementById('display-nv-info').innerText;
    const kyLuong = document.getElementById('display-month-info').innerText.replace('Tháng: ', '').trim();
    const infoParts = nvInfo.split(' - ');

    // Tách Mã NV và Tên NV an toàn (Tránh crash nếu không có dấu " - ")
    const maNV = infoParts[0].replace('Mã NV: ', '').replace('Nhân viên: ', '').trim();
    const hoTen = infoParts.length > 1 ? infoParts[1].trim() : infoParts[0].replace('Nhân viên: ', '').trim();

    const lcb = document.getElementById('detail-lcb').value || "0";
    const ltg = document.getElementById('detail-ltg').value || "0";
    const sgl = document.getElementById('detail-sgl').value;
    const thuong = document.getElementById('detail-thuong').value || "0";
    const phat = document.getElementById('detail-phat').value || "0";
    const tongLuong = document.getElementById('detail-tong-luong').innerText;

    // NẾU ĐANG TRONG CHẾ ĐỘ CHỈNH SỬA
    if (editingRow) {
        editingRow.cells[4].innerText = lcb;
        editingRow.cells[5].innerText = ltg;
        editingRow.cells[6].innerText = sgl;
        editingRow.cells[7].innerText = thuong;
        editingRow.cells[8].innerText = phat;
        editingRow.cells[9].innerHTML = `<strong>${tongLuong}</strong>`;

        showPayrollToast("Cập nhật bảng lương thành công");
        closeDetailModal();
        
        editingRow = null; // Reset biến sau khi sửa xong
        return;
    }

    // 2. Tạo hàng mới cho bảng (.ge-main-table tbody)
    const tbody = document.querySelector('.ge-main-table tbody');

    // Nếu đang hiển thị "Không có dữ liệu", hãy xóa dòng đó đi
    if (tbody.rows.length === 1 && tbody.rows[0].cells.length === 1) {
        tbody.innerHTML = '';
    }

    const newRow = document.createElement('tr');
    newRow.className = 'new-row-highlight';
    newRow.setAttribute('data-ma-luong', maLuong);
    newRow.setAttribute('data-ma-nv', maNV);
    newRow.setAttribute('data-status', 'cho-duyet'); // Để bộ lọc nhận diện

    // Cấu trúc 12 cột: STT, Mã lương, Tên nhân viên, Kỳ lương, Lương cơ bản, Lương theo giờ, Số giờ làm, Thưởng, Phạt, Tổng lương, Trạng thái, Hành động
    newRow.innerHTML = `
        <td>1</td>
        <td>${maLuong}</td>
        <td>${hoTen}</td>
        <td>${kyLuong}</td>
        <td>${lcb}</td>
        <td>${ltg}</td>
        <td>${sgl}</td>
        <td>${thuong}</td>
        <td>${phat}</td>
        <td><strong>${tongLuong}</strong></td>
        <td><div class="status-badge cho-duyet"><button class="btn-action-ge btn-approve-ge" title="Duyệt"><i class="fas fa-check"></i></button><button class="btn-action-ge btn-reject-ge" title="Từ chối"><i class="fas fa-times"></i></button></div></td>
        <td>
            <div class="action-buttons">
                <button type="button" class="btn-action-ge btn-edit-ge" title="Sửa" onclick="openEditPayrollModal(this)"><i class="fas fa-pen"></i></button>
                <button type="button" class="btn-action-ge btn-delete-ge" title="Xóa" onclick="openDeletePayrollPopup('${maLuong}')"><i class="fas fa-trash-alt"></i></button>
            </div>
        </td>
    `;

    // Chèn vào đầu bảng
    tbody.insertBefore(newRow, tbody.firstChild);

    // Cập nhật lại số STT
    Array.from(tbody.rows).forEach((row, index) => {
        row.cells[0].innerText = index + 1;
    });

    // 3. Đóng Modal và thông báo
    
    // Kiểm tra xem còn nhân viên nào trong danh sách đã chọn chưa được tính không
    currentProcessingIndex++;
    if (currentProcessingIndex < selectedEmployees.length) {
        // Nếu còn, hiển thị thông tin của nhân viên tiếp theo
        showEmployeeDetail(currentProcessingIndex);
    } else {
        // Nếu đã hết, đóng modal và hiện toast
        closeDetailModal();
        showPayrollToast("Cập nhật bảng lương thành công");
    }
}

/**
 * Chuyển từ Modal 1 sang Modal Chi tiết
 */
function processCalculation() {
    const month = document.getElementById('salaryMonth').value;
    const year = document.getElementById('salaryYear').value;
    if (!month || !year) return;
    
    // 1. Lấy tất cả các checkbox được chọn
    const checkedBoxes = document.querySelectorAll('#employeeTableBody input[type="checkbox"]:checked');
    
    if (checkedBoxes.length === 0) {
        alert("Vui lòng chọn ít nhất một nhân viên để tính lương.");
        return;
    }

    // 2. Lưu danh sách nhân viên đã chọn vào biến toàn cục
    selectedEmployees = Array.from(checkedBoxes).map(cb => {
        const row = cb.closest('tr');
        return {
            id: row.cells[1].innerText,
            name: row.cells[2].innerText
        };
    });

    // 3. Bắt đầu xử lý từ nhân viên đầu tiên
    currentProcessingIndex = 0;
    closeSalaryModal();
    showEmployeeDetail(currentProcessingIndex);
}

/**
 * Hàm bổ trợ để hiển thị thông tin nhân viên cụ thể lên Modal chi tiết
 */
function showEmployeeDetail(index) {
    const emp = selectedEmployees[index];
    const month = document.getElementById('salaryMonth').value;
    const year = document.getElementById('salaryYear').value;
    
    // 1. Tạo mã lương định dạng ML0001 dựa trên số lượng hàng hiện có trong bảng + index xử lý
    const currentCount = document.querySelectorAll('.ge-main-table tbody tr').length + 1 + index;
    const maLuong = `ML${currentCount.toString().padStart(4, '0')}`;
    
    const displayMonth = `${month}/${year}`;

    // 3. Đổ dữ liệu vào modal chi tiết
    document.getElementById('display-ma-luong').innerText = `Mã lương: ${maLuong}`;
    document.getElementById('display-nv-info').innerText = `Mã NV: ${emp.id} - ${emp.name}`;
    document.getElementById('display-month-info').innerText = `Tháng: ${displayMonth}`;

    // Đổ dữ liệu mặc định (hoặc có thể lấy từ emp object nếu có)
    document.getElementById('detail-lcb').value = "5.000.000"; 
    document.getElementById('detail-ltg').value = "50.000";
    document.getElementById('detail-sgl').value = "160";
    document.getElementById('detail-scl').value = "22"; // Gán thêm số ca làm nếu cần
    
    // Reset các ô nhập liệu thưởng/phạt
    document.getElementById('detail-thuong').value = "0";
    document.getElementById('detail-phat').value = "0";
    
    recalculateTotal();

    // Mở modal chi tiết
    document.getElementById('salaryDetailModal').style.display = 'flex';
}

/**
 * Hiển thị thông báo Toast
 */
function showPayrollToast(message) {
    const toast = document.getElementById('toast-notification');
    const toastContent = toast ? toast.querySelector('.toast-content') : null;

    if (toast) {
        toastContent.innerText = message;

        // Reset trạng thái và hiển thị trượt từ phải vào
        toast.classList.remove('show', 'fade-out');
        void toast.offsetWidth; // Force reflow để trigger animation
        toast.classList.add('show');

        // Sau 3 giây thì mờ dần và ẩn đi
        setTimeout(() => {
            toast.classList.add('fade-out');
            setTimeout(() => {
                toast.classList.remove('show');
            }, 600); // Đợi hiệu ứng mờ kết thúc rồi mới ẩn hẳn
        }, 3000);
    }
}

function recalculateTotal() {
    // Hàm giúp chuyển đổi chuỗi tiền tệ "1.000.000" hoặc "1,000,000" thành số
    const parseMoney = (id) => parseInt(document.getElementById(id).value.replace(/[^0-9]/g, '')) || 0;

    const lcb = parseMoney('detail-lcb');
    const ltg = parseMoney('detail-ltg');
    const sgl = parseFloat(document.getElementById('detail-sgl').value) || 0;
    const thuong = parseMoney('detail-thuong');
    const phat = parseMoney('detail-phat');

    // Công thức: (Lương giờ * Số giờ) + Lương cơ bản + Thưởng - Phạt
    const total = (ltg * sgl) + lcb + thuong - phat;
    document.getElementById('detail-tong-luong').innerText = total.toLocaleString('vi-VN') + " VNĐ";
}

function closeDetailModal() {
    // Reset lại trạng thái khi đóng modal
    editingRow = null;
    document.querySelector('#salaryDetailModal .detail-title').innerText = "Tính lương";
    document.getElementById('salaryDetailModal').style.display = 'none';
}