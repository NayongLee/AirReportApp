package com.api.airreport.airreportapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by 이나영 on 2017-11-08.
 */

public class CityListActivity extends Activity{

    String[] city = new String[]{"서울", "부산", "대구", "인천", "광주", "대전",
                                "울산", "경기", "강원", "충북", "충남", "전북",
                                "전남", "경북", "경남", "제주", "세종"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.city_list);
        setResult(Activity.RESULT_CANCELED);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, city);

        ListView listView = findViewById(R.id.listView1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(mClickListener);
                /*new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strText = (String) parent.getItemAtPosition(position);
            }
        });*/


    }

    private AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String strText = (String) parent.getItemAtPosition(position);
            String city = ((TextView) view).getText().toString();
            Intent intent = new Intent();
            intent.putExtra("city", city);
            setResult(Activity.RESULT_OK, intent);
//            startActivityForResult(intent, 3);
            finish();

//            startActivityForResult(intent, 3);
        }
    };
}
