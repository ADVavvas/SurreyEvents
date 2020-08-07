package com.group19.softwareengineeringproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.group19.softwareengineeringproject.models.MapEvent;
import com.group19.softwareengineeringproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.EventViewHolder> {
    private List<MapEvent> dataSet;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView titleText;
        public TextView subText;
        public TextView distanceText;
        public TextView categoryText;
        public ImageView imageView;

        public EventViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.list_event_title_text);
            subText = itemView.findViewById(R.id.list_event_sub_text);
            distanceText = itemView.findViewById(R.id.list_event_distance_text);
            categoryText = itemView.findViewById(R.id.list_event_category_text);
            imageView = itemView.findViewById(R.id.list_event_image_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public EventRecyclerViewAdapter (List<MapEvent> dataSet) {
        this.dataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventRecyclerViewAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View rootView = inflater.inflate(R.layout.event_list_element, parent, false);

        EventViewHolder vh = new EventViewHolder(rootView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        holder.titleText.setText(dataSet.get(position).getTitle());
        holder.subText.setText(dataSet.get(position).getHost());
        holder.categoryText.setText(dataSet.get(position).getCategory());
        holder.distanceText.setText(3.2 + " miles");

        Picasso.get().load(dataSet.get(position).getImgUrl()).into(holder.imageView);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
