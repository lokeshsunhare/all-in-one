package com.goldentech.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldentech.R;
import com.goldentech.common.DbContract;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        TextView tv_title = findViewById(R.id.tv_title);

        tv_title.getPaint().setShader(DbContract.getGradientColor(this, tv_title));

        TextView tv_version_name = findViewById(R.id.tv_version_name);

//        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.sequential);
//        crop_logo = findViewById(R.id.crop_logo);
//        ImageView image_study = findViewById(R.id.image_study);
//        crop_logo.startAnimation(myAnim);
//        image_study.startAnimation(myAnim);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tv_version_name.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(this::endSplash, 3000);
    }

    private void endSplash() {
        Intent intent = new Intent(SplashActivity.this, LoginRegisterActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
