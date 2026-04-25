from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import ChamCongViewSet

router = DefaultRouter()
router.register(r'', ChamCongViewSet)

urlpatterns = [
    path('', include(router.urls)),  # 👈 sửa lại dòng này
]