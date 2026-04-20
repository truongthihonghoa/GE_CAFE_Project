from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import TaiKhoanViewSet

router = DefaultRouter()
router.register(r'accounts', TaiKhoanViewSet, basename='accounts')

urlpatterns = [
    path('', include(router.urls)),
]