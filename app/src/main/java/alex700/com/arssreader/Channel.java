package alex700.com.arssreader;

import android.content.ContentValues;

import java.util.Date;

/**
 * Created by Алексей on 06.01.2015.
 */
public class Channel {
    private String name;
    private String description;
    private String url;
    private Date date;
    private String encoding;
    private int id;

    public Channel() {}

    public Channel(String name, String description, String url, int id, Date date, String encoding) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.id = id;
        this.date = date;
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FeedDatabaseHelper.CHANNEL_NAME, name);
        cv.put(FeedDatabaseHelper.CHANNEL_DESCRIPTION, description);
        cv.put(FeedDatabaseHelper.CHANNEL_DATE, 0);
        cv.put(FeedDatabaseHelper.CHANNEL_ENCODING, encoding);
        cv.put(FeedDatabaseHelper.CHANNEL_URL, url);
        return cv;
    }
}
