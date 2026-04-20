from rest_framework import serializers
from .models import LichLamViec, ChiTietLichLamViec
from apps.employees.models import NhanVien
from apps.branches.models import ChiNhanh

class ScheduleDetailSerializer(serializers.ModelSerializer):
    class Meta:
        model = ChiTietLichLamViec
        fields = ['ma_nv', 'vi_tri']

class ScheduleSerializer(serializers.ModelSerializer):
    ho_ten_nv = serializers.SerializerMethodField()
    # Danh sách nhân viên kèm vị trí
    chi_tiet_nhan_vien = ScheduleDetailSerializer(many=True, source='chitietlichlamviec_set', required=False)
    
    class Meta:
        model = LichLamViec
        fields = ['ma_llv', 'ngay_lam', 'ca_lam', 'trang_thai', 'ngay_tao', 'ghi_chu', 'ma_chi_nhanh', 'chi_tiet_nhan_vien', 'ho_ten_nv']
        extra_kwargs = {
            'ma_llv': {'required': False}
        }

    def get_ho_ten_nv(self, obj):
        # Trả về chuỗi kết hợp: Tên (Vị trí), Tên (Vị trí)...
        details = ChiTietLichLamViec.objects.filter(ma_llv=obj)
        return ", ".join([f"{d.ma_nv.ho_ten} ({d.vi_tri})" for d in details])

    def create(self, validated_data):
        chi_tiet_data = validated_data.pop('chitietlichlamviec_set', [])
        schedule = LichLamViec.objects.create(**validated_data)
        for item in chi_tiet_data:
            ChiTietLichLamViec.objects.create(ma_llv=schedule, **item)
        return schedule

    def update(self, instance, validated_data):
        chi_tiet_data = validated_data.pop('chitietlichlamviec_set', [])
        
        # Cập nhật thông tin cơ bản
        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        instance.save()
        
        # Cập nhật chi tiết nhân viên (Xóa cũ thêm mới)
        if chi_tiet_data:
            instance.chitietlichlamviec_set.all().delete()
            for item in chi_tiet_data:
                ChiTietLichLamViec.objects.create(ma_llv=instance, **item)
        
        return instance