package inha.ac.kr.pdychoo.buslinker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.widget.Toast;
import inha.ac.kr.pdychoo.buslinker.Entity.Deal;
import inha.ac.kr.pdychoo.buslinker.Entity.Member;
import inha.ac.kr.pdychoo.buslinker.Network.SetIPActivity;
import inha.ac.kr.pdychoo.buslinker.Restful.PostResponse;
import inha.ac.kr.pdychoo.buslinker.Restful.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UploadDataActivity extends AppCompatActivity { //데이터를 DB에 업로드할 액티비티
    //이전 액티비티로 넘겨받을 정보들
    private String userID, startTmn, startNm, startContact, endTmn, endNm, endContact, divTime, message, paydate;
    private int sideX, sideY, sideZ, weight, price, method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_data);

        getParameter();
        CreateDeal();
    }

    private void getParameter() {   //이전 액티비티로부터 파라미터 전달
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        startTmn = intent.getStringExtra("startTmn");
        startNm = intent.getStringExtra("startNm");
        startContact = intent.getStringExtra("startContact");
        endTmn = intent.getStringExtra("endTmn");
        endNm = intent.getStringExtra("endNm");
        endContact = intent.getStringExtra("endContact");
        divTime = intent.getStringExtra("divTime");
        message = intent.getStringExtra("message");
        if(message==null){ //메세지를 입력하지 않았을 경우 초기화
            message="";
        }
        paydate=intent.getStringExtra("paydate");
        sideX = intent.getIntExtra("sideX", 0);
        sideY = intent.getIntExtra("sideY", 0);
        sideZ = intent.getIntExtra("sideZ", 0);
        weight = intent.getIntExtra("weight", 0);
        price = intent.getIntExtra("price", 0);
        method = intent.getIntExtra("method", 0);
    }

    private void CreateDeal() {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://"+ SetIPActivity.IP+":3000")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        //테스트용 객체 디스플레이
        Deal deal=new Deal(userID, startTmn, startNm, startContact, endTmn, endNm, endContact, divTime, message, paydate, sideX, sideY, sideZ, weight, price, method, 0);
        Log.e("Deal", deal.toString());
        Call<PostResponse> call=retrofitService.PostDeal(deal);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    if(response.body().getResult().equals("OK")){
                        Toast.makeText(UploadDataActivity.this, "거래가 완료되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UploadDataActivity.this, "데이터 갱신에 실패했습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {

            }
        });

    }

}
