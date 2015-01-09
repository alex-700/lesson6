package alex700.com.arssreader;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Алексей on 20.10.2014.
 */
public class FeedParser extends DefaultHandler {

    private enum Tag {ITEM, LINK, TITLE, DESCRIPTION, PUBDATE, ANOTHER};

    private static Map<String, Tag> items = new HashMap<>();
    static {
        items.put("item", Tag.ITEM);
        items.put("entry", Tag.ITEM);
        items.put("link", Tag.LINK);
        items.put("link", Tag.LINK);
        items.put("title", Tag.TITLE);
        items.put("title", Tag.TITLE);
        items.put("description", Tag.DESCRIPTION);
        items.put("summary", Tag.DESCRIPTION);
        items.put("pubDate", Tag.PUBDATE);
        items.put("published", Tag.PUBDATE);
    }

    private List<Feed> data = null;
    private Feed currentFeed = null;
    private Stack<Tag> tags = null;
    private boolean inItem = false;
    private String dateString = null;
    private boolean findLink = false;

    public List<Feed> getData() {
        return data;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tags.push(items.get(qName) == null ? Tag.ANOTHER : items.get(qName));
        if (tags.peek().equals(Tag.ITEM)) {
            currentFeed = new Feed();
            inItem = true;
        } else if (tags.peek().equals(Tag.LINK) && attributes.getValue("href") != null && inItem){
            currentFeed.setLink(attributes.getValue("href"));
            findLink = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        Tag lastTag = tags.pop();
        if (lastTag.equals(Tag.ITEM)) {
            data.add(currentFeed);
            inItem = false;
            findLink = false;
        } else if (lastTag.equals(Tag.PUBDATE)) {
            currentFeed.setPubDate(dateString);
            dateString = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inItem) {
            if (tags.peek().equals(Tag.LINK)) {
                if (!findLink) currentFeed.setLink(new String(ch, start, length));
            } else if (tags.peek().equals(Tag.TITLE)) {
                if (currentFeed.getTitle() != null) {
                    currentFeed.setTitle(currentFeed.getTitle() + new String(ch, start, length));
                } else {
                    currentFeed.setTitle(new String(ch, start, length));
                }
            } else if (tags.peek().equals(Tag.DESCRIPTION)) {
                if (currentFeed.getDescription() != null) {
                    currentFeed.setDescription(currentFeed.getDescription() + new String(ch, start, length));
                } else {
                    currentFeed.setDescription(new String(ch, start, length));
                }
            } else if (tags.peek().equals(Tag.PUBDATE)) {
                if (dateString != null) {
                    dateString += new String(ch, start, length);
                } else {
                    dateString = new String(ch, start, length);
                }
            }
        }
    }

    @Override
    public void startDocument() throws SAXException {
        Log.v("parsing", "start parsing");
        data = new ArrayList<>();
        tags = new Stack<>();
    }

    @Override
    public void endDocument() throws SAXException {
        Log.v("parsing", "stop parsing");
    }

}
