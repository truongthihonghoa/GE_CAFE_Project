from django.shortcuts import render, redirect
from django.contrib.auth import logout
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from .models import TaiKhoan
from .serializers import TaiKhoanSerializer


def _account_rows():
    return [
        {
            'stt': 1,
            'ho_ten': 'Nguyễn Văn An',
            'ten_dang_nhap': 'NGUYENVANAN',
            'quyen': 'Nhân viên',
            'trang_thai': 'Đang hoạt động',
            'trang_thai_key': 'active',
        },
        {
            'stt': 2,
            'ho_ten': 'Lê Thị Hồng Châu',
            'ten_dang_nhap': 'LETHIHONGCHAU',
            'quyen': 'Nhân viên',
            'trang_thai': 'Đang hoạt động',
            'trang_thai_key': 'active',
        },
        {
            'stt': 3,
            'ho_ten': 'Trần Tuấn Anh',
            'ten_dang_nhap': 'TRANTUANANH',
            'quyen': 'Nhân viên',
            'trang_thai': 'Đang hoạt động',
            'trang_thai_key': 'active',
        },
        {
            'stt': 4,
            'ho_ten': 'Trình Phúc Lâm',
            'ten_dang_nhap': 'TRINHPHUCLAM',
            'quyen': 'Nhân viên',
            'trang_thai': 'Ngừng hoạt động',
            'trang_thai_key': 'inactive',
        },
        {
            'stt': 5,
            'ho_ten': 'Nguyễn Thị Anh',
            'ten_dang_nhap': 'NGUYENTHIANH',
            'quyen': 'Quản lý',
            'trang_thai': 'Đang hoạt động',
            'trang_thai_key': 'active',
        },
    ]


def _admin_accounts():
    """Return only admin accounts - separate management list"""
    return [
        {
            'stt': 1,
            'ho_ten': 'Lê Minh Tuấn',
            'ten_dang_nhap': 'LEMINHTUAN',
            'quyen': 'Admin',
            'trang_thai': 'Đang hoạt động',
            'trang_thai_key': 'active',
        },
        {
            'stt': 2,
            'ho_ten': 'Trần Thị Mai',
            'ten_dang_nhap': 'TRANTHIMAI',
            'quyen': 'Admin',
            'trang_thai': 'Đang hoạt động',
            'trang_thai_key': 'active',
        },
        {
            'stt': 3,
            'ho_ten': 'Nguyễn Thị Anh',
            'ten_dang_nhap': 'NGUYENTHIANH',
            'quyen': 'Quản lý',
            'trang_thai': 'Đang hoạt động',
            'trang_thai_key': 'active',
        },
    ]


def _employee_accounts():
    """Return only employee accounts - separate management list"""
    return [
        {
            'stt': 1,
            'ho_ten': 'Nguyễn Văn An',
            'ten_dang_nhap': 'NGUYENVANAN',
            'quyen': 'Nhân viên',
            'trang_thai': 'Đang hoạt động',
            'trang_thai_key': 'active',
        },
        {
            'stt': 2,
            'ho_ten': 'Lê Thị Hồng Châu',
            'ten_dang_nhap': 'LETHIHONGCHAU',
            'quyen': 'Nhân viên',
            'trang_thai': 'Đang hoạt động',
            'trang_thai_key': 'active',
        },
        {
            'stt': 3,
            'ho_ten': 'Trần Tuấn Anh',
            'ten_dang_nhap': 'TRANTUANANH',
            'quyen': 'Nhân viên',
            'trang_thai': 'Đang hoạt động',
            'trang_thai_key': 'active',
        },
        {
            'stt': 4,
            'ho_ten': 'Trình Phúc Lâm',
            'ten_dang_nhap': 'TRINHPHUCLAM',
            'quyen': 'Nhân viên',
            'trang_thai': 'Ngừng hoạt động',
            'trang_thai_key': 'inactive',
        },
    ]


def login_view(request):
    """
    Renders the login page. The actual login logic is now handled by frontend JavaScript
    for demonstration purposes.
    """
    return render(request, 'accounts/login.html')

# @login_required(login_url='/accounts/login/') # Kept commented out for easy frontend testing
def dashboard_view(request):
    return render(request, 'accounts/dashboard.html')


def account_employee_list_view(request):
    return render(
        request,
        'accounts/account_employee_list.html',
        {
            'account_rows': _employee_accounts(),
        },
    )


def account_admin_list_view(request):
    return render(
        request,
        'accounts/account_admin_list.html',
        {
            'account_rows': _admin_accounts(),
        },
    )


def logout_view(request):
    """
    Handles user logout
    """
    logout(request)
    return redirect('login')

class TaiKhoanViewSet(viewsets.ReadOnlyModelViewSet):
    serializer_class = TaiKhoanSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        user = self.request.user

        if user.is_superuser:
            # Chủ: xem tất cả tài khoản
            return TaiKhoan.objects.all()
        elif user.is_staff:
            # Quản lý: xem tài khoản trong chi nhánh mình quản lý
            chi_nhanh_nv = getattr(user.taikhoan.ma_nv, 'ma_chi_nhanh', None)
            return TaiKhoan.objects.filter(ma_nv__ma_chi_nhanh=chi_nhanh_nv)
        else:
            # Nhân viên: chỉ xem chính mình
            return TaiKhoan.objects.filter(user=user)
