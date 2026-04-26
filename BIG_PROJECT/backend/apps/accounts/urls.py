from django.urls import path, include
from . import views
from rest_framework.routers import DefaultRouter
#
from .views import TaiKhoanViewSet
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView
app_name = 'accounts'
router = DefaultRouter()
# Bạn nên đặt tên cho router để tránh bị trống URL, ví dụ 'taikhoan'
router.register(r'taikhoan', TaiKhoanViewSet, basename='taikhoan')

urlpatterns = [
    path('', include(router.urls)),
    path('login/', views.login_view, name='login'),

    # --- THÊM LẠI 2 DÒNG NÀY ---
    path('api/login/', TokenObtainPairView.as_view(), name='token_obtain_pair'),
    path('api/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
    # ---------------------------
]


Lời khuyên cho bạn: Bạn nên yêu cầu người làm Backend sửa lại API /api/login/ để trả về thêm trường ma_nv (mã nhân viên). Đây là cách tối ưu nhất, giúp App chạy nhanh hơn và tránh việc phải xử lý quá nhiều logic phức tạp dưới Android, đồng thời giảm tải cho server.