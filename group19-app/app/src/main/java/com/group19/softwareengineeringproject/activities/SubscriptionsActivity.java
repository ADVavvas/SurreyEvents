package com.group19.softwareengineeringproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import com.group19.softwareengineeringproject.R;
import com.group19.softwareengineeringproject.adapters.NonSwipeViewPager;
import com.group19.softwareengineeringproject.adapters.SocietyPagerAdapter;

public class SubscriptionsActivity extends AppCompatActivity {
  //private SocietyPagerAdapter societyPagerAdapter;
  //private NonSwipeViewPager socViewPager;
  //private TabLayout socTabLayout;
  private Button socXButton;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_subscriptions);

    socXButton=findViewById(R.id.soc_x_button);

    socXButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    //socViewPager = findViewById(R.id.subs_pager);

    //societyPagerAdapter = new SocietyPagerAdapter(getSupportFragmentManager());
    //socTabLayout=findViewById(R.id.subs_layout);

    //socViewPager.setAdapter(societyPagerAdapter);
    //socTabLayout.setupWithViewPager(socViewPager);

    try{
      //socTabLayout.getTabAt(0).setIcon(R.drawable.ic_favorite_black_24dp);
    } catch (Exception e){
      e.printStackTrace();
    }

  }
}

