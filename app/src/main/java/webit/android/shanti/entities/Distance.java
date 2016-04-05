package webit.android.shanti.entities;

/**
 * Created by crm on 20/08/2015.
 */
public class Distance {
    private String text;
    private int value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Distance(String text, int value) {
        this.text = text;
        this.value = value;
    }
}
