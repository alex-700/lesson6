package alex700.com.arssreader;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Алексей on 06.01.2015.
 */
public class FeedContentProvider extends ContentProvider {
    public static final String AUTHORITY = FeedContentProvider.class.getName();

    public static final Uri CHANNEL_CONTENT_URI = Uri.parse(
            "content://" + AUTHORITY + "/" + FeedDatabaseHelper.CHANNEL_TABLE_NAME);
    public static final Uri FEED_CONTENT_URI = Uri.parse(
            "content://" + AUTHORITY + "/" + FeedDatabaseHelper.FEED_TABLE_NAME);
    static final String CHANNEL_CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + FeedDatabaseHelper.CHANNEL_TABLE_NAME;
    static final String FEED_CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + FeedDatabaseHelper.FEED_TABLE_NAME;
    public static final int URI_CHANNEL_ID = 0;
    public static final int URI_FEED_ID = 1;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, FeedDatabaseHelper.CHANNEL_TABLE_NAME, URI_CHANNEL_ID);
        uriMatcher.addURI(AUTHORITY, FeedDatabaseHelper.FEED_TABLE_NAME, URI_FEED_ID);
    }

    private FeedDatabaseHelper dbHelper;


    @Override
    public boolean onCreate() {
        dbHelper = new FeedDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        switch (uriMatcher.match(uri)) {
            case URI_CHANNEL_ID:
                Cursor cursor = dbHelper.getReadableDatabase().query(FeedDatabaseHelper.CHANNEL_TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), CHANNEL_CONTENT_URI);
                return cursor;
            case URI_FEED_ID:
                cursor = dbHelper.getReadableDatabase().query(FeedDatabaseHelper.FEED_TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), FEED_CONTENT_URI);
                return cursor;
            default:
                throw new IllegalArgumentException("wrong URI");
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_CHANNEL_ID:
                return CHANNEL_CONTENT_TYPE;
            case URI_FEED_ID:
                return FEED_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("wrong URI");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (uriMatcher.match(uri)) {
            case URI_CHANNEL_ID:
                long id = dbHelper.getWritableDatabase().insert(FeedDatabaseHelper.CHANNEL_TABLE_NAME, null, contentValues);
                Uri result = ContentUris.withAppendedId(CHANNEL_CONTENT_URI, id);
                getContext().getContentResolver().notifyChange(result, null);
                return result;
            case URI_FEED_ID:
                id = dbHelper.getWritableDatabase().insert(FeedDatabaseHelper.FEED_TABLE_NAME, null, contentValues);
                result = ContentUris.withAppendedId(FEED_CONTENT_URI, id);
                getContext().getContentResolver().notifyChange(result, null);
                return result;
            default:
                throw new IllegalArgumentException("wrong URI");
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        switch (uriMatcher.match(uri)) {
            case URI_CHANNEL_ID:
                int count = dbHelper.getWritableDatabase().delete(FeedDatabaseHelper.CHANNEL_TABLE_NAME, s, strings);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case URI_FEED_ID:
                count = dbHelper.getWritableDatabase().delete(FeedDatabaseHelper.FEED_TABLE_NAME, s, strings);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("wrong URI");
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        switch (uriMatcher.match(uri)) {
            case URI_CHANNEL_ID:
                int count = dbHelper.getWritableDatabase().update(FeedDatabaseHelper.CHANNEL_TABLE_NAME, contentValues, s, strings);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case URI_FEED_ID:
                count = dbHelper.getWritableDatabase().update(FeedDatabaseHelper.FEED_TABLE_NAME, contentValues, s, strings);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("wrong URI");
        }
    }
}
