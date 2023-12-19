import os
import cv2
import pathlib
import requests
from datetime import datetime

class ChangeDetection:
    result_prev = []
    HOST = 'http://omj1102.pythonanywhere.com'
    username = 'admin'
    password = 'asdf1234'
    token = ''
    title = 'bird'
    text = 'bird found!'

    def __init__(self, names):
        self.result_prev = [0 for i in range(len(names))]

        res = requests.post(self.HOST + '/api-token-auth/', {
            'username': self.username,
            'password': self.password,
        })
        res.raise_for_status()
        self.token = res.json()['token']
        print(self.token)

    def send(self, image_path, video_pc_path):
        now = datetime.now()
        now.isoformat()
        
        headers = {'Authorization' : 'JWT ' + self.token, 'Accept' : 'application/json'}
        
        data = {
            'title' : self.title,
            'author' : self.username,
            'text' : self.text,
            'created_date' : now,
            'published_date' : now
        }
        file = {
            'image' : open(image_path, 'rb'),
            'video_pc' : open(video_pc_path, 'rb'),
            'video_mobile' : open(str(video_pc_path).replace("_1.mp4", ".mp4"), 'rb')
            }
        res = requests.post(self.HOST + '/api_root/Post/', data=data, files=file, headers=headers)
        print(res)