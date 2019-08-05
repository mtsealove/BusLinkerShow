package inha.ac.kr.pdychoo.buslinker.Entity;

import java.io.Serializable;

public class Deal implements Serializable {
    private String userID, startTmn, startNm, startContact, endTmn, endNm, endContact, divTime, message, paydate;
    private int sideX, sideY, sideZ, weight, price, method, status;

    public Deal(String userID, String startTmn, String startNm, String startContact,
                String endTmn, String endNm, String endContact,
                 String divTime, String message, String paydate,
                int sideX, int sideY, int sideZ, int weight, int price, int method, int status){
        this.userID=userID;
        this.startTmn=startTmn;
        this.startNm=startNm;
        this.startContact=startContact;
        this.endTmn=endTmn;
        this.endNm=endNm;
        this.endContact=endContact;
        this.divTime=divTime;
        this.message=message;
        this.paydate=paydate;
        this.sideX=sideX;
        this.sideY=sideY;
        this.sideZ=sideZ;
        this.weight=weight;
        this.price=price;
        this.method=method;
        this.status=status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStartTmn() {
        return startTmn;
    }

    public void setStartTmn(String startTmn) {
        this.startTmn = startTmn;
    }

    public String getStartNm() {
        return startNm;
    }

    public void setStartNm(String startNm) {
        this.startNm = startNm;
    }

    public String getStartContact() {
        return startContact;
    }

    public void setStartContact(String startContact) {
        this.startContact = startContact;
    }

    public String getEndTmn() {
        return endTmn;
    }

    public void setEndTmn(String endTmn) {
        this.endTmn = endTmn;
    }

    public String getEndNm() {
        return endNm;
    }

    public void setEndNm(String endNm) {
        this.endNm = endNm;
    }

    public String getEndContact() {
        return endContact;
    }

    public void setEndContact(String endContact) {
        this.endContact = endContact;
    }

    public String getDivTime() {
        return divTime;
    }

    public void setDivTime(String divTime) {
        this.divTime = divTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPaydate() {
        return paydate;
    }

    public void setPaydate(String paydate) {
        this.paydate = paydate;
    }

    public int getSideX() {
        return sideX;
    }

    public void setSideX(int sideX) {
        this.sideX = sideX;
    }

    public int getSideY() {
        return sideY;
    }

    public void setSideY(int sideY) {
        this.sideY = sideY;
    }

    public int getSideZ() {
        return sideZ;
    }

    public void setSideZ(int sideZ) {
        this.sideZ = sideZ;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String result="출발 터미널: "+this.startTmn
                +"\n보내는 사람: "+this.startNm
                +"\n보내는 사람 연락처: "+this.startContact
                +"\n받는 터미널: "+this.endTmn
                +"\n받는 사람: "+this.endNm
                +"\n받는 연락처: "+this.endContact
                +"\n배송시간: "+this.divTime
                +"\n가로: "+sideX+" 세로: "+sideY+" 높이: "+sideZ+" 무게: "+weight
                +"\n메세지: "+message
                +"\n결제방법: "+method
                +"\n금액: "+price
                +"\n결제날짜: "+paydate;
        return result;
    }
}
