package webit.android.shanti.customViews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.internal.ChannelSendFileResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import webit.android.shanti.R;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.KeyValue;
import webit.android.shanti.login.signin.BaseInfoFragment;

/**
 * Created by AndroIT on 01/02/2015.
 */
public class MultiSpinner extends Spinner implements Serializable {

    public interface clickCallBack {
        public void doCallBack(HashMap<Integer, Boolean> selected);
    }

    public enum ESpinnerType {
        Multi,//אפשר לבחור מספר ערכים לדוגמא מספר שפות
        Single//אפשר לבחור ערך אחד לדוגמא מגדר
    }

    public enum ESpinnerStyle {
        Simple, SearchDef
    }

    public ESpinnerType geteSpinnerType() {
        return eSpinnerType;
    }

    public void seteSpinnerType(ESpinnerType eSpinnerType) {
        this.eSpinnerType = eSpinnerType;
    }

    public ESpinnerStyle geteSpinnerStyle() {
        return eSpinnerStyle;
    }

    public void seteSpinnerStyle(ESpinnerStyle eSpinnerStyle) {
        this.eSpinnerStyle = eSpinnerStyle;

    }

    public String getSearchHint() {
        return searchHint;
    }

    public void setSearchHint(String searchHint) {
        this.searchHint = searchHint;
    }

    private ESpinnerType eSpinnerType = ESpinnerType.Single;
    ESpinnerStyle eSpinnerStyle = ESpinnerStyle.Simple;
    private List<CodeValue> items = new ArrayList<>();
    private List<KeyValue> items1 = new ArrayList<>();
    public CodeValue mCountry;
    public HashMap<Integer, Boolean> selected = new HashMap<>();
    public HashMap<String, Boolean> selected1 = new HashMap<>();
    private String defaultText;
    public int oldSelectedKey = -1;
    public String oldSelectedKey1 = "-1";
    SpinnerAdapter adapter;
    private String searchHint;
    public Dialog dialog;
    private clickCallBack clickCallBack;
    private String codeTable;
    SpinnerDialogAdapter spinnerDialogAdapter;
    SpinnerDialogAdapterLanguage spinnerDialogAdapterLanguage;
    public static List<KeyValue> Languages = new ArrayList<>();
    TextView textView;


    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        init(arg1);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        init(arg1);
    }

    public void setClickCallBack(MultiSpinner.clickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }
    //אתחול
    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.MultiSpinner, 0, 0);
        setSearchHint(a.getString(R.styleable.MultiSpinner_search_hint));//תיבת חיפוש
        int spinnerType = a.getInt(R.styleable.MultiSpinner_multi_spinner_type, 0);//סוג הספינר - יחיד או מרובה
        if (spinnerType == 0) {
            this.eSpinnerType = ESpinnerType.Single;
        } else {
            this.eSpinnerType = ESpinnerType.Multi;
        }
        a.recycle();
    }

    public void onClick(int which, boolean isChecked) {//בעת לחיצה על אחד מפרטי הרשימה בספינר
        if (isChecked)//אם בחור
            selected.put(which, true);//מוסיף לרשימת הבחורים בתוספת true
        else//אם לא בחור
            selected.put(which, false);//מוסיף לרשימת הבחורים בתוספת false
    }

    //שומר את הפריטים שנבחרו
    public void onCancel() {
        String spinnerText = "";
        if (getCodeTable() != null && getCodeTable().equals(CodeValue.countries)) {//אם זה רשימת מדינות
            for (CodeValue item : items) {//עובר על כל המדינות
                if (selected.get(item.getiKeyId())) {//אם המדינה נבחרה
                    spinnerText = "+" + item.getNvValueParam();//משרשר אותה ל spinnerText
                    mCountry = item;//שווה למדינה האחרונה שנבחרה
                    break;
                }
            }
        } else {
            StringBuffer spinnerBuffer = new StringBuffer();
            boolean someUnselected = false;
            //for (int i = 0; i < items.size(); i++) {
            if (getCodeTable() != null && getCodeTable().equals(CodeValue.language)) {
                for (KeyValue item : items1) {
                    if (selected1.get(item.getNvKey())) {//אם הפריט נבחר
                        spinnerBuffer.append(item.getNvValue());//משרשר את הפריט ל spinnerBuffer
                        spinnerBuffer.append(", ");
                        someUnselected = true;
                        Languages.add(new KeyValue(item.getNvKey(), item.getNvValue()));
                    }
                }
            } else {
                for (CodeValue item : items) {
                    if (selected.get(item.getiKeyId()) == true) {//אם הפריט נבחר
                        spinnerBuffer.append(item.getNvValue());//משרשר את הפריט ל spinnerBuffer
                        spinnerBuffer.append(", ");
                        someUnselected = true;

                    }

                }
            }
            if (someUnselected) {//אם נבחר פריט כלשהו
                spinnerText = spinnerBuffer.toString();
                if (spinnerText.length() > 2)
                    spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
            } else {
                spinnerText = defaultText;
                if (eSpinnerStyle == ESpinnerStyle.SearchDef) {
                    spinnerText = "";
                }

            }
        }

        int res = R.layout.profile_row;
        if (eSpinnerStyle == ESpinnerStyle.SearchDef) {
            res = R.layout.search_def_profit;
        }
//
//        customTextView.setText(spinnerText);
        adapter = new SpinnerAdapter(getContext(), res, new String[]{spinnerText});
        setAdapter(adapter);
//        CustomTextView customTextView = (CustomTextView) this.getAdapter().getView(0, inflate(getContext(),res,null), null).findViewById(R.id.profileInfoUseTermsTxt1);
//        customTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        //spinnerDialogAdapter.setSelected(selected);
    }

    public String getCodeTable() {
        return codeTable;
    }

    public void setCodeTable(String codeTable) {
        this.codeTable = codeTable;
    }

   String a;

    @Override
    public boolean performClick() {//בלחיצה על אחד מהמאפיינים
        dialog = new Dialog(getContext());
        dialog.setCancelable(true);
        if (MultiSpinner.this.getParent().getParent() instanceof SwitchView)//אם הספינר מסוג SwitchView
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    boolean isSelect = false;
                    if (eSpinnerType == ESpinnerType.Single) {
                        if (selected != null)
                            for (Map.Entry<Integer, Boolean> entry : selected.entrySet()) {//עובר על הרשימה של הפריטים שנבחרו
                                if (entry.getValue())//אם נבחר
                                    isSelect = true;
                            }
                        if (!isSelect)//אם לא נבחר כלום
                            ((SwitchView) MultiSpinner.this.getParent().getParent()).aSwitch.setChecked(false);//הופך את ה switch ל x
                    } else {
                        ((SwitchView) MultiSpinner.this.getParent().getParent()).aSwitch.setChecked(false);//הופך את ה switch ל v
                    }
                }

            });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.spinner_dialog);//פותח חלון ספינר
        ((TextView) dialog.findViewById(R.id.spinnerDialogTitle)).setText(defaultText);
       if (codeTable.equals("11"))
            spinnerDialogAdapterLanguage = new SpinnerDialogAdapterLanguage(getContext(), R.layout.spinner_dialog_row, items1, selected1, getCodeTable());
        else
            spinnerDialogAdapter = new SpinnerDialogAdapter(getContext(), R.layout.spinner_dialog_row, items, selected, getCodeTable());
        ListView listView = ((ListView) dialog.findViewById(R.id.spinnerDialogList));
        if (codeTable.equals("11"))
            listView.setAdapter(spinnerDialogAdapterLanguage);
        else
            listView.setAdapter(spinnerDialogAdapter);
        listView.setEmptyView(dialog.findViewById(R.id.spinnerDialogEmptyList));//אם אין פריטים ברשימה
        final EditText searchText = (EditText) dialog.findViewById(R.id.spinnerDialogSearch);
        if (this.searchHint == null)//אם תיבת החיפוש ריקה
            searchText.setVisibility(GONE);
        else searchText.setHint(searchHint);

        //מאזין לשינויים בתיבת טקסט
        searchText.addTextChangedListener(new TextWatcher() {//תיבת חיפוש
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //MainActivity.this.adapter.getFilter().filter(cs);
 if (MultiSpinner.this.defaultText.equals(getContext().getResources().getString(R.string.moreInfoLanguages)))
                    spinnerDialogAdapterLanguage.getFilter().filter(cs);//על כל שינוי מפעיל סינון
                else
                    spinnerDialogAdapter.getFilter().filter(cs);//על כל שינוי מפעיל סינון

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        //בלחיצה על פריט ברשימה
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//שינוי ערך
                //i = spinnerDialogAdapter.getOrginalId(i);
                //i = items.get(i).getiKeyId();
                if (MultiSpinner.this.defaultText.equals(getContext().getResources().getString(R.string.moreInfoLanguages)))
                    a = spinnerDialogAdapterLanguage.getOrginalId(i);
                else
                    i = (int) spinnerDialogAdapter.getOrginalId(i);

                if (eSpinnerType == ESpinnerType.Single) {//אם הספינר מסוג single
                    if (oldSelectedKey != -1)//האם נבחר מגדר
                        selected.put(oldSelectedKey, false);//הפיכת הערך הקודם שנבחר לfalse
                    if (oldSelectedKey != i) {//אם לא לוחצים על אותו ערך
                        selected.put(i, true);//שם vעל הבחירה החדשה
                        oldSelectedKey = i;//הבחירה החדשה
                    } else oldSelectedKey = -1;//הערך הוא לא מוגדר
                    onCancel();//שומר את הפריטים שנבחרו
                    spinnerDialogAdapter.notifyDataSetChanged();//שמירת השינויים בספינר
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            if (dialog != null && dialog.isShowing())
//                                dialog.dismiss();
                        }
                    }, 200);

                } else {///אם זה מסוג multi
                    if (MultiSpinner.this.defaultText.equals(getContext().getResources().getString(R.string.moreInfoLanguages)))//אם הספינר הוא של שפות מדוברות{
                    {
                        if (!selected1.get(a))//אם הפריט לא בחור
                            if (SelectUpToNumItems(3))//אם נבחרו כבר 3 שפות
                                return;
                        //אם אין 3 בחירות
                        selected1.put(a, !selected1.get(a));//מוסיף לרשימת הבחורים ומשנה את true ל false ולהפך
                        if (selected1.get(a))//אם הפריט בחור
                            view.findViewById(R.id.spinnerDialogIcon).setBackgroundResource(R.drawable.v_select);//סמן V
                        else
                            view.findViewById(R.id.spinnerDialogIcon).setBackgroundResource(R.drawable.empty);//תיבה ריקה
                        spinnerDialogAdapterLanguage.setSelected(selected1);//שליחת הרשימה
                    } else {
                        if (!(getCodeTable().equals(CodeValue.countries))) {//אם הספינר לא של מדינות
                            if (!selected.get(i))//אם הפריט בחור
                                view.findViewById(R.id.spinnerDialogIcon).setBackgroundResource(R.drawable.v_select);//סמן V
                            else
                                view.findViewById(R.id.spinnerDialogIcon).setBackgroundResource(R.drawable.empty);//תיבה ריקה
                            selected.put(i, !selected.get(i));
                        }
                    }
                    if (!(getCodeTable().equals(CodeValue.language)))
                        spinnerDialogAdapter.setSelected(selected);//שליחת הרשימה
                }
                //  }
                if (eSpinnerType == ESpinnerType.Single && clickCallBack != null)
                    clickCallBack.doCallBack(selected);
            }
        });
//        if (eSpinnerType == ESpinnerType.Multi) {

        Button button = (Button) dialog.findViewById(R.id.spinnerDialogOkBtn);//כפתור אישור
        button.setText(getContext().getString(R.string.userPrefOk));
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancel();//שומר את הפריטים שנבחרו
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();//סוגר את ה dialog
                if (MultiSpinner.this.getParent().getParent() instanceof SwitchView && isEmptySelected())//אם לא נבחר שום ערך
                    ((SwitchView) MultiSpinner.this.getParent().getParent()).aSwitch.setChecked(false);//הפוך ל x
                if (clickCallBack != null)
                    clickCallBack.doCallBack(selected);
            }
        });
//        }
//        if (this.items.size() == 0) {
////            Button button = (Button) dialog.findViewById(R.id.spinnerDialogOkBtn);
//            button.setText(getContext().getString(R.string.userPrefOk));
//            button.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onCancel();
//                    if (dialog != null && dialog.isShowing())
//                        dialog.dismiss();
//                }
//            });
//        }
        if (!((Activity) getContext()).isFinishing()) {//אם האקטיביטי לא נסגר
            dialog.show();//הצג הודעה
        }
        return true;

//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        CharSequence[] itemsCharSequence = getItemsAsCharSequenceArray();
//        builder.setMultiChoiceItems(itemsCharSequence, selected, this);
//        builder.setPositiveButton(android.R.string.ok,
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        builder.setOnCancelListener(this);
//        builder.show();
//        return true;
    }

    //מנקה את הבחירות בשביל הפעם הבאה
    private void clearSelected() {
        for (Map.Entry<Integer, Boolean> entry : selected.entrySet()) {
            if (entry.getValue())
                selected.put(entry.getKey(), false);
        }
        spinnerDialogAdapter.notifyDataSetChanged();
    }

    //בודק אם בחרו יותר מ - 3 שפות
    private boolean SelectUpToNumItems(int i) {
        int count = 0;
        for (Map.Entry<String, Boolean> entry : selected1.entrySet()) {
            if (entry.getValue())
                count++;//ספירת פריטים
        }
        if (count == i) {
//            for (Map.Entry<Integer, Boolean> entryIn : selected.entrySet()) {
//                if (entryIn.getValue()) {
//                    entryIn.setValue(false);
            return true;//אם בחר 3 שפות - true
        }
        return false;//אם לא בחר 3 שפות - false
    }


    private CharSequence[] getItemsAsCharSequenceArray() {
        CharSequence[] itemsCharSequence = new CharSequence[items.size()];
        int i = 0;
        for (Iterator<CodeValue> iterator = items.iterator(); iterator.hasNext(); ) {
            CodeValue next = iterator.next();
            itemsCharSequence[i] = next.getNvValue();
            i++;
        }
        return itemsCharSequence;
    }

    public void setItems(List<CodeValue> items, String allText, List<Integer> selected, String codeTable) {
        this.setCodeTable(codeTable);
        this.items = items;
        this.defaultText = allText;
        this.selected = new HashMap<>();

        for (CodeValue item : items) {
            this.selected.put(item.getiKeyId(), false);
            String s = "+" + item.getNvValueParam();
            if (s.equals(BaseInfoFragment.mPhonePerfix))//אם מוצא את הקידומת ברשימה שלו
                this.selected.put(item.getiKeyId(), true);//ומאתחל אותה כבחורה
        }

        if (selected != null && selected.size() > 0 && selected.get(0) != -1) {
            //put default value if no exist
            if (selected.get(0) == -2) {
                this.selected.put(items.get(0).getiKeyId(), true);
                oldSelectedKey = items.get(0).getiKeyId();
            }
            if (codeTable.equals(CodeValue.gender) && ((selected.get(0) == -3) || (selected.get(0) == -4))) {
                if (selected.get(0) == -3) {
                    this.selected.put(items.get(0).getiKeyId(), true);
                    oldSelectedKey = items.get(0).getiKeyId();
                }
                if (selected.get(0) == -4) {
                    this.selected.put(items.get(1).getiKeyId(), true);
                    oldSelectedKey = items.get(1).getiKeyId();
                }
            } else {
                for (Integer key : selected) {
                    this.selected.put(key, true);
                }
                if (eSpinnerType == eSpinnerType.Single)
                    oldSelectedKey = selected.get(0);
                else oldSelectedKey = -1;
            }
        }
        //selected[i] = false;

        int res = R.layout.profile_row;
        if (eSpinnerStyle == ESpinnerStyle.SearchDef) {
            res = R.layout.search_def_profit;
            allText = "";
        }

        adapter = new SpinnerAdapter(getContext(), res, new String[]{allText});
        setAdapter(adapter);
        onCancel();
        //spinnerDialogAdapter.setSelected(selected);
    }


    public void setItemsOfLanduages(List<KeyValue> items, String allText, List<Integer> selected, String codeTable) {
        this.setCodeTable(codeTable);
        this.items1 = items;
        this.defaultText = allText;
        this.selected1 = new HashMap<>();
        setSearchHint(getContext().getString(R.string.search));//תיבת חיפוש
        for (KeyValue item : items) {
            this.selected1.put(item.getNvKey(), false);
            String s = "+" + item.getNvValueParam();
            if (s.equals(BaseInfoFragment.mPhonePerfix))//אם מוצא את הקידומת ברשימה שלו
                this.selected1.put(item.getNvKey(), true);//ומאתחל אותה כבחורה
        }
        if (selected != null && selected.size() > 0 && selected.get(0) != -1) {
            //put default value if no exist
            if (selected.get(0) == -2) {
                this.selected1.put(items.get(0).getNvKey(), true);
                oldSelectedKey1 = items.get(0).getNvKey();
            }
            if (codeTable.equals(CodeValue.gender) && ((selected.get(0) == -3) || (selected.get(0) == -4))) {
                if (selected.get(0) == -3) {
                    this.selected1.put(items.get(0).getNvKey(), true);
                    oldSelectedKey1 = items.get(0).getNvKey();
                }
                if (selected.get(0) == -4) {
                    this.selected1.put(items.get(1).getNvKey(), true);
                    oldSelectedKey1 = items.get(1).getNvKey();
                }
            } else {
                for (Integer key : selected) {
                    this.selected.put(key, true);
                }
                if (eSpinnerType == eSpinnerType.Single)
                    oldSelectedKey = selected.get(0);
                else oldSelectedKey = -1;
            }
        }
        int res = R.layout.profile_row;
        if (eSpinnerStyle == ESpinnerStyle.SearchDef) {
            res = R.layout.search_def_profit;
            allText = "";
        }

        adapter = new SpinnerAdapter(getContext(), res, new String[]{allText});
        setAdapter(adapter);
        onCancel();
        //spinnerDialogAdapter.setSelected(selected);
    }


    //בונה מערך של הפריטים שנבחרו שמכיל את ה key של הפריטים
    public ArrayList<String> getChooseValues() {
        ArrayList<String> chooseValues = new ArrayList<>();
        if (items != null) {
            for (Map.Entry<Integer, Boolean> entry : selected.entrySet()) {
                if (entry.getValue())
                    chooseValues.add(entry.getKey().toString());
            }
            if (chooseValues.size() == 0)
                chooseValues.add("-1");
        } else chooseValues.add("-1");

        return chooseValues;
    }
//        if (items != null) {
//            for (int i = 0; i < items.size(); i++) {
//                if (selected.get(i)) {
//                    chooseValues.add(items.get(i).getiKeyId());
//                }
//            }
//            if (chooseValues.size() == 0)
//                chooseValues.add(-1);
//
//            //}
//        } else {
//            chooseValues.add(-1);
//        }
//        //spinnerDialogAdapter.setSelected(selected);
//        return chooseValues;


//    public void setChooseValues(ArrayList<CodeValue> selected) {
//
//        for (CodeValue codeValue : selected) {
//            this.selected.put(codeValue.getiKeyId(), true);
//            oldSelectedKey = codeValue.getiKeyId();
//        }
//        if (selected.size() > 1)
//            oldSelectedKey = -1;
//
//
//        onCancel();
//    }


    //public void setChooseValues(ArrayList<Integer> chooseValues) {
//        int t = 2;
//        for (int i = 0; i < items.size(); i++) {
//            for (int i1 = 0; i1 < chooseValues.size(); i1++)
//                if ((items.get(i).getiKeyId() == chooseValues.get(i1)))
//                    selected[i] = true;
//                else selected[i] = false;
//        }
    //   }

    //פתיחת spinner
    public void openSpinner() {
        performClick();
    }

    //סגירת spinner
    public void closeSpinner() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        if (MultiSpinner.this.getParent().getParent() instanceof SwitchView && isEmptySelected())//אם ה spinner מסוג switch וגם לא נבחר כלום
            ((SwitchView) MultiSpinner.this.getParent().getParent()).aSwitch.setChecked(false);
    }

    @Override
    public Parcelable onSaveInstanceState() {//שומר את הנתונים בעת שינוי ב SaveInstanceState
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        //end
        ss.dialog = this.dialog;
        ss.selected = this.selected;
        ss.items = this.items;
        ss.eSpinnerType = this.eSpinnerType;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {//שליפת נתונים מה SaveInstanceState
        //begin boilerplate code so parent classes can restore state
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        //end
        this.dialog = ss.dialog;
        this.selected = ss.selected;
        this.items = ss.items;
        this.eSpinnerType = ss.eSpinnerType;
        if (this.eSpinnerType == ESpinnerType.Multi)
            onCancel();

    }

    static class SavedState extends BaseSavedState {
        private ESpinnerType eSpinnerType = ESpinnerType.Single;
        HashMap<Integer, Boolean> selected = new HashMap<>();
        private List<CodeValue> items = new ArrayList<>();
        Dialog dialog;

        SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel in) {
            super(in);
            this.selected = in.readHashMap(HashMap.class.getClassLoader());
            this.items = in.readArrayList(List.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {//שמירת הנתונים
            super.writeToParcel(out, flags);
            out.writeMap(this.selected);
            out.writeList(this.items);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public CodeValue getmCountry() {
        return mCountry;
    }

    public void setmCountry(CodeValue mCountry) {
        this.mCountry = mCountry;
    }

    public void setError(String errorS) {
        if (errorS != null) {
            TextView errorText = (TextView) MultiSpinner.this.getSelectedView();
            if (errorText != null)
                errorText.setError(errorS);
        }
    }

    private boolean isEmptySelected() {
        for (Map.Entry<Integer, Boolean> entry : selected.entrySet()) {
            if (entry.getValue())
                return false;
        }
        return true;
    }

//    OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//
//        }
//    };
}