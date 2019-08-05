package inha.ac.kr.pdychoo.buslinker;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import inha.ac.kr.pdychoo.buslinker.Entity.Arr;
import inha.ac.kr.pdychoo.buslinker.Entity.Member;
import inha.ac.kr.pdychoo.buslinker.Views.SlideMenu;
import inha.ac.kr.pdychoo.buslinker.Views.SystemUiTuner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private String arrTmnNm, depTmnNm, depTime, arrTime, registrationDate, MSG, depTmnCd;
    private double X=0, Y=0, Z=0, weight=0;
    EditText depPersonNameET, depPersonContactET;
    TextView depTmnNmTV, timeTV, freightInfoTV, inputMsgTV, showMsgTV, StartTimeTV;
    SlideMenu slideMenu;
    Button panelBtn, addBtn;
    RelativeLayout checkPayLayout;
    LinearLayout arrLayout, setDemensionLayout;
    static android.support.v4.widget.DrawerLayout drawerLayout;
    public final static int CASE_START = 3000, CASE_END = 3100, CASE_CONTACT=3200;
    public static int indexOfArr=0;
    private ArrayList<Arr> arrlist;
    private String StartTime = null;
    private int destincationCount=0;    //도착지의 개수

    int price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        depTmnNmTV = findViewById(R.id.depTmnNmTV);
        timeTV = findViewById(R.id.deliveryTimeTV);
        freightInfoTV = findViewById(R.id.freightInfoTV);
        checkPayLayout = findViewById(R.id.checkPayLayout);
        depPersonNameET = findViewById(R.id.depPersonNameET);
        depPersonContactET = findViewById(R.id.depPersonContactET);
        StartTimeTV = findViewById(R.id.setStartTimeTV);
        setDemensionLayout = findViewById(R.id.setDemensionLayout);

        slideMenu = findViewById(R.id.slideMenu);
        panelBtn = findViewById(R.id.panelBtn);
        addBtn = findViewById(R.id.addBtn);
        drawerLayout = findViewById(R.id.drawerLayout);
        inputMsgTV = findViewById(R.id.inputMSGTV);
        showMsgTV = findViewById(R.id.showMsgTV);
        arrLayout = findViewById(R.id.arrLayout);

        //키보드 내리기
        View view=this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //하얀 상태바
        SystemUiTuner systemUiTuner = new SystemUiTuner(MainActivity.this);
        systemUiTuner.setStatusBarWhite();

        checkPayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayMethod();
            }
        }); //결제 방법 선택 다이얼로그 출력

        //패널 버튼
        panelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        //메시지 입력
        inputMsgTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMsg();
            }
        });
        //전화번호 양식
        depPersonContactET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        if (Member.user != null)   //만약 로그인되어 있다면
            depPersonNameET.setText(Member.user.getName()); //사용자 이름을 우선적으로 설정
        ReadPhoneNumber();


        depTmnNmTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StartTime == null) {
                    Toast.makeText(MainActivity.this, "출발 시간을 먼저 선택하세요", Toast.LENGTH_SHORT).show();
                } else
                    SetTerminal("start");
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddArr();
            }
        });

        StartTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime();
            }
        });

        setDemensionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDemension();
            }
        });

        //도착지 배열에 1개 원소 삽입
        arrlist = new ArrayList<>();
        arrlist.add(new Arr(this, 0));
        arrLayout.addView(arrlist.get(0));
        slideMenu.checkLogin();
    }

    private void SetTerminal(String node) {
        Intent intent = new Intent(MainActivity.this, SearchTerminalActivity.class);
        intent.putExtra("node", node);
        startActivityForResult(intent, 3000);
    }

    private boolean ReusePayMethod = false;   //결제 수단을 다시 사용할 것인지 선택

    private void setPayMethod() {   //결제 방법 선택 다이얼로그 출력
        //먼저 모든 정보가 입력되었는지 확인
        int Side = (int) (X + Y + Z);
        int Weight = (int) weight;

        try{
            price=(Side+Weight)*destincationCount*50;
            Log.e("price", String.valueOf(price));
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "화물 정보를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        for(Arr arr:arrlist){   //터미널이 선택되지 않은만큼 도착지 개수 --
            if(arr.getArrTmnNm()==null)
                destincationCount--;
        }

        if (weight==0||X==0||Y==0||Z==0) {
            Toast.makeText(this, "화물정보를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        } else if (price == 0) {
            Toast.makeText(this, "출/도착지를 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        } else if (depPersonNameET.getText().toString().length() == 0) {
            Toast.makeText(this, "보내는 사람의 이름을 적어주세요", Toast.LENGTH_SHORT).show();
            return;
        } else if (depPersonContactET.getText().toString().length() == 0) {
            Toast.makeText(this, "보내는 사람의 연락처를 적어주세요", Toast.LENGTH_SHORT).show();
            return;
        } else {

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pay, null);
        TextView priceTV = view.findViewById(R.id.priceTV);
        priceTV.setText(price + " 원");

        CheckBox reuseCB = view.findViewById(R.id.reuseCB);
        reuseCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ReusePayMethod = b;
            }
        });
        Button mobileCardBtn = view.findViewById(R.id.mobileCardBtn);
        Button virtualAccountBtn = view.findViewById(R.id.virtualAccountBtn);
        Button noAccountBtn = view.findViewById(R.id.noAccountBtn);
        mobileCardBtn.setOnClickListener(PayMethodClickListener);
        virtualAccountBtn.setOnClickListener(PayMethodClickListener);
        noAccountBtn.setOnClickListener(PayMethodClickListener);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    View.OnClickListener PayMethodClickListener = new View.OnClickListener() {    //결제 방법 선택 버튼 리스너
        @Override
        public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, UploadDataActivity.class);
                //계정정보 전송
                try{
                    intent.putExtra("userID", Member.user.getEmail());
                } catch (NullPointerException e){
                    intent.putExtra("userID", "none");
                }
                //출발정보 전송
                intent.putExtra("startTmn", depTmnNm);
                intent.putExtra("startNm", depPersonNameET.getText().toString());
                intent.putExtra("startContact", depPersonContactET.getText().toString());
                //도착정보 전송
                String endTmn="", endNm="", endContact="", divTime="";
                for(Arr arr: arrlist){  //유동적으로 도착정보의 개수 판단
                    endTmn+=arr.getArrTmnNm()+";;";
                    endNm+=arr.getName()+";;";
                    endContact+=arr.getContact()+";;";
                    divTime+=arr.getStartTime()+";;";
                }
                intent.putExtra("endTmn", endTmn);
                intent.putExtra("endNm", endNm);
                intent.putExtra("endContact", endContact);
                intent.putExtra("divTime", divTime);
                //화물정보 전송
                intent.putExtra("sideX", (int)X);
                intent.putExtra("sideY", (int)Y);
                intent.putExtra("sideZ", (int)Z);
                intent.putExtra("weight", (int)weight);
                intent.putExtra("price", price);
                intent.putExtra("message", MSG);
                Date date=new Date();
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                intent.putExtra("paydate", format.format(date));

                switch (view.getId()) {
                    case R.id.mobileCardBtn:    //모바일 카드결제
                        intent.putExtra("method", 0);
                        break;
                    case R.id.virtualAccountBtn:    //가상계좌
                        intent.putExtra("method", 1);
                        break;
                    case R.id.noAccountBtn: //무통장 입금
                        intent.putExtra("method", 2);
                        break;
                }
                startActivity(intent);
        }
    };

    private void ReadPhoneNumber() {    //전화번호 읽어서 입력
        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String PhoneNum = telManager.getLine1Number();
        if (PhoneNum.startsWith("+82")) {
            PhoneNum = PhoneNum.replace("+82", "0");
        }
        depPersonContactET.setText(PhoneNum);
    }

    private void InputMsg() {   //메시지 입력 다이얼로그 출력
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_input_msg, null);
        Button confirmBtn = view.findViewById(R.id.confirmBtn);
        final EditText msgET = view.findViewById(R.id.MsgET);
        msgET.setText(MSG);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        dialog.show();
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSG = msgET.getText().toString();
                if (MSG.length() != 0) {    //글자수가 0이 아니면
                    showMsgTV.setText(MSG);
                    showMsgTV.setVisibility(View.VISIBLE);
                } else {
                    showMsgTV.setVisibility(View.GONE);
                }
                dialog.cancel();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        slideMenu.checkLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CASE_START:
                    depTmnNm = data.getStringExtra("tmnNm");
                    depTmnCd = data.getStringExtra("tmnCd");    //출발 터미널 코드 활성화
                    depTmnNmTV.setText(depTmnNm);

                    //각 도착지 객체에 출발 터미널 설정
                    for (Arr arr : arrlist) {
                        arr.setDepTmnCd(depTmnCd);
                        arr.setDepTmnNm(depTmnNm);
                        arr.setStartTime(StartTime);
                    }
                    break;
                case CASE_END:  //도착 터미널인 경우
                    String arrTime = data.getStringExtra("arrTime");
                    String depTime = data.getStringExtra("depTime");
                    int index = data.getIntExtra("index", 0);
                    String arrTmnNm = data.getStringExtra("arrTmnNm");
                    Log.e("최종 결과", "터미널명: " + arrTmnNm + " 출발시간: " + depTime + " 도착시간: " + arrTime + " 번호: " + index);


                    arrlist.get(index).setTeminalTV(arrTmnNm);
                    String temp = timeTV.getText().toString();
                    if (temp.length() != 0) temp += "\n";
                    arrTmnNm = arrTmnNm.split("\\(")[0];
                    temp += arrTmnNm + ": " + depTime + "~" + arrTime;
                    timeTV.setText(temp);

                    destincationCount++;    //도착지 추가
                    break;
                case CASE_CONTACT:  //연락처 선택
                    Cursor cursor=getContentResolver().query((data.getData()), new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                    cursor.moveToFirst();
                    String name=cursor.getString(0);
                    String number=cursor.getString(1);
                    number=number.replace("+82", "0");
                    cursor.close();
                    arrlist.get(indexOfArr).setArrPersonNameET(name);
                    arrlist.get(indexOfArr).setArrPersonContactET(number);
                    break;
            }
        }
    }

    private int maxArr = 10;

    //화면에 터미널 선택창 추가
    private void AddArr() {
        if (arrlist.size() < maxArr) {
            arrlist.add(new Arr(this, arrlist.size()));
            arrLayout.addView(arrlist.get(arrlist.size() - 1));
            //객체에 출발지 정보 할당
            arrlist.get(arrlist.size() - 1).setDepTmnNm(depTmnNm);
            arrlist.get(arrlist.size() - 1).setDepTmnCd(depTmnCd);
            arrlist.get(arrlist.size() - 1).setStartTime(StartTime);
            if (arrlist.size() == maxArr)
                addBtn.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "도착지는 10개를 초과할 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTime() {
        //현재시간 변환
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat MinFormat = new SimpleDateFormat("mm");
        SimpleDateFormat HourFormat = new SimpleDateFormat("HH");
        int Min = Integer.parseInt(MinFormat.format(date));
        int Hour = Integer.parseInt(HourFormat.format(date));
        TimePickerDialog pickerDialog = new TimePickerDialog(this, timeSetListener, Hour, Min, false);
        pickerDialog.show();
    }

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            String hour = "";
            String min = "";
            if (i < 10) hour += "0";
            if (i1 < 10) min += "0";
            hour += i;
            min += i1;
            StartTime = hour + ":" + min;
            StartTimeTV.setText("출발시간: " + StartTime);
        }
    };

    // 화물정보 입력
    private void setDemension() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_input_xyz, null);
        final EditText XET, YET, ZET, weightET;
        XET = layout.findViewById(R.id.XET);
        YET = layout.findViewById(R.id.YET);
        ZET = layout.findViewById(R.id.ZET);
        weightET = layout.findViewById(R.id.weightET);

        Button confirmBtn = layout.findViewById(R.id.confirmBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (XET.getText().length() == 0)
                    Toast.makeText(MainActivity.this, "가로 길이를 입력하세요", Toast.LENGTH_SHORT).show();
                else if (YET.getText().toString().length() == 0)
                    Toast.makeText(MainActivity.this, "세로길이를 입력하세요", Toast.LENGTH_SHORT).show();
                else if (ZET.getText().toString().length() == 0)
                    Toast.makeText(MainActivity.this, "높이를 입력하세요", Toast.LENGTH_SHORT).show();
                else if (weightET.getText().toString().length() == 0)
                    Toast.makeText(MainActivity.this, "무게를 입력하세요", Toast.LENGTH_SHORT).show();
                else {
                    X = Double.parseDouble(XET.getText().toString());
                    Y = Double.parseDouble(YET.getText().toString());
                    Z = Double.parseDouble(ZET.getText().toString());
                    weight = Double.parseDouble(weightET.getText().toString());

                    freightInfoTV.setText("가로: " + X + "cm 세로: " + Y + "cm 높이: " + Z + "cm\n" + "무게: " + weight + "kg");
                    dialog.cancel();
                }
            }
        });
        dialog.show();
    }

    public static void closeDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START);
    }

    //뒤로 가기 2번 눌러 종료
    long pressTime = 0;
    @Override
    public void onBackPressed() {

        long currentTime = System.currentTimeMillis();
        long intervalTime = currentTime - pressTime;

        if(drawerLayout.isDrawerOpen(Gravity.START)){
            drawerLayout.closeDrawer(Gravity.START);
        } else if(intervalTime <2000){
            super.onBackPressed();
            finishAffinity();
        }else{
            pressTime = currentTime;
            Toast.makeText(this,"'뒤로가기' 버튼을한 번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }

    }
}