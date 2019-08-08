package inha.ac.kr.pdychoo.buslinker.Account;

import android.app.ProgressDialog;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import inha.ac.kr.pdychoo.buslinker.Entity.Deal;
import inha.ac.kr.pdychoo.buslinker.Entity.Member;
import inha.ac.kr.pdychoo.buslinker.Entity.RecycleAdapter;
import inha.ac.kr.pdychoo.buslinker.Network.SetIPActivity;
import inha.ac.kr.pdychoo.buslinker.R;
import inha.ac.kr.pdychoo.buslinker.Restful.RetrofitService;
import inha.ac.kr.pdychoo.buslinker.Views.SystemUiTuner;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DealListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    static DrawerLayout drawerLayout;
    TextView noDealTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_list);
        drawerLayout = findViewById(R.id.drawerLayout);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        noDealTV = findViewById(R.id.noDealTV);

        SystemUiTuner systemUiTuner = new SystemUiTuner(this);
        systemUiTuner.setStatusBarWhite();

        GetDealList();
    }

    //거래내역 읽어오기
    private void GetDealList() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("거래내역을 조회중입니다");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + SetIPActivity.IP + ":3000")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<List<Deal>> call = retrofitService.GetDealList(Member.user.getEmail());
        call.enqueue(new Callback<List<Deal>>() {
            @Override
            public void onResponse(Call<List<Deal>> call, Response<List<Deal>> response) {
                if (response.isSuccessful()) {
                    List<Deal> dealList = response.body();
                    RecycleAdapter recycleAdapter = new RecycleAdapter(DealListActivity.this, dealList);
                    recyclerView.setAdapter(recycleAdapter);

                    //거래내역이 존재하지 않을 경우
                    if (recycleAdapter.getItemCount() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        noDealTV.setVisibility(View.VISIBLE);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Deal>> call, Throwable t) {
                Toast.makeText(DealListActivity.this, "서버 연결에 실패했습니다", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public static void openDrawer() {
        if(!drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.openDrawer(Gravity.START);
    }
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
