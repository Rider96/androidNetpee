package com.example.test.udong;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class clubRegisterActivity extends AppCompatActivity {
    public final static int PICK_PHOTO_CODE = 1046;
    EditText etName;
    EditText etInfo;
    String userNo;
    Spinner spState;
    Spinner spCategory;
    TextView tv_photo;
    String str_path;
    @Override

    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_clubproduce);

        tv_photo = findViewById(R.id.tv_photo);

        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");

        Spinner stateSpinner = (Spinner)findViewById(R.id.spinner_state);
        ArrayAdapter stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.seoul_state, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        Spinner categotySpinner = (Spinner)findViewById(R.id.spinner_category);
        ArrayAdapter categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_menu, android.R.layout.simple_spinner_item);
        categotySpinner.setAdapter(categoryAdapter);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Button btnClubRegister = (Button)findViewById(R.id.btnClubRegister);
        etName = (EditText)findViewById(R.id.clubName);
        etInfo = (EditText)findViewById(R.id.clubInfo);
        spState = (Spinner)findViewById(R.id.spinner_state);
        spCategory = (Spinner)findViewById(R.id.spinner_category);

        btnClubRegister.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                String strUrl = null;
                try {
                    strUrl = "http://" + getString(R.string.server_url) + "/android/produceClub.jsp"
                            + "?name=" + URLEncoder.encode(etName.getText().toString(), "UTF-8")
                            + "&info=" + URLEncoder.encode(etInfo.getText().toString(), "UTF-8")
                            + "&category=" + URLEncoder.encode(spCategory.getSelectedItem().toString(), "UTF-8")
                            + "&area=" + URLEncoder.encode(spState.getSelectedItem().toString(), "UTF-8")
                            + "&userNo=" + userNo;


                }catch (UnsupportedEncodingException e) {
                    Log.e("Yourapp", "UnsupportedEncodingException");
                }
                new DownloadWebpageTask().execute(strUrl);
            }
        });
    }



    public void selectPhoto(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Uri photoUri = data.getData();
        str_path = getRealPathFromURI(this, photoUri);
        tv_photo.setText("사진이 선택되었습니다.");
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
        finish();
        }

        private String downloadUrl(String myurl) throws IOException {
            String responseBody = "failure";
            HttpClient client = new DefaultHttpClient();
            try{

                client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

                String url = myurl;

                HttpPost post = new HttpPost(url);
                // post.addHeader("Accept", "application/xml");

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setCharset(MIME.UTF8_CHARSET);

                File file = new File(str_path);

                builder.addBinaryBody("Filedata",file , ContentType.MULTIPART_FORM_DATA, file.getName());

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


