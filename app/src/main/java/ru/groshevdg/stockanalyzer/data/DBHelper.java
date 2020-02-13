package ru.groshevdg.stockanalyzer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "stocks.db";
    private static final int DB_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Stock.TABLE_NAME + "( " + Stock._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Stock.COMPANY_NAME + " TEXT NOT NULL, " + Stock.PRICE + " TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Stock.TABLE_NAME +";");
        onCreate(db);
    }

    public static class Stock implements BaseColumns {
        public static String TABLE_NAME = "stocks";
        public static String _ID = BaseColumns._ID;
        public static String COMPANY_NAME = "companyName";
        public static String PRICE = "price";
    }

}
