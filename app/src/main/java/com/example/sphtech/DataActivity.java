package com.example.sphtech;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.sphtech.model.YearAdapter;
import com.example.sphtech.model.YearData;
import com.example.sphtech.util.HttpPostObject;
import com.example.sphtech.util.Sqlite;
import com.example.sphtech.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataActivity extends AppCompatActivity {
    private List<YearData> ydata = new ArrayList<YearData>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        checkData();

        this.findViewById(R.id.getdata).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getData();
            }
        });
    }

    public void checkData(){
        String cachedata = Util.getString(DataActivity.this,"data",null);
        if(cachedata != null){
            dataProcess(cachedata);
        }else{
            getData();
        }
    }

    public  void getData() {
        if (Util.hasConnection(this)) {
            HttpPostObject.HttpResponseCallback callback =
                    new HttpPostObject.HttpResponseCallback() {
                        @Override
                        public void onHttpResponse(HttpPostObject postObject, String result, int status) {
                            if (result != null) {
                                try {
                                    Button getdata = (Button) findViewById(R.id.getdata);
                                    if(getdata.getVisibility() == View.VISIBLE){
                                        getdata.setVisibility(View.INVISIBLE);
                                    }
                                    JSONObject response = new JSONObject(result);
                                    JSONArray records = response.getJSONObject("result").getJSONArray("records");
                                    //catch
                                    Util.setString(DataActivity.this,"data",records.toString());
                                    dataProcess(Util.getString(DataActivity.this,"data",null));
                                } catch (JSONException e) {
                                    Log.e("post", "Error getdata " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    };

            HttpPostObject post = new HttpPostObject("https://data.gov.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f", callback, "");
            Util.httpPost(post);
        }else{
            Log.e("connection","fales");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Button getdata = (Button) findViewById(R.id.getdata);
                    getdata.setVisibility(View.VISIBLE);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void dataProcess(String data){
        try {
            JSONArray records = new JSONArray(data);
            for (int i = 0; i < records.length(); i++) {
                JSONObject leagueData = records.getJSONObject(i);
                Sqlite dataprocess = new Sqlite(DataActivity.this);
                dataprocess.insertdata(leagueData.getString("volume_of_mobile_data"),leagueData.getString("quarter").substring(0, 4),leagueData.getString("quarter").substring(5, 7),i);
            }
            displayData();
        }catch (Exception e){
            Log.e("dataProcess",e.getLocalizedMessage());
        }
    }

    public void displayData(){
        List<Map<String, String>> getData = new Sqlite(DataActivity.this).getdata();
        ListView listView = (ListView) findViewById(R.id.lvList);
        for (Map<String, String> showdata : getData){
            String year = "Year: " + showdata.get("year");
            String value = "Volume of mobile data: " + showdata.get("value");
            YearData data = new YearData();
            data.setYear(year);
            data.setValue(value);
            ydata.add(data);
            YearAdapter adapter = new YearAdapter(DataActivity.this,
                    R.layout.year, ydata);
            listView.setAdapter(adapter);
            Log.e("displaydata","display : " + year + " " + value);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                YearData yd = ydata.get(position);
                Log.e("onclick"," data: " + yd.getValue());
            }
        });
    }
}

