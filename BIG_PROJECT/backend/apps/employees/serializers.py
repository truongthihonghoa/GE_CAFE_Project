from rest_framework import serializers
from .models import NhanVien

class NhanVienSerializer(serializers.ModelSerializer):
    class Meta:
        model = NhanVien
        fields = '__all__'
        extra_kwargs = {
            'ma_nv': {'required': False},
            'ma_chi_nhanh': {'required': False},
        }