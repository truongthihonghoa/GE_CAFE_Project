from django.urls import path, include
from rest_framework.routers import SimpleRouter
from .views import ChiNhanhViewSet

router = SimpleRouter()
router.register(r'', ChiNhanhViewSet, basename='branches')

urlpatterns = [
    path('', include(router.urls)),
]
