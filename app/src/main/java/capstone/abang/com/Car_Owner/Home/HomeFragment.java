package capstone.abang.com.Car_Owner.Home;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
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

import capstone.abang.com.Car_Owner.InboxActivity;
import capstone.abang.com.Car_Owner.car_owner;
import capstone.abang.com.Car_Renter.Home.CarBookingDetailActivity;
import capstone.abang.com.Car_Renter.Home.CarDetailsActivity;
import capstone.abang.com.Common.Common;
import capstone.abang.com.Models.BookingFile;
import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.Tracking;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.R;
import capstone.abang.com.Remote.IGoogleAPI;
import capstone.abang.com.Utils.DirectionJSONParser;
import capstone.abang.com.Utils.LatLngInterpolator;
import capstone.abang.com.Utils.MapStateManager;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ZXingScannerView.ResultHandler
{
    private GoogleMap mMap;
    MapView mMapView;
    View mView;

    private Toolbar toolbar;
    private ZXingScannerView zXingScannerView;
    private LinearLayout StatusReminder;
    private boolean hasSchedule = true;
    private boolean onTrackRenter = false;
    private boolean SwitchStatus;
    private BookingFile booking;
    private ImageView btnInbox;



    HashMap<String,Marker> hashMapMarker = new HashMap<>();
    HashMap<String,LatLng> LastLatLng = new HashMap<>();
    HashMap<String,String> Destination = new HashMap<>();
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference drivers;
    GeoFire geoFire;
    private ValueEventListener eventListener;
    private DatabaseReference databaseReference;
    private DatabaseReference updateReference;
    private DatabaseReference findRenter;


    private Marker mCurrent;
    private Marker mDestination;


    SwitchCompat location_switch;


    private Marker marker;


    private LatLng currentPosition;

    private Polyline direction;
    private String destination;
    private IGoogleAPI mService;

    private boolean aboutToArrive = false;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_owner_home, container, false);
        zXingScannerView = new ZXingScannerView(getActivity());



        setupToolbar(mView);

        if(mCurrent!=null){
            mCurrent.remove();
        }

        return mView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = mView.findViewById(R.id.map);
        StatusReminder = view.findViewById(R.id.StatusReminder);



        if(mCurrent!=null){
            mCurrent.remove();
        }

        setupMapIfNeeded();
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        location_switch = mView.findViewById(R.id.location_switch);

        if(!location_switch.isChecked()){

        }


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

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("capstone.abang.com",Context.MODE_PRIVATE).edit();
                        editor.putBoolean("SwitchStatus", true);
                        editor.apply();
                    }

                    retrieveAllBookings();

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

                    if(hashMapMarker!=null) {
                        Marker marker = hashMapMarker.get("MyCurrentPos");
                        if(marker!=null)
                            marker.remove();
                        hashMapMarker.remove("MyCurrentPos");
                    }


                    if(getActivity()!=null)
                        Snackbar.make(getActivity().findViewById(android.R.id.content),"You are offline",Snackbar.LENGTH_SHORT).show();
                   // mMap.clear();
                   /* if(direction!=null){
                        direction.remove();
                    }*/

                }


            }
        });


        //Geo Fire

        drivers = FirebaseDatabase.getInstance().getReference("OwnersLocation");
        geoFire = new GeoFire(drivers);

        setUpLocation();
        mService = Common.getGoogleAPI();



    }

    private void updateStatus(final String status) {
        updateReference = FirebaseDatabase.getInstance().getReference("BookingFile");
        updateReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(BookingFile.class).getBOwnerCode().equalsIgnoreCase(uID) && snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("Booked") || snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("Onboard")) {
                        BookingFile bookingFile = snapshot.getValue(BookingFile.class);
                        updateReference.child(bookingFile.getBCode()).child("bownerStatus").setValue(status);
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


    private void retrieveAllBookings() {


        databaseReference = FirebaseDatabase.getInstance().getReference("BookingFile");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            private boolean HasBooking = false;
            int count = 0;
            ArrayList <String> Status = new ArrayList<>();
            ArrayList <String> ServiceType= new ArrayList<>();
            ArrayList <String> DeliveryMode = new ArrayList<>();
            ArrayList <String> RenterStatus = new ArrayList<>();
            ArrayList <String> DateStart = new ArrayList<>();
            ArrayList <String> RenterCode = new ArrayList<>();


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(BookingFile.class).getBOwnerCode() != null) {
                        if (snapshot.getValue(BookingFile.class).getBOwnerCode().equalsIgnoreCase(uID) && snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("BOOKED")) {
                            Status.add(count,"Booked");
                            ServiceType.add(count,snapshot.getValue(BookingFile.class).getBServiceType());
                            DeliveryMode.add(count,snapshot.getValue(BookingFile.class).getBDeliveryMode());
                            RenterStatus.add(count,snapshot.getValue(BookingFile.class).getBRenterStatus());
                            DateStart.add(count,snapshot.getValue(BookingFile.class).getBDateStart());
                            RenterCode.add(count,snapshot.getValue(BookingFile.class).getBRenterCode());
                            count++;


                            HasBooking = true;
                        }
                        else if (snapshot.getValue(BookingFile.class).getBOwnerCode().equalsIgnoreCase(uID) && snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("Onboard")) {
                            Status.add(count, "Onboard");
                            ServiceType.add(count,snapshot.getValue(BookingFile.class).getBServiceType());
                            DeliveryMode.add(count,snapshot.getValue(BookingFile.class).getBDeliveryMode());
                            RenterStatus.add(count,snapshot.getValue(BookingFile.class).getBRenterStatus());
                            DateStart.add(count,snapshot.getValue(BookingFile.class).getBDateStart());
                            RenterCode.add(count,snapshot.getValue(BookingFile.class).getBRenterCode());

                            if(snapshot.getValue(BookingFile.class).getBDestination()!=null && snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("Onboard")){
                                Destination.put(snapshot.getValue(BookingFile.class).getBRenterCode(),snapshot.getValue(BookingFile.class).getBDestination());
                            }
                            count++;

                            HasBooking = true;
                        }
                    }
                }
                if(HasBooking){
                    checkBookings(Status,ServiceType,DeliveryMode,RenterStatus,DateStart,RenterCode);
                }
                if(!HasBooking){
                   mMap.clear();
                    displayMyLocation(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkBookings(ArrayList<String> status, ArrayList<String> serviceType, ArrayList<String> deliveryMode, ArrayList<String> renterStatus, ArrayList<String> dateStart, ArrayList<String> renterCode) {
        databaseReference.removeEventListener(eventListener);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDate = dateFormat.format(c);


        for(int i=0;i<status.size();i++){
            if(status.get(i).equalsIgnoreCase("Booked")){
                if(serviceType.get(i).equalsIgnoreCase("Self Drive")){
                    if(Destination.get(renterCode.get(i))!=null){
                        Destination.remove(renterCode.get(i));

                        if(mDestination!=null)
                            mDestination.remove();

                    }

                    if(dateStart.get(i).equalsIgnoreCase(currentDate) && deliveryMode.get(i).equalsIgnoreCase("Deliver")){
                        if(getActivity()!=null){
                            hasSchedule=true;
                            if(renterStatus.get(i).equalsIgnoreCase("online")) {
                                findRenter(renterCode.get(i), status.get(i),renterCode.get(i),renterStatus.get(i));
                                displayMyLocation(false);
                            }
                            else{
                                if(hashMapMarker!=null) {
                                    Marker marker = hashMapMarker.get(renterCode.get(i));
                                    marker.remove();
                                    hashMapMarker.remove(renterCode.get(i));
                                }
                                if(location_switch.isChecked()){
                                    displayMyLocation(false);
                                }

                            }

                        }
                    }
                    else{
                        if(getActivity()!=null){
                            hasSchedule=true;
                            if(renterStatus.get(i).equalsIgnoreCase("online")) {
                                findRenter(renterCode.get(i), status.get(i),renterCode.get(i),renterStatus.get(i));
                                displayMyLocation(false);

                            }
                            else{
                                if(hashMapMarker!=null) {
                                     Marker marker = hashMapMarker.get(renterCode.get(i));
                                if(marker!=null)
                                     marker.remove();
                                     hashMapMarker.remove(renterCode.get(i));
                                }
                                if(location_switch.isChecked()){
                                    displayMyLocation(false);
                                }
                            }
                        }
                    }
                }
                else{

                        if(getActivity()!=null){
                            hasSchedule=true;
                            if(renterStatus.get(i).equalsIgnoreCase("online")) {
                                findRenter(renterCode.get(i), status.get(i),renterCode.get(i),renterStatus.get(i));
                                displayMyLocation(false);
                            }
                            else{
                                if(hashMapMarker!=null) {
                                    Marker marker = hashMapMarker.get(renterCode.get(i));
                                    if(marker!=null)
                                      marker.remove();
                                    hashMapMarker.remove(renterCode.get(i));
                                }
                                if(location_switch.isChecked()){
                                    displayMyLocation(false);
                                }

                            }

                        }

                }

            }
            else if(status.get(i).equalsIgnoreCase("Onboard")){
                if(serviceType.get(i).equalsIgnoreCase("Self Drive")){
                    findOnboardRenter("SD",renterCode.get(i),renterStatus.get(i),"");
                    if(direction!=null)
                        direction.remove();

                }
                else{
                    if(Destination.get(renterCode.get(i))!=null) {
                        String destination = Destination.get(renterCode.get(i));
                        findOnboardRenter("WD", renterCode.get(i), renterStatus.get(i), destination);
                    }
                    else
                       findOnboardRenter("WD",renterCode.get(i),renterStatus.get(i),"");
                }

            }
        }

    }



    private void findRenter(final String RenterCode, final String Status,final String renterCode,final String renterStatus) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("RentersLocation");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(capstone.abang.com.Models.Location.class).getCode().equalsIgnoreCase(RenterCode) && Status.equalsIgnoreCase("Booked")) {
                        setRenterLocation(snapshot.getValue(capstone.abang.com.Models.Location.class),renterCode,renterStatus);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setRenterLocation(final capstone.abang.com.Models.Location location, final String renterCode, final String renterStatus) {



        if (location.getLat() != null && location.getLng() != null) {
            final double latitude = Double.parseDouble(location.getLat());
            final double longitude = Double.parseDouble(location.getLng());



            if (hasSchedule) {


               /* Toast.makeText(getActivity(), "HOY DAGAN", Toast.LENGTH_SHORT).show();*/
                Log.d("NISUD SIYA DIRI NOY", "setRenterLocation: ");
                if(hashMapMarker!=null) {
                    Marker marker = hashMapMarker.get(renterCode);
                    if(marker!=null)
                        marker.remove();
                    hashMapMarker.remove(renterCode);
                }


                databaseReference = FirebaseDatabase.getInstance().getReference("UDFile");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.getValue(UDFile.class).getUDUserCode().equalsIgnoreCase(renterCode) )

                            {
                                if(hashMapMarker!=null) {
                                    Marker marker = hashMapMarker.get(renterCode);
                                    if(marker!=null)
                                        marker.remove();
                                    hashMapMarker.remove(renterCode);
                                }


                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_renter_marker))
                                        .position(new LatLng(latitude, longitude))
                                        .title(snapshot.getValue(UDFile.class).getUDFullname())
                                        .snippet(snapshot.getValue(UDFile.class).getUDContact()));
                                hashMapMarker.put(renterCode, marker);





                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

        }


    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    private boolean checkBookingTomorrow(String bookDate, String currentDate) {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        setupMapIfNeeded();
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


    private void setupToolbar(View view) {
        toolbar = view.findViewById(R.id.profiletoolbar);
        ImageView btnCamera = view.findViewById(R.id.btnCamera);
        btnInbox = view.findViewById(R.id.inboxBtn);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
        btnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InboxActivity.class);
                startActivity(intent);
            }
        });

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_car_owner, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
                            retrieveAllBookings();
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
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
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
                //Toast.makeText(getContext(),"This device is not supported",Toast.LENGTH_SHORT).show();
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
    @Override
    public void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        if(mGoogleApiClient!=null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
        if(mGoogleApiClient!=null)
            mGoogleApiClient.disconnect();
    }
    private void displayMyLocation(final boolean onBoard){
        if(getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


            if (mLastLocation != null) {


                if (location_switch.isChecked()) {


                    final double latitude = mLastLocation.getLatitude();
                    final double longitude = mLastLocation.getLongitude();


                    if(mCurrent!=null)
                        mCurrent.remove();

                    final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("OwnersLocation");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(snapshot.getValue(capstone.abang.com.Models.Location.class).getCode().equals(uID)) {
                                    String lastLat = snapshot.getValue(capstone.abang.com.Models.Location.class).getLat();
                                    String lastLong = snapshot.getValue(capstone.abang.com.Models.Location.class).getLng();

                                    final double LastLat = Double.parseDouble(lastLat);
                                    final double LastLng = Double.parseDouble(lastLong);


                                    if(onBoard) {
                                        if(mCurrent!=null)
                                            mCurrent.remove();
                                        mCurrent = mMap.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                                .position(new LatLng(latitude, longitude))
                                                .flat(true)
                                                .title("You are here"));
                                    }
                                    else{
                                        if(mCurrent!=null)
                                            mCurrent.remove();
                                        mCurrent = mMap.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_owner_marker))
                                                .position(new LatLng(latitude, longitude))
                                                .title("You are here"));

                                    }


                                    LatLng LastPos = new LatLng(LastLat,LastLng);
                                    LatLng ToPos = new LatLng(latitude,longitude);


                                    Log.d("LAST LOC", "Lat "+latitude + " Long "+longitude);



                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    drivers.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    String.valueOf(mLastLocation.getLatitude()),
                                    String.valueOf(mLastLocation.getLongitude())));



                }
            } else {
                Log.d("ERROR", "Cannot get your location");
            }
        }

    }


    private void startLocationUpdates() {

        if(getActivity() !=null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        retrieveAllBookings();

    }


    private void scan() {
        assert (getActivity()) != null;
        (getActivity()).setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }


    @Override
    public void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
        if(getActivity()!=null) {
            MapStateManager mgr = new MapStateManager(getActivity());
            mgr.saveMapState(mMap);
        }

    }

    @Override
    public void handleResult(final Result result) {

        // Toast.makeText(getActivity(), " mao ni QR" + result.getText(), Toast.LENGTH_SHORT).show();
        databaseReference = FirebaseDatabase.getInstance().getReference("BookingFile");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(BookingFile.class).getBOwnerCode().equalsIgnoreCase(uID) && snapshot.getValue(BookingFile.class).getBRenterCode().equalsIgnoreCase(result.getText())
                            && snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("Booked")) {

                        if(snapshot.getValue(BookingFile.class).getBServiceType().equalsIgnoreCase("Self Drive"))
                               CheckOnBoard(snapshot.getValue(BookingFile.class),result.getText(),snapshot.getValue(BookingFile.class).getBDateStart(),"");
                        else{
                            if(snapshot.getValue(BookingFile.class).getBDestination()==null)
                                  CheckOnBoard(snapshot.getValue(BookingFile.class),result.getText(),snapshot.getValue(BookingFile.class).getBSchedDate(),"");
                            else{
                                destination = snapshot.getValue(BookingFile.class).getBDestination();
                                destination = destination.replace(" ","+");
                                CheckOnBoard(snapshot.getValue(BookingFile.class),result.getText(),snapshot.getValue(BookingFile.class).getBSchedDate(),destination);
                            }
                        }
                    }
                    else if(snapshot.getValue(BookingFile.class).getBOwnerCode().equalsIgnoreCase(uID)&&  snapshot.getValue(BookingFile.class).getBRenterCode().equalsIgnoreCase(result.getText())
                            && snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("Onboard")){
                        mMap.clear();
                        if(Destination.get(snapshot.getValue(BookingFile.class).getBRenterCode())!=null){
                            Destination.remove(snapshot.getValue(BookingFile.class).getBRenterCode());
                        }
                        displayMyLocation(false);
                        BookingFile bookingFile = snapshot.getValue(BookingFile.class);
                        updateBookingStatus("Done",result.getText());
                        updateTransactions(bookingFile, "Done");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if(getActivity()!=null) {
            Intent intent = new Intent(getActivity(), car_owner.class);
            intent.putExtra("rentercode", result.getText());
            startActivity(intent);
        }
    }

    private void updateTransactions(final BookingFile bookingFile, String status) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CDFile");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CDFile cdFile = new CDFile();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(CDFile.class).getCDCode().equalsIgnoreCase(bookingFile.getBCarCode())) {
                        cdFile = snapshot.getValue(CDFile.class);
                        break;
                    }
                }
                int holder = cdFile.getCdtransactions();
                reference.child(bookingFile.getBCarCode()).child("cdtransactions").setValue(holder + 1);
                reference.child(bookingFile.getBCarCode()).child("reservations").child(bookingFile.getBCode()).removeValue();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ReservationFile");
                databaseReference.child(bookingFile.getBCode()).child("resStatus").setValue("Done");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("UDFile");
        referenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UDFile udFile;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(UDFile.class).getUDUserCode().equalsIgnoreCase(bookingFile.getBOwnerCode())) {
                        udFile = snapshot.getValue(UDFile.class);
                        int holder = udFile.getUDTransactions();
                        referenceUser.child(bookingFile.getBOwnerCode()).child("udtransactions").setValue(holder + 1);
                    }
                    else if(snapshot.getValue(UDFile.class).getUDUserCode().equalsIgnoreCase(bookingFile.getBRenterCode())) {
                        udFile = snapshot.getValue(UDFile.class);
                        int holder = udFile.getUDTransactions();
                        referenceUser.child(bookingFile.getBRenterCode()).child("udtransactions").setValue(holder + 1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void CheckOnBoard(final BookingFile value,String RenterCode, String StartDate,String Destination) {
        databaseReference.removeEventListener(eventListener);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        String currentDate = dateFormat.format(c);


        Toast.makeText(getActivity(), "Current date: "+currentDate + " and  StartDate: "+StartDate, Toast.LENGTH_SHORT).show();
        if(StartDate.equalsIgnoreCase(currentDate)){

            if(value.getBRenterCode().equalsIgnoreCase(RenterCode)){
                updateBookingStatus("Onboard",RenterCode);

                if(value.getBServiceType().equalsIgnoreCase("Self Drive")) {
                    findOnboardRenter("SD",RenterCode,value.getBRenterStatus(),Destination);
                }
                else {
                    findOnboardRenter("WD",RenterCode,value.getBRenterStatus(),Destination);
                }


            }
            else{
                if(getActivity()!=null)
                     Toast.makeText(getActivity(), "Invalid QR Code" , Toast.LENGTH_SHORT).show();
            }

        }
        else{
            if(getActivity()!=null)
                  Toast.makeText(getActivity(), "You don't have a Booking Schedule Today " , Toast.LENGTH_SHORT).show();
        }


    }

    private void findOnboardRenter(final String ServiceType, final String RenterCode, final String renterStatus, final String Destination) {
         findRenter = FirebaseDatabase.getInstance().getReference("RentersLocation");
        eventListener =  findRenter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(capstone.abang.com.Models.Location.class).getCode().equalsIgnoreCase(RenterCode)) {
                        trackRenterLocation(snapshot.getValue(capstone.abang.com.Models.Location.class),ServiceType,RenterCode,renterStatus,Destination);

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void trackRenterLocation(capstone.abang.com.Models.Location location, String ServiceType, final String RenterCode,final String renterStatus,final String Destination) {
        findRenter.removeEventListener(eventListener);

        if(ServiceType.equalsIgnoreCase("WD")) {
            if (location.getLat() != null && location.getLng() != null) {
                final double latitude = Double.parseDouble(location.getLat());
                final double longitude = Double.parseDouble(location.getLng());


                databaseReference = FirebaseDatabase.getInstance().getReference("UDFile");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {


                            if(snapshot.getValue(UDFile.class).getUDUserCode().equalsIgnoreCase(RenterCode) ) {


                            if(renterStatus.equalsIgnoreCase("online")){
                                if(hashMapMarker!=null) {
                                    Marker marker = hashMapMarker.get(RenterCode);
                                    if(marker!=null)
                                        marker.remove();
                                    hashMapMarker.remove(RenterCode);
                                }

                                if(!Destination.equalsIgnoreCase(""))
                                       getDirection(latitude,longitude,Destination);

                                if (mCurrent != null)
                                    mCurrent.remove();

                                if(LastLatLng.get(RenterCode)!=null){

                                    LatLng prevLoc = LastLatLng.get(RenterCode);
                                    LatLng NewLoc = new LatLng(latitude,longitude);

                                    marker = mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                            .position(new LatLng(latitude, longitude))
                                            .anchor(0.5f, 0.5f)
                                            .title("Onboard")
                                            .flat(true)
                                            .rotation((float)bearingBetweenLocations(prevLoc,NewLoc)));

                                    animateMarker(marker,NewLoc);

                                    LastLatLng.remove(RenterCode);
                                    LastLatLng.put(RenterCode,NewLoc);
                                    hashMapMarker.put(RenterCode, marker);

                                }
                                else {

                                    marker = mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                            .position(new LatLng(latitude, longitude))
                                            .anchor(0.5f, 0.5f)
                                            .flat(true)
                                            .title("Onboard"));
                                    LatLng oldLoc = new LatLng(latitude, longitude);
                                    LastLatLng.put(RenterCode,oldLoc);
                                    hashMapMarker.put(RenterCode,marker);
                                }

                            }
                            else{
                                if(hashMapMarker!=null) {
                                    Marker marker = hashMapMarker.get(RenterCode);
                                    if(marker!=null)
                                        marker.remove();
                                    hashMapMarker.remove(RenterCode);
                                }

                            }






                                    /*rotateMarker(marker, (float) bearingBetweenLocations(lastLatLng,NewLatLng)); */



                            }


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                LatLng lastLatLng = LastLatLng.get(RenterCode);
                LatLng NewLatLng = new LatLng(latitude,longitude);



            }
        }
        else if(ServiceType.equalsIgnoreCase("SD")){

            if (location.getLat() != null && location.getLng() != null) {
                final double latitude = Double.parseDouble(location.getLat());
                final double longitude = Double.parseDouble(location.getLng());

                if(hashMapMarker!=null) {
                    Marker marker = hashMapMarker.get(RenterCode);
                    if(marker!=null)
                        marker.remove();
                    hashMapMarker.remove(RenterCode);
                }


                if(mDestination!=null){
                    mDestination.remove();
                }

                databaseReference = FirebaseDatabase.getInstance().getReference("UDFile");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {


                            if(snapshot.getValue(UDFile.class).getUDUserCode().equalsIgnoreCase(RenterCode) ) {
                                if(hashMapMarker!=null) {
                                    Marker marker = hashMapMarker.get(RenterCode);
                                    if(marker!=null)
                                        marker.remove();
                                    hashMapMarker.remove(RenterCode);
                                }

                                        if(LastLatLng.get(RenterCode)!=null){


                                            LatLng prevLoc = LastLatLng.get(RenterCode);
                                            LatLng NewLoc = new LatLng(latitude,longitude);
                                             marker = mMap.addMarker(new MarkerOptions()
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                                    .position(new LatLng(latitude, longitude))
                                                    .anchor(0.5f, 0.5f)
                                                    .flat(true)
                                                    .title(snapshot.getValue(UDFile.class).getUDFullname())
                                                    .snippet(snapshot.getValue(UDFile.class).getUDContact())
                                                    .rotation((float)bearingBetweenLocations(prevLoc,NewLoc)));


                                          animateMarker(marker,NewLoc);
                                            LastLatLng.remove(RenterCode);
                                            LastLatLng.put(RenterCode,NewLoc);
                                            hashMapMarker.put(RenterCode, marker);

                                        }
                                       else {

                                             marker = mMap.addMarker(new MarkerOptions()
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                                    .position(new LatLng(latitude, longitude))
                                                    .anchor(0.5f, 0.5f)
                                                    .flat(true)
                                                    .title(snapshot.getValue(UDFile.class).getUDFullname())
                                                    .snippet(snapshot.getValue(UDFile.class).getUDContact()));
                                            LatLng oldLoc = new LatLng(latitude, longitude);
                                            LastLatLng.put(RenterCode,oldLoc);
                                            hashMapMarker.put(RenterCode,marker);
                                        }

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(location_switch.isChecked()){
                    displayMyLocation(false);
                }



            }

        }

    }
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
    /*private void rotateMarker(final Marker marker, final double toRotation) {

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

                    float rot = (float) (t * toRotation + (1 - t) * startRotation);

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



    private void updateBookingStatus(final String status, final String RenterCode) {
        updateReference = FirebaseDatabase.getInstance().getReference("BookingFile");
        updateReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(BookingFile.class).getBOwnerCode().equalsIgnoreCase(uID) && snapshot.getValue(BookingFile.class).getBRenterCode().equalsIgnoreCase(RenterCode)) {
                        BookingFile bookingFile = snapshot.getValue(BookingFile.class);
                        updateReference.child(bookingFile.getBCode()).child("bstatus").setValue(status);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
}