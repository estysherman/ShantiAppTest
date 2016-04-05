package webit.android.shanti.entities;

import com.google.gson.Gson;
import webit.android.shanti.general.BaseShantiObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1 on 04/05/15.
 */
public class UsersListDetails implements BaseShantiObject {

    private List<User> users;
    private String usersDetails;//פרטי משתמשים
    private ArrayList<MeetingPoint> meetingPoints;

    public UsersListDetails() {
        this.setUsers(new ArrayList<User>());
        this.setUsersDetails("");
    }

    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getUsersDetails() {
        return usersDetails;
    }

    public void setUsersDetails(String usersDetails) {
        this.usersDetails = usersDetails;
    }

    public ArrayList<MeetingPoint> getMeetingPoints() {
        return meetingPoints;
    }

    public void setMeetingPoints(ArrayList<MeetingPoint> meetingPoints) {
        this.meetingPoints = meetingPoints;
    }

    @Override
    public String getJson() {
        return new Gson().toJson(this);
    }
}
