package project.ui;

import android.content.Intent;
import android.os.Bundle;

import com.shokoofeadeli.iranianmapapi.R;

import project.base.BaseActivity;
import project.comon.ActionBarSetting;

import static project.base.BaseApplication.getBaseApplication;

public class SplashActivity extends BaseActivity {

    private static final long SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarSetting();
        setContentView(R.layout.activity_splash);
        startMapActivity();
    }

    public void setActionBarSetting() {
        new ActionBarSetting(this)
                .hasActionbar(false)
                .build();
    }

    private void startMapActivity() {
        getBaseApplication().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}