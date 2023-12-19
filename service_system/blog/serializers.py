from blog.models import Post
from rest_framework import serializers

class PostSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Post
        fields = ('title', 'author', 'text', 'created_date', 'published_date', 'image', 'video_pc', 'video_mobile')