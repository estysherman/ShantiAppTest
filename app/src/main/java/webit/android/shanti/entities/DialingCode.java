package webit.android.shanti.entities;

/**
 * Created by crm on 13/10/2015.
 */
public class DialingCode  {
    String countryName;
    String dialingCode;

    public DialingCode(){}
    public DialingCode(String countryName, String dialingCode) {
        this.countryName = countryName;
        this.dialingCode = dialingCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getDialingCode() {
        return dialingCode;
    }

    public void setDialingCode(String dialingCode) {
        this.dialingCode = dialingCode;
    }
}
