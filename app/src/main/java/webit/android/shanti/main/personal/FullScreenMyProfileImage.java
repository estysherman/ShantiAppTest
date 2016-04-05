package webit.android.shanti.main.personal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import webit.android.shanti.R;
import webit.android.shanti.main.MainBaseFragment;


public class FullScreenMyProfileImage extends MainBaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private static String mUserImage;
    private static String mUserName;
    private ImageView imageView;



    public FullScreenMyProfileImage() {
        // Required empty public constructor
    }


    public static FullScreenMyProfileImage newInstance(String param1, String param2) {
        FullScreenMyProfileImage fragment = new FullScreenMyProfileImage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static FullScreenMyProfileImage instance = null;

    public static FullScreenMyProfileImage getInstance() {
        if (instance == null)
            instance = new FullScreenMyProfileImage();
        return instance;

    }

    public static FullScreenMyProfileImage getInstance(Context context, String userImage, String userName) {
        mUserImage = userImage;
        mUserName = userName;
        FullScreenMyProfileImage fragment = FullScreenMyProfileImage.getInstance();
        return fragment;
    }


    @Override
    public void changeActionBar(View view, String title) {
        super.changeActionBar(view, title);
        setBackView(view.findViewById(R.id.profileGroupActionBack));
        ((TextView) view.findViewById(R.id.profileGroupActionTitle)).setText(mUserName + "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView =  inflater.inflate(R.layout.fragment_full_screen_profile_image, container, false);
        changeActionBar(mRootView, "");
        imageView = (ImageView) mRootView.findViewById(R.id.full_screen_profil_image);
        Picasso.with(getActivity()).load(mUserImage).into(imageView);
        return mRootView;
    }
}
