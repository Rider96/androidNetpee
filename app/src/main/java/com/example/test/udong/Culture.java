package com.example.test.udong;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class Culture extends AppCompatActivity  implements View.OnClickListener {
    String clubNo;
    String userNo;
    String grade;
    String category;
    int id = 1;
    int total;
    int now = 5;
    LinearLayout culture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture);
        culture = (LinearLayout)findViewById(R.id.culture);
        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");
        clubNo = intent.getStringExtra("clubNo");
        grade = intent.getStringExtra("grade");
        category = intent.getStringExtra("category");
        Button btn5 = findViewById(R.id.btn5);
        btn5.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                if (now<total) {

                    int start = now + 1;
                    int end = now + 5;
                    if (end > total){
                        end = total;
                    }
                    String strUrl = "http://openapi.seoul.go.kr:8088/5175734d76646c71333975554b6964/xml/SearchPerformanceBySubjectService/"+start+"/"+end+"/"+category;
                    now = end;
                    new DownloadWebpageTask().execute(strUrl);

                }
            }
        });
        switch (category){
            case "스포츠" :
                category = "6";
                break;
            case "애견" :
                category = "10";
                break;
            case "음악" :
                category = "1";
                break;
            case "음식" :
                category = "10";
                break;
            case "미술" :
                category = "7";
                break;
        }
        String strUrl = "http://openapi.seoul.go.kr:8088/5175734d76646c71333975554b6964/xml/SearchPerformanceBySubjectService/1/5/"+category;
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

                String title = null;
                String start = null;
                String end = null;
                String place = null;
                String imgUrl = null;


                boolean bSet_title = false;
                boolean bSet_start = false;
                boolean bSet_end = false;
                boolean bSet_place = false;
                boolean bSet_imgUrl = false;
                boolean bSet_count = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("list_total_count"))
                            bSet_count = true;
                        if (tag_name.equals("TITLE"))
                            bSet_title = true;
                        if (tag_name.equals("STRTDATE"))
                            bSet_start = true;
                        if (tag_name.equals("END_DATE"))
                            bSet_end = true;
                        if (tag_name.equals("PLACE"))
                            bSet_place = true;
                        if (tag_name.equals("MAIN_IMG"))
                            bSet_imgUrl = true;
                    } else if (eventType == XmlPullParser.TEXT) {
                        if(bSet_count){
                            total = Integer.parseInt(xpp.getText());
                            bSet_count = false;
                        }
                        if(bSet_title){
                            title = xpp.getText();
                            bSet_title = false;
                        }
                        if(bSet_start){
                            start = xpp.getText();
                            bSet_start = false;
                        }
                        if(bSet_end){
                            end = xpp.getText();
                            bSet_end = false;
                        }
                        if(bSet_place){
                            place = xpp.getText();
                            bSet_place = false;
                        }
                        if(bSet_imgUrl){
                            imgUrl = xpp.getText();
                            bSet_imgUrl = false;
                        }


                    } else if (eventType == XmlPullParser.END_TAG) {
                        String tag_name = xpp.getName();
                        if(tag_name.equals("row")){
                            newList(title,start,end,place,imgUrl);
                        }
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
    public void newList(String title,String start,String end,String place,String imgUrl){
        String url = imgUrl;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,15,0,15);
        LinearLayout layout_list = new LinearLayout(this);
        layout_list.setOrientation(LinearLayout.VERTICAL);
        layout_list.setLayoutParams(params);
        int roundRadius = 15; // 8px not dp
        int fillColor = Color.parseColor("#ffffff");

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        layout_list.setBackground(gd);
        layout_list.setHorizontalGravity(1);
        layout_list.setPadding(10,10,10,10);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.WRAP_CONTENT, 400);
        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(params2);
        //imageView.setTag("gg");
        Glide.with(this).load(imgUrl.toLowerCase()).into(imageView);




        layout_list.addView(imageView);


        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(this);
        textView.setLayoutParams(params3);
        textView.setPadding(10,10,10,10);
        textView.setText(title);
        //textView.setTag("title");
        layout_list.addView(textView);

        textView = new TextView(this);
        textView.setLayoutParams(params3);
        textView.setText(place);
        //textView.setTag("place");
        layout_list.addView(textView);
        textView = new TextView(this);
        textView.setLayoutParams(params3);
        //textView.setTag("date");
        textView.setText(start + " ~ " + end);


        layout_list.addView(textView);

        textView = new TextView(this);
        textView.setLayoutParams(params3);
        //textView.setTag("date");
        textView.setText(imgUrl.toLowerCase());
        textView.setVisibility(View.INVISIBLE);

        layout_list.addView(textView);

        layout_list.setId(id);
        id = id + 1;
        layout_list.setOnClickListener(this);
        culture.addView(layout_list);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        String imgUrl;
        String title;
        String date;
        String place;
        LinearLayout layout = (LinearLayout)findViewById(id);

        View view_1 = layout.getChildAt(1);
        TextView textView = (TextView)view_1;
        title = textView.getText().toString();

        view_1 = layout.getChildAt(2);
        textView = (TextView)view_1;
        place = textView.getText().toString();

        view_1 = layout.getChildAt(3);
        textView = (TextView)view_1;
        date = textView.getText().toString();

        view_1 = layout.getChildAt(4);
        textView = (TextView)view_1;
        imgUrl = textView.getText().toString();

        Intent it = new Intent(Culture.this, AddCulture.class);
        it.putExtra("userNo",userNo);
        it.putExtra("clubNo",clubNo);
        it.putExtra("title",title);
        it.putExtra("place",place);
        it.putExtra("date",date);
        it.putExtra("imgUrl",imgUrl);
        startActivity(it);

    }
}
