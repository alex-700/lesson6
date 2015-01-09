package alex700.com.arssreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.Date;

/**
 * Created by Алексей on 30.11.2014.
 */
public class FeedDatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String DB_NAME = "rssreader.db";
    public static final int DB_VERSION = 1;

    public static final String CHANNEL_TABLE_NAME = "channels";
    public static final String CHANNEL_ID = _ID;
    public static final String CHANNEL_NAME = "name";
    public static final String CHANNEL_URL = "url";
    public static final String CHANNEL_DESCRIPTION = "description";
    public static final String CHANNEL_DATE = "date";
    public static final String CHANNEL_ENCODING = "encoding";

    public static final String CREATE_CHANNEL_TABLE = "create table " +
            CHANNEL_TABLE_NAME + " ( " +
            CHANNEL_ID + " integer primary key autoincrement, " +
            CHANNEL_NAME + " text, " +
            CHANNEL_URL + " text, " +
            CHANNEL_DESCRIPTION + " text, " +
            CHANNEL_DATE + " integer, "+
            CHANNEL_ENCODING + " text)";

    public static final String FEED_TABLE_NAME = "feeds";
    public static final String FEED_ID = _ID;
    public static final String FEED_LINK = "link";
    public static final String FEED_TITLE = "title";
    public static final String FEED_DESCRIPTION = "description";
    public static final String FEED_PUBDATE = "pubdate";
    public static final String FEED_CHANNEL_ID = "channel_id";

    public static final String CREATE_FEED_TABLE = "create table " +
            FEED_TABLE_NAME + " ( " +
            FEED_ID + " integer primary key autoincrement, " +
            FEED_LINK + " text, " +
            FEED_TITLE + " text, " +
            FEED_DESCRIPTION + " text, " +
            FEED_PUBDATE + " integer, " +
            FEED_CHANNEL_ID + " integer)";




    public FeedDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CHANNEL_TABLE);
        sqLiteDatabase.execSQL(CREATE_FEED_TABLE);

        ContentValues cv = new ContentValues();
        cv.put(CHANNEL_NAME, "Bash");
        cv.put(CHANNEL_DESCRIPTION, "Цитатник рунета.");
        cv.put(CHANNEL_URL, "http://bash.im/rss/");
        cv.put(CHANNEL_DATE, 0);
        cv.put(CHANNEL_ENCODING, "windows-1251");
        sqLiteDatabase.insert(CHANNEL_TABLE_NAME, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("drop table " + CHANNEL_TABLE_NAME);
        sqLiteDatabase.execSQL("drop table " + FEED_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public static class ChannelCursor extends CursorWrapper {
        private Cursor cursor;

        public ChannelCursor(Cursor cursor) {
            super(cursor);
            this.cursor = cursor;
        }

        public static Channel getChannel(Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndex(CHANNEL_ID));
            String name = cursor.getString(cursor.getColumnIndex(CHANNEL_NAME));
            String description = cursor.getString(cursor.getColumnIndex(CHANNEL_DESCRIPTION));
            String url = cursor.getString(cursor.getColumnIndex(CHANNEL_URL));
            Date date = new Date(cursor.getLong(cursor.getColumnIndex(CHANNEL_DATE)));
            String encoding = cursor.getString(cursor.getColumnIndex(CHANNEL_ENCODING));
            return new Channel(name, description, url, id, date, encoding);
        }

        public Channel getChannel() {
            return getChannel(cursor);
        }
    }

    public static class FeedCursor extends CursorWrapper {
        private Cursor cursor;

        public FeedCursor(Cursor cursor) {
            super(cursor);
            this.cursor = cursor;
        }

        public static Feed getFeed(Cursor cursor) {
            return new Feed(cursor.getString(cursor.getColumnIndex(FEED_LINK)),
                    cursor.getString(cursor.getColumnIndex(FEED_TITLE)),
                    cursor.getString(cursor.getColumnIndex(FEED_DESCRIPTION)),
                    new Date(cursor.getLong(cursor.getColumnIndex(FEED_PUBDATE))),
                    cursor.getInt(cursor.getColumnIndex(FEED_CHANNEL_ID)));
        }

        public Feed getFeed() {
            return getFeed(cursor);
        }
    }
}
