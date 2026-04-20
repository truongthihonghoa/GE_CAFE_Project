from django.urls import path, include
from rest_framework.routers import SimpleRouter
from .views import NhanVienViewSet

router = SimpleRouter()
router.register(r'', NhanVienViewSet, basename='employees')

urlpatterns = [
    path('', include(router.urls)),
]
