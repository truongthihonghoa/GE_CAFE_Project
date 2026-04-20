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
class ScheduleAPIViewSet(viewsets.ModelViewSet):
    queryset = LichLamViec.objects.all().order_by('-ngay_lam')
    serializer_class = ScheduleSerializer