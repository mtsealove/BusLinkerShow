package inha.ac.kr.pdychoo.buslinker.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.widget.*;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import inha.ac.kr.pdychoo.buslinker.Entity.Member;
import inha.ac.kr.pdychoo.buslinker.Network.SetIPActivity;
import inha.ac.kr.pdychoo.buslinker.R;
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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    EditText IDET, passwordET;
    Button loginBtn, signUpBtn;
    com.kakao.usermgmt.LoginButton kakaoLoginBtn;
    CheckBox keepLoginCB;
    private SessionCallback KakaoCallback;
    ImageView hidePwIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        IDET = findViewById(R.id.IDET);
        passwordET = findViewById(R.id.passwordET);
        passwordET.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordET.setTransformationMethod( PasswordTransformationMethod.getInstance());

        loginBtn = findViewById(R.id.loginBtn);
        signUpBtn = findViewById(R.id.signUpBtn);
        kakaoLoginBtn = findViewById(R.id.kakaoLoginBtn);
        keepLoginCB = findViewById(R.id.keepLoginCB);
        hidePwIV=findViewById(R.id.hidePwIV);

        SystemUiTuner systemUiTuner = new SystemUiTuner(LoginActivity.this);
        systemUiTuner.setStatusBarWhite();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        KakaoCallback = new SessionCallback();

        //카카오 콜백 설정
        Session.getCurrentSession().addCallback(KakaoCallback);
        Session.getCurrentSession().close();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginAsBusLinker();
            }
        });
        hidePwIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TogglePwHide();
            }
        });
    }

    //회원가입 페이지로 이동
    private void signUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    ProgressDialog progressDialog;

    protected void LoginAsBusLinker() { //버스링커 계정으로 로그인
        String ID = IDET.getText().toString();
        String passwordDec = null;
        try {
            passwordDec = AES256Chiper.AES_Encode(passwordET.getText().toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        final String password = passwordDec;
        final boolean keepLogin = keepLoginCB.isChecked();

        //ID, 비밀번호 입력 체크
        if (ID.length() == 0) Toast.makeText(this, "ID를 입력하세요", Toast.LENGTH_SHORT).show();
        else if (password.length() == 0)
            Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else {  //ID 모두 입력 시
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("로그인");
            progressDialog.setMessage("로그인중입니다");
            progressDialog.show();

            OkHttpClient okHttpClient=new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + SetIPActivity.IP + ":3000")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            RetrofitService retrofitService = retrofit.create(RetrofitService.class);
            Call<LoginResponse> call = retrofitService.PostLogin(new LoginData(ID, password));
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    Log.e("Result", "onResponse");
                    if (response.isSuccessful() && response.body().getID() != null) {    //로그인에 성공하였을 경우
                        String ID = response.body().getID();
                        String name = response.body().getName();
                        //String birth=response.body().getBirth();
                        //char gender=response.body().getGender();
                        Member.user = new Member(ID, name, 1);
                        LoginKeeper loginKeeper = new LoginKeeper(LoginActivity.this);
                        if (keepLogin) {  // 로그인 유지 체크 시
                            loginKeeper.SetBusLinkerLogin(new LoginData(ID, password));
                        } else { //로그인 유지 미 체크 시
                            loginKeeper.SetBusLinkerLogin();
                        }

                        loginKeeper.SetKakaoLogin(false);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        Log.e("Result", "실패");
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("Result", "결과 반환 실패");
                    Toast.makeText(LoginActivity.this, "서버 연결에 실패하였습니다", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }


    protected void requestKakaoAccount() {  //카카오 계정 요청
        System.out.println("세션 열림");
        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                System.out.println("세션 오류");
            }

            @Override
            public void onNotSignedUp() {
                System.out.println("회원 아님");
            }

            @Override
            public void onSuccess(UserProfile result) {
                System.out.println("성공 " + result.toString());
                String email = result.getEmail();
                String name = result.getNickname();
                String profilePath = result.getProfileImagePath();
                String profileThumbPath = result.getThumbnailImagePath();
                boolean verified = result.getEmailVerified();
                if (verified) {  //이메일 인증이 된 사용자라면
                    Member.user = new Member(email, name, profilePath, profileThumbPath, Member.KAKAO_MEMBER);
                    System.out.println(Member.user.toString());
                    LoginKeeper loginKeeper = new LoginKeeper(LoginActivity.this);
                    loginKeeper.SetKakaoLogin(true);
                    loginKeeper.SetBusLinkerLogin();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "카카오톡 이메일 인증이 되지 않았습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Session.getCurrentSession().removeCallback(KakaoCallback);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean Hide=true;
    private void TogglePwHide() {
        if(Hide){   //숨겨져 있을 경우
            Hide=false;
            passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        }else {
            Hide=true;
            passwordET.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
            passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}