

from django.db import models


class BaoCao(models.Model):
   ma_bc = models.CharField(max_length=20, primary_key=True)


   ngay_bd = models.DateField()
   ngay_kt = models.DateField()
   ngay_tao = models.DateField()
   tong = models.FloatField()


   ma_tk = models.ForeignKey(
       'accounts.TaiKhoan',
       on_delete=models.CASCADE
   )




class BaoCao_CT(models.Model):
   ma_nv = models.ForeignKey('employees.NhanVien', on_delete=models.CASCADE)


   # trỏ sang bảng CHẤM CÔNG (vừa tạo)
   ma_cc = models.ForeignKey(
       'attendances.ChamCong',
       on_delete=models.CASCADE,
       null=True,
       blank=True
   )
   ma_bc = models.ForeignKey(BaoCao, on_delete=models.CASCADE)


   ten_nv = models.CharField(max_length=255)
   so_gio_lam = models.FloatField()
   so_ca_lam = models.FloatField()

   dung_gio = models.FloatField(default=0)
   di_muon = models.FloatField(default=0)



   ghi_chu = models.TextField(null=True, blank=True)


   class Meta:
       unique_together = ('ma_nv', 'ma_cc', 'ma_bc')
