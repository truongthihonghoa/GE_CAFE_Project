from rest_framework import serializers
from .models import ChiNhanh

class ChiNhanhSerializer(serializers.ModelSerializer):
    manager_name = serializers.CharField(source='ma_nv_ql.ho_ten', read_only=True)

    class Meta:
        model = ChiNhanh
        fields = ['ma_chi_nhanh', 'ten_chi_nhanh', 'dia_chi', 'sdt', 'ma_nv_ql', 'manager_name', 'trang_thai']
        read_only_fields = ['ma_chi_nhanh']