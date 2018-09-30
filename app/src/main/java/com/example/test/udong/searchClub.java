package com.example.test.udong;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.net.URLEncoder;

public class searchClub extends AppCompatActivity  implements View.OnClickListener {
    String name = "";
    String category = "";
    String count = "";
    String clubNo;
    String userNo;
    LinearLayout layout = null;
    LinearLayout middle_Layout = null;
    Bitmap bmImg;

    Spinner spArea;
    Spinner spCategory;
    boolean isFull = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchclub);
        Button searchClub = findViewById(R.id.searchClub);
        layout = (LinearLayout)findViewById(R.id.clubs);
        Intent intent = getIntent();
        MyApplication myApp = (MyApplication) getApplication();
        userNo = intent.getStringExtra("userNo");
        spArea = (Spinner)findViewById(R.id.spinner_s_area);
        spCategory = (Spinner)findViewById(R.id.spinner_s_category);
        Spinner stateSpinner = (Spinner)findViewById(R.id.spinner_s_area);
        ArrayAdapter stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.seoul_state, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        Spinner categotySpinner = (Spinner)findViewById(R.id.spinner_s_category);
        ArrayAdapter categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_menu, android.R.layout.simple_spinner_item);
        categotySpinner.setAdapter(categoryAdapter);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        String strUrl = "http://"+getString(R.string.server_url)+"/android/searchClub.jsp?area="+ URLEncoder.encode(myApp.getUserArea())+"&category="+ URLEncoder.encode("%");
        searchClub.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){

                layout.removeAllViews();
                try {
                    String url = "http://" + getString(R.string.server_url) + "/android/searchClub.jsp?area=" + URLEncoder.encode(spArea.getSelectedItem().toString(), "utf-8") + "&category=" + URLEncoder.encode(spCategory.getSelectedItem().toString(), "utf-8");
                    new DownloadWebpageTask().execute(url);
                }catch (IOException e){

                }
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
            Log.d("gfdsgfdgs",result);
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
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet_club) {

                            if (bSet_name) {
                                name = xpp.getText();

                                bSet_name = false;
                            }
                            if(bSet_clubNo){
                                clubNo = xpp.getText();
                                bSet_clubNo = false;
                            }
                            if(bSet_count){
                                count = xpp.getText();
                                bSet_count = false;
                            }
                            if(bSet_category){
                                category = xpp.getText();
                                bSet_category = false;
                            }
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        String tag_name = xpp.getName();
                        if(tag_name.equals("club")){
                            newList();
                            bSet_club = false;
                        }
                    }
                    eventType = xpp.next();
                }
                if(!isFull){
                    newEmptyList();
                }
                isFull = true;
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
    public  void  newEmptyList(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight=1;
        LinearLayout layout_list = new LinearLayout(this);
        layout_list.setLayoutParams(params);
        middle_Layout.addView(layout_list);
    }
    public void newList(){

        if(isFull){
            middle_Layout = new LinearLayout(this);
            middle_Layout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(

                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            middle_Layout.setLayoutParams(params2);
            layout.addView(middle_Layout);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight=1;
        params.setMargins(10,10,10,10);
        LinearLayout layout_list = new LinearLayout(this);
        layout_list.setOrientation(LinearLayout.VERTICAL);
        layout_list.setLayoutParams(params);
        //layout_list.setPadding(20,20,20,0);
        int strokeWidth = 5; // 3px not dp
        int roundRadius = 15; // 8px not dp
        int strokeColor = Color.parseColor("#2E3135");
        //int fillColor = Color.parseColor("#ffdafa");

        GradientDrawable gd = new GradientDrawable();
        //gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        layout_list.setBackground(gd);
        LinearLayout.LayoutParams params_image = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, 350);
        LinearLayout layout_image = new LinearLayout(this);
        layout_image.setLayoutParams(params_image);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setAdjustViewBounds(true);
        Glide.with(this).load("http://"+getString(R.string.server_url)+"/android/clubimg/club"+clubNo+".jpg").into(imageView);
        TextView textView = new TextView(this);
        textView.setText(name);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(20);
        layout_image.addView(imageView);
        layout_list.addView(layout_image);
        layout_list.addView(textView);
        layout_list.setTag(clubNo);
        layout_list.setId(Integer.parseInt(clubNo));
        layout_list.setOnClickListener(this);

        LinearLayout info_layout = new LinearLayout(this);
        info_layout.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv_category = new TextView(this);
        tv_category.setText(category);



        LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tv_category.setGravity(Gravity.LEFT);

        LinearLayout layout_1 = new LinearLayout(this);
        layout_1.setLayoutParams(param3);
        layout_1.setGravity(Gravity.RIGHT);
        LinearLayout.LayoutParams param4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ImageView imageView_1 = new ImageView(this);
        imageView_1.setLayoutParams(param4);
        imageView_1.setPadding(10,10,0,10);
        imageView_1.setAdjustViewBounds(true);
        imageView_1.setImageResource(R.drawable.count);

        TextView tv_count = new TextView(this);
        tv_count.setText(" "+count);


        layout_1.addView(imageView_1);
        layout_1.addView(tv_count);

        info_layout.addView(tv_category);
        info_layout.addView(layout_1);

        layout_list.addView(info_layout);
        middle_Layout.addView(layout_list);
        isFull = !isFull;
    }





    @Override
    public void onClick(View view) {
        int id = view.getId();
        LinearLayout layout_club = (LinearLayout)findViewById(id);
        String clubNo = (String)layout_club.getTag();

        Intent it = new Intent(searchClub.this, DetailClub.class);
        it.putExtra("clubNo", clubNo);
        it.putExtra("userNo",userNo);
        startActivity(it);
    }
}
