package webit.android.shanti.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import webit.android.shanti.R;
import java.util.Locale;


public class CustomButton extends Button {
    private String fontName;

    public CustomButton(Context context) {
        super(context);
        init(null, context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
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
                //Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontFileName);
                Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/simpleclm-medium-webfont.ttf");
                setTypeface(myTypeface);

            }
            if(a.getInt(R.styleable.CustomFont_fontStyle,0)==1){
                if (!isInEditMode()) {
                    //this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + "Roboto-Thin.ttf"));
                    this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/simpleclm-medium-webfont.ttf"));
                }
            }
            if(a.getInt(R.styleable.CustomFont_fontStyle,0)==4){
                if (!isInEditMode()) {
                    //this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + "Roboto-Thin.ttf"));
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
    }
}
