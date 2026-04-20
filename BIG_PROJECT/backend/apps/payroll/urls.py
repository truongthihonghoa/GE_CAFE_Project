from django.urls import path, include

from . import views


urlpatterns = [
    path('', views.payroll_list_view, name='payroll_list'),
    path('export/', views.payroll_export_view, name='payroll_export'),
    path('data/api/', include('apps.payroll.api_urls')),
]
