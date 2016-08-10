package com.codeforsociety.coolies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;


public class SearchResults extends MainActivity {
    TextView tvRes;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        setActionBar(new Toolbar(this));
        lv = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resultsToDisplayArrayList);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            // argument position gives the index of item which is clicked
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                String subUrl = resultsToDisplayArrayList.get(position);
                int rel = (int) urlDatabase.get(urlToID.get(subUrl)).get(1);
                urlDatabase.get(urlToID.get(subUrl)).set(1,rel+1);
                setUrl(subUrl);
                Intent intent = new Intent(getBaseContext(), WikiPage.class);
                startActivity(intent);
            }
        });
        tvRes  = (TextView) findViewById(R.id.resultText);
        tvRes.setText(resText);

    }

    public void setUrl(String s){
        url = "https://en.wikipedia.org/wiki/"+s;
    }
    }

