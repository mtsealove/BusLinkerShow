package inha.ac.kr.pdychoo.buslinker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import inha.ac.kr.pdychoo.buslinker.Account.LoginActivity;
import inha.ac.kr.pdychoo.buslinker.Account.LoginKeeper;
import inha.ac.kr.pdychoo.buslinker.Entity.Member;
import inha.ac.kr.pdychoo.buslinker.Kakao.GlobalApplication;
import inha.ac.kr.pdychoo.buslinker.Network.SetIPActivity;
import inha.ac.kr.pdychoo.buslinker.Restful.LoginData;
import inha.ac.kr.pdychoo.buslinker.Restful.LoginResponse;
import inha.ac.kr.pdychoo.buslinker.Restful.RetrofitService;
import inha.ac.kr.pdychoo.buslinker.Views.SystemUiTuner;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


public class LoadActivity extends AppCompatActivity {

    private SessionCallback KakaoCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        SystemUiTuner systemUiTuner = new SystemUiTuner(LoadActivity.this);
        systemUiTuner.setStatusBarWhite();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            TedPermission.with(this)
                    .setPermissionListener(permissionListener)
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS)
                    .setRationaleMessage("위치정보에 접근하기 위해 권한이 필요합니다")
                    .check();
        } else {
            StartSearchActivity();
        }
    }

    private void StartSearchActivity() {    //메인 화면으로 이동
        LoginKeeper loginKeeper = new LoginKeeper(this);    //로그인 유지 관련
        GetIP();    //서버 IP 읽어오기
        if (loginKeeper.GetKakaoLogin()) {
            Log.e("카카오", "로그인 시도");
            if (Session.getCurrentSession().isOpened()) {
                requestKakaoAccount();
            } else {
                KakaoCallback = new SessionCallback();
                Session.getCurrentSession().addCallback(KakaoCallback);
                Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this);
            }
        } else if (loginKeeper.GetBusLinkerLogin() != null) {
            Log.e("Buslinker", "버스링커 로그인 시도");
            GetBusLinkerLogin(loginKeeper);
        } else {
            Log.e("Buslinker", "로그인 안해");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {    //로딩 화면을 보여주기 위해 0.7초 기다림
                @Override
                public void run() {
                    Start();
                }
            }, 700);
        }
    }

    private void Start() {
        Intent intent = new Intent(LoadActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(LoadActivity.this, "권한이 허용되었습니다", Toast.LENGTH_SHORT).show();
            StartSearchActivity();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            for (String deniedPermission : deniedPermissions) {
                switch (deniedPermission) {
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                        Toast.makeText(LoadActivity.this, "권한이 거부되었기 때문에 위치 정보를 이용할 수 없습니다", Toast.LENGTH_SHORT).show();
                        break;
                    case Manifest.permission.READ_PHONE_STATE:
                        Toast.makeText(LoadActivity.this, "권한이 거부되었기 때문에 전화번호를 읽을 수 없습니다", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            StartSearchActivity();
        }
    };

    //버스링커 ID로 로그인
    private void GetBusLinkerLogin(LoginKeeper loginKeeper) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("로그인");
        progressDialog.setMessage("로그인중입니다");
        progressDialog.show();
        LoginData loginData = loginKeeper.GetBusLinkerLogin();
        Log.e("로그인", loginKeeper.GetBusLinkerLogin().getID());
        Log.e("로그인", loginKeeper.GetBusLinkerLogin().getPassword());

        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+SetIPActivity.IP+":3000")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        Call<LoginResponse> call = retrofitService.PostLogin(loginData);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.e("Result", "onResponse");
                if (response.isSuccessful()) {    //로그인에 성공하였을 경우
                    String ID = response.body().getID();
                    String name = response.body().getName();
                    //String birth=response.body().getBirth();
                    //char gender=response.body().getGender();
                    if(response.body().getID()!=null)   //로그인에 성공하면
                        Member.user = new Member(ID, name, 1);
                } else {
                    Log.e("Result", "실패");
                }
                progressDialog.dismiss();
                Start();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("Result", "결과 반환 실패");
                progressDialog.dismiss();
                Start();
            }
        });
    }


    protected void requestKakaoAccount() {  //카카오 계정 요청
        System.out.println("세션 열림");
        UserManagement.getInstance().me(new MeV2ResponseCallback() {

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                System.out.println("세션 오류");
                Start();
            }

            @Override
            public void onSuccess(MeV2Response result) {
                System.out.println("성공 " + result.toString());
                String email = result.getKakaoAccount().getEmail();
                String name = result.getNickname();
                String profilePath = result.getProfileImagePath();
                String profileThumbPath = result.getThumbnailImagePath();
                boolean verified = result.getKakaoAccount().isEmailVerified().getBoolean();
                if (verified) {  //이메일 인증이 된 사용자라면
                    Member.user = new Member(email, name, profilePath, profileThumbPath, Member.KAKAO_MEMBER);
                    System.out.println(Member.user.toString());
                }
                Start();
            }
        });
    }

    //서버 IP 확인
    private void GetIP() {
        File file = new File(getFilesDir() + "IP.dat");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String ip = br.readLine();
            br.close();
            SetIPActivity.IP = ip;    //IP 액티비티의 변수로 설정
        } catch (Exception e) {
            Log.e("Load", "IP 파일 읽기 실패");
            SetIPActivity.IP = "192.168.10.31";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            System.out.println("액티비티 결과");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //카카오 세션 콜백 클래스
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestKakaoAccount();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            System.out.println("세션 열기 실패");
            if (exception != null) {
                Logger.e(exception);
            }
        }
    }
}
