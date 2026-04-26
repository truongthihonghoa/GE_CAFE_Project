from rest_framework import serializers
from .models import LichLamViec

class ScheduleSerializer(serializers.ModelSerializer):
    ten_nv = serializers.CharField(source='ma_nv.ho_ten', read_only=True)

    class Meta:
        model = LichLamViec
        fields = [
            'ma_llv',
            'ngay_lam',
            'ca_lam',
            'trang_thai',
            'ngay_tao',
            'ghi_chu',
            'ma_chi_nhanh',
            'ma_nv',
            'ten_nv'
        ]
