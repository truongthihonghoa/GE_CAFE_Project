from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import YeuCauViewSet, DangKyLichViewSet, NghiPhepViewSet

router = DefaultRouter()
router.register(r'yeu-cau', YeuCauViewSet, basename='yeu-cau')
router.register(r'dang-ky-lich', DangKyLichViewSet, basename='dang-ky-lich')
router.register(r'nghi-phep', NghiPhepViewSet, basename='nghi-phep')

urlpatterns = [
    path('', include(router.urls)),
]
