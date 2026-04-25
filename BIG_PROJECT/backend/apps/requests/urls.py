from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import DangKyLichViewSet, NghiPhepViewSet
from . import views

router = DefaultRouter()
router.register(r'dangkylich', DangKyLichViewSet, basename='dangkylich')
router.register(r'nghiphep', NghiPhepViewSet, basename='nghiphep')

urlpatterns = [
    path('', include(router.urls)),
    path('', views.request_list_view, name='request_list'),
    path('review/', views.request_review_list_view, name='request_review_list'),
]
