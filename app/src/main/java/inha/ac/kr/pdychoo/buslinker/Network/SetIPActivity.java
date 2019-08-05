package inha.ac.kr.pdychoo.buslinker.Network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import inha.ac.kr.pdychoo.buslinker.R;

import java.io.*;

public class SetIPActivity extends AppCompatActivity {
    private EditText IPET;
    private Button IPBtn;
    public static String IP;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);

        IPET=findViewById(R.id.IPET);
        IPBtn=findViewById(R.id.IPBtn);

        //IP 설정 버튼 리스너
        IPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeIP();
            }
        });

        ReadIP();
    }


    //파일에 IP 저장
    private void writeIP() {
        String[] ips=(IPET.getText().toString()).split("\\.");
        if(ips.length!=4){  // IP 입력 검증
            Toast.makeText(this, "올바른 IP를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        IP=IPET.getText().toString();
        file=new File(getFilesDir()+"IP.dat");
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(file));
            bw.write(IP);
            bw.flush();
            bw.close();
            Toast.makeText(this, "새 IP가 저장되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ReadIP() {
        file=new File(getFilesDir()+"IP.dat");
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            IP=bufferedReader.readLine();
            bufferedReader.close();
        } catch (Exception e) {
            IP="192.168.10.31";
        } finally {
            IPET.setText(IP);
        }

    }
}
