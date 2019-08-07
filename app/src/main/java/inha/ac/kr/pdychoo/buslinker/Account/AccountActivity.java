package inha.ac.kr.pdychoo.buslinker.Account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import inha.ac.kr.pdychoo.buslinker.Entity.Member;
import inha.ac.kr.pdychoo.buslinker.Network.SetIPActivity;
import inha.ac.kr.pdychoo.buslinker.R;
import inha.ac.kr.pdychoo.buslinker.Restful.PostResponse;
import inha.ac.kr.pdychoo.buslinker.Restful.RetrofitService;
import inha.ac.kr.pdychoo.buslinker.Restful.UpdateData;
import inha.ac.kr.pdychoo.buslinker.Restful.User;
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
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        SystemUiTuner systemUiTuner=new SystemUiTuner(this);
        systemUiTuner.setStatusBarWhite();

        IDTV=findViewById(R.id.IDTV);
        birthTV=findViewById(R.id.birthTV);
        genderTV=findViewById(R.id.genderTV);
        nameET=findViewById(R.id.nameET);
        newPasswordET=findViewById(R.id.newPasswordET);
        newPasswordConfirmET=findViewById(R.id.newPasswordConfirmET);
        passwordET=findViewById(R.id.passwordET);

        getInfo();
    }

    private void getInfo() {
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://"+ SetIPActivity.IP+":3000")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build();

        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        Call<User> call=retrofitService.GetUser(Member.user.getEmail());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User user=(User)response.body();
                    IDTV.setText(user.getID());
                    nameET.setText(user.getName());
                    switch (user.getGender()){
                        case 'm':
                            genderTV.setText("남성");
                            break;
                        case 'f':
                            genderTV.setText("여성");
                            break;
                    }
                    birthTV.setText((user.getBirth()).substring(0, 10));
                    password=user.getPassword();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void UpdateData() {
        String newPassword=newPasswordET.getText().toString(), newPasswordConfirm=newPasswordConfirmET.getText().toString();
        String ogPassword=null;
        try {
            ogPassword=AES256Chiper.AES_Decode(password);
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
        if(nameET.getText().toString().length()==0)
            Toast.makeText(this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(passwordET.getText().toString().length()==0)
            Toast.makeText(this, "기존 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else if(newPassword.length()!=0&&newPasswordConfirm!=newPassword)
            Toast.makeText(this, "새 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        else if(ogPassword!=passwordET.getText().toString())
            Toast.makeText(this, "기존 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        else {
            if(newPasswordET.getText().toString().length()==0)
                newPassword=password;
            else {
                try {
                    newPassword=AES256Chiper.AES_Encode(newPassword);
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
                OkHttpClient okHttpClient=new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build();

                Retrofit retrofit=new Retrofit.Builder()
                        .baseUrl("http://"+SetIPActivity.IP+":3000")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .client(okHttpClient)
                        .build();

                RetrofitService retrofitService=retrofit.create(RetrofitService.class);
                Call<PostResponse> call=retrofitService.UpdateUser(new UpdateData(newPassword, nameET.getText().toString()));
                call.enqueue(new Callback<PostResponse>() {
                    @Override
                    public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                        Toast.makeText(AccountActivity.this, "정보가 수정되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<PostResponse> call, Throwable t) {
                        Toast.makeText(AccountActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }
}
