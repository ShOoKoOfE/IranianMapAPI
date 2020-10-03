package project.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.shokoofeadeli.iranianmapapi.R;

import project.base.BaseActivity;
import project.comon.ActionBarSetting;
import project.helper.RequestHelper;

import static project.base.BaseApplication.getBaseApplication;

public class SplashActivity extends BaseActivity {

    private static final long SPLASH_TIME_OUT = 5000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBarSetting();
        setContentView(R.layout.activity_splash);

        requestForWriteSDCardPermission();
    }



    private void requestForWriteSDCardPermission() {
        RequestHelper request = new RequestHelper(this);
        RequestHelper.OnGrantedListener grantedListenerListener = new RequestHelper.OnGrantedListener() {
            @Override
            public void onGranted() {
                requestForLocationPermission();
            }
        };

        RequestHelper.OnAlreadyGrantedListener grantedListener = new RequestHelper.OnAlreadyGrantedListener() {
            @Override
            public void onAlreadyGranted() {
                requestForLocationPermission();
            }
        };

        RequestHelper.OnDeniedListener deniedListener = new RequestHelper.OnDeniedListener() {
            @Override
            public void onDenied() {
                ShowDeniedDialog("SDCard");
            }
        };

        request.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, grantedListenerListener, deniedListener,grantedListener);
    }

    private void requestForLocationPermission() {
        RequestHelper request = new RequestHelper(this);
        RequestHelper.OnGrantedListener grantedListenerListener = new RequestHelper.OnGrantedListener() {
            @Override
            public void onGranted() {
                startMapActivity();
            }
        };

        RequestHelper.OnAlreadyGrantedListener grantedListener = new RequestHelper.OnAlreadyGrantedListener() {
            @Override
            public void onAlreadyGranted() {
                startMapActivity();
            }
        };

        RequestHelper.OnDeniedListener deniedListener = new RequestHelper.OnDeniedListener() {
            @Override
            public void onDenied() {
                ShowDeniedDialog("Location");
            }
        };

        request.request(Manifest.permission.ACCESS_FINE_LOCATION, grantedListenerListener, deniedListener,grantedListener);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        RequestHelper.onRequestPermissionResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void ShowDeniedDialog(String state) {
        new AlertDialog.Builder(SplashActivity.this)
                .setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.permission_warning))
                .setPositiveButton(getString(R.string.create_permission), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (state.equalsIgnoreCase("Location"))
                            requestForLocationPermission();
                        else
                            requestForWriteSDCardPermission();
                    }
                })
                .setNegativeButton(getString(R.string.close_app), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                })
                .create()
                .show();
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