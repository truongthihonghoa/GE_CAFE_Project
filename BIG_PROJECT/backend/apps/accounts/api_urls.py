from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import TaiKhoanViewSet

router = DefaultRouter()
router.register(r'taikhoan', TaiKhoanViewSet, basename='taikhoan')

urlpatterns = [
    path('', include(router.urls)),
]