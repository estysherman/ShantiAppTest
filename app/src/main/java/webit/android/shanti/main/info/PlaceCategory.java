package webit.android.shanti.main.info;

import java.util.List;

/**
 * Created by 1 on 3/31/2015.
 */
public class PlaceCategory {

    private int iPlaceCategoryId;

    private String nvPlaceName;

    private String nvFontCode;

    private String nvFontName;

    private List<PlaceItem> lPlaceItems;

    public PlaceCategory(int iPlaceCategoryId, String nvPlaceName, String nvFontCode, String nvFontName, List<PlaceItem> lPlaceItems) {
        this.iPlaceCategoryId = iPlaceCategoryId;
        this.nvPlaceName = nvPlaceName;
        this.nvFontCode = nvFontCode;
        this.nvFontName = nvFontName;
        this.lPlaceItems = lPlaceItems;
    }

    public PlaceCategory() {
    }

    public int getiPlaceCategoryId() {
        return iPlaceCategoryId;
    }

    public void setiPlaceCategoryId(int iPlaceCategoryId) {
        this.iPlaceCategoryId = iPlaceCategoryId;
    }

    public String getNvPlaceName() {
        return nvPlaceName;
    }

    public void setNvPlaceName(String nvPlaceName) {
        this.nvPlaceName = nvPlaceName;
    }

    public String getNvFontCode() {
        return nvFontCode;
    }

    public void setNvFontCode(String nvFontCode) {
        this.nvFontCode = nvFontCode;
    }

    public String getNvFontName() {
        return nvFontName;
    }

    public void setNvFontName(String nvFontName) {
        this.nvFontName = nvFontName;
    }

    public List<PlaceItem> getlPlaceItems() {
        return lPlaceItems;
    }

    public void setlPlaceItems(List<PlaceItem> lPlaceItems) {
        this.lPlaceItems = lPlaceItems;
    }
}
