package capstone.abang.com.Car_Renter.Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.internal.ClientApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vlk.multimager.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import capstone.abang.com.Car_Renter.Car_Renter;
import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.CarPhotos;
import capstone.abang.com.Models.CarSettings;
import capstone.abang.com.Models.ReservationFile;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.UniversalImageLoader;
import capstone.abang.com.Utils.ViewPagerAdapter;

public class CarBookingDetailActivity extends AppCompatActivity {
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
    private TextView textPackage;
    private TextView textPrice;
    private TextView textExcess;
    private LinearLayout layoutCancel;
    private Button btnCancel;
    private Button btnViewCredentials;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_booking_detail);
        getExtras();
        setupWidgets();
        setupToolbar();
        setupFirebase();
        retrieveData();
    }

    private void retrieveData() {
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

    private void setProfileWidgets(CarSettings uSettings) throws ParseException {
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


        //Check if the user already reserved this car
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uID = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query checkQuery = reference
                .child("CDFile")
                .child(carCodeHolder)
                .child("reservations");

        checkQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ReservationFile reservationFile = new ReservationFile();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(ReservationFile.class).getResRenterCode().equalsIgnoreCase(uID) &&
                            snapshot.getValue(ReservationFile.class).getResStatus().equalsIgnoreCase("Approved")) {
                        reservationFile = snapshot.getValue(ReservationFile.class);
                        break;
                    }
                }
                setupBookingDetails(reservationFile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupBookingDetails(final ReservationFile reservationFile) {
        int numRemaining = getTotalDays(reservationFile);

        if(numRemaining >= 3) {
            layoutCancel.setVisibility(View.VISIBLE);
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(CarBookingDetailActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(CarBookingDetailActivity.this);
                }

                builder.setTitle("Booking cancellation")
                        .setMessage("Do you really want to cancel this booking?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CDFile").child(carCodeHolder).child("reservations");
                                reference.child(reservationFile.getResCode()).removeValue();
                                DatabaseReference references = FirebaseDatabase.getInstance().getReference("ReservationFile");
                                references.child(reservationFile.getResCode()).child("resStatus").setValue("Cancelled");
                                DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("BookingFile");
                                reference3.child(reservationFile.getResCode()).child("bstatus").setValue("Cancelled");
                                Intent intent = new Intent(getApplicationContext(), CarDetailsActivity.class);
                                intent.putExtra("carcode", carCodeHolder);
                                intent.putExtra("usercode", ownerCodeHolder);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .show();
            }
        });


        btnViewCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewCredentialsActivity.class);
                intent.putExtra("carcode", carCodeHolder);
                intent.putExtra("usercode", ownerCodeHolder);
                startActivity(intent);
            }
        });




        if(reservationFile.getResServiceType().equalsIgnoreCase("Self Drive")) {
            layoutServiceType.setVisibility(View.VISIBLE);
            layoutStartDate.setVisibility(View.VISIBLE);
            layoutEndDate.setVisibility(View.VISIBLE);
            layoutTotal.setVisibility(View.VISIBLE);
            layoutDeliveryMode.setVisibility(View.VISIBLE);
            textServiceType.setText(reservationFile.getResServiceType());
            textStartDate.setText(reservationFile.getResDateStart());
            textEndDate.setText(reservationFile.getResDateEnd());
            textTotal.setText(String.valueOf(reservationFile.getResTotal()));
            textDeliveryMode.setText(reservationFile.getResDeliveryMode());

        }
        else if(reservationFile.getResServiceType().equalsIgnoreCase("Out of Town")) {
            layoutServiceType.setVisibility(View.VISIBLE);
            layoutDestination.setVisibility(View.VISIBLE);
            layoutServiceMode.setVisibility(View.VISIBLE);
            layoutScheduleDate.setVisibility(View.VISIBLE);
            layoutScheduleTime.setVisibility(View.VISIBLE);
            layoutTotal.setVisibility(View.VISIBLE);
            layoutFrom.setVisibility(View.VISIBLE);

            textServiceType.setText(reservationFile.getResServiceType());
            textDestination.setText(reservationFile.getResDestination());
            textServiceMode.setText(reservationFile.getResServiceMode());
            textTotal.setText(String.valueOf(reservationFile.getResTotal()));
            textSchedTime.setText(reservationFile.getResDateSchedule());
            textSchedDate.setText(reservationFile.getResTimeSchedule());
            textFrom.setText(reservationFile.getResFrom());

        }
        else if(reservationFile.getResServiceType().equalsIgnoreCase("City Tour") || reservationFile.getResServiceType().equalsIgnoreCase("Top Hills Tour")) {
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
        }
        else if(reservationFile.getResServiceType().equalsIgnoreCase("Drop-off") || reservationFile.getResServiceType().equalsIgnoreCase("Pick-up")) {
            layoutServiceType.setVisibility(View.VISIBLE);
            layoutDestination.setVisibility(View.VISIBLE);
            layoutScheduleDate.setVisibility(View.VISIBLE);
            layoutScheduleTime.setVisibility(View.VISIBLE);
            layoutTotal.setVisibility(View.VISIBLE);
            layoutFrom.setVisibility(View.VISIBLE);

            textServiceType.setText(reservationFile.getResServiceType());
            textDestination.setText(reservationFile.getResDestination());
            textServiceMode.setText(reservationFile.getResServiceMode());
            textTotal.setText(String.valueOf(reservationFile.getResTotal()));
            textSchedTime.setText(reservationFile.getResDateSchedule());
            textSchedDate.setText(reservationFile.getResTimeSchedule());
            textFrom.setText(reservationFile.getResFrom());
        }
    }

    private int getTotalDays(ReservationFile reservationFile) {
        int holder = 0;

        if(reservationFile.getResServiceType().equalsIgnoreCase("Self Drive")) {
            final Calendar c = Calendar.getInstance();
            String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
            DateFormat df1 = new SimpleDateFormat("MMM dd, yyyy");
            Date date1 = null;
            Date date2 = null;

            try {
                date1 = df1 .parse(currentDateString);
                date2 = df1 .parse(reservationFile.getResDateStart());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            while(!cal1.after(cal2))
            {
                Calendar dummmy = Calendar.getInstance();
                dummmy.setTime(cal1.getTime());
                holder = holder + 1;
                if(dummmy.getTime().toString().equalsIgnoreCase(cal2.getTime().toString())) {
                    break;
                }
                cal1.add(Calendar.DATE, 1);
            }
        }
        else {
            final Calendar c = Calendar.getInstance();
            String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
            DateFormat df1 = new SimpleDateFormat("MMM dd, yyyy");
            Date date1 = null;
            Date date2 = null;

            try {
                date1 = df1.parse(currentDateString);
                date2 = df1.parse(reservationFile.getResDateSchedule());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            while(!cal1.after(cal2))
            {
                Calendar dummmy = Calendar.getInstance();
                dummmy.setTime(cal1.getTime());
                holder = holder + 1;
                if(dummmy.getTime().toString().equalsIgnoreCase(cal2.getTime().toString())) {
                    break;
                }
                cal1.add(Calendar.DATE, 1);
            }
        }

        return holder;
    }

    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
    }

    private void getExtras() {
        Bundle b = getIntent().getExtras();
        carCodeHolder = b.getString("carcode");
        ownerCodeHolder = b.getString("ownercode");
    }

    private void setupWidgets() {
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
        layoutCancel = findViewById(R.id.layoutCancel);
        btnCancel = findViewById(R.id.btnCancel);
        btnViewCredentials = findViewById(R.id.viewCredentials);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText("Book Details");
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
