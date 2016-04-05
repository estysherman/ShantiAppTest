package webit.android.shanti.customViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.KeyValue;
import webit.android.shanti.login.signin.BaseInfoFragment;
import webit.android.shanti.login.signin.ProfileInfoFragment;
import webit.android.shanti.main.MainActivity;


/**
 * Created by AndroIT on 03/02/2015.
 */
//
//ניראות של כל שורה ב SPINNER
public class SpinnerDialogAdapterLanguage extends ArrayAdapter<KeyValue> implements Filterable {


    Context context;
    List<KeyValue> orginalCodeValues;
    List<KeyValue> filterdCodeValues;
    private String mCodeTable;
    //boolean[] selected;
    HashMap<String, Boolean> selected = new HashMap<>();

    int res;
    private ItemFilter mFilter = new ItemFilter();

    public SpinnerDialogAdapterLanguage(Context context, int res, List<KeyValue> codeValues, HashMap<String, Boolean> selected,String codeTable) {
        super(context, res, codeValues);
        this.context = context;
        this.orginalCodeValues = codeValues;
        this.filterdCodeValues = codeValues;
        this.mCodeTable = codeTable;
        this.res = res;
        this.selected = selected;
    }

    public void setSelected(int position) {
        //selected[position] = !selected[position];
    }

    @Override
    public int getCount() {
        return filterdCodeValues.size();
    }

    @Override
    public KeyValue getItem(int i) {

       /* for (KeyValue keyValue : filterdCodeValues) {
            if (keyValue.getNvKey() == i)*/
                return filterdCodeValues.get(i);
       // }
        //return filterdCodeValues.get(i);
        //return null;
    }

    @Override
    public long getItemId(int i) {
        return 1;
       // return filterdCodeValues.get(i).getNvKey();
        //return i;
    }

    public String getOrginalId(int i) {
        //return orginalCodeValues.get(i).getiKeyId();
        //return orginalCodeValues.indexOf(filterdCodeValues.get(i));
        int pos = 0;
        for (KeyValue keyValue : filterdCodeValues) {
            if (pos == i)
                return keyValue.getNvKey();
            pos++;
        }
        return "0";
        //return filterdCodeValues.
    }

    public void setSelected(HashMap<String, Boolean> selected) {
        this.selected = selected;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
       String a = filterdCodeValues.get(i).getNvKey();
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(res, viewGroup, false);
            holder = new ViewHolder();
            holder.text = ((TextView) view.findViewById(R.id.spinnerDialogTextRow));//טקסט שמופיע בכל שורה
            holder.textViewPhonePrefix = ((TextView) view.findViewById(R.id.spinnerDialogTextRowPhonePrefix));//בקידומת טלפון - המספר שמופיע ליד שם המדינה
            holder.vIcon = view.findViewById(R.id.spinnerDialogIcon);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.text.setText(getItem(i).getNvValue());
        if (mCodeTable!= null&& mCodeTable.equals(CodeValue.countries)) {//אם שולף מטבלת מדינות
            holder.textViewPhonePrefix.setVisibility(View.VISIBLE);//שיראה את מספרי קידומת הטלפון
            holder.textViewPhonePrefix.setText("+" + getItem(i).getNvValueParam());

        } else {//אם שולף מטבלה אחרת
            holder.textViewPhonePrefix.setVisibility(View.GONE);//שיסתיר את מספרי קידומת הטלפון

        }
        if (selected.get(i)!=null) {//מוסיף/מסיר V לפי הבחירה
            holder.vIcon.setBackgroundResource(R.drawable.v_select);
        } else holder.vIcon.setBackgroundResource(R.drawable.empty);
        return view;
    }

    private static class ViewHolder {
        TextView text;
        TextView textViewPhonePrefix;
        View vIcon;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    //סינון
    private class ItemFilter extends Filter {
        @Override//מבצע סינון  - ומחזיר תוצאות חיפוש
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();//התויים מהתיבת חיפוש - באותיות קטנות

            FilterResults results = new FilterResults();//תוצאות החיפוש

            final List<KeyValue> list = orginalCodeValues;//נרשימה המלאה ללא סינון

            int count = list.size();//מספר הפריטים ברשימה
            final ArrayList<KeyValue> nlist = new ArrayList<KeyValue>(count);

            KeyValue filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getNvValue().toLowerCase().startsWith(filterString)) {//אם האיבר מתחיל באות של הסינון
                    nlist.add(filterableString);//מוסיף אותו ל nlist
                }
            }

            //מעביר ל results את התוצאות
            results.values = nlist;
            results.count = nlist.size();

            return results;//מחזיר תוצאות חיפוש
        }

        @SuppressWarnings("unchecked")

        //שם ב - filterdCodeValues את תוצאות החיפוש
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterdCodeValues = (ArrayList<KeyValue>) results.values;//רשימת תוצאות חיפוש
            refreshSelectedAfterFilter();
            notifyDataSetChanged();
        }
    }

    private void refreshSelectedAfterFilter() {
//        for (CodeValue codeValue : filterdCodeValues) {
//            if(selecte)
//        }
    }
}
