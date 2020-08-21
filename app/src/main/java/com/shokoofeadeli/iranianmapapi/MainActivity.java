package com.shokoofeadeli.iranianmapapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LongSparseArray;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.map.sdk_map.maps.MapView;
import ir.map.sdk_map.MapirStyle;
import ir.map.servicesdk.MapService;
import ir.map.servicesdk.ResponseListener;
import ir.map.servicesdk.enums.RouteType;
import ir.map.servicesdk.model.base.MapirError;
import ir.map.servicesdk.request.RouteRequest;
import ir.map.servicesdk.response.RouteResponse;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;

public class MainActivity extends AppCompatActivity {
    MapboxMap map;
    Style mapStyle;
    MapView mapView;
    int zoom = 16;

    ArrayList<LatLng> markerPoints = new ArrayList<>();
    LatLng currentLatLng;
    SymbolManager sampleSymbolManager;
    MapService mapService = new MapService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                map.setStyle(new Style.Builder().fromUri(MapirStyle.MAIN_MOBILE_VECTOR_STYLE), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mapStyle = style;
                        enableLocationComponent();
                        sampleSymbolManager = new SymbolManager(mapView, map, mapStyle);
                    }
                });


                map.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {

                        if (markerPoints.size() > 4) {
                            markerPoints.clear();
                            List<Symbol> symbols = new ArrayList<>();
                            LongSparseArray<Symbol> symbolArray = sampleSymbolManager.getAnnotations();
                            for (int i = 0; i < symbolArray.size(); i++) {
                                symbols.add(symbolArray.valueAt(i));
                            }
                            sampleSymbolManager.delete(symbols);
                        }

                        LatLng latLng = new LatLng(point.getLatitude(),point.getLongitude());
                        markerPoints.add(latLng);
                        addSymbolToMap(latLng);
                        zoomToSpecificLocation(latLng);

                        if(markerPoints.size() == 5){
                            measureDistance();
                        }
                        return false;
                    }
                });
            }
        });
    }


    private void enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .accuracyColor(Color.RED)
                    .build();
            LocationComponent locationComponent = map.getLocationComponent();
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, mapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();
            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            assert locationComponent.getLastKnownLocation() != null;
            double currentLat = locationComponent.getLastKnownLocation().getLatitude();
            double currentLng = locationComponent.getLastKnownLocation().getLongitude();
            currentLatLng = new LatLng(currentLat,currentLng);
            //currentLatLng = new LatLng(35.756492,51.408772);
            //zoomToSpecificLocation(currentLatLng);

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
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            });
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void addSymbolToMap(LatLng latLng) {
        mapStyle.addImage("sample_image_id", getResources().getDrawable(R.drawable.mapbox_marker_icon_default));
        sampleSymbolManager.addClickListener(new OnSymbolClickListener() {
            @Override
            public void onAnnotationClick(Symbol symbol) {
                Toast.makeText(MainActivity.this, "This is CLICK_EVENT", Toast.LENGTH_SHORT).show();
            }
        });
        sampleSymbolManager.addLongClickListener(new OnSymbolLongClickListener() {
            @Override
            public void onAnnotationLongClick(Symbol symbol) {
                Toast.makeText(MainActivity.this, "This is LONG_CLICK_EVENT", Toast.LENGTH_SHORT).show();
            }
        });
        sampleSymbolManager.setIconAllowOverlap(true);
        sampleSymbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);
        SymbolOptions sampleSymbolOptions = new SymbolOptions();
        sampleSymbolOptions.withLatLng(latLng);
        sampleSymbolOptions.withIconImage("sample_image_id");
        sampleSymbolOptions.withIconSize(1.0f);
        Symbol sampleSymbol = sampleSymbolManager.create(sampleSymbolOptions);
    }

    private void zoomToSpecificLocation(LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void measureDistance() {
        Location currentLocation = new Location("");
        currentLocation.setLatitude(currentLatLng.getLatitude());
        currentLocation.setLongitude(currentLatLng.getLongitude());

        Location targetLocation = new Location("");
        targetLocation.setLatitude(markerPoints.get(0).getLatitude());
        targetLocation.setLongitude(markerPoints.get(0).getLongitude());

        float minDistanceInMeters = targetLocation.distanceTo(currentLocation);
        LatLng destination = markerPoints.get(0);

        for(LatLng latLng : markerPoints){
            targetLocation.setLatitude(latLng.getLatitude());
            targetLocation.setLongitude(latLng.getLongitude());
            float distanceInMeters =  targetLocation.distanceTo(currentLocation);
            if(minDistanceInMeters>distanceInMeters){
                minDistanceInMeters = distanceInMeters;
                destination = latLng;
            }
        }
        routeRequest(destination);
    }

    private void routeRequest(LatLng destination) {
        RouteRequest requestBody = new RouteRequest.Builder(currentLatLng.getLatitude(),currentLatLng.getLongitude(),
                destination.getLatitude(),destination.getLongitude(),
                RouteType.DRIVING
        ).build();

        mapService.route(requestBody, new ResponseListener<RouteResponse>() {
            @Override
            public void onSuccess(RouteResponse response) {
                showRouteOnMap(response.getRoutes().get(0).getGeometry());
            }
            @Override
            public void onError(MapirError error) {
                Toast.makeText(MainActivity.this, "مشکلی در مسیریابی پیش آمده", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRouteOnMap(String geometry) {
        LineManager lineManager = new LineManager(mapView, map, mapStyle, "hw-secondary-tertiary");

        LineString routeLine = LineString.fromPolyline(geometry, 5); // second parameter must be 5

        LineOptions lineOptions = new LineOptions()
                .withGeometry(routeLine)
                .withLineColor("#ff5252")
                .withLineWidth(5f);

        lineManager.create(lineOptions);
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