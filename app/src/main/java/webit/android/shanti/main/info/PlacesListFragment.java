package webit.android.shanti.main.info;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.CustomTextView;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.main.info.classes.GooglePlaceDetailsResult;
import webit.android.shanti.main.info.classes.GooglePlaceResult;
import webit.android.shanti.main.info.classes.Place;

public class PlacesListFragment extends InfoMainFragment {

    public static String TYPE = "type";
    public static String NAME = "name";
    private final String placesAPI = "https://maps.googleapis.com/maps/api/place/search/json";
    private final String placeDetailsAPI = "https://maps.googleapis.com/maps/api/place/details/json";
    private final String placePhotoAPI = "https://maps.googleapis.com/maps/api/place/photo";
    private final String APIkey = "AIzaSyCxDblSXmkmqnyJPFTxEDJ3-qlV__P-plw";
    static PlacesListFragment instance = null;
    private int estysherman;

    public static PlacesListFragment getInstance() {
        if (instance == null) {
            instance = new PlacesListFragment();
        }
        return instance;
    }

    List<Place> places = new ArrayList<>();
    private View rootView = null;
    ListView placesListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_places_list, null);
        //((MainActivity) getActivity()).setLeftButtonMapActionBar(R.string.actionBarMap);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            loadData(bundle.getString(PlacesListFragment.TYPE));
            placesListView = (ListView) rootView.findViewById(R.id.placesList);
            changeActionBar(rootView, bundle.getString(PlacesListFragment.NAME));
        }
        return rootView;
    }

    private void loadData(String type) {
        String uri = Uri.parse(placesAPI)
                .buildUpon()
                .appendQueryParameter("type", type)
                .appendQueryParameter("location", "37.787930,-122.4074990")
                .appendQueryParameter("radius", "5000")
                .appendQueryParameter("key", APIkey)
                .build().toString();

        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                GooglePlaceResult resultData = gson.fromJson(result, GooglePlaceResult.class);
                places = resultData.getResults();
                PlacesAdapter placesAdapter = new PlacesAdapter();
                placesListView.setAdapter(placesAdapter);
            }
        }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.GET.toString(), uri);


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class PlacesAdapter extends BaseAdapter {

        class ViewHolder {
            ImageView placeImage;
            CustomTextView placeName;
            CustomTextView placeInfo;
            CustomTextView placeAddress;
            CustomTextView placePhone;
            CustomTextView placeReviewCount;
            CustomTextView placeRating;
        }

        @Override
        public int getCount() {
            return places.size();
        }

        @Override
        public Object getItem(int position) {
            return places.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.info_place_item, null);
                holder = new ViewHolder();
                holder.placeImage = (ImageView) convertView.findViewById(R.id.placeItemImage);
                holder.placeName = (CustomTextView) convertView.findViewById(R.id.placeItemName);
                holder.placeInfo = (CustomTextView) convertView.findViewById(R.id.placeItemInfo);
                holder.placeAddress = (CustomTextView) convertView.findViewById(R.id.placeItemAddress);
                holder.placePhone = (CustomTextView) convertView.findViewById(R.id.placeItemPhone);
                holder.placeReviewCount = (CustomTextView) convertView.findViewById(R.id.placeItemReviewCount);
                holder.placeRating = (CustomTextView) convertView.findViewById(R.id.placeItemRating);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Place place = places.get(position);
            holder.placeName.setText(place.getName());
            String info = "";
            for (String s : place.getTypes())
                info += s + ", ";
            info = info.substring(0, info.length() - 2);
            holder.placeInfo.setText(info);
            String rating = "";
            int i;
            for (i = 0; i < place.getRating() - 1; i++)
                rating += getString(R.string.infoHoleStarIcon);
            if (place.getRating() > i - 1)
                rating = getString(R.string.infoHalfStarIcon) + rating
                        ;
            holder.placeRating.setText(rating);

            String uriDetails = Uri.parse(placeDetailsAPI)
                    .buildUpon()
                    .appendQueryParameter("reference", place.getReference())
                    .appendQueryParameter("key", APIkey)
                    .build().toString();

            new GeneralTask(getActivity(), new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    GooglePlaceDetailsResult googlePlaceDetailsResult = gson.fromJson(result, GooglePlaceDetailsResult.class);
                    place.setPlaceDetails(googlePlaceDetailsResult.getResult());
                    holder.placeAddress.setText(place.getPlaceDetails().getFormatted_address());
                    holder.placePhone.setText(place.getPlaceDetails().getInternational_phone_number());
                    String response = "";
                    if (place.getPlaceDetails().getReviews() != null)
                        response = place.getPlaceDetails().getReviews().size() + " " + getString(R.string.infoReview);
                    else
                        response = "0 " + getString(R.string.infoReview);
                    holder.placeReviewCount.setText(response);
                }
            }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.GET.toString(), uriDetails);

            if (place.getReference() != null && place.getPhotos() != null && place.getPhotos().size() > 0) {
                String uriPhoto = Uri.parse(placePhotoAPI)
                        .buildUpon()
                        .appendQueryParameter("reference", place.getReference())
                        .appendQueryParameter("key", APIkey)
                        .appendQueryParameter("photoreference", place.getPhotos().get(0).getPhoto_reference())
                        .appendQueryParameter("maxheight", "422")
                        .build().toString();

                place.getPhotos().get(0).setUrl(uriPhoto);
                //holder.placeImage.setImageBitmap(ImageHelper.getBitmapFromURL(uriPhoto));
                Picasso.with(getActivity()).load(uriPhoto).into(holder.placeImage);
//                new Utils.DownloadImageTask(holder.placeImage).execute(uriPhoto);

            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(PlaceDetailsFragment.CURRENT_PLACE, new Gson().toJson(place));
                    PlaceDetailsFragment fragment = PlaceDetailsFragment.getInstance();
                    fragment.setArguments(bundle);
                    initFragment(fragment);
                }
            });
            return convertView;
        }
    }
}
