package webit.android.shanti.customViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by AndroIT on 01/02/2015.
 */
public class SpinnerAdapter extends ArrayAdapter<String>{
    Context context;
    String text="";
    int res;
    public SpinnerAdapter(Context context,int res,String[] text){
        super(context,0,text);
        this.context=context;
        this.text=text[0];
        this.res=res;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater= (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView= (TextView) layoutInflater.inflate(res, parent, false);
        textView.setText(text);
        return textView;
    }
}
