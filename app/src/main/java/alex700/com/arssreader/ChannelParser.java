package alex700.com.arssreader;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Алексей on 07.01.2015.
 */
public class ChannelParser extends DefaultHandler {
    private static Map<String, Tag> items = new HashMap<>();

    static {
        items.put("feed", Tag.CHANNEL);
        items.put("channel", Tag.CHANNEL);
        items.put("item", Tag.ITEM);
        items.put("entry", Tag.ITEM);
        items.put("link", Tag.LINK);
        items.put("atom:link", Tag.LINK);
        items.put("title", Tag.TITLE);
        items.put("description", Tag.DESCRIPTION);
        items.put("subtitle", Tag.DESCRIPTION);
    }

    private Channel channel = new Channel();
    private Stack<Tag> tags = null;
    private boolean inItem = false;
    private String dateString = null;

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//        Log.d("PARSING", qName);
        tags.push(items.get(qName) == null ? Tag.ANOTHER : items.get(qName));
        if (tags.peek().equals(Tag.ITEM)) {
            inItem = true;

        } else if (tags.peek().equals(Tag.LINK)) {
//            Log.d("PARSING", qName);
//            Log.d("PARSING", (attributes.getValue("href") != null) + " " + (attributes.getValue("type") != null ?  attributes.getValue("type").equals("application/atom+xml"):"null") + " " + inItem);
            if (attributes.getValue("href") != null
                    && !inItem
                    && attributes.getValue("type") != null
                    && attributes.getValue("type").contains("+xml")) {
                String url = attributes.getValue("href");
                Log.d("PARSING", url);
                if (url.endsWith(" %>")) {
                    url = url.substring(0, url.indexOf(" %>"));
                }
                channel.setUrl(url);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        Tag lastTag = tags.pop();
        if (lastTag.equals(Tag.ITEM)) {
            inItem = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!inItem) {
            if (tags.peek().equals(Tag.LINK)) {
            } else if (tags.peek().equals(Tag.TITLE)) {
                channel.setName(new String(ch, start, length));
            } else if (tags.peek().equals(Tag.DESCRIPTION)) {
                if (channel.getDescription() != null) {
                    channel.setDescription(channel.getDescription() + new String(ch, start, length));
                } else {
                    channel.setDescription(new String(ch, start, length));
                }
            }
        }
    }

    @Override
    public void startDocument() throws SAXException {
        Log.v("parsing", "start parsing");
        tags = new Stack<>();
    }

    @Override
    public void endDocument() throws SAXException {
        Log.v("parsing", "stop parsing");
    }

    private enum Tag {ITEM, LINK, TITLE, DESCRIPTION, PUBDATE, CHANNEL, ANOTHER}

}
