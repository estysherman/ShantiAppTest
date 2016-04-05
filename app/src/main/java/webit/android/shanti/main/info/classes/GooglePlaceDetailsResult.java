package webit.android.shanti.main.info.classes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 1 on 4/13/2015.
 */
public class GooglePlaceDetailsResult {

    @SerializedName("debug_info")
    private List<String> debug_info;

    @SerializedName("html_attributions")
    private List<String> html_attributions;

    @SerializedName("next_page_token")
    private String next_page_token;

    @SerializedName("result")
    private PlaceDetails result;

    @SerializedName("status")
    private String status;

    public List<String> getDebug_info() {
        return debug_info;
    }

    public void setDebug_info(List<String> debug_info) {
        this.debug_info = debug_info;
    }

    public List<String> getHtml_attributions() {
        return html_attributions;
    }

    public void setHtml_attributions(List<String> html_attributions) {
        this.html_attributions = html_attributions;
    }

    public String getNext_page_token() {
        return next_page_token;
    }

    public void setNext_page_token(String next_page_token) {
        this.next_page_token = next_page_token;
    }

    public PlaceDetails getResult() {
        return result;
    }

    public void setResults(PlaceDetails result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
