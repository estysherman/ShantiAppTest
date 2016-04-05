package webit.android.shanti.main.info;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import webit.android.shanti.R;
import webit.android.shanti.general.photo.ImageHelper;
import webit.android.shanti.main.MainBaseFragment;
import webit.android.shanti.main.info.classes.Place;


public class PlaceDetailsFragment extends InfoMainFragment {

    public static String CURRENT_PLACE = "currentPlace";

    static PlaceDetailsFragment instance = null;

    public static PlaceDetailsFragment getInstance() {
        if (instance == null) {
            instance = new PlaceDetailsFragment();
        }
        return instance;
    }

    View currentPlace;
    Place place;
    private View rootView = null;
    private FragmentTabHost tabHost;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        rootView = inflater.inflate(R.layout.info_place_details, null);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            place = new Gson().fromJson(bundle.getString(PlaceDetailsFragment.CURRENT_PLACE), Place.class);
            initViews();
        }
        return rootView;
    }


    private void initViews() {
        ImageView placeImage = (ImageView) rootView.findViewById(R.id.placeDetailsImage);
        placeImage.setImageBitmap(ImageHelper.getBitmapFromURL(place.getPhotos().get(0).getUrl(),getActivity()));
        ((TextView) rootView.findViewById(R.id.placeDetailsName)).setText(place.getName());
        ((TextView) rootView.findViewById(R.id.placeDetailsInfo)).setText("בשרים, גריל, איטלקי");
        int pictureNumberCount = place.getPhotos() == null ? 0 : place.getPhotos().size();
        ((TextView) rootView.findViewById(R.id.placeDetailsPictureNumberCount)).setText("0/" + pictureNumberCount);
    }
}