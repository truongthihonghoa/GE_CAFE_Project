from django.urls import path, include
from . import views
from rest_framework.routers import DefaultRouter
#
from .views import TaiKhoanViewSet
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView
app_name = 'accounts'
router = DefaultRouter()
# Bạn nên đặt tên cho router để tránh bị trống URL, ví dụ 'taikhoan'
router.register(r'accounts', TaiKhoanViewSet, basename='accounts')

urlpatterns = [
    path('', include(router.urls)),
    path('login/', views.login_view, name='login'),

    # --- THÊM LẠI 2 DÒNG NÀY ---
    path('api/login/', TokenObtainPairView.as_view(), name='token_obtain_pair'),
    path('api/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
    # ---------------------------
]