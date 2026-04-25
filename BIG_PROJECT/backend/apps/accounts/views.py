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


from rest_framework.permissions import IsAuthenticated
from rest_framework_simplejwt.authentication import JWTAuthentication


class TaiKhoanViewSet(viewsets.ReadOnlyModelViewSet):
    serializer_class = TaiKhoanSerializer
    # Chỉ cho phép người đã đăng nhập (có Token) mới được gọi API
    permission_classes = [IsAuthenticated]
    authentication_classes = [JWTAuthentication]

    def get_queryset(self):
        user = self.request.user

        # Kiểm tra quyền Staff trực tiếp từ Token của User
        if user.is_superuser:
            return TaiKhoan.objects.all()

        if user.is_staff:
            # Lấy chi nhánh của quản lý để lọc
            # Đảm bảo model TaiKhoan có quan hệ ngược hoặc truy vấn đúng
            try:
                ma_chi_nhanh = user.taikhoan.ma_nv.ma_chi_nhanh
                return TaiKhoan.objects.filter(ma_nv__ma_chi_nhanh=ma_chi_nhanh)
            except AttributeError:
                return TaiKhoan.objects.none()  # Trả về trống nếu ko tìm thấy chi nhánh

        # Nếu là nhân viên thường, chỉ thấy chính mình
        return TaiKhoan.objects.filter(user=user)