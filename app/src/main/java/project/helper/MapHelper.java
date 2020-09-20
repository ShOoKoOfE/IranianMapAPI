package project.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.LineString;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolLongClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.shokoofeadeli.iranianmapapi.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ir.map.sdk_map.MapirStyle;
import ir.map.sdk_map.maps.MapView;
import ir.map.servicesdk.MapService;
import ir.map.servicesdk.ResponseListener;
import ir.map.servicesdk.enums.RouteType;
import ir.map.servicesdk.model.base.MapirError;
import ir.map.servicesdk.model.inner.SearchItem;
import ir.map.servicesdk.request.RouteRequest;
import ir.map.servicesdk.request.SearchRequest;
import ir.map.servicesdk.response.RouteResponse;
import ir.map.servicesdk.response.SearchResponse;
import project.ui.MapActivity;

import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;

public class MapHelper {
    public static LatLng destinationLatLng = null;
    public static MapboxMap map;
    static int zoom = 17;
    static LatLng lastKnowLatLng = new LatLng(35.756492, 51.408772);
    MapService mapService = new MapService();
    SymbolManager symbolManager;
    LineManager lineManager;
    Style mapStyle;
    MapClickListener mapClickListener;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private Activity activity;
    private MapView mapView;
    private MapHelper.MyLocationCallback callback;
    public static boolean flagCurrentLocation = true;
    LongSparseArray<Symbol> symbolArray = new LongSparseArray<>();

    public MapHelper(Activity activity, MapView mapView, MapClickListener mapClickListener) {
        this.activity = activity;
        this.mapView = mapView;
        this.mapClickListener = mapClickListener;
        callback = new MapHelper.MyLocationCallback((MapActivity) activity);
        initiateMap();
    }

    public static void zoomToSpecificLocation(LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initiateMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                map.setStyle(new Style.Builder().fromUri(MapirStyle.MAIN_MOBILE_VECTOR_STYLE), new Style.OnStyleLoaded() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mapStyle = style;
                        enableLocationComponent();
                        setSymbolManager();
                        lineManager = new LineManager(mapView, map, mapStyle, "hw-secondary-tertiary");
                    }
                });
                map.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        clearMap();
                        destinationLatLng = new LatLng(point.getLatitude(), point.getLongitude());
                        addSymbolToMap(destinationLatLng);
                        mapClickListener.onMapClick();
                        return false;
                    }
                });
            }
        });
    }

    public void clearMap() {
        List<Symbol> symbols = new ArrayList<>();
        for (int i = 0; i < symbolArray.size(); i++) {
            symbols.add(symbolArray.valueAt(i));
        }
        symbolManager.delete(symbols);
        lineManager.deleteAll();
        symbolArray.clear();
        setSymbolManager();
    }

    @SuppressLint("MissingPermission")
    public void enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(activity)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .accuracyColor(Color.RED)
                    .build();
            LocationComponent locationComponent = map.getLocationComponent();
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(activity, mapStyle)
                            .useDefaultLocationEngine(false)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();
            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
            initLocationEngine();
            locationComponent.addOnLocationClickListener(new OnLocationClickListener() {
                @Override
                public void onLocationComponentClick() {
                }
            });
        } else {
            PermissionsManager permissionsManager = new PermissionsManager(new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> permissionsToExplain) {
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted)
                        enableLocationComponent();
                    else
                        Toast.makeText(activity, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            });
            permissionsManager.requestLocationPermissions(activity);
        }
    }

    @SuppressLint("MissingPermission")
    public void initLocationEngine() {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(activity);
        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();
        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void addSymbolToMap(LatLng latLng) {
        symbolManager.addClickListener(new OnSymbolClickListener() {
            @Override
            public void onAnnotationClick(Symbol symbol) {
            }
        });
        symbolManager.addLongClickListener(new OnSymbolLongClickListener() {
            @Override
            public void onAnnotationLongClick(Symbol symbol) {
            }
        });

        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);
        SymbolOptions symbolOptions = new SymbolOptions();
        symbolOptions.withLatLng(latLng);
        symbolOptions.withIconImage("sample_image_id");
        symbolOptions.withIconSize(1.0f);
        Symbol symbol = symbolManager.create(symbolOptions);
        symbolArray.append(symbolArray.size(),symbol);
    }

    public void routeRequest() {
        RouteRequest requestBody = new RouteRequest.Builder(lastKnowLatLng.getLatitude(), lastKnowLatLng.getLongitude(),
                destinationLatLng.getLatitude(), destinationLatLng.getLongitude(),
                RouteType.DRIVING
        ).build();

        mapService.route(requestBody, new ResponseListener<RouteResponse>() {
            @Override
            public void onSuccess(RouteResponse response) {
                showRouteOnMap(response.getRoutes().get(0).getGeometry());
            }

            @Override
            public void onError(MapirError error) {
                Toast.makeText(activity, "مشکلی در مسیریابی پیش آمده", Toast.LENGTH_SHORT).show();
            }
        });
        destinationLatLng = null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setSymbolManager(){
        mapStyle.addImage("sample_image_id", activity.getResources().getDrawable(R.drawable.mapbox_marker_icon_default));
        symbolManager = new SymbolManager(mapView, map, mapStyle);
    }

    public void showRouteOnMap(String geometry) {
        LineString routeLine = LineString.fromPolyline(geometry, 5); // second parameter must be 5
        LineOptions lineOptions = new LineOptions()
                .withGeometry(routeLine)
                .withLineColor("#ff5252")
                .withLineWidth(5f);
        lineManager.create(lineOptions);
    }

    public void searchLocation(SearchRequest requestBody, LocationSearchListener locationSearchListener) {
        mapService.search(requestBody, new ResponseListener<SearchResponse>() {
            @Override
            public void onSuccess(SearchResponse response) {
                int count = response.getCount();
                List<SearchItem> searchItems = response.getSearchItems();
                locationSearchListener.OnResponseComplete(searchItems);
                Toast.makeText(activity, "پاسخ جستجو دریافت شد", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(MapirError error) {
                Toast.makeText(activity, "مشکلی در جستجو پیش آمده", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateLocation(double latitude,double longitude){
        map.easeCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
    }

    private static class MyLocationCallback implements LocationEngineCallback<LocationEngineResult> {
        private final WeakReference<MapActivity> activityWeakReference;

        MyLocationCallback(MapActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            MapActivity activity = activityWeakReference.get();
            if (activity != null && flagCurrentLocation) {
                flagCurrentLocation = false;
                Location location = result.getLastLocation();
                //Location location = new Location("");
                //location.setLatitude(35.756492);
                //location.setLongitude(51.408772);
                if (location == null)
                    return;
                lastKnowLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                //lastKnowLatLng = new LatLng(35.756492, 51.408772);
                zoomToSpecificLocation(lastKnowLatLng);
                map.getLocationComponent().forceLocationUpdate(location);
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            MapActivity activity = activityWeakReference.get();
            if (activity != null)
                Toast.makeText(activity, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
