from django.urls import path, include
from rest_framework.routers import DefaultRouter
from . import views

# 1. Khai báo Router cho API
router = DefaultRouter()
router.register(r'api', views.ScheduleAPIViewSet, basename='schedule-api')

urlpatterns = [
    # Các đường dẫn Giao diện của nhóm (Giữ nguyên)
    path('', views.schedule_list_view, name='schedule_list'),
    path('create/', views.schedule_create_view, name='schedule_create'),
    path('edit/<str:schedule_id>/', views.schedule_edit_view, name='schedule_edit'),
    path('delete/<str:schedule_id>/', views.schedule_delete_view, name='schedule_delete'),
    path('detail/<str:schedule_id>/', views.schedule_detail_view, name='schedule_detail'),
    path('send-notification/', views.schedule_send_notification_view, name='schedule_send_notification'),

    # 2. Thêm đường dẫn API của bạn vào đây
    path('data/', include(router.urls)),
]