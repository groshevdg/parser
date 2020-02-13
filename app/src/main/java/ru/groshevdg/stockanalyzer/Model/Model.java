package ru.groshevdg.stockanalyzer.Model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import ru.groshevdg.stockanalyzer.Presenter.Presenter;
import ru.groshevdg.stockanalyzer.Model.data.DBHelper;

public class Model {
    private Presenter presenter;
    private ArrayList companies;
    private Context context;

    public Model(Context context) {
        this.context = context;
        presenter = new Presenter(context);
    }

    public ArrayList<String> getArrayList() {
        SQLiteDatabase database = presenter.getDatabase();
        Cursor cursor = database.query(DBHelper.Stock.TABLE_NAME, null,
                null, null, null, null, null);

        int companyNameIndex = cursor.getColumnIndex(DBHelper.Stock.COMPANY_NAME);
        int priceIndex = cursor.getColumnIndex(DBHelper.Stock.PRICE);

        companies = new ArrayList<>();

        while (cursor.moveToNext()) {
            String companyName = cursor.getString(companyNameIndex);
            String prices = cursor.getString(priceIndex);

            companies.add(companyName + "\n" + "Current price: " + prices);
        }
        return companies;
    }

    public void loadAndSaveData(String url, Context context) {
        new LoadAndParseData(context, url).execute();
    }
}

