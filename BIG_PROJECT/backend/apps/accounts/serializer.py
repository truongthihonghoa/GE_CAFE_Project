from rest_framework import serializers
from django.contrib.auth.models import User
from .models import TaiKhoan
from apps.employees.models import NhanVien
from django.db import IntegrityError

class TaiKhoanSerializer(serializers.ModelSerializer):
    ten_dang_nhap = serializers.CharField(source='user.username', read_only=True)
    ho_ten = serializers.CharField(source='ma_nv.ho_ten', read_only=True)
    vai_tro = serializers.CharField(read_only=True)
    trang_thai = serializers.CharField(read_only=True)
    
    # Write-only fields for creation/update
    username = serializers.CharField(write_only=True, required=False)
    password = serializers.CharField(write_only=True, required=False)
    ma_nv_id = serializers.PrimaryKeyRelatedField(
        source='ma_nv',
        queryset=NhanVien.objects.all(),
        write_only=True,
        required=False
    )
    is_staff = serializers.BooleanField(write_only=True, required=False)
    is_active = serializers.BooleanField(write_only=True, required=False)

    class Meta:
        model = TaiKhoan
        fields = [
            'id', 'ten_dang_nhap', 'ho_ten', 'vai_tro', 'trang_thai',
            'username', 'password', 'ma_nv_id', 'is_staff', 'is_active'
        ]

    def create(self, validated_data):
        username = validated_data.pop('username', None)
        password = validated_data.pop('password', None)
        is_staff = validated_data.pop('is_staff', False)
        is_active = validated_data.pop('is_active', True)
        ma_nv = validated_data.pop('ma_nv', None)

        if not username:
            raise serializers.ValidationError("Thiếu tên đăng nhập (username)")
        if not password:
            raise serializers.ValidationError("Thiếu mật khẩu (password)")
        if not ma_nv:
            raise serializers.ValidationError("Chưa chọn nhân viên (ma_nv_id)")

        if User.objects.filter(username=username).exists():
            raise serializers.ValidationError("Tên đăng nhập này đã tồn tại")

        if TaiKhoan.objects.filter(ma_nv=ma_nv).exists():
            raise serializers.ValidationError("Nhân viên này đã được cấp tài khoản")

        try:
            # Tạo Django User
            user = User.objects.create_user(
                username=username,
                password=password,
                is_staff=is_staff,
                is_active=is_active
            )

            # Tạo TaiKhoan liên kết
            taikhoan = TaiKhoan.objects.create(user=user, ma_nv=ma_nv)
            return taikhoan
        except IntegrityError as e:
            raise serializers.ValidationError(f"Lỗi cơ sở dữ liệu: {str(e)}")
        except Exception as e:
            raise serializers.ValidationError(f"Lỗi không xác định: {str(e)}")

    def update(self, instance, validated_data):
        user = instance.user
        
        if 'is_staff' in validated_data:
            user.is_staff = validated_data.pop('is_staff')
        
        if 'is_active' in validated_data:
            user.is_active = validated_data.pop('is_active')
            
        if 'password' in validated_data:
            user.set_password(validated_data.pop('password'))
            
        user.save()
        return super().update(instance, validated_data)