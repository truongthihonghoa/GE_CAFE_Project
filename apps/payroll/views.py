from django.shortcuts import render


def _payroll_rows():
    return [
        {
            'ma_luong': 'ML0001',
            'ma_nv': 'NV001',
            'ten_nv': 'Nguyễn Văn An',
            'thang': '01/2026',
            'luong_co_ban': '8,000,000',
            'luong_gio': '50,000',
            'so_gio': 160,
            'thuong': '500,000',
            'phat': '0',
            'thuc_linh': '16,500,000',
            'branch_id': 1,
            'trang_thai': 'Chờ duyệt',
            'trang_thai_key': 'cho-duyet',
        },
        {
            'ma_luong': 'ML0002',
            'ma_nv': 'NV002',
            'ten_nv': 'Trần Thị B',
            'thang': '01/2026',
            'luong_co_ban': '7,500,000',
            'luong_gio': '45,000',
            'so_gio': 150,
            'thuong': '300,000',
            'phat': '100,000',
            'thuc_linh': '14,450,000',
            'branch_id': 2,
            'trang_thai': 'Chờ duyệt',
            'trang_thai_key': 'cho-duyet',
        },
        {
            'ma_luong': 'ML0003',
            'ma_nv': 'NV003',
            'ten_nv': 'Lê Văn C',
            'thang': '01/2026',
            'luong_co_ban': '9,000,000',
            'luong_gio': '55,000',
            'so_gio': 170,
            'thuong': '800,000',
            'phat': '0',
            'thuc_linh': '19,150,000',
            'branch_id': 1,
            'trang_thai': 'Đã duyệt',
            'trang_thai_key': 'da-duyet',
        },
        {
            'ma_luong': 'ML0004',
            'ma_nv': 'NV004',
            'ten_nv': 'Phạm Thị D',
            'thang': '01/2026',
            'luong_co_ban': '7,000,000',
            'luong_gio': '40,000',
            'so_gio': 155,
            'thuong': '200,000',
            'phat': '50,000',
            'thuc_linh': '13,450,000',
            'branch_id': 3,
            'trang_thai': 'Chờ duyệt',
            'trang_thai_key': 'cho-duyet',
        },
        {
            'ma_luong': 'ML0005',
            'ma_nv': 'NV005',
            'ten_nv': 'Hoàng Văn E',
            'thang': '01/2026',
            'luong_co_ban': '8,500,000',
            'luong_gio': '52,000',
            'so_gio': 165,
            'thuong': '600,000',
            'phat': '0',
            'thuc_linh': '17,680,000',
            'branch_id': 2,
            'trang_thai': 'Đã duyệt',
            'trang_thai_key': 'da-duyet',
        },
    ]


def _employees():
    return [
        {'ma_nv': 'NV001', 'ten_nv': 'Nguyễn Văn An'},
        {'ma_nv': 'NV002', 'ten_nv': 'Trần Thị B'},
        {'ma_nv': 'NV003', 'ten_nv': 'Lê Văn C'},
        {'ma_nv': 'NV004', 'ten_nv': 'Phạm Thị D'},
        {'ma_nv': 'NV005', 'ten_nv': 'Hoàng Văn E'},
    ]


def _branches():
    return [
        {'id': 1, 'ten_chi_nhanh': 'Chi nhánh Quận 1'},
        {'id': 2, 'ten_chi_nhanh': 'Chi nhánh Quận 3'},
        {'id': 3, 'ten_chi_nhanh': 'Chi nhánh Thủ Đức'},
    ]


def payroll_list_view(request):
    payroll_rows = _payroll_rows()
    
    branches = _branches()
    branch_id = request.GET.get('branch')
    month = request.GET.get('month')
    year = request.GET.get('year')

    # Nếu không có branch_id trong URL, mặc định lấy cái đầu tiên
    if not branch_id and branches:
        branch_id = str(branches[0]['id'])

    # 1. Lọc theo chi nhánh
    if branch_id:
        payroll_rows = [row for row in payroll_rows if str(row.get('branch_id')) == branch_id]

    # 2. Lọc theo Kỳ lương (Tháng và Năm)
    if month and year:
        # Định dạng trong dữ liệu mẫu là "MM/YYYY" (ví dụ: "01/2026")
        selected_period = f"{month}/{year}"
        payroll_rows = [row for row in payroll_rows if row.get('thang') == selected_period]

    # Lọc theo từ khóa tìm kiếm
    search_q = request.GET.get('q')
    if search_q:
        search_q = search_q.lower()
        payroll_rows = [
            row for row in payroll_rows 
            if search_q in row['ten_nv'].lower() or search_q in row['ma_nv'].lower()
        ]

    return render(
        request,
        'payroll/payroll_list.html',
        {
            'payroll_rows': payroll_rows,
            'employees': _employees(),
            'branches': _branches(),
        },
    )


def payroll_export_view(request):
    return render(
        request,
        'payroll/payroll_export.html',
        {
            'payroll_rows': _payroll_rows(),
        },
    )
