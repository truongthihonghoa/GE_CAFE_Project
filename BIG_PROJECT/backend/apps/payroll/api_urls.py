from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .api_views import LuongViewSet

router = DefaultRouter()
router.register(r'', LuongViewSet, basename='payroll')

urlpatterns = [
    path('', include(router.urls)),
]
