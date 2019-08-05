package inha.ac.kr.pdychoo.buslinker.Account;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import inha.ac.kr.pdychoo.buslinker.Restful.LoginData;

import java.io.*;
import java.util.ArrayList;

public class LoginKeeper extends AppCompatActivity {
    private File KakaoFile, BusLinkerFile;
    final private Context context;
    public LoginKeeper(Context context) {
        this.context=context;
        KakaoFile=new File(context.getFilesDir()+"kakao.dat");
        BusLinkerFile=new File(context.getFilesDir()+"BL.dat");
    }
    //카카오톡 유지 로그인 설정
    public void SetKakaoLogin(boolean keep){    //로그인 유지 변수
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(KakaoFile));
            bw.write(Boolean.toString(keep));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //카카오톡 로그인 유지 확인
    public boolean GetKakaoLogin() {
        try {
            BufferedReader br=new BufferedReader(new FileReader(KakaoFile));
            String line=br.readLine();
            boolean result=Boolean.parseBoolean(line);
            return result;  //저장된 파일에서 카카오 로그인 여부 판단
        } catch (Exception e) { //오류 발생 시 거짓 값 반환
            e.printStackTrace();
            return false;
        }
    }

    //로그인 데이터 저장(취소)
    public void SetBusLinkerLogin(){
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(BusLinkerFile));
            bw.write("false");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //로그인 데이터 저장
    public void SetBusLinkerLogin(LoginData loginData) {
        try{
            BufferedWriter bw=new BufferedWriter(new FileWriter(BusLinkerFile));
            bw.write("true");
            bw.newLine();
            bw.write(loginData.getID());
            bw.newLine();;
            bw.write(loginData.getPassword());
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //로그인 데이터 반환
    public LoginData GetBusLinkerLogin() {
        try{
            BufferedReader br=new BufferedReader(new FileReader(BusLinkerFile));
            if(Boolean.parseBoolean(br.readLine())==false)
                return null;
            else {
                String ID=br.readLine();
                String password=br.readLine();
                return new LoginData(ID, password);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
