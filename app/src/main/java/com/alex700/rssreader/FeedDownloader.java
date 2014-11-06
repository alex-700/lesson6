package com.alex700.rssreader;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Алексей on 20.10.2014.
 */
public class FeedDownloader extends AsyncTask<Void, Void, Boolean> {
    private String url = null;
    private List<Feed> listOfFeeds = null;
    private MyFeedAdapter adapter;
    private Context context;

    public FeedDownloader(String url, List<Feed> listOfFeeds, MyFeedAdapter adapter, Context context) {
        super();
        this.url = url;
        this.listOfFeeds = listOfFeeds;
        this.adapter = adapter;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL mainUrl = new URL(url);
            URLConnection connection = mainUrl.openConnection();
            Reader reader = new InputStreamReader(connection.getInputStream(), "windows-1251");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            FeedParser feeds = new FeedParser();
            parser.parse(new InputSource(reader), feeds);
            listOfFeeds.addAll(feeds.getData());
            Log.v("parsing",""+listOfFeeds.size());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT);
        }
    }
}
