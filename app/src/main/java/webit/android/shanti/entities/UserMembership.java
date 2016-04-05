package webit.android.shanti.entities;

import com.google.gson.Gson;
import webit.android.shanti.general.BaseShantiObject;

import java.io.Serializable;

/**
 * Created by AndroIT on 19/01/2015.
    */
    public class UserMembership implements  Serializable, BaseShantiObject{

    private int iUserId;
    private String nvUserName;
    private String nvUserPassword;
    private boolean bIsLocked;
    private String nvLastLogin;


    public UserMembership() {
        this.nvUserName = "";
        this.nvUserPassword = "";
    }

    public UserMembership(String nvUserName, String nvUserPassword) {
        this.nvUserName = nvUserName;
        this.nvUserPassword = nvUserPassword;
    }

    public int getiUserId() {
        return iUserId;
    }

    public void setiUserId(int iUserId) {
        this.iUserId = iUserId;
    }

    public String getNvLastLogin() {
        return nvLastLogin;
    }

    public void setNvLastLogin(String nvLastLogin) {
        this.nvLastLogin = nvLastLogin;
    }

    public String getNvUserPassword() {
        return nvUserPassword;
    }

    public void setNvUserPassword(String nvUserPassword) {
        this.nvUserPassword = nvUserPassword;
    }

    public String getNvUserName() {
        return nvUserName;
    }

    public void setNvUserName(String nvUserName) {
        this.nvUserName = nvUserName;
    }

    public boolean isbIsLocked() {
        return bIsLocked;
    }

    public void setbIsLocked(boolean bIsLocked) {
        this.bIsLocked = bIsLocked;
    }

    public boolean isValid() {
        return (nvUserName!=null && !nvUserName.equals("") && nvUserPassword!=null && !nvUserPassword.equals(""));
    }

    @Override
    public String getJson() {
        return new Gson().toJson(this);
    }
}
