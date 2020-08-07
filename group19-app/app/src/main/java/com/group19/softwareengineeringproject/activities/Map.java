package com.group19.softwareengineeringproject.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.group19.softwareengineeringproject.adapters.NonSwipeViewPager;
import com.group19.softwareengineeringproject.adapters.EventPagerAdapter;
import com.group19.softwareengineeringproject.helpers.EventViewModel;
import com.group19.softwareengineeringproject.helpers.RetrofitManager;
import com.group19.softwareengineeringproject.helpers.UserManager;
import com.group19.softwareengineeringproject.models.MapEvent;
import com.group19.softwareengineeringproject.helpers.MarkerFactory;
import com.group19.softwareengineeringproject.R;
import com.group19.softwareengineeringproject.models.Story;
import com.group19.softwareengineeringproject.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Map extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private List<Target> targetList;
  private EventPagerAdapter eventPagerAdapter;
  private NonSwipeViewPager eventViewPager;
  private TabLayout eventTabLayout;
  private FusedLocationProviderClient mFusedLocationProviderClient;
  private LocationCallback locationCallback;
  private LocationRequest locationRequest;
  private User user;

  private EventViewModel model;

  private Marker mUserMarker;
  private Marker mNewEventMarker;


  private LatLng mDefaultLocation = new LatLng(51.242126, -0.590540);
  private Location mLastKnownLocation;

  private FloatingActionButton fab;
  private boolean mLocationPermissionGranted = false;
  private boolean requestingLocationUpdates = true;

  static final int REQUEST_IMAGE_CAPTURE = 12;
  static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3;
  static final int REQUEST_CHECK_SETTINGS = 4;
  static final int DEFAULT_ZOOM = 16;
  static final int CREATE_EVENT_TAG = 144;
  static final int USER_MARKER_TAG = 288;
  static final String TAG = "maps debug";

  //TODO: redo Markers with ViewModel
  //TODO: check if EventFragment can be single class with 3 instances
  //TODO: hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    targetList = new ArrayList<>();

    fab = findViewById(R.id.fab);

    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getBaseContext(), SubscriptionsActivity.class);
        startActivity(intent);
      }
    });

    eventViewPager = findViewById(R.id.bottom_sheet_pager);

    eventPagerAdapter = new EventPagerAdapter(getSupportFragmentManager());
    eventTabLayout = findViewById(R.id.tab_layout);

    eventViewPager.setAdapter(eventPagerAdapter);
    eventTabLayout.setupWithViewPager(eventViewPager);

    try {
      eventTabLayout.getTabAt(0).setIcon(R.drawable.ic_whatshot_black_24dp);
      //eventTabLayout.getTabAt(1).setIcon(R.drawable.ic_star_border_black_24dp);
      eventTabLayout.getTabAt(1).setIcon(R.drawable.ic_favorite_black_24dp);
    } catch(Exception e) {
      e.printStackTrace();
    }


    // Construct a GeoDataClient.
    //mGeoDataClient = Places.getGeoDataClient(this, null);

    // Construct a PlaceDetectionClient.
    //mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

    // Construct a FusedLocationProviderClient.
    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
          return;
        }
        Log.d("location tag", "got location");
        for (Location location : locationResult.getLocations()) {
          if(mUserMarker == null) {
            Drawable circleDrawable = getResources().getDrawable(R.drawable.user_location_marker);

            mUserMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zIndex(999999999.0f)
                    .rotation(location.getBearing())
                    .icon(MarkerFactory.createUserMarker(circleDrawable)));
            mUserMarker.setTag(USER_MARKER_TAG);
          } else {
            mUserMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
          }
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
      };
    };

    user = UserManager.getInstance().getUser();
  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
      @Override
      public void onMapLongClick(LatLng latLng) {
        if(!user.getType().equals("society")) return;
        if(mNewEventMarker != null) {
          mNewEventMarker.remove();
        }
        mNewEventMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Create new event"));
        mNewEventMarker.setTag(CREATE_EVENT_TAG);
      }
    });

    try {
      // Customise the styling of the base map using a JSON object defined
      // in a raw resource file.
      boolean success = mMap.setMapStyle(
              MapStyleOptions.loadRawResourceStyle(
                      this, R.raw.map_style));

      if (!success) {
        Log.e("maps debug", "Style parsing failed.");
      }

    } catch (Resources.NotFoundException e) {
      Log.e("maps debug", "Can't find style. Error: ", e);
    }

    LatLng guildford = new LatLng(51.242126, -0.590540);

    mMap.setMinZoomPreference(15.0f);
    mMap.setMaxZoomPreference(16.0f);

    mMap.moveCamera(CameraUpdateFactory.newLatLng(guildford));
    mMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));

    mMap.setOnMarkerClickListener(marker -> {
      if(marker.getTag().getClass() == Integer.class) {
        if((int)marker.getTag() == CREATE_EVENT_TAG) {
          launchCreateEvent(marker.getPosition());
          return true;
        } else if ((int)marker.getTag() == USER_MARKER_TAG) {
          return true;
        }
      }
      launchEventDetails((String)marker.getTag());
      return true;
    });


    model = ViewModelProviders.of(this).get(EventViewModel.class);
    model.getEvents().observe(this, events -> {
      targetList.clear();
      for(MapEvent e : events) {

        Target target = new Target() {
          @Override
          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d("picasso", "succ");
            addMarker(bitmap, e.getLocation(), e.getTitle(), e.getId());
          }

          @Override
          public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            Log.d("picasso", "fail");
          }

          @Override
          public void onPrepareLoad(Drawable placeHolderDrawable) {

          }
        };

        targetList.add(target);
        Picasso.get().load(e.getImgUrl()).into(target);
      }

    });

    updateLocationUI();
    //getDeviceLocation();
    createLocationRequest();


  }

  public void addMarker(Bitmap thumbnail, LatLng position, String title, String id) {
    if(mMap == null) return;

    Marker marker = mMap.addMarker(new MarkerOptions()
            .icon(MarkerFactory.bitmapDescriptorFromVector(
                    this,
                    R.drawable.event_marker_basic,
                    thumbnail))
            .position(position)
            .title(title));
    marker.setTag(id);
  }


  private void launchCreateEvent(LatLng location) {

    if(!user.getType().equals("society")) return;
    Intent intent = new Intent(getBaseContext(), CreateEventActivity.class);
    intent.putExtra("location", location);
    startActivity(intent);
  }

  private void launchEventDetails(String eventId) {

    Intent intent = new Intent(getBaseContext(), EventDetailsActivity.class);
    intent.putExtra("eventId", eventId);
    startActivity(intent);
  }

  private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
      mLocationPermissionGranted = true;
    } else {
      ActivityCompat.requestPermissions(this,
              new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
              PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String permissions[],
                                         @NonNull int[] grantResults) {
    mLocationPermissionGranted = false;
    switch (requestCode) {
      case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          mLocationPermissionGranted = true;
        }
      }
    }
    updateLocationUI();
  }


  private void updateLocationUI() {
    if (mMap == null) {
      return;
    }
    try {
      if (mLocationPermissionGranted) {
        Log.d("location tag", "updating location ui");
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
      } else {
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mLastKnownLocation = null;
        getLocationPermission();
      }
    } catch (SecurityException e)  {
      Log.e("Exception: %s", e.getMessage());
    }
  }

  private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    try {
      if (mLocationPermissionGranted) {
        Task locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener() {
          @Override
          public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
              // Set the map's camera position to the current location of the device.
              mLastKnownLocation =(Location) task.getResult();
              if(mLastKnownLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
              }
            } else {
              Log.d(TAG, "Current location is null. Using defaults.");
              Log.e(TAG, "Exception: %s", task.getException());
              mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
              mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
          }
        });
      }
    } catch(SecurityException e)  {
      Log.e("Exception: %s", e.getMessage());
    }
  }

  protected void createLocationRequest() {
    Log.d("location tag", "creating location request");
    locationRequest = LocationRequest.create();
    locationRequest.setInterval(10000);
    locationRequest.setFastestInterval(5000);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);

    SettingsClient client = LocationServices.getSettingsClient(this);
    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

    task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
      @Override
      public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
        // All location settings are satisfied. The client can initialize
        // location requests here.
        // ...

        Log.d("location tag", "starting location update");
        startLocationUpdates();
      }
    });

    task.addOnFailureListener(this, new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Log.d("location tag", "failed");
        if (e instanceof ResolvableApiException) {
          // Location settings are not satisfied, but this can be fixed
          // by showing the user a dialog.
          try {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            ResolvableApiException resolvable = (ResolvableApiException) e;
            resolvable.startResolutionForResult(Map.this,
                    REQUEST_CHECK_SETTINGS);
          } catch (IntentSender.SendIntentException sendEx) {
            // Ignore the error.
          }
        }
      }
    });
  }


  @Override
  protected void onResume() {
    super.onResume();
    if (requestingLocationUpdates) {
      startLocationUpdates();
      if(model != null) {
        model.loadEvents();
        model.loadSubEvents();
        model.loadHotEvents();
      }
    }
    if(mNewEventMarker != null) {
      mNewEventMarker.remove();
    }
  }

  private void startLocationUpdates() {
    mFusedLocationProviderClient.requestLocationUpdates(locationRequest,
            locationCallback,
            null /* Looper */);
  }

  @Override
  protected void onPause() {
    super.onPause();
    stopLocationUpdates();
  }

  private void stopLocationUpdates() {
    mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
  }


}
