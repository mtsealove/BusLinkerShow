package inha.ac.kr.pdychoo.buslinker;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import inha.ac.kr.pdychoo.buslinker.Views.SlideMenu;
import inha.ac.kr.pdychoo.buslinker.Views.SystemUiTuner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;


public class SelectRouteActivity extends AppCompatActivity {
    private String depTmnNm, depTmnCd, arrTmnNm, arrTmnCd, registrationDate, StartTime;
    TextView depTmnTV, arrTmnTV, dateTV, expressTV, intercityTV;
    LinearLayout delayLayout, noResultLayout;
    ListView dispatchLV;
    SlideMenu slideMenu;
    ArrayList<String> arrPrdtTm, corpNm, depTm;
    DispatchAdapter dispatchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);
        depTmnTV = findViewById(R.id.depTmnNmTV);
        arrTmnTV = findViewById(R.id.arrTmnNmTV);
        dateTV = findViewById(R.id.dateTV);
        expressTV = findViewById(R.id.expressTV);
        intercityTV = findViewById(R.id.intercityTV);
        dispatchLV = findViewById(R.id.dispatchList);
        delayLayout = findViewById(R.id.delayLayout);
        slideMenu = findViewById(R.id.slideMenu);
        noResultLayout=findViewById(R.id.noResultLayout);

        SystemUiTuner systemUiTuner = new SystemUiTuner(SelectRouteActivity.this);
        systemUiTuner.setStatusBarWhite();

        getParameter();
        getExpress();

    }

    private void getParameter() {   //매개변수 읽어오기
        Intent intent = getIntent();
        depTmnCd = intent.getStringExtra("depTmnCd");
        depTmnNm = intent.getStringExtra("depTmnNm") + "터미널";
        arrTmnCd = intent.getStringExtra("arrTmnCd");
        arrTmnNm = intent.getStringExtra("arrTmnNm") + "터미널";
        StartTime = intent.getStringExtra("StartTime");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        registrationDate = dateFormat.format(new Date());

        arrTmnTV.setText(arrTmnNm);
        depTmnTV.setText(depTmnNm);
        String Date4Show = registrationDate.substring(0, 4) + "년 " + registrationDate.substring(4, 6) + "월 " + registrationDate.substring(6, 8) + "일";
        dateTV.setText(Date4Show);
    }


    private void getExpress() { //고속버스 데이터를 가져옴
        delayLayout.setVisibility(View.VISIBLE);
        dispatchLV.setVisibility(View.GONE);
        arrPrdtTm = new ArrayList<>();
        corpNm = new ArrayList<>();
        depTm = new ArrayList<>();
        final String key = "ArpN26ykcA%2FOVG5glFhhBYwU3OJAO4x%2B2bQyILHAmwAH%2FLZUbQM7xig9xdwe77qwoVYkNuko4Hs%2F6qg%2B4WmfVw%3D%3D";

        new Thread(new Runnable() {
            @Override
            public void run() {
                int DongSeoul = 1;
                if (depTmnCd.equals("NAEK030") || arrTmnCd.equals("NAEK030")) DongSeoul = 2;   //동서울이 매개변수이면
                try {
                    for (int i = 0; i < DongSeoul; i++) {    //
                        if (i == 1) {
                            System.out.println("동서울 발견");
                            if (depTmnCd.equals("NAEK030")) depTmnCd = "NAEK032";
                            else arrTmnCd = "NAEK032";
                            System.out.println("arr: " + arrTmnCd + " dep: " + depTmnCd);
                        }
                        final String query = "http://openapi.tago.go.kr/openapi/service/ExpBusInfoService/getStrtpntAlocFndExpbusInfo?" +
                                "serviceKey=" + key
                                + "&numOfRows=100"
                                + "&depTerminalId=" + depTmnCd
                                + "&arrTerminalId=" + arrTmnCd
                                + "&depPlandTime=" + registrationDate;
                        URL url = new URL(query);
                        InputStream is = url.openStream();
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        XmlPullParser xmlPullParser = factory.newPullParser();
                        xmlPullParser.setInput(new InputStreamReader(is, StandardCharsets.UTF_8));

                        String tag;
                        xmlPullParser.next();
                        int eventType = xmlPullParser.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    tag = xmlPullParser.getName();
                                    if (tag.equals("depPlandTime")) {
                                        xmlPullParser.next();
                                        String time = xmlPullParser.getText().substring(8, 12);
                                        time = time.substring(0, 2) + "시 " + time.substring(2, 4) + "분";
                                        depTm.add(time);
                                    } else if (tag.equals("arrPlandTime")) {
                                        xmlPullParser.next();
                                            String time = xmlPullParser.getText().substring(8, 12);
                                            time = time.substring(0, 2) + "시 " + time.substring(2, 4) + "분";
                                            arrPrdtTm.add(time);
                                    }
                                    break;
                            }
                            eventType = xmlPullParser.next();
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             dispatchAdapter = new DispatchAdapter();

                            for (int i = 0; i < depTm.size(); i++) {
                                String dep = depTm.get(i);
                                System.out.println("출발시간: " + dep);
                                String arr = arrPrdtTm.get(i);
                                System.out.println("도착시간: " + arr);
                                if (IsNextDispatch(dep)) //나중이면 표시
                                    dispatchAdapter.addItem(arr, dep);
                                else {  //아니면 제거
                                    depTm.remove(i);
                                    arrPrdtTm.remove(i);
                                }
                            }
                            dispatchLV.setAdapter(dispatchAdapter);
                            dispatchLV.setOnItemClickListener(itemClickListener);
                            dispatchLV.setVisibility(View.VISIBLE);
                            delayLayout.setVisibility(View.GONE);
                            if(dispatchAdapter.getCount()==0){
                                noResultLayout.setVisibility(View.VISIBLE);
                                dispatchLV.setVisibility(View.GONE);
                            }
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String arrTime=((DispatchItem)dispatchAdapter.getItem(i)).getArrTime();
            String depTime=((DispatchItem) dispatchAdapter.getItem(i)).getDepTime();
            Intent intent=new Intent();
            intent.putExtra("arrTime", arrTime);
            intent.putExtra("depTime", depTime);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    private boolean IsNextDispatch(String depTime) {   //현재 시간보다 나중인지 반환
        String[] times = StartTime.split(":");
        int hour = Integer.parseInt(depTime.substring(0, 2));
        int min = Integer.parseInt(depTime.substring(4, 6));

        int hourNow = Integer.parseInt(times[0]);
        int minNow = Integer.parseInt(times[1]);
        if (hour > hourNow) {   //시간 자체가 높으면
            return true;
        } else //시간은 같고 분이 나중이면
            return hour == hourNow && min >= minNow;
    }


    @Override
    public void onResume() {
        super.onResume();
        slideMenu.checkLogin();
    }
}
