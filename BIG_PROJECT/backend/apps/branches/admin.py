from django.contrib import admin
from .models import ChiNhanh

# Register your models here.
@admin.register(ChiNhanh)
class ChiNhanhAdmin(admin.ModelAdmin):
    list_display = ('ma_chi_nhanh', 'ten_chi_nhanh', 'dia_chi', 'sdt', 'ma_nv_ql')
    search_fields = ('ma_chi_nhanh', 'ten_chi_nhanh')
    list_filter = ('ma_nv_ql',)
    raw_id_fields = ('ma_nv_ql',)