from rest_framework import serializers
from .models import HopDongLaoDong, HopDongLD_CT

class HopDongLDCTSerializer(serializers.ModelSerializer):
    class Meta:
        model = HopDongLD_CT
        fields = '__all__'




class HopDongLaoDongSerializer(serializers.ModelSerializer):
    chi_tiet = HopDongLDCTSerializer(required=False)

    ten_nv = serializers.CharField(source='ma_nv.ho_ten', read_only=True)

    class Meta:
        model = HopDongLaoDong
        fields = [
            'ma_hd',
            'ma_nv',
            'ten_nv',          
            'ma_chi_nhanh',
            'loai_hd',
            'chuc_vu',
            'ngay_bat_dau',
            'ngay_ket_thuc',
            'trang_thai',
            'chi_tiet'
        ]

    def create(self, validated_data):
        chi_tiet_data = validated_data.pop('chi_tiet', None)
        hop_dong = HopDongLaoDong.objects.create(**validated_data)

        if chi_tiet_data:
            HopDongLD_CT.objects.create(ma_hd=hop_dong, **chi_tiet_data)

        return hop_dong

    def update(self, instance, validated_data):
        chi_tiet_data = validated_data.pop('chi_tiet', None)

        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        instance.save()

        if chi_tiet_data:
            HopDongLD_CT.objects.update_or_create(
                ma_hd=instance,
                defaults=chi_tiet_data
            )

        return instance
