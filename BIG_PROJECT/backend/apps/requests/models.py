# apps/requests/models.py

from django.db import models

class YeuCau(models.Model):
    ma_yc = models.CharField(max_length=20, primary_key=True)

    loai_yeu_cau = models.CharField(max_length=100)
    ngay_bd = models.DateField()
    ngay_kt = models.DateField()
    ly_do = models.TextField()
    trang_thai = models.CharField(max_length=50)

    ma_nv = models.ForeignKey(
        'employees.NhanVien',
        on_delete=models.CASCADE
    )

