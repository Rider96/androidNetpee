package com.example.test.udong;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;


public class MainActivity extends TabActivity {
    ImageView imgButton;
    LinearLayout pop;
    DrawerLayout dl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dl = findViewById(R.id.mainDrawerLayout);
        MyApplication myApp = (MyApplication) getApplication();
        TextView side_name = (TextView)findViewById(R.id.side_name);
        side_name.setText(myApp.getUserName());
        TextView side_nickname = (TextView)findViewById(R.id.side_nickname);
        side_nickname.setText("("+myApp.getNickname()+")");
        TextView side_area = (TextView)findViewById(R.id.side_area);
        side_area.setText(myApp.getUserArea());
        Button btn_logout = findViewById(R.id.logout);
        btn_logout.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent it_Login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(it_Login);
                finish();
            }
        });
        pop = findViewById(R.id.pop);
        pop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        imgButton = (ImageView)findViewById(R.id.goMY);
        imgButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                dl.openDrawer(GravityCompat.END);
            }
        });
        final TabHost tab_host = getTabHost();
        // 탭에서 액티비티를 사용할 수 있도록 인텐트를 생성한다.
        TabHost.TabSpec spec;
        Intent intent;
        intent = getIntent();
        String userNo = intent.getStringExtra("userNo");
        intent = new Intent().setClass(this, MyClub.class);
        intent.putExtra("userNo",userNo);

        spec = tab_host.newTabSpec("myclub");

        // 탭이름 설정
        spec.setIndicator("내 모임");

        // TabSpec 객체에 FrameLayout 이 출력할 페이지를 설정한다.
        spec.setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        // 탭호스트에 해당 정보를 가진 탭을 추가한다

        tab_host.addTab(spec);

        intent = new Intent().setClass(this, searchClub.class);
        intent.putExtra("userNo",userNo);
        spec = tab_host.newTabSpec("searchclub");
        spec.setIndicator("모임 찾기");
        spec.setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        tab_host.addTab(spec);
        tab_host.setCurrentTab(0);
        int tab = tab_host.getCurrentTab();
        for (int i = 0; i < tab_host.getTabWidget().getChildCount(); i++) {
            // When tab is not selected
            tab_host.getTabWidget().getChildAt(i).setBackground(null);
            TextView tv = (TextView) tab_host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#666666"));
            tv.setTypeface(null, Typeface.NORMAL);
        }
        // When tab is selected
        //tab_host.getTabWidget().getChildAt(tab_host.getCurrentTab()).setBackgroundColor(Color.parseColor("#53003c"));
        TextView tv = (TextView) tab_host.getTabWidget().getChildAt(tab).findViewById(android.R.id.title);
        tv.setTextColor(Color.parseColor("#53003c"));
        tv.setTypeface(null, Typeface.BOLD);
        /*
        *  만약 탭을추가할시 탭 제목을 이미지로 하려면..

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher);

        intent = new Intent().setClass(this, Chinese_Food.class);

        spec = tab_host.newTabSpec("chines");
        spec.setIndicator(imageView); // <- 이곳의 imageView를 넣어주시면 됩니다.
        spec.setContent(intent);

        tab_host.addTab(spec);

        tab_host.setCurrentTab(0);*/
        tab_host.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId) {
                int tab = tab_host.getCurrentTab();
                for (int i = 0; i < tab_host.getTabWidget().getChildCount(); i++) {
                    // When tab is not selected
                    // tab_host.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ffffffff"));
                    TextView tv = (TextView) tab_host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                    tv.setTextColor(Color.parseColor("#666666"));
                    tv.setTypeface(null, Typeface.NORMAL);
                }
                // When tab is selected
                //tab_host.getTabWidget().getChildAt(tab_host.getCurrentTab()).setBackgroundColor(Color.parseColor("#53003c"));
                TextView tv = (TextView) tab_host.getTabWidget().getChildAt(tab).findViewById(android.R.id.title);
                tv.setTextColor(Color.parseColor("#53003c"));
                tv.setTypeface(null, Typeface.BOLD);
            }
        });
    }
}

