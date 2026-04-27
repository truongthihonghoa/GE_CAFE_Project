from rest_framework import serializers
from django.contrib.auth.models import User
from .models import TaiKhoan

class TaiKhoanSerializer(serializers.ModelSerializer):
    # 1. CÁC TRƯỜNG CHỈ ĐỂ HIỂN THỊ (Read Only)
    ten_dang_nhap = serializers.CharField(source='user.username', read_only=True)
    ho_ten = serializers.CharField(source='ma_nv.ho_ten', read_only=True)

    # Sửa lỗi TypeError bằng cách lấy ID của nhân viên để hiển thị
    ma_nv_display = serializers.IntegerField(source='ma_nv.id', read_only=True)

    # 2. CÁC TRƯỜNG ĐỂ NHẬP DỮ LIỆU (Bỏ read_only để hiện nút POST)
    vai_tro = serializers.CharField(required=False, allow_blank=True)
    trang_thai = serializers.CharField(required=False, allow_blank=True)

    # Dùng write_only để nhận dữ liệu từ App/Web gửi lên
    username = serializers.CharField(write_only=True, required=True)
    password = serializers.CharField(write_only=True, required=True, style={'input_type': 'password'})
    ma_nv = serializers.IntegerField(write_only=True, required=True)

    is_active = serializers.BooleanField(source='user.is_active', required=False, default=True)
    is_staff = serializers.BooleanField(source='user.is_staff', required=False, default=False)

    class Meta:
        model = TaiKhoan
        fields = [
            'id',
            'ten_dang_nhap',
            'ho_ten',
            'ma_nv_display', # Trường hiển thị ID nhân viên
            'vai_tro',
            'trang_thai',
            'username',
            'password',
            'ma_nv',         # Trường nhập ID nhân viên (số nguyên)
            'is_active',
            'is_staff',
        ]

    def create(self, validated_data):
        # Lấy ID nhân viên từ dữ liệu đã validate
        username = validated_data.pop('username')
        password = validated_data.pop('password')
        ma_nv_id = validated_data.pop('ma_nv') # Đây là số nguyên (int)

        user_data = validated_data.pop('user', {})

        # Tìm đối tượng NhanVien thực sự dựa trên ID
        from apps.employees.models import NhanVien
        nhan_vien_obj = NhanVien.objects.get(pk=ma_nv_id)

        # 1. Tạo User hệ thống
        user = User.objects.create_user(
            username=username,
            password=password,
            is_active=user_data.get('is_active', True),
            is_staff=user_data.get('is_staff', False)
        )

        # 2. Tạo TaiKhoan và gán đối tượng nhan_vien_obj vào
        tai_khoan = TaiKhoan.objects.create(
            user=user,
            ma_nv=nhan_vien_obj,
            vai_tro=validated_data.get('vai_tro', ''),
            trang_thai=validated_data.get('trang_thai', 'Đang hoạt động')
        )
        return tai_khoan

    def update(self, instance, validated_data):
        if 'password' in validated_data:
            instance.user.set_password(validated_data.pop('password'))
        if 'username' in validated_data:
            instance.user.username = validated_data.pop('username')

        user_data = validated_data.pop('user', {})
        if 'is_active' in user_data:
            instance.user.is_active = user_data['is_active']
        if 'is_staff' in user_data:
            instance.user.is_staff = user_data['is_staff']
        instance.user.save()

        if 'ma_nv' in validated_data:
            from apps.employees.models import NhanVien
            ma_nv_id = validated_data.pop('ma_nv')
            instance.ma_nv = NhanVien.objects.get(pk=ma_nv_id)

        instance.vai_tro = validated_data.get('vai_tro', instance.vai_tro)
        instance.trang_thai = validated_data.get('trang_thai', instance.trang_thai)
        instance.save()
        return instance