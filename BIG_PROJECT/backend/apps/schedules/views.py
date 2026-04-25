from django.shortcuts import render, redirect, get_object_or_404
from django.contrib import messages
from django.http import JsonResponse
from django.db import transaction
from django.db.utils import OperationalError, ProgrammingError
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_http_methods

# --- IMPORT MODELS ---
from .models import LichLamViec
from .models import NhanVien
from rest_framework import viewsets
from .serializers import ScheduleSerializer

import datetime


# --- CÁC HÀM HELPER ---
def _is_admin(user):
    return user.is_authenticated and user.is_staff


def _get_week_boundaries(date=None):
    if date is None: date = datetime.date.today()
    start = date - datetime.timedelta(days=date.weekday())
    return start, start + datetime.timedelta(days=6)


def _get_month_boundaries(date=None):
    if date is None: date = datetime.date.today()
    start = date.replace(day=1)
    if date.month == 12:
        end = date.replace(year=date.year + 1, month=1, day=1) - datetime.timedelta(days=1)
    else:
        end = date.replace(month=date.month + 1, day=1) - datetime.timedelta(days=1)
    return start, end


# --- VIEWS CHO GIAO DIỆN WEB (HTML) ---

def schedule_list_view(request):
    filter_type = request.GET.get('filter', 'week')
    start_date, end_date = _get_month_boundaries() if filter_type == 'month' else _get_week_boundaries()

    schedules = []
    try:
        schedule_objects = LichLamViec.objects.filter(
            ngay_lam__gte=start_date, ngay_lam__lte=end_date
        ).select_related('ma_nv').order_by('ngay_lam', 'ca_lam')

        for schedule in schedule_objects:
            schedules.append({
                'ma_llv': schedule.ma_llv,
                'ngay_lam': schedule.ngay_lam.strftime('%d/%m/%Y'),
                'khung_gio': schedule.ca_lam,
                'trang_thai': schedule.trang_thai,
                'nhan_vien': [{'ma_nv': schedule.ma_nv.ma_nv, 'ten_nv': schedule.ma_nv.ho_ten}]
            })
    except (OperationalError, ProgrammingError):
        schedules = []

    context = {
        'schedules': schedules,
        'filter_type': filter_type,
        'is_admin': _is_admin(request.user),
    }
    return render(request, 'schedules/schedule_list.html', context)


# 1. Hàm tạo (Create)
def schedule_create_view(request):
    context = {
        'employee_options': NhanVien.objects.all(),
        'shift_options': ['7:00 - 11:00', '13:00 - 17:00', '18:00 - 22:00'],
    }
    return render(request, 'schedules/schedule_create.html', context)


# 2. Hàm sửa (Edit) - FIX LỖI ATTRIBUTEERROR HIỆN TẠI
def schedule_edit_view(request, schedule_id):
    schedule = get_object_or_404(LichLamViec, ma_llv=schedule_id)
    context = {
        'schedule': schedule,
        'employee_options': NhanVien.objects.all(),
        'shift_options': ['7:00 - 11:00', '13:00 - 17:00', '18:00 - 22:00'],
    }
    return render(request, 'schedules/schedule_edit.html', context)


# 3. Hàm xóa (Delete)
@require_http_methods(["DELETE"])
def schedule_delete_view(request, schedule_id):
    schedule = get_object_or_404(LichLamViec, ma_llv=schedule_id)
    schedule.delete()
    return JsonResponse({'success': True})


# 4. Hàm gửi thông báo
@require_http_methods(["POST"])
def schedule_send_notification_view(request):
    return JsonResponse({'success': True, 'message': 'Đã gửi thông báo'})


# 5. Hàm chi tiết (Detail)
@require_http_methods(["GET"])
def schedule_detail_view(request, schedule_id):
    schedule = get_object_or_404(LichLamViec.objects.select_related('ma_nv'), ma_llv=schedule_id)
    return JsonResponse({
        'ma_llv': schedule.ma_llv,
        'ngay_lam': schedule.ngay_lam.strftime('%d/%m/%Y'),
        'khung_gio': schedule.ca_lam,
        'nhan_vien': [{'ma_nv': schedule.ma_nv.ma_nv, 'ten_nv': schedule.ma_nv.ho_ten}]
    })


# --- PHẦN QUAN TRỌNG: API CHO APP MOBILE ---
from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework_simplejwt.authentication import JWTAuthentication

class ScheduleAPIViewSet(viewsets.ModelViewSet):
    serializer_class = ScheduleSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        """
        Logic phân quyền:
        - Chủ (Admin): Thấy toàn bộ lịch làm việc của tất cả nhân viên.
        - Nhân viên: Chỉ thấy lịch làm việc của chính mình.
        """
        user = self.request.user

        # 1. Nếu là Chủ (is_staff hoặc is_superuser) -> Hiển thị tất cả
        if user.is_staff or user.is_superuser:
            return LichLamViec.objects.all().order_by('-ngay_lam')

        # 2. Nếu là Nhân viên -> Lọc qua bảng trung gian TaiKhoan
        # Giả sử trong model LichLamViec có trường 'ma_nv' nối tới model NhanVien
        # Và trong model NhanVien có trường 'taikhoan' nối tới bảng trung gian
        return LichLamViec.objects.filter(
            ma_nv__taikhoan__user=user
        ).order_by('-ngay_lam')

    def is_admin(self, request):
        return request.user.is_staff or request.user.is_superuser

    def create(self, request, *args, **kwargs):
        """Chỉ Chủ mới được sắp lịch làm việc"""
        if not self.is_admin(request):
            return Response(
                {"detail": "Chỉ quản lý mới có quyền sắp xếp lịch làm việc."},
                status=status.HTTP_403_FORBIDDEN
            )
        return super().create(request, *args, **kwargs)

    def update(self, request, *args, **kwargs):
        """Chỉ Chủ mới được sửa lịch làm việc"""
        if not self.is_admin(request):
            return Response(
                {"detail": "Bạn không có quyền chỉnh sửa lịch làm việc này."},
                status=status.HTTP_403_FORBIDDEN
            )
        return super().update(request, *args, **kwargs)

    def destroy(self, request, *args, **kwargs):
        """Chỉ Chủ mới được xóa lịch làm việc"""
        if not self.is_admin(request):
            return Response(
                {"detail": "Hành động bị từ chối. Chỉ Chủ mới có thể xóa lịch."},
                status=status.HTTP_403_FORBIDDEN
            )
        return super().destroy(request, *args, **kwargs)