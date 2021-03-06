package ru.groshevdg.stockanalyzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.groshevdg.stockanalyzer.data.DBHelper;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.list_of_stocks)
    ListView stockList;

    @BindView(R.id.load_data)
    Button load_data;

    @BindView(R.id.show_data)
    Button show_data;

    private static final String ADAPTER_STATE = "adapter_of_list_view";
    private ArrayList<String> companies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            companies = savedInstanceState.getStringArrayList(ADAPTER_STATE);
            stockList.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, companies));
        }
    }

    public void loadAndParse(View view) {
        new LoadAndParseData(getApplicationContext()).execute();
    }

    public void showData(View view) {
        stockList.setAdapter(emptyAdapter());
        ListAdapter adapter = getAdapter();
        stockList.setAdapter(adapter);
    }

    private ListAdapter getAdapter() {
        SQLiteDatabase database = new DBHelper(getApplicationContext()).getReadableDatabase();
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

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, companies);
        database.close();
        cursor.close();
        return adapter;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putStringArrayList(ADAPTER_STATE, companies);
        super.onSaveInstanceState(outState);
    }

    private ListAdapter emptyAdapter() {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[] {});
    }
}
