from rest_framework import serializers
from .models import HopDongLaoDong, HopDongLD_CT

class HopDongLDCTSerializer(serializers.Serializer):
    luong_co_ban = serializers.FloatField()
    luong_theo_gio = serializers.FloatField(required=False, default=0)
    so_gio_lam = serializers.FloatField(required=False, default=0)
    che_do_thuong = serializers.FloatField(required=False, default=0)
    dieu_khoan = serializers.CharField(required=False, allow_null=True, allow_blank=True)
    trach_nhiem = serializers.CharField(required=False, allow_null=True, allow_blank=True)
    ghi_chu = serializers.CharField(required=False, allow_null=True, allow_blank=True)

    def to_internal_value(self, data):
        # CAN THIỆP MẠNH: Xóa bỏ ma_hd khỏi dữ liệu ngay khi nhận được
        # Điều này ngăn DRF kiểm tra tính tồn tại của mã hợp đồng khi đang tạo mới
        if isinstance(data, dict):
            data = data.copy()
            data.pop('ma_hd', None)
        return super().to_internal_value(data)

class HopDongLaoDongSerializer(serializers.ModelSerializer):
    # Khai báo trường này để trả dữ liệu về cho Android hiển thị
    du_lieu_luong = serializers.SerializerMethodField()
    
    class Meta:
        model = HopDongLaoDong
        fields = ['ma_hd', 'ma_nv', 'ma_chi_nhanh', 'loai_hd', 'chuc_vu', 'ngay_bat_dau', 'ngay_ket_thuc', 'trang_thai', 'du_lieu_luong']

    def get_du_lieu_luong(self, obj):
        # Truy vấn trực tiếp bằng Primary Key để đảm bảo khớp với cột ma_hd_id trong DB
        from .models import HopDongLD_CT
        ct = HopDongLD_CT.objects.filter(ma_hd_id=obj.pk).first()
        
        if ct:
            return {
                'luong_co_ban': ct.luong_co_ban,
                'luong_theo_gio': ct.luong_theo_gio,
                'so_gio_lam': ct.so_gio_lam,
                'che_do_thuong': ct.che_do_thuong,
                'dieu_khoan': ct.dieu_khoan or '',
                'trach_nhiem': ct.trach_nhiem or '',
                'ghi_chu': ct.ghi_chu or ''
            }
        return None

    def create(self, validated_data):
        # Lấy dữ liệu từ request
        request_data = self.context['request'].data
        chi_tiet_data = request_data.get('du_lieu_luong') or request_data.get('chi_tiet')
        
        # Tự động sinh mã hợp đồng nếu chưa có
        if not validated_data.get('ma_hd'):
            import datetime
            validated_data['ma_hd'] = f"HD{int(datetime.datetime.now().timestamp())}"

        from django.db import transaction
        with transaction.atomic():
            # 1. Tạo hợp đồng chính
            hop_dong = HopDongLaoDong.objects.create(**validated_data)

            # 2. Tạo bản ghi chi tiết hợp đồng (Ép buộc phải có)
            def to_f(val):
                try:
                    if isinstance(val, str):
                        val = val.replace('.', '').replace(',', '')
                    return float(val)
                except: return 0.0

            # Lấy dữ liệu hoặc dùng giá trị mặc định 0
            detail_values = chi_tiet_data if isinstance(chi_tiet_data, dict) else {}
            
            HopDongLD_CT.objects.create(
                ma_hd=hop_dong,
                luong_co_ban=to_f(detail_values.get('luong_co_ban')),
                luong_theo_gio=to_f(detail_values.get('luong_theo_gio')),
                so_gio_lam=to_f(detail_values.get('so_gio_lam')),
                che_do_thuong=to_f(detail_values.get('che_do_thuong')),
                dieu_khoan=detail_values.get('dieu_khoan', ''),
                trach_nhiem=detail_values.get('trach_nhiem', ''),
                ghi_chu=detail_values.get('ghi_chu', '')
            )

        return hop_dong

    def update(self, instance, validated_data):
        # Cập nhật thông tin chính
        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        instance.save()

        # Cập nhật chi tiết
        request_data = self.context['request'].data
        chi_tiet_data = request_data.get('du_lieu_luong') or request_data.get('chi_tiet')
        
        if chi_tiet_data and isinstance(chi_tiet_data, dict):
            def to_f(val):
                try:
                    if isinstance(val, str):
                        val = val.replace('.', '').replace(',', '')
                    return float(val)
                except: return 0.0

            HopDongLD_CT.objects.update_or_create(
                ma_hd=instance,
                defaults={
                    'luong_co_ban': to_f(chi_tiet_data.get('luong_co_ban')),
                    'luong_theo_gio': to_f(chi_tiet_data.get('luong_theo_gio')),
                    'so_gio_lam': to_f(chi_tiet_data.get('so_gio_lam')),
                    'che_do_thuong': to_f(chi_tiet_data.get('che_do_thuong')),
                    'dieu_khoan': chi_tiet_data.get('dieu_khoan', ''),
                    'trach_nhiem': chi_tiet_data.get('trach_nhiem', ''),
                    'ghi_chu': chi_tiet_data.get('ghi_chu', '')
                }
            )

        return instance
