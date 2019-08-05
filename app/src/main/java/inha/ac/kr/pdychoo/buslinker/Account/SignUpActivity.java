package inha.ac.kr.pdychoo.buslinker.Account;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import inha.ac.kr.pdychoo.buslinker.Network.SetIPActivity;
import inha.ac.kr.pdychoo.buslinker.R;
import inha.ac.kr.pdychoo.buslinker.Restful.RetrofitService;
import inha.ac.kr.pdychoo.buslinker.Restful.SignUpData;
import inha.ac.kr.pdychoo.buslinker.Restful.PostResponse;
import inha.ac.kr.pdychoo.buslinker.Views.SystemUiTuner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SignUpActivity extends AppCompatActivity {
    Button signUpBtn;
    EditText nameET, emailET, passwordET, passwordCheckET;
    TextView birthTV;
    RadioGroup genderGroup;
    CheckBox acceptAllCB, accept1CB, accept2CB, accept3CB;
    private String birth = null;
    private char gender = 'e';

    private boolean EmailExist = true;   //메일이 존재하는지 확인할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SystemUiTuner systemUiTuner = new SystemUiTuner(SignUpActivity.this);
        systemUiTuner.setStatusBarYellow();

        //회원정보
        nameET = findViewById(R.id.nameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        passwordCheckET = findViewById(R.id.checkPasswordET);
        birthTV = findViewById(R.id.birthTV);
        birthTV.setOnClickListener(DateSetListener);

        genderGroup = findViewById(R.id.genderRG);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.maleRB:
                        gender = 'm';
                        break;
                    case R.id.femaleRB:
                        gender = 'f';
                        break;
                }
            }
        });
        signUpBtn = findViewById(R.id.signUpBtn);
        //약관 체크박스들
        acceptAllCB = findViewById(R.id.acceptAllCB);
        accept1CB = findViewById(R.id.accept1CB);
        accept2CB = findViewById(R.id.accept2CB);
        accept3CB = findViewById(R.id.accept3CB);

        acceptAllCB.setOnCheckedChangeListener(AllCheckListener);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });
    }

    protected void SignUp() {   //회원가입
        if (CheckData() && IsAcceptChecked()) {    //데이터 입력 및 약관 동의 확인
            String email = emailET.getText().toString();
            String name = nameET.getText().toString();
            String password = passwordET.getText().toString();

            try {
                password=AES256Chiper.AES_Encode(password);
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

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + SetIPActivity.IP + ":3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            RetrofitService retrofitService = retrofit.create(RetrofitService.class);
            Call<PostResponse> call = retrofitService.PostSignUp(new SignUpData(email, name, birth, gender, password));
            call.enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                    if (response.isSuccessful() && response.body().getResult().equals("OK")) {//회원가입에 성공한 경우
                        AlertDialog.Builder builder=new AlertDialog.Builder(SignUpActivity.this);
                        builder.setTitle("성공")
                                .setMessage("회원가입에 성공했습니다")
                                .setCancelable(false)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    } else {    //쿼리 오류인 경우
                        Toast.makeText(SignUpActivity.this, "중복된 ID가 있습니다", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PostResponse> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this, "서버 연결에 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    protected boolean CheckData() {    //모든 정보가 제대로 입력되었는지 확인
        boolean result = false;
        String email = emailET.getText().toString();
        String name = nameET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordCheck = passwordCheckET.getText().toString();

        if (email.length() == 0) toast("이메일을 입력해주세요");
        else if ((!email.contains("@")) || ((!email.contains(".com")) && (!email.contains(".kr")) && (!email.contains(".net"))))
            toast("올바른 이메일을 입력해주세요");
        else if (name.length() == 0) toast("이름을 입력해주세요");
        else if (birth == null) toast("생년월일을 입력해주세요");
        else if (gender == 'e') toast("성별을 선택해주세요");
        else if (password.length() == 0) toast("비밀번호를 입력해주세요");
        else if (passwordCheck.length() == 0) toast("비밀번호를 확인해주세요");
        else if (password.length() < 8 || password.length() > 15) toast("비밀번호는 8자 이상 15자 이하여야 합니다");
        else if (!CheckPasswordRight(password)) toast("비밀번호는 대/소문자 숫자가 모두 포함되어야 합니다");
        else if (!password.equals(passwordCheck)) toast("비밀번호가 일치하지 않습니다");
        else result = true;   //모든 정보가 제대로 입력되었을 경우

        return result;
    }

    protected boolean CheckPasswordRight(String password) { //대/소문자 및 숫자가 모두 입력되었는지 확인
        boolean Upper = false;
        boolean Lower = false;
        boolean Digit = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) Upper = true;
            else if (Character.isLowerCase(password.charAt(i))) Lower = true;
            else if (Character.isDigit(password.charAt(i))) Digit = true;
        }

        return Upper && Lower && Digit;
    }

    protected boolean IsAcceptChecked() {   //모든 필수 약관이 체크되었는지 확인
        if (accept1CB.isChecked() && accept2CB.isChecked()) return true;
        else {
            toast("필수 약관에 동의해주세요");
            return false;
        }
    }

    CompoundButton.OnCheckedChangeListener AllCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            //모든 체크를 체크하게 설정
            accept1CB.setChecked(b);
            accept2CB.setChecked(b);
            accept3CB.setChecked(b);
        }
    };

    private View.OnClickListener DateSetListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Date date = new Date();
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
            int year = Integer.parseInt(yearFormat.format(date));
            int month = Integer.parseInt(monthFormat.format(date)) - 1;
            int day = Integer.parseInt(dayFormat.format(date));
            DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this, onDateSetListener, year, month, day);
            datePickerDialog.show();
        }
    };

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            birth = i + "-" + (i1 + 1) + "-" + i2;
            birthTV.setText(birth);
        }
    };

    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
