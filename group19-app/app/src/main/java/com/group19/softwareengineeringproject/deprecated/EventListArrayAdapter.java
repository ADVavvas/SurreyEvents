package com.group19.softwareengineeringproject.deprecated;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.group19.softwareengineeringproject.models.MapEvent;
import com.group19.softwareengineeringproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventListArrayAdapter extends ArrayAdapter<MapEvent> {

    private final Activity context;
    private ArrayList<MapEvent> dataSet;

    //TODO: check if useful
    // View lookup cache
    private static class ViewHolder {
        TextView mEventTitleText;
        TextView mEventSubText;
        TextView mEventCategoryText;
        TextView mEventDistanceText;
        ImageView mEventImageView;
    }

    public EventListArrayAdapter(ArrayList<MapEvent> data, Activity context) {
        super(context, R.layout.event_list_element, data);
        this.dataSet = data;
        this.context = context;

    }

    @Override
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View event_list_item = inflater.inflate(R.layout.event_list_element, null,true);

        TextView mEventTitleText = event_list_item.findViewById(R.id.list_event_title_text);
        TextView mEventSubText = event_list_item.findViewById(R.id.list_event_sub_text);
        TextView mEventCategoryText = event_list_item.findViewById(R.id.list_event_category_text);
        TextView mEventDistanceText = event_list_item.findViewById(R.id.list_event_distance_text);
        ImageView mEventImageView = event_list_item.findViewById(R.id.list_event_image_view);

        mEventTitleText.setText(dataSet.get(position).getTitle());
        mEventSubText.setText(dataSet.get(position).getHost());
        mEventCategoryText.setText(dataSet.get(position).getCategory());
        mEventDistanceText.setText(3.2 + " miles");

        Picasso.get().load(dataSet.get(position).getImgUrl()).into(mEventImageView);

        return event_list_item;
    };
}
