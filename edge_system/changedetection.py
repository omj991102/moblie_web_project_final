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
    title = ''
    text = ''

    def __init__(self, names):
        self.result_prev = [0 for i in range(len(names))]

        res = requests.post(self.HOST + '/api-token-auth/', {
            'username': self.username,
            'password': self.password,
        })
        res.raise_for_status()
        self.token = res.json()['token']
        print(self.token)

    def add(self, detected_current, save_dir, image):
        self.title = ''
        self.text = ''
        change_flag = 0
        
        if self.result_prev[14]==0 and detected_current[14]==1:
            change_flag = 1
            self.title = "bird"
            self.text = "bird found!"

        self.result_prev = detected_current[:]

        if change_flag==1:
            self.send(save_dir, image)

    def send(self, save_dir, image):
        now = datetime.now()
        now.isoformat()

        today = datetime.now()
        save_path = os.getcwd() / save_dir / 'detected' / str(today.year) / str(today.month) / str(today.day)
        pathlib.Path(save_path).mkdir(parents=True, exist_ok=True)

        full_path = save_path / '{0}-{1}-{2}-{3}.jpg'.format(today.hour,today.minute,today.second,today.microsecond)

        dst = cv2.resize(image, dsize=(320, 240), interpolation=cv2.INTER_AREA)
        cv2.imwrite(full_path, dst)
        
        headers = {'Authorization' : 'JWT ' + self.token, 'Accept' : 'application/json'}
        
        data = {
            'title' : self.title,
            'author' : self.username,
            'text' : self.text,
            'created_date' : now,
            'published_date' : now
        }
        file = {'image' : open(full_path, 'rb')}
        res = requests.post(self.HOST + '/api_root/Post/', data=data, files=file, headers=headers)
        print(res)