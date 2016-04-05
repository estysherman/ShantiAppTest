package webit.android.shanti.main.info;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.CustomTextView;
import webit.android.shanti.general.Dimension;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;


public class PlacesFragment extends InfoMainFragment {

    public static PlacesFragment instance = null;
    List<webit.android.shanti.main.info.PlaceCategory> categories = new ArrayList<>();
    View currentCategory;


    public static PlacesFragment getInstance() {
        if (instance == null) {
            instance = new PlacesFragment();
        }
        return instance;
    }

    private View rootView = null;
    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        ((MainActivity) getActivity()).showActionBar();
//        if (mRootView == null) {

//        if (mRootView == null) {
        rootView = inflater.inflate(R.layout.fragment_main_info, null);
        gridView = (GridView) rootView.findViewById(R.id.infoMainTable);
        addMaimButton();
        super.changeActionBar(rootView, getString(R.string.infoActionBarInfo));
        loadDate();
//        }else


        return rootView;
//        return inflater.inflate(R.layout.fragment_main_info, null);nvLanguage
    }

    private void loadDate() {
        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                Type type = new TypeToken<List<webit.android.shanti.main.info.PlaceCategory>>() {
                }.getType();
                categories = new Gson().fromJson(result, type);
                addMaimButton();
            }
        }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"nvLanguage\":\"" + Utils.getDefaultLocale() + "\"}", ConnectionUtil.GetGooglePlaces);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void addMaimButton() {
        gridView.setAdapter(new PlaceCategoryAdapter());
    }


    private class PlaceCategoryAdapter extends BaseAdapter {

        class ViewHolder {
            CustomTextView text;
            CustomTextView icon;
        }

        @Override
        public int getCount() {
            if (categories != null)
                return categories.size();
            else return 0;
        }

        @Override
        public Object getItem(int position) {
            return categories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.info_subject_button, null);
                holder = new ViewHolder();
                holder.text = (CustomTextView) convertView.findViewById(R.id.inoSubjectBtnText);
                holder.icon = (CustomTextView) convertView.findViewById(R.id.inoSubjectBtnIcon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.icon.setText(categories.get(position).getNvFontCode());
            holder.icon.setFontName(categories.get(position).getNvFontName());
            holder.text.setText(categories.get(position).getNvPlaceName());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.info_subject_button, null);
                    webit.android.shanti.main.info.PlaceCategory category = categories.get(position);
                    ((CustomTextView) view.findViewById(R.id.inoSubjectBtnText)).setText(category.getNvPlaceName());
                    ((CustomTextView) view.findViewById(R.id.inoSubjectBtnIcon)).setFontName(category.getNvFontName());
                    ((CustomTextView) view.findViewById(R.id.inoSubjectBtnIcon)).setText(category.getNvFontCode());
                    ((LinearLayout) rootView.findViewById(R.id.infoCurrentCategory)).removeAllViews();
                    ((LinearLayout) rootView.findViewById(R.id.infoCurrentCategory)).addView(view);
                    gridView.setVisibility(View.GONE);
                    addItems(category.getlPlaceItems());
                }
            });
            return convertView;
        }

        private void addItems(List<webit.android.shanti.main.info.PlaceItem> items) {

            LinearLayout itemsLayout = (LinearLayout) rootView.findViewById(R.id.infoItemsTable);
            itemsLayout.setVisibility(View.VISIBLE);

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int windowWidth = size.x;

            float margin = Dimension.convertPixelsToDp(25, getActivity());
            float charSize = Dimension.convertPixelsToDp(60, getActivity());
            int itemsInLine = 0;
            int chars = 0;
            LinearLayout row = new LinearLayout(getActivity());
            row.setGravity(Gravity.CENTER);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(30, 30, 30, 30);
            row.setLayoutParams(layoutParams);
            float width = 0;

            for (final webit.android.shanti.main.info.PlaceItem item : items) {
                chars = item.getNvPlaceName().length();
                width += (chars * charSize) + (margin * 2);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.info_item_button, null);
                CustomTextView textView = (CustomTextView) view.findViewById(R.id.inoItemBtnText);
                textView.setText(item.getNvPlaceName());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlacesListFragment fragment = PlacesListFragment.getInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString(PlacesListFragment.TYPE, item.getNvGoogleType());
                        bundle.putString(PlacesListFragment.NAME, item.getNvPlaceName());
                        fragment.setArguments(bundle);
                        initFragment(fragment);
                    }
                });
                row.addView(view);
                itemsInLine++;
                if (width >= windowWidth) {
                    row = new LinearLayout(getActivity());
                    row.setGravity(Gravity.CENTER);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    itemsLayout.addView(row);
                    itemsInLine = 0;
                    width = 0;
                }
            }
//            itemsLayout.addView(row);
        }
    }
}
