from django.contrib import admin
from .models import ChamCong

@admin.register(ChamCong)
class ChamCongAdmin(admin.ModelAdmin):
    list_display = ('ma_cc', 'ma_nv', 'ngay_lam', 'gio_vao', 'gio_ra', 'trang_thai')
    # BẮT BUỘC có dòng này để bảng BaoCao_CT không bị lỗi admin.E040
    search_fields = ('ma_cc', 'ma_nv__ho_ten')
    list_filter = ('ngay_lam', 'trang_thai')