package webit.android.shanti.general.connection;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import webit.android.shanti.R;
import webit.android.shanti.general.Common;


public class GeneralTask extends AsyncTask<String, String, String> {

    private String resultS = "";
    private UseTask useTask;
    private ProgressDialog dialog;
    private Context context;
    private boolean isUI = true;
    private String url = null;
    private String taskName;
    private CountDownTimer mCountDownTimer;


    public GeneralTask(Activity context, UseTask useTask) {
        this.context = context;
        this.useTask = useTask;
    }

    public GeneralTask(Context context, UseTask useTask, boolean isToShowProgress) {
        this.context = context;
        this.useTask = useTask;
        this.isUI = isToShowProgress;
    }

    public GeneralTask(Context context, String url, UseTask useTask, boolean isToShowProgress) {
        this.context = context;
        this.useTask = useTask;
        this.url = url;
        isUI = isToShowProgress;
    }


    public GeneralTask(Activity context, boolean b) {
        this.context = context;
        isUI = b;
    }

    public GeneralTask(Context context, String url, UseTask useTask) {
        this.context = context;
        this.url = url;
        this.useTask = useTask;
    }

    @Override
    protected void onPreExecute() {
        if (isUI)
            if (context != null && !((Activity) context).isFinishing())
                dialog = ProgressDialog.show(context, "", "");
            }

        boolean isReturnServer = false;

    /* 0- type
    * 1- data
    * 2- function*/
        @Override
        protected String doInBackground (String...params){

            if (Common.isNetworkAvailable(context)) {
                if (url == null) {
                    if (params[0].equals(SendType.POST.toString())) {
                        taskName = params[2];
                        Log.d("Shanti send to " + taskName, params[1]);
                        filedServer();
                        String s = "";
                        s = new JsonServerConnection().postGetJsonVer2(params[1],
                                ConnectionUtil.getServerUrl() + params[2]);
                        isReturnServer = true;
                        if (s != null && !s.equals("")) {
                            if (mCountDownTimer != null)
                                mCountDownTimer.cancel();
                            mCountDownTimer = null;
                        }
                        return s;
                    } else {
                        if (params[0].equals(SendType.GET.toString())) {
                            taskName = params[1];
                            Log.d("Shanti send Get", params[1]);
                            if (taskName != null)
                                try {
                                    return new JsonServerConnection().get(taskName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d("JsonServerConnection Exception", e.toString());
                                    return "";
                                }
                        }
                    }

                } else {
                    Log.d("Shanti send Get", url);
                    if (params[0].equals(SendType.POST.toString())) {
                        filedServer();
                        String s = "";
                        s = new JsonServerConnection().postGetJsonVer2(params[1],
                                url);
                        isReturnServer = true;
                        if (s != null && !s.equals("")) {
                            if (mCountDownTimer != null)
                                mCountDownTimer.cancel();
                            mCountDownTimer = null;
                        }
                        return s;
                    } else
                        return new JsonServerConnection().postGet(url);
                }
            } else {
                return null;
            /*if (url == null) {
                if (params[0].equals(SendType.POST.toString())) {
                    taskName = params[2];

                }
                if (params[0].equals(SendType.GET.toString())) {
                    taskName = params[1];

                }
            }*/
            }
            return null;

        }

    private void filedServer() {
        if (context instanceof Activity)
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCountDownTimer = new CountDownTimer(30 * 1000, 1 * 1000) {
                        //                new CountDownTimer(1, 1) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            if (!isReturnServer && resultS != null && resultS.equals("")) {
                                useTask.doAfterTask(null);
                                Toast.makeText(context, context.getResources().getString(R.string.noServer), Toast.LENGTH_LONG).show();
                            }
                        }
                    }.start();
                }
            });
        else {

        }
    }


    @Override
    protected void onPostExecute(String result) {
        resultS = result;
        if ((dialog != null) && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            } catch (final Exception e) {
                // Handle or log or ignore
            } finally {
                dialog = null;
            }
        }
        if (result == null) {
            Toast.makeText(context, context.getString(R.string.noConnectionNetwork), Toast.LENGTH_SHORT).show();
            useTask.doAfterTask(result);
        } else {
            Log.d("Shanti", taskName + ": " + result);
            if (useTask != null) useTask.doAfterTask(result);
        }
    }


    public enum SendType {
        POST, GET
    }
}
