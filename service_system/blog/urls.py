from django.urls import path, include
from . import views
from rest_framework import routers

router = routers.DefaultRouter()
router.register('Post', views.IntruderImage)

urlpatterns = [
    path('', views.post_list, name='post_list'),
    path('post/<int:pk>/', views.post_detail, name='post_detail'),
    path('post/new/', views.post_new, name='post_new'),
    path('post/<int:pk>/edit/', views.post_edit, name='post_edit'),
    path('post/<pk>/remove/', views.post_remove, name='post_remove'),
    path('post/<pk>/comment/', views.add_comment_to_post, name='add_comment_to_post'),
    path('api_root/', include(router.urls)),
]