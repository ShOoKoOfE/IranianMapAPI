package project.database;

public class LocationTable {
    public static final String TABLE_LOCATION = "location";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_Latitude = "latitude";
    public static final String COLUMN_Longitude = "longitude";

    public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_Latitude, COLUMN_Longitude};
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_LOCATION + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_Latitude + " REAL," +
                    COLUMN_Longitude + " REAL" + ");";
    public static final String SQL_DELETE =
            "DROP TABLE " + SQL_CREATE;
}
