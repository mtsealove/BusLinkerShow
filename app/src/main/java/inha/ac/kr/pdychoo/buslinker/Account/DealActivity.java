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
    TextView startTmnTV, startNmTV, startContactTV, endTmnTV, endNmTVm, endContactTV, divTimeTV, dimensionTV, weightTV, messageTV, priceTV, methodeTV, paydateTV, statusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        drawerLayout=findViewById(R.id.drawerLayout);
        startTmnTV=findViewById(R.id.startTmnTV);
        startNmTV=findViewById(R.id.startNmTV);
        startContactTV=findViewById(R.id.startContactTV);


        Intent intent=getIntent();
        deal=(Deal)(intent.getSerializableExtra("Deal"));
        Log.e("Deal", deal.toString());

        SystemUiTuner systemUiTuner=new SystemUiTuner(this);
        systemUiTuner.setStatusBarWhite();
    }

    //메뉴 열기
    public static void openDrawer() {
        if(!drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.openDrawer(Gravity.START);
    }
}
