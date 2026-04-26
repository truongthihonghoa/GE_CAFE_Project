from django.shortcuts import render, redirect, get_object_or_404
from django.contrib import messages
from django.http import JsonResponse
from django.db import transaction
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_http_methods
from django import forms
from .models import HopDongLaoDong, HopDongLD_CT
from apps.employees.models import NhanVien
import datetime


class ContractForm(forms.Form):
    ma_nv = forms.CharField(max_length=20, required=True, error_messages={'required': 'Vui lòng chọn nhân viên'})
    loai_hd = forms.CharField(max_length=100, required=True, error_messages={'required': 'Vui lòng chọn loại hợp đồng'})
    ngay_bd = forms.DateField(required=True, error_messages={'required': 'Vui lòng chọn ngày bắt đầu'})
    ngay_kt = forms.DateField(required=True, error_messages={'required': 'Vui lòng chọn ngày kết thúc'})
    chuc_vu = forms.CharField(max_length=100, required=True, error_messages={'required': 'Vui lòng chọn chức vụ'})
    luong_co_ban = forms.FloatField(required=False, min_value=0)
    luong_theo_gio = forms.FloatField(required=False, min_value=0)
    so_gio_lam = forms.FloatField(required=False, min_value=0)
    thuong = forms.FloatField(required=False, min_value=0)
    phat = forms.FloatField(required=False, min_value=0)
    
    def clean(self):
        cleaned_data = super().clean()
        ngay_bd = cleaned_data.get('ngay_bd')
        ngay_kt = cleaned_data.get('ngay_kt')
        loai_hd = cleaned_data.get('loai_hd')
        luong_co_ban = cleaned_data.get('luong_co_ban')
        luong_theo_gio = cleaned_data.get('luong_theo_gio')
        
        # Date validation
        if ngay_bd and ngay_kt and ngay_bd >= ngay_kt:
            raise forms.ValidationError('Ngày bắt đầu phải nhỏ hơn ngày kết thúc')
        
        # Salary validation
        if loai_hd == 'Full-time':
            if luong_theo_gio and luong_theo_gio > 0:
                raise forms.ValidationError('Full time không có lương/giờ')
            if not luong_co_ban or luong_co_ban <= 0:
                raise forms.ValidationError('Full time phải có lương cơ bản')
        elif loai_hd == 'Part-time':
            if luong_co_ban and luong_co_ban > 0:
                raise forms.ValidationError('Part time không có lương cơ bản')
            if not luong_theo_gio or luong_theo_gio <= 0:
                raise forms.ValidationError('Part time phải có lương/giờ')
        
        return cleaned_data


def _sample_contracts():
    return [
        {'ma_hd': 'HĐ00001', 'ma_nv': 'NV00001', 'ten_nv': 'Nguyễn Văn An', 'loai_hd': 'Part-time', 'ngay_bd': '25/12/2025', 'ngay_kt': '25/12/2026', 'ngay_bd_iso': '2025-12-25', 'ngay_kt_iso': '2026-12-25', 'chuc_vu': 'Pha chế', 'muc_luong': '2.000.000'},
        {'ma_hd': 'HĐ00002', 'ma_nv': 'NV00002', 'ten_nv': 'Lê Hoài Bảo An', 'loai_hd': 'Full-time', 'ngay_bd': '10/01/2026', 'ngay_kt': '10/01/2027', 'ngay_bd_iso': '2026-01-10', 'ngay_kt_iso': '2027-01-10', 'chuc_vu': 'Giữ xe', 'muc_luong': '6.500.000'},
        {'ma_hd': 'HĐ00003', 'ma_nv': 'NV00003', 'ten_nv': 'Trần Thị Mai Loan', 'loai_hd': 'Thời vụ', 'ngay_bd': '15/02/2026', 'ngay_kt': '15/08/2026', 'ngay_bd_iso': '2026-02-15', 'ngay_kt_iso': '2026-08-15', 'chuc_vu': 'Phục vụ', 'muc_luong': '4.800.000'},
        {'ma_hd': 'HĐ00004', 'ma_nv': 'NV00004', 'ten_nv': 'Phạm Quang Bảo', 'loai_hd': 'Part-time', 'ngay_bd': '01/03/2026', 'ngay_kt': '01/03/2027', 'ngay_bd_iso': '2026-03-01', 'ngay_kt_iso': '2027-03-01', 'chuc_vu': 'Phục vụ', 'muc_luong': '2.600.000'},
        {'ma_hd': 'HĐ00005', 'ma_nv': 'NV00005', 'ten_nv': 'Nguyễn Viết Bảo', 'loai_hd': 'Thử việc', 'ngay_bd': '20/03/2026', 'ngay_kt': '20/05/2026', 'ngay_bd_iso': '2026-03-20', 'ngay_kt_iso': '2026-05-20', 'chuc_vu': 'Pha chế', 'muc_luong': '3.500.000'},
        {'ma_hd': 'HĐ00006', 'ma_nv': 'NV00006', 'ten_nv': 'Lê Văn Nhật Anh', 'loai_hd': 'Full-time', 'ngay_bd': '05/04/2026', 'ngay_kt': '05/04/2027', 'ngay_bd_iso': '2026-04-05', 'ngay_kt_iso': '2027-04-05', 'chuc_vu': 'Giữ xe', 'muc_luong': '6.200.000'},
        {'ma_hd': 'HĐ00007', 'ma_nv': 'NV00007', 'ten_nv': 'Nguyễn Văn Anh', 'loai_hd': 'Part-time', 'ngay_bd': '12/04/2026', 'ngay_kt': '12/10/2026', 'ngay_bd_iso': '2026-04-12', 'ngay_kt_iso': '2026-10-12', 'chuc_vu': 'Pha chế', 'muc_luong': '2.400.000'},
        {'ma_hd': 'HĐ00008', 'ma_nv': 'NV00008', 'ten_nv': 'Trần Lê Văn Khoa', 'loai_hd': 'Full-time', 'ngay_bd': '22/04/2026', 'ngay_kt': '22/04/2027', 'ngay_bd_iso': '2026-04-22', 'ngay_kt_iso': '2027-04-22', 'chuc_vu': 'Giữ xe', 'muc_luong': '6.000.000'},
    ]


def _form_context():
    return {
        'employees': NhanVien.objects.all(),
        'contract_types': ['Part-time', 'Full-time', 'Thử việc', 'Thời vụ'],
        'positions': ['Pha chế', 'Phục vụ', 'Giữ xe', 'Thu ngân'],
    }


def _validate_contract_data(ma_nv, loai_hd, ngay_bd, ngay_kt, chuc_vu, luong_co_ban, luong_theo_gio):
    errors = []
    
    # Validate required fields
    if not ma_nv:
        errors.append('Vui lòng chọn nhân viên')
    if not loai_hd:
        errors.append('Vui lòng chọn loại hợp đồng')
    if not ngay_bd:
        errors.append('Vui lòng chọn ngày bắt đầu')
    if not ngay_kt:
        errors.append('Vui lòng chọn ngày kết thúc')
    if not chuc_vu:
        errors.append('Vui lòng chọn chức vụ')
    
    # Validate date logic
    if ngay_bd and ngay_kt and ngay_bd >= ngay_kt:
        errors.append('Ngày bắt đầu phải nhỏ hơn ngày kết thúc')
    
    # Validate salary logic
    if loai_hd == 'Full-time':
        if luong_theo_gio and luong_theo_gio > 0:
            errors.append('Full time không có lương/giờ')
        if not luong_co_ban or luong_co_ban <= 0:
            errors.append('Full time phải có lương cơ bản')
    elif loai_hd == 'Part-time':
        if luong_co_ban and luong_co_ban > 0:
            errors.append('Part time không có lương cơ bản')
        if not luong_theo_gio or luong_theo_gio <= 0:
            errors.append('Part time phải có lương/giờ')
    
    return errors


def _check_employee_contract(ma_nv, exclude_contract_id=None):
    existing_contract = HopDongLaoDong.objects.filter(
        ma_nv_id=ma_nv,
        trang_thai='Đang hiệu lực'
    )
    
    if exclude_contract_id:
        existing_contract = existing_contract.exclude(ma_hd=exclude_contract_id)
    
    return existing_contract.exists()


def contract_list_view(request):
    # Lấy dữ liệu thật từ Database
    contracts = HopDongLaoDong.objects.all().order_by('-ma_hd')
    
    context = {
        'contracts': contracts,
        'form': ContractForm()
    }
    return render(request, 'contracts/contract_list.html', context)

def contract_edit_view(request, contract_id):
    # Lấy hợp đồng thật từ DB
    hop_dong = get_object_or_404(HopDongLaoDong, ma_hd=contract_id)
    
    if request.method == 'POST':
        form = ContractForm(request.POST)
        if form.is_valid():
            try:
                with transaction.atomic():
                    cd = form.cleaned_data
                    # Cập nhật hợp đồng chính
                    hop_dong.loai_hd = cd['loai_hd']
                    hop_dong.ngay_bat_dau = cd['ngay_bd']
                    hop_dong.ngay_ket_thuc = cd['ngay_kt']
                    hop_dong.chuc_vu = cd['chuc_vu']
                    hop_dong.save()
                    
                    # Cập nhật chi tiết lương
                    HopDongLD_CT.objects.update_or_create(
                        ma_hd=hop_dong,
                        defaults={
                            'luong_co_ban': cd.get('luong_co_ban', 0),
                            'luong_theo_gio': cd.get('luong_theo_gio', 0),
                            'so_gio_lam': cd.get('so_gio_lam', 0),
                            'che_do_thuong': cd.get('thuong', 0),
                        }
                    )
                messages.success(request, 'Cập nhật hợp đồng thành công')
                return redirect('contract_list')
            except Exception as e:
                messages.error(request, f'Lỗi khi cập nhật: {str(e)}')
    
    # Load dữ liệu cũ vào form
    try:
        ct = hop_dong.chi_tiet
        initial_data = {
            'ma_nv': hop_dong.ma_nv_id,
            'loai_hd': hop_dong.loai_hd,
            'ngay_bd': hop_dong.ngay_bat_dau,
            'ngay_kt': hop_dong.ngay_ket_thuc,
            'chuc_vu': hop_dong.chuc_vu,
            'luong_co_ban': ct.luong_co_ban,
            'luong_theo_gio': ct.luong_theo_gio,
            'so_gio_lam': ct.so_gio_lam,
            'thuong': ct.che_do_thuong
        }
    except:
        initial_data = {'ma_nv': hop_dong.ma_nv_id}

    return render(request, 'contracts/contract_edit.html', {
        'contract': hop_dong,
        'form': ContractForm(initial=initial_data),
        'employees': NhanVien.objects.all(),
        'contract_types': ['Full-time', 'Part-time', 'Thời vụ', 'Thử việc'],
        'positions': ['Pha chế', 'Phục vụ', 'Giữ xe']
    })

@require_http_methods(["GET"])
def contract_detail_view(request, contract_id):
    hop_dong = get_object_or_404(HopDongLaoDong, ma_hd=contract_id)
    ct = getattr(hop_dong, 'chi_tiet', None)
    
    data = {
        'ma_hd': hop_dong.ma_hd,
        'ma_nv': hop_dong.ma_nv_id,
        'ten_nv': hop_dong.ma_nv.ho_ten,
        'loai_hd': hop_dong.get_loai_hd_display(),
        'ngay_bd': hop_dong.ngay_bat_dau.strftime('%d/%m/%Y'),
        'ngay_kt': hop_dong.ngay_ket_thuc.strftime('%d/%m/%Y') if hop_dong.ngay_ket_thuc else '',
        'chuc_vu': hop_dong.get_chuc_vu_display(),
        'trang_thai': hop_dong.get_trang_thai_display(),
        'luong_co_ban': ct.luong_co_ban if ct else 0,
        'luong_theo_gio': ct.luong_theo_gio if ct else 0,
        'so_gio_lam': ct.so_gio_lam if ct else 0,
        'thuong': ct.che_do_thuong if ct else 0,
    }
    return JsonResponse(data)


from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework_simplejwt.authentication import JWTAuthentication
from .models import HopDongLaoDong, HopDongLD_CT
from .serializers import HopDongLaoDongSerializer


class HopDongViewSet(viewsets.ModelViewSet):
    serializer_class = HopDongLaoDongSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        user = self.request.user
        if user.is_superuser or user.is_staff:
            return HopDongLaoDong.objects.all()

        # Lọc hợp đồng của nhân viên có user_id khớp với người đang login
        return HopDongLaoDong.objects.filter(ma_nv__taikhoan__user=user)

    def check_admin_permission(self, request):
        """Hàm phụ trợ kiểm tra quyền Quản lý"""
        return request.user.is_superuser or request.user.is_staff

    def create(self, request, *args, **kwargs):
        """Chặn nhân viên thêm mới hợp đồng"""
        if not self.check_admin_permission(request):
            return Response(
                {"detail": "Bạn không có quyền tạo hợp đồng mới. Vui lòng liên hệ Admin."},
                status=status.HTTP_403_FORBIDDEN
            )
        return super().create(request, *args, **kwargs)

    def update(self, request, *args, **kwargs):
        """Chặn nhân viên chỉnh sửa hợp đồng"""
        if not self.check_admin_permission(request):
            return Response(
                {"detail": "Bạn không có quyền chỉnh sửa thông tin hợp đồng."},
                status=status.HTTP_403_FORBIDDEN
            )
        return super().update(request, *args, **kwargs)

    def destroy(self, request, *args, **kwargs):
        """Chặn nhân viên xóa hợp đồng"""
        if not self.check_admin_permission(request):
            return Response(
                {"detail": "Chỉ quản lý mới có quyền xóa hợp đồng khỏi hệ thống."},
                status=status.HTTP_403_FORBIDDEN
            )
        return super().destroy(request, *args, **kwargs)