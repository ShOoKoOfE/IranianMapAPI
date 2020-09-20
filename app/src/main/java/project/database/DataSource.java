package project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import project.model.LocationPoint;

public class DataSource {
    SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DataSource(Context context) {
        sqLiteOpenHelper = new DatabaseCreate(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
    }

    public void open() {
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
    }

    public void close() {
        sqLiteOpenHelper.close();
    }

    public void CreateLocation(LocationPoint locationPoint) {
        ContentValues values = locationPoint.toValues();
        sqLiteDatabase.insert(LocationTable.TABLE_LOCATION, null, values);
    }

    public List<LocationPoint> getAllLocations() {
        List<LocationPoint> locationPoints = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(LocationTable.TABLE_LOCATION, LocationTable.ALL_COLUMNS, null, null, null, null, null);
        while (cursor.moveToNext()) {
            LocationPoint locationPoint = new LocationPoint();
            locationPoint.setId(cursor.getInt(cursor.getColumnIndex(LocationTable.COLUMN_ID)));
            locationPoint.setLatitude(cursor.getDouble(cursor.getColumnIndex(LocationTable.COLUMN_Latitude)));
            locationPoint.setLongitude(cursor.getDouble(cursor.getColumnIndex(LocationTable.COLUMN_Longitude)));
            locationPoints.add(locationPoint);
        }
        cursor.close();
        return locationPoints;
    }
}
