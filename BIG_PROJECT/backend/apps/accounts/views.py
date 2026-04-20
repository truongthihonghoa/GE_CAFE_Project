from rest_framework import viewsets, status, permissions
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token
from .models import TaiKhoan
from .serializer import TaiKhoanSerializer
from django.contrib.auth.models import User
from django.shortcuts import render

def login_view(request):
    return render(request, 'accounts/login.html')

class CustomObtainAuthToken(ObtainAuthToken):
    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data,
                                           context={'request': request})
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']
        token, created = Token.objects.get_or_create(user=user)
        
        # Xác định vai trò
        role = "STAFF"
        if user.is_superuser:
            role = "ADMIN"
        elif user.is_staff:
            role = "MANAGER"
            
        # Lấy tên hiển thị
        full_name = user.get_full_name()
        if not full_name:
            try:
                full_name = user.taikhoan.ma_nv.ho_ten
            except:
                full_name = user.username

        return Response({
            'token': token.key,
            'user_id': user.pk,
            'username': user.username,
            'full_name': full_name,
            'role': role
        })

class TaiKhoanViewSet(viewsets.ModelViewSet):
    serializer_class = TaiKhoanSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        user = self.request.user

        if user.is_superuser:
            return TaiKhoan.objects.all()
        elif user.is_staff:
            try:
                chi_nhanh_nv = user.taikhoan.ma_nv.ma_chi_nhanh
                return TaiKhoan.objects.filter(ma_nv__ma_chi_nhanh=chi_nhanh_nv)
            except:
                return TaiKhoan.objects.none()
        else:
            return TaiKhoan.objects.filter(user=user)

    def create(self, request, *args, **kwargs):
        if not request.user.is_staff:
            return Response({"error": "Không có quyền tạo tài khoản"}, status=status.HTTP_403_FORBIDDEN)
        return super().create(request, *args, **kwargs)

    @action(detail=True, methods=['post'], url_path='change-password')
    def change_password(self, request, pk=None):
        taikhoan = self.get_object()
        new_password = request.data.get('new_password')
        
        if not new_password:
            return Response({"error": "Mật khẩu mới không được để trống"}, status=status.HTTP_400_BAD_REQUEST)
            
        user = taikhoan.user
        user.set_password(new_password)
        user.save()
        
        return Response({"message": "Đổi mật khẩu thành công"})