package ru.groshevdg.stockanalyzer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ru.groshevdg.stockanalyzer.data.DBHelper;

public class LoadAndParseData extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String[] selectionForCheckDataSaved = new String[] {DBHelper.Stock.COMPANY_NAME};
    private boolean hasSaved = false;
    private int countNonSavedData = 0;

    public LoadAndParseData(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Map<String, String> stocks = parseJson(getJsonObject());
        saveDataInDB(stocks);

        return null;
    }

    @Nullable
    private JSONObject getJsonObject() {
        JSONObject jsonObject = null;
        String line;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL("https://financialmodelingprep.com/api/v3/stock/actives");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            jsonObject = new JSONObject(response.toString());

            inputStream.close();
            reader.close();
            connection.disconnect();
        }
        catch (MalformedURLException e ) {
            Log.d("Load", "Url exception");
        }
        catch (IOException e) {
            Log.d("Load", "I/O exception");
        }
        catch (JSONException e ) {
            Log.d("Load", "Parse JSON exception");
        }
        catch (Exception e) {
            Log.d("Load", "Unknown exception");
        }

        return jsonObject;
    }

    private Map<String, String> parseJson(JSONObject loadedJSON) {

        if (loadedJSON == null) return new HashMap<>();

        Map<String, String> companies = new HashMap<>();
        try {
            JSONArray jsonArray = loadedJSON.getJSONArray("mostActiveStock");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject currentObject = jsonArray.getJSONObject(i);
                String companyName = currentObject.getString("companyName");
                String price = currentObject.getString("price");

                companies.put(companyName, price);
            }
        }
        catch (JSONException e) {
            Log.d("Load", "Parse JSON exception");
        }
        return companies;
    }

    private void saveDataInDB(Map<String, String> map) {
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        countNonSavedData = 0;

        for (Map.Entry<String, String> entry: map.entrySet()) {
            ContentValues contentValues = new ContentValues();

            String companyName = entry.getKey();
            String price = entry.getValue();

            if (isDataSaved(db, companyName)) {
                continue;
            } else {
                countNonSavedData++;

                contentValues.put(DBHelper.Stock.COMPANY_NAME, companyName);
                contentValues.put(DBHelper.Stock.PRICE, price);

                db.insert(DBHelper.Stock.TABLE_NAME, null, contentValues);
            }
        }
        db.close();
    }

    private boolean isDataSaved(SQLiteDatabase db, String currentCompanyName) {
        Cursor cursor = db.query(DBHelper.Stock.TABLE_NAME, selectionForCheckDataSaved,
                null, null, null, null, null);
        int savedCompanyNameIndex = cursor.getColumnIndex(DBHelper.Stock.COMPANY_NAME);

        while (cursor.moveToNext()) {
            String savedCompanyName = cursor.getString(savedCompanyNameIndex);

            if (savedCompanyName.equals(currentCompanyName)) {
                hasSaved = true;
                break;
            } else {
                hasSaved = false;
            }
        }
        cursor.close();
        return hasSaved;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (countNonSavedData != 0) {
            Toast.makeText(context, context.getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, context.getResources().getString(R.string.nothing_to_parse), Toast.LENGTH_SHORT).show();

        }
        super.onPostExecute(aVoid);
    }
}
