package webit.android.shanti.main.SearchUsers;


import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quickblox.chat.QBChatService;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.CustomButton;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.MainBaseFragment;
import webit.android.shanti.main.info.InfoMainFragment;
import webit.android.shanti.main.map.MapFragment;

public class SearchUsersFragment extends MainBaseFragment implements View.OnClickListener, AbsListView.OnScrollListener {

    public static SearchUsersFragment instance = null;

    private enum ListType {
        ALL_SHANTI_USERS_LIST,
        SEARCH_USERS_LIST,
        SORT_NAME_USERS_LIST,
        SORT_DISTANCE_USERS_LIST
    }


    public static SearchUsersFragment getInstance() {
        if (instance == null) {
            instance = new SearchUsersFragment();
        }
        return instance;
    }

    private String TAG = "SearchUsersFragment";
    private int selectedVisibleItem;
    private View mRootView;
    private ListView usersListView;
    private RelativeLayout emptyView;
    EditText searchText;
    TextView nameSort;
    TextView farSort;
    private CustomButton mSortByBtn;
    SearchUsersAdapter mSearchUsersAdapter;
    String charSequenceChange;
    private static List<User> users;
    List<User> users2;

    List<User> usersHelp;
    List<User> searchUser;
    public static int point = 0;
    public static int pointSearch = 0;
    private int RowHeight = 218;
    public static int rowsInPage;
    private static int preLast;
    private boolean flag = false;
private boolean flag1 = false;
    private boolean flagSortName = false;
    private ListType listType = ListType.ALL_SHANTI_USERS_LIST;

    public SearchUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public void setActionBarEvents(View view) {
        super.setActionBarEvents(view);

        super.setActionBarEvents(view);
        view.findViewById(R.id.actionMapToggleBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).drawerClick();
            }
        });

        view.findViewById(R.id.actionMapPublicMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment.getInstance().getmMapFooterManager().sendPublicMessageClick();
            }
        });

        view.findViewById(R.id.actionMapLeftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).initFragment(InfoMainFragment.getInstance());
            }
        });

//        view.findViewById(R.id.messagesActionMapBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                initFragment(MapFragment.getInstance());
//            }
//        });
//
//        setBackView(view.findViewById(R.id.messagesActionBackBtn));
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_search_user, container, false);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            setActionBarEvents(mRootView);
            this.usersListView = (ListView) mRootView.findViewById(R.id.searchList);
            mSortByBtn = (CustomButton) mRootView.findViewById(R.id.sort_by);
            mSortByBtn.setOnClickListener(this);
            this.emptyView = (RelativeLayout) mRootView.findViewById((R.id.searchEmptyList));
            this.searchText = (EditText) mRootView.findViewById(R.id.searchSearchText);
            this.farSort = (TextView) mRootView.findViewById(R.id.searchFarSort);
            this.farSort.setOnClickListener(this);
            this.nameSort = (TextView) mRootView.findViewById(R.id.searchNameSort);
            this.nameSort.setOnClickListener(this);

//            this.farSort = (TextView) mRootView.findViewById(R.id.searchNameSort);

            this.searchText.addTextChangedListener(new TextWatcher() {//בכל הקלדה בתיבת חיפוש
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    searchUser = null;
                    pointSearch = 0;
                    if (charSequence.length() == 0) {
                        listType = ListType.ALL_SHANTI_USERS_LIST;
                        getAllUsers();
                    } else {
                        listType = ListType.SEARCH_USERS_LIST;
                        getSearchUsers();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            this.usersListView.setOnScrollListener(this);//בגלילת רשימת משתמשים

        }
        //this.searchText.setText("");
        //this.searchText.getText().clear();
        getNumRowsForTwoPages();
        point = 0;
        getAllUsers();//שולף את רשימת המשתמשים ל common
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//שלא יראו מקלדת אחרי טעינת הרשימה
        return mRootView;
    }

    //מחזיר מספר רשומות משתמשים ל - 2 עמודים
    public int getNumRowsForTwoPages() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int screenHeight = display.getHeight();
        rowsInPage = screenHeight/RowHeight;
        rowsInPage *= 2;
        return rowsInPage;
    }

    public void getSearchUsers() {
        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                Type programsListType = new TypeToken<List<User>>() {
                }.getType();
                try {
                    usersHelp = new Gson().fromJson(result, programsListType);//רשימת משתמשים שחוזרת מהשרת
                    if (usersHelp.size() > 0) {//אם חזרה מלאה
                        if (searchUser == null)
                            searchUser = usersHelp;
                        else {
                            searchUser.addAll(usersHelp);
                            usersListView.setSelection(selectedVisibleItem);
                        }
                        buildListView(searchUser);//בונה את רשימת המשתמשים - שולח לאדפטר
                        flag = true;
                    }
                    else {
                        if (searchUser == null) {
                            emptyView.setVisibility(View.VISIBLE);
                            usersListView.setVisibility(View.GONE);
                            //usersListView.setEmptyView(emptyView);//מציב תצוגה של view ריק - במקרה שאין משתמשים
                        }

                    }
                } catch (Exception e) {
                    Log.d("Json Exception", e.getMessage());
                }
            }
        }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"languageId\":\"" + Common.user.getiAppLanguageId() + "\",\"text\":\"" + searchText.getText().toString() + "\",\"numRow\":\"" + rowsInPage+ "\",\"id\":\"" + Common.user.getiUserId() + "\",\"point\":\"" + pointSearch + "\"}", ConnectionUtil.SearchName);

    }

    public void getFilterNameUsers() {
        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                Type programsListType = new TypeToken<List<User>>() {
                }.getType();
                try {
                    usersHelp = new Gson().fromJson(result, programsListType);//רשימת משתמשים שחוזרת מהשרת
                    if (usersHelp.size() > 0) {//אם חזרה מלאה
                        if (searchUser == null) {
                            searchUser = usersHelp;
                            buildListView(searchUser);//בונה את רשימת המשתמשים - שולח לאדפטר
                        }
                        else {
                            searchUser.addAll(usersHelp);
                            buildListView(searchUser);//בונה את רשימת המשתמשים - שולח לאדפטר
                            usersListView.setSelection(selectedVisibleItem);
                        }
                        //buildListView(searchUser);//בונה את רשימת המשתמשים - שולח לאדפטר
                        flag = true;
                    }
                } catch (Exception e) {
                    Log.d("Json Exception", e.getMessage());
                }
            }
        }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"languageId\":\"" + Common.user.getiAppLanguageId() + "\",\"numRow\":\"" + rowsInPage+ "\",\"id\":\"" + Common.user.getiUserId() + "\",\"point\":\"" + pointSearch + "\"}", ConnectionUtil.FilterName);

    }

    public void getFilterDistanceUsers() {
        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                Type programsListType = new TypeToken<List<User>>() {
                }.getType();
                try {
                    usersHelp = new Gson().fromJson(result, programsListType);//רשימת משתמשים שחוזרת מהשרת
                    if (usersHelp.size() > 0) {//אם חזרה מלאה
                        if (searchUser == null)
                            searchUser = usersHelp;
                        else {
                            searchUser.addAll(usersHelp);
                            usersListView.setSelection(selectedVisibleItem);
                        }
                        buildListView(searchUser);//בונה את רשימת המשתמשים - שולח לאדפטר
                        flag = true;
                    }
                } catch (Exception e) {
                    Log.d("Json Exception", e.getMessage());
                }
            }
        }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"languageId\":\"" + Common.user.getiAppLanguageId() + "\",\"numRow\":\"" + rowsInPage+ "\",\"id\":\"" + Common.user.getiUserId() + "\",\"point\":\"" + pointSearch + "\",\"longitude\":\"" + Common.user.getoLocation().getdLongitude() + "\",\"latitude\":\"" + Common.user.getoLocation().getdLatitude() + "\"}", ConnectionUtil.FilterDistance);

    }


    //שולף את רשימת המשתמשים ל common
    public void getAllUsers() {
        //if (Common.getAllUsers() == null)//אם אין לי רשימת משתמשים
            new GeneralTask(getActivity(), new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    Type programsListType = new TypeToken<List<User>>() {
                    }.getType();
                    try {
                        usersHelp = new Gson().fromJson(result, programsListType);//רשימת משתמשים שחוזרת מהשרת
                        if (usersHelp != null) {//אם חזרה מלאה
                            if (users == null) {//בפעם הראשונה שפותחים את האפליקציה
                                Common.setAllUsers(usersHelp);//מציב ב common את רשימת המשתמשים
                                users = Common.getAllUsers();//מציב ב users את רשימת המשתמשים
                                for (int i = 0; i < users.size(); i++) {//עובר על רשימת המשתמשים
                                    if (users.get(i).setLastBroadcastDate(Utils.convertToJsonDate(users.get(i).getNvLastBroadcastDate()))) {//התאריך האחרון ששידר את המיקום שלו
                                        Log.d("check", "name: " + users.get(i).getNvShantiName() + " NvLast:" + users.get(i).getNvLastBroadcastDate() + " Last:" + users.get(i).getLastBroadcastDate() +
                                                "  " + users.get(i).getiUserId());
//                                        if (users.get(i).getLastBroadcastDate() == null || users.get(i).getNvLastBroadcastDate().length() < 8)//אם באמת לא חזר מהשרת null ואםהתאריך שגוי - קטן משמונה בפורמט
//                                            users.remove(users.get(i));//מוחק מרשימת המשתמשים
//                                        else
                                            Common.getExistUsers().put(users.get(i).getiUserId(), true);//שם ב Common.getExistUsers את המשתמשים הקיימים עכשיו
                                    }
                                }
                                Log.d("endList", "come to the end and built list 1");
                                buildListView(users);//בונה את רשימת המשתמשים - שולח לאדפטר
                                flag = true;
flag1 = true;
                            }
                            else {//מהגלילה הראשונה
                                for (int i = 0; i < usersHelp.size(); i++) {
                                    usersHelp.get(i).setLastBroadcastDate(Utils.convertToJsonDate(usersHelp.get(i).getNvLastBroadcastDate()));
                                    Log.d("check", "name: " + usersHelp.get(i).getNvShantiName() + " NvLast:" + usersHelp.get(i).getNvLastBroadcastDate() + " Last:" + usersHelp.get(i).getLastBroadcastDate() +
                                            "  " + usersHelp.get(i).getiUserId());
                                }
                                for (int i = 0; i < usersHelp.size(); i++) {
                                    if (Common.getExistUsers().get(usersHelp.get(i).getiUserId()) == null) {//אם לא קיים ברשימה שב common
                                        Common.getAllUsers().add(usersHelp.get(i));//מוסיף ל common ול user
                                        Common.getExistUsers().put(usersHelp.get(i).getiUserId(), true);
                                    }
                                }
                                if (usersHelp.size() > 0) {
                                    buildListView(users);//בונה את רשימת המשתמשים - שולח לאדפטר
                                    usersListView.setSelection(selectedVisibleItem);
                                    flag = true;
                                }
                            }
                        }
                        else
                            Toast.makeText(getActivity(),"אין אנשי קשר נוספים",Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.d("Json Exception", e.getMessage());
                    }
                }
            }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"iAppLanguageId\":\"" + Common.user.getiAppLanguageId() + "\",\"numRow\":\"" + rowsInPage + "\",\"id\":\"" + Common.user.getiUserId() + "\",\"point\":\"" + point + "\"}", ConnectionUtil.GetUsersListToSearch);
    }



    //בונה את רשימת המשתמשים - שולח לאדפטר
    void buildListView(final List<User> users) {
        this.usersListView.setEmptyView(emptyView);//מציב תצוגה של view ריק - במקרה שאין משתמשים
        this.mSearchUsersAdapter = new SearchUsersAdapter(getActivity(), users, searchText);//שולח לאדפטר רשימת משתמשים קיימים בתוספת תוכן תיבת חיפוש
        this.usersListView.setAdapter(mSearchUsersAdapter);//מציב את האדפטר במקום בו צריכה להופיע הרשימה בדף
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchFarSort://מיון לפי מרחק
                flagSortName = false;
                listType = ListType.SORT_DISTANCE_USERS_LIST;
                searchText.getText().clear();
                pointSearch = 0;
                searchUser = null;
                getFilterDistanceUsers();
                nameSort.setVisibility(View.GONE);
                farSort.setVisibility(View.GONE);
//                if (users == null || users.size() == 0)
//                    break;

//                if (users == null || users.size() == 0)
//                    return;


//                farSort.setBackground(getActivity().getResources().getDrawable(R.drawable.sort_selected));
//                farSort.setTextColor(getActivity().getResources().getColor(R.color.white));
//                nameSort.setBackground(getActivity().getResources().getDrawable(R.drawable.edit_text_border));
//                nameSort.setTextColor(getActivity().getResources().getColor(R.color.gray_light));
//                try {
//                    Log.d("mimi1", users.toString());
//                    for (int i = 0; i < users.size(); i++) {
//                        if (users.get(i).getLastBroadcastDate() == null || users.get(i).getNvLastBroadcastDate().length() < 8)
//                            users.remove(users.get(i));
//
//                    }
//                    User.isComparable = !User.isComparable;
//                    Collections.sort(users);
//
//                } catch (Exception e) {
//                    Log.d("sort user", e.toString());
//                }
//                this.mSearchUsersAdapter = new SearchUsersAdapter(getActivity(), users, searchText);
//                this.usersListView.setAdapter(mSearchUsersAdapter);
//                farSort.setVisibility(View.GONE);
//                nameSort.setVisibility(View.GONE);
                break;
            case R.id.searchNameSort:
                flagSortName = true;
                listType = ListType.SORT_NAME_USERS_LIST;
                searchText.getText().clear();
                pointSearch = 0;
                searchUser = null;
                getFilterNameUsers();
                nameSort.setVisibility(View.GONE);
                farSort.setVisibility(View.GONE);
//                if (users == null || users.size() == 0)
//                    return;

//                nameSort.setBackground(getActivity().getResources().getDrawable(R.drawable.sort_selected));
//                nameSort.setTextColor(getActivity().getResources().getColor(R.color.white));
//                farSort.setBackground(getActivity().getResources().getDrawable(R.drawable.edit_text_border));
//                farSort.setTextColor(getActivity().getResources().getColor(R.color.gray_light));
//                mSearchUsersAdapter.sortListByShantiName();
                break;
            case R.id.sort_by://לחיצה על כפתור מיון לפי
                flagSortName = false;
                if (farSort.getVisibility() == View.VISIBLE) {
                    farSort.setVisibility(View.GONE);
                    nameSort.setVisibility(View.GONE);
                } else {
                    farSort.setBackground(getActivity().getResources().getDrawable(R.drawable.edit_text_border));
                    farSort.setTextColor(getActivity().getResources().getColor(R.color.gray_light));
                    nameSort.setBackground(getActivity().getResources().getDrawable(R.drawable.edit_text_border));
                    nameSort.setTextColor(getActivity().getResources().getColor(R.color.gray_light));
                    farSort.setVisibility(View.VISIBLE);
                    nameSort.setVisibility(View.VISIBLE);
                }
                break;
        }
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (view.getId()) {
            case R.id.searchList: {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL)
                    Utils.HideKeyboard(getActivity());
            }
        }
    }

    @Override//בעת גלילה
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        switch(view.getId()) {
            case R.id.searchList: {//אם גללו את רשימת משתמשים
                final int lastItem = firstVisibleItem + visibleItemCount;//מוצא את הפריט האחרון שנראה בתצוגה עכשיו
                selectedVisibleItem = firstVisibleItem + 1;//שומר את המיקום שבראש הרשימה - לאן נחזור אחרי גלילה
                if (lastItem == totalItemCount && flag && lastItem != preLast) {//אם מוצג הפריט האחרון ברשימה&& users.size() > point
                    flag = false;//שלא יבצע הוספת נתונים עד שלא סיים להוסיף את ההוספה הקודמת
                    switch (listType) {
                        case ALL_SHANTI_USERS_LIST:
                            point += getNumRowsForTwoPages();//מאיזה מיקום ברשימת המשתמשים השרת ישלח רשומות
                            getAllUsers();
                            break;
                        case SEARCH_USERS_LIST:
                            pointSearch += getNumRowsForTwoPages();//מאיזה מיקום ברשימת המשתמשים השרת ישלח רשומות
                            getSearchUsers();
                            break;
                        case SORT_NAME_USERS_LIST:
                            pointSearch += getNumRowsForTwoPages();//מאיזה מיקום ברשימת המשתמשים השרת ישלח רשומות
                            getFilterNameUsers();
                            break;
                        case SORT_DISTANCE_USERS_LIST:
                            pointSearch += getNumRowsForTwoPages();//מאיזה מיקום ברשימת המשתמשים השרת ישלח רשומות
                            getFilterDistanceUsers();
                            break;
                        default:
                            break;
                    }
                    if (preLast != lastItem) { //to avoid multiple calls for last item
                    Log.d("Last", "Last");
                    preLast = lastItem;
                    }
                }
            }
        }
    }
//                switch (listType) {
//                    case ALL_SHANTI_USERS_LIST: {
//                        final int lastItem = firstVisibleItem + visibleItemCount;//מוצא את הפריט האחרון שנראה בתצוגה עכשיו
//                        selectedVisibleItem = firstVisibleItem + 1;//שומר את המיקום שבראש הרשימה - לאן נחזור אחרי גלילה
//                        if (lastItem == totalItemCount && flag) {//אם מוצג הפריט האחרון ברשימה&& users.size() > point
//                            point += getNumRowsForTwoPages();//מאיזה מיקום ברשימת המשתמשים השרת ישלח רשומות
//                            flag = false;//שלא יבצע הוספת נתונים עד שלא סיים להוסיף את ההוספה הקודמת
//                            getAllUsers();
//                            if (preLast != lastItem) { //to avoid multiple calls for last item
//                                Log.d("Last", "Last");
//                                preLast = lastItem;
//                            }
//                        }
//                        break;
//                    }
//                    case SEARCH_USERS_LIST: {
//                        final int lastItem = firstVisibleItem + visibleItemCount;//מוצא את הפריט האחרון שנראה בתצוגה עכשיו
//                        selectedVisibleItem = firstVisibleItem + 1;//שומר את המיקום שבראש הרשימה - לאן נחזור אחרי גלילה
//                        if (lastItem == totalItemCount && flag) {//אם מוצג הפריט האחרון ברשימה&& users.size() > point
//                            pointSearch += getNumRowsForTwoPages();//מאיזה מיקום ברשימת המשתמשים השרת ישלח רשומות
//                            flag = false;//שלא יבצע הוספת נתונים עד שלא סיים להוסיף את ההוספה הקודמת
//                            getSearchUsers();
//                            if (preLast != lastItem) { //to avoid multiple calls for last item
//                                Log.d("Last", "Last");
//                                preLast = lastItem;
//                            }
//                        }
//                        break;
//                    }
//                    case SORT_NAME_USERS_LIST: {
//                        final int lastItem = firstVisibleItem + visibleItemCount;//מוצא את הפריט האחרון שנראה בתצוגה עכשיו
//                        selectedVisibleItem = firstVisibleItem + 1;//שומר את המיקום שבראש הרשימה - לאן נחזור אחרי גלילה
//                        if (lastItem == totalItemCount && flag) {//אם מוצג הפריט האחרון ברשימה&& users.size() > point
//                            pointSearch += getNumRowsForTwoPages();//מאיזה מיקום ברשימת המשתמשים השרת ישלח רשומות
//                            flag = false;//שלא יבצע הוספת נתונים עד שלא סיים להוסיף את ההוספה הקודמת
//                            getFilterNameUsers();
//                            if (preLast != lastItem) { //to avoid multiple calls for last item
//                                Log.d("Last", "Last");
//                                preLast = lastItem;
//                            }
//                        }
//                        break;
//                    }
//                    case SORT_DISTANCE_USERS_LIST: {
//                        final int lastItem = firstVisibleItem + visibleItemCount;//מוצא את הפריט האחרון שנראה בתצוגה עכשיו
//                        selectedVisibleItem = firstVisibleItem + 1;//שומר את המיקום שבראש הרשימה - לאן נחזור אחרי גלילה
//                        if (lastItem == totalItemCount && flag) {//אם מוצג הפריט האחרון ברשימה&& users.size() > point
//                            pointSearch += getNumRowsForTwoPages();//מאיזה מיקום ברשימת המשתמשים השרת ישלח רשומות
//                            flag = false;//שלא יבצע הוספת נתונים עד שלא סיים להוסיף את ההוספה הקודמת
//                            getFilterDistanceUsers();
//                            if (preLast != lastItem) { //to avoid multiple calls for last item
//                                Log.d("Last", "Last");
//                                preLast = lastItem;
//                            }
//                        }
//                        break;
//                    }
//
//                }


//                if (searchText.getText().length() == 0 && farSort.getVisibility() == View.GONE && nameSort.getVisibility() == View.GONE){//חיפוש אנשים רגיל בלי חיפוש וסינון
//
//                }
//                else {
//                    if (searchText.getText().length() != 0 && farSort.getVisibility() == View.GONE && nameSort.getVisibility() == View.GONE){
//
//                    }
//                    else {
//                        if (flagSortName){
//                            final int lastItem = firstVisibleItem + visibleItemCount;//מוצא את הפריט האחרון שנראה בתצוגה עכשיו
//                            selectedVisibleItem = firstVisibleItem + 1;//שומר את המיקום שבראש הרשימה - לאן נחזור אחרי גלילה
//                            if (lastItem == totalItemCount && flag) {//אם מוצג הפריט האחרון ברשימה&& users.size() > point
//                                pointSearch += getNumRowsForTwoPages();//מאיזה מיקום ברשימת המשתמשים השרת ישלח רשומות
//                                flag = false;//שלא יבצע הוספת נתונים עד שלא סיים להוסיף את ההוספה הקודמת
//                                getFilterNameUsers();
//                                if (preLast != lastItem) { //to avoid multiple calls for last item
//                                    Log.d("Last", "Last");
//                                    preLast = lastItem;
//                                }
//                            }
//                        }
//                    }
//                }



    private void GetNextUserList() {
        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {//שולף את רשימת המשתמשים מהשרת
                Type programsListType = new TypeToken<List<User>>() {
                }.getType();
                try {
                    users2 = new Gson().fromJson(result, programsListType);//עוד רשומות ל 2 עמודות, מהנקודה שנשלחה
                    for (int i = 0; i < users2.size(); i++) {
                        users2.get(i).setLastBroadcastDate(Utils.convertToJsonDate(users2.get(i).getNvLastBroadcastDate()));
                        Log.d("check", "name: " + users2.get(i).getNvShantiName() + " NvLast:" + users2.get(i).getNvLastBroadcastDate() + " Last:" + users2.get(i).getLastBroadcastDate() +
                                "  " + users2.get(i).getiUserId());
                    }
                    for (int i = 0; i < users2.size(); i++) {
                        if (users2.get(i).getLastBroadcastDate() == null || users2.get(i).getNvLastBroadcastDate().length() < 8)//אם תאריך - נראה לאחרונה לא תקין
                            users2.remove(users2.get(i));//מוחק מהרשימה
                        else {
                            if (Common.getExistUsers().get(users2.get(i).getiUserId()) == null) {//אם לא קיים ברשימה שב common
                                Common.getAllUsers().add(users2.get(i));//מוסיף ל common
                                Common.getExistUsers().put(users2.get(i).getiUserId(), true);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d("Json Exception", e.getMessage());
                }
            }
        }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"iAppLanguageId\":\"" + Common.user.getiAppLanguageId() + "\",\"numRow\":\"" + rowsInPage + "\",\"point\":\"" + point + "\"}", ConnectionUtil.GetUsersListToSearch);

    }
    //        usersListView.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return users.size();
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return users.get(position);
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return 0;
//            }
//
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                final ViewHolder holder;
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_search_user, null);
//                    holder = new ViewHolder();
//                    holder.searchUserImage = (SelectableRoundedImageView) convertView.findViewById(R.id.searchUserImage);
//                    holder.searchUserName = (TextView) convertView.findViewById(R.id.searchUserName);
//                    holder.searchUserTime = (TextView) convertView.findViewById(R.id.searchUserTime);
//                    holder.searchUserFar = (TextView) convertView.findViewById(R.id.searchUserFar);
//                    convertView.setTag(holder);
//                } else {
//                    holder = (ViewHolder) convertView.getTag();
//                }
//
//                final User user = users.get(position);
//
//                if (CalcDateToOnlineUsers(user.getNvLastBroadcastDate()))
//                    holder.searchUserImage.setBorderColor(getActivity().getResources().getColor(R.color.green));
//                else
//                    holder.searchUserImage.setBorderColor(getActivity().getResources().getColor(R.color.red));
//
//                Picasso.with(getActivity()).load(user.getNvImage()).into(holder.searchUserImage);
//                holder.searchUserName.setText(user.getNvShantiName() + ", " + user.getoCountry().getNvValue());
//                holder.searchUserTime.setText(user.getNvLastBroadcastDate());
//                new AsyncTask<Void, Void, Void>() {
//
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        try {
//                            final String distance = DistanceCalculator.GetDistance(Common.user.getoLocation().getAsLatLng(), user.getoLocation().getAsLatLng());
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    holder.searchUserFar.setText(distance);
//                                }
//                            });
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    }
//                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//                //final User finalUser = user;
//                convertView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        initFragment(ChatFragment.getInstance(user.getJson()));
//                    }
//                });
//
//                return convertView;
//            }
//
//            class ViewHolder {
//                SelectableRoundedImageView searchUserImage;
//                TextView searchUserName;
//                TextView searchUserTime;
//                TextView searchUserFar;
//            }
//
//            private boolean CalcDateToOnlineUsers(String lastBroadcastDate) {
//                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
//                try {
//
//                    Date date = formatter.parse(lastBroadcastDate);
//                    long lastBroadcastDateMilliSeconds = date.getTime();
//                    long nowDateMilliseconds = new Date().getTime();
//                    if (nowDateMilliseconds - lastBroadcastDateMilliSeconds <= 1000)
//                        return true;
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                return false;
//            }
//        });
}
