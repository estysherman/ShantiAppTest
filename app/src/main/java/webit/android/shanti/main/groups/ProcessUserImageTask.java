package webit.android.shanti.main.groups;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by CRM on 25/02/2015.
 */

public class ProcessUserImageTask extends AsyncTask<String, String, String> {

    DoTask useTask;
    DoTask afterTask;
    ProgressDialog dialog;
    Context context;
    boolean isUI = true;


    public ProcessUserImageTask(Context context, DoTask useTask,DoTask afterTask, boolean isToShowProgress) {
        this.context = context;
        this.useTask = useTask;
        this.afterTask = afterTask;
        isUI = isToShowProgress;
    }


    @Override
    protected void onPreExecute() {
        if (isUI)
            dialog = ProgressDialog.show(context, "", "");
    }


    @Override
    protected String doInBackground(String... params) {
        if (useTask != null) useTask.run();
        return  "";
    }




    @Override
    protected void onPostExecute(String result) {
        if (isUI)
            dialog.dismiss();
        if (afterTask != null) afterTask.run();

    }
}

