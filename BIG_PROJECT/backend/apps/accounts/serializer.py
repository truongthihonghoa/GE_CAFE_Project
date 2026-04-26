from rest_framework import serializers
from .models import TaiKhoan

class TaiKhoanSerializer(serializers.ModelSerializer):
    ten_dang_nhap = serializers.CharField(source='user.username', read_only=True)
    ho_ten = serializers.CharField(source='ma_nv.ho_ten', read_only=True)  # giả sử NhanVien có ho_ten
    vai_tro = serializers.CharField(read_only=True)
    trang_thai = serializers.CharField(read_only=True)

    class Meta:
        model = TaiKhoan
        fields = [
            'id',           # nếu có
            'ten_dang_nhap',
            'ho_ten',
            'vai_tro',
            'ma_nv',
            'trang_thai',
        ]
