package webit.android.shanti.entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.quickblox.users.model.QBUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.general.BaseShantiObject;
import webit.android.shanti.main.map.MapFragment;


/**
 * Created by AndroIT on 19/01/2015.
 */
public class User extends BaseMarker implements Serializable, Parcelable, BaseShantiObject, Comparable<User> {

    public static String defaultUserImageUrl = "http://shantiappqa.com/ShantiFiles/Files/Users/defaultUser.jpg";

    private int iUserId;
    private String nvFacebookUserId;
    private String nvGoogleUserId;
    private int iUserType;
    private String nvFirstName;
    private String nvLastName;
    private String nvEmail;
    private boolean bIsActive;
    private String nvShantiName;
    private int iUserStatusId;
    private String nvPhone;
    private int PhonePrefix;
    private String dtBirthDate;
    private CodeValue oCountry;
    private int iReligionId;
    private String sReligion;
    private int iReligiousLevelId;
    private String sReligiousLevel;
    private String nvProfession;
    private String nvHobby;
    private transient String loginImage;
    private String nvImage;
    private String nvPhonePrefix;
    private String nvAboutMe;
    private String nvCreateDate;
    private String nvLastBroadcastDate;
    private Date LastBroadcastDate = new Date();
    private List<KeyValue> oLanguages;
    private UserMembership oUserMemberShip;
    private UserQuickBlox oUserQuickBlox=new UserQuickBlox();
    private QBUser qbUser;
    private int iGenderId;
    private int iAppLanguageId;
    public static boolean isComparable = true;
    private final int iDeviceTypeId = 456;
    private String nvTokenId;
    //private transient Bitmap processedBitmap;
    private boolean bIsMainUser;
    private int iNumGroupAsMainUser;
    //    private Date nvBirthDate;
    private int iNumGroupAsMemberUser;
    private transient String nvLastMessage;
    private transient int distance = 0;
    private boolean bIsLocked;


    private Address oAddress=new Address();
    //private Location oLocation;
    private double Distance;
    private int waintingMessages;
    private boolean didLoginToQB;
    private String sDistanceString;
    private Date  dtLastBroadcastDate;
    private String nvDistance;
    private int iDistance;
    private boolean isOnline;

    public Address getoAddress() {
        return oAddress;
    }

    public void setoAddress(Address oAddress) {
        this.oAddress = oAddress;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public int getiDistance() {
        return iDistance;
    }

    public void setiDistance(int iDistance) {
        this.iDistance = iDistance;
    }

    public String getNvDistance() {
        return nvDistance;
    }

    public void setNvDistance(String nvDistance) {
        this.nvDistance = nvDistance;
    }

    public Date getDtLastBroadcastDate() {
        return dtLastBroadcastDate;
    }

    public void setDtLastBroadcastDate(Date dtLastBroadcastDate) {
        this.dtLastBroadcastDate = dtLastBroadcastDate;
    }

    public String getsDistanceString() {
        return sDistanceString;
    }

    public void setsDistanceString(String sDistanceString) {
        this.sDistanceString = sDistanceString;
    }

    public boolean isDidLoginToQB() {
        return didLoginToQB;
    }

    public void setDidLoginToQB(boolean didLoginToQB) {
        this.didLoginToQB = didLoginToQB;
    }

    public int getWaintingMessages() {
        return waintingMessages;
    }

    public void setWaintingMessages(int waintingMessages) {
        this.waintingMessages = waintingMessages;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    /*@Override
    public Location getoLocation() {
        return oLocation;
    }

    @Override
    public void setoLocation(Location oLocation) {
        this.oLocation = oLocation;
    }*/



    public boolean isbIsLocked() {
        return bIsLocked;
    }

    public void setbIsLocked(boolean bIsLocked) {
        this.bIsLocked = bIsLocked;
    }

    public Integer getDistance() {
        return distance;
    }

    public String getLoginImage() {
        return loginImage;
    }

    public void setLoginImage(String loginImage) {
        this.loginImage = loginImage;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public User() {
        this.iUserId = -1;
        this.dtBirthDate = "";
        this.nvFacebookUserId = "";
        this.nvGoogleUserId = "";
        this.iGenderId = -1;
        this.iReligionId = -1;
        this.iReligiousLevelId = -1;
        this.oCountry = new CodeValue(-1,"");
        this.iAppLanguageId = Utils.getIntDefaultLocale();
        setoLocation(new Location(0, 0, 0));

    }
    public boolean getbIsLocked() {return bIsLocked;}

    public String getNvPhonePrefix() {
        return nvPhonePrefix;
    }

    public void setNvPhonePrefix(String nvPhonePrefix) {
        this.nvPhonePrefix = nvPhonePrefix;
    }

    public String getNvAboutMe() {
        return nvAboutMe;
    }

    public void setNvAboutMe(String nvAboutMe) {
        this.nvAboutMe = nvAboutMe;
    }

    public String getNvBirthDate() {
        //todo change for live
        return dtBirthDate;
//        return Utils.convertJsonDateToString(nvBirthDate);
    }

    public void setNvBirthDate(String nvBirthDate) {
        this.dtBirthDate = nvBirthDate;
        //todo change live
//        this.nvBirthDate = Utils.convertStringToDate(nvBirthDate);
    }

    public int getiAppLanguageId() {
        return iAppLanguageId;
    }

    public void setiAppLanguageId(int iAppLanguageId) {
        this.iAppLanguageId = iAppLanguageId;
    }

    public User(int iUserId) {
        this.iUserId = iUserId;
    }

    public int getiDeviceTypeId() {
        return iDeviceTypeId;
    }

    public String getNvTokenId() {
        return nvTokenId;
    }

    public boolean isbIsActive() {
        return bIsActive;
    }

    public void setbIsActive(boolean bIsActive) {
        this.bIsActive = bIsActive;
    }

    public String getNvFacebookUserId() {
        return nvFacebookUserId;
    }

    public void setNvFacebookUserId(String nvFacebookUserId) {
        this.nvFacebookUserId = nvFacebookUserId;
    }


    public String getNvGoogleUserId() {
        return nvGoogleUserId;
    }

    public void setNvGoogleUserId(String nvGoogleUserId) {
        this.nvGoogleUserId = nvGoogleUserId;
    }

    public void setNvTokenId(String nvTokenId) {
        this.nvTokenId = nvTokenId;
    }

    public Date getLastBroadcastDate() {
        return LastBroadcastDate;
    }

    public boolean setLastBroadcastDate(Date lastBroadcastDate) {
        LastBroadcastDate = lastBroadcastDate;
        return true;
    }

    @Override
    public String getMarkerId() {
        return MapFragment.MarkerType.UNIQUE_USER.toString() + getiUserId();
    }

    public List<KeyValue> getoLanguages() {
        return oLanguages;
    }

    public void setoLanguages(List<KeyValue> oLanguages) {
        this.oLanguages = oLanguages;
    }

    public User(String nvEmail) {
        this.nvEmail = nvEmail;
    }

    public String getNvShantiName() {
        return nvShantiName;
    }

    public void setNvShantiName(String nvShantiName) {
        this.nvShantiName = nvShantiName;
    }


//    public Bitmap getProcessedBitmap() {
//        return processedBitmap;
//    }
//
//    public void setProcessedBitmap(Bitmap processedBitmap) {
//        this.processedBitmap = processedBitmap;
//    }

    public UserQuickBlox getoUserQuickBlox() {
        return oUserQuickBlox;
    }

    public void setoUserQuickBlox(UserQuickBlox oUserQuickBlox) {
        this.oUserQuickBlox = oUserQuickBlox;
    }

    public String getUserName() {
        //if (nvFirstName.length() > 0) {
        //    return nvFirstName;
        //}
        //return nvShantiName;
        return nvFirstName;
    }

    public QBUser getQbUser() {
        return qbUser;
    }

    public void setQbUser(QBUser qbUser) {
        this.qbUser = qbUser;
    }

    public int getiGenderId() {
        return iGenderId;
    }

    public void setiGenderId(int iGenderId) {
        this.iGenderId = iGenderId;
    }

    public UserMembership getoUserMemberShip() {
        return oUserMemberShip;
    }

    public void setoUserMemberShip(UserMembership oUserMemberShip) {
        this.oUserMemberShip = oUserMemberShip;
    }

    public CodeValue getoCountry() {
        return oCountry;
    }

    public void setoCountry(CodeValue oCountry) {
        this.oCountry = oCountry;
    }

    public String getNvCreateDate() {
        return nvCreateDate;
    }

    public void setNvCreateDate(String nvCreateDate) {
        this.nvCreateDate = nvCreateDate;
    }

    public String getNvLastBroadcastDate() {
        return nvLastBroadcastDate;
    }
//
//    public void setNvLastBroadcastDate(String nvLastBroadcastDate) {
//        this.nvLastBroadcastDate = nvLastBroadcastDate;
//        this.setLastBroadcastDate(Utils.convertToJsonDate(nvLastBroadcastDate));
//    }

    public int getiUserId() {
        return iUserId;
    }

    public void setiUserId(int iUserId) {
        this.iUserId = iUserId;
    }

    public int getiUserType() {
        return iUserType;
    }

    public void setiUserType(int iUserType) {
        this.iUserType = iUserType;
    }

    public String getNvFirstName() {
        return nvFirstName;
    }

    public void setNvFirstName(String nvFirstName) {
        this.nvFirstName = nvFirstName;
    }

    public String getNvLastName() {
        return nvLastName;
    }

    public void setNvLastName(String nvLastName) {
        this.nvLastName = nvLastName;
    }

    public String getNvEmail() {
        return nvEmail;
    }

    public void setNvEmail(String nvEmail) {
        this.nvEmail = nvEmail;
    }

    public int getiUserStatusId() {
        return iUserStatusId;
    }

    public void setiUserStatusId(int iUserStatusId) {
        this.iUserStatusId = iUserStatusId;
    }

    public String getNvPhone() {
        return nvPhone;
    }

    public void setNvPhone(String nvPhone) {
        this.nvPhone = nvPhone;
    }

    public int getPhonePrefix() {
        return PhonePrefix;
    }

    public void setPhonePrefix(int phonePrefix) {
        PhonePrefix = phonePrefix;
    }


    public int getiReligionId() {
        return iReligionId;
    }

    public void setiReligionId(int iReligionId) {
        this.iReligionId = iReligionId;
    }

    public boolean isbIsMainUser() {
        return bIsMainUser;
    }

    public void setbIsMainUser(boolean bIsMainUser) {
        this.bIsMainUser = bIsMainUser;
    }


    public String getsReligion() {
        return sReligion;
    }

    public void setsReligion(String sReligion) {
        this.sReligion = sReligion;
    }

    public int getiReligiousLevelId() {
        return iReligiousLevelId;
    }

    public void setiReligiousLevelId(int iReligiousLevelId) {
        this.iReligiousLevelId = iReligiousLevelId;
    }

    public String getsReligiousLevel() {
        return sReligiousLevel;
    }

    public void setsReligiousLevel(String sReligiousLevel) {
        this.sReligiousLevel = sReligiousLevel;
    }

    public String getNvProfession() {
        return nvProfession;
    }

    public void setNvProfession(String nvProfession) {
        this.nvProfession = nvProfession;
    }

    public String getNvHobby() {
        return nvHobby;
    }

    public void setNvHobby(String nvHobby) {
        this.nvHobby = nvHobby;
    }

    public String getNvImage() {
        return nvImage;
    }

    public void setNvImage(String nvImage) {
        this.nvImage = nvImage;
    }

    public String getFullName() {
        return nvFirstName + " " + nvLastName;
    }

    public String getUserInfo(Context context) {
        return context.getString(R.string.userInfoText, getiNumGroupAsMemberUser(), getiNumGroupAsMainUser());
    }


    public String getNvLastMessage() {
        return nvLastMessage;
    }

    public void setNvLastMessage(String nvLastMessage) {
        this.nvLastMessage = nvLastMessage;
    }

    public int getiNumGroupAsMainUser() {
        return iNumGroupAsMainUser;
    }

    public void setiNumGroupAsMainUser(int iNumGroupAsMainUser) {
        this.iNumGroupAsMainUser = iNumGroupAsMainUser;
    }

    public int getiNumGroupAsMemberUser() {
        return iNumGroupAsMemberUser;
    }

    public void setiNumGroupAsMemberUser(int iNumGroupAsMemberUser) {
        this.iNumGroupAsMemberUser = iNumGroupAsMemberUser;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String toSend() {
        if (oUserMemberShip == null) {
            oUserMemberShip = new UserMembership();
        }
        if (oLanguages == null) {
            oLanguages = new ArrayList<>();
        }
        return new Gson().toJson(new UserToSend(this));//this - המשתמש שעכשיו נרשם לאפליקציה
    }

    private boolean isStringEmpty(String s) {
        return !(s != null && !s.equalsIgnoreCase(""));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public String getJson() {
        return new Gson().toJson(this);
    }


    @Override
    public int compareTo(User user) {//בדיקה לפי תאריך ואז לפי מרחק

        if (!isComparable) {//straight search
            int i = -1;//LastBroadcastDate
            int j = -1;//Distance
            if (user.getLastBroadcastDate() == null || this.getLastBroadcastDate() == null) {//if one of the two LastBroadcastDate is missing
                j = user.getDistance().compareTo(this.getDistance());//compare Distance
                if (j == 0) {//if Distance equal
                    return this.getFullName().compareTo(user.getFullName());//compare FullName
                } else {
                    return j;
                }
            } else {
                i = user.getLastBroadcastDate().compareTo(this.getLastBroadcastDate());//compare LastBroadcastDate
                if (i == 0) {//if LastBroadcastDate equal
                    j = user.getDistance().compareTo(this.getDistance());//compare Distance
                    if (j == 0) {//if Distance equal
                        return this.getFullName().compareTo(user.getFullName());//compare FullName
                    } else {
                        return j;
                    }
                } else
                    return i;
            }
        } else {//reverse search
            int i = -1;
            int j = -1;
            if (this.getLastBroadcastDate() == null || user.getLastBroadcastDate() == null) {
                j = this.getDistance().compareTo(user.getDistance());
                if (j == 0) {
                    return user.getFullName().compareTo(this.getFullName());
                } else {
                    return j;
                }
            } else {
                i = this.getLastBroadcastDate().compareTo(user.getLastBroadcastDate());
                if (i == 0) {
                    j = this.getDistance().compareTo(user.getDistance());
                    if (j == 0) {
                        return user.getFullName().compareTo(this.getFullName());
                    } else {
                        return j;
                    }
                } else
                    return i;
            }
        }
    }


    private class UserToSend {
        User newUser;

        public UserToSend(User newUser) {
            this.newUser = newUser;
        }
    }

    public static List<Integer> getUsersId(List<User> users) {

        List<Integer> ids = new ArrayList<>();
        for (User user : users)
            ids.add(user.getiUserId());
        return ids;
    }

    public QBUser getQBUserToChat() {
        QBUser user = new QBUser();
        user.setId(getoUserQuickBlox().getID());
        user.setLogin(getoUserQuickBlox().getLogin());
        user.setPassword(getoUserQuickBlox().getPassword());
        return user;
    }


    public ArrayList<QBUser> getUsersToChat() {
        ArrayList<QBUser> newUsers = new ArrayList<>();
        QBUser user = getQBUserToChat();
        setQbUser(user);
        newUsers.add(user);
        return newUsers;
    }


}
