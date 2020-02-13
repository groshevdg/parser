package ru.groshevdg.stockanalyzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.groshevdg.stockanalyzer.Presenter.Presenter;
import ru.groshevdg.stockanalyzer.View.MainView;

public class MainActivity extends AppCompatActivity implements MainView {
    @BindView(R.id.list_of_stocks)
    ListView stockList;

    @BindView(R.id.load_data)
    Button load_data;

    @BindView(R.id.show_data)
    Button show_data;

    private static final String ADAPTER_STATE = "adapter_of_list_view";
    private ArrayList<String> companies;
    private Presenter myPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        myPresenter = new Presenter(this, getApplicationContext());

        if (savedInstanceState != null) {
            companies = savedInstanceState.getStringArrayList(ADAPTER_STATE);
            stockList.setAdapter(new ArrayAdapter<>(this,
                    R.layout.custom_list_item, R.id.text, companies));
        }
    }

    public void showData(View view) {
        show();
    }

    public void loadAndParse(View view) {
        if (myPresenter.isNetworkAvailable()) {
            parse();
        }
        else {
            Toast.makeText(this, getResources().getString(R.string.not_available), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        companies = myPresenter.getListToSave();
        outState.putStringArrayList(ADAPTER_STATE, companies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void show() {
        stockList.setAdapter(emptyAdapter());
        stockList.setAdapter(myPresenter.getArrayAdapter());
    }

    @Override
    public void parse() {
        myPresenter.parse();
    }

    private ListAdapter emptyAdapter() {
        return new ArrayAdapter<>(this, R.layout.custom_list_item, R.id.text, new String[] {});
    }
}
