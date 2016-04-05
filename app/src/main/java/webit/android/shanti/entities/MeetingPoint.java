package webit.android.shanti.entities;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import webit.android.shanti.general.BaseShantiObject;
import webit.android.shanti.main.map.MapFragment;

/**
 * Created by 1 on 04/05/15.
 */
public class MeetingPoint extends BaseMarker implements BaseShantiObject {

    private int iMeetingPointId;

    private int iGroupId;

    private String dtMeetingTime;

    private String nvTitle;

    private String nvComment;

    private String nvGroupName;


    public MeetingPoint() {
        this.iMeetingPointId = -1;
    }

    public MeetingPoint(int iMeetingPointId, int iGroupId, String dtMeetingTime, String nvTitle, String nvComment, LatLng latLng) {
        this.iMeetingPointId = iMeetingPointId;
        this.iGroupId = iGroupId;
        this.dtMeetingTime = dtMeetingTime;
        this.nvTitle = nvTitle;
        this.nvComment = nvComment;
        setoLocation(new Location(latLng.latitude, latLng.longitude, -1,5));
    }

    public MeetingPoint(int iMeetingPointId, int iGroupId, String dtMeetingTime, String nvTitle, String nvComment, LatLng latLng, double distance) {
        this.iMeetingPointId = iMeetingPointId;
        this.iGroupId = iGroupId;
        this.dtMeetingTime = dtMeetingTime;
        this.nvTitle = nvTitle;
        this.nvComment = nvComment;
        setoLocation(new Location(latLng.latitude, latLng.longitude, -1,distance));
    }

    public int getiMeetingPointId() {
        return iMeetingPointId;
    }

    public void setiMeetingPointId(int iMeetingPointId) {
        this.iMeetingPointId = iMeetingPointId;
    }

    public int getiGroupId() {
        return iGroupId;
    }

    public void setiGroupId(int iGroupId) {
        this.iGroupId = iGroupId;
    }

    public String getDtMeetingTime() {
        return dtMeetingTime;
    }

    public String GetMeetingShortTime() {
        return getDtMeetingTime().length() < 5 ? "" : getDtMeetingTime().substring(getDtMeetingTime().length() - 8, getDtMeetingTime().length() - 3);
    }

    public void setDtMeetingTime(String dtMeetingTime) {
        this.dtMeetingTime = dtMeetingTime;
    }

    public String getNvTitle() {
        return nvTitle;
    }

    public void setNvTitle(String nvTitle) {
        this.nvTitle = nvTitle;
    }

    public String getNvGroupName() {
        return nvGroupName;
    }

    public void setNvGroupName(String nvGroupName) {
        this.nvGroupName = nvGroupName;
    }

    @Override
    public String getJson() {
        return new Gson().toJson(new MeetingPointToSend(this));
    }


    @Override
    public String getMarkerId() {
        return MapFragment.MarkerType.UNIQUE_MEETING_POINT.toString() + getiMeetingPointId();
    }

    public String getNvComment() {
        return this.nvComment;
    }

    public void setNvComment(String nvComment) {
        this.nvComment = nvComment;
    }

    private class MeetingPointToSend {
        MeetingPoint oMeetingPoint;

        public MeetingPointToSend(MeetingPoint newMeetingPoint) {
            this.oMeetingPoint = newMeetingPoint;
        }
    }


}
