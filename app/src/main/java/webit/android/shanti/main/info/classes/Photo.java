package webit.android.shanti.main.info.classes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 1 on 4/12/2015.
 */
public class Photo {
    @SerializedName("height")
    private int height;

    @SerializedName("width")
    private int width;

    @SerializedName("photo_reference")
    private String photo_reference;

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }

    public Photo(int height, int width, String photo_reference) {
        this.height = height;
        this.width = width;
        this.photo_reference = photo_reference;
    }

    public Photo() {
    }

}
