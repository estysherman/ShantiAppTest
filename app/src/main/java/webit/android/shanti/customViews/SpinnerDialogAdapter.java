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
public class SpinnerDialogAdapter extends ArrayAdapter<CodeValue> implements Filterable {


    Context context;
    List<CodeValue> orginalCodeValues;
    List<CodeValue> filterdCodeValues;
    private String mCodeTable;
    //boolean[] selected;
    HashMap<Integer, Boolean> selected = new HashMap<>();
    int res;
    private ItemFilter mFilter = new ItemFilter();

    public SpinnerDialogAdapter(Context context, int res, List<CodeValue> codeValues, HashMap<Integer, Boolean> selected,String codeTable) {
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
    public CodeValue getItem(int i) {
        for (CodeValue codeValue : filterdCodeValues) {
            if (codeValue.getiKeyId() == i)
                return codeValue;
        }
        //return filterdCodeValues.get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return filterdCodeValues.get(i).getiKeyId();
        //return i;
    }

    public long getOrginalId(int i) {
        //return orginalCodeValues.get(i).getiKeyId();
        //return orginalCodeValues.indexOf(filterdCodeValues.get(i));
        int pos = 0;
        for (CodeValue codeValue : filterdCodeValues) {
            if (pos == i)
                return codeValue.getiKeyId();
            pos++;
        }
        return 0;
        //return filterdCodeValues.
    }

    public void setSelected(HashMap<Integer, Boolean> selected) {
        this.selected = selected;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        i = filterdCodeValues.get(i).getiKeyId();
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
        if (selected.get(i)) {//מוסיף/מסיר V לפי הבחירה
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

            final List<CodeValue> list = orginalCodeValues;//נרשימה המלאה ללא סינון

            int count = list.size();//מספר הפריטים ברשימה
            final ArrayList<CodeValue> nlist = new ArrayList<CodeValue>(count);

            CodeValue filterableString;

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
            filterdCodeValues = (ArrayList<CodeValue>) results.values;//רשימת תוצאות חיפוש
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
