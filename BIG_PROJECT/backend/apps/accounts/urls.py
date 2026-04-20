from django.urls import path, include
from . import views
from rest_framework.routers import DefaultRouter
#
from .views import TaiKhoanViewSet

app_name = 'accounts'
router = DefaultRouter()
router.register(r'', TaiKhoanViewSet, basename='')

urlpatterns = [
    path('', include(router.urls)),
    path('login/', views.login_view, name='login'),
    # path('logout/', views.logout_view, name='logout'),
    # path('dashboard/', views.dashboard_view, name='dashboard'),
    # path('employee/', views.account_employee_list_view, name='account_employee_list'),
    # path('admin/', views.account_admin_list_view, name='account_admin_list'),
]