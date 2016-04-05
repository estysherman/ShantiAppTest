package webit.android.shanti.main.info;

/**
 * Created by 1 on 3/31/2015.
 */
public class PlaceItem {

    private int iPlaceItemId;

    private String nvPlaceName;

    private String nvGoogleType;

    public PlaceItem(int iPlaceItemId, String nvPlaceName, String nvGoogleType) {
        this.iPlaceItemId = iPlaceItemId;
        this.nvPlaceName = nvPlaceName;
        this.nvGoogleType = nvGoogleType;
    }

    public PlaceItem() {

    }

    public int getiPlaceItemId() {
        return iPlaceItemId;
    }

    public void setiPlaceItemId(int iPlaceItemId) {
        this.iPlaceItemId = iPlaceItemId;
    }

    public String getNvPlaceName() {
        return nvPlaceName;
    }

    public void setNvPlaceName(String nvPlaceName) {
        this.nvPlaceName = nvPlaceName;
    }

    public String getNvGoogleType() {
        return nvGoogleType;
    }

    public void setNvGoogleType(String nvGoogleType) {
        this.nvGoogleType = nvGoogleType;
    }
}
