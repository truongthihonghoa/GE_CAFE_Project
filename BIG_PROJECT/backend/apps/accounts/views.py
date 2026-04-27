from django.shortcuts import render, redirect
from django.contrib.auth import logout
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated, AllowAny
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
from rest_framework.response import Response
from rest_framework import status


class TaiKhoanViewSet(viewsets.ModelViewSet):
    queryset = TaiKhoan.objects.all()
    serializer_class = TaiKhoanSerializer

    # ÉP MỞ QUYỀN: Không dùng IsAuthenticated lúc này
    permission_classes = [AllowAny]
    authentication_classes = [] # Tạm thời bỏ qua xác thực để thông luồng POST/PUT
    # Cho phép test thoải mái không lo bị chặn 405/401


    def get_queryset(self):
        # Trả về tất cả để App Android có thể hiển thị sau khi CRUD
        return TaiKhoan.objects.all()

    # --- CRUD CỦA BẠN ĐÂY ---

    def create(self, request, *args, **kwargs):
        """Xử lý lệnh POST từ App Android"""
        serializer = self.get_serializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def update(self, request, *args, **kwargs):
        """Xử lý lệnh PUT từ App Android"""
        instance = self.get_object()
        # partial=True cực kỳ quan trọng, giúp bạn sửa lẻ từng trường
        serializer = self.get_serializer(instance, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def destroy(self, request, *args, **kwargs):
        """Xử lý lệnh DELETE từ App Android"""
        instance = self.get_object()
        instance.delete()
        return Response({"detail": "Xóa thành công!"}, status=status.HTTP_204_NO_CONTENT)
