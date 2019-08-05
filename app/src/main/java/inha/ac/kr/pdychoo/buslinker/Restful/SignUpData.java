package inha.ac.kr.pdychoo.buslinker.Restful;

public class SignUpData {
    String ID, name, birth, password;
    char gender;

    public SignUpData(String ID, String name, String birth, char gender, String password){
        this.ID=ID;
        this.name=name;
        this.birth= birth;
        this.gender=gender;
        this.password=password;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }
}
