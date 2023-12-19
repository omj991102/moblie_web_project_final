from django.conf import settings
from django.db import models
from django.utils import timezone
from django.core.validators import FileExtensionValidator

# Create your models here.
class Post(models.Model):
    models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    title = models.CharField(max_length=200)
    author = models.CharField(max_length=200, default='')
    text = models.TextField()
    created_date = models.DateTimeField(default=timezone.now)
    published_date = models.DateTimeField(blank=True, null=True)
    image = models.ImageField(upload_to='intruder_image/%Y/%m/%d/', default='intruder_image/default_error.png')
    video_pc = models.FileField(upload_to='intruder_image/%Y/%m/%d/', null=True, validators=[FileExtensionValidator(allowed_extensions=['MOV','avi','mp4','webm','mkv'])])
    video_mobile = models.FileField(upload_to='intruder_image/%Y/%m/%d/', null=True, validators=[FileExtensionValidator(allowed_extensions=['MOV','avi','mp4','webm','mkv'])])

    def __str__(self):
        return self.title

class Comment(models.Model):
    post = models.ForeignKey('blog.Post', related_name='comments', on_delete=models.CASCADE)
    author = models.CharField(max_length=200, default='')
    text = models.TextField()
    created_date = models.DateTimeField(default=timezone.now)
    approved_comment = models.BooleanField(default=False)

    def approve(self):
        self.approved_comment = True
        self.save()

    def __str__(self):
        return self.text