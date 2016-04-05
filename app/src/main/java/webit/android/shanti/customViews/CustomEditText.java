package webit.android.shanti.customViews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.Locale;

import webit.android.shanti.R;

public class CustomEditText extends EditText {
    private String fontName;

    public CustomEditText(Context context) {
        super(context);
        init(null, context);
        //Typeface myTypeface = Typeface.createFromAsset(context
        //        .getAssets(), "fonts/" + fontName);
        //setTypeface(myTypeface);
    }


    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, context);
    }

    //    private void init(AttributeSet attrs, Context context) {
////        if (!isInEditMode())
////            return;
//        String fontFileName = null;
//        TypedArray a = null, b = null;
//        boolean withoutPadding = false;
//        try {
//            if (attrs != null) {
//                a = context.obtainStyledAttributes(attrs,
//                        R.styleable.CustomFont);
//                fontFileName = a.getString(R.styleable.CustomFont_fontName);
//                b = context.obtainStyledAttributes(attrs,
//                        R.styleable.CustomEditText);
//                withoutPadding = b.getBoolean(R.styleable.CustomEditText_withoutPadding, false);
//            }
//            if (fontFileName == null)
////                fontFileName = Locale.getDefault().getLanguage() + ".ttf";
//                fontFileName = "Roboto-Regular.ttf";
//            if (fontFileName != null) {
//                Typeface myTypeface = Typeface.createFromAsset(context
//                        .getAssets(), "fonts/" + fontFileName);
//                setTypeface(myTypeface);
//
//            }
//            if (!withoutPadding) {
//                setPadding(
//                        (int) getResources().getDimension(R.dimen.edit_text_padding),
//                        (int) getResources().getDimension(R.dimen.edit_text_paddingTop),
//                        (int) getResources().getDimension(R.dimen.edit_text_padding),
//                        (int) getResources().getDimension(R.dimen.edit_text_paddingBottom));
//            }
//            if(a.getInt(R.styleable.CustomFont_fontStyle,0)==1){
//                if (!isInEditMode()) {
//                    this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/" + "Roboto-Thin.ttf"));
//                }
//            }
//            if (a != null)
//                a.recycle();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    private void init(AttributeSet attrs, Context context) {
        String fontFileName = null;
        boolean withoutPadding = false;
        TypedArray a = null, b = null;
        ;
        try {
            b = context.obtainStyledAttributes(attrs,
                    R.styleable.CustomEditText);
            withoutPadding = b.getBoolean(R.styleable.CustomEditText_withoutPadding, false);
            if (attrs != null) {
                a = context.obtainStyledAttributes(attrs,
                        R.styleable.CustomFont);
                fontFileName = a.getString(R.styleable.CustomFont_fontName);
            }
            if (fontFileName == null)
                fontFileName = Locale.getDefault().getLanguage() + ".ttf";
            if (fontFileName != null) {

                //Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontFileName);
                Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/simpleclm-medium-webfont.ttf");
                setTypeface(myTypeface);

            }
            if (a.getInt(R.styleable.CustomFont_fontStyle, 0) == 1) {
                if (!isInEditMode()) {
                    //this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + "Roboto-Thin.ttf"));
                    this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/simpleclm-medium-webfont.ttf"));
                }
            }
            if (a.getInt(R.styleable.CustomFont_fontStyle, 0) == 2) {
                if (!isInEditMode()) {
                    //this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + "Roboto-Light.ttf"));
                    this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/simpleclm-medium-webfont.ttf"));
                }
            }
            if (a.getInt(R.styleable.CustomFont_fontStyle, 0) == 3) {
                if (!isInEditMode()) {
                    //this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + "Roboto-Regular.ttf"));
                    this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/simpleclm-medium-webfont.ttf"));
                }
                if (!withoutPadding) {
                    setPadding(
                            (int) getResources().getDimension(R.dimen.edit_text_padding),
                            (int) getResources().getDimension(R.dimen.edit_text_paddingTop),
                            (int) getResources().getDimension(R.dimen.edit_text_padding),
                            (int) getResources().getDimension(R.dimen.edit_text_paddingBottom));
                }
                if (a.getInt(R.styleable.CustomFont_fontStyle, 0) == 1) {
                    if (!isInEditMode()) {
                        //this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + "Roboto-Thin.ttf"));
                        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/simpleclm-medium-webfont.ttf"));
                    }
                }
                if (a.getInt(R.styleable.CustomFont_fontStyle, 0) == 4) {
                    if (!isInEditMode()) {
                        //this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + "Roboto-Thin.ttf"));
                        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf"));
                    }
                }
                if (a != null)
                    a.recycle();
            }
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
