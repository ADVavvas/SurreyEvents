package com.group19.softwareengineeringproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.group19.softwareengineeringproject.R;
import com.group19.softwareengineeringproject.helpers.RetrofitManager;
import com.group19.softwareengineeringproject.helpers.UserManager;
import com.group19.softwareengineeringproject.models.Society;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SocietyRecyclerViewAdapter extends RecyclerView.Adapter<SocietyRecyclerViewAdapter.SocietyViewHolder>  {
    private List<Society> societySet;

    public static class SocietyViewHolder extends RecyclerView.ViewHolder{
        public TextView socTitle;
        public ToggleButton socSubButton;

        public SocietyViewHolder(View itemView){
            super(itemView);

            socTitle = itemView.findViewById(R.id.list_society_title_text);
            socSubButton = itemView.findViewById(R.id.list_society_toggle);

        }


    }

    public SocietyRecyclerViewAdapter (List<Society> societySet) { this.societySet=societySet; }

    @Override
    public SocietyRecyclerViewAdapter.SocietyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View rootView = inflater.inflate(R.layout.society_list_element, parent, false);

        SocietyViewHolder vh = new SocietyViewHolder(rootView);
        return vh;
    }

    @Override
    public void onBindViewHolder(SocietyViewHolder holder, int position){
        holder.socTitle.setText(societySet.get(position).getTitle());

        ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        holder.socSubButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //animation
                compoundButton.startAnimation(scaleAnimation);

                HashMap<String, Object> map = new HashMap<>();


                map.put("sub", isChecked);
                //map.put("society", societySet.get(position).getId());
                map.put("user", UserManager.getInstance().getUser().getId());

                Call<Boolean> subCall = RetrofitManager.getInstance().api.sub(societySet.get(position).getId(), map);
                subCall.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                      if(response.body() == null) return;
                      holder.socSubButton.setChecked(response.body());
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });
            }
        });


        Call<Boolean> isSubbedCall = RetrofitManager.getInstance().api.isSubbed(societySet.get(position).getId(), UserManager.getInstance().getUser().getId());
        isSubbedCall.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                holder.socSubButton.setChecked(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return societySet.size();
    }
}
