package webit.android.shanti.entities;

import com.google.gson.Gson;

import webit.android.shanti.general.BaseShantiObject;

import java.util.ArrayList;

/**
 * Created by AndroIT on 03/02/2015.
 */
public class UserSearchDef implements BaseShantiObject {

    private int iUserSearchDefId = -1;
    private int iUserId = -1;
    //  private int iAgeRangeId = -1;
    private ArrayList<Integer> AgeRanges;
    private int iGenderId = -1;
    // private int iReligionId = -1;
    private ArrayList<Integer> Religions;
    private int iReligionLevelId = -1;
    private String nvComment;
    private ArrayList<Integer> Countries;
    private ArrayList<Integer> Languages;
    private int iRadiusId = -1;



    public int getiUserSearchDefId() {
        return iUserSearchDefId;
    }

    public void setiUserSearchDefId(int iUserSearchDefId) {
        this.iUserSearchDefId = iUserSearchDefId;
    }

    public int getiUserId() {
        return iUserId;
    }

    public void setiUserId(int iUserId) {
        this.iUserId = iUserId;
    }

   /* public int getiAgeRangeId() {
        return iAgeRangeId;
    }

    public void setiAgeRangeId(int iAgeRangeId) {
        this.iAgeRangeId = iAgeRangeId;
    }*/

    public int getiGenderId() {
        return iGenderId;
    }

    public void setiGenderId(int iGenderId) {
        this.iGenderId = iGenderId;
    }

   /* public int getiReligionId() {
        return iReligionId;
    }

    public void setiReligionId(int iReligionId) {
        this.iReligionId = iReligionId;
    }*/

    public int getiReligionLevelId() {
        return iReligionLevelId;
    }

    public void setiReligionLevelId(int iReligionLevelId) {
        this.iReligionLevelId = iReligionLevelId;
    }

    public String getNvComment() {
        return nvComment;
    }

    public void setNvComment(String nvComment) {
        this.nvComment = nvComment;
    }

    public ArrayList<Integer> getCountries() {
        return Countries;
    }

    public void setCountries(ArrayList<Integer> countries) {
        Countries = countries;
    }

    public ArrayList<Integer> getAgeRangeId() {
        return AgeRanges;
    }

    public void setAgeRangeId(ArrayList<Integer> ageRangeId) {
        AgeRanges = ageRangeId;
    }

    public ArrayList<Integer> getReligionId() {
        return Religions;
    }

    public void setReligionId(ArrayList<Integer> religionId) {
        Religions = religionId;
    }






    public ArrayList<Integer> getLanguages() {
        return Languages;
    }

    public void setLanguages(ArrayList<Integer> languages) {
        Languages = languages;
    }

    public int getiRadiusId() {
        return iRadiusId;
    }

    public void setiRadiusId(int iRadiusId) {
        this.iRadiusId = iRadiusId;
    }

    @Override
    public String toString() {
        return new Gson().toJson(new UserSearchDefToSend(this));
    }

    @Override
    public String getJson() {
        return new Gson().toJson(this);
    }

    private class UserSearchDefToSend {
        private UserSearchDef newUserDef;

        public UserSearchDefToSend(UserSearchDef newUserDef) {
            this.newUserDef = newUserDef;
        }
    }
}
