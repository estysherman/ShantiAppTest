package webit.android.shanti.entities;

import com.google.gson.Gson;
import webit.android.shanti.general.BaseShantiObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CRM on 23/02/2015.
 */
public class Group implements BaseShantiObject {

    public static String  defultGroupImageUrl = "http://shantiappqa.com/ShantiFiles/Files/Groups/marker_defualt.png";

    private int iGroupId;
    private String nvGroupName;
    private String nvComment;
    private String dtCreateDate;
    private int iMainUserId;
    private int iNumOfMembers;
    private String nvImage;
    private List<User> UsersList;
    private int iGroupType;
    private String nvQBDialogId;
    private String nvQBRoomJid;


    public int getiGroupId() {
        return iGroupId;
    }

    public void setiGroupId(int iGroupId) {
        this.iGroupId = iGroupId;
    }

    public String getNvGroupName() {
        return nvGroupName;
    }

    public void setNvGroupName(String nvGroupName) {
        this.nvGroupName = nvGroupName;
    }

    public String getNvComment() {
        return nvComment;
    }

    public void setNvComment(String nvComment) {
        this.nvComment = nvComment;
    }

    public String getDtCreateDate() {
        return dtCreateDate;
    }

    public void setDtCreateDate(String dtCreateDate) {
        this.dtCreateDate = dtCreateDate;
    }

    public int getiMainUserId() {
        return iMainUserId;
    }

    public void setiMainUserId(int iMainUserId) {
        this.iMainUserId = iMainUserId;
    }

    public int getiNumOfMembers() {
        return iNumOfMembers;
    }

    public void setiNumOfMembers(int iNumOfMembers) {
        this.iNumOfMembers = iNumOfMembers;
    }

    public String getNvImage() {
        return nvImage;
    }

    public void setNvImage(String nvImage) {
        this.nvImage = nvImage;
    }

    public List<User> getUsersList() {
        return UsersList;
    }

    public void setUsersList(List<User> usersList) {
        UsersList = usersList;
    }

    public int getiGroupType() {
        return iGroupType;
    }

    public void setiGroupType(int iGroupType) {
        this.iGroupType = iGroupType;
    }

    public String getNvQBDialogId() {
        return nvQBDialogId;
    }

    public void setNvQBDialogId(String nvQBDialogId) {
        this.nvQBDialogId = nvQBDialogId;
    }

    public String getNvQBRoomJid() {
        return nvQBRoomJid;
    }

    public void setNvQBRoomJid(String nvQBRoomJid) {
        this.nvQBRoomJid = nvQBRoomJid;
    }

    public Group(int iGroupId, String nvGroupName, String nvComment, String dtCreateDate, int iMainUserId, int iNumOfMembers, String nvImage, List<User> usersList, int iGroupType, String nvQBDialogId) {
        this.iGroupId = iGroupId;
        this.nvGroupName = nvGroupName;
        this.nvComment = nvComment;
        this.dtCreateDate = dtCreateDate;
        this.iMainUserId = iMainUserId;
        this.iNumOfMembers = iNumOfMembers;
        this.nvImage = nvImage;
        UsersList = usersList;
        this.iGroupType = iGroupType;
        this.nvQBDialogId = nvQBDialogId;
    }

    public Group() {
        this.setUsersList(new ArrayList<User>());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String toSend() {
        return new Gson().toJson(new GroupToSend(this));
    }

    @Override
    public String getJson() {
        return new Gson().toJson(this);
    }


    public static Group copy(Group group){
        Group copyGroup = new Group(group.getiGroupId(),group.getNvGroupName(),group.getNvComment(), group.getDtCreateDate(),group.getiMainUserId(),group.getiNumOfMembers(),group.getNvImage(),group.getUsersList(),group.getiGroupType(),group.getNvQBDialogId());
        return copyGroup;
    }


    private class GroupToSend {
        Group oGroup;

        public GroupToSend(Group newGroup) {
            this.oGroup = newGroup;
        }
    }


}
