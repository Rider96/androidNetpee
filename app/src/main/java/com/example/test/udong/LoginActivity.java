package com.example.test.udong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    EditText etId;
    EditText etPw;
    CheckBox remember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("로그인");
        remember = findViewById(R.id.remember);
        remember.setChecked(true);
        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        Button btnRegister = (Button)findViewById(R.id.btnRegister);
        etId = (EditText)findViewById(R.id.id);
        etPw = (EditText)findViewById(R.id.pw);
        btnLogin.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                String strUrl = "http://"+getString(R.string.server_url)+"/android/test.jsp"+"?id="+etId.getText()+"&pw="+etPw.getText();
                new DownloadWebpageTask().execute(strUrl);
            }
        });
        btnRegister.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent it_Register = new Intent(LoginActivity.this, RegisterActivity.class);
                //Intent it_Register = new Intent(LoginActivity.this, Test.class);//테스트용
                startActivity(it_Register);
            }
        });
        SharedPreferences sf = getSharedPreferences("login", 0);
        if(sf != null) {
            String id = sf.getString("id", "");
            etId.setText(id);
            String pw = sf.getString("pw", "");
            etPw.setText(pw);
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
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();

                String loginResult = "";
                String userNo = "";
                String nickname = "";
                String userName = "";
                String state = "";

                boolean bSet_loginResult = false;
                boolean bSet_userNo = false;
                boolean bSet_nickname = false;
                boolean bSet_userName = false;
                boolean bSet_state = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("loginResult"))
                            bSet_loginResult = true;
                        if (tag_name.equals("userNo"))
                            bSet_userNo = true;
                        if (tag_name.equals("userName"))
                            bSet_userName = true;
                        if (tag_name.equals("nickname"))
                            bSet_nickname = true;
                        if (tag_name.equals("state"))
                            bSet_state = true;
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet_loginResult) {
                            loginResult = xpp.getText();

                            bSet_loginResult = false;
                        }
                        if (bSet_userNo) {
                            userNo = xpp.getText();

                            bSet_userNo = false;
                        }
                        if (bSet_userName) {
                            userName = xpp.getText();

                            bSet_userName = false;
                        }
                        if (bSet_nickname) {
                            nickname = xpp.getText();

                            bSet_nickname = false;
                        }
                        if (bSet_state) {
                           state = xpp.getText();

                            bSet_state = false;
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }
                if(loginResult.equals("success")){
                    Intent it_main = new Intent(LoginActivity.this, MainActivity.class);
                    it_main.putExtra("userNo",userNo);

                    if(remember.isChecked()) {
                        SharedPreferences sf = getSharedPreferences("login", 0);
                        SharedPreferences.Editor editor = sf.edit();//저장하려면 editor가 필요
                        String id = etId.getText().toString(); // 사용자가 입력한 값
                        String pw = etPw.getText().toString(); // 사용자가 입력한 값
                        editor.putString("id", id); // 입력
                        editor.putString("pw", pw); // 입력
                        editor.commit();
                    }
                    MyApplication myApp = (MyApplication) getApplication();
                    myApp.setUserNo(userNo);
                    myApp.setNickname(nickname);
                    myApp.setUserName(userName);
                    myApp.setUserArea(state);
                    startActivity(it_main);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), loginResult, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

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
}
