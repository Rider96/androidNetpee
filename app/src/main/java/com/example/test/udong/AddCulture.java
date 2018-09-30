package com.example.test.udong;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddCulture extends AppCompatActivity {
    String userNo;
    String clubNo;
    String title;
    String place;
    String date;
    String imgUrl;
    LinearLayout culture_editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addculture);
        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");
        clubNo = intent.getStringExtra("clubNo");
        title = intent.getStringExtra("title");
        place = intent.getStringExtra("place");
        date = intent.getStringExtra("date");
        imgUrl = intent.getStringExtra("imgUrl");

        culture_editor = findViewById(R.id.culture_editor);





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
        Glide.with(this).load(imgUrl).into(imageView);




        layout_list.addView(imageView);


        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(this);
        textView.setLayoutParams(params3);
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
        textView.setText(date);


        layout_list.addView(textView);


        culture_editor.addView(layout_list);






        Button btn = findViewById(R.id.culture_btnpost);
        btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                new PostTask().execute();
            }
        });
    }



    private class PostTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... unused) {
            return (String) executeClient();
        }

        protected void onPostExecute(String result) {

                finish();


        }

        // 실제 전송하는 부분
        public String executeClient() {

            String responseBody = "failure";
            HttpClient client = new DefaultHttpClient();
try{

            client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            String url = "http://"+getString(R.string.server_url)+"/android/postCulture.jsp";

            HttpPost post = new HttpPost(url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(MIME.UTF8_CHARSET);

                builder.addTextBody("userNo", userNo, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                builder.addTextBody("clubNo", clubNo, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                TextView tv1 = findViewById(R.id.culture_title);
                TextView tv2 = findViewById(R.id.culture_content);
                builder.addTextBody("title", title, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                builder.addTextBody("place", place, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                builder.addTextBody("date", date, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                builder.addTextBody("imgUrl", imgUrl, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                builder.addTextBody("c_title", tv1.getText().toString(), ContentType.create("text/plain", MIME.UTF8_CHARSET));
                builder.addTextBody("c_content", tv2.getText().toString(), ContentType.create("text/plain", MIME.UTF8_CHARSET));



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

}
