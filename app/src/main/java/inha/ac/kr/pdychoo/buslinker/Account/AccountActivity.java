package inha.ac.kr.pdychoo.buslinker.Account;

import android.app.ProgressDialog;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import inha.ac.kr.pdychoo.buslinker.Entity.Member;
import inha.ac.kr.pdychoo.buslinker.Network.SetIPActivity;
import inha.ac.kr.pdychoo.buslinker.R;
import inha.ac.kr.pdychoo.buslinker.Restful.*;
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
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class AccountActivity extends AppCompatActivity {
    TextView IDTV, birthTV, genderTV;
    EditText nameET, newPasswordET, newPasswordConfirmET, passwordET;
    Button confirmBtn;
    private static DrawerLayout drawerLayout;
    private String password;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        SystemUiTuner systemUiTuner = new SystemUiTuner(this);
        systemUiTuner.setStatusBarWhite();

        drawerLayout = findViewById(R.id.drawerLayout);
        IDTV = findViewById(R.id.IDTV);
        birthTV = findViewById(R.id.birthTV);
        genderTV = findViewById(R.id.genderTV);
        nameET = findViewById(R.id.nameET);
        newPasswordET = findViewById(R.id.newPasswordET);
        newPasswordConfirmET = findViewById(R.id.newPasswordConfirmET);
        passwordET = findViewById(R.id.passwordET);
        confirmBtn = findViewById(R.id.confirmBtn);

        getInfo();
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateData();
            }
        });
    }

    private void getInfo() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("정보를 불러오는 중입니다");
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
        Call<User> call = retrofitService.GetUser(Member.user.getEmail());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = (User) response.body();
                    IDTV.setText(user.getID());
                    nameET.setText(user.getName());
                    switch (user.getGender()) {
                        case 'm':
                            genderTV.setText("남성");
                            break;
                        case 'f':
                            genderTV.setText("여성");
                            break;
                    }
                    birthTV.setText((user.getBirth()).substring(0, 10));
                    password = user.getPassword();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AccountActivity.this, "서버 연결에 실패했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateData() {
        String newPassword = newPasswordET.getText().toString(), newPasswordConfirm = newPasswordConfirmET.getText().toString();
        String ogPassword = null;
        try {
            ogPassword = AES256Chiper.AES_Decode(password);
        } catch (Exception e) {
            ogPassword = "default";
            e.printStackTrace();
        }
        if (nameET.getText().toString().length() == 0)
            Toast.makeText(this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
        else if (passwordET.getText().toString().length() == 0)
            Toast.makeText(this, "기존 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else if (newPassword.length() != 0 && !newPasswordConfirm.equals(newPassword))
            Toast.makeText(this, "새 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        else if (!ogPassword.equals(passwordET.getText().toString())) {
            Log.e("기존 비번", ogPassword);
            Log.e("입력 비번", passwordET.getText().toString());
            Toast.makeText(this, "기존 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        } else {
            if (newPasswordET.getText().toString().length() == 0)
                newPassword = password;
            else {
                try {
                    newPassword = AES256Chiper.AES_Encode(newPassword);
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
            }
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
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
            Call<PostResponse> call = retrofitService.UpdateUser(new UpdateData(Member.user.getEmail(), newPassword, nameET.getText().toString()));
            final String finalNewPassword = newPassword;
            call.enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                    if (response.isSuccessful() && response.body().getResult().equals("OK")) {
                        Toast.makeText(AccountActivity.this, "정보가 수정되었습니다", Toast.LENGTH_SHORT).show();
                        LoginKeeper loginKeeper = new LoginKeeper(AccountActivity.this);
                        loginKeeper.SetBusLinkerLogin(new LoginData(Member.user.getEmail(), finalNewPassword));
                        finish();
                    } else {
                        Log.e("Retrofit", "오류 발생");
                    }
                }

                @Override
                public void onFailure(Call<PostResponse> call, Throwable t) {
                    Toast.makeText(AccountActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    //메뉴 여닫기
    public static void openDrawer() {
        if (!drawerLayout.isDrawerOpen(Gravity.START))
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
