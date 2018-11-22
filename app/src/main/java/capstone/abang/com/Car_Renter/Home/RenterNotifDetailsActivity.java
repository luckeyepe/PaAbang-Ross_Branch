package capstone.abang.com.Car_Renter.Home;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vlk.multimager.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.CarPhotos;
import capstone.abang.com.Models.CarSettings;
import capstone.abang.com.Models.ReservationFile;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.LatLngInterpolator;
import capstone.abang.com.Utils.ViewPagerAdapter;

public class RenterNotifDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbar_title;
    private TextView textViewCarName;
    private TextView textViewPostedOn;
    private TextView textViewContacts;
    private TextView textViewUserFullname;
    private TextView textViewUserDateJoined;
    private ImageView imageViewProfile;
    private TextView textDestination;
    private TextView textServiceType;
    private TextView textServiceMode;
    private TextView textDeliveryMode;
    private TextView textStartDate;
    private TextView textEndDate;
    private TextView textTotal;
    private TextView textSchedDate;
    private TextView textSchedTime;
    private TextView textFrom;
    private TextView servicemode;
    private ViewPager viewPager;
    private LinearLayout sliderDotsPanel;
    private LinearLayout layoutFrom;
    private LinearLayout layoutDestination;
    private LinearLayout layoutServiceType;
    private LinearLayout layoutServiceMode;
    private LinearLayout layoutDeliveryMode;
    private LinearLayout layoutStartDate;
    private LinearLayout layoutEndDate;
    private LinearLayout layoutScheduleDate;
    private LinearLayout layoutScheduleTime;
    private LinearLayout layoutPackage;
    private LinearLayout layoutPrice;
    private LinearLayout layoutExcessRate;
    private LinearLayout layoutTotal;
    private LinearLayout layoutEndorse;
    private TextView textPackage;
    private TextView textPrice;
    private TextView textExcess;
    private Button acceptBtn;
    private Button declineBtn;

    //Firebase
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private ValueEventListener listener;

    //Variables
    private String carCodeHolder;
    private String ownerCodeHolder;
    private int dotscount;
    private ImageView[] dots;
    private StringBuilder stringBuilder;
    private String[] urls = new String[4];
    private String reservationCodeHolder;
    private ReservationFile reservationFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationdetails);

        refs(); getExtras();
        setupToolbar();
        setupFirebase();
        retrieveData();
    }

    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
    }

    private void refs() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        textDestination = findViewById(R.id.textDestination);
        textServiceType = findViewById(R.id.textServiceType);
        textServiceMode = findViewById(R.id.textServiceMode);
        textDeliveryMode = findViewById(R.id.textDeliveryMode);
        textStartDate = findViewById(R.id.textStartDate);
        textEndDate = findViewById(R.id.textEndDate);
        textTotal = findViewById(R.id.textTotal);
        textViewCarName = findViewById(R.id.textviewcarname);
        textViewPostedOn = findViewById(R.id.textviewpostedon);
        textViewContacts = findViewById(R.id.textviewcontact);
        textViewUserFullname = findViewById(R.id.txtuserfullname);
        textViewUserDateJoined = findViewById(R.id.txtprofileuserdatejoined);
        imageViewProfile = findViewById(R.id.imgviewprofilepic);
        viewPager = findViewById(R.id.viewpager);
        sliderDotsPanel = findViewById(R.id.sliderdots);
        servicemode = findViewById(R.id.servicemode);
        layoutDeliveryMode = findViewById(R.id.layoutDeliveryMode);
        layoutDestination = findViewById(R.id.layoutDestination);
        layoutEndDate = findViewById(R.id.layoutEndDate);
        layoutStartDate = findViewById(R.id.layoutStartDate);
        layoutServiceMode = findViewById(R.id.layoutServiceMode);
        layoutScheduleDate = findViewById(R.id.layoutScheduleDate);
        layoutScheduleTime = findViewById(R.id.layoutScheduleTime);
        layoutFrom = findViewById(R.id.layoutFrom);
        textSchedTime = findViewById(R.id.textScheduleTime);
        textSchedDate = findViewById(R.id.textScheduleDate);
        textFrom = findViewById(R.id.textFrom);
        layoutPackage = findViewById(R.id.layoutPackage);
        layoutPrice = findViewById(R.id.layoutPrice);
        layoutExcessRate = findViewById(R.id.layoutExcess);
        textPackage = findViewById(R.id.textPackage);
        textPrice = findViewById(R.id.textPrices);
        textExcess = findViewById(R.id.textExcesses);
        layoutServiceType = findViewById(R.id.layoutServiceType);
        layoutTotal = findViewById(R.id.layoutTotal);
        acceptBtn = findViewById(R.id.acceptButton);
        declineBtn = findViewById(R.id.declineButton);
        layoutEndorse = findViewById(R.id.hideEndorse);
    }

    private void getExtras() {
        Bundle b = getIntent().getExtras();
        ownerCodeHolder = b.getString("ownercode");
        carCodeHolder = b.getString("carcode");
        reservationCodeHolder = b.getString("reservationcode");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText("Notification Details");
    }

    public void retrieveData() {
        listener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    setProfileWidgets(getUserSettings(dataSnapshot));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private CarSettings getUserSettings(DataSnapshot dataSnapshot) {
        UDFile udFile = new UDFile();
        UHFile uhFile = new UHFile();
        CDFile cdFile = new CDFile();

        for (DataSnapshot ds: dataSnapshot.getChildren()) {
            if(ds.getKey().equals("UDFile")) {
                udFile = ds.child(ownerCodeHolder).getValue(UDFile.class);
            }
            if(ds.getKey().equals("UHFile")) {
                uhFile = ds.child(ownerCodeHolder).getValue(UHFile.class);
            }
            if(ds.getKey().equals("CDFile")) {
                cdFile = ds.child(carCodeHolder).getValue(CDFile.class);
            }
        }
        return new CarSettings(udFile, uhFile, cdFile);
    }

    public void setProfileWidgets(CarSettings uSettings) throws ParseException {
        myRef.removeEventListener(listener);
        UDFile udFile = uSettings.getUdFile();
        UHFile uhFile = uSettings.getUhFile();
        CDFile cdFile = uSettings.getCdFile();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
        String holder = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss", Locale.getDefault()).format(new Date());
        Date datePosted = simpleDateFormat.parse(cdFile.getCdpostedon());
        Date currentDate = simpleDateFormat.parse(holder);

        long different = currentDate.getTime() - datePosted.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapseDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapseHourse = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapseMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapseSeconds = different % secondsInMilli;
        String date = null;
        int w = 0;
        if(elapseDays >= 1) {
            if(elapseDays > 7) {
                while (elapseDays >= 7) {
                    elapseDays = elapseDays - 7;
                    w++;
                }
                if(w < 1) {
                    date = w + " week ago";
                } else {
                    date = w + " weeks ago";
                }
            }
            else {
                date = elapseDays + " days ago";
            }
        } else if(elapseHourse >= 1){
            if(elapseHourse < 1) {
                date = elapseHourse + " hour ago";
                Toast.makeText(this, " " + elapseHourse, Toast.LENGTH_SHORT).show();
            }
            else {
                date = elapseHourse + " hours ago";
            }
        } else if(elapseMinutes >= 1) {
            if(elapseMinutes < 1) {
                date = elapseMinutes + " minute ago";
            } else {
                date = elapseMinutes + " minutes ago";
            }
        } else {
            if(elapseSeconds < 1) {
                date = elapseSeconds + " second ago";
            } else {
                date = elapseSeconds + " seconds ago";
            }
        }
        stringBuilder = new StringBuilder();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        Query query = databaseReference.child("CDFile").child(cdFile.getCDCode()).child("photos");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    stringBuilder.append(singleSnapshot.getValue(CarPhotos.class).getImageUrl());
                    stringBuilder.append(",");
                }
                if(!dataSnapshot.exists()) {
                    sliderDotsPanel.setVisibility(View.GONE);
                }
                String[] picUrls = stringBuilder.toString().split(",");
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getApplicationContext(), picUrls);
                viewPager.setAdapter(viewPagerAdapter);
                dotscount = viewPagerAdapter.getCount();
                dots = new ImageView[dotscount];

                for(int k = 0; k < dotscount; k++) {
                    dots[k] = new ImageView(getApplicationContext());
                    dots[k].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 0, 8, 0);
                    sliderDotsPanel.addView(dots[k], params);
                }

                dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        for(int i = 0; i < dotscount; i++) {
                            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                        }
                        dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        textViewCarName.setText(cdFile.getCDMaker() + " " + cdFile.getCDModel() + " " + cdFile.getCdcaryear());
        textViewPostedOn.setText(date);
        textViewUserDateJoined.setText(uhFile.getUhdatecreated());
        textViewUserFullname.setText(udFile.getUDFullname());
        textViewContacts.setText(udFile.getUDContact());

        Glide.with(getApplicationContext())
                .load(udFile.getUDImageProfile())
                .into(imageViewProfile);
        imageViewProfile.setBackgroundResource(0);

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);


        //Check if the user already reserved this car
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uID = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query checkQuery = reference
                .child("ReservationFile");

        checkQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                ReservationFile reservationFile = new ReservationFile();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(ReservationFile.class).getResCode().equalsIgnoreCase(reservationCodeHolder)) {
                        reservationFile = snapshot.getValue(ReservationFile.class);
                        setupBookingDetails(reservationFile);
                        break;
                    }
                }
//                setupBookingDetails(reservationFile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupBookingDetails(ReservationFile reservationFile) {
        layoutEndorse.setVisibility(View.GONE);
        if("Self Drive".equalsIgnoreCase(reservationFile.getResServiceType())) {
            if (reservationFile.getResStatus().equalsIgnoreCase("Approved")) {
                acceptBtn.setEnabled(false);
                declineBtn.setVisibility(View.GONE);
                acceptBtn.setText("Booking accepted!");
            }

            if (reservationFile.getResStatus().equalsIgnoreCase("Declined")) {
                declineBtn.setEnabled(false);
                acceptBtn.setVisibility(View.GONE);
                declineBtn.setText("Booking declined!");
            }
            if (reservationFile.getResStatus().equalsIgnoreCase("Cancelled")) {
                declineBtn.setEnabled(false);
                acceptBtn.setVisibility(View.GONE);
                declineBtn.setText("Booking cancelled!");
                declineBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.orange_primary));
            }
            if (reservationFile.getResStatus().equalsIgnoreCase("Done")) {
                acceptBtn.setEnabled(false);
                declineBtn.setVisibility(View.GONE);
                acceptBtn.setText("Booking Ended");
                acceptBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.blue));
            }
            layoutServiceType.setVisibility(View.VISIBLE);
            layoutStartDate.setVisibility(View.VISIBLE);
            layoutEndDate.setVisibility(View.VISIBLE);
            layoutTotal.setVisibility(View.VISIBLE);
            layoutDeliveryMode.setVisibility(View.VISIBLE);
            layoutServiceMode.setVisibility(View.GONE);
            textServiceType.setText(reservationFile.getResServiceType());
            textStartDate.setText(reservationFile.getResDateStart());
            textEndDate.setText(reservationFile.getResDateEnd());
            textTotal.setText(String.valueOf(reservationFile.getResTotal()));
            textDeliveryMode.setText(reservationFile.getResDeliveryMode());

        } else if("Out of Town".equalsIgnoreCase(reservationFile.getResServiceType())) {
            if (reservationFile.getResStatus().equalsIgnoreCase("Approved")) {
                acceptBtn.setEnabled(false);
                declineBtn.setVisibility(View.GONE);
                acceptBtn.setText("Booking accepted!");
            }

            if (reservationFile.getResStatus().equalsIgnoreCase("Declined")) {
                declineBtn.setEnabled(false);
                acceptBtn.setVisibility(View.GONE);
                declineBtn.setText("Booking declined!");
            }
            if (reservationFile.getResStatus().equalsIgnoreCase("Cancelled")) {
                declineBtn.setEnabled(false);
                acceptBtn.setVisibility(View.GONE);
                declineBtn.setText("Booking cancelled!");
                declineBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.orange_primary));
            }
            if (reservationFile.getResStatus().equalsIgnoreCase("Done")) {
                acceptBtn.setEnabled(false);
                declineBtn.setVisibility(View.GONE);
                acceptBtn.setText("Booking Ended");
                acceptBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.blue));
            }
            layoutServiceType.setVisibility(View.VISIBLE);
            layoutDestination.setVisibility(View.VISIBLE);
            layoutServiceMode.setVisibility(View.VISIBLE);
            layoutDeliveryMode.setVisibility(View.GONE);
            layoutScheduleDate.setVisibility(View.VISIBLE);
            layoutScheduleTime.setVisibility(View.VISIBLE);
            layoutTotal.setVisibility(View.VISIBLE);
            layoutFrom.setVisibility(View.VISIBLE);

            textServiceType.setText(reservationFile.getResServiceType());
            textDestination.setText(reservationFile.getResDestination());
            textServiceMode.setText(reservationFile.getResServiceMode());
            textTotal.setText(String.valueOf(reservationFile.getResTotal()));
            textSchedTime.setText(reservationFile.getResTimeSchedule());
            textSchedDate.setText(reservationFile.getResDateSchedule());
            textFrom.setText(reservationFile.getResFrom());
        }
        else if("City Tour".equalsIgnoreCase(reservationFile.getResServiceType()) || "Top Hills Tour".equalsIgnoreCase(reservationFile.getResServiceType())) {
            if (reservationFile.getResStatus().equalsIgnoreCase("Approved")) {
                acceptBtn.setEnabled(false);
                declineBtn.setVisibility(View.GONE);
                acceptBtn.setText("Booking accepted!");
            }

            if (reservationFile.getResStatus().equalsIgnoreCase("Declined")) {
                declineBtn.setEnabled(false);
                acceptBtn.setVisibility(View.GONE);
                declineBtn.setText("Booking declined!");
            }
            if (reservationFile.getResStatus().equalsIgnoreCase("Cancelled")) {
                declineBtn.setEnabled(false);
                acceptBtn.setVisibility(View.GONE);
                declineBtn.setText("Booking cancelled!");
                declineBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.orange_primary));
            }
            if (reservationFile.getResStatus().equalsIgnoreCase("Done")) {
                acceptBtn.setEnabled(false);
                declineBtn.setVisibility(View.GONE);
                acceptBtn.setText("Booking Ended");
                acceptBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.blue));
            }
            layoutServiceType.setVisibility(View.VISIBLE);
            layoutScheduleDate.setVisibility(View.VISIBLE);
            layoutScheduleTime.setVisibility(View.VISIBLE);
            layoutPrice.setVisibility(View.VISIBLE);
            layoutPackage.setVisibility(View.VISIBLE);
            layoutExcessRate.setVisibility(View.VISIBLE);

            textSchedDate.setText(reservationFile.getResDateSchedule());
            textSchedTime.setText(reservationFile.getResTimeSchedule());
            textPackage.setText(reservationFile.getResPackage());
            textPrice.setText(reservationFile.getResPrice());
            textExcess.setText(reservationFile.getResExcessRate());
            textServiceType.setText(reservationFile.getResServiceType());
        }else if(reservationFile.getResServiceType().equalsIgnoreCase("Drop-off") ||
                reservationFile.getResServiceType().equalsIgnoreCase("Pick-up")){
            if (reservationFile.getResStatus().equalsIgnoreCase("Approved")) {
                acceptBtn.setEnabled(false);
                declineBtn.setVisibility(View.GONE);
                acceptBtn.setText("Booking Accepted");
            }

            if (reservationFile.getResStatus().equalsIgnoreCase("Declined")) {
                declineBtn.setEnabled(false);
                acceptBtn.setVisibility(View.GONE);
                declineBtn.setText("Booking Declined");
            }

            if (reservationFile.getResStatus().equalsIgnoreCase("Cancelled")) {
                declineBtn.setEnabled(false);
                acceptBtn.setVisibility(View.GONE);
                declineBtn.setText("Booking Cancelled");
                declineBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.orange_primary));
            }
            if (reservationFile.getResStatus().equalsIgnoreCase("Done")) {
                acceptBtn.setEnabled(false);
                declineBtn.setVisibility(View.GONE);
                acceptBtn.setText("Booking Ended");
                acceptBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.blue));
            }
            layoutServiceType.setVisibility(View.VISIBLE);
            layoutDestination.setVisibility(View.VISIBLE);
            layoutServiceMode.setVisibility(View.VISIBLE);
            layoutDeliveryMode.setVisibility(View.GONE);
            layoutScheduleDate.setVisibility(View.VISIBLE);
            layoutScheduleTime.setVisibility(View.VISIBLE);
            layoutTotal.setVisibility(View.VISIBLE);
            layoutFrom.setVisibility(View.VISIBLE);

            textServiceType.setText(reservationFile.getResServiceType());
            textDestination.setText(reservationFile.getResDestination());
            textServiceMode.setVisibility(View.GONE);
            textTotal.setText(String.valueOf(reservationFile.getResTotal()));
            textSchedTime.setText(reservationFile.getResDateSchedule());
            textSchedDate.setText(reservationFile.getResTimeSchedule());
            textFrom.setText(reservationFile.getResFrom());
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        else {
            return true;
        }
    }

}
