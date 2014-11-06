package com.alex700.rssreader;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Алексей on 20.10.2014.
 */
public class FeedParser extends DefaultHandler {

    private List<Feed> data = null;
    private Feed currentFeed = null;
    private Stack<String> tags = null;
    private boolean inItem = false;

    public List<Feed> getData() {
        return data;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tags.push(qName);
        if (qName.equals("item")) {
            currentFeed = new Feed();
            inItem = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        tags.pop();
        if (qName.equals("item")) {
            data.add(currentFeed);
            inItem = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inItem) {
            if (tags.peek().equals("link")) {
                currentFeed.setLink(new String(ch, start, length));
            } else if (tags.peek().equals("title")) {
                currentFeed.setTitle(new String(ch, start, length));
            } else if (tags.peek().equals("description")) {
                if (currentFeed.getDescription() != null) {
                    currentFeed.setDescription(currentFeed.getDescription() + new String(ch, start, length));
                } else {
                    currentFeed.setDescription(new String(ch, start, length));
                }
            } else if (tags.peek().equals("pubDate")) {
                currentFeed.setPubDate(new String(ch, start, length));
            }
        }
    }

    @Override
    public void startDocument() throws SAXException {
        Log.v("parsing", "start parsing");
        data = new ArrayList<Feed>();
        tags = new Stack<String>();
    }

    @Override
    public void endDocument() throws SAXException {
        Log.v("parsing", "stop parsing");
    }

}
