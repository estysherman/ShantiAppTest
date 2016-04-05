package webit.android.shanti.general.connection;

import android.util.Log;

import webit.android.shanti.BuildConfig;

public class ConnectionUtil {

    public static String getServerUrl() {
//        return "http://10.0.0.175/ShantiWS/Service1.svc/";
        //כשמחליפים שרת - לשים לב לשנות ב user את הניתוב של defaultUserImageUrl
        //ושל defultGroupImageUrl ב GROUP
        if (BuildConfig.DEBUG)
            //return "http://qa.webit-track.com/ShantiWS/Service1.svc/";
            //return "http://qa.shantiapp.com/ws/Service1.svc/";
            return "http://shantiappqa.com/ws/Service1.svc/";
        else
            return "http://www.shantiapp.com/ws/Service1.svc/";
    }

    public static final String GetUsersList = "GetUsersList";

    //Login & User
    public static final String SetLocation = "SetLocation";
    public static final String SetLocationGetUsersList = "SetLocationGetUsersList";
    public static final String SetLocationGetFilterUsersList = "SetLocationGetFilterUsersList";
    public static final String SetUserSearchDef = "SetUserSearchDef";
    public static final String GetUserSearchDef = "GetUserSearchDef";
    public static final String SetUser = "SetUser";
    public static final String GetUser = "GetUser";
    public static final String LoginUser = "Login";
    public static final String UpdateUser = "UpdateUser";
    public static final String UpdateTokenAndDevice = "UpdateTokenAndDevice";
    public static final String CheckUserDetailsIsFree = "CheckUserDetailsIsFree";
    public static final String GetUsersBySearchText = "GetUsersBySearchText";
    public static final String GetUsersByQBIdList = "GetUsersByQBIdList";
    public static final String ForgotPassword = "ForgotPassword";
    public static final String LogOut = "LogOut";
    public static final String SendVerificationCode = "SendVerificationCode";
    public static final String GetUsersListToSearch = "GetUsersListToSearch";
    public static final String SearchName = "SearchName";
    public static final String FilterName = "FilterName";
    public static final String FilterDistance = "FilterDistance";
    public static final String ChangeLogin = "ChangeLogin";



    public static final String LoginGoogle = "LoginGoogle";
    public static final String LoginFacebook = "LoginFacebook";
    public static final String UpdateGoogleId = "UpdateGoogleId";
    public static final String UpdateFacebookId = "UpdateFacebookId";


    //Groups
    public static final String GetUserGroups = "GetUserGroups";
    public static final String GetGroup = "GetGroup";
    public static final String CreateNewGroup = "CreateNewGroup";
    public static final String GetUsersListByGroup = "GetUsersListByGroup";
    public static final String AddUserToGroup = "AddUserToGroup";
    public static final String GetUserByUserName = "GetUserByUserName";
    public static final String ApprovalUserGroup = "ApprovalUserGroup";
    public static final String RejectUserGroup = "RejectUserGroup";
    public static final String GetUserPendingGroups = "GetUserPendingGroups";
    public static final String GetUserGroupsAsMain = "GetUserGroupsAsMain";
    public static final String GetUserMeetingPoints = "GetUserMeetingPoints";
    public static final String CreateNewMeetingPoint = "CreateNewMeetingPoint";
    public static final String DeleteGroup = "DeleteGroup";
    public static final String UpdateGroup = "UpdateGroup";
    public static final String RemoveUserFromGroup = "RemoveUserFromGroup";


    //push
    public static final String PushUpdates = "PushUpdates";

    public static final String GetCodeTable = "GetCodeTable";
    public static final String GetCodeTableParam = "GetCodeTableParam";
   public static final String GetLanguagesCodeTable ="GetLanguagesCodeTable";
    //Google places
    public static final String GetGooglePlaces = "GetGooglePlaces";


}
