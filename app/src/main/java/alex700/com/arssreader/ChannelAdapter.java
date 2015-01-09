package alex700.com.arssreader;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Алексей on 06.01.2015.
 */
public class ChannelAdapter extends BaseAdapter {

    private List<Channel> data;

    public void clear() {
        data.clear();
    }

    public void add(Channel c) {
        data.add(c);
    }

    public ChannelAdapter() {
        data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Channel getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        if (view == null) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.channel_layout, viewGroup, false);
        } else {
            v = view;
        }
        Channel current = getItem(i);
        TextView textView = (TextView) v.findViewById(R.id.channel_name);
        textView.setText(current.getName());

        textView = (TextView) v.findViewById(R.id.channel_description);
        textView.setText(current.getDescription());

        if (i % 2 == 0) {
            v.setBackgroundColor(0x0F000000);
        } else {
            v.setBackgroundColor(Color.WHITE);
        }
        return v;
    }
}
