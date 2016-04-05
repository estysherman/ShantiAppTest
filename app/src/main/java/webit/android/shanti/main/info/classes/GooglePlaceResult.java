package webit.android.shanti.main.info.classes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 1 on 4/13/2015.
 */
public class GooglePlaceResult {
    @SerializedName("debug_info")
    private List<String> debug_info;

    @SerializedName("html_attributions")
    private List<String> html_attributions;

    @SerializedName("next_page_token")
    private String next_page_token;

    @SerializedName("results")
    private List<Place> results;

    public List<String> getDebugInfo() {
        return debug_info;
    }

    public List<String> getHtmlAttributions() {
        return html_attributions;
    }

    public String getNextPageToken() {
        return next_page_token;
    }

    public List<Place> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    @SerializedName("status")
    private String status;
}
