from django.urls import path, include
from rest_framework.routers import SimpleRouter
from .views import HopDongViewSet

router = SimpleRouter()
router.register(r'', HopDongViewSet, basename='contracts')

urlpatterns = [
    path('', include(router.urls)),
]
