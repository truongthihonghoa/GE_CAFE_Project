from rest_framework import serializers
from .models import TaiKhoan

class TaiKhoanSerializer(serializers.ModelSerializer):
    ten_dang_nhap = serializers.CharField(source='user.username', read_only=True)
    ho_ten = serializers.CharField(source='ma_nv.ho_ten', read_only=True)
    vai_tro = serializers.CharField(read_only=True)
    trang_thai = serializers.CharField(read_only=True)

    is_staff = serializers.BooleanField(source='user.is_staff', read_only=True)

    class Meta:
        model = TaiKhoan
        fields = [
            'id',
            'ten_dang_nhap',
            'ho_ten',
            'vai_tro',
            'ma_nv',
            'trang_thai',
            'is_staff'
        ]
