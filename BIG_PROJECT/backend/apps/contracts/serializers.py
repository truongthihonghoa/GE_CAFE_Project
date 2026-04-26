from rest_framework import serializers
from .models import HopDongLaoDong, HopDongLD_CT


# =======================
# CHI TIẾT LƯƠNG
# =======================
class HopDongLDCTSerializer(serializers.Serializer):
    luong_co_ban = serializers.FloatField()
    luong_theo_gio = serializers.FloatField(required=False, default=0)
    so_gio_lam = serializers.FloatField(required=False, default=0)
    che_do_thuong = serializers.FloatField(required=False, default=0)
    dieu_khoan = serializers.CharField(required=False, allow_null=True, allow_blank=True)
    trach_nhiem = serializers.CharField(required=False, allow_null=True, allow_blank=True)
    ghi_chu = serializers.CharField(required=False, allow_null=True, allow_blank=True)

    def to_internal_value(self, data):
        if isinstance(data, dict):
            data = data.copy()
            data.pop('ma_hd', None)
        return super().to_internal_value(data)


# =======================
# HỢP ĐỒNG
# =======================
class HopDongLaoDongSerializer(serializers.ModelSerializer):

    # ===== HIỂN THỊ THÊM CHO ANDROID =====
    ten_nv = serializers.CharField(source='ma_nv.ho_ten', read_only=True)

    du_lieu_luong = serializers.SerializerMethodField()

    # 👉 THÊM FIELD LƯƠNG (để Android không phải bóc JSON)
    luong_co_ban = serializers.SerializerMethodField()
    luong_theo_gio = serializers.SerializerMethodField()
    so_gio_lam = serializers.SerializerMethodField()
    che_do_thuong = serializers.SerializerMethodField()

    class Meta:
        model = HopDongLaoDong
        fields = [
            'ma_hd',
            'ma_nv',
            'ten_nv',              # ✅ THÊM TÊN NV
            'ma_chi_nhanh',
            'loai_hd',
            'chuc_vu',
            'ngay_bat_dau',
            'ngay_ket_thuc',
            'trang_thai',

            # 👉 LƯƠNG
            'luong_co_ban',
            'luong_theo_gio',
            'so_gio_lam',
            'che_do_thuong',

            # JSON chi tiết
            'du_lieu_luong'
        ]

    # =======================
    # GET CHI TIẾT LƯƠNG
    # =======================
    def get_du_lieu_luong(self, obj):
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

    # =======================
    # GET LƯƠNG (FLAT FIELD)
    # =======================
    def get_luong_co_ban(self, obj):
        ct = getattr(obj, 'chi_tiet', None)
        return ct.luong_co_ban if ct else 0

    def get_luong_theo_gio(self, obj):
        ct = getattr(obj, 'chi_tiet', None)
        return ct.luong_theo_gio if ct else 0

    def get_so_gio_lam(self, obj):
        ct = getattr(obj, 'chi_tiet', None)
        return ct.so_gio_lam if ct else 0

    def get_che_do_thuong(self, obj):
        ct = getattr(obj, 'chi_tiet', None)
        return ct.che_do_thuong if ct else 0


    # =======================
    # CREATE
    # =======================
    def create(self, validated_data):
        request_data = self.context['request'].data
        chi_tiet_data = request_data.get('du_lieu_luong') or request_data.get('chi_tiet')

        if not validated_data.get('ma_hd'):
            import datetime
            validated_data['ma_hd'] = f"HD{int(datetime.datetime.now().timestamp())}"

        from django.db import transaction
        with transaction.atomic():
            hop_dong = HopDongLaoDong.objects.create(**validated_data)

            def to_f(val):
                try:
                    if isinstance(val, str):
                        val = val.replace('.', '').replace(',', '')
                    return float(val)
                except:
                    return 0.0

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


    # =======================
    # UPDATE
    # =======================
    def update(self, instance, validated_data):
        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        instance.save()

        request_data = self.context['request'].data
        chi_tiet_data = request_data.get('du_lieu_luong') or request_data.get('chi_tiet')

        if chi_tiet_data and isinstance(chi_tiet_data, dict):

            def to_f(val):
                try:
                    if isinstance(val, str):
                        val = val.replace('.', '').replace(',', '')
                    return float(val)
                except:
                    return 0.0

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
