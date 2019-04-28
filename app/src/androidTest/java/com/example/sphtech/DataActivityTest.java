package com.example.sphtech;

import android.content.DialogInterface;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DataActivityTest extends AppCompatActivity {
    private List<YearData> ydata = new ArrayList<YearData>();

    @Test
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

    @Test
    public void checkData() {
        try {
            String cachedata = Util.getString(DataActivityTest.this, "data", null);
            if (cachedata != null) {
                dataProcess(cachedata);
            } else {
                getData();
            }
        }catch (Exception e){
            Log.e("checkdata error",e.getLocalizedMessage());
        }
    }

    @Test
    private void dataProcess(String data){
        try {
            JSONArray records = new JSONArray(data);
            for (int i = 0; i < records.length(); i++) {
                JSONObject leagueData = records.getJSONObject(i);
                Sqlite dataprocess = new Sqlite(DataActivityTest.this);
                dataprocess.insertdata(leagueData.getString("volume_of_mobile_data"),leagueData.getString("quarter").substring(0, 4),leagueData.getString("quarter").substring(5, 7),i);
            }
            displayData();
        }catch (Exception e){
            Log.e("dataProcess",e.getLocalizedMessage());
        }
    }

    @Test
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
                                    Util.setString(DataActivityTest.this,"data",records.toString());
                                    dataProcess(Util.getString(DataActivityTest.this,"data",null));
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

    @Test
    public void displayData(){
        List<Map<String, String>> getData = new Sqlite(DataActivityTest.this).getdata();
        ListView listView = (ListView) findViewById(R.id.lvList);
        for (Map<String, String> showdata : getData){
            String year = "Year: " + showdata.get("year");
            String value = "Volume of mobile data: " + showdata.get("value");
            YearData data = new YearData();
            data.setYear(year);
            data.setValue(value);
            ydata.add(data);
            YearAdapter adapter = new YearAdapter(DataActivityTest.this,
                    R.layout.year, ydata);
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                YearData yd = ydata.get(position);
                List<Map<String, String>> geteachquarterdatabyyear = new Sqlite(DataActivityTest.this).geteachquarterdatabyyear(yd.getYear().substring(6,10));
                String finalresult = "";
                for (Map<String, String> dialogdata : geteachquarterdatabyyear){
                    String year = "Year: " + dialogdata.get("year") + "\n";
                    String quarter = "Quarter: " + dialogdata.get("quarter") + "\n";
                    String value = "Volume of mobile data: " + dialogdata.get("value") + "\n\n";

                    finalresult = finalresult + year + quarter + value;

                }
                AlertDialog.Builder builder2 = new AlertDialog.Builder(DataActivityTest.this);
                builder2.setMessage(finalresult)
                        .setTitle("Quarter data by year");

                builder2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
            }
        });
    }
}