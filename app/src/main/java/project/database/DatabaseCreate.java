package project.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseCreate extends SQLiteOpenHelper {
    public static final String DB_FILE_NAME = "place.db";
    public static final int DB_VERSION = 1;

    public DatabaseCreate(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(LocationTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(LocationTable.SQL_DELETE);
        onCreate(sqLiteDatabase);
    }
}
