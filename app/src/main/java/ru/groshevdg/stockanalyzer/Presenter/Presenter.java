package ru.groshevdg.stockanalyzer.Presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import java.util.ArrayList;
import ru.groshevdg.stockanalyzer.Model.Model;
import ru.groshevdg.stockanalyzer.R;
import ru.groshevdg.stockanalyzer.View.MainView;
import ru.groshevdg.stockanalyzer.Model.data.DBHelper;

public class Presenter {
    private MainView view;
    private Model model;
    private Context context;

    public Presenter(MainView view, Context context) {
        this.view = view;
        this.context = context;
        model = new Model(context);
    }

    public Presenter(Context context) {
        this.context = context;
    }

    public ListAdapter getArrayAdapter() {
        ArrayList<String> arrayList = model.getArrayList();
        return new ArrayAdapter<>(context, R.layout.custom_list_item, R.id.text, arrayList);
    }

    public ArrayList<String> getListToSave() {
        return model.getArrayList();
    }

    public void parse() {
        model.loadAndSaveData("https://financialmodelingprep.com/api/v3/stock/actives", context);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    || (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))) {
                return true;
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public SQLiteDatabase getDatabase() {
        return new DBHelper(context).getReadableDatabase();
    }
}
