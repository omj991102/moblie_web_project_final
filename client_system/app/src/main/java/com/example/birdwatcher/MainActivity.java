package com.example.birdwatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
    String url = "http://omj1102.pythonanywhere.com";
    String json = "";
    ArrayList<String> imgUrls = new ArrayList<>();
    int index = 0;
    Bitmap bmImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = findViewById(R.id.imgView);
    }
    public void onClickForLoadAll(View v) {
        new Thread(() -> {
            json = getJson(url);

            try {
                imgUrls = getList(json, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            showImg(imgUrls, imgUrls.size()-1);
            index = imgUrls.size()-1;
        }).start();
    }
    public void onClickForLoadMine(View v) {
        new Thread(() -> {
            json = getJson(url);

            try {
                imgUrls = getList(json, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            showImg(imgUrls, imgUrls.size()-1);
            index = imgUrls.size()-1;
        }).start();
    }
    public void onClickForPrev(View v) {
        if (index+1 >= imgUrls.size())
            Toast.makeText(getApplicationContext(), "이전 사진이 없습니다", Toast.LENGTH_LONG).show();
        else
            showImg(imgUrls, ++index);
    }
    public void onClickForNext(View v)
    {
        if (index-1 < 0)
            Toast.makeText(getApplicationContext(), "다음 사진이 없습니다", Toast.LENGTH_LONG).show();
        else
            showImg(imgUrls, --index);
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

    protected ArrayList<String> getList(String json, boolean mine) throws ParseException, JSONException
    {
        imgUrls.clear();

        Log.d("JsonParsing", json);
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(json);
        ArrayList<String> urlList = new ArrayList<>();

        if (mine)
        {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String name = (String) jsonObject.get("author");

                if (name.equals("admin"))
                    urlList.add((String) jsonObject.get("image"));
            }
        }
        else {
            for (int i = 0; i < jsonArray.size(); i++)
            {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                urlList.add((String) jsonObject.get("image"));
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
                            public void run() {
                                imgView.setImageBitmap(bmImg);
                            }
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