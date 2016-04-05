package webit.android.shanti.main.personal;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import webit.android.shanti.R;
import webit.android.shanti.chat.chatManager.ApplicationSingleton;
import webit.android.shanti.customViews.CustomViewsInitializer;
import webit.android.shanti.customViews.MultiSpinner;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;

public class MyProfileActivity extends ActionBarActivity {

    private String TAG ="MyProfileActivity";
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ApplicationSingleton application = (ApplicationSingleton) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        setViews();
        initUserOldValuse();
    }

    private void setViews() {
        User user = Common.user;

        CustomViewsInitializer initializer = new CustomViewsInitializer(this);
        initializer.setDatePicker(findViewById(R.id.myProfileBirthDate), (TextView) findViewById(R.id.myProfileTextBirthDate), user.getNvBirthDate());
//        initializer.setAutoComplete((AutoCompleteTextView) findViewById(R.id.myProfileCountry), CodeValue.countries);
        initializer.setSpinner((MultiSpinner) findViewById(R.id.myProfileLanguage), CodeValue.language, getString(R.string.userPrefLanguage), 1);
        initializer.setSpinner((MultiSpinner) findViewById(R.id.myProfileGender), CodeValue.gender, getString(R.string.userPrefGender), user.getiGenderId());
        initializer.setSpinner((MultiSpinner) findViewById(R.id.myProfileReligion), CodeValue.religions, getString(R.string.userPrefReligion), user.getiReligionId());
        initializer.setSpinner((MultiSpinner) findViewById(R.id.myProfileLevelReligion), CodeValue.religionLevel, getString(R.string.userPrefLevelReligion), user.getiReligiousLevelId());
        ArrayList<CodeValue> gender = new ArrayList<>();
       gender.add(new CodeValue(0,getString(R.string.genderFemale)));
        gender.add(new CodeValue( 1,getString(R.string.genderMale)));
        ((MultiSpinner) findViewById(R.id.myProfileGender)).setItems(gender, getString(R.string.userPrefGender), null,"");

    }

    private void initUserOldValuse() {
        User user = Common.user;

        ((EditText) findViewById(R.id.myProfileEmail)).setText(user.getoUserMemberShip().getNvUserName());
        ((EditText) findViewById(R.id.myProfilePassword)).setText(user.getoUserMemberShip().getNvUserPassword());
        ((EditText) findViewById(R.id.myProfileFirstName)).setText(user.getNvFirstName());
        ((EditText) findViewById(R.id.myProfileLastName)).setText(user.getNvLastName());
        ((EditText) findViewById(R.id.myProfilePhone)).setText(user.getNvPhone());
        ((EditText) findViewById(R.id.myProfileJob)).setText(user.getNvProfession());
        ((EditText) findViewById(R.id.myProfileHobby)).setText(user.getNvHobby());

        //((AutoCompleteTextView) findViewById(R.id.mpCountry)).setText(user.getNvCountry());

//        findViewById(R.id.mpTextBirthDate).setTag(user.getNvBirthDate());
//        findViewById(R.id.mpTextBirthDate).oncli

        ArrayList<Integer> chooseValues = new ArrayList<>();
    }


    private void saveChange() {

        boolean isValid = true;
       String gender = "-1", religion = "-1", forReligion = "-1";
//        String country = ((AutoCompleteTextView) findViewById(R.id.myProfileCountry)).getText() + "";
        gender = ((MultiSpinner) findViewById(R.id.myProfileGender)).getChooseValues().get(0);
        religion = ((MultiSpinner) findViewById(R.id.myProfileReligion)).getChooseValues().get(0);
        forReligion = ((MultiSpinner) findViewById(R.id.myProfileLevelReligion)).getChooseValues().get(0);
        String birthDate = findViewById(R.id.myProfileTextBirthDate).getTag() + "";
        if ( birthDate.equals("null") || gender.equals(-1) || religion.equals(-1) || forReligion.equals(-1)) {
          isValid = false;
        }
        //Odeya
        //isValid = true;
        if (!isValid) {
            Toast.makeText(this, "לא נבחרו ערכים", Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
