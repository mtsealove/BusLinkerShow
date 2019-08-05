package inha.ac.kr.pdychoo.buslinker.Views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import inha.ac.kr.pdychoo.buslinker.Account.DealListActivity;
import inha.ac.kr.pdychoo.buslinker.Account.LoginActivity;
import inha.ac.kr.pdychoo.buslinker.Account.LoginKeeper;
import inha.ac.kr.pdychoo.buslinker.Entity.CustomAlert;
import inha.ac.kr.pdychoo.buslinker.Entity.Member;
import inha.ac.kr.pdychoo.buslinker.MainActivity;
import inha.ac.kr.pdychoo.buslinker.Network.SetIPActivity;
import inha.ac.kr.pdychoo.buslinker.R;
import inha.ac.kr.pdychoo.buslinker.Restful.DealStatus;
import inha.ac.kr.pdychoo.buslinker.Restful.RetrofitService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;


public class SlideMenu extends RelativeLayout {
    LinearLayout myAccountLayout;
    TextView recentDealTV, nameTV, readyTV, progressTV, completeTV;    //사용자 정보로 표시될 텍스트
    RelativeLayout loginLayout, inquireDealLayout, publicNoticeLayout, faqLayout, memberLayout, logoutLayout;  //하단에 표시될 레이아웃
    ImageView myAccountIcon, appIcon;    //프로필 사진
    Button closeBtn;
    private Context context;

    public SlideMenu(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SlideMenu(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        init();
    }

    public SlideMenu(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.context = context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlideMenu(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init() {
        String inflaterService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(inflaterService);
        View view = inflater.inflate(R.layout.slide_layout, SlideMenu.this, false);
        addView(view);

        //뷰 매칭
        closeBtn = view.findViewById(R.id.closeBtn);
        appIcon = view.findViewById(R.id.appIcon);
        //로그인 했을 떄 보이는 뷰들
        myAccountLayout = view.findViewById(R.id.myAccountLayout);
        myAccountIcon = view.findViewById(R.id.myAccountIcon);
        recentDealTV = view.findViewById(R.id.recentDealTV);
        nameTV = view.findViewById(R.id.nameTV);
        readyTV=view.findViewById(R.id.ReadyTV);
        progressTV=view.findViewById(R.id.progressTV);
        completeTV=view.findViewById(R.id.completeTV);

        //하단 뷰
        loginLayout = view.findViewById(R.id.loginLayout);
        memberLayout = view.findViewById(R.id.memberLayout);
        inquireDealLayout = view.findViewById(R.id.inquireLayout);
        publicNoticeLayout = view.findViewById(R.id.noticeLayout);
        //checkChargeLayout=view.findViewById(R.id.check_chargeLayout);
        faqLayout = view.findViewById(R.id.faqLayout);
        logoutLayout = view.findViewById(R.id.logoutLayout);

        loginLayout.setVisibility(View.VISIBLE);
        logoutLayout.setVisibility(GONE);
        memberLayout.setVisibility(View.GONE);
        myAccountLayout.setVisibility(GONE);

        loginLayout.setOnClickListener(new OnClickListener() {  //로그인 버튼 눌렀을 때
            @Override
            public void onClick(View view) {
                Login();
            }
        });
        logoutLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });
        inquireDealLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InquireLog();
            }
        });
        checkLogin();

        closeBtn.setOnClickListener(CloseListener);
        appIcon.setOnLongClickListener(OnAppIconLongClickListener);
        inquireDealLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DealLog();
            }
        });
    }

    private void Login() {  //로그인 액티비티로 이동
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public void checkLogin() {    //로그인 확인
        if (Member.user != null) { //로그인 되었을 때
            memberLayout.setVisibility(View.VISIBLE);
            myAccountLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
            logoutLayout.setVisibility(View.VISIBLE);
            inquireDealLayout.setVisibility(View.VISIBLE);

            nameTV.setText(Member.user.getName());
            setMyAccountIcon(Member.user.getProfileThumbPath());
            SetRecentDeal(Member.user.getEmail());
        } else {    //로그인 되지 않았을 때
            inquireDealLayout.setVisibility(View.GONE);
            memberLayout.setVisibility(View.GONE);
            myAccountLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
            logoutLayout.setVisibility(GONE);
        }
    }

    public void Logout() {  //로그아웃
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("로그아웃")
                .setMessage("로그아웃하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Member.user = null;   // 사용자를 null로 초기화
                        checkLogin();
                        MainActivity.closeDrawer();
                        LoginKeeper loginKeeper = new LoginKeeper(context);
                        loginKeeper.SetKakaoLogin(false);
                        // 카카오 계정 로그인 취소
                        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Log.e("kakao", "로그아웃");
                            }
                        });
                        loginKeeper.SetBusLinkerLogin();    // 자체계정 로그인 취소
                        //로그아웃 확인 메세지 출력
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("로그아웃")
                                .setMessage("로그아웃되었습니다")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                        builder1.create().show();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    Bitmap bitmap = null;

    private void setMyAccountIcon(final Object urlStr) { //URL에서 파일을 다운받아 실행

        if (urlStr instanceof String) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL((String) urlStr);    //URL변환
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();

                        InputStream is = connection.getInputStream(); //읽어옴
                        bitmap = BitmapFactory.decodeStream(is); //이미지로 변환

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
                myAccountIcon.setBackgroundDrawable(context.getDrawable(R.drawable.round_rectangle));
                myAccountIcon.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (urlStr instanceof Uri) {
            System.out.print("URI입니다");
            myAccountIcon.setImageURI((Uri) urlStr);
        }
    }

    private void InquireLog() { //거래내역 조회
        /*
        Intent intent = new Intent(getContext(), DealLogListActivity.class);
        context.startActivity(intent);
         */
    }

    private OnClickListener CloseListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.closeDrawer();
        }
    };

    private OnLongClickListener OnAppIconLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(20);

            Intent intent = new Intent(context, SetIPActivity.class);
            context.startActivity(intent);
            return false;
        }
    };

    private void SetRecentDeal(String userID){
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
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
        Call<DealStatus> call=retrofitService.GetRecentDeal(userID);
        call.enqueue(new Callback<DealStatus>() {
            @Override
            public void onResponse(Call<DealStatus> call, Response<DealStatus> response) {
                if(response.isSuccessful()){    //데이터 반환 성공
                    DealStatus status=response.body();
                    recentDealTV.setText("최근배송: "+status.getAll()+"건");
                    readyTV.setText("배송준비중\n"+status.getReady());
                    progressTV.setText("배송중\n"+status.getProgress());
                    completeTV.setText("배송완료\n"+status.getComplete());
                }
            }

            @Override
            public void onFailure(Call<DealStatus> call, Throwable t) {
            }
        });
    }


    private void DealLog() {
        Intent intent=new Intent(context, DealListActivity.class);
        context.startActivity(intent);
    }
}
