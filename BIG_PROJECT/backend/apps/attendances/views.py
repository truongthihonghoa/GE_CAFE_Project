from rest_framework import viewsets, permissions, status
from rest_framework.response import Response
from django.utils import timezone
from datetime import datetime
from .models import ChamCong
from .serializers import ChamCongSerializer
from rest_framework.authentication import SessionAuthentication
from rest_framework_simplejwt.authentication import JWTAuthentication


class ChamCongViewSet(viewsets.ModelViewSet):
    queryset = ChamCong.objects.all()
    serializer_class = ChamCongSerializer
    # Chỉ cho phép người đã đăng nhập mới được thao tác
    permission_classes = [permissions.IsAuthenticated]
    authentication_classes = [JWTAuthentication, SessionAuthentication]

    def get_queryset(self):
        user = self.request.user
        
        # Luôn luôn chỉ trả về lịch sử của người đang đăng nhập
        # (Không phân biệt Admin hay Nhân viên để tránh lộ dữ liệu trên màn hình cá nhân)
        try:
            return ChamCong.objects.filter(ma_nv__taikhoan__user=user).order_by('-ngay_lam', '-gio_vao')
        except Exception:
            return ChamCong.objects.none()

    def create(self, request):
        user = request.user

        # Tự động lấy mã nhân viên từ tài khoản đang đăng nhập
        try:
            if not hasattr(user, 'taikhoan') or not user.taikhoan.ma_nv:
                return Response({"error": "Tài khoản của bạn chưa được liên kết với hồ sơ nhân viên."},
                                status=status.HTTP_400_BAD_REQUEST)

            nhan_vien = user.taikhoan.ma_nv
            ma_nv_id = nhan_vien.ma_nv
        except Exception:
            return Response({"error": "Lỗi xác thực hồ sơ nhân viên."}, status=400)

        today = timezone.now().date()
        now_time = timezone.now().time()

        # Kiểm tra xem có bản ghi nào ĐANG MỞ (đã có giờ vào nhưng chưa có giờ ra) không
        # Ưu tiên lấy bản ghi mới nhất của ngày hôm nay
        record = ChamCong.objects.filter(
            ma_nv=nhan_vien,
            ngay_lam=today,
            gio_ra__isnull=True
        ).order_by('-gio_vao').first()

        if record is None:
            # CHECK-IN: Tạo bản ghi mới
            # Tạo mã CC duy nhất bằng cách thêm 4 số cuối của timestamp
            suffix = int(timezone.now().timestamp()) % 10000
            ma_cc = f"C{ma_nv_id}{today.strftime('%y%m%d')}{suffix}" 
            # Ví dụ: CNV000072404271234 (1 + 7 + 6 + 4 = 18 ký tự, vừa đủ max_length=20)
            
            new_record = ChamCong.objects.create(
                ma_cc=ma_cc,
                ma_nv=nhan_vien,
                ngay_lam=today,
                gio_vao=now_time
            )
            return Response({
                "message": "Check-in thành công",
                "gio_vao": now_time.strftime('%H:%M:%S'),
                "ma_cc": ma_cc
            }, status=status.HTTP_201_CREATED)

        else:
            # CHECK-OUT: Đóng bản ghi đang mở
            record.gio_ra = now_time
            # Tính toán số giờ làm cho lượt này
            t1 = datetime.combine(today, record.gio_vao)
            t2 = datetime.combine(today, record.gio_ra)
            record.so_gio_lam = round((t2 - t1).seconds / 3600, 2)
            record.save()

            return Response({
                "message": "Check-out thành công",
                "gio_ra": now_time.strftime('%H:%M:%S'),
                "tong_gio_luot_nay": record.so_gio_lam
            })
