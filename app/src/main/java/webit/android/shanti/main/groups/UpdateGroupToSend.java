package webit.android.shanti.main.groups;

import com.google.gson.Gson;

import java.util.List;

import webit.android.shanti.entities.Group;
import webit.android.shanti.general.BaseShantiObject;

/**
 * Created by user on 09/06/2015.
 */
public class UpdateGroupToSend implements BaseShantiObject {
    private Group oGroup;
    private List<Integer> AddingUsers;
    private List<Integer> DeletingUsers;

    public UpdateGroupToSend(Group oGroup, List<Integer> addingUsers, List<Integer> deletingUsers) {
        this.oGroup = oGroup;
        AddingUsers = addingUsers;
        DeletingUsers = deletingUsers;
    }

    public Group getoGroup() {
        return oGroup;
    }

    public void setoGroup(Group oGroup) {
        this.oGroup = oGroup;
    }

    public List<Integer> getAddingUsers() {
        return AddingUsers;
    }

    public void setAddingUsers(List<Integer> addingUsers) {
        AddingUsers = addingUsers;
    }

    public List<Integer> getDeletingUsers() {
        return DeletingUsers;
    }

    public void setDeletingUsers(List<Integer> deletingUsers) {
        DeletingUsers = deletingUsers;
    }

    @Override
    public String getJson() {
        return new Gson().toJson(this);
    }
}