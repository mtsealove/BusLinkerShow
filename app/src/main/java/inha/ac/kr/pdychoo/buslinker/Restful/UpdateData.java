package inha.ac.kr.pdychoo.buslinker.Restful;

public class UpdateData {
    String userID, newPassword,name;

    public UpdateData(String userID, String newPassword, String name) {
        this.userID=userID;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
