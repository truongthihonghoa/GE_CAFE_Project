from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import DangKyLichViewSet, NghiPhepViewSet, YeuCauViewSet

router = DefaultRouter()
router.register(r'dangkylich', DangKyLichViewSet, basename='dangkylich')
router.register(r'nghiphep', NghiPhepViewSet, basename='nghiphep')
router.register(r'yeu-cau', YeuCauViewSet, basename='yeu-cau')

urlpatterns = [
    path('', include(router.urls)),
]