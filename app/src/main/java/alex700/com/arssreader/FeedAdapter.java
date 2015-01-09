package alex700.com.arssreader;

import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Алексей on 14.10.2014.
 */
public class FeedAdapter extends BaseAdapter {


    private List<Feed> data;

    public void clear() {
        data.clear();
    }

    public void add(Feed feed) {
        data.add(feed);
    }

    public FeedAdapter() {
        data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Feed getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        if (view == null) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_layout, viewGroup, false);
        } else {
            v = view;
        }
        Feed current = getItem(i);
        TextView textView = (TextView) v.findViewById(R.id.title);
        textView.setText(current.getTitle());
        textView = (TextView) v.findViewById(R.id.description);
        String s = String.valueOf(Html.fromHtml(current.getDescription()));
        s = s.substring(0, Math.min(s.length(), 150)) + "...";
        textView.setText(s);
        textView = (TextView) v.findViewById(R.id.pubDate);
        textView.setText(""+current.getPubDate());
        if (i % 2 == 0) {
            v.setBackgroundColor(0x0F000000);
        } else {
            v.setBackgroundColor(Color.WHITE);
        }
        return v;
    }
}
