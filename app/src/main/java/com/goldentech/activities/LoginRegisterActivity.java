package com.goldentech.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.goldentech.R;
import com.goldentech.fragments.LoginFragment;

public class LoginRegisterActivity extends AppCompatActivity {

    private static final String TAG = "LoginRegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        initView();


    }

    private void initView() {
        displaySelectedFragment(0);
    }

    public void displaySelectedFragment(int id) {
        Fragment fragment = null;
        if (id == 0) {
            fragment = new LoginFragment();
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (id == 0)
                ft.replace(R.id.frameContainer, fragment);
            else
                ft.replace(R.id.frameContainer, fragment).addToBackStack(null);
            ft.commit();
        }
    }
}
