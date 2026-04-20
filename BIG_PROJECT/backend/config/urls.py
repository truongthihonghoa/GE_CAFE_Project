from django.contrib import admin
from django.urls import path, include
from django.views.generic import RedirectView
from apps.accounts.views import CustomObtainAuthToken

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', RedirectView.as_view(url='api/employees/')),
    path('api/employees/', include('apps.employees.api_urls')),
    path('api/payroll/', include('apps.payroll.urls')),
    path('api/accounts/', include('apps.accounts.urls')),
    path('api/contracts/', include('apps.contracts.api_urls')),
    path('api/attendances/', include('apps.attendances.urls')),
    path('api/requests/', include('apps.requests.urls')),
    path('api/branches/', include('apps.branches.api_urls')),
    path('api/schedules/', include('apps.schedules.urls')),
    path('api-token-auth/', CustomObtainAuthToken.as_view()),
]
