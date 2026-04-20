from rest_framework import serializers
from .models import YeuCau

class YeuCauSerializer(serializers.ModelSerializer):
    ten_nv = serializers.CharField(source='ma_nv.ho_ten', read_only=True)

    class Meta:
        model = YeuCau
        fields = ['ma_yc', 'loai_yeu_cau', 'ngay_bd', 'ngay_kt', 'ly_do', 'trang_thai', 'ma_nv', 'ten_nv']