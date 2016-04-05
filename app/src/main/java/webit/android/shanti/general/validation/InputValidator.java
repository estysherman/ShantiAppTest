package webit.android.shanti.general.validation;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by AndroIT202 on 04/02/2015.
 */
public class InputValidator implements TextWatcher, View.OnFocusChangeListener {

    private String pattern;
    private String error;
    private EditText editText;
    private boolean loastFocus = true;
    private Context mContext;

    public static List<EditText> collection = new ArrayList<EditText>();//רשימת שדות להזכרת הסיסמה

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public boolean isLoastFocus() {
        return loastFocus;
    }

    public void setLoastFocus(boolean loastFocus) {
        this.loastFocus = loastFocus;
    }

    public static List<EditText> getCollection() {
        return collection;
    }

    public static void setCollection(List<EditText> collection) {
        InputValidator.collection = collection;
    }

    public InputValidator(String pattern, EditText editText, String error) {
        this.pattern = pattern;
        this.error = error;
        this.editText = editText;
        collection.add(this.editText);
    }
    public InputValidator(String pattern, EditText editText, String error, Context context) {
        this.pattern = pattern;
        this.error = error;
        this.editText = editText;
        this.mContext = context;
        collection.add(this.editText);
    }
//מציב הודעת ש
    public static boolean isValidForm(String error) {
        boolean b = true;
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getText().toString().equals(null) || collection.get(i).getText().toString().equals(""))
                collection.get(i).setError(error);
            if (collection.get(i).getError() != null)
                b = false;
        }
        return b;
    }

    @Override

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (s.equals(""))
            this.loastFocus = false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // Pattern pattern = Pattern.compile(this.pattern);
        // Matcher matcher = pattern.matcher(s.toString());
        if (loastFocus && !isValid(s.toString()))
            editText.setError(error);
        else editText.setError(null);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            this.loastFocus = true;
            //((EditText) v).getText()
            if (!isValid(editText.getText().toString()))
                editText.setError(error);
            else editText.setError(null);
        }
    }


    private boolean isValid(String s) {
        Pattern pattern = Pattern.compile(this.pattern);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }
}