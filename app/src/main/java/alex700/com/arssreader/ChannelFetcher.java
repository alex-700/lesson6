package alex700.com.arssreader;

import android.app.LoaderManager;
import android.net.Uri;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Алексей on 07.01.2015.
 */
public class ChannelFetcher {
    public static Channel fetch(String urlString) {
        try {
            Uri uri = Uri.parse(urlString);
            String uriString = uri.toString();
            if (!uriString.startsWith("http://")) {
                uriString = "http://" + uriString;
            }
            URL url = new URL(uriString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            String contentType = httpURLConnection.getContentType();
            String encoding = "utf-8";
            if (contentType.contains("charset=")) {
                int index = contentType.indexOf("charset=") + 8;
                int endIndex = index;
                while (endIndex < contentType.length() && contentType.charAt(endIndex) != ' ' &&
                        contentType.charAt(endIndex) != ';') {
                    endIndex++;
                }
                Log.d("CHANNEL_FETCHER", "encoding = " + contentType.substring(index, endIndex));
                encoding = contentType.substring(index, endIndex);
            }

            Reader reader = new InputStreamReader(inputStream, encoding);

            InputSource inputSource = new InputSource(reader);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            ChannelParser channelParser = new ChannelParser();
            parser.parse(inputSource, channelParser);
            Channel channel = channelParser.getChannel();
            channel.setEncoding(encoding);
            if (channel.getUrl() == null) {
                Log.d("PARSING", "url copies from start url");
                channel.setUrl(uriString);
            }
            return channel;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}
