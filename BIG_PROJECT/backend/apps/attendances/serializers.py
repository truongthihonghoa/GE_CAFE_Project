from rest_framework import serializers
from .models import ChamCong

class ChamCongSerializer(serializers.ModelSerializer):
    ten_nhan_vien_that = serializers.SerializerMethodField()

    class Meta:
        model = ChamCong
        fields = '__all__'

    def get_ten_nhan_vien_that(self, obj):
        try:
            # 1. Thử lấy từ bảng NhanVien (Trường ho_ten)
            if obj.ma_nv and obj.ma_nv.ho_ten:
                return obj.ma_nv.ho_ten
            
            # 2. Thử lấy từ bảng User (Nếu có liên kết)
            # Truy cập: ChamCong -> NhanVien -> TaiKhoan -> User
            try:
                user = obj.ma_nv.taikhoan.user
                full_name = f"{user.last_name} {user.first_name}".strip()
                if full_name:
                    return full_name
            except:
                pass

            # 3. Cuối cùng mới trả về mã nhân viên
            return obj.ma_nv.ma_nv
        except:
            return "Chưa xác định"