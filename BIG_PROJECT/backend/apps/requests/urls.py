from django.urls import path

from . import views


urlpatterns = [
    path('', views.request_list_view, name='request_list'),
    path('review/', views.request_review_list_view, name='request_review_list'),
]
