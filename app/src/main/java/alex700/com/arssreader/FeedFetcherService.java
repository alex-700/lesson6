package alex700.com.arssreader;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Алексей on 07.01.2015.
 */
public class FeedFetcherService extends IntentService {
    public static final String SERVICE_NAME = FeedFetcherService.class.getName();

    public static final String CHANNEL_NAME = "CHANNEL_NAME";

    public static final int UPDATED = 0;
    public static final int ALREADY_UPDATED = 1;
    public static final int UPDATING = 2;
    public static final int ERROR = 3;
    public static final long UPDATE_INTERVAL = 10L * 1000L; // 10 seconds
    private static final List<String> tasks = new ArrayList<>();
    private static Handler handler;

    public FeedFetcherService() {
        super(SERVICE_NAME);
    }

    public static void loadChannel(Context context, String channel) {
        context.startService(new Intent(context, FeedFetcherService.class).putExtra(CHANNEL_NAME, channel));
    }


    public static boolean isLoading(String channel) {
        return tasks.contains(channel);
    }

    public static void setHandler(Handler handler) {
        FeedFetcherService.handler = handler;
    }

    public static List<Feed> loadFeedsInChannel(Channel channel) {
        return FeedFetcher.fetch(channel);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        String channelName = intent.getStringExtra(CHANNEL_NAME);
        if (!tasks.contains(channelName)) {
            tasks.add(channelName);
            super.onStart(intent, startId);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String channelName = intent.getStringExtra(CHANNEL_NAME);
        Log.d("service", "start for " + channelName);
        Cursor cursor = getApplication().getContentResolver()
                .query(FeedContentProvider.CHANNEL_CONTENT_URI, null, FeedDatabaseHelper.CHANNEL_NAME + "='" + channelName + "'", null, null);
        cursor.moveToNext();
        if (cursor.isAfterLast()) {
            tasks.remove(0);
            return;
        }

        Channel channel = FeedDatabaseHelper.ChannelCursor.getChannel(cursor);
        if (alreadyUpdated(channel)) {
            tasks.remove(0);
            if (handler != null) {
                handler.obtainMessage(ALREADY_UPDATED).sendToTarget();
            }
            return;
        }
        Log.d("service", "channel id " + channel.getId());
        if (handler != null) {
            Log.d("HANDLER", "send");
            handler.obtainMessage(UPDATING).sendToTarget();
        }

        List<Feed> feeds = loadFeedsInChannel(channel);

        if (update(feeds, channel)) {
            if (handler != null) {
                handler.obtainMessage(UPDATED).sendToTarget();
            }
        } else {
            if (handler != null) {
                handler.obtainMessage(ERROR).sendToTarget();
            }
        }
        tasks.remove(0);
    }

    private boolean update(List<Feed> feeds, Channel channel) {
        if (feeds == null || feeds.size() == 0) {
            return false;
        }

        getContentResolver().delete(FeedContentProvider.FEED_CONTENT_URI,
                FeedDatabaseHelper.FEED_CHANNEL_ID + " = " + channel.getId(), null);

        for (Feed data : feeds) {
            ContentValues cv = data.getContentValues();
            cv.put(FeedDatabaseHelper.FEED_CHANNEL_ID, channel.getId());
            getContentResolver().insert(FeedContentProvider.FEED_CONTENT_URI, cv);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedDatabaseHelper.CHANNEL_DATE, new Date().getTime());
        getContentResolver().update(FeedContentProvider.CHANNEL_CONTENT_URI, contentValues,
                FeedDatabaseHelper.CHANNEL_ID + " = " + channel.getId(), null);
        return true;
    }

    private boolean alreadyUpdated(Channel channel) {
        return ((new Date()).getTime() - channel.getDate().getTime()) <= UPDATE_INTERVAL;
    }
}
