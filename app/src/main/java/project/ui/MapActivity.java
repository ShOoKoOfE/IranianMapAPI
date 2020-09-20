package project.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.shokoofeadeli.iranianmapapi.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.map.sdk_map.maps.MapView;
import ir.map.servicesdk.model.inner.SearchItem;
import ir.map.servicesdk.request.SearchRequest;
import project.adapter.AddressListener;
import project.adapter.AddressRecyclerAdapter;
import project.base.BaseActivity;
import project.comon.ActionBarSetting;
import project.database.DataSource;
import project.helper.LocationSearchListener;
import project.helper.MapClickListener;
import project.helper.MapHelper;
import project.model.LocationPoint;

import static project.base.BaseApplication.HideSoftKeyboard;
import static project.helper.MapHelper.destinationLatLng;

public class MapActivity extends BaseActivity {
    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.editTextLocation)
    EditText editTextLocation;
    @BindView(R.id.layoutAction)
    LinearLayout layoutAction;
    @BindView(R.id.layoutRoute)
    LinearLayout layoutRoute;
    @BindView(R.id.layoutSave)
    LinearLayout layoutSave;
    @BindView(R.id.imageButtonSearch)
    ImageButton imageButtonSearch;
    @BindView(R.id.imageButtonAllLocation)
    ImageButton imageButtonAllLocation;
    @BindView(R.id.lstAddress)
    RecyclerView lstAddress;
    @BindView(R.id.imgCurrentLocation)
    ImageView imgCurrentLocation;

    DataSource dataSource;
    MapHelper mapHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarSetting();
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        dataSource = new DataSource(this);
        dataSource.open();

        mapHelper = new MapHelper(MapActivity.this, mapView, new MapClickListener() {
            @Override
            public void onMapClick() {
                layoutAction.setVisibility(View.VISIBLE);
            }
        });
    }

    @OnClick(R.id.layoutRoute)
    public void onLayoutRouteClick(View view) {
        layoutAction.setVisibility(View.GONE);
        mapHelper.routeRequest();
    }

    @OnClick(R.id.layoutSave)
    public void onLayoutSaveClick(View view) {
        LocationPoint locationPoint = new LocationPoint(destinationLatLng.getLatitude(),destinationLatLng.getLongitude());
        dataSource.CreateLocation(locationPoint);
        destinationLatLng = null;
        layoutAction.setVisibility(View.GONE);
    }

    @OnClick(R.id.imageButtonAllLocation)
    public void onButtonAllLocationClick(View view) {
        mapHelper.clearMap();
        List<LocationPoint> locationPoints = dataSource.getAllLocations();
        for (LocationPoint locationPoint : locationPoints) {
            LatLng latLng = new LatLng(locationPoint.getLatitude(), locationPoint.getLongitude());
            mapHelper.addSymbolToMap(latLng);
        }
    }

    @OnClick(R.id.imgCurrentLocation)
    public void onImageCurrentLocationClick(View view) {
        mapHelper.flagCurrentLocation = true;
    }

    @OnClick(R.id.imageButtonSearch)
    public void onButtonSearchClick(View view) {
        HideSoftKeyboard(MapActivity.this);
        String locationName = editTextLocation.getText().toString();
        if (TextUtils.isEmpty(locationName)) {
            editTextLocation.setError("محل مورد نظرتان را وارد نمایید؟");
            editTextLocation.requestFocus();
        }
        else{
            SearchRequest requestBody = new SearchRequest.Builder(locationName).build();
            mapHelper.searchLocation(requestBody, new LocationSearchListener() {
                @Override
                public void OnResponseComplete(List<SearchItem> searchItems) {
                    lstAddress.setLayoutManager(new LinearLayoutManager(MapActivity.this));
                    lstAddress.setVisibility(View.VISIBLE);
                    AddressRecyclerAdapter addressRecyclerAdapter = new AddressRecyclerAdapter(searchItems, MapActivity.this, new AddressListener() {
                        @Override
                        public void onResponseAddress(double latitude, double longitude) {
                            mapHelper.updateLocation(latitude,longitude);
                            lstAddress.setVisibility(View.GONE);
                        }
                    });
                    lstAddress.setAdapter(addressRecyclerAdapter);
                }
            });
        }
    }

    public void setActionBarSetting() {
        new ActionBarSetting(this)
                .hasActionbar(false)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        dataSource.close();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}