package inha.ac.kr.pdychoo.buslinker.Entity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import inha.ac.kr.pdychoo.buslinker.MainActivity;
import inha.ac.kr.pdychoo.buslinker.R;
import inha.ac.kr.pdychoo.buslinker.SearchTerminalActivity;

public class Arr extends LinearLayout {
    private TextView arrTmnNmTV;
    private EditText arrPersonNameET, arrPersonContactET;
    private Button referenceContactBtn;
    private String depTmnCd, depTmnNm, StartTime, arrTmnNm;
    private Context context;
    private int index;  //몇 번째인지 확인할 번호

    public void init() {
        //레이아웃 inflate
        String inflaterService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(inflaterService);
        View view = inflater.inflate(R.layout.list_arrive, Arr.this, false);
        addView(view);

        //뷰 매칭
        arrTmnNmTV=view.findViewById(R.id.arrTmnNmTV);
        arrTmnNmTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SetTerminal();
            }
        });
        arrPersonNameET=view.findViewById(R.id.arrPersonNameET);
        arrPersonContactET=view.findViewById(R.id.arrPersonContactET);
        referenceContactBtn=view.findViewById(R.id.referenceContactBtn);
        referenceContactBtn.setOnClickListener(referenceContactListener);

        arrPersonContactET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    public Arr(Context context, int index) {
        super(context);
        this.context=context;
        this.index=index;
        init();
    }

    public Arr(Context context, @org.jetbrains.annotations.Nullable @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }


    public Arr(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }

    public String getName(){
        return arrPersonNameET.getText().toString();
    }

    public String getContact(){
        return arrPersonContactET.getText().toString();
    }

    public void setDepTmnCd(String depTmnCd) {
        this.depTmnCd=depTmnCd;
    }
    public void setDepTmnNm(String depTmnNm){
        this.depTmnNm=depTmnNm;
    }
    public String getDepTmnNm(){
        return depTmnNm;
    }

    //터미널 검색
    private void SetTerminal() {
        if(depTmnCd==null) {
            Toast.makeText(context, "출발지를 먼저 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        } else if(arrTmnNm!=null){
            Toast.makeText(context, "이미 선택된 목적지는 변경할 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent=new Intent(context, SearchTerminalActivity.class);
        intent.putExtra("node", "end");
        intent.putExtra("depTmnCd", depTmnCd);
        intent.putExtra("depTmnNm", depTmnNm);
        intent.putExtra("StartTime", StartTime);
        intent.putExtra("index", index);
        ((Activity)context).startActivityForResult(intent, MainActivity.CASE_END);
    }

    public void setTeminalTV(String tmnNm){
        arrTmnNm=tmnNm;
        arrTmnNmTV.setText(tmnNm);
    }

    public String getArrTmnNm() {
        return arrTmnNm;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public void setArrPersonNameET(String name) {
        arrPersonNameET.setText(name);
    }

    public void setArrPersonContactET(String contact){
        arrPersonContactET.setText(contact);
    }

    private OnClickListener referenceContactListener=new OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.indexOfArr=index;
            Intent intent=new Intent(Intent.ACTION_PICK);
            intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            ((Activity)context).startActivityForResult(intent, MainActivity.CASE_CONTACT);
        }
    };

}
