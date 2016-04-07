package webit.android.shanti.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.chat.chatManager.ApplicationSingleton;
import webit.android.shanti.chat.chatManager.core.ChatMainManager;
import webit.android.shanti.entities.Group;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.SPManager;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.general.gcm.Consts;
import webit.android.shanti.general.photo.ImageHelper;
import webit.android.shanti.login.signin.BaseInfoFragment;
import webit.android.shanti.login.signin.MainLoginFragment;
import webit.android.shanti.login.signin.MoreInfoFragment;
import webit.android.shanti.login.signin.ProfileInfoFragment;
import webit.android.shanti.login.signin.SearchPreferencesFragment;
import webit.android.shanti.login.signin.SignUpActivity;
import webit.android.shanti.login.signin.TakePhotoFragment;
import webit.android.shanti.main.SearchUsers.SearchUsersFragment;
import webit.android.shanti.main.groups.ApprovalGroupFragment;
import webit.android.shanti.main.groups.EditGroupFragment;
import webit.android.shanti.main.groups.GroupChatFragment;
import webit.android.shanti.main.groups.GroupMainFragment;
import webit.android.shanti.main.groups.GroupProfileFragment;
import webit.android.shanti.main.groups.GroupsListFragment;
import webit.android.shanti.main.groups.NewGroupFragment;
import webit.android.shanti.main.info.InfoMainFragment;
import webit.android.shanti.main.info.PlaceDetailsFragment;
import webit.android.shanti.main.info.PlacesFragment;
import webit.android.shanti.main.map.ForDeleteFragment;
import webit.android.shanti.main.map.MapFooterManager;
import webit.android.shanti.main.map.MapFragment;
import webit.android.shanti.main.map.MemberProfileFragment;
import webit.android.shanti.main.map.SendCurrentLocationTimer;
import webit.android.shanti.main.messages.ChatFragment;
import webit.android.shanti.main.messages.FullScreenChatImageFragment;
import webit.android.shanti.main.messages.MessagesFragment;
import webit.android.shanti.main.personal.FullScreenMyProfileImage;
import webit.android.shanti.main.personal.MyProfileFragment;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnTouchListener {
 private int checkpull;

    private String lastFragmentInStack = "";
    private static Fragment mfrargment;
    DrawerLayout drawer;
    LinearLayout navList;
    Activity context;
    ImageView mProfileImg;
    GoogleApiClient mGoogleApiClient;
    private Bitmap image;

    public static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    private String TAG = "MainActivity";
    private Tracker mTracker;
    private Fragment saveFragment;

    public static   boolean flag_layout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        //new RetrieveFeedTask().execute(urlToRssFeed);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//סגירת מקלדת
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.mainContainer);
        frameLayout.setOnTouchListener(this);
        ApplicationSingleton application = (ApplicationSingleton) getApplication();//שליחת נתונים לסטטיסטיקה
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        instance = this;
        initNavigationDrawer();
        //initFragment(MapFragment.getInstance(), true);


        SendCurrentLocationTimer.getInstance(MainActivity.this).startSendLocation();
        if (savedInstanceState != null) {
            switch ((String) savedInstanceState.get("fragment")) {//יש לשים לב שלא נמחק אותיות מהstring שבcase
                case "SearchUsersFragment":
                    initFragment(new SearchUsersFragment());
                    break;
                case "MapFragment"://לשנות נראות לשכיבה
                    initFragment(new MapFragment());
                    break;
                case "ChatFragment":
                    initFragment(new ChatFragment());
                    break;
                case "GroupsListFragment":
                    initFragment(new GroupsListFragment());
                    break;
                case "GroupChatFragment":
                    initFragment(new GroupChatFragment());
                    break;
                case "GroupProfileFragment":
                    initFragment(new GroupProfileFragment());
                    break;
                case "EditGroupFragment":
                    initFragment(new EditGroupFragment());
                    break;
                case "NewGroupFragment":
                    initFragment(new NewGroupFragment());
                    break;
                case "MessagesFragment":
                    initFragment(new MessagesFragment());
                    break;
                case "ApprovalGroupFragment":
                    initFragment(new ApprovalGroupFragment());
                    break;
                case "PlacesFragment":
                    initFragment(new PlacesFragment());
                    break;
                case "MyProfileFragment":
                    initFragment(new MyProfileFragment());
                    break;
                case "SearchPreferencesFragment":
                    initFragment(new SearchPreferencesFragment());
                    break;
                case "MemberProfileFragment":
                    initFragment(new MemberProfileFragment());
                    break;
                case "FullScreenChatImageFragment":
                    initFragment(new FullScreenChatImageFragment());
                    break;
                case "FullScreenMyProfileImage":
                    initFragment(new FullScreenMyProfileImage());
                    break;
                default:
                    break;
            }
        } else
        initFragment(SearchUsersFragment.getInstance());

        context = this;
        CareNotification(getIntent());
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        outState.putString("fragment", saveFragment.getClass().getSimpleName().toString());
    }

    public String getCurrentFragment() {//מחזיר שם פרגמנט נוכחי
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            Fragment currentFragment = getSupportFragmentManager()
                    .findFragmentByTag(fragmentTag);
            return fragmentTag;
        } else return "";

    }


    public String getCurrentFragmentV2() {
        return lastFragmentInStack;
    }

    //פתיחת וסגירת התפריט
    public void drawerClick() {
        if (drawer.isDrawerOpen(navList)) {//אם פתוח
            closeNavigation();//סגור
        } else {//אם סגור
            openNavigation();//פתח
        }
    }

    private void CareNotification(Intent intent) {
        if (intent.getExtras() != null) {
            int notificationType;
            try {
                notificationType = Integer.parseInt(intent.getExtras().getString(Consts.NOTIFICATION_TYPE));
            } catch (NumberFormatException e) {
                notificationType = -1;
            }
            switch (notificationType) {////////////////למה שתי פרגמנט///////////////////////
                case Consts.NOTIFICATION_TYPE_NEW_MESSAGE_PRIVATE://התכתבות עם משתמש בודד
                    initFragment(MapFragment.getInstance());
                    //initFragment(MessagesFragment.getInstance());
                    initFragment(ChatFragment.getInstance(this, intent.getStringExtra(Consts.USER_AS_JSON)));
                    break;

                case Consts.NOTIFICATION_TYPE_NEW_MESSAGE_GROUP://התכתבות עם קבוצה
                    initFragment(MapFragment.getInstance());
                    if (intent.getStringExtra(Consts.GROUP_AS_JSON) == null) {
                        initFragment(GroupChatFragment.getInstance(MainActivity.this, ""));


                    } else
                        initFragment(GroupChatFragment.getInstance(MainActivity.this, intent.getStringExtra(Consts.GROUP_AS_JSON)));
                    break;
                case Consts.NOTIFICATION_TYPE_GLOBAL_MESSAGE://הודעה לכל המשתמשים המוצגים במפה
                    initFragment(MapFragment.getInstance());
                    MainActivity.getInstance().messageReceivedDialog(new Gson().fromJson(intent.getStringExtra(Consts.USER_AS_JSON), User.class), intent.getStringExtra(Consts.MESSAGE));
                    //initFragment(ChatFragment.getInstance(intent.getStringExtra(Consts.USER_AS_JSON)));
                    break;
                case Consts.NOTIFICATION_TYPE_APPROVAL_GROUP://שמבקשים בקשת הצטרפות
                    initFragment(MapFragment.getInstance());
                    initFragment(ApprovalGroupFragment.getInstance());
                    break;
            }
        }
    }

    public void initNavigationDrawerBackgroundImage() throws IOException {
        //        pathName = "/drawable/img1"
//        winner.setBackground(Drawable.createFromPath(pathName));
//
//        Bitmap bmImg = BitmapFactory.decodeStream();
//        BitmapDrawable background = new BitmapDrawable(bmImg);
//        navList.setBackgroundDrawable(background);
//        URL url = new URL(Common.user.getNvImage());
//        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//        BitmapDrawable background = new BitmapDrawable(image);
//        navList.setBackgroundDrawable(background);
//        navList.setBackground(Drawable.createFromPath(Common.user.getNvImage()));

        Bitmap image = ImageHelper.getBitmapFromURL(Common.user.getNvImage(),this);
        BitmapDrawable background = new BitmapDrawable(image);
        navList.setBackgroundDrawable(background);
    }
    private void initNavigationDrawer() {
        if (Common.user == null)
            return;
//        mProfileImg = (ImageView) findViewById(R.id.sideMenuUserImg);
//        Picasso.with(this).load(Common.user.getNvImage()).into(mProfileImg);
//        new Utils.DownloadImageTask(mProfileImg).execute(Common.user.getNvImage());//לוקח תמונה של המשתמש

        ((TextView) findViewById(R.id.sideMenuUserName)).setText(Common.user.getFullName());
        ((TextView) findViewById(R.id.sideMenuUserInfo)).setText(Common.user.getUserInfo(this));
        drawer = (DrawerLayout)
                findViewById(R.id.drawer_layout);
        navList = (LinearLayout) findViewById(R.id.mainDrawer);

        try {
            initNavigationDrawerBackgroundImage();
        } catch (IOException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }


        addSideMenuListener();
        addSideMenuPersonalAreaListener();
        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                     @Override
                                     public void onDrawerSlide(View drawerView, float slideOffset) {
                                         super.onDrawerSlide(drawerView, slideOffset);
                                         drawer.bringChildToFront(navList);
                                         drawer.requestLayout();
                                     }

                                     @Override
                                     public void onDrawerOpened(View drawerView) {
                                         if (KeyboardIsShowing())
                                             Utils.HideKeyboard(MainActivity.this);
                                         super.onDrawerOpened(drawerView);
                                     }

                                     @Override
                                     public void onDrawerStateChanged(int newState) {
                                         super.onDrawerStateChanged(newState);
                                     }

                                     @Override
                                     public void onDrawerClosed(View drawerView) {
                                         super.onDrawerClosed(drawerView);
                                     }
                                 }
        );

    }

    private void addSideMenuListener() {


        final LinearLayout mainSideMenu = (LinearLayout) findViewById(R.id.sideMenuMainArea);
        final LinearLayout messagesSideMenu = (LinearLayout) findViewById(R.id.sideMenuMessages);
        final LinearLayout personalSideMenu = (LinearLayout) findViewById(R.id.sideMenuPersonalArea);


        findViewById(R.id.sideMenuMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment(MapFragment.getInstance());
            }
        });

        findViewById(R.id.sideMenuSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment(SearchUsersFragment.getInstance());
            }
        });

        findViewById(R.id.sideMenuGroups).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment(GroupMainFragment.getInstance());
            }
        });

        findViewById(R.id.sideMenuInformation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment(InfoMainFragment.getInstance());
            }
        });

        findViewById(R.id.sideMenuMessagesOutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messagesSideMenu.setVisibility(View.VISIBLE);
                mainSideMenu.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.sideMenuMessagesInnerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messagesSideMenu.setVisibility(View.GONE);
                mainSideMenu.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.sideMenuPraveMessagessBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment(MessagesFragment.getInstance());
            }
        });

        findViewById(R.id.sideMenuApprovalBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment(ApprovalGroupFragment.getInstance());
            }
        });


        findViewById(R.id.sideMenuPersonalAreaOutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personalSideMenu.setVisibility(View.VISIBLE);
                mainSideMenu.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.sideMenuPersonalAreaInnerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personalSideMenu.setVisibility(View.GONE);
                mainSideMenu.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addSideMenuPersonalAreaListener() {

//        findViewById(R.id.paPersonalArea).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findViewById(R.id.mainSideMenu).setVisibility(View.VISIBLE);
//                findViewById(R.id.personalAreaSideMenu).setVisibility(View.GONE);
//            }
//        });

        findViewById(R.id.sideMenuMyProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//when click on one from menu bar item
                //startActivity(new Intent(MainActivity.this, MyProfileActivity.class));
                initFragment(MyProfileFragment.getInstance());
            }
        });

        findViewById(R.id.sideMenuSearchPeoplesSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFragment(SearchPreferencesFragment.getInstance(SearchPreferencesFragment.EDoAfterUpdate.GO_BACK, Common.user.getiUserId()));
            }
        });

        findViewById(R.id.sideMenuDisplaySettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        findViewById(R.id.sideMenuLogOut1).setOnClickListener(logoutClick);
        findViewById(R.id.sideMenuLogOut2).setOnClickListener(logoutClick);
    }

    View.OnClickListener logoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new GeneralTask(context, new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    if (result != null && !result.equals("")) {
                        if (result.equals("true")) {
                            SPManager.getInstance(getApplicationContext()).logoffUser();
                            SendCurrentLocationTimer.getInstance(context).stopTimer();
                            ChatMainManager.getInstance(getApplicationContext()).logout();
                            SPManager.getInstance(getApplicationContext()).saveInteger(SPManager.NOTIFICATION_COUNTER, 0);
                            updateRegIdInServer();
                            Common.user = new User();
                            GroupsListFragment.setInstance(null);
                            MapFragment.setInstance(null);
                            LoginManager.getInstance().logOut();
                            if (mGoogleApiClient.isConnected()) {
                                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                                mGoogleApiClient.disconnect();
                            }
                            destroyFragments();
                            finish();
                            startActivity(new Intent(context, SignUpActivity.class));
                        }
                        else
                        if (result.equals("false"))
                            Toast.makeText(context.getApplicationContext(), getString(R.string.logoutError), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context.getApplicationContext(), getString(R.string.loginError), Toast.LENGTH_LONG).show();
                    }
                }
            }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(),"{\"iUserId\":\"" +  Common.user.getiUserId()  + "\"}", ConnectionUtil.LogOut);//שולח משתמש
        }
    };

    private void destroyFragments() {//when click logOut button
        BaseInfoFragment.instance = null;
        ProfileInfoFragment.instance = null;
        MoreInfoFragment.instance = null;
        SearchUsersFragment.instance = null;
        MapFragment.setInstance(null);
        GroupsListFragment.setInstance(null);
        GroupChatFragment.instance = null;
        PlacesFragment.instance = null;
        ChatFragment.instance = null;
        MessagesFragment.instance = null;
        ApprovalGroupFragment.instance = null;
        GroupChatFragment.instance = null;
        GroupsListFragment.instance = null;
        MyProfileFragment.instance = null;
        Common.user = null;
        Common.allUsers = null;
        Common.CrashTag = "CrashTag";
        Common.existUsers = new HashMap<>();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void updateRegIdInServer() {//עדכון בשרת
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("iUserId", Common.user.getiUserId());
            jsonObject.put("nvTokenId", "");
            jsonObject.put("iDeviceType", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GeneralTask(context, new UseTask() {
            @Override
            public void doAfterTask(String result) {

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), jsonObject.toString(), ConnectionUtil.UpdateTokenAndDevice);

    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(navList)) {
            closeNavigation();
            return;
        }
        if (getCurrentFragment().equals(SearchUsersFragment.class.toString()) || getCurrentFragment().equals("")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(null);
            alertDialogBuilder
                    .setMessage(getResources().getString(R.string.ifYouExit))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.exitYes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FragmentManager fm = getSupportFragmentManager();
                            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                fm.popBackStack();
                            }
                            MainActivity.super.onBackPressed();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.exitNo), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

       /* else if (getCurrentFragment().equals(MapFragment.class.toString())) {
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount() - 1; ++i) {
                fm.popBackStack();
            }
            initFragment(SearchUsersFragment.getInstance());
        } */
        } else
            if (!getCurrentFragment().equals("") && getCurrentFragment().equals(MapFragment.getInstance().getClass().toString())) {
            if (MapFragment.getInstance().getmMapFooterManager().isCreateMeetingPoint()) {
                MapFragment.getInstance().getmMapFooterManager().setIsCreateMeetingPoint(false);
                MapFragment.getInstance().getmMapFooterManager().changeFooter(MapFooterManager.MapFooterPosition.MAP_FOOTER_ME);
            } else {
                getSupportFragmentManager().popBackStack();
                if (getSupportFragmentManager().getBackStackEntryCount() == 1)
                    finish();
            }
        } else {
            getSupportFragmentManager().popBackStack();
            if (getSupportFragmentManager().getBackStackEntryCount() == 1)
                finish();
        }


        /*super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            //onKeyDown(KeyEvent.KEYCODE_HOME, new KeyEvent(null));
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }*/
    }

    public void backClick(View view) {
//        this.onBackPressed();
        getSupportFragmentManager().popBackStack();
        if (getSupportFragmentManager().getBackStackEntryCount() == 1)
            finish();
    }

    public void initFragment(Fragment fragment) {
        initFragment(fragment, true);
    }

    public void initFragment(Fragment fragment, boolean addToBackStack) {
        saveFragment = fragment;
        Utils.HideKeyboard(context);//שעוברים לפרגמט חדש סוגר מקלדת אם היא פתוחה
        if (!getCurrentFragment().equals("") && getCurrentFragment().equals(fragment.getClass().toString())) {
            closeNavigation();//menu
            return;
        }
        //addToBackStack = false cause all fragment became transparent
        runOnUiThread(new Runnable() {///////////////למה בסרד נפרד זה  לא שרת
            @Override
            public void run() {
                closeNavigation();
            }
        });
        ApplicationSingleton application = (ApplicationSingleton) getApplication();//שליחה לסטטיסטיקה
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(fragment.getClass().toString());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, fragment);
        boolean existInStack = false;
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++)
            if (getSupportFragmentManager().getBackStackEntryAt(i).getName().equals(Fragment.class.toString())) {
                existInStack = true;
                return;
            }
        if (!existInStack)
            fragmentTransaction.addToBackStack(fragment.getClass().toString());

//        Log.d("addToBackStack", fragment.getClass().toString());
        //fragmentTransaction.attach(fragment);
        //}
        fragmentTransaction.commitAllowingStateLoss();

        //;lastFragmentInStack = fragment.getClass().toString();
        //getFragmentManager().executePendingTransactions();


    }

    public void openNavigation() {
        if (KeyboardIsShowing())
            Utils.HideKeyboard(this);
        drawer.openDrawer(navList);
    }

    public void closeNavigation() {
        if (drawer != null)
            drawer.closeDrawer(navList);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        SendCurrentLocationTimer.getInstance(context).stopTimer();
        //remove users from mapFragment
        super.onDestroy();
    }


    public void messageReceivedDialog(final User user, String message) {
        final Dialog dialog = new Dialog(this, R.style.Base_Theme_AppCompat_Dialog_FixedSize);
        dialog.setContentView(R.layout.public_message_received_dialog);
        dialog.setTitle("");

        ((TextView) dialog.findViewById(R.id.public_message_text)).setText(message);

        dialog.findViewById(R.id.public_message_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.public_message_replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (MainActivity.this != null)
                    if (!getCurrentFragment().equals("") && getCurrentFragment().equals(ChatFragment.getInstance().getClass().toString())) {
                        ApplicationSingleton application = (ApplicationSingleton) getApplication();
                        mTracker = application.getDefaultTracker();
                        mTracker.setScreenName(ChatFragment.getInstance().getClass().toString());
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.mainContainer, ChatFragment.getInstance(MainActivity.this, user.getJson()));
                        boolean existInStack = false;
                        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++)
                            if (getSupportFragmentManager().getBackStackEntryAt(i).getName().equals(Fragment.class.toString())) {
                                existInStack = true;
                                return;
                            }
                        if (!existInStack)
                            fragmentTransaction.addToBackStack(ChatFragment.getInstance().getClass().toString());
//        Log.d("addToBackStack", fragment.getClass().toString());
                        //fragmentTransaction.attach(fragment);
                        //}
                        fragmentTransaction.commitAllowingStateLoss();
                        return;
                    }
                initFragment(ChatFragment.getInstance(MainActivity.this, user.getJson()));
            }
        });
        dialog.show();
    }

    private Boolean KeyboardIsShowing() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void changeProfileImage() {
        if (mProfileImg != null)
            new Utils.DownloadImageTask(mProfileImg).execute(Common.user.getNvImage());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Utils.HideKeyboard(MainActivity.this);
        return true;
    }
}
