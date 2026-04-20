from django.contrib import admin
from .models import Luong

@admin.register(Luong)
class LuongAdmin(admin.ModelAdmin):

    list_display = (
        'ma_luong',
        'nhan_vien',
        'chi_nhanh',
        'thang',
        'nam',
        'tong_luong',
        'trang_thai'
    )

    search_fields = (
        'ma_luong',
        'nhan_vien__ten_nv'   # tìm theo tên nhân viên
    )

    list_filter = (
        'trang_thai',
        'thang',
        'nam',
        'chi_nhanh'
    )

    raw_id_fields = ('nhan_vien',)
    autocomplete_fields = ('nhan_vien', 'chi_nhanh')

    ordering = ('-created_at',)