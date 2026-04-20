# apps/schedules/models.py

from django.db import models

class LichLamViec(models.Model):
    ma_llv = models.CharField(max_length=20, primary_key=True)
    ngay_lam = models.DateField()
    ca_lam = models.CharField(max_length=50)
    trang_thai = models.CharField(max_length=50)
    ngay_tao = models.DateField()
    ghi_chu = models.TextField(blank=True, null=True)
    ma_chi_nhanh = models.ForeignKey(
        'branches.ChiNhanh',
        on_delete=models.CASCADE,
        default='CN01',
        related_name='lich_lam_viec'
    )
    # Nhiều nhân viên, mỗi người có 1 vị trí riêng thông qua bảng ChiTietLichLamViec
    ma_nv = models.ManyToManyField(
        'employees.NhanVien',
        through='ChiTietLichLamViec',
        related_name='lich_lam_viec'
    )

    class Meta:
        verbose_name = "Lịch làm việc"
        verbose_name_plural = "Lịch làm việc"

class ChiTietLichLamViec(models.Model):
    ma_llv = models.ForeignKey(LichLamViec, on_delete=models.CASCADE)
    ma_nv = models.ForeignKey('employees.NhanVien', on_delete=models.CASCADE)
    vi_tri = models.CharField(max_length=50, verbose_name="Vị trí làm việc")

    class Meta:
        unique_together = ('ma_llv', 'ma_nv')