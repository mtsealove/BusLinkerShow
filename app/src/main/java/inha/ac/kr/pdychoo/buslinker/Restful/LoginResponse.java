package inha.ac.kr.pdychoo.buslinker.Restful;

public class LoginResponse {
    String ID, name, birth;
    char gender;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    @Override
    public String toString(){
        return  "ID: "+ID+" 이름: "+name+" 생년월일: "+birth+" 성별: "+gender;
    }
}
