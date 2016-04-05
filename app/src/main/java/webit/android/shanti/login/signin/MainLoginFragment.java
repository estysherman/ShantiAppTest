package webit.android.shanti.login.signin;

import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import webit.android.shanti.BuildConfig;
import webit.android.shanti.R;
import webit.android.shanti.chat.chatManager.core.ChatMainManager;
import webit.android.shanti.customViews.CustomButton;
import webit.android.shanti.entities.User;
import webit.android.shanti.entities.UserMembership;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.SPManager;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.main.MainActivity;

//הדף המכיל את כפתורי הכניסה לאפליקציה ואת הקוד של פייסבוק וגוגל+
public class MainLoginFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private CallbackManager callbackManager;
    private View mRootView;
    private SignInButton signInGoogleButton;
    private LoginButton signInGoogleFacebookButton;
    private boolean isNormalLogin = true;
    private GoogleApiClient mGoogleApiClient;
    private int DeviceId = 456;
    private String DeviceDisplayLanguage;
    private int LanguageId;


    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GET_DETAILS = 9000;

    private ProgressDialog mProgressDialog;

    public MainLoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mRootView = inflater.inflate(R.layout.fragment_main_login, null);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);//allow the orientation change: portrait/landscape
        signInGoogleButton = (SignInButton) mRootView.findViewById(R.id.sign_in_button);//google+
        signInGoogleFacebookButton = (LoginButton) mRootView.findViewById(R.id.facebook_login);//facebook

        signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

//        if (DeviceDisplayLanguage.equals(getActivity().getString(R.string.DeviceDisplayHebrew))) {
//            LanguageId = 1;
//        }
//        if (DeviceDisplayLanguage.equals(getActivity().getString(R.string.DeviceDisplayEnglish)))
//            LanguageId = 2;

        //facebook
        callbackManager = CallbackManager.Factory.create();
        signInGoogleFacebookButton.setFragment(this);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {//success to conection to facebook on the machine
                        final GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),//מאפשר גישה למכשיר
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, final GraphResponse response) {
                                        // Application code
                                        final JSONObject jsonObject = response.getJSONObject();//תגובה
                                        if (jsonObject != null) {//קיבל תגובה
                                            if (jsonObject.toString().contains("id"))//אם מכיל ID
                                                try {
                                                    //התחברות דרך פייסבוק
                                                    new GeneralTask(MainLoginFragment.this.getActivity(), new UseTask() {
                                                        @Override
                                                        public void doAfterTask(String result) {
                                                            if (result != null) {
                                                                Type programsListType = new TypeToken<User>() {
                                                                }.getType();
                                                                final User user = new Gson().fromJson(result, programsListType);//מקבל משתמש
                                                                if (user.getiUserId() == -1) {//new user
                                                                    if (!jsonObject.toString().contains("email")) {//אם אין לו מייל
                                                                        try {
                                                                            getEmailAlertDialog(jsonObject.get("id").toString(), true, jsonObject, null);//פותח דיאלוג להכנסת מייל
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    } else {//אם יש לו מייל
                                                                        try {//בודק עם המייל קיים במערכת
                                                                            alreadyExistEmail(jsonObject.get("email").toString(), jsonObject.get("id").toString(), true, jsonObject, null);
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                } else {//old user
                                                                    try {
                                                                        user.setNvFacebookUserId(jsonObject.get("id").toString());//מציב את ה ID מפייסבוק ב USER
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    UserMembership userMembership = new UserMembership();
                                                                    userMembership.setNvUserName(user.getNvEmail());
                                                                    userMembership.setNvUserPassword(user.getNvFacebookUserId());
                                                                    user.setoUserMemberShip(userMembership);
                                                                    login(user, MainLoginFragment.this.getActivity());//מכניס נתוני משתמש כמשתמש הנוכחי באפליקציה
                                                                }
                                                            } else {
                                                                SingUpWithFacebook(jsonObject, "");
                                                            }
                                                        }
                                                    }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":\"" + jsonObject.get("id").toString() +"\",\"DeviceId\":\"" + DeviceId +"\"}", ConnectionUtil.LoginFacebook);//"\",\"tokenId\":\"" + SPManager.getInstance(getActivity()).getString(SPManager.REG_ID) +"\",\"iLanguageId\":\"" + LanguageId+
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                        } else {
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,last_name, first_name,link,location,email,gender, birthday,picture,age_range");
                        request.setParameters(parameters);//שולח פרמטרים לבקשה
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                }

        );
        //google+
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestScopes(Plus.SCOPE_PLUS_LOGIN, Plus.SCOPE_PLUS_PROFILE)
                .build();
        if (mGoogleApiClient == null)
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApiIfAvailable(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

        signInGoogleButton.setSize(SignInButton.SIZE_STANDARD);
        signInGoogleButton.setScopes(gso.getScopeArray());
        Common.user = new User();

//        ImageView imageView = (ImageView) mRootView.findViewById(R.id.odeyakatz);
//        Bitmap bitmap = ImageHelper.getRoundedCornerBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap(), getResources().getColor(R.color.green), 10, 10, getActivity());
//        imageView.setImageBitmap(bitmap);
        mRootView.findViewById(R.id.mainLoginCreateUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNormalLogin) {//נכנס רגיל
                    if(Common.user!=null) {
                        Common.user.setNvGoogleUserId("");//מכניס ערך ריק לקוד גוגל
                        Common.user.setNvFacebookUserId("");//מכניס ערך ריק לקוד פייסבוק - כדי שיהיה בו ערך ולא יקרוס
                    }
                    ((SignUpActivity) getActivity()).initFragment(BaseInfoFragment.getInstance(), getString(R.string.loginSignUp));
                } else {
                    isNormalLogin = true;
                    startLogin();//איתחול כל הפרגמנטים של ההרשמה ומביא לדף ההרשמה הראשון
                }
            }
        });

        mRootView.findViewById(R.id.mainLoginSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((SignUpActivity) getActivity()).initFragment(new LoginFragment(), getString(R.string.loginEnter));
            }
        });
        return mRootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {//google+
            final GoogleSignInResult resultAcct = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            final GoogleSignInAccount acct = resultAcct.getSignInAccount();
            if (acct != null && acct.getId() != null)//אם החשבון מלא ויש מזהה לחשבון
                new GeneralTask(getActivity(), new UseTask() {
                    @Override
                    public void doAfterTask(String result) {
                        if (result != null) {
                            Log.d("result login google", result);
                            Type programsListType = new TypeToken<User>() {
                            }.getType();
                            final User user = new Gson().fromJson(result, programsListType);
                            if (user.getiUserId() == -1) {//new user
                                if (acct.getEmail().equals(""))//אם אין מייל
                                    getEmailAlertDialog(acct.getId(), false, null, resultAcct);//פותח דיאלוג להכנסת כתובת מייל
                                else
                                    alreadyExistEmail(acct.getEmail(), acct.getId(), false, null, resultAcct);//בודק אם המייל כבר קיים במערכת של האפליקציה
                            } else {//old user
                                user.setNvGoogleUserId(acct.getId());
                                UserMembership userMembership = new UserMembership();
                                userMembership.setNvUserName(user.getNvEmail());
                                userMembership.setNvUserPassword(user.getNvGoogleUserId());
                                user.setoUserMemberShip(userMembership);
                                login(user, MainLoginFragment.this.getActivity());//מכניס נתוני משתמש כמשתמש הנוכחי באפליקציה
                            }
                        } else {
                            //////////////////////////////////////////////////////////////////////מה שולח לפונקציה
                            handleSignInResult(resultAcct, "");    //הצבת נתוני משתמש מ +GOOGLE
                        }
                    }
                }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":\"" + acct.getId() +"\",\"DeviceId\":\"" + DeviceId  + "\"}", ConnectionUtil.LoginGoogle);//+ "\",\"tokenId\":\"" + SPManager.getInstance(getActivity()).getString(SPManager.REG_ID)+ "\",\"iLanguageId\":\"" + LanguageId

        } else if (requestCode == RC_GET_DETAILS) {
            if (BuildConfig.DEBUG)
                Toast.makeText(getActivity(), "dsdsdsdd", Toast.LENGTH_LONG).show();
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void handleGetDetails(GoogleApiClient result) {
        if (result.isConnected()) {
            isNormalLogin = false;

            // Signed in successfully, show authenticated UI.
            startLogin();

            updateUI(true);
        }
    }

    //הצבת נתוני משתמש מ +GOOGLE
    private void handleSignInResult(GoogleSignInResult result, String email) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {//אם החשבון מאומת
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            if (Common.user == null)//כניסה ראשונה לאפליקציה
                Common.user = new User();
            if (acct.getGrantedScopes() != null) {//מחזיר את כל התחומים/מאפיינים שמורשים לאפליקציה שלנו
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String token;
                            String scope = "oauth2:" + Scopes.PROFILE;
                            token = GoogleAuthUtil.getToken(getActivity(), acct.getEmail(), scope);
                            Log.d("token", token);
                        } catch (Exception e) {
                            Log.d("Exception:Scopes.PROFILE", e.toString());
                        }
                    }
                }).start();

            }
            if (acct.getId() != null)
                Common.user.setNvGoogleUserId(acct.getId());//משנה את ה ID של המשתמש לID שמגיע מ GOOGLE+
            if (acct.getDisplayName() != null) {
                if (acct.getDisplayName().contains(" ")) {
                    String firstName = acct.getDisplayName().substring(0, acct.getDisplayName().indexOf(" "));
                    Common.user.setNvFirstName(firstName);//מציב שם פרטי
                    String lastName = acct.getDisplayName().substring(firstName.length() + 1, acct.getDisplayName().length());
                    Common.user.setNvLastName(lastName);//מציב שם משפחה
                }
            }
            if (acct.getPhotoUrl() != null)
                Common.user.setLoginImage(acct.getPhotoUrl().toString());
            if (acct.getEmail() != null)
                Common.user.setNvEmail(acct.getEmail());
            else
                Common.user.setNvEmail(email);
//                if (jsonObject.toString().contains("gender"))
//                {
//                    if(jsonObject.get("gender").toString().equals("female")){
//                        Common.user.setiGenderId(-3);
//                    }
//                    else if(jsonObject.get("gender").toString().equals("male")){
//                        Common.user.setiGenderId(-4);
//                    }
//                }
//                if (jsonObject.toString().contains("birthday"))
//                    Common.user.setNvBirthDate(jsonObject.get("birthday").toString());
            if (acct.getIdToken() != null)
                Log.d("google+ details getIdToken:", acct.getIdToken());
            if (acct.getServerAuthCode() != null)
                Log.d("google+ details getServerAuthCode:", acct.getServerAuthCode());
            if (acct.getGrantedScopes().toString() != null)
                Log.d("google+ details getGrantedScopes:", acct.getGrantedScopes().toString());

            //אתחול הפרגמנטים של הרשמה
            BaseInfoFragment.instance = null;
            MoreInfoFragment.instance = null;
            TakePhotoFragment.instance = null;
            ProfileInfoFragment.instance = null;
            SearchPreferencesFragment.instance = null;
            isNormalLogin = false;
            ((SignUpActivity) getActivity()).initFragment(BaseInfoFragment.getInstance(), getString(R.string.loginSignUp));
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);//הפעלת ה google+
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void getDetails(GoogleApiClient googleApiClient) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_GET_DETAILS);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
//        if (signedIn) {
//           signInGoogleButton.setVisibility(View.GONE);
//            mRootView.findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
//        } else {
//            mStatusTextView.setText("signed_out");
//
//            signInGoogleButton.setVisibility(View.VISIBLE);
//            mRootView.findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
//        }
    }

    @Override
    public void onConnected(Bundle bundle) {
//        Log.d("onconnect", bundle.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    //איתחול כל הפרגמנטים של ההרשמה ומביא לדף ההרשמה הראשון
    private void startLogin() {
        BaseInfoFragment.instance = null;
        MoreInfoFragment.instance = null;
        TakePhotoFragment.instance = null;
        ProfileInfoFragment.instance = null;
        SearchPreferencesFragment.instance = null;
        ((SignUpActivity) getActivity()).initFragment(BaseInfoFragment.getInstance(), getString(R.string.loginSignUp));
    }


    //שולח לשרת קוד משתמש, מספר מכשיר וסוג מכשיר
    public static void updateRegIdInServer(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("iUserId", Common.user.getiUserId());
            jsonObject.put("nvTokenId", SPManager.getInstance(context).getString(SPManager.REG_ID));//token = מספר מכשיר
            jsonObject.put("iDeviceType", Common.user.getiDeviceTypeId());//DeviceType = סוג מכשיר: אייפון או אנדרואיד

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GeneralTask(context, new UseTask() {
            @Override
            public void doAfterTask(String result) {

            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), jsonObject.toString(), ConnectionUtil.UpdateTokenAndDevice);
    }
    //מכניס נתוני משתמש כמשתמש הנוכחי באפליקציה
    public static void login(User user, Context context) {

        Common.user = user;
        ChatMainManager.getInstance(context).signIn(Common.user.getQBUserToChat());
        SPManager.getInstance(context).saveUser(user);
        updateRegIdInServer(context);//בכל LOGIN שולח עדכון של קוד משתמש, מספר מכשיר וסוג מכשיר - משום שלפעמים הקוד מכשיר משתנה
        Intent mainIntent = new Intent(context, MainActivity.class);//נכנס לדף הבית
        context.startActivity(mainIntent);
        ((Activity) context).finish();
    }

    //כשאין כתובת מייל - פותח תיבת דיאלוג להכנסת מייל
    private void getEmailAlertDialog(final String id, final boolean isFacebook, final JSONObject jsonObject, final GoogleSignInResult resultAcct) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.enter_email);
        dialog.setTitle(null);

        final EditText editText = (EditText) dialog.findViewById(R.id.mainLoginEmail);
        CustomButton dialogButton = (CustomButton) dialog.findViewById(R.id.mainLoginOk);//כפתור אישור

        //פותח דיאלוג להכנסת מייל
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {
                    editText.setError(getResources().getString(R.string.loginErrorRequiredEmail));
                } else if (!isEmailValid(editText.getText().toString())) {//בדיקת תקינות למייל
                    editText.setError(getResources().getString(R.string.loginErrorEmail));
                } else {//אם ה EMAIL תקין
                    editText.setError(null);
                    alreadyExistEmail(editText.getText().toString(), id, isFacebook, jsonObject, resultAcct);//בדיקה אם המייל קיים במערכת
                    dialog.dismiss();
                }
            }
        });
        //אם לא מכניסים כתובת מייל
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                LoginManager.getInstance().logOut();//יוצא מהאפליקציה
                if (mGoogleApiClient.isConnected()) {//מתנתק מ +google
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                }
            }
        });


        dialog.show();
    }
    //בדיקת תקינות - מייל
    public boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);//תבנית לביטוי עם רגישות לאותיות קטנות וגדולות
        Matcher matcher = pattern.matcher(email);//בודק את תקינות המייל
        if (matcher.matches()) {//אם מתאים
            isValid = true;//תקין
        }
        return isValid;//לא תקין
    }
    //בכניסה ראשונה לא דרך ההרשמה מקבל מזהה יחודי למשתמש
    public static void updateId(final Context context, String funcName, String email, String id) {
        String att = "";
        if (funcName.equals(ConnectionUtil.UpdateFacebookId))
            att = "nvFacebookId";
        else att = "nvGoogleId";
        new GeneralTask(context, new UseTask() {//בכניסה ראשונה לא דרך ההרשמה מקבל מזהה יחודי למשתמש
            @Override
            public void doAfterTask(String result) {//מחזיר user
                Type programsListType = new TypeToken<User>() {
                }.getType();
                final User user = new Gson().fromJson(result, programsListType);
                if (user.getiUserId() != -1) {//קיבל מזהה יחודי
                    login(user, context);
                } else
                    Toast.makeText(context, context.getResources().getString(R.string.noServer), Toast.LENGTH_LONG).show();
            }//עדכון ה id שיהיה תואם בשני סוגי הכניסות לאפליקציה
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"nvUserName\": \"" + email + "\",\"" + att + "\":\"" + id + "\"}", funcName);//עדכון ה id שיהיה תואם בשני סוגי הכניסות לאפליקציה
    }
    //הצבת נתוני משתמש מפייבוק
    private void SingUpWithFacebook(JSONObject jsonObject, String email) {
        if (Common.user == null)
            Common.user = new User();
        try {//הצבת נתוני משתמש מפייבוק
            Common.user.setNvImage("");
            if (jsonObject.toString().contains("id"))
                Common.user.setNvFacebookUserId(jsonObject.get("id").toString());
            if (jsonObject.toString().contains("email"))
                Common.user.setNvEmail(jsonObject.get("email").toString());
            else
                Common.user.setNvEmail(email);
            if (jsonObject.toString().contains("last_name"))
                Common.user.setNvLastName(jsonObject.get("last_name").toString());
            if (jsonObject.toString().contains("first_name"))
                Common.user.setNvFirstName(jsonObject.get("first_name").toString());
            if (jsonObject.toString().contains("picture")) {
                JSONObject object1 = null;
                try {
                    object1 = new JSONObject(jsonObject.get("picture").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject object2 = null;
                try {
                    object2 = new JSONObject(object1.get("data").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (object2.get("url").toString() != null) {
                        String s = object2.get("url").toString();
                        Common.user.setLoginImage(s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.toString().contains("gender")) {
                if (jsonObject.get("gender").toString().equals("female")) {
                    Common.user.setiGenderId(-3);
                } else if (jsonObject.get("gender").toString().equals("male")) {
                    Common.user.setiGenderId(-4);
                }
            }
            //todo
//            if (jsonObject.toString().contains("birthday"))
//                Common.user.setNvBirthDate(jsonObject.get("birthday").toString());
            LoginManager.getInstance().logOut();
            startLogin();
            isNormalLogin = false;
        } catch (Exception e) {
            Log.d("e", e.toString());
        }
    }
//בדיקה אם המייל קיים במערכת
    private void alreadyExistEmail(final String email, final String id, final boolean isFacebook, final JSONObject jsonObject, final GoogleSignInResult resultAcct) {
        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                if (result != null && isAdded())
                    if (result.equals("3") || result.equals("4")) {//4- רשום דרך ההרשמה רגילה ורוצה להכנס דרך פייסבוק או גוגל יהיה צריך לעדכן לו רק את id שיהיה זהה בשני סוגי הכניסות
                        //todo update;
                        // בכניסה ראשונה לא דרך ההרשמה מקבל מזהה יחודי למשתמש
                        updateId(MainLoginFragment.this.getActivity(), isFacebook ? ConnectionUtil.UpdateFacebookId : ConnectionUtil.UpdateGoogleId, email, id);
                    } else {//רשום ראשוני לאפליקציה דרך פייסבוק או גוגל +
                        if (isFacebook)//דרך גוגל יהיה false ודרך פייסבוק true
                            SingUpWithFacebook(jsonObject, email);//הצבת נתוני משתמש מ FACEBOOK
                        else
                            handleSignInResult(resultAcct, email);//הצבת נתוני משתמש מ +GOOGLE
                    }
            }
        }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"nvShantiName\": \"" + "" + "\",\"nvEmail\":\"" + email + "\"}", ConnectionUtil.CheckUserDetailsIsFree);

    }
}
