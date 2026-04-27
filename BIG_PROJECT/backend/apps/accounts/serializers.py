from rest_framework import serializers
from django.contrib.auth.models import User
from .models import TaiKhoan


class TaiKhoanSerializer(serializers.ModelSerializer):
    # =========================
    # CÁC TRƯỜNG CHỈ HIỂN THỊ
    # =========================

    ten_dang_nhap = serializers.CharField(
        source='user.username',
        read_only=True
    )

    ho_ten = serializers.CharField(
        source='ma_nv.ho_ten',
        read_only=True
    )

    # Chỉ hiển thị mã nhân viên (ví dụ: NV001)
    ma_nhan_vien = serializers.CharField(
        source='ma_nv.ma_nv',
        read_only=True
    )

    vai_tro = serializers.CharField(
        read_only=True
    )

    trang_thai = serializers.CharField(
        read_only=True
    )

    # =========================
    # CÁC TRƯỜNG NHẬP DỮ LIỆU
    # =========================

    username = serializers.CharField(
        write_only=True,
        required=True
    )

    password = serializers.CharField(
        write_only=True,
        required=True,
        style={'input_type': 'password'}
    )

    # chỉ nhập ID nhân viên để tạo tài khoản
    ma_nv = serializers.IntegerField(
        write_only=True,
        required=True
    )

    is_active = serializers.BooleanField(
        source='user.is_active',
        required=False,
        default=True
    )

    is_staff = serializers.BooleanField(
        source='user.is_staff',
        required=False,
        default=False
    )

    class Meta:
        model = TaiKhoan
        fields = [
            'id',

            # hiển thị
            'ten_dang_nhap',
            'ho_ten',
            'ma_nhan_vien',
            'vai_tro',
            'trang_thai',

            # nhập dữ liệu
            'username',
            'password',
            'ma_nv',
            'is_active',
            'is_staff',
        ]

    def create(self, validated_data):
        username = validated_data.pop('username')
        password = validated_data.pop('password')
        ma_nv_id = validated_data.pop('ma_nv')

        user_data = validated_data.pop('user', {})

        from apps.employees.models import NhanVien

        # lấy nhân viên theo ID
        nhan_vien_obj = NhanVien.objects.get(pk=ma_nv_id)

        # tạo User
        user = User.objects.create_user(
            username=username,
            password=password,
            is_active=user_data.get('is_active', True),
            is_staff=user_data.get('is_staff', False)
        )

        # tạo Tài khoản
        tai_khoan = TaiKhoan.objects.create(
            user=user,
            ma_nv=nhan_vien_obj
        )

        return tai_khoan

    def update(self, instance, validated_data):
        # cập nhật password
        if 'password' in validated_data:
            instance.user.set_password(
                validated_data.pop('password')
            )

        # cập nhật username
        if 'username' in validated_data:
            instance.user.username = validated_data.pop(
                'username'
            )

        user_data = validated_data.pop('user', {})

        if 'is_active' in user_data:
            instance.user.is_active = user_data['is_active']

        if 'is_staff' in user_data:
            instance.user.is_staff = user_data['is_staff']

        instance.user.save()

        # cập nhật nhân viên nếu cần
        if 'ma_nv' in validated_data:
            from apps.employees.models import NhanVien

            ma_nv_id = validated_data.pop('ma_nv')
            instance.ma_nv = NhanVien.objects.get(
                pk=ma_nv_id
            )

        instance.save()
        return instance
