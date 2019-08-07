package inha.ac.kr.pdychoo.buslinker.Restful;

public class UpdateData {
    String newPassword,name;

    public UpdateData(String newPassword, String name) {
        this.newPassword = newPassword;
        this.name = name;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
