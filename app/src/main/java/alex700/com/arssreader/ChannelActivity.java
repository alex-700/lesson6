package alex700.com.arssreader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Date;


public class ChannelActivity extends ActionBarActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {
    public static final String CHANNEL_NAME = "channel_name";
    public static final String CHANNEL_DESCRIPTION = "channel_description";
    public static final String CHANNEL_URL = "channel_url";
    public static final String CHANNEL_ID = "channel_id";
    public static final String CHANNEL_DATE = "date";
    public static final String CHANNEL_ENCODING = "encoding";
    private ListView feedList;
    private FeedAdapter adapter;
    private Channel channel;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        feedList = (ListView) findViewById(R.id.feed_list);
        adapter = new FeedAdapter();
        feedList.setAdapter(adapter);
        channel = new Channel(getIntent().getStringExtra(CHANNEL_NAME),
                getIntent().getStringExtra(CHANNEL_DESCRIPTION),
                getIntent().getStringExtra(CHANNEL_URL),
                getIntent().getIntExtra(CHANNEL_ID, 0),
                new Date(getIntent().getLongExtra(CHANNEL_DATE, 0)),
                getIntent().getStringExtra(CHANNEL_ENCODING));
        Log.d("CHANNEL", "onCreate");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(channel.getName());
        }
        Log.d("CHANNEL", "channel id = " + channel.getId());
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case FeedFetcherService.UPDATING:
                        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                        if (actionBar != null) {
                            actionBar.setSubtitle("updating...");
                        }
                        break;
                    case FeedFetcherService.UPDATED:
                        getLoaderManager().restartLoader(2223, null, ChannelActivity.this);
                        actionBar = getSupportActionBar();
                        if (actionBar != null) {
                            actionBar.setSubtitle(null);
                        }
                        break;
                    case FeedFetcherService.ALREADY_UPDATED:
                        Toast.makeText(ChannelActivity.this, "Already updated", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        FeedFetcherService.setHandler(handler);
        feedList.setOnItemClickListener(this);

        FeedFetcherService.loadChannel(this, channel.getName());
        getLoaderManager().restartLoader(2221, null, this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            Log.d("MENU", "refresh");
            FeedFetcherService.loadChannel(this, channel.getName());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                FeedContentProvider.FEED_CONTENT_URI,
                null,
                FeedDatabaseHelper.FEED_CHANNEL_ID + " = " + channel.getId(),
                null,
                FeedDatabaseHelper.FEED_PUBDATE + " asc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            adapter.clear();
            while (data.moveToNext()) {
                adapter.add(FeedDatabaseHelper.FeedCursor.getFeed(data));
            }
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        FeedFetcherService.setHandler(handler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FeedFetcherService.setHandler(null);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(null);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("CLICK_ON_FEED", "" + position);
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("url", adapter.getItem(position).getLink());
        startActivity(intent);
    }


}
