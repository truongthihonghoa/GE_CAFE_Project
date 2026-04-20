from rest_framework import serializers
from .models import NhanVien

class NhanVienSerializer(serializers.ModelSerializer):
    class Meta:
        model = NhanVien
        fields = '__all__'