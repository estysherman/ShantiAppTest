package webit.android.shanti.customViews;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.KeyValue;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;

/**
 * Created by AndroIT202 on 15/02/2015.
 */

public class CustomViewsInitializer {

    private Context context;
    private int counter;
    private ICallBackLoadFinish callBack;

    public static HashMap<String, ArrayList<CodeValue>> codeTables = new HashMap<>();
	public static HashMap<String, ArrayList<KeyValue>> codeTables1 = new HashMap<>();

    public CustomViewsInitializer(Context context) {
        this.context = context;
    }

    public CustomViewsInitializer(Context context, int count, ICallBackLoadFinish callBackLoadFinish) {
        this.context = context;
        counter = count;
        callBack = callBackLoadFinish;
    }

    private void openEndCloseKeyboardToFixABug(final TextView textView) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);


        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
    }

    public void getCodeTable(final String tableId, final ICallBackCustomViewsData backFunction) {

        boolean b = false;
        for (int i = 0; i < codeTables.size() && !b; i++) {
            if (codeTables.get(tableId) != null) {
                backFunction.doCallBack(codeTables.get(tableId));
                checkIfCallCallback();
                b = true;
            }
        }

        if (!b) {

            new GeneralTask(context, new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    if (result != null && !result.equals("")) {
                        Type programsListType = new TypeToken<List<CodeValue>>() {
                        }.getType();
                        ArrayList<CodeValue> codeValues = new Gson().fromJson(result, programsListType);
                        backFunction.doCallBack(codeValues);
                        checkIfCallCallback();
                        codeTables.put(tableId, codeValues);
                    }
                    checkIfCallCallback();
                }
//            }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), CodeValue.getJsonToSent(Integer.parseInt(tableId)), ConnectionUtil.GetCodeTable);
            }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(),
                    "{\"TableId\":" + tableId + ",\"nvLanguage\":\"" + Utils.getDefaultLocale() + "\"}", ConnectionUtil.GetCodeTable);
        }
    }

    public void getCodeTableForLanguage(final String tableId, final ICallBackCustomViewsData1 backFunction) {

        boolean b = false;
        for (int i = 0; i < codeTables1.size() && !b; i++) {
            if (codeTables1.get(tableId) != null) {
                backFunction.doCallBack(codeTables1.get(tableId));
                checkIfCallCallback();
                b = true;
            }
        }

        if (!b) {

            new GeneralTask(context, new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    if (result != null && !result.equals("")) {
                        Type programsListType = new TypeToken<List<KeyValue>>() {
                        }.getType();
                        ArrayList<KeyValue> codeValues = new Gson().fromJson(result, programsListType);
                        backFunction.doCallBack(codeValues);
                        checkIfCallCallback();
                        codeTables1.put(tableId, codeValues);
                    }
                    checkIfCallCallback();
                }
//            }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), CodeValue.getJsonToSent(Integer.parseInt(tableId)), ConnectionUtil.GetCodeTable);
            }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(),
                    "{\"nvLanguage\":\"" + Utils.language + "\"}", ConnectionUtil.GetLanguagesCodeTable);
        }
    }

    public void setDatePicker(View datePicker, final TextView textDatePicker, final String defultValue) {
        if (!defultValue.equals("")) {
            textDatePicker.setText(defultValue + "");
            textDatePicker.setTag(defultValue);
        }
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                if (!Common.user.getNvBirthDate().equals(""))
                    try {
                        calendar.setTime(sdf.parse(Common.user.getNvBirthDate()));// all done
                    } catch (ParseException e) {
                        calendar.setTime(new Date());
                        e.printStackTrace();
                    }
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month++;
                        String sDay = day < 10 ? "0" + day : day + "";
                        String sMonth = month < 10 ? "0" + month : month + "";
                        final String date = sDay + "/" + sMonth + "/" + year;
                        Date selectedDate = null;

                        try {
                            DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            selectedDate = format.parse(date);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        final Date finalSelectedDate = selectedDate;
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textDatePicker.setTextColor(context.getResources().getColor(R.color.black));
                                if (finalSelectedDate.after(new Date())) {
                                    textDatePicker.setText(context.getString(R.string.dateBeforeToday) + "");
                                } else {
                                    textDatePicker.setText(date + "");
                                }
                                textDatePicker.setTag(date + "");
                            }
                        });
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }

    public void setAutoComplete(final AutoCompleteTextView complete, String codeTable) {

        getCodeTable(codeTable, new ICallBackCustomViewsData() {
            @Override
            public void doCallBack(ArrayList<CodeValue> codeValues) {
                complete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b)
                            ((AutoCompleteTextView) view).showDropDown();
                    }
                });
                ArrayAdapter<CodeValue> arrayAdapter = new ArrayAdapter<>(context, R.layout.profile_row, codeValues);
                complete.setAdapter(arrayAdapter);
            }
        });
    }

    public static int getAutoCompleteTextViewValue(String text, String codeTable) {
        for (CodeValue codeValue : codeTables.get(codeTable)) {
            if (codeValue.getNvValue().equals(text))
                return codeValue.getiKeyId();
        }
        return -1;
    }

    public void setSpinner(final MultiSpinner spinner, String codeTable, final String title, final int selectedKey) {
        ArrayList<Integer> codeValues = new ArrayList<>();
        codeValues.add(selectedKey);
        setSpinner(spinner, codeTable, title, codeValues);
    }


    public void setSpinner(final MultiSpinner spinner, final String codeTable, final String title, final List<Integer> selected) {

        if (codeTable.equals(CodeValue.countries)) {
            getPhonePrefix(codeTable, new ICallBackCustomViewsData() {
                @Override
                public void doCallBack(ArrayList<CodeValue> codeValues) {
                    spinner.setItems(codeValues, title, selected, codeTable);
                }
            });
        } else {
            if (codeTable.equals(CodeValue.countries_name)) {
                getCountriesName(new ICallBackCustomViewsData() {
                    @Override
                    public void doCallBack(ArrayList<CodeValue> codeValues) {
                        spinner.setItems(codeValues, title, selected, codeTable);
                    }
                });

            } else {
                if (codeTable.equals(CodeValue.language)) {
                    getCodeTableForLanguage(codeTable, new ICallBackCustomViewsData1() {
                        @Override
                        public void doCallBack(ArrayList<KeyValue> codeValues) {
                            spinner.setItemsOfLanduages(codeValues, title, selected, codeTable);
                        }
                    });
                } else {
                    getCodeTable(codeTable, new ICallBackCustomViewsData() {
                        @Override
                        public void doCallBack(ArrayList<CodeValue> codeValues) {
                            spinner.setItems(codeValues, title, selected, codeTable);
                        }
                    });
                }
            }
        }
    }


    private void getCountriesName(final ICallBackCustomViewsData iCallBackCustomViewsData) {
        boolean b = false;
        for (int i = 0; i < codeTables.size() && !b; i++) {
            if (codeTables.get(CodeValue.countries) != null) {
                iCallBackCustomViewsData.doCallBack(codeTables.get(CodeValue.countries));
                checkIfCallCallback();
                b = true;
            }
        }
        if (!b) {
            new GeneralTask(context, new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    if (result != null && !result.equals("")) {
                        Type programsListType = new TypeToken<List<CodeValue>>() {
                        }.getType();
                        ArrayList<CodeValue> codeValues = new Gson().fromJson(result, programsListType);
                        iCallBackCustomViewsData.doCallBack(codeValues);
                        checkIfCallCallback();
                        codeTables.put(CodeValue.countries, codeValues);
                    }
                }
//            }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), CodeValue.getJsonToSent(Integer.parseInt(tableId)), ConnectionUtil.GetCodeTable);
            }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(),
                    "{\"TableId\":" + CodeValue.countries + ",\"nvLanguage\":\"" + Utils.getDefaultLocale() + "\"}", ConnectionUtil.GetCodeTableParam);
        }
    }

    public void setSwitchView(final SwitchView switchView, String codeTable, final String title, int selectedKey) {
        ArrayList<Integer> codeValues = new ArrayList<>();
        codeValues.add(selectedKey);
        setSwitchView(switchView, codeTable, title, codeValues);
    }

    public void setSwitchView(final SwitchView switchView, final String codeTable, final String title, final List<Integer> selected) {
        if (codeTable.equals(CodeValue.countries)) {
            getPhonePrefix(codeTable, new ICallBackCustomViewsData() {
                @Override
                public void doCallBack(ArrayList<CodeValue> codeValues) {
                    switchView.setSpinnerItems(codeValues, codeTable, title, selected);

                }
            });
        } else if (codeTable.equals(CodeValue.countries_name)) {
            getCountriesName(new ICallBackCustomViewsData() {
                @Override
                public void doCallBack(ArrayList<CodeValue> codeValues) {
                    switchView.setSpinnerItems(codeValues, codeTable, title, selected);
                }
            });


        } else {
            getCodeTable(codeTable, new ICallBackCustomViewsData() {
                @Override
                public void doCallBack(ArrayList<CodeValue> codeValues) {
                    switchView.setSpinnerItems(codeValues, codeTable, title, selected);
                }
            });
        }
    }

    private void getPhonePrefix(final String codeTable, final ICallBackCustomViewsData iCallBackCustomViewsData) {
        boolean b = false;
        for (int i = 0; i < codeTables.size() && !b; i++) {
            if (codeTables.get(codeTable) != null) {
                iCallBackCustomViewsData.doCallBack(codeTables.get(codeTable));
                checkIfCallCallback();
                b = true;
            }
        }

        if (!b) {
            new GeneralTask(context, new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    if (result != null && !result.equals("")) {
                        Type programsListType = new TypeToken<List<CodeValue>>() {
                        }.getType();
                        ArrayList<CodeValue> codeValues = new Gson().fromJson(result, programsListType);
                        iCallBackCustomViewsData.doCallBack(codeValues);
                        checkIfCallCallback();
                        codeTables.put(codeTable, codeValues);
                    }
                }
//            }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), CodeValue.getJsonToSent(Integer.parseInt(tableId)), ConnectionUtil.GetCodeTable);
            }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(),
                    "{\"TableId\":" + codeTable + ",\"nvLanguage\":\"" + Utils.getDefaultLocale() + "\"}", ConnectionUtil.GetCodeTableParam);
        }
    }

    private void checkIfCallCallback() {

        if (counter == 1)
            callBack.doCallBack();
        counter--;
    }


}
