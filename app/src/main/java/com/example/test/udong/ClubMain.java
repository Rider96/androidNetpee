package com.example.test.udong;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ClubMain extends AppCompatActivity  implements View.OnClickListener {
    String title;
    String nickname;
    String postNo;
    String clubNo;
    String userNo;
    String grade;
    String category;
    int test = 0;
    Button startEdit;
    LinearLayout posts;
    LinearLayout notice_lay;
    int id = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubmain);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        posts = (LinearLayout)findViewById(R.id.posts);
        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");
        clubNo = intent.getStringExtra("clubNo");
        notice_lay = findViewById(R.id.notice);
        //Toast.makeText(getApplicationContext(), userNo +"_"+clubNo, Toast.LENGTH_LONG).show();
        ImageView imageView = findViewById(R.id.mainimg);
      /*  startEdit = (Button)findViewById(R.id.startedit);
        startEdit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent it_edit = new Intent(ClubMain.this, AddPost.class);
                it_edit.putExtra("userNo",userNo);
                it_edit.putExtra("clubNo",clubNo);
                startActivity(it_edit);
            }
        });*/

        Glide.with(this).load("http://"+getString(R.string.server_url)+"/android/clubimg/club"+clubNo+".jpg").into(imageView);

        String strUrl = "http://"+getString(R.string.server_url)+"/android/clubMain.jsp?clubNo="+clubNo+"&userNo="+userNo;
        new DownloadWebpageTask().execute(strUrl);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_clubmain, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.editPlus:
                Intent it_edit = new Intent(ClubMain.this, AddPost.class);
                it_edit.putExtra("userNo",userNo);
                it_edit.putExtra("clubNo",clubNo);
                it_edit.putExtra("grade",grade);
                startActivity(it_edit);

                break;
            case R.id.cul:
                Intent it = new Intent(ClubMain.this, Culture.class);
                it.putExtra("clubNo",clubNo);
                it.putExtra("userNo",userNo);
                it.putExtra("grade",grade);
                it.putExtra("category",category);
                startActivity(it);

                break;
            case R.id.re:

                Intent intent = new Intent(ClubMain.this,ClubMain.class);
                intent.putExtra("userNo",userNo);
                intent.putExtra("clubNo",clubNo);
                finish();
                startActivity(intent);
                break;

        }
        return true;
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

                String c_title = "";
                String c_content= "";
                String title_c= "";
                String place= "";
                String date= "";
                String imgurl= "";

                boolean bSet_nickname = false;
                boolean bSet_category = false;
                boolean bSet_title = false;
                boolean bSet_postNo = false;
                boolean bSet_post = false;
                boolean bSet_notice = false;
                boolean bSet_clubName = false;
                boolean bSet_grade = false;

                boolean bSet_c_title = false;
                boolean bSet_c_content = false;
                boolean bSet_title_c = false;
                boolean bSet_place = false;
                boolean bSet_date = false;
                boolean bSet_imgurl = false;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("post"))
                            bSet_post = true;
                        if (tag_name.equals("category"))
                            bSet_category = true;
                        if (tag_name.equals("notice"))
                            bSet_notice = true;
                        if (tag_name.equals("nickname"))
                            bSet_nickname = true;
                        if (tag_name.equals("title"))
                            bSet_title = true;
                        if (tag_name.equals("postNo"))
                            bSet_postNo = true;
                        if (tag_name.equals("clubName"))
                            bSet_clubName = true;
                        if (tag_name.equals("grade"))
                            bSet_grade = true;
                        if (tag_name.equals("c_title"))
                            bSet_c_title = true;
                        if (tag_name.equals("c_content"))
                            bSet_c_content = true;
                        if (tag_name.equals("title_c"))
                            bSet_title_c = true;
                        if (tag_name.equals("place"))
                            bSet_place = true;
                        if (tag_name.equals("date"))
                            bSet_date = true;
                        if (tag_name.equals("imgurl"))
                            bSet_imgurl = true;
                        if (tag_name.equals("grade"))
                            bSet_grade = true;
                    } else if (eventType == XmlPullParser.TEXT) {

                        if(bSet_c_title){
                            c_title = xpp.getText();
                            bSet_c_title = false;
                        }
                        if(bSet_c_content){
                            c_content = xpp.getText();
                            bSet_c_content = false;
                        }
                        if(bSet_title_c){
                            title_c = xpp.getText();
                            bSet_title_c = false;
                        }
                        if(bSet_place){
                            place = xpp.getText();
                            bSet_place = false;
                        }
                        if(bSet_date){
                            date = xpp.getText();
                            bSet_date = false;
                        }
                        if(bSet_imgurl){
                            imgurl = xpp.getText();
                            bSet_imgurl = false;
                        }



                        if(bSet_clubName){
                            setTitle(xpp.getText());
                            bSet_clubName = false;
                        }
                        if(bSet_category){
                            category = xpp.getText();
                            bSet_category = false;
                        }
                        if(bSet_grade){
                            grade = xpp.getText();
                            bSet_grade = false;
                        }
                        if (bSet_post) {

                            if (bSet_nickname) {
                                nickname = xpp.getText();

                                bSet_nickname = false;
                            }
                            if(bSet_title){
                                title = xpp.getText();
                                bSet_title = false;
                            }
                            if(bSet_postNo){
                                postNo = xpp.getText();
                                bSet_postNo = false;
                            }
                        }
                        if (bSet_notice) {

                            if (bSet_nickname) {
                                nickname = xpp.getText();

                                bSet_nickname = false;
                            }
                            if(bSet_title){
                                title = xpp.getText();
                                bSet_title = false;
                            }
                            if(bSet_postNo){
                                postNo = xpp.getText();
                                bSet_postNo = false;
                            }
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        String tag_name = xpp.getName();
                        if(tag_name.equals("post")){
                            newPost();
                            bSet_post = false;

                        }
                        if(tag_name.equals("culture")){
                            newCulture(c_title,c_content,title_c,place,date,imgurl);
                        }
                        if(tag_name.equals("notice")){
                            newNotice();
                            bSet_notice = false;
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
    public void newPost(){
/*
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        LinearLayout layout_list = new LinearLayout(this);
        layout_list.setOrientation(LinearLayout.HORIZONTAL);
        layout_list.setLayoutParams(params);
        layout_list.setPadding(20,20,20,0);
        int strokeWidth = 5; // 3px not dp
        int roundRadius = 15; // 8px not dp
        int strokeColor = Color.parseColor("#2E3135");
        int fillColor = Color.parseColor("#f0f0f0");

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        layout_list.setBackground(gd);
        layout_list.setPadding(5,5,5,5);

        TextView tv_nickname = new TextView(this);
        tv_nickname.setText(nickname);

        TextView tv_title = new TextView(this);
        tv_title.setText(title);
        tv_title.setTextSize(20);

        layout_list.addView(tv_nickname);
        layout_list.addView(tv_title);




        layout_list.setTag(postNo);
        layout_list.setId(Integer.parseInt(postNo));
        layout_list.setOnClickListener(this);


        posts.addView(layout_list);

*/
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
        layout_list.setPadding(10,10,10,10);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout layout_title = new LinearLayout(this);
        layout_title.setOrientation(LinearLayout.HORIZONTAL);
        layout_title.setLayoutParams(params1);
        gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#53003c"));
        gd.setCornerRadius(roundRadius);
        //layout_title.setBackground(gd);
        TextView tv_title = new TextView(this);
        tv_title.setTextColor(Color.parseColor("#53003c"));
        tv_title.setTextSize(20);

        tv_title.setText(title);


        TextView tv_user = new TextView(this);
        tv_user.setTextColor(Color.parseColor("#53003c"));
        tv_user.setTextSize(14);

        tv_user.setText(" by"+nickname);

        layout_title.addView(tv_title);
        layout_title.addView(tv_user);

        layout_list.addView(layout_title);

        layout_list.setTag(postNo);
        layout_list.setId(Integer.parseInt(postNo));
        layout_list.setOnClickListener(this);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins(10,10,10,10);
        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(params2);
        Glide.with(this).load("http://"+getString(R.string.server_url)+"/android/postimg/"+postNo+"_"+1+".jpg").into(imageView);
        layout_list.addView(imageView);

        posts.addView(layout_list);

    }
    public void newNotice(){


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,15,0,15);

        LinearLayout layout_list = new LinearLayout(this);
        layout_list.setOrientation(LinearLayout.HORIZONTAL);
        layout_list.setLayoutParams(params);
        int roundRadius = 15; // 8px not dp
        int fillColor = Color.parseColor("#ffffff");

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        layout_list.setBackground(gd);
        layout_list.setPadding(10,10,10,10);

        TextView tv_notice = new TextView(this);
        tv_notice.setText("공지 ");
        tv_notice.setTextColor(Color.parseColor("#ff2222"));
        tv_notice.setTextSize(20);

        TextView tv_title = new TextView(this);
        tv_title.setText(title);
        tv_title.setTextSize(20);

        layout_list.addView(tv_notice);
        layout_list.addView(tv_title);




        layout_list.setTag(postNo);
        layout_list.setId(Integer.parseInt(postNo));
        layout_list.setOnClickListener(this);



        notice_lay.addView(layout_list);

    }
    public void newCulture(String c_title,String c_content,String title_c,String place,String date,String imgurl){
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


        TextView tvtv = new TextView(this);
        tvtv.setText(c_title);
        tvtv.setTextSize(22);
        layout_list.addView(tvtv);
        tvtv = new TextView(this);
        tvtv.setText(c_content);
        tvtv.setTextSize(14);
        layout_list.addView(tvtv);


        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.WRAP_CONTENT, 400);
        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(params2);
        Glide.with(this).load(imgurl).into(imageView);




        layout_list.addView(imageView);


        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(this);
        textView.setLayoutParams(params3);
        textView.setText(title_c);
        //textView.setTag("title");
        layout_list.addView(textView);

        textView = new TextView(this);
        textView.setLayoutParams(params3);
        textView.setText(place);
        //textView.setTag("place");
        layout_list.addView(textView);
        textView = new TextView(this);
        textView.setLayoutParams(params3);
        textView.setText(date);


        layout_list.addView(textView);


        notice_lay.addView(layout_list);

    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        LinearLayout layout_post = (LinearLayout)findViewById(id);
        String postNo_1 = (String)layout_post.getTag();

        Intent it = new Intent(ClubMain.this, DetailPost.class);
        it.putExtra("postNo", postNo_1);
      startActivity(it);
    }
}
