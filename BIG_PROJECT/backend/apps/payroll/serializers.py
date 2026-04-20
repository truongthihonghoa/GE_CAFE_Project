from rest_framework import serializers
from .models import Luong

class LuongSerializer(serializers.ModelSerializer):
    ten_nv = serializers.CharField(source='nhan_vien.ho_ten', read_only=True)
    
    class Meta:
        model = Luong
        fields = [
            'ma_luong', 'nhan_vien', 'ten_nv', 'chi_nhanh', 
            'thang', 'nam', 'trang_thai', 'luong_co_ban', 
            'luong_theo_gio', 'so_ca_lam', 'so_gio_lam', 
            'thuong', 'phat', 'tong_luong', 'created_at', 'updated_at'
        ]
        read_only_fields = ['ma_luong', 'tong_luong', 'created_at', 'updated_at']

    def create(self, validated_data):
        # Tự động tính tổng lương nếu chưa có logic phức tạp hơn
        luong_co_ban = validated_data.get('luong_co_ban', 0)
        luong_theo_gio = validated_data.get('luong_theo_gio', 0)
        so_gio_lam = validated_data.get('so_gio_lam', 0)
        thuong = validated_data.get('thuong', 0)
        phat = validated_data.get('phat', 0)
        
        validated_data['tong_luong'] = luong_co_ban + (luong_theo_gio * so_gio_lam) + thuong - phat
        
        if not validated_data.get('ma_luong'):
            import time
            validated_data['ma_luong'] = f"ML{int(time.time())}"
            
        return super().create(validated_data)
