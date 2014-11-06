package com.alex700.rssreader;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends ListActivity {
    private Button button;
    private EditText editText;
    private ListView listView;
    private List<Feed> listOfFeeds;
    private MyFeedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(android.R.id.list);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listOfFeeds = new ArrayList<Feed>();
                adapter = new MyFeedAdapter(listOfFeeds);
                listView.setAdapter(adapter);
                (new FeedDownloader(editText.getText().toString(), listOfFeeds, adapter, getBaseContext())).execute();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyActivity.this, WebActivity.class);
                intent.putExtra("url", ((Feed) getListView().getAdapter().getItem(i)).getLink());
                startActivity(intent);
            }
        });

    }


}
