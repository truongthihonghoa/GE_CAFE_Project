from django.db import models


class ChamCong(models.Model):
    TRANG_THAI_CHOICES = [
        ('DUNG_GIO', 'Đúng giờ'),
        ('DI_MUON', 'Đi muộn'),
    ]

    ma_cc = models.CharField(max_length=20, primary_key=True)

    # Liên kết nhân viên
    ma_nv = models.ForeignKey(
        'employees.NhanVien',
        on_delete=models.CASCADE,
        related_name='cham_cong'
    )

    ngay_lam = models.DateField()
    gio_vao = models.TimeField(null=True, blank=True)
    gio_ra = models.TimeField(null=True, blank=True)

    # Các trường để tính toán cho báo cáo
    so_gio_lam = models.FloatField(default=0)
    trang_thai = models.CharField(
        max_length=20,
        choices=TRANG_THAI_CHOICES,
        default='DUNG_GIO'
    )

    ghi_chu = models.TextField(null=True, blank=True)

    def __str__(self):
        return f"{self.ma_nv.ho_ten} - {self.ngay_lam}"