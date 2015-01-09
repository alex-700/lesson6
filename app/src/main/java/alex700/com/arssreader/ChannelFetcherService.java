package alex700.com.arssreader;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import org.apache.http.entity.ContentProducer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Алексей on 07.01.2015.
 */
public class ChannelFetcherService extends IntentService {
    public static final String SERVICE_NAME = ChannelFetcherService.class.getName();

    public static final String CHANNEL_URL = "channel_name";

    public static final int UPDATED = 0;
    public static final int UPDATING = 2;
    public static final int ERROR = 3;
    private static final List<String> tasks = new ArrayList<>();
    private static Handler handler;

    public ChannelFetcherService() {
        super(SERVICE_NAME);
    }

    public static void loadChannel(Context context, String url) {
        context.startService(new Intent(context, ChannelFetcherService.class).putExtra(CHANNEL_URL, url));
    }


    public static void setHandler(Handler handler) {
        ChannelFetcherService.handler = handler;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        String url = intent.getStringExtra(CHANNEL_URL);
        if (!tasks.contains(url)) {
            tasks.add(url);
            super.onStart(intent, startId);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra(CHANNEL_URL);
        Log.d("ChannelService", "start for " + url);
        Cursor cursor = getApplication().getContentResolver()
                .query(FeedContentProvider.CHANNEL_CONTENT_URI, null, FeedDatabaseHelper.CHANNEL_URL + "='" + url + "'", null, null);
        cursor.moveToNext();
        if (!cursor.isAfterLast()) {
            tasks.remove(0);
            return;
        }

        if (handler != null) {
            handler.obtainMessage(UPDATING).sendToTarget();
        }

        Channel channel = loadChannel(url);
        if (channel == null) {
            handler.obtainMessage(ERROR).sendToTarget();
        } else {
            if (update(channel)) {
                if (handler != null) {
                    handler.obtainMessage(UPDATED).sendToTarget();
                }
            } else {
                if (handler != null) {
                    handler.obtainMessage(ERROR).sendToTarget();
                }
            }
        }
        tasks.remove(0);
    }

    private boolean update(Channel channel) {
        ContentValues cv = channel.getContentValues();
        getContentResolver().insert(FeedContentProvider.CHANNEL_CONTENT_URI, cv);
        return true;
    }

    public static Channel loadChannel(String url) {
        return ChannelFetcher.fetch(url);
    }
}
