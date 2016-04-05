package webit.android.shanti.customViews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import webit.android.shanti.R;

import java.util.Locale;


public class CustomAutoComplete extends AutoCompleteTextView {
    private String fontName;

    public CustomAutoComplete(Context context) {
        super(context);
        init(null, context);
    }

    public CustomAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public CustomAutoComplete(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, context);
    }

    private void init(AttributeSet attrs, Context context) {

        String fontFileName = null;
        TypedArray a = null;
        try {
            if (attrs != null) {
                a = context.obtainStyledAttributes(attrs,
                        R.styleable.CustomFont);
                fontFileName = a.getString(R.styleable.CustomFont_fontName);
            }
            if (fontFileName == null)
                fontFileName = Locale.getDefault().getLanguage() + ".ttf";
            if (fontFileName != null) {
                Typeface myTypeface = Typeface.createFromAsset(context
                        .getAssets(), "fonts/" + fontFileName);
                setTypeface(myTypeface);

            }
            if (a != null)
                a.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
}
