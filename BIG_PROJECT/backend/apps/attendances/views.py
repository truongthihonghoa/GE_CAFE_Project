from rest_framework.viewsets import ModelViewSet
from rest_framework.response import Response
from rest_framework import permissions
from django.utils import timezone
from datetime import datetime, time
from .serializers import ChamCongSerializer


from .models import ChamCong


class ChamCongViewSet(ModelViewSet):
    queryset = ChamCong.objects.all()
    serializer_class = ChamCongSerializer
    permission_classes = [permissions.AllowAny]

    def create(self, request):
        ma_nv = request.data.get('ma_nv')

        if not ma_nv:
            return Response({"error": "Thiếu mã nhân viên"}, status=400)

        today = timezone.now().date()
        now_time = timezone.now().time()

        record = ChamCong.objects.filter(
            ma_nv=ma_nv,
            ngay_lam=today
        ).first()

        if record is None:
            new_record = ChamCong.objects.create(
                ma_cc=f"CC_{ma_nv}_{today}",
                ma_nv_id=ma_nv,
                ngay_lam=today,
                gio_vao=now_time
            )
            return Response({"message": "Check-in thành công"})

        else:
            if record.gio_ra is None:
                record.gio_ra = now_time
                t1 = datetime.combine(today, record.gio_vao)
                t2 = datetime.combine(today, record.gio_ra)
                record.so_gio_lam = (t2 - t1).seconds / 3600
                record.save()

                return Response({"message": "Check-out thành công"})

            return Response({"message": "Đã chấm công đủ"})