package webit.android.shanti.login.signin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import webit.android.shanti.R;
import webit.android.shanti.entities.CodeValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndroIT on 27/01/2015.
 */

//בינתיים לא ראינו שמשתמשים בו
public class ProfileAdapter extends BaseAdapter implements Filterable{

    List<CodeValue> codeValues;
    int resource;
    Context context;
    public ProfileAdapter(Context context, int resource, List<CodeValue> codeValues) {
         this.codeValues=codeValues;
        this.resource=resource;
        this.context=context;
        if(codeValues==null){
            this.codeValues=new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return codeValues.size()+1;
    }

    @Override
    public CodeValue getItem(int i) {
        if(i==0){
            return new CodeValue(-1,context.getString(resource));
        }
        return codeValues.get(i-1);
    }

    @Override
    public long getItemId(int i) {
        if(i==0){
            return -1;
        }
        return codeValues.get(i-1).getiKeyId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(  R.layout.profile_row,parent, false);
        }

        TextView textView= (TextView) convertView;
        textView.setText(getItem(position).getNvValue());
        textView.setTag(getItem(position).getiKeyId());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
