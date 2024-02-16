package com.goldentech.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.goldentech.R;
import com.goldentech.app.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckServerProblem {
    private Activity activity;
    private Context mContext;
    private AppPreferences appPreferences;
    private static final String TAG = "CheckServerProblem";
    private Dialog alertDialog;

    public CheckServerProblem(Context context) {
        activity = (Activity) context;
        mContext = context;
        appPreferences = new AppPreferences(context);
        if (NetworkCheckActivity.isNetworkAvailable(activity)) {
            getServerProblem();
        } else {
            showNoInternetDialog();
        }

    }

    private void getServerProblem() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllApiActivity.GET_SERVER_STATUS_URL,
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            boolean is_server_problem = jsonObj.getBoolean("is_server_problem");
                            if (is_server_problem)
                                showServerProblemDialog();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("    catch  JSONException   ");
                        }
                    } else {
                        Log.e("JSON Data", "JSON data error!");
                    }
                },
                error -> showServerProblemDialog());
        MyApplication.getInstance(activity).addToRequestQueue(stringRequest);
    }

    private void showServerProblemDialog() {
        final View dialogView = View.inflate(mContext, R.layout.dialog_common_info_massage, null);
        alertDialog = new Dialog(mContext);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setContentView(dialogView);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.MyAlertDialogStyle;

        TextView btn_ok = alertDialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(v -> {
            activity.finish();
            alertDialog.cancel();
        });
        alertDialog.setCancelable(false);
        if (!((Activity) mContext).isFinishing()) {
            try {
                alertDialog.show();
            } catch (WindowManager.BadTokenException e) {
                Log.e("WindowManagerBad ", e.toString());
            }
        }
    }

    private void showNoInternetDialog() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Net Problem");
        alertDialogBuilder.setMessage("Sorry !!! on internet connection");
        alertDialogBuilder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (NetworkCheckActivity.isNetworkAvailable(activity)) {
                    getServerProblem();
                    dialog.dismiss();
                } else {
                    showNoInternetDialog();
                }
            }
        });
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.show();
    }

}
