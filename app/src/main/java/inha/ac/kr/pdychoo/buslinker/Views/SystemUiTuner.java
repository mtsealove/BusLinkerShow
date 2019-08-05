package inha.ac.kr.pdychoo.buslinker.Views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

public class SystemUiTuner extends AppCompatActivity {
    final private Context context;
    public SystemUiTuner(Context context) {
        this.context=context;
    }

    public void setStatusBarWhite() {   //하얀 배경에 검은 아이콘
        View view=((Activity)context).getWindow().getDecorView();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if(view!=null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                ((Activity)context).getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
            }
        }
    }
    public void setStatusBarYellow() {  //노란 배경에 하얀 아이콘
        View view=((Activity)context).getWindow().getDecorView();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if(view!=null) {
                Window window=((Activity) context).getWindow();
                window.setStatusBarColor(Color.parseColor("#FFCC00"));
            }
        }
    }
}
