package webit.android.shanti.entities;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import webit.android.shanti.Utils.Utils;
import webit.android.shanti.general.BaseShantiObject;

/**
 * Created by AndroIT on 19/01/2015.
 */

public class CodeValue implements BaseShantiObject,Serializable {

    //שמות הטבלאות הקיימות - והמספר שלהן
    public static String countries = "16";
    // this code value only client
    public static String countries_name = "1";
    public static String language = "11";
    public static String religions = "12";
    public static String ageRange = "13";
    public static String religionLevel = "14";
    public static String radius = "17";
    public static String groupType = "18";
    public static String DeviceType = "19";
    public static String gender = "20";


    private int iKeyId;
    private String nvValue;
    private String nvValueParam;
    private int iTableId;

    public CodeValue(int iKeyId,String nvValue) {
        this.nvValue = nvValue;
        this.iKeyId = iKeyId;
    }

    public CodeValue() {
        this.nvValue = "";
        this.iKeyId = -1;
    }

    public static String getJsonToSent(String tableId) {
        return "{\"TableId\":" + tableId + ",\"nvLanguage\":\"" + Utils.getDefaultLocale() + "\"}";
    }

    public int getiTableId() {
        return iTableId;
    }

    public void setiTableId(int iTableId) {
        this.iTableId = iTableId;
    }


    public String getNvValue() {
        return nvValue;
    }

    public void setNvValue(String nvValue) {
        this.nvValue = nvValue;
    }

    public int getiKeyId() {
        return iKeyId;
    }

    public String getNvValueParam() {
        return nvValueParam;
    }

    public void setNvValueParam(String nvValueParam) {
        this.nvValueParam = nvValueParam;
    }

    public void setiKeyId(int iKeyId) {
        this.iKeyId = iKeyId;
    }

    @Override
    public String toString() {
        return nvValue;
    }

    @Override
    public String getJson() {
        return new Gson().toJson(this);
    }

    public static ArrayList<Integer> getKeys(List<CodeValue> codeValues) {
        if (codeValues == null)
            return null;
        ArrayList<Integer> keys = new ArrayList<>();
        for (CodeValue codeValue : codeValues) {
            keys.add(codeValue.getiKeyId());
        }
        return keys;
    }

    public static ArrayList<CodeValue> getCodeValues(ArrayList<Integer> keys) {
        if (keys == null)
            return null;
        ArrayList<CodeValue> codeValues = new ArrayList<>();
        for (Integer integer : keys) {
            codeValues.add(new CodeValue(integer,""));
        }
        return codeValues;
    }

}