package project.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.LayoutRes;

import com.shokoofeadeli.iranianmapapi.R;

import java.lang.reflect.Field;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import ir.map.sdk_map.Mapir;
import ir.map.servicesdk.MapirService;

public class BaseApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static BaseApplication baseApplication;
    private LayoutInflater layoutInflater;
    private Handler handler;

    public static BaseApplication getBaseApplication() {
        return baseApplication;
    }

    public static void HideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        baseApplication = this;
        Mapir.getInstance(this, "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImQyM2MxMDczOWY3MzcxM2ZhMDQ3Mjk1ZDdiNmZkNmI4ZTYyODY3YWUwZTUzMzk1N2YyODM3N2Q1ZjU2OGMyZTc0OTA2Zjk0YjQ1ZTU2MDg0In0.eyJhdWQiOiIxMDU0OCIsImp0aSI6ImQyM2MxMDczOWY3MzcxM2ZhMDQ3Mjk1ZDdiNmZkNmI4ZTYyODY3YWUwZTUzMzk1N2YyODM3N2Q1ZjU2OGMyZTc0OTA2Zjk0YjQ1ZTU2MDg0IiwiaWF0IjoxNTk4MDMxOTEyLCJuYmYiOjE1OTgwMzE5MTIsImV4cCI6MTYwMDcxMzkxMiwic3ViIjoiIiwic2NvcGVzIjpbImJhc2ljIl19.rceQya0iD_eck77yiBnvCBoqwn5ncG__fy-YwaDR6zvzMR8jwAf0rytZCONi8-sO6F8-DZJh5pNGF6SLQvF_PEWNsuyhlx6WRI4p8Pqe54s868yFu-KA2cqt3jymMrbarz_e0jXk5JtM4x-aKjPCefvzOIRWBRkDhcw5OowTOJXKyZ0P6zudLiGyiHOTXOVKdv9b4ZMaPuBvjIJauBGbccuxxQ2VulAyyv2vr2Htm5VOPCYgnFgydHhSTjLdzm-tK2v_ao9hRtD5s4MsuhtKLhqF7NnbG2CDImfvIoLfA6I9y1sOz9cwDdUPDljkDdf65sbgK3zTuStOAnLZmIVEFw");
        MapirService.init(getBaseContext(), "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImQyM2MxMDczOWY3MzcxM2ZhMDQ3Mjk1ZDdiNmZkNmI4ZTYyODY3YWUwZTUzMzk1N2YyODM3N2Q1ZjU2OGMyZTc0OTA2Zjk0YjQ1ZTU2MDg0In0.eyJhdWQiOiIxMDU0OCIsImp0aSI6ImQyM2MxMDczOWY3MzcxM2ZhMDQ3Mjk1ZDdiNmZkNmI4ZTYyODY3YWUwZTUzMzk1N2YyODM3N2Q1ZjU2OGMyZTc0OTA2Zjk0YjQ1ZTU2MDg0IiwiaWF0IjoxNTk4MDMxOTEyLCJuYmYiOjE1OTgwMzE5MTIsImV4cCI6MTYwMDcxMzkxMiwic3ViIjoiIiwic2NvcGVzIjpbImJhc2ljIl19.rceQya0iD_eck77yiBnvCBoqwn5ncG__fy-YwaDR6zvzMR8jwAf0rytZCONi8-sO6F8-DZJh5pNGF6SLQvF_PEWNsuyhlx6WRI4p8Pqe54s868yFu-KA2cqt3jymMrbarz_e0jXk5JtM4x-aKjPCefvzOIRWBRkDhcw5OowTOJXKyZ0P6zudLiGyiHOTXOVKdv9b4ZMaPuBvjIJauBGbccuxxQ2VulAyyv2vr2Htm5VOPCYgnFgydHhSTjLdzm-tK2v_ao9hRtD5s4MsuhtKLhqF7NnbG2CDImfvIoLfA6I9y1sOz9cwDdUPDljkDdf65sbgK3zTuStOAnLZmIVEFw");
        layoutInflater = LayoutInflater.from(context);
        handler = new Handler();

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/IRANSans.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        setActionBarMenuSetting();
    }

    public View inflateLayout(@LayoutRes int res) {
        return layoutInflater.inflate(res, null);
    }

    public Handler getHandler() {
        return handler;
    }

    private void setActionBarMenuSetting() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
