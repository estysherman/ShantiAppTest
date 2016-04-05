package webit.android.shanti.customViews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import webit.android.shanti.R;

import java.util.Locale;


public class CustomTextView extends TextView {
    private String fontName;
    private AttributeSet attrs;
    private Context context;
    //private Typeface typeface;


    public CustomTextView(Context context) {
        super(context);
        this.context = context;
        //setTypeface(typeface, Typeface.BOLD);
        init(null, context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        //setTypeface(typeface, Typeface.BOLD);
        init(attrs, context);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        //setTypeface(typeface, Typeface.BOLD);
        init(attrs, context);
    }

//    public void setTypeface(Typeface tf, int style) {
//        if (style == Typeface.BOLD) {
//            super.setTypeface(TypeFaces.getTypeFace(getContext(),
//                    "fonts/simpleclm-medium-webfont.ttf"));
//        } else if (style == Typeface.ITALIC) {
//            super.setTypeface(TypeFaces.getTypeFace(getContext(),
//                    "fonts/simpleclm-medium-webfont.ttf"));
//        } else {
//            super.setTypeface(TypeFaces.getTypeFace(getContext(),
//                    "fonts/simpleclm-medium-webfont.ttf"));
//        }
//    }

//    private void init(AttributeSet attrs, Context context) {
////        if (!isInEditMode())
////            return;
//        String fontFileName = null;
//        TypedArray a = null;
//        try {
//            if (attrs != null) {
//                a = context.obtainStyledAttributes(attrs,
//                        R.styleable.CustomFont);
//                fontFileName = a.getString(R.styleable.CustomFont_fontName);
//            }
//            if (fontFileName == null)
//                fontFileName = Locale.getDefault().getLanguage() + ".ttf";
//            if (fontFileName != null) {
//                Typeface myTypeface = Typeface.createFromAsset(context
//                        .getAssets(), "fonts/" + fontFileName);
//                setTypeface(myTypeface);
//
//            }
////            if(a.getString(R.styleable.CustomTextView_fontStyle).equals("normal")){
////                if (!isInEditMode()) {
////                    this.setTypeface(Typeface.createFromAsset(context.getAssets(), "SpacerBold.TTF"));
////                }
////            }
////            else {
//            if(a.getInt(R.styleable.CustomFont_fontStyle,0)==1){
//                if (!isInEditMode()) {
//                        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/" + "Roboto-Thin.ttf"));
//                    }
//                }
//
//            if (a != null)
//                a.recycle();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
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
        }
        if (a.getInt(R.styleable.CustomFont_fontStyle, 0) == 4) {
            if (!isInEditMode()) {
                //this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + "Roboto-Regular.ttf"));
                this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf"));
            }
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
        changeFont();
    }

    private void changeFont() {
        if (this.fontName != null) try {
            Typeface myTypeface = Typeface.createFromAsset(context
                    .getAssets(), "fonts/" + this.fontName);
            setTypeface(myTypeface);
        } catch (Exception e) {
        }
    }
}
