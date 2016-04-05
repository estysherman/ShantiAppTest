package webit.android.shanti.main.messages;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import webit.android.shanti.R;

public class FullScreenChatImageFragment extends ChatFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private static String mUserImage;
    private static String mUserName;
    private static String previousFragment;
    private static View mActionBarGroup;
    private ImageView imageView;
    private TextView UserNameTv;



    public FullScreenChatImageFragment() {
        // Required empty public constructor
    }

    public static FullScreenChatImageFragment instance = null;

    public static FullScreenChatImageFragment getInstance() {
        if (instance == null)
            instance = new FullScreenChatImageFragment();
        return instance;

    }

    public static FullScreenChatImageFragment getInstance(Context context, String userImage, String userName, String fragmentName, View actionBar) {
        mUserImage = userImage;
        mUserName = userName;
        previousFragment = fragmentName;
        mActionBarGroup = actionBar;
        //if (userImage == null)
        FullScreenChatImageFragment fragment = FullScreenChatImageFragment.getInstance();
        return fragment;
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
        // Inflate the layout for this fragment
        View mRootView =  inflater.inflate(R.layout.fragment_full_screen_chat_image, container, false);
        imageView = (ImageView) mRootView.findViewById(R.id.full_screen_image);
        UserNameTv = (TextView) mRootView.findViewById(R.id.chatTitleName);
        mRootView.findViewById(R.id.chatBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        Picasso.with(getActivity()).load(mUserImage).into(imageView);
        UserNameTv.setText(mUserName + "");
        return mRootView;
    }


}
