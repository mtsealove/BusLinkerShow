package inha.ac.kr.pdychoo.buslinker.Account;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import inha.ac.kr.pdychoo.buslinker.Entity.Deal;
import inha.ac.kr.pdychoo.buslinker.R;
import inha.ac.kr.pdychoo.buslinker.Views.SystemUiTuner;

public class DealActivity extends AppCompatActivity {
    Deal deal;
    static DrawerLayout drawerLayout;
    TextView startTmnTV, startNmTV, startContactTV, endTmnTV, endNmTV, endContactTV, divTimeTV, dimensionTV, weightTV, messageTV, priceTV, methodTV, paydateTV, statusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        SystemUiTuner systemUiTuner = new SystemUiTuner(this);
        systemUiTuner.setStatusBarWhite();

        findView();
        setParameter();
    }

    private void findView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        startTmnTV = findViewById(R.id.startTmnTV);
        startNmTV = findViewById(R.id.startNmTV);
        startContactTV = findViewById(R.id.startContactTV);
        endTmnTV = findViewById(R.id.endTmnTV);
        endNmTV = findViewById(R.id.endNmTV);
        endContactTV = findViewById(R.id.endContactTV);
        divTimeTV = findViewById(R.id.divTimeTV);
        dimensionTV = findViewById(R.id.weightTV);
        weightTV = findViewById(R.id.weightTV);
        messageTV = findViewById(R.id.messageTV);
        priceTV = findViewById(R.id.priceTV);
        methodTV = findViewById(R.id.methodTV);
        paydateTV = findViewById(R.id.paydateTV);
        statusTV = findViewById(R.id.statusTV);
    }

    private void setParameter() {
        Intent intent = getIntent();
        deal = (Deal) (intent.getSerializableExtra("Deal"));
        Log.e("Deal", deal.toString());

        //출발
        startTmnTV.setText("터미널: "+deal.getStartTmn());
        startNmTV.setText("이름: "+deal.getStartNm());
        startContactTV.setText("연락처: "+deal.getStartContact());
        //도착
        String endTmn="터미널: ";
        for(String tmn: (deal.getEndTmn()).split(";;")){
            endTmn+=tmn+"\n";
        }
        endTmn=endTmn.substring(0, endTmn.length()-1);
        endTmnTV.setText(endTmn);
        String endNm="이름: ";
        for(String nm: (deal.getEndNm()).split(";;")){
            endNm+=nm+"\n";
        }
        endNm=endNm.substring(0, endNm.length()-1);
        endNmTV.setText(endNm);
        String endContact="연락처: ";
        for(String contact: (deal.getEndContact()).split(";;")){
            endContact+=contact+"\n";
        }
        endContact=endContact.substring(0, endContact.length()-1);
        endContactTV.setText(endContact);
        String divTime="배송시간: ";
        for(String time: (deal.getDivTime()).split(";;"))
            divTime+=time+"\n";
        divTime=divTime.substring(0, divTime.length()-1);
        divTimeTV.setText(divTime);
        //화물정보
        String dimension="가로: "+deal.getSideX()+"cm 세로: "+deal.getSideY()+"cm 높이: "+deal.getSideZ()+"cm";
        dimensionTV.setText(dimension);
        weightTV.setText(deal.getWeight()+"KG");
        //결제정보
        if(deal.getMessage()!=null&&deal.getMessage().length()!=0)  //사용자가 메세지를 입력했었을 경우
            messageTV.setText("메세지\n"+deal.getMessage());
        else
            messageTV.setText("메세지: 없음");

        priceTV.setText("가격: "+deal.getPrice()+"원");
        String method;
        switch (deal.getMethod()){
            case 1: method="가상계좌";
            break;
            case 2: method="무통장 입금";
            break;
            default:
                method="신용카드";
        }
        methodTV.setText("결제방법: "+method);
        paydateTV.setText("결제시간: "+deal.getPaydate());
        String status;
        switch (deal.getStatus()){
            case 1: status="배송중";
            break;
            case 2: status="배송완료";
            break;
            default: status="배송준비중";
        }
        statusTV.setText(status);
    }

    //메뉴 열기
    public static void openDrawer() {
        if (!drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.openDrawer(Gravity.START);
    }
    //닫기
    public static void closeDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START);
        else
            super.onBackPressed();
    }
}
