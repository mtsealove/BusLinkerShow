package inha.ac.kr.pdychoo.buslinker.Entity;

import java.util.ArrayList;

public class Terminal { //위치기반에 이용할 터미널 객체
    String terminalName;
    String terminalCode;
    double latitude, longitude;
    double distance=1000000;

    public Terminal(String terminalName, String terminalCode, double latitude, double longitude) {
        this.terminalName=terminalName;
        this.terminalCode=terminalCode;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public void getDistance(double latitude, double longitude) {
        double theta = this.longitude - longitude;
        double dist = Math.sin(deg2rad(this.latitude)) * Math.sin(deg2rad(latitude)) + Math.cos(deg2rad(this.latitude)) * Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1609.344; //미터 단위로 반환
        distance=dist;
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static ArrayList<Terminal> sortTerminalListByDistance(ArrayList<Terminal> terminals) {  //터미널 리스트 정렬
        ArrayList<Terminal> reusltList=new ArrayList<>();
        Terminal[] terminalArray=new Terminal[terminals.size()];
        for(int i=0; i<terminals.size(); i++) { //배열로 변환
            terminalArray[i]=terminals.get(i);
        }
        Terminal tmp;
        for(int i=0; i<terminalArray.length; i++) { //거리 기준으로 버블정렬
            for(int j=0; j<i; j++) {
                if(terminalArray[j].distance>terminalArray[j+1].distance) {
                    tmp=terminalArray[j];
                    terminalArray[j]=terminalArray[j+1];
                    terminalArray[j+1]=tmp;
                }
            }
        }
        //리스트를 다시 생성
        reusltList=new ArrayList<>();
        for(int i=0; i<terminalArray.length; i++)
            reusltList.add(terminalArray[i]);
        return  reusltList;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public String getTerminalCode() {
        return terminalCode;
    }
}
