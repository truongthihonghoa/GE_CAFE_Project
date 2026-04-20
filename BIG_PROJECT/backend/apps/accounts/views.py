from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from .models import TaiKhoan
from .serializer import TaiKhoanSerializer

from django.shortcuts import render

def login_view(request):
    return render(request, 'accounts/login.html')


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