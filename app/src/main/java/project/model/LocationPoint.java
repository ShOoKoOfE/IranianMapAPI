package project.model;

import android.content.ContentValues;

import project.database.LocationTable;

public class LocationPoint {
    private Integer id;
    private Double latitude;
    private Double longitude;

    public LocationPoint() {
    }

    public LocationPoint(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues(2);
        values.put(LocationTable.COLUMN_Latitude, latitude);
        values.put(LocationTable.COLUMN_Longitude, longitude);
        return values;
    }
}
