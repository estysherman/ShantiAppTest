package webit.android.shanti.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.entities.CodeValue;

/**
 * Created by AndroIT on 03/02/2015.
 */


public class SwitchView extends RelativeLayout {

    public enum ESpinnerType {
        Multi, Single
    }

    private ESpinnerType eSpinnerType;

    public String getDefText() {
        return defText;
    }

    public void setDefText(String defText) {
        this.defText = defText;
        textView.setText(defText);
    }

    String defText;
    TextView textView;
    Switch aSwitch;
    MultiSpinner spinnerView;
    private String searchHint;

    public SwitchView(Context context) {
        super(context);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }
//כל מה שקשור לבחירה ולאיפוסה
    public void init(AttributeSet attrs) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.custom_search_pref, this);//סימון V/X
        aSwitch = (Switch) findViewById(R.id.searchPrefSwitch);
        spinnerView = (MultiSpinner) findViewById(R.id.searchPrefSpinner);//ההגדרות שנבחרו
        textView = (TextView) findViewById(R.id.searchPrefText);
        spinnerView.eSpinnerStyle = MultiSpinner.ESpinnerStyle.SearchDef;
        spinnerView.setClickCallBack(new MultiSpinner.
                clickCallBack() {
            @Override
            public void doCallBack(HashMap<Integer, Boolean> selected) {
                if (!selected.containsValue(true))//אם לא נבחר שום פריט
                    aSwitch.setChecked(false);//סמן X
            }
        });


        TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.SwitchView, 0, 0);
        setSearchHint(a.getString(R.styleable.SwitchView_search_hint_sv));
        spinnerView.setSearchHint(this.searchHint);
        seteSpinnerType(a.getInt(R.styleable.SwitchView_spinner_type, 1));
        setDefText(a.getString(R.styleable.SwitchView_defText));

        a.recycle();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {//בשינוי X/V
                if (b) {//אם V
                    findViewById(R.id.searchPrefSwitchCon).setBackgroundResource(R.drawable.light_purpil_block);//משנה צבע רקע לסגול בהיר
                    spinnerView.setVisibility(VISIBLE);//מראה את ההגדרה שנבחרה
//                    if (spinnerView.getChooseValues() == null || spinnerView.getChooseValues().get(0) == -1|| spinnerView.getChooseValues().get(0) == -2)
                    spinnerView.openSpinner();
                    textView.setTextColor(getContext().getResources().getColor(R.color.purple_home));
                    setOnClickListener(onClickListener);
                } else {//אם x
                    findViewById(R.id.searchPrefSwitchCon).setBackgroundResource(R.drawable.wihte_block_with_stroke);
                    spinnerView.setVisibility(INVISIBLE);
                    textView.setTextColor(getContext().getResources().getColor(R.color.gray));
                    setOnClickListener(null);
                }
            }
        });
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            spinnerView.openSpinner();
        }
    };

    public ESpinnerType geteSpinnerType() {
        return eSpinnerType;
    }
//איתחול סוג ספינר
    public void seteSpinnerType(int type) {
        if (type == 0) {
            this.eSpinnerType = ESpinnerType.Single;
            spinnerView.seteSpinnerType(MultiSpinner.ESpinnerType.Single);
        } else {
            this.eSpinnerType = ESpinnerType.Multi;
            spinnerView.seteSpinnerType(MultiSpinner.ESpinnerType.Multi);
        }
    }

    public void setSearchHint(String searchHint) {
        this.searchHint = searchHint;
    }

//    List<Integer> selected = new ArrayList<>();

    public void setSpinnerItems(ArrayList<CodeValue> items, String codeTable, String defText, List<Integer> selected) {
//        this.selected = selected;
        spinnerView.setItems(items, defText, selected, codeTable);
        setSpinnerItemBool(selected);//הצבת v
    }
//הצבת v
    public void setSpinnerItemBool(List<Integer> selected) {
        if (selected != null && selected.size() > 0 && selected.get(0) != -1) {
            aSwitch.setChecked(true);//יהיה v
            spinnerView.closeSpinner(); //סגירת ספינר
        }
    }
//מחזיר רשימה של כל הערכים שנבחרו בספינר
    public ArrayList<String> getChooseValues() {
        if (aSwitch.isChecked())
            return spinnerView.getChooseValues();
        else return new ArrayList<>(Arrays.asList("-1"));
    }
//
//    @Override
//    public Parcelable onSaveInstanceState() {
//        //begin boilerplate code that allows parent classes to save state
//        Parcelable superState = super.onSaveInstanceState();
//        SavedState ss = new SavedState(superState);
//        //end
//        ss.aSwitch = this.aSwitch;
//        ss.spinnerView = this.spinnerView;
//        ss.defText  = this.defText;
//        ss.searchHint = this.searchHint;
//        ss.textView = this.textView;
//        return ss;
//    }
//
//    @Override
//    public void onRestoreInstanceState(Parcelable state) {
//        //begin boilerplate code so parent classes can restore state
//        if (!(state instanceof SavedState)) {
//            super.onRestoreInstanceState(state);
//            return;
//        }
//        SavedState ss = (SavedState) state;
//        this.aSwitch = ss.aSwitch;
//        this.defText  = ss.defText;
//        this.searchHint = ss.searchHint;
//        this.spinnerView = ss.spinnerView;
//        this.textView = ss.textView;
//        super.onRestoreInstanceState(ss.getSuperState());
//
//        //end
//    }
//
//    static class SavedState extends BaseSavedState {
//        String defText;
//        TextView textView;
//        Switch aSwitch;
//        MultiSpinner spinnerView;
//        String searchHint;
//
//        SavedState(Parcelable superState) {
//            super(superState);
//        }
//
//        public SavedState(Parcel in) {
//            super(in);
////            this.selected = in.readArrayList(ArrayList.class.getClassLoader());
//
//        }
//
//        @Override
//        public void writeToParcel(Parcel out, int flags) {
//            super.writeToParcel(out, flags);
////            out.writeList(this.selected);
//        }
//
//        //required field that makes Parcelables from a Parcel
//        public static final Parcelable.Creator<SavedState> CREATOR =
//                new Parcelable.Creator<SavedState>() {
//                    public SavedState createFromParcel(Parcel in) {
//                        return new SavedState(in);
//                    }
//
//                    public SavedState[] newArray(int size) {
//                        return new SavedState[size];
//                    }
//                };
//    }

}
