package com.example.test.udong;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPost extends AppCompatActivity {
    public final static int PICK_PHOTO_CODE = 1046;
    String str_path;
    String userNo;
    String clubNo;
    String grade;
    EditText et_title;
    LinearLayout editor;
    List<String> img_path;
    Spinner spNotice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button btn_photo;
        Button btn_post;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);
        spNotice = (Spinner)findViewById(R.id.spinner_notice);

        ArrayAdapter stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.notice, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNotice.setAdapter(stateAdapter);

        img_path = new ArrayList<String>();
        editor = (LinearLayout)findViewById(R.id.editor);
        et_title = (EditText) findViewById(R.id.title);
        btn_photo = findViewById(R.id.btnphoto);
        btn_post = findViewById(R.id.btnpost);
        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");
        clubNo = intent.getStringExtra("clubNo");
        grade = intent.getStringExtra("grade");
        if (grade.equals("nomal")){
            spNotice.setVisibility(View.INVISIBLE);
        }
        btn_photo.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_PHOTO_CODE);
            }
        });
        btn_post.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                new PostTask().execute();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Uri photoUri = data.getData();
        str_path = getRealPathFromURI(this, photoUri);
        img_path.add(str_path);
        int degrees = GetExifOrientation(str_path);
        Toast.makeText(getApplicationContext(), "gfds"+degrees, Toast.LENGTH_LONG).show();

        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);

            int WIDTH = 300;
            int width = WIDTH;
            float ratio = (float) bm.getHeight() / bm.getWidth();
            int height = (int) (WIDTH * ratio);

            Bitmap thumb = Bitmap.createScaledBitmap(bm, width, height,false);
            thumb = GetRotatedBitmap(thumb, degrees);
            addImage(thumb);
        } catch(FileNotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch(IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public void addImage(Bitmap thumb){
        ViewGroup.LayoutParams imageViewParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView imageView = new ImageView(this);

        imageView.setImageBitmap(thumb);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(imageViewParams);
        editor.addView(imageView);
        EditText editText = new EditText(this);
        editText.setBackground(null);
        editor.addView(editText);
    }
    private int GetExifOrientation(String filepath) {

        int degree = 0;

        ExifInterface exif = null;



        try {

            exif = new ExifInterface(filepath);

        } catch (Exception e) {

            e.printStackTrace();

        }



        if (exif != null) {

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);



            if (orientation != -1) {

                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:

                        degree = 90;

                        break;



                    case ExifInterface.ORIENTATION_ROTATE_180:

                        degree = 180;

                        break;



                    case ExifInterface.ORIENTATION_ROTATE_270:

                        degree = 270;

                        break;

                }

            }

        }



        return degree;

    }
    private Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) {

        if (degrees != 0 && bitmap != null) {

            Matrix m = new Matrix();

            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);



            try {

                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);



                if (bitmap != b2) {

                    bitmap.recycle();

                    bitmap = b2;

                }

            } catch (OutOfMemoryError e) {


            }

        }



        return bitmap;

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

            String url = "http://"+getString(R.string.server_url)+"/android/uploadImg.jsp";

            HttpPost post = new HttpPost(url);
           // post.addHeader("Accept", "application/xml");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(MIME.UTF8_CHARSET);

                builder.addTextBody("userNo", userNo, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                builder.addTextBody("clubNo", clubNo, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                if(grade.equals("nomal")){
                    builder.addTextBody("notice", "false", ContentType.create("text/plain", MIME.UTF8_CHARSET));
                }else{
                    String notice = spNotice.getSelectedItem().toString();
                    if(notice.equals("일반")) {
                        builder.addTextBody("notice", "false", ContentType.create("text/plain", MIME.UTF8_CHARSET));
                    }else{
                        builder.addTextBody("notice", "true", ContentType.create("text/plain", MIME.UTF8_CHARSET));
                    }
                }
                builder.addTextBody("title", et_title.getText().toString(), ContentType.create("text/plain", MIME.UTF8_CHARSET));
                String postXml = "";
                for(int i = 0;i<editor.getChildCount();i++){
                    View view = editor.getChildAt(i);
                    if(view instanceof ImageView){
                        postXml +="<image>image</image>";
                    }else{
                        EditText et = (EditText)view;
                        if(!(et.getText() == null)){
                            postXml += "<text>" +et.getText() + "</text>";
                        }
                    }
                }
    builder.addTextBody("postXml", postXml, ContentType.create("text/plain", MIME.UTF8_CHARSET));
            File file;
                for (int i = 0;i<img_path.size();i++) {
                    file = new File(img_path.get(i));
                    builder.addBinaryBody("image"+i,file , ContentType.MULTIPART_FORM_DATA, file.getName());
                }




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
