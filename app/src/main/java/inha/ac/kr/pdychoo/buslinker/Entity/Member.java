package inha.ac.kr.pdychoo.buslinker.Entity;

import java.util.ArrayList;
import java.util.Date;

public class Member {
    public static final int KAKAO_MEMBER = 1, NORMAL_MEMBER = 2;
    public static Member user;   //현재 로그인된 계정
    String email;   //메일 주소
    String name;    //이름
    String gender;  //성별
    String birth;   //생일
    String profilePath; //프로필 사진 경로
    Object profileThumbPath;    //프로필 사진 썸네일 경로
    int category;   //사용자 종류


    public Member() {

    }

    public Member(String email, String name, String gender, String birth, int category, String profilePath) {
        this();
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.birth = birth;
        this.category = category;
        this.profilePath = profilePath;
    }


    public Member(String email, String name, int category) {  //프로필 사진이 없을 경우
        this();
        this.email=email;
        this.name=name;
        this.category=category;
    }

    public Member(String email, String name, String profilePath, String profileThumbPath, int category) {  //카카오 계정으로 프로필 사진이 있을 경우
        this();
        this.email=email;
        this.name=name;
        this.profilePath=profilePath;
        this.category=category;
        this.profileThumbPath=profileThumbPath;
    }

    public Member(Object profilePath, int category) {
        this();
        this.category = category;
        this.profileThumbPath = profilePath;
    }


    @Override
    public String toString() {
        String result = "사용자: 이름: " + name + " 이메일: " + email ;
        return  result;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public int getCategory() {
        return category;
    }

    public Object getProfileThumbPath() {
        return profileThumbPath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public void setProfileThumbPath(String profileThumbPath) {
        this.profileThumbPath = profileThumbPath;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }


}
