package input.stdio.pe.kr.daejeonparking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDAO extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "parkingList.db";
    private static final int DATABASE_VERSION = 4;
    private Context mContext;

    public SQLiteDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE parking (_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append("PARKING_ID VARCHAR UNIQUE, NAME VARCHAR NOT NULL, LAT VARCHAR,");
        sb.append("LON VARCHAR, ADDR01 VARCHAR, ADDR02 VARCHAR, DIVIDE_NUM VARCHAR,");
        sb.append("TYPE_NUM VARCHAR, LAND_LEVEL_NUM VARCHAR, TOTAL_PARKING_LOT VARCHAR,");
        sb.append("RESTRICT_CODE VARCHAR, OPERATEDAY_CODE VARCHAR,");
        sb.append("WEEKDAY_OPEN_TIME VARCHAR, WEEKDAY_CLOSE_TIME VARCHAR,");
        sb.append("SAT_OPEN_TIME VARCHAR, SAT_CLOSE_TIME VARCHAR,");
        sb.append("HOLIDAY_OPEN_TIME VARCHAR, HOLIDAY_CLOSE_TIME VARCHAR,");
        sb.append("FREECHARGE_BASETIME VARCHAR, RESERVATION_CODE VARCHAR, ADDITIONAL VARCHAR)");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS parking");
        onCreate(db);
    }
}
