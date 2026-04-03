from django.contrib import admin
from django.urls import path, include
from django.views.generic import RedirectView

from rest_framework.authtoken import views
urlpatterns = [
    path('', RedirectView.as_view(pattern_name='dashboard', permanent=False)),
    path('admin/', admin.site.urls),
    path('accounts/', include('apps.accounts.urls')),
    path('branches/', include('apps.branches.urls')),
    path('employees/', include('apps.employees.urls')),
    path('contracts/', include('apps.contracts.urls')),
    path('payroll/', include('apps.payroll.urls')),
    path('requests/', include('apps.requests.urls')),
    path('schedules/', include('apps.schedules.urls')),
    path('reports/', include('apps.reports.urls')),
    path('api-token-auth/', views.obtain_auth_token),
    path('api/branches/', include('apps.branches.api_urls')),
    path('api/employees/', include('apps.employees.api_urls')),
    path('api/contracts/', include('apps.contracts.api_urls')),
]
