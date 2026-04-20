from django.contrib import admin
from .models import YeuCau

# Register your models here.
@admin.register(YeuCau)
class YeuCauAdmin(admin.ModelAdmin):
    list_display = ('ma_yc', 'loai_yeu_cau', 'ngay_bd', 'ngay_kt', 'ly_do', 'trang_thai')
    search_fields = ('ma_yc', 'loai_yeu_cau')
    list_filter = ('loai_yeu_cau', 'trang_thai')
    raw_id_fields = ('ma_nv',)
    autocomplete_fields = ('ma_nv',)
    ordering = ('ma_yc',)