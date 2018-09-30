package com.example.test.udong;

import android.app.Activity;
import android.content.Intent;
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

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailPost extends AppCompatActivity {
    String postNo;
    String text;
    TextView postTitle;
    LinearLayout postContents;
    LinearLayout commtents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailpost);
        Button btn_submit = findViewById(R.id.submit);
        postTitle = (TextView) findViewById(R.id.posttitle);
        postContents = (LinearLayout) findViewById(R.id.contents);
        commtents = findViewById(R.id.comments);
        Intent intent = getIntent();
        postNo = intent.getStringExtra("postNo");
        btn_submit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                new SubmitTask().execute();
            }
        });
        String strUrl = "http://"+getString(R.string.server_url)+"/android/detailPost.jsp?postNo="+postNo;


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
            Log.d("TESTTEST",result);
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                String nickname = "";
                String contents = "";

                boolean bSet_title = false;
                boolean bSet_image = false;
                boolean bSet_text = false;
                boolean bSet_nickname = false;
                boolean bSet_contents = false;
                int i = 1;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("title"))
                            bSet_title = true;
                        if (tag_name.equals("text"))
                            bSet_text = true;
                        if (tag_name.equals("image"))
                            bSet_image = true;
                        if (tag_name.equals("nickname"))
                            bSet_nickname = true;
                        if (tag_name.equals("contents"))
                            bSet_contents = true;
                    } else if (eventType == XmlPullParser.TEXT) {


                            if (bSet_title) {
                                postTitle.setText(xpp.getText());

                                bSet_title = false;
                            }
                        if (bSet_nickname) {
                            nickname = xpp.getText();

                            bSet_nickname = false;
                        }
                        if (bSet_contents) {
                            contents = xpp.getText();

                            bSet_contents = false;
                        }
                        if (bSet_text) {
                                text = xpp.getText();
                            newText();

                            bSet_text = false;
                        }
                        if (bSet_image) {
                            newImage(i);
                            i++;
                            bSet_image = false;
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("text"))
                            bSet_text = false;
                        if (tag_name.equals("comment"))
                            addComment(nickname, contents);
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

    public void newText(){


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(this);
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextSize(20);
        postContents.addView(textView);
    }
    public void newImage(int i){


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        Glide.with(this).load("http://"+getString(R.string.server_url)+"/android/postimg/"+postNo+"_"+i+".jpg").into(imageView);
        postContents.addView(imageView);
    }



    private class SubmitTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... unused) {
            return (String) executeClient();
        }

        protected void onPostExecute(String result) {

Intent intent = new Intent(DetailPost.this,DetailPost.class);
intent.putExtra("postNo",postNo);
finish();
startActivity(intent);

        }

        // 실제 전송하는 부분
        public String executeClient() {

            String responseBody = "failure";
            HttpClient client = new DefaultHttpClient();
            try{

                client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

                String url = "http://"+getString(R.string.server_url)+"/android/comment.jsp";

                HttpPost post = new HttpPost(url);
                // post.addHeader("Accept", "application/xml");

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setCharset(MIME.UTF8_CHARSET);
                MyApplication myApp = (MyApplication) getApplication();
                builder.addTextBody("userNo", myApp.getUserNo(), ContentType.create("text/plain", MIME.UTF8_CHARSET));
                builder.addTextBody("postNo", postNo, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                EditText et = (EditText)findViewById(R.id.et_comment);
                builder.addTextBody("contents",et.getText().toString(), ContentType.create("text/plain", MIME.UTF8_CHARSET));

                post.setEntity(builder.build());

                client.execute(post);



            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.getConnectionManager().shutdown();
            }

            return null;


        }
    }
    void addComment(String nickname, String contents){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        int roundRadius = 25; // 8px not dp
        int fillColor = Color.parseColor("#cccccc");

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);

        LinearLayout layout_list = new LinearLayout(this);
        layout_list.setLayoutParams(params);
        layout_list.setPadding(10,10,10,10);
        layout_list.setBackground(gd);
        TextView textView1 = new TextView(this);
        textView1.setTextSize(18);
        textView1.setTextColor(Color.parseColor("#3367f5"));
        textView1.setText(nickname);
        TextView textView2 = new TextView(this);
        textView2.setText(" "+contents);
        textView2.setTextSize(18);
        layout_list.addView(textView1);
        layout_list.addView(textView2);
        commtents.addView(layout_list);
    }
}
