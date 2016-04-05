package webit.android.shanti.entities;

import com.google.gson.Gson;
import com.quickblox.users.model.QBUser;
import webit.android.shanti.general.BaseShantiObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CRM on 17/02/2015.
 */
public class UserQuickBlox implements BaseShantiObject {

    private int iUserId;
    private int ID;
    private String login;
    private String password;

    public int getiUserId() {
        return iUserId;
    }

    public void setiUserId(int iUserId) {
        this.iUserId = iUserId;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserQuickBlox(int iUserId, int ID, String login, String password) {
        this.iUserId = iUserId;
        this.ID = ID;
        this.login = login;
        this.password = password;
    }

    public UserQuickBlox() {
    }

    public static User getUserByQbUserId(List<User> users, int qbUserId) {
        for (User user : users)
            if (user.getoUserQuickBlox().getID() == qbUserId)
                return user;
        return null;
    }

    public static List<QBUser> getQbUsersList(List<User> users) {
        List<QBUser> qbUsers = new ArrayList<>();
        for (User user : users)
            qbUsers.add(user.getQBUserToChat());
        return qbUsers;
    }

    public static ArrayList<Integer> getQbUsersIdsList(List<User> users) {
        ArrayList<Integer> qbUsers = new ArrayList<>();
        for (User user : users)
            qbUsers.add(user.getoUserQuickBlox().getID());
        return qbUsers;
    }

    public static User getUserByQbId(List<User> users, int qbId) {
        for (User user : users)
            if (user.getoUserQuickBlox().getiUserId() == qbId)
                return user;
        return null;
    }

    @Override
    public String getJson() {
        return new Gson().toJson(this);
    }
}
