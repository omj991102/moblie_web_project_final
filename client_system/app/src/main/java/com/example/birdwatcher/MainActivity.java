package com.example.birdwatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainActivity extends AppCompatActivity {
    ImageView imgView;
    TextView owner;
    String url = "http://omj1102.pythonanywhere.com";
    String vidURL = "";
    String json = "";
    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<String> imgUrls = new ArrayList<>();
    ArrayList<String> vdoUrls = new ArrayList<>();
    int index = 0;
    Bitmap bmImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = findViewById(R.id.imgView);
        owner = findViewById(R.id.textView);
    }
    public void onClickForLoadAll(View v) {
        new Thread(() -> {
            json = getJson(url);

            try {
                imgUrls.clear();
                vdoUrls.clear();
                imgUrls = getList(json, false, "image");
                vdoUrls = getList(json, false, "video_mobile");
            } catch (Exception e) {
                e.printStackTrace();
            }
            showImg(imgUrls, imgUrls.size()-1);
            index = imgUrls.size()-1;
            if (nameList.size() != 0) { owner.setText(nameList.get(index)+"의 사진"); }
            else owner.setText("게시자");
        }).start();
    }
    public void onClickForLoadOne(View v) {
        new Thread(() -> {
            json = getJson(url);

            try {
                imgUrls.clear();
                vdoUrls.clear();
                imgUrls = getList(json, true, "image");
                vdoUrls = getList(json, true, "video_mobile");
            } catch (Exception e) {
                e.printStackTrace();
            }
            showImg(imgUrls, imgUrls.size()-1);
            index = imgUrls.size()-1;
            if (nameList.size() != 0) { owner.setText(nameList.get(index)+"의 사진"); }
            else owner.setText("게시자");
        }).start();
    }
    public void onClickForPrev(View v) {
        if (index+1 >= imgUrls.size())
            Toast.makeText(getApplicationContext(), "이전 사진이 없습니다", Toast.LENGTH_LONG).show();
        else
        {
            showImg(imgUrls, ++index);
            if (nameList.size() != 0) { owner.setText(nameList.get(index)+"의 사진"); }
            else owner.setText("게시자");
        }
    }
    public void onClickForNext(View v)
    {
        if (index-1 < 0)
            Toast.makeText(getApplicationContext(), "다음 사진이 없습니다", Toast.LENGTH_LONG).show();
        else
        {
            showImg(imgUrls, --index);
            if (nameList.size() != 0) { owner.setText(nameList.get(index)+"의 사진"); }
            else owner.setText("게시자");
        }
    }
    public void toVideo(View v)
    {
        Log.d("vid", vdoUrls.toString());
        Intent intent = new Intent(MainActivity.this, VideoActivity.class);
        vidURL = vdoUrls.get(index).toString();
        intent.putExtra("vidURL", vidURL);

        startActivity(intent);
    }
    protected String getJson(String url)
    {
        String json = "";
        try {
            URL jsonUrl = new URL(url+"/api_root/Post");
            HttpURLConnection conn = (HttpURLConnection) jsonUrl.openConnection();
            if(conn != null)
            {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                int resCode = conn.getResponseCode();
                if(resCode == HttpURLConnection.HTTP_OK)
                {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    json = reader.readLine();

                    reader.close();
                }
                conn.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    protected ArrayList<String> getList(String json, boolean one, String key) throws ParseException, JSONException
    {
        nameList.clear();
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(json);
        ArrayList<String> urlList = new ArrayList<>();

        if (one)
        {
            EditText editText = findViewById(R.id.editText);
            for (int i = 0; i < jsonArray.size(); i++)
            {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String name = (String) jsonObject.get("author");

                if (name.equals(editText.getText().toString()))
                {
                    nameList.add(name);
                    urlList.add((String) jsonObject.get(key));
                }
            }
        }
        else {
            for (int i = 0; i < jsonArray.size(); i++)
            {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                nameList.add((String) jsonObject.get("author"));
                urlList.add((String) jsonObject.get(key));
            }
        }
        return urlList;
    }

    protected void showImg(ArrayList<String> list, int index)
    {
        if (imgUrls.size() != 0) {
            new Thread() {
                @Override
                public void run() {
                try {
                    URL myFileUrl = new URL(list.get(index));
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();

                    bmImg = BitmapFactory.decodeStream(is);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { imgView.setImageBitmap(bmImg); }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            }.start();
        }
    }
}