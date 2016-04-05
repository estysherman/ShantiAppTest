package webit.android.shanti.main.info.classes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 1 on 4/13/2015.
 */
public class AddressComponents {

    @SerializedName("types")
    private List<String> types;

    @SerializedName("long_name")
    private String long_name;

    @SerializedName("short_name")
    private String short_name;

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getLong_name() {
        return long_name;
    }

    public void setLong_name(String long_name) {
        this.long_name = long_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }
}
