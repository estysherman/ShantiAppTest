package webit.android.shanti.entities;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.general.BaseShantiObject;

/**
 * Created by CRM on 12/03/2015.
 */
public class KeyValue implements BaseShantiObject {
    private String nvKey;
    private String nvValue;
    private String nvValueParam;

    public KeyValue(String key, String value) {
        nvKey = key;
        nvValue = value;
    }

    public String getNvValueParam() {
        return nvValueParam;
    }

    public void setNvValueParam(String nvValueParam) {
        this.nvValueParam = nvValueParam;
    }

    public String getNvKey() {
        return nvKey;
    }

    public void setNvKey(String nvKey) {
        this.nvKey = nvKey;
    }

    public String getNvValue() {
        return nvValue;
    }

    public void setNvValue(String nvValue) {
        this.nvValue = nvValue;
    }

    @Override
    public String getJson() {
        return new Gson().toJson(this);
    }

    public static ArrayList<KeyValue> getKeyValues(ArrayList<String> keys) {
        if (keys == null)
            return null;
        ArrayList<KeyValue> KeyValues = new ArrayList<>();
        for (String string : keys) {
            KeyValues.add(new KeyValue("",""));// ?? ?????? ???????? ?? ?? ????? ? key value ????? ??????
        }
        return KeyValues;
    }
    public static ArrayList<String> getKeys(List<KeyValue> keyValues) {
        if (keyValues == null)
            return null;
        ArrayList<String> keys = new ArrayList<>();
        for (KeyValue keyValue : keyValues) {
            keys.add(keyValue.getNvKey());
        }
        return keys;
    }
}