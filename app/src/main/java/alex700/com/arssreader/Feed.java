package alex700.com.arssreader;

import android.content.ContentValues;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Алексей on 20.10.2014.
 */
public class Feed {
    private final static DateFormat[] dateFormats = new DateFormat[]{
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US),
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    };
    private String link;
    private String title;
    private String description;
    private Date pubDate;
    private int channelId;

    public Feed() {

    }

    public Feed(String link, String title, String description, Date pubDate, int channelId) {
        this.link = link;
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.channelId = channelId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(String dateString) {
//        Log.d("PUBDATE", dateString);
        boolean ok = false;
        for (DateFormat dateFormat : dateFormats) {
            try {
                dateFormat.parse(dateString);
                ok = true;
            } catch (ParseException e) {
            }
        }
        if (!ok) {
            throw new RuntimeException("Wrong date format");
        }
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FeedDatabaseHelper.FEED_DESCRIPTION, description);
        if (pubDate == null) {
            cv.put(FeedDatabaseHelper.FEED_PUBDATE, new Date().getTime());
        } else {
            cv.put(FeedDatabaseHelper.FEED_PUBDATE, pubDate.getTime());
        }
        cv.put(FeedDatabaseHelper.FEED_TITLE, title);
        cv.put(FeedDatabaseHelper.FEED_LINK, link);
        return cv;

    }
}
