from rest_framework import serializers
from .models import ChamCong

class ChamCongSerializer(serializers.ModelSerializer):
    # Lấy tên nhân viên trực tiếp từ mối quan hệ ForeignKey
    ho_ten = serializers.CharField(source='ma_nv.ho_ten', read_only=True)

    class Meta:
        model = ChamCong
        fields = ('ma_cc', 'ma_nv', 'ho_ten', 'ngay_lam', 'gio_vao', 'gio_ra', 'so_gio_lam', 'trang_thai', 'ghi_chu')