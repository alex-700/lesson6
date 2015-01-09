package alex700.com.arssreader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener,
        View.OnClickListener,
        AdapterView.OnItemLongClickListener {

    Button addButton;
    EditText addEditText;
    ListView channelList;
    ChannelAdapter adapter;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = (Button) findViewById(R.id.add_channel_button);
        addEditText = (EditText) findViewById(R.id.add_channel_edit_text);
        channelList = (ListView) findViewById(R.id.channel_list);
        adapter = new ChannelAdapter();
        channelList.setAdapter(adapter);
        getLoaderManager().restartLoader(2222, null, this);
        channelList.setOnItemClickListener(this);
        channelList.setOnItemLongClickListener(this);
        addButton.setOnClickListener(this);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case ChannelFetcherService.UPDATING:
                        break;
                    case ChannelFetcherService.UPDATED:
                        getLoaderManager().restartLoader(2122, null, MainActivity.this);
                        break;
                    case ChannelFetcherService.ERROR:
                        Toast.makeText(MainActivity.this, "Wrong url", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });
        ChannelFetcherService.setHandler(handler);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FeedContentProvider.CHANNEL_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            adapter.clear();
            while (data.moveToNext()) {
                adapter.add(FeedDatabaseHelper.ChannelCursor.getChannel(data));
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ChannelActivity.class);
        intent.putExtra(ChannelActivity.CHANNEL_ID, adapter.getItem(position).getId());
        intent.putExtra(ChannelActivity.CHANNEL_NAME, adapter.getItem(position).getName());
        intent.putExtra(ChannelActivity.CHANNEL_DESCRIPTION, adapter.getItem(position).getDescription());
        intent.putExtra(ChannelActivity.CHANNEL_URL, adapter.getItem(position).getUrl());
        intent.putExtra(ChannelActivity.CHANNEL_DATE, adapter.getItem(position).getDate().getTime());
        intent.putExtra(ChannelActivity.CHANNEL_ENCODING, adapter.getItem(position).getEncoding());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        String url = addEditText.getText().toString();
        ChannelFetcherService.loadChannel(this, url);
        addEditText.setText("");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String name = adapter.getItem(position).getName();
        Cursor cursor = getContentResolver().query(FeedContentProvider.CHANNEL_CONTENT_URI, null,
                FeedDatabaseHelper.CHANNEL_NAME + " = '" + name + "'", null, null);
        cursor.moveToNext();
        Channel current;
        if (!cursor.isAfterLast()) {
            current = FeedDatabaseHelper.ChannelCursor.getChannel(cursor);
            getContentResolver().delete(FeedContentProvider.FEED_CONTENT_URI, FeedDatabaseHelper.FEED_CHANNEL_ID + " = " + current.getId(), null);
            getContentResolver().delete(FeedContentProvider.CHANNEL_CONTENT_URI, FeedDatabaseHelper.CHANNEL_ID + " = " + current.getId(), null);
            getLoaderManager().restartLoader(2121, null, this);
            return true;
        } else {
            return true;
        }
    }
}
