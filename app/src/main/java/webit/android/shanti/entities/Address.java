package webit.android.shanti.entities;

import com.google.gson.Gson;
import webit.android.shanti.general.BaseShantiObject;

/**
 * Created by AndroIT on 19/01/2015.
 */
public class Address implements BaseShantiObject {

    private int iAddressId;
    private String nvFullAddress;
    private String nvCity;
    private String nvStreet;

    public int getiAddressId() {
        return iAddressId;
    }

    public void setiAddressId(int iAddressId) {
        this.iAddressId = iAddressId;
    }

    public String getNvFullAddress() {
        return nvFullAddress;
    }

    public void setNvFullAddress(String nvFullAddress) {
        this.nvFullAddress = nvFullAddress;
    }

    public String getNvCity() {
        return nvCity;
    }

    public void setNvCity(String nvCity) {
        this.nvCity = nvCity;
    }

    public String getNvStreet() {
        return nvStreet;
    }

    public void setNvStreet(String nvStreet) {
        this.nvStreet = nvStreet;
    }

    @Override
    public String getJson() {
        return new Gson().toJson(this);
    }
}
