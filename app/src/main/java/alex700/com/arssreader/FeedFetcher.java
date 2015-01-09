package alex700.com.arssreader;

import android.net.Uri;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Алексей on 07.01.2015.
 */
public class FeedFetcher {
    public static List<Feed> fetch(Channel channel) {
        try {
            Uri uri = Uri.parse(channel.getUrl());
            URL url = new URL(uri.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            Reader reader = new InputStreamReader(inputStream, channel.getEncoding());
            InputSource inputSource = new InputSource(reader);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            FeedParser feeds = new FeedParser();
            Log.d("PARSING", channel.getEncoding());
            Log.d("PARSING", channel.getUrl());
            parser.parse(inputSource, feeds);

            return feeds.getData();

        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
