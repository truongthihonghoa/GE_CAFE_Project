from django.shortcuts import render
from rest_framework.viewsets import ModelViewSet
from .models import YeuCau
from .serializers import YeuCauSerializer

def _sample_requests():
    return [
        {
            'ma_dk': 'DK000001',
            'ma_nv': 'NV001',
            'ten_nv': 'Nguyễn Văn An',
            'loai_yc': 'Nghỉ phép',
            'ngay_dk': '26/12/2026',
            'trang_thai': 'Chờ duyệt',
            'trang_thai_key': 'pending',
            'chi_tiet_rows': [
                ('Mã nhân viên', 'NV001'),
                ('Loại yêu cầu', 'Nghỉ phép'),
                ('Ngày bắt đầu', '26/12/2026'),
                ('Ngày kết thúc', '27/12/2026'),
                ('Lý do', 'Đi khám sức khỏe'),
                ('Ngày đăng ký', '25/12/2026'),
            ],
        },
        {
            'ma_dk': 'DK000002',
            'ma_nv': 'NV002',
            'ten_nv': 'Nguyễn Thanh Anh',
            'loai_yc': 'Nghỉ phép',
            'ngay_dk': '26/02/2025',
            'trang_thai': 'Chờ duyệt',
            'trang_thai_key': 'pending',
            'chi_tiet_rows': [
                ('Mã nhân viên', 'NV002'),
                ('Loại yêu cầu', 'Nghỉ phép'),
                ('Ngày bắt đầu', '26/02/2025'),
                ('Ngày kết thúc', '27/02/2025'),
                ('Lý do', 'Giải quyết việc gia đình'),
                ('Ngày đăng ký', '25/02/2025'),
            ],
        },
        {
            'ma_dk': 'DK000003',
            'ma_nv': 'NV003',
            'ten_nv': 'Nguyễn Văn Anh',
            'loai_yc': 'Nghỉ phép',
            'ngay_dk': '26/03/2025',
            'trang_thai': 'Chờ duyệt',
            'trang_thai_key': 'pending',
            'chi_tiet_rows': [
                ('Mã nhân viên', 'NV003'),
                ('Loại yêu cầu', 'Nghỉ phép'),
                ('Ngày bắt đầu', '26/03/2025'),
                ('Ngày kết thúc', '26/03/2025'),
                ('Lý do', 'Nghỉ cá nhân'),
                ('Ngày đăng ký', '25/03/2025'),
            ],
        },
        {
            'ma_dk': 'DK000004',
            'ma_nv': 'NV004',
            'ten_nv': 'Nguyễn Thị Anh',
            'loai_yc': 'Ca làm',
            'ngay_dk': '08/02/2026',
            'trang_thai': 'Đã duyệt',
            'trang_thai_key': 'approved',
            'chi_tiet_rows': [
                ('Mã nhân viên', 'NV004'),
                ('Loại yêu cầu', 'Ca làm việc'),
                ('Ngày làm', '08/02/2026'),
                ('Giờ bắt đầu', '08:00'),
                ('Giờ kết thúc', '17:00'),
                ('Ngày đăng ký', '25/12/2026'),
            ],
        },
        {
            'ma_dk': 'DK000005',
            'ma_nv': 'NV005',
            'ten_nv': 'Nguyễn Văn Ánh',
            'loai_yc': 'Nghỉ phép',
            'ngay_dk': '26/05/2025',
            'trang_thai': 'Đã duyệt',
            'trang_thai_key': 'approved',
            'chi_tiet_rows': [
                ('Mã nhân viên', 'NV005'),
                ('Loại yêu cầu', 'Nghỉ phép'),
                ('Ngày bắt đầu', '26/05/2025'),
                ('Ngày kết thúc', '27/05/2025'),
                ('Lý do', 'Nghỉ phép năm'),
                ('Ngày đăng ký', '24/05/2025'),
            ],
        },
        {
            'ma_dk': 'DK000006',
            'ma_nv': 'NV006',
            'ten_nv': 'Trần Minh Quân',
            'loai_yc': 'Ca làm',
            'ngay_dk': '02/06/2025',
            'trang_thai': 'Chờ duyệt',
            'trang_thai_key': 'pending',
            'chi_tiet_rows': [
                ('Mã nhân viên', 'NV006'),
                ('Loại yêu cầu', 'Ca làm việc'),
                ('Ngày làm', '02/06/2025'),
                ('Giờ bắt đầu', '13:00'),
                ('Giờ kết thúc', '21:00'),
                ('Ngày đăng ký', '01/06/2025'),
            ],
        },
        {
            'ma_dk': 'DK000007',
            'ma_nv': 'NV007',
            'ten_nv': 'Lê Thị Hồng',
            'loai_yc': 'Nghỉ phép',
            'ngay_dk': '05/06/2025',
            'trang_thai': 'Đã từ chối',
            'trang_thai_key': 'rejected',
            'chi_tiet_rows': [
                ('Mã nhân viên', 'NV007'),
                ('Loại yêu cầu', 'Nghỉ phép'),
                ('Ngày bắt đầu', '05/06/2025'),
                ('Ngày kết thúc', '06/06/2025'),
                ('Lý do', 'Nghỉ việc cá nhân'),
                ('Ngày đăng ký', '04/06/2025'),
            ],
        },
    ]


def request_review_list_view(request):
    return render(
        request,
        'requests/request_review_list.html',
        {'requests_data': _sample_requests()},
    )


def request_list_view(request):
    """New view for the enhanced request list UI with mock data"""
    return render(request, 'requests/request_list.html')




# API đăng ký lịch
from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.decorators import action
from rest_framework.permissions import IsAuthenticated
from rest_framework_simplejwt.authentication import JWTAuthentication


class BaseYeuCauViewSet(viewsets.ModelViewSet):
    serializer_class = YeuCauSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated]

    def get_base_queryset(self, keyword):
        user = self.request.user
        # 1. Admin (Chủ): Thấy tất cả yêu cầu theo loại
        if user.is_superuser or user.is_staff:
            return YeuCau.objects.filter(loai_yeu_cau__icontains=keyword)

        # 2. Nhân viên: Chỉ thấy yêu cầu của chính mình qua bảng trung gian
        return YeuCau.objects.filter(
            loai_yeu_cau__icontains=keyword,
            ma_nv__taikhoan__user=user
        )

    @action(detail=True, methods=['patch'], url_path='duyet')
    def duyet_yeu_cau(self, request, pk=None):
        """Tính năng dành riêng cho Chủ để duyệt yêu cầu"""
        if not (request.user.is_superuser or request.user.is_staff):
            return Response({"detail": "Chỉ Chủ cửa hàng mới có quyền duyệt."}, status=403)

        yeu_cau = self.get_object()
        moi_status = request.data.get('trang_thai')  # Gửi lên: "Đã duyệt" hoặc "Từ chối"

        if not moi_status:
            return Response({"detail": "Vui lòng cung cấp trạng thái mới."}, status=400)

        yeu_cau.trang_thai = moi_status
        yeu_cau.save()
        return Response({"detail": f"Đã cập nhật trạng thái thành: {moi_status}"})


# --- Các ViewSet cụ thể ---

class DangKyLichViewSet(BaseYeuCauViewSet):
    def get_queryset(self):
        return self.get_base_queryset("Đăng ký")


class NghiPhepViewSet(BaseYeuCauViewSet):
    def get_queryset(self):
        return self.get_base_queryset("Nghỉ")