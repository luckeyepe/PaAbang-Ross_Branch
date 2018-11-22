package capstone.abang.com.Car_Renter.Location;



import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import capstone.abang.com.Car_Owner.Home.HomeFragment;
import capstone.abang.com.Common.Common;
import capstone.abang.com.Manifest;
import capstone.abang.com.Models.BookingFile;
import capstone.abang.com.Models.Tracking;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Register.LoginActivity;
import capstone.abang.com.R;
import capstone.abang.com.Remote.IGoogleAPI;
import capstone.abang.com.Utils.DirectionJSONParser;
import capstone.abang.com.Utils.LatLngInterpolator;
import capstone.abang.com.Utils.MapStateManager;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LocationFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    //Google Map
    private GoogleMap mMap;
    MapView mMapView;
    View mView;

    //Hashmap
    HashMap<String,Marker> hashMapMarker = new HashMap<>();
    HashMap<String,LatLng> LastLatLng = new HashMap<>();
    HashMap<String,String> Destination = new HashMap<>();

    private LinearLayout StatusReminder;
    private boolean isMarkerRotating = false;


    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private boolean hasSchedule = true;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;


    private LatLng currentPosition;


    DatabaseReference drivers;
    GeoFire geoFire;
    private ValueEventListener eventListener;
    private DatabaseReference databaseReference;
    private DatabaseReference updateReference;

    Marker mCurrent;
    Marker mOwner;
    private Marker mDestination;


    private Polyline direction;
    private String destination;

    private IGoogleAPI mService;
    SwitchCompat location_switch;


    private boolean aboutToArrive = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_location, container, false);


        return mView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            mMapView = mView.findViewById(R.id.map);
            StatusReminder = view.findViewById(R.id.StatusReminder);


            setupMapIfNeeded();
            if(mMapView != null){
                mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        location_switch = mView.findViewById(R.id.location_switch);
        if(getActivity()!=null) {
            SharedPreferences sharedPrefs = this.getActivity().getSharedPreferences("capstone.abang.com", Context.MODE_PRIVATE);
            location_switch.setChecked(sharedPrefs.getBoolean("SwitchStatus", true));
        }

        location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOnline) {
                if(isOnline){

                    updateStatus("online");
                    if(getActivity()!=null) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("capstone.abang.com", Context.MODE_PRIVATE).edit();
                        editor.putBoolean("SwitchStatus", true);
                        editor.apply();
                    }

                    retrieveAllBookings();
                  //  displayLocation();

                    if(getActivity()!=null)
                        Snackbar.make(getActivity().findViewById(android.R.id.content),"You are online",Snackbar.LENGTH_SHORT).show();

                }
                else{
                    updateStatus("offline");
                    if(getActivity()!=null) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("capstone.abang.com",Context.MODE_PRIVATE).edit();
                        editor.putBoolean("SwitchStatus", false);
                        editor.apply();
                    }
                    stopLocationUpdates();
                    if(mCurrent!=null)
                         mCurrent.remove();
                    if(getActivity()!=null)
                        Snackbar.make(getActivity().findViewById(android.R.id.content),"You are offline",Snackbar.LENGTH_SHORT).show();
                   // mMap.clear();
                }
            }
        });

        //Geo Fire

        drivers = FirebaseDatabase.getInstance().getReference("RentersLocation");
        geoFire = new GeoFire(drivers);

        setUpLocation();
        mService = Common.getGoogleAPI();
    }

    private void retrieveAllBookings() {
        databaseReference = FirebaseDatabase.getInstance().getReference("BookingFile");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            private boolean HasBooking = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(BookingFile.class).getBRenterCode().equalsIgnoreCase(uID) && snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("BOOKED")) {
                        location_switch.setClickable(true);
                        checkBookings(snapshot.getValue(BookingFile.class),"Booked");
                        HasBooking = true;
                    }
                    if(snapshot.getValue(BookingFile.class).getBRenterCode().equalsIgnoreCase(uID) && snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("Onboard")) {
                        location_switch.setClickable(false);

                        if(snapshot.getValue(BookingFile.class).getBDestination()!=null && snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("Onboard")){
                            Destination.put("Dest",snapshot.getValue(BookingFile.class).getBDestination());
                        }
                        checkBookings(snapshot.getValue(BookingFile.class),"Onboard");
                        HasBooking = true;
                    }

                }
                if(!HasBooking){
                    location_switch.setClickable(true);
                    if(hashMapMarker!=null) {
                        Marker marker = hashMapMarker.get("MyCurrentPos");
                        if(marker!=null)
                            marker.remove();
                        hashMapMarker.remove("MyCurrentPos");
                    }
                    displayLocation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void checkBookings(final BookingFile bookingFile,String Status) {
        databaseReference.removeEventListener(eventListener);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        String currentDate = dateFormat.format(c);


        if (Status.equalsIgnoreCase("Booked")) {
            if (bookingFile.getBServiceType().equalsIgnoreCase("Self Drive")) {


                if (Destination.get("Dest") != null) {
                    Destination.remove("Dest");

                    if (mDestination != null)
                        mDestination.remove();
                }

                    //if naay booking today
                    if (bookingFile.getBDateStart().equalsIgnoreCase(currentDate) && bookingFile.getBDeliveryMode().equalsIgnoreCase("Deliver")) {
            /*    Toast.makeText(getActivity(), "Naa kay booking bruh", Toast.LENGTH_SHORT).show();*/
               /* StatusReminder.setVisibility(View.VISIBLE);*/

                        if (getActivity() != null) {
                            hasSchedule = true;
                            if (bookingFile.getBOwnerStatus().equalsIgnoreCase("online")) {
                                findOwner(bookingFile);
                                displayLocation();
                            } else {
                                if (mOwner != null)
                                    mOwner.remove();

                                if (location_switch.isChecked()) {
                                    displayLocation();
                                }

                            }

                            // getDirection();

                        }
                    } else if (bookingFile.getBDateStart().equalsIgnoreCase(currentDate) && bookingFile.getBDeliveryMode().equalsIgnoreCase("Pick Up")) {
                        if (getActivity() != null) {
                            hasSchedule = true;
                            if (bookingFile.getBOwnerStatus().equalsIgnoreCase("online"))
                                findOwner(bookingFile);
                            else {
                                if (mOwner != null)
                                    mOwner.remove();

                            }

                        }
                    }

                if (location_switch.isChecked()) {
                    displayLocation();
                }
                } else {
                    if (bookingFile.getBSchedDate().equalsIgnoreCase(currentDate)) {

                        hasSchedule = true;
                        StatusReminder.setVisibility(View.VISIBLE);
                        if (bookingFile.getBOwnerStatus().equalsIgnoreCase("online")) {
                            findOwner(bookingFile);
                        }
                        else {
                            if (mOwner != null)
                                mOwner.remove();

                        }
                        if (location_switch.isChecked()) {
                            displayLocation();
                        }
                    } /*else if (checkBookingTomorrow(bookingFile.getBSchedDate(), currentDate)) {

                    Log.d("S", "Naa ka booking ugma ");
                }*/
                }
            } else if (Status.equalsIgnoreCase("Onboard")) {

                if (bookingFile.getBServiceType().equalsIgnoreCase("Self Drive")) {
                    DisplayOnBoard("SD");
                } else {
                    DisplayOnBoard("WD");
                }
            }
        }

    private void DisplayOnBoard(String ServiceType) {

        if (getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


            if (mLastLocation != null) {
                if (location_switch.isChecked()) {
                    final double latitude = mLastLocation.getLatitude();
                    final double longitude = mLastLocation.getLongitude();

                    if (mOwner != null)
                        mOwner.remove();

                    drivers.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    String.valueOf(mLastLocation.getLatitude()),
                                    String.valueOf(mLastLocation.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
                    if (mCurrent != null)
                        mCurrent.remove();


                    if (hashMapMarker != null) {
                        Marker marker = hashMapMarker.get("MyCurrentPos");
                        if (marker != null)
                            marker.remove();
                        hashMapMarker.remove("MyCurrentPos");
                    }

                    LatLng prevLoc = LastLatLng.get("MyCurrentPos");
                    LatLng NewLoc = new LatLng(latitude, longitude);
                    if (LastLatLng.get("MyCurrentPos") != null) {
                        mCurrent = mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .flat(true)
                                .title("Current Location")
                                .rotation((float) bearingBetweenLocations(prevLoc, NewLoc)));


                        animateMarker(mCurrent, NewLoc);
                        LastLatLng.remove("MyCurrentPos");
                        LastLatLng.put("MyCurrentPos", NewLoc);
                        hashMapMarker.put("MyCurrentPos", mCurrent);
                    }

                       else {

                            mCurrent = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                    .position(new LatLng(latitude, longitude))
                                    .anchor(0.5f, 0.5f)
                                    .flat(true)
                                    .title("Current Location")
                            );
                            LatLng oldLoc = new LatLng(latitude, longitude);
                            LastLatLng.put("MyCurrentPos", oldLoc);
                            hashMapMarker.put("MyCurrentPos", mCurrent);
                        }

                    if (ServiceType.equalsIgnoreCase("WD")) {
                        if (Destination.get("Dest") != null) {
                            destination = Destination.get("Dest");
                            destination = destination.replace(" ", "+");


                            getDirection(latitude, longitude, destination);
                        }

                    }

                }
                } else {
                    Log.d("ERROR", "Cannot get your location");
                }
            }

        }


    private void getDirection(final double latitude, final double longitude, final String Destination)
    {

        if(latitude!=0 && longitude!=0 ) {
            currentPosition = new LatLng(latitude,longitude);
            String requestApi = null;
            try {
                requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "mode=driving&" +
                        "transit_routing_preference=less_driving&" +
                        "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                        "destination=" +Destination + "&" +
                        "key=" + getResources().getString(R.string.google_direction_api);

                mService.getPath(requestApi)
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {

                                try {
                                    new ParserTask().execute(response.body());
                                    if(Destination!=null && getActivity()!=null) {

                                        //Set Current Location
                                        Location currentLoc = new Location("");
                                        currentLoc.setLatitude(latitude);
                                        currentLoc.setLongitude(longitude);


                                        //Set Destination Location
                                        LatLng temp = getLocationFromAddress(getActivity(),Destination);
                                        double Lat = temp.latitude;
                                        double Long = temp.longitude;
                                        Location destinationLoc = new Location("");
                                        destinationLoc.setLatitude(Lat);
                                        destinationLoc.setLongitude(Long);
                                        if(mDestination!=null){
                                            mDestination.remove();
                                        }

                                        //Toast.makeTe
                                        // xt(getActivity(), " " +(int) currentLoc.distanceTo(destinationLoc), Toast.LENGTH_SHORT).show();

                                        //Alert if Distance is lesser than 500 meters
                                        if((int) currentLoc.distanceTo(destinationLoc) <= 500 && (int) currentLoc.distanceTo(destinationLoc) >0 && !aboutToArrive){
                                            AlertDialog.Builder builder;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                                            } else {
                                                builder = new AlertDialog.Builder(getActivity());
                                            }
                                            builder.setTitle("Alert")
                                                    .setMessage("You are "+ (int)currentLoc.distanceTo(destinationLoc) + " meters away from your destination")
                                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();

                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .setCancelable(true)
                                                    .show();
                                            aboutToArrive = true;
                                        }


                                        mDestination = mMap.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_destination))
                                                .position(getLocationFromAddress(getActivity(), Destination))
                                                .title(Destination)
                                                .snippet("Distance "+new DecimalFormat("#.#").format((currentLoc.distanceTo(destinationLoc))/1000) +" km"));
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {


                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();

            }
        }



    }

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    private void findOwner(final BookingFile bookingFile) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OwnersLocation");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(capstone.abang.com.Models.Location.class).getCode().equalsIgnoreCase(bookingFile.getBOwnerCode()) && bookingFile.getBStatus().equalsIgnoreCase("Booked")) {
                        setOwnerLocation(snapshot.getValue(capstone.abang.com.Models.Location.class));
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void setOwnerLocation(capstone.abang.com.Models.Location location) {
        if (location.getLat() != null && location.getLng() != null) {
            final double latitude = Double.parseDouble(location.getLat());
            final double longitude = Double.parseDouble(location.getLng());


           /* if (destination != null)
                destination.remove();*/

            if (mOwner != null) {
                mOwner.remove();
            }

            if (hasSchedule) {
                if(hashMapMarker!=null) {
                    mOwner = hashMapMarker.get("OwnerCurrentPos");
                    if(mOwner!=null)
                        mOwner.remove();
                    hashMapMarker.remove("OwnerCurrentPos");
                }


                LatLng prevLoc = LastLatLng.get("OwnerCurrentPos");
                LatLng NewLoc = new LatLng(latitude,longitude);
                if(LastLatLng.get("OwnerCurrentPos")!=null) {

                    mOwner = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                            .position(new LatLng(latitude, longitude))
                            .title("Owner's Current Location")
                            .flat(true)
                            .anchor(0.5f, 0.5f)
                            .rotation((float)bearingBetweenLocations(prevLoc,NewLoc)));



                  // float Bearing = (float)bearingBetweenLocations(prevLoc,NewLoc);
                  //  rotateMarker(mOwner,Bearing);
                    animateMarker(mOwner,NewLoc);
                    LastLatLng.remove("OwnerCurrentPos");
                    LastLatLng.put("OwnerCurrentPos",NewLoc);
                    hashMapMarker.put("OwnerCurrentPos", mOwner);

                }
                else{
                    mOwner = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                            .position(new LatLng(latitude, longitude))
                            .title("Owner's Current Location")
                            .anchor(0.5f, 0.5f)
                            .flat(true));
                    LatLng oldLoc = new LatLng(latitude, longitude);
                    LastLatLng.put("OwnerCurrentPos",oldLoc);
                    hashMapMarker.put("OwnerCurrentPos",mOwner);
                }

            }

        }
    }

    public void animateMarker(final Marker marker,final LatLng toPosition) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 5000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private void rotateMarker(final Marker marker, final LatLng destination, final float rotation) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                        float bearing = computeRotation(v, startRotation, rotation);

                        marker.setRotation(bearing);
                        marker.setPosition(newPosition);

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    /* private void rotateMarker(final Marker marker, final float toRotation) {
         if(!isMarkerRotating) {
             final Handler handler = new Handler();
             final long start = SystemClock.uptimeMillis();
             final float startRotation = marker.getRotation();
             final long duration = 1000;
 
             final Interpolator interpolator = new LinearInterpolator();
 
             handler.post(new Runnable() {
                 @Override
                 public void run() {
                     isMarkerRotating = true;
 
                     long elapsed = SystemClock.uptimeMillis() - start;
                     float t = interpolator.getInterpolation((float) elapsed / duration);
 
                     float rot = t * toRotation + (1 - t) * startRotation;
 
                     marker.setRotation(-rot > 180 ? rot / 2 : rot);
                     if (t < 1.0) {
                         // Post again 16ms later.
                         handler.postDelayed(this, 16);
                     } else {
                         isMarkerRotating = false;
                     }
                 }
             });
         }
     }*/
    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }


/*    private boolean checkBookingTomorrow(String bookDate, String currentDate) {
        boolean flag = false;
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date date = null;
        Date book = null;
        try {
            date = dateFormat.parse(currentDate);
            book = dateFormat.parse(bookDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(book);

        calendar.add(Calendar.DATE, 1);

        if(calendar.getTime().toString().equalsIgnoreCase(calendar2.getTime().toString())) {
            flag = true;
        }

        return flag;
    }*/


    private void updateStatus(final String status) {
        updateReference = FirebaseDatabase.getInstance().getReference("BookingFile");
        updateReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(BookingFile.class).getBRenterCode().equalsIgnoreCase(uID)  && snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("Booked") || snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("Onboard")) {
                        BookingFile bookingFile = snapshot.getValue(BookingFile.class);
                        updateReference.child(bookingFile.getBCode()).child("brenterStatus").setValue(status);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupMapIfNeeded() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mMap == null) {
            mMapView = mView.findViewById(R.id.map);
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupMapIfNeeded();
    }

    @Override
    public void onPause() {
        super.onPause();
        MapStateManager mgr = new MapStateManager(getActivity());
        mgr.saveMapState(mMap);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        if(location_switch.isChecked())
                            displayLocation();
                    }
                }
        }
    }

    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {

            //Request runtime permission

            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);
        }
        else{
            if(checkPlayServices()){
                buildGoogleApiClient();
                createLocationRequest();
                if(location_switch.isChecked())
                    retrieveAllBookings();
            }
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,getActivity(),PLAY_SERVICE_RES_REQUEST).show();
            else{
                Toast.makeText(getContext(),"This device is not supported",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    private void stopLocationUpdates() {


        if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {

            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);

    }

    private void displayLocation() {

        if(getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


            if (mLastLocation != null) {
                if (location_switch.isChecked()) {
                    final double latitude = mLastLocation.getLatitude();
                    final double longitude = mLastLocation.getLongitude();

                    drivers.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    String.valueOf(mLastLocation.getLatitude()),
                                    String.valueOf(mLastLocation.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
                    if (mCurrent != null)
                                mCurrent.remove();
                            mCurrent = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_renter_marker))
                                    .position(new LatLng(latitude, longitude))
                                    .title("You are here"));

                    //Update to Firebase
//                    geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
//                        @Override
//                        public void onComplete(String key, DatabaseError error) {
//                            //Add marker
//                            if (mCurrent != null)
//                                mCurrent.remove();
//                            mCurrent = mMap.addMarker(new MarkerOptions()
//                                    .position(new LatLng(latitude, longitude))
//                                    .title("You"));
//
//                            // Camera
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
//
//                        }
//                    });
                }
            } else {
                Log.d("ERROR", "Cannot get your location");
            }
        }
    }



    private void startLocationUpdates() {

        if(getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


            if(getContext() != null) {
                MapsInitializer.initialize(getContext());

                try{
                    boolean isSuccess = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.custom_map)
                    );
                    if(!isSuccess)
                        Log.e("ERROR", "Map sytle load failed ");
                }
                catch (Resources.NotFoundException ex){
                    ex.printStackTrace();
                }


            mMap = googleMap;


            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setTrafficEnabled(false);
            mMap.setIndoorEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);

            MapStateManager mgr = new MapStateManager(getContext());
            CameraPosition position = mgr.getSavedCameraPosition();
            if (position != null) {
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
                //Toast.makeText(getContext(), "entering Resume State", Toast.LENGTH_SHORT).show();
                mMap.moveCamera(update);

                mMap.setMapType(mgr.getSavedMapType());
            }
        }



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        retrieveAllBookings();
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        mCurrent.remove();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mCurrent.remove();

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        retrieveAllBookings();
    }


    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>
    {
        ProgressDialog mDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  mDialog.setMessage("Please wait..");
            mDialog.show();*/
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();
                routes = parser.parse(jObject);
            }catch(JSONException e){
                e.printStackTrace();

            }
            return  routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
           /* mDialog.dismiss();*/

            ArrayList points= new ArrayList();
            PolylineOptions polylineOptions = new PolylineOptions();

            for(int i=0;i<lists.size();i++)
            {
               /* points = new ArrayList();*/
               /* polylineOptions = new PolylineOptions();*/

                List<HashMap<String,String>> path = lists.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);


                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat,lng);

                    points.add(position);


                }
                polylineOptions.addAll(points);
                polylineOptions.width(12);
                polylineOptions.color(Color.MAGENTA);
                polylineOptions.geodesic(true);

            }

            if(direction!=null)
                direction.remove();

            direction = mMap.addPolyline(polylineOptions);

        }
    }
}