from django.urls import path, include
from . import views
from rest_framework.routers import DefaultRouter
from .views import ChiNhanhViewSet

app_name = 'branches'
router = DefaultRouter()
router.register(r'branches', ChiNhanhViewSet)

urlpatterns = [

    path('admin/', views.branch_list, name='branch_admin'),
    path('', views.branch_list, name='branch_list'),
    path('', include(router.urls)),
    path('create/', views.branch_create, name='branch_create'),
    path('edit/<str:pk>/', views.branch_update, name='branch_update'),
    path('delete/<str:pk>/', views.branch_delete, name='branch_delete'),
]
