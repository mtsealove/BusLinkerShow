package inha.ac.kr.pdychoo.buslinker;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import inha.ac.kr.pdychoo.buslinker.Entity.Arr;

import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {
    String title;
    ListView listView;
    TextView titleTV;
    ArrayList<String> titleList;
    static DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        titleTV = findViewById(R.id.titleTV);
        listView = findViewById(R.id.listView);
        drawerLayout=findViewById(R.id.drawerLayout);

        title = getIntent().getStringExtra("boardName");
        titleList = new ArrayList();

        switch (title){
            case "notice":
                setNotice();
                break;
            case "FAQ":
                setFAQ();
                break;
        }
    }

    private void setNotice() {
        titleTV.setText("공지사항");
        titleList.add("버스링커 1.0 런칭");
        titleList.add("공지사항 예시");

        ArrayAdapter arrayAdapter=new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, titleList);
        listView.setAdapter(arrayAdapter);
    }

    private void setFAQ() {
        titleTV.setText("FAQ");
        titleList.add("FAQ 예시");
        titleList.add("자주 물어보는 질문입니다");
        titleList.add("무엇이든 물어보세요");

        ArrayAdapter arrayAdapter=new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, titleList);
        listView.setAdapter(arrayAdapter);
    }

    public static void openDrawer() {
        if(!drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.openDrawer(Gravity.START);
    }
    public static void closeDrawer() {
        if(drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START);
    }
}
