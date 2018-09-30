package com.example.test.udong;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailClub extends AppCompatActivity {
    String clubNo;
    String userNo;
    TextView nameClub;
    TextView categoryClub;
    TextView countClub;

    TextView areaClub;
    TextView infoClub;
    Button registerClub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailclub);

        nameClub = (TextView)findViewById(R.id.nameClub);
        categoryClub = (TextView)findViewById(R.id.categoryClub);
        countClub = (TextView)findViewById(R.id.countClub);
        areaClub = (TextView)findViewById(R.id.areaClub);
        infoClub = (TextView)findViewById(R.id.infoClub);
        registerClub = (Button)findViewById(R.id.registerClub);

        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");
        clubNo = intent.getStringExtra("clubNo");
        Glide.with(this).load("http://"+getString(R.string.server_url)+"/android/clubimg/club"+clubNo+".jpg").into((ImageView) findViewById(R.id.clubthum));
        String strUrl = "http://"+getString(R.string.server_url)+"/android/detailClub.jsp?userNo="+userNo+"&clubNo="+clubNo;

        registerClub.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                String url = "http://"+getString(R.string.server_url)+"/android/registerClub.jsp?userNo="+userNo+"&clubNo="+clubNo;
                new Register().execute(url);
            }
        });


        new DownloadWebpageTask().execute(strUrl);

    }
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        protected void onPostExecute(String result) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();


                boolean bSet_club = false;
                boolean bSet_clubNo = false;
                boolean bSet_name = false;
                boolean bSet_count = false;
                boolean bSet_category = false;
                boolean bSet_area = false;
                boolean bSet_info = false;
                boolean bSet_isReg = false;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("club"))
                            bSet_club = true;
                        if (tag_name.equals("name"))
                            bSet_name = true;
                        if (tag_name.equals("clubNo"))
                            bSet_clubNo = true;
                        if (tag_name.equals("category"))
                            bSet_category = true;
                        if (tag_name.equals("count"))
                            bSet_count = true;
                        if (tag_name.equals("category"))
                            bSet_category = true;
                        if (tag_name.equals("info"))
                            bSet_info = true;
                        if (tag_name.equals("area"))
                            bSet_area = true;
                        if (tag_name.equals("isReg"))
                            bSet_isReg = true;
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet_club) {

                            if (bSet_name) {
                                nameClub.setText(xpp.getText());

                                bSet_name = false;
                            }
                            if (bSet_count) {
                                countClub.setText(xpp.getText());
                                bSet_count = false;
                            }
                            if (bSet_category) {
                                categoryClub.setText(xpp.getText());
                                bSet_category = false;
                            }
                            if (bSet_area) {
                                areaClub.setText(xpp.getText());
                                bSet_area = false;
                            }
                            if (bSet_info) {
                                infoClub.setText(xpp.getText());
                                bSet_info = false;
                            }
                        }
                        if (bSet_isReg) {
                            if (xpp.getText().equals("yes")) {
                                registerClub.setVisibility(View.GONE);
                            }
                            bSet_isReg = false;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                    }
                    eventType = xpp.next();
                }

            } catch (Exception e) {
                // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }

        private String downloadUrl(String myurl) throws IOException {

            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }
                return page;
            } finally {
                conn.disconnect();
            }
        }
    }

        private class Register extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... urls) {
                try {
                    return (String) downloadUrl((String) urls[0]);
                } catch (IOException e) {
                    return "다운로드 실패";
                }
            }

            protected void onPostExecute(String result) {
                finish();
            }

            private String downloadUrl(String myurl) throws IOException {

                HttpURLConnection conn = null;
                try {
                    URL url = new URL(myurl);
                    conn = (HttpURLConnection) url.openConnection();
                    BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                    String line = null;
                    String page = "";
                    while ((line = bufreader.readLine()) != null) {
                        page += line;
                    }
                    return page;
                } finally {
                    conn.disconnect();
                }
            }
        }


    }
