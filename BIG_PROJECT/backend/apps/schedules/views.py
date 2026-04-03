from django.shortcuts import render, redirect, get_object_or_404
from django.contrib import messages
from django.http import JsonResponse
from django.db import transaction
from django.db.utils import OperationalError, ProgrammingError
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_http_methods
from django import forms
from django.utils import timezone
from django.core.exceptions import ValidationError
from .models import LichLamViec
from apps.employees.models import NhanVien
import datetime


class ScheduleForm(forms.Form):
    ngay_lam = forms.DateField(required=True, error_messages={'required': 'Vui lòng chọn ngày làm việc'})
    khung_gio = forms.CharField(max_length=50, required=True, error_messages={'required': 'Vui lòng chọn khung giờ'})
    vi_tri = forms.CharField(max_length=100, required=True, error_messages={'required': 'Vui lòng chọn vị trí'})
    ma_nv = forms.CharField(max_length=20, required=False)  # Only for admin
    
    def clean_ngay_lam(self):
        ngay_lam = self.cleaned_data.get('ngay_lam')
        if ngay_lam and ngay_lam < datetime.date.today():
            raise forms.ValidationError('Ngày làm việc không được ở quá khứ')
        return ngay_lam
    
    def clean(self):
        cleaned_data = super().clean()
        ngay_lam = cleaned_data.get('ngay_lam')
        khung_gio = cleaned_data.get('khung_gio')
        
        # Validate time format
        if khung_gio:
            try:
                start_time, end_time = khung_gio.split(' - ')
                datetime.datetime.strptime(start_time, '%H:%M')
                datetime.datetime.strptime(end_time, '%H:%M')
            except (ValueError, AttributeError):
                raise forms.ValidationError('Khung giờ không hợp lệ')
        
        return cleaned_data


def _validate_schedule_data(ngay_lam, khung_gio, vi_tri, ma_nv=None):
    errors = []
    
    # Validate required fields
    if not ngay_lam:
        errors.append('Vui lòng chọn ngày làm việc')
    if not khung_gio:
        errors.append('Vui lòng chọn khung giờ')
    if not vi_tri:
        errors.append('Vui lòng chọn vị trí')
    
    # Validate date logic
    if ngay_lam and ngay_lam < datetime.date.today():
        errors.append('Ngày làm việc không được ở quá khứ')
    
    # Validate time format
    if khung_gio:
        try:
            start_time, end_time = khung_gio.split(' - ')
            datetime.datetime.strptime(start_time, '%H:%M')
            datetime.datetime.strptime(end_time, '%H:%M')
        except (ValueError, AttributeError):
            errors.append('Khung giờ không hợp lệ')
    
    return errors


def _check_edit_permission(ngay_lam):
    """Check if schedule can be edited (only 1 day before)"""
    if ngay_lam:
        today = datetime.date.today()
        diff_days = (ngay_lam - today).days
        return diff_days >= 0  # Can edit if schedule is today or future
    return False


def _get_week_boundaries(date=None):
    """Get week start and end dates for given date (default: current week)"""
    if date is None:
        date = datetime.date.today()
    
    # Assuming week starts on Monday (weekday() returns 0 for Monday)
    start_of_week = date - datetime.timedelta(days=date.weekday())
    end_of_week = start_of_week + datetime.timedelta(days=6)
    
    return start_of_week, end_of_week


def _get_month_boundaries(date=None):
    """Get month start and end dates for given date (default: current month)"""
    if date is None:
        date = datetime.date.today()
    
    start_of_month = date.replace(day=1)
    
    # Get last day of month
    if date.month == 12:
        end_of_month = date.replace(year=date.year + 1, month=1, day=1) - datetime.timedelta(days=1)
    else:
        end_of_month = date.replace(month=date.month + 1, day=1) - datetime.timedelta(days=1)
    
    return start_of_month, end_of_month


def _is_admin(user):
    """Check if user is admin (simplified for demo)"""
    return user.is_authenticated and user.is_staff


def _sample_schedule_rows():
    return [
        {
            'ma_llv': 'LL001',
            'ngay_lam': '01/02/2026',
            'khung_gio': '7:00 - 11:00',
            'trang_thai': 'Đã Gửi',
            'trang_thai_key': 'sent',
            'nhan_vien': [
                {'ma_nv': 'NV001', 'ten_nv': 'Nguyễn Văn A', 'vi_tri': 'Pha chế'},
                {'ma_nv': 'NV002', 'ten_nv': 'Trần Thị B', 'vi_tri': 'Phục vụ'},
                {'ma_nv': 'NV003', 'ten_nv': 'Lê Minh C', 'vi_tri': 'Thu ngân'},
            ],
        },
        {
            'ma_llv': 'LL002',
            'ngay_lam': '02/02/2026',
            'khung_gio': '13:00 - 17:00',
            'trang_thai': 'Chưa Gửi',
            'trang_thai_key': 'draft',
            'nhan_vien': [
                {'ma_nv': 'NV004', 'ten_nv': 'Phạm Khánh D', 'vi_tri': 'Phục vụ'},
                {'ma_nv': 'NV005', 'ten_nv': 'Đỗ Hoàng E', 'vi_tri': 'Pha chế'},
            ],
        },
        {
            'ma_llv': 'LL003',
            'ngay_lam': '03/02/2026',
            'khung_gio': '18:00 - 22:00',
            'trang_thai': 'Đã Gửi',
            'trang_thai_key': 'sent',
            'nhan_vien': [
                {'ma_nv': 'NV006', 'ten_nv': 'Nguyễn Thu F', 'vi_tri': 'Thu ngân'},
                {'ma_nv': 'NV007', 'ten_nv': 'Trịnh Gia G', 'vi_tri': 'Giữ xe'},
                {'ma_nv': 'NV008', 'ten_nv': 'Võ Hải H', 'vi_tri': 'Phục vụ'},
            ],
        },
        {
            'ma_llv': 'LL004',
            'ngay_lam': '04/02/2026',
            'khung_gio': '7:00 - 11:00',
            'trang_thai': 'Chưa Gửi',
            'trang_thai_key': 'draft',
            'nhan_vien': [
                {'ma_nv': 'NV009', 'ten_nv': 'Bùi Lan I', 'vi_tri': 'Phục vụ'},
                {'ma_nv': 'NV010', 'ten_nv': 'Ngô Quốc K', 'vi_tri': 'Pha chế'},
            ],
        },
        {
            'ma_llv': 'LL005',
            'ngay_lam': '05/02/2026',
            'khung_gio': '13:00 - 17:00',
            'trang_thai': 'Đã Gửi',
            'trang_thai_key': 'sent',
            'nhan_vien': [
                {'ma_nv': 'NV011', 'ten_nv': 'Phan Tú L', 'vi_tri': 'Thu ngân'},
                {'ma_nv': 'NV012', 'ten_nv': 'Lý Bảo M', 'vi_tri': 'Giữ xe'},
            ],
        },
        {
            'ma_llv': 'LL006',
            'ngay_lam': '06/02/2026',
            'khung_gio': '18:00 - 22:00',
            'trang_thai': 'Chưa Gửi',
            'trang_thai_key': 'draft',
            'nhan_vien': [
                {'ma_nv': 'NV013', 'ten_nv': 'Trần An N', 'vi_tri': 'Phục vụ'},
                {'ma_nv': 'NV014', 'ten_nv': 'Mai Nhật O', 'vi_tri': 'Pha chế'},
                {'ma_nv': 'NV015', 'ten_nv': 'Tạ Hồng P', 'vi_tri': 'Thu ngân'},
            ],
        },
    ]


def _employee_options():
    return [
        {'ma_nv': 'NV001', 'ten_nv': 'Nguyễn Văn A', 'vi_tri': 'Pha chế'},
        {'ma_nv': 'NV002', 'ten_nv': 'Trần Thị B', 'vi_tri': 'Phục vụ'},
        {'ma_nv': 'NV003', 'ten_nv': 'Lê Minh C', 'vi_tri': 'Thu ngân'},
        {'ma_nv': 'NV004', 'ten_nv': 'Phạm Khánh D', 'vi_tri': 'Phục vụ'},
        {'ma_nv': 'NV005', 'ten_nv': 'Đỗ Hoàng E', 'vi_tri': 'Pha chế'},
        {'ma_nv': 'NV006', 'ten_nv': 'Nguyễn Thu F', 'vi_tri': 'Thu ngân'},
    ]


def schedule_list_view(request):
    filter_type = request.GET.get('filter', 'week')  # Default to week
    
    if filter_type == 'month':
        start_date, end_date = _get_month_boundaries()
    else:  # default to week
        start_date, end_date = _get_week_boundaries()
    
    schedules = []
    try:
        schedule_objects = LichLamViec.objects.filter(
            ngay_lam__gte=start_date,
            ngay_lam__lte=end_date
        ).order_by('ngay_lam', 'ca_lam')

        for schedule in schedule_objects:
            employees = []
            try:
                schedule_details = schedule.lichlamviec_ct_set.select_related('ma_nv').all()
                for detail in schedule_details:
                    employees.append({
                        'ma_nv': detail.ma_nv.ma_nv,
                        'ten_nv': detail.ma_nv.ho_ten,
                        'vi_tri': detail.vi_tri_vl
                    })
            except Exception:
                pass

            schedules.append({
                'ma_llv': schedule.ma_llv,
                'ngay_lam': schedule.ngay_lam.strftime('%d/%m/%Y'),
                'khung_gio': schedule.ca_lam,
                'trang_thai': schedule.trang_thai,
                'nhan_vien': employees
            })
    except (OperationalError, ProgrammingError):
        schedules = _sample_schedule_rows()
    
    context = {
        'schedules': schedules,
        'employee_options': _employee_options(),
        'shift_options': ['7:00 - 11:00', '13:00 - 17:00', '18:00 - 22:00'],
        'position_options': ['Pha chế', 'Phục vụ', 'Thu ngân', 'Giữ xe'],
        'filter_type': filter_type,
        'is_admin': _is_admin(request.user),
        'current_date': datetime.date.today().strftime('%d/%m/%Y'),
    }
    
    return render(request, 'schedules/schedule_list.html', context)


def schedule_create_view(request):
    if request.method == 'POST':
        ngay_lam = request.POST.get('ngay_lam')
        khung_gio = request.POST.get('khung_gio')
        selected_employees = request.POST.getlist('selected_employees')
        
        # Validate required fields
        errors = []
        if not ngay_lam:
            errors.append('Vui lòng chọn ngày làm việc')
        if not khung_gio:
            errors.append('Vui lòng chọn khung giờ')
        if not selected_employees:
            errors.append('Vui lòng chọn ít nhất một nhân viên')
        
        # Validate position for each selected employee
        for ma_nv in selected_employees:
            position_key = f'position_{ma_nv}'
            vi_tri = request.POST.get(position_key, '')
            if not vi_tri:
                errors.append(f'Vui lòng chọn vị trí cho nhân viên {ma_nv}')
        
        try:
            ngay_lam = datetime.datetime.strptime(ngay_lam, '%Y-%m-%d').date()
        except ValueError:
            errors.append('Định dạng ngày không hợp lệ')
        
        # Validate date logic
        if ngay_lam and ngay_lam < datetime.date.today():
            errors.append('Ngày làm việc không được ở quá khứ')
        
        if errors:
            for error in errors:
                messages.error(request, error)
            return _render_create_form(request)
        
        try:
            with transaction.atomic():
                # Generate schedule ID
                last_schedule = LichLamViec.objects.all().order_by('-ma_llv').first()
                last_num = int(last_schedule.ma_llv[2:]) if last_schedule else 0
                new_ma_llv = f'LL{last_num + 1:03d}'
                
                # Create schedule
                schedule = LichLamViec.objects.create(
                    ma_llv=new_ma_llv,
                    ngay_lam=ngay_lam,
                    ca_lam=khung_gio,
                    trang_thai='Chưa Gửi',
                    ngay_tao=datetime.date.today()
                )
                
                # Create schedule details for each selected employee
                for ma_nv in selected_employees:
                    position_key = f'position_{ma_nv}'
                    vi_tri = request.POST.get(position_key, '')
                    
                    if vi_tri:  # Only create if position is selected
                        try:
                            LichLamViec_CT.objects.create(
                                ma_llv=schedule,
                                ma_nv_id=ma_nv,
                                vi_tri_vl=vi_tri
                            )
                        except Exception as e:
                            print(f"Error creating schedule detail for {ma_nv}: {e}")
                
                messages.success(request, 'Tạo lịch làm việc thành công')
                return redirect('schedule_list')
                
        except Exception as e:
            messages.error(request, f'Lỗi: {str(e)}')
    
    return _render_create_form(request)

def _render_create_form(request):
    context = {
        'form': ScheduleForm(),
        'employee_options': NhanVien.objects.all(),
        'shift_options': ['7:00 - 11:00', '13:00 - 17:00', '18:00 - 22:00'],
        'position_options': ['Pha chế', 'Phục vụ', 'Thu ngân', 'Giữ xe'],
        'is_admin': _is_admin(request.user),
    }
    
    return render(request, 'schedules/schedule_create.html', context)


def schedule_edit_view(request, schedule_id):
    schedule = get_object_or_404(LichLamViec, ma_llv=schedule_id)
    
    # Check edit permission (only 1 day before)
    if not _check_edit_permission(schedule.ngay_lam):
        messages.error(request, 'Chỉ được sửa lịch trước 1 ngày')
        return redirect('schedule_list')
    
    if request.method == 'POST':
        ngay_lam = request.POST.get('ngay_lam')
        khung_gio = request.POST.get('khung_gio')
        selected_employees = request.POST.getlist('selected_employees')
        
        # Validate required fields
        errors = []
        if not ngay_lam:
            errors.append('Vui lòng chọn ngày làm việc')
        if not khung_gio:
            errors.append('Vui lòng chọn khung giờ')
        if not selected_employees:
            errors.append('Vui lòng chọn ít nhất một nhân viên')
        
        try:
            ngay_lam = datetime.datetime.strptime(ngay_lam, '%Y-%m-%d').date()
        except ValueError:
            errors.append('Định dạng ngày không hợp lệ')
        
        # Check edit permission again for new date
        if not _check_edit_permission(ngay_lam):
            errors.append('Chỉ được sửa lịch trước 1 ngày')
        
        if errors:
            for error in errors:
                messages.error(request, error)
            return _render_edit_form(request, schedule)
        
        try:
            with transaction.atomic():
                # Update schedule
                schedule.ngay_lam = ngay_lam
                schedule.ca_lam = khung_gio
                schedule.save()
                
                # Delete existing schedule details
                LichLamViec_CT.objects.filter(ma_llv=schedule).delete()
                
                # Create new schedule details for each selected employee
                for ma_nv in selected_employees:
                    position_key = f'position_{ma_nv}'
                    vi_tri = request.POST.get(position_key, '')
                    
                    if vi_tri:  # Only create if position is selected
                        try:
                            LichLamViec_CT.objects.create(
                                ma_llv=schedule,
                                ma_nv_id=ma_nv,
                                vi_tri_vl=vi_tri
                            )
                        except Exception as e:
                            print(f"Error creating schedule detail for {ma_nv}: {e}")
                
                messages.success(request, 'Cập nhật lịch làm việc thành công')
                return redirect('schedule_list')
                
        except Exception as e:
            messages.error(request, f'Lỗi: {str(e)}')
    
    return _render_edit_form(request, schedule)


def _render_edit_form(request, schedule):
    # Get current assignments for this schedule
    current_assignments = {}
    try:
        details = schedule.lichlamviec_ct_set.all()
        for detail in details:
            current_assignments[detail.ma_nv_id] = detail.vi_tri_vl
    except:
        pass
    
    context = {
        'schedule': schedule,
        'employee_options': NhanVien.objects.all(),
        'shift_options': ['7:00 - 11:00', '13:00 - 17:00', '18:00 - 22:00'],
        'position_options': ['Pha chế', 'Phục vụ', 'Thu ngân', 'Giữ xe'],
        'is_admin': _is_admin(request.user),
        'current_assignments': current_assignments,
    }
    
    return render(request, 'schedules/schedule_edit.html', context)


@require_http_methods(["DELETE"])
def schedule_delete_view(request, schedule_id):
    schedule = get_object_or_404(LichLamViec, ma_llv=schedule_id)
    
    try:
        with transaction.atomic():
            # Delete schedule details first
            LichLamViec_CT.objects.filter(ma_llv=schedule).delete()
            # Delete schedule
            schedule.delete()
            
        return JsonResponse({
            'success': True,
            'message': 'Xóa lịch làm việc thành công'
        })
    except Exception as e:
        return JsonResponse({
            'success': False,
            'message': f'Lỗi: {str(e)}'
        }, status=500)


@require_http_methods(["POST"])
def schedule_send_notification_view(request):
    schedule_ids = request.POST.getlist('schedule_ids')
    
    if not schedule_ids:
        return JsonResponse({
            'success': False,
            'message': 'Vui lòng chọn lịch để gửi thông báo'
        }, status=400)
    
    try:
        with transaction.atomic():
            # Update status to 'Đã Gửi' for selected schedules
            updated_count = LichLamViec.objects.filter(
                ma_llv__in=schedule_ids,
                trang_thai='Chưa Gửi'
            ).update(trang_thai='Đã Gửi')
            
        return JsonResponse({
            'success': True,
            'message': f'Đã gửi thông báo cho {updated_count} lịch làm việc'
        })
    except Exception as e:
        return JsonResponse({
            'success': False,
            'message': f'Lỗi: {str(e)}'
        }, status=500)


@require_http_methods(["GET"])
def schedule_detail_view(request, schedule_id):
    schedule = get_object_or_404(LichLamViec, ma_llv=schedule_id)
    
    # Get schedule details
    details = schedule.lichlamviec_ct_set.select_related('ma_nv').all()
    
    employees = []
    for detail in details:
        employees.append({
            'ma_nv': detail.ma_nv.ma_nv,
            'ten_nv': detail.ma_nv.ho_ten,
            'vi_tri': detail.vi_tri_vl
        })
    
    data = {
        'ma_llv': schedule.ma_llv,
        'ngay_lam': schedule.ngay_lam.strftime('%d/%m/%Y'),
        'khung_gio': schedule.ca_lam,
        'trang_thai': schedule.trang_thai,
        'ngay_tao': schedule.ngay_tao.strftime('%d/%m/%Y'),
        'nhan_vien': employees
    }
    
    return JsonResponse(data)
