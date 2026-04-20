from rest_framework import viewsets, permissions
from .models import Luong
from .serializers import LuongSerializer

class LuongViewSet(viewsets.ModelViewSet):
    queryset = Luong.objects.all().order_by('-nam', '-thang')
    serializer_class = LuongSerializer
    permission_classes = [permissions.AllowAny]

    def get_queryset(self):
        queryset = super().get_queryset()
        thang = self.request.query_params.get('month')
        nam = self.request.query_params.get('year')
        ma_chi_nhanh = self.request.query_params.get('branch')

        if thang:
            queryset = queryset.filter(thang=thang)
        if nam:
            queryset = queryset.filter(nam=nam)
        if ma_chi_nhanh:
            queryset = queryset.filter(chi_nhanh=ma_chi_nhanh)
            
        return queryset
