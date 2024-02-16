package com.goldentech.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldentech.R;
import com.goldentech.activities.HomeActivity;
import com.goldentech.api.Api;
import com.goldentech.app.GPSTracker;
import com.goldentech.common.AllApiActivity;
import com.goldentech.common.AppPreferences;
import com.goldentech.common.CheckPermission;
import com.goldentech.common.DbContract;
import com.goldentech.common.NetworkCheckActivity;
import com.goldentech.model.login_register_res.LoginRegisterRes;
import com.goldentech.model.login_register_res.ResponseResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private View view;
    private EditText et_login_id;
    private EditText et_password;
    private long mLastClickTime = 0;
    private String POST_TIME = null;
    protected int screenW;
    protected int screenH;
    private AppPreferences appPreferences;
    private static final String TAG = "LoginFragment";
    private String deviceId;
    private double latitude, longitude;
    private Dialog alertDialog;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appPreferences = new AppPreferences(getActivity());
        this.view = view;
        initView();
    }

    private void initView() {

        CheckPermission checkPermission = new CheckPermission(getActivity());
        checkPermission.getLocationPermission();

        GPSTracker gps = new GPSTracker(getActivity());
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

        TextView tv_sign_in = view.findViewById(R.id.tv_sign_in);
        et_login_id = view.findViewById(R.id.et_login_id);
        et_password = view.findViewById(R.id.et_password);

        deviceId = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        if (appPreferences.isLoggedIn()) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
            //getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return;
        }

        view.findViewById(R.id.layout_dont_have_account).setOnClickListener(v -> {
            Fragment fragment = new RegisterFragment();
            if (fragment != null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                ft.replace(R.id.frameContainer, fragment).addToBackStack(null);
                ft.commit();
            }
        });
        tv_sign_in.setOnClickListener(v -> {
            validateField();
        });
    }

    private void validateField() {

        boolean valid = true;
        if (et_login_id.getText().toString().trim().isEmpty()) {
            valid = false;
            Toast.makeText(getActivity(), "Please enter mobile number", Toast.LENGTH_SHORT).show();
        } else if (et_password.getText().toString().trim().isEmpty()) {
            valid = false;
            Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
        }
        if (valid) {

            if (NetworkCheckActivity.isNetworkAvailable(getActivity())) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                POST_TIME = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                getUserLogin(et_login_id.getText().toString().trim(), et_password.getText().toString().trim());

            } else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void getUserLogin(String mobile,
                              String password) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait while we login your account..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d(TAG, "getUserLogin: ");

        Api api = AllApiActivity.getInstance().getApi();
        Call<LoginRegisterRes> resCall = api.getUserLogin(DbContract.UNIQUE_ID, POST_TIME, mobile, password,
                "" + latitude, "" + longitude, deviceId, Build.VERSION.RELEASE, Build.BRAND, Build.MODEL,
                String.valueOf(Build.VERSION.SDK_INT), DbContract.getLocalIpAddress(),
                DbContract.getPublicIPAddress(getActivity())
        );
        resCall.enqueue(new Callback<LoginRegisterRes>() {
            @Override
            public void onResponse(Call<LoginRegisterRes> call, Response<LoginRegisterRes> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<ResponseResult> result = response.body().getResult();
                        String statusCode = result.get(0).getStatusCode();
                        String message = result.get(0).getMessage();
                        if (statusCode.equals("1")) {
                            appPreferences.clearSession();
                            appPreferences.createLogin(result.get(0).getUserId(),
                                    result.get(0).getName(),
                                    result.get(0).getUserTypeId());
                            Toast.makeText(getActivity(), "Welcome to " + getString(R.string.app_name), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            getActivity().finish();
                            //getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else if (statusCode.equals("0")) {
                            showInfoMessageDialog(message, false);
                        } else showInfoMessageDialog(message, true);
                    }
                    progressDialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<LoginRegisterRes> call, Throwable t) {
                progressDialog.cancel();
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }

    private void showInfoMessageDialog(String message, boolean isRegister) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_common_info_massage, null);
        alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setContentView(dialogView);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.MyAlertDialogStyle;

        TextView tv_title_text = alertDialog.findViewById(R.id.tv_title_text);
        TextView tv_message_text = alertDialog.findViewById(R.id.tv_message_text);
        GifImageView card_view_gif = alertDialog.findViewById(R.id.card_view_gif);

        tv_title_text.setText("Info Message");
        tv_title_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_24dp, 0, 0, 0);
        tv_message_text.setText(message);

        Glide.with(this).load(R.drawable.sorry_gif).into(card_view_gif);
        TextView btn_cancel = alertDialog.findViewById(R.id.btn_cancel);
        TextView btn_ok = alertDialog.findViewById(R.id.btn_ok);
        if (!isRegister) {
            btn_cancel.setVisibility(View.VISIBLE);
            btn_cancel.setText("No Now");
            btn_cancel.setOnClickListener(v -> {
                alertDialog.cancel();
            });
        } else {
            btn_cancel.setVisibility(View.GONE);
            btn_ok.setText("OK");
        }
        btn_ok.setOnClickListener(v -> {
            if (!isRegister) {
                Fragment fragment = new RegisterFragment();
                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frameContainer, fragment).addToBackStack(null);
                    ft.commit();
                }
            } else {
                alertDialog.cancel();
            }
        });
        alertDialog.setCancelable(false);
        if (!((Activity) getActivity()).isFinishing()) {
            try {
                alertDialog.show();
            } catch (WindowManager.BadTokenException e) {
                Log.e("WindowManagerBad ", e.toString());
            }
        }
    }

}
