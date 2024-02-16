package com.goldentech.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldentech.R;
import com.goldentech.adapter.Navigation_View_Adapter;
import com.goldentech.api.Api;
import com.goldentech.common.AllApiActivity;
import com.goldentech.common.AppPreferences;
import com.goldentech.common.CheckPermission;
import com.goldentech.common.DbContract;
import com.goldentech.common.IOnBackPressed;
import com.goldentech.common.NetworkCheckActivity;
import com.goldentech.fragments.HomeFragment;
import com.goldentech.model.NavViewModel;
import com.goldentech.model.prfile_detail_res.ProfileDetailRes;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.goldentech.model.NavViewModel.NoHeader;

public class HomeActivity extends AppCompatActivity implements Navigation_View_Adapter.OnButtonClickListener {
    private RecyclerView recycler_view_nev_item;
    private AppPreferences appPreferences;
    private Dialog alertDialog;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private long mBackPressed;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        appPreferences = new AppPreferences(this);
        CheckPermission checkPermission = new CheckPermission(this);
        checkPermission.getAllRuntimePermissions();
        initView();
    }

    private void initView() {

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        toolbar = findViewById(R.id.toolbar);

//        switch (appPreferences.getThemeStyle()) {
//            case "1":
//                toolbar.setBackgroundResource(R.drawable.style1);
//                break;
//            case "2":
//                toolbar.setBackgroundResource(R.drawable.style2);
//                break;
//            case "3":
//                toolbar.setBackgroundResource(R.drawable.style3);
//                break;
//        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        recycler_view_nev_item = findViewById(R.id.recycler_view_nev_item);
        recycler_view_nev_item.setHasFixedSize(true);
        recycler_view_nev_item.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recycler_view_nev_item.setNestedScrollingEnabled(false);


        displaySelectedFragment(0);
        initDrawer();
        setNavItem();

        if (NetworkCheckActivity.isNetworkAvailable(this)) {
            getUserProfile();
        }

    }

    public void displaySelectedFragment(int id) {
        Fragment fragment = null;

        if (id == 0) {
            fragment = new HomeFragment();
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            if (id == 0)
                ft.replace(R.id.frameContainer, fragment, "home_fragment");
            else
                ft.replace(R.id.frameContainer, fragment).addToBackStack(null);

            ft.commit();
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
    }


    private void initDrawer() {

        drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//        findViewById(R.id.image_open_nav).setOnClickListener(v -> {
//            if (drawer.isDrawerOpen(GravityCompat.START)) {
//                drawer.closeDrawer(GravityCompat.START);
//            } else {
//                drawer.openDrawer(GravityCompat.START);
//            }
//        });

        TextView drawer_name = findViewById(R.id.drawer_name);

        if (appPreferences.isLoggedIn()) {
            HashMap<String, String> profile = appPreferences.getUserDetails();
            if (appPreferences.getUserDetails() != null && profile.get("user_name") != null) {
                drawer_name.setText(profile.get("user_name").toUpperCase());
            }
        }

//        if (appPreferences.isSecureLogin()) {
//            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        } else {
//            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//        }
    }

    private void setNavItem() {
        String home;
        String logout;

        home = "Home";
        // language = "Change Language";
        logout = "Logout";

        List<NavViewModel> navViewModelList = new ArrayList<>();

        navViewModelList.add(new NavViewModel(DbContract.HOME, R.drawable.ic_home_24dp,
                home, NoHeader));

        navViewModelList.add(new NavViewModel("Communication", NavViewModel.Header));

//        navViewModelList.add(new NavViewModel(DbContract.WEBSITE, R.drawable.ic_website,
//                website, NoHeader));

        navViewModelList.add(new NavViewModel(DbContract.LOGOUT, R.drawable.ic_power24dp,
                logout, NoHeader));

        Navigation_View_Adapter nAdapter = new Navigation_View_Adapter(HomeActivity.this
                , navViewModelList,
                HomeActivity.this, recycler_view_nev_item);
        recycler_view_nev_item.setAdapter(nAdapter);
        nAdapter.notifyDataSetChanged();

    }

    private void closeDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onClickNavItem(NavViewModel navViewModels) {

        if (navViewModels.getId() == DbContract.HOME) {
            closeDrawer();
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else if (navViewModels.getId() == DbContract.LOGOUT) {
            logout();
        }
    }

    private void logout() {

        final View dialogView = View.inflate(this, R.layout.dialog_common_info_massage, null);
        alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setContentView(dialogView);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.MyAlertDialogStyle;

        TextView tv_title_text = alertDialog.findViewById(R.id.tv_title_text);
        TextView tv_message_text = alertDialog.findViewById(R.id.tv_message_text);
        GifImageView card_view_gif = alertDialog.findViewById(R.id.card_view_gif);

        tv_title_text.setText("Logout Info");
        tv_title_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_24dp, 0, 0, 0);
        tv_message_text.setText("Are you sure you want to logout?");

        Glide.with(this).load(R.drawable.sure_gif).into(card_view_gif);

        TextView btn_ok = alertDialog.findViewById(R.id.btn_ok);
        TextView btn_cancel = alertDialog.findViewById(R.id.btn_cancel);
        btn_ok.setText("YES");
        btn_cancel.setVisibility(View.VISIBLE);
        btn_cancel.setText("Cancel");

        alertDialog.setCancelable(false);
        if (!((Activity) HomeActivity.this).isFinishing()) {
            try {
                alertDialog.show();
            } catch (WindowManager.BadTokenException e) {
                Log.e("WindowManagerBad ", e.toString());
            }
        }
        btn_ok.setOnClickListener(v -> {
            alertDialog.dismiss();
            appPreferences.clearSession();
            Intent intent_logout = new Intent(HomeActivity.this,
                    LoginRegisterActivity.class);
            intent_logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent_logout);
            finish();
            overridePendingTransition(R.anim.modal_in, R.anim.modal_out);
            displayCostumeToast();
        });

        btn_cancel.setOnClickListener(v -> alertDialog.dismiss());

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameContainer);
            if (!(fragment instanceof IOnBackPressed) || ((IOnBackPressed) fragment).onBackPressed()) {
                super.onBackPressed();
            } else {

                int mTimeDelay = 2000;
                if (mBackPressed + mTimeDelay > System.currentTimeMillis()) {
                    finish();
                } else {
                    Toast toast = Toast.makeText(HomeActivity.this, "Press Back again to exit", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                mBackPressed = System.currentTimeMillis();
            }

        }
    }

    private void displayCostumeToast() {
        int toastDuration = 200000;
        final Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_toast_with_image, null);
        GifImageView card_view_gif = view.findViewById(R.id.card_view_gif);
        TextView tv_title_text = view.findViewById(R.id.tv_title_text);
        tv_title_text.setText("You have been successfully logged out.");
        Glide.with(this).load(R.drawable.ty_gif).into(card_view_gif);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new

                                    Runnable() {
                                        @Override
                                        public void run() {
                                            toast.cancel();
                                        }
                                    }, toastDuration);
    }


    private void getUserProfile() {

        Api api = AllApiActivity.getInstance().getApi();
        Call<ProfileDetailRes> resCall = api.getUserProfileDetail(DbContract.UNIQUE_ID, appPreferences.getUserId());
        resCall.enqueue(new Callback<ProfileDetailRes>() {
            @Override
            public void onResponse(Call<ProfileDetailRes> call, Response<ProfileDetailRes> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        String status_code = response.body().getStatusCode();
                        String message = response.body().getMessage();
                        if (status_code.equals("1")) {
                            String name = response.body().getProfileDetail().getName();
                            String mobile = response.body().getProfileDetail().getMobile();
                            String email_id = response.body().getProfileDetail().getEmailId();
                            String profile_photo = response.body().getProfileDetail().getProfilePhoto();
                            String user_type_id = response.body().getProfileDetail().getUserTypeID();
                            String user_type_name = response.body().getProfileDetail().getUserTypeName();
                            TextView drawer_mobile = findViewById(R.id.drawer_mobile);
                            drawer_mobile.setText(mobile);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileDetailRes> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }

}
