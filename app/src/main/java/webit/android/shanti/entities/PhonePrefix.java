package webit.android.shanti.entities;

/**
 * Created by crm on 28/10/2015.
 */
public class PhonePrefix {
    int nvKey;
    String nvValue;
    String nvValueParam;

    public  PhonePrefix(){

    }
    public PhonePrefix(int nvKey, String nvValue, String nvValueParam) {
        this.nvKey = nvKey;
        this.nvValue = nvValue;
        this.nvValueParam = nvValueParam;
    }

    public int getNvKey() {
        return nvKey;
    }

    public void setNvKey(int nvKey) {
        this.nvKey = nvKey;
    }

    public String getNvValue() {
        return nvValue;
    }

    public void setNvValue(String nvValue) {
        this.nvValue = nvValue;
    }

    public String getNvValueParam() {
        return nvValueParam;
    }

    public void setNvValueParam(String nvValueParam) {
        this.nvValueParam = nvValueParam;
    }
}
