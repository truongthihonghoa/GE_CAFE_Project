from django.contrib import admin
from .models import LichLamViec

# Register your models here.
@admin.register(LichLamViec)
class LichLamViecAdmin(admin.ModelAdmin):
    list_display = ('ma_llv', 'ngay_lam', 'ca_lam', 'ghi_chu', 'ma_chi_nhanh', 'trang_thai', 'ngay_tao')
    search_fields = ('ma_llv', 'ngay_lam')
    list_filter = ('ca_lam', 'trang_thai')
    ordering = ('ma_llv',)



