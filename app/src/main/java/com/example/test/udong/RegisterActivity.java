package com.example.test.udong;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {
    EditText etId;
    EditText etPw;
    EditText etName;
    EditText nickName;
    RadioGroup rgGender;
    DatePicker dpBirth;
    Spinner spState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("회원가입");
        
        Spinner yearSpinner = (Spinner)findViewById(R.id.spinner_state);
        ArrayAdapter stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.seoul_state, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(stateAdapter);
        Button btnRegister = (Button)findViewById(R.id.btnRegister);
        etId = (EditText)findViewById(R.id.id);
        etPw = (EditText)findViewById(R.id.pw);
        etName = (EditText)findViewById(R.id.name);
        nickName = (EditText)findViewById(R.id.nickname);
        rgGender = (RadioGroup)findViewById(R.id.radioGender);
        dpBirth = (DatePicker)findViewById(R.id.birthday);
        spState = (Spinner)findViewById(R.id.spinner_state);
        btnRegister.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                int id = rgGender.getCheckedRadioButtonId();
                int gender = 0;
                RadioButton rb = (RadioButton) findViewById(id);
                if(rb.getText().equals("여성")) {
                    gender = 1;
                }

                String strUrl = null;
                try{
                strUrl = "http://"+getString(R.string.server_url)+"/android/register.jsp"+"?id="+etId.getText()+"&pw="+etPw.getText()
                        +"&name="+URLEncoder.encode(etName.getText().toString(), "UTF-8")
                        +"&nickname="+URLEncoder.encode(nickName.getText().toString(), "UTF-8")
                        +"&gender="+gender
                        +"&birth_year="+dpBirth.getYear()
                        +"&birth_month="+(dpBirth.getMonth()+1)
                        +"&birth_day="+dpBirth.getDayOfMonth()
                        +"&state="+URLEncoder.encode(spState.getSelectedItem().toString(), "UTF-8");
                        ;

                }catch (UnsupportedEncodingException e) {
                    Log.e("Yourapp", "UnsupportedEncodingException");
                }
                new DownloadWebpageTask().execute(strUrl);
            }
        });
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

                String registerResult = "";

                boolean bSet_registerResult = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("registerResult"))
                            bSet_registerResult = true;
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet_registerResult) {
                            registerResult = xpp.getText();
                            if(registerResult.equals("dup")){
                                Toast.makeText(getApplicationContext(), "id가 중복되었습니다!", Toast.LENGTH_LONG).show();
                            }else if(registerResult.equals("fail")){
                                Toast.makeText(getApplicationContext(), "회원가입에 실패했습니다.", Toast.LENGTH_LONG).show();
                            }else{
                                Intent it_Login = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(it_Login);
                            }

                            bSet_registerResult = false;
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

            }
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
