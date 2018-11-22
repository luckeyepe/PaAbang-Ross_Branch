package capstone.abang.com.Car_Renter.Home;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.os.UserHandle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vlk.multimager.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import capstone.abang.com.Chat.Chat;
import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.CarPhotos;
import capstone.abang.com.Models.CarSettings;
import capstone.abang.com.Models.ReservationFile;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.Models.USettings;
import capstone.abang.com.R;
import capstone.abang.com.Utils.UniversalImageLoader;
import capstone.abang.com.Utils.ViewPagerAdapter;

public class CarDetailsActivity extends AppCompatActivity {
    //widgets
    private Toolbar toolbar;
    private TextView toolbar_title;
    private TextView textViewCarName;
    private TextView textViewPostedOn;
    private TextView textViewPrice;
    private TextView textViewAddress;
    private TextView textViewRented;
    private TextView textViewCapacity;
    private TextView textViewTransmission;
    private TextView textViewLikes;
    private TextView textViewContacts;
    private TextView textViewUserFullname;
    private TextView textViewUserDateJoined;
    private ImageView imageViewPhoto;
    private ImageView imageViewProfile;
    private ViewPager viewPager;
    private LinearLayout sliderDotsPanel;
    private ValueEventListener listener;
    private Button btnBook;
    private Button btnViewDetails;
    private RelativeLayout bookingLayout;
    private RelativeLayout viewDetailsLayout;
    private ImageButton btnMessage;


    //vars
    private String carCodeHolder;
    private String ownerCodeHolder;
    private String startDateHolder;
    private String endDateHolder;
    private int dotscount;
    private ImageView[] dots;
    private ArrayList<CarPhotos> list;
    private String[] urls = new String[4];
    private StringBuilder stringBuilder;

    //Firebase
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        textViewCarName = findViewById(R.id.textviewcarname);
        textViewPostedOn = findViewById(R.id.textviewpostedon);
        textViewPrice = findViewById(R.id.textviewprice);
        textViewAddress = findViewById(R.id.textviewaddress);
        textViewRented = findViewById(R.id.textviewrented);
        textViewCapacity = findViewById(R.id.textviewpax);
        textViewTransmission = findViewById(R.id.textviewtransmission);
        textViewLikes = findViewById(R.id.textviewheart);
        textViewContacts = findViewById(R.id.textviewcontact);
        textViewUserFullname = findViewById(R.id.txtuserfullname);
        textViewUserDateJoined = findViewById(R.id.txtprofileuserdatejoined);
        //imageViewPhoto = findViewById(R.id.imgviewphoto);
        imageViewProfile = findViewById(R.id.imgviewprofilepic);
        viewPager = findViewById(R.id.viewpager);
        sliderDotsPanel = findViewById(R.id.sliderdots);
        btnBook = findViewById(R.id.bookbtn);
        btnViewDetails = findViewById(R.id.viewBookDetails);
        bookingLayout = findViewById(R.id.bookinglayout);
        viewDetailsLayout = findViewById(R.id.viewdetailslayout);
        btnMessage = findViewById(R.id.btnMsg);


        setSupportActionBar(toolbar);
        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText("Car Details");

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        Bundle b = getIntent().getExtras();
        carCodeHolder = b.getString("carcode");
        ownerCodeHolder = b.getString("usercode");
        startDateHolder = b.getString("startdate");
        endDateHolder = b.getString("enddate");
        Log.d("TAG", carCodeHolder);
        Log.d("TAG: USER", ownerCodeHolder);
        initImageLoader();
        retrieveData();



        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CarBookingActivity.class);
                intent.putExtra("carcode", carCodeHolder);
                intent.putExtra("ownercode", ownerCodeHolder);
                intent.putExtra("startdate", startDateHolder);
                intent.putExtra("enddate", endDateHolder);
                startActivity(intent);
            }
        });


    }
    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
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
        final UDFile udFile = uSettings.getUdFile();
        final UHFile uhFile = uSettings.getUhFile();
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
                if(w <= 1) {
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
        ValueEventListener listener;
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
        textViewPrice.setText(String.valueOf(1000.00));
        textViewAddress.setText(udFile.getUDAddr());
        textViewRented.setText(String.valueOf(cdFile.getCdtransactions()));
        textViewCapacity.setText(String.valueOf(cdFile.getCDCapacity()));
        textViewUserDateJoined.setText(uhFile.getUhdatecreated());
        textViewTransmission.setText(cdFile.getCDTransmission());
        textViewLikes.setText(String.valueOf(cdFile.getCdlikes()));
        textViewUserFullname.setText(udFile.getUDFullname());
        Log.d("TAG", " " + udFile.getUDFullname());
        textViewContacts.setText(udFile.getUDContact());



        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Chat.class);
                intent.putExtra("UID",ownerCodeHolder);
                intent.putExtra("Name",udFile.getUDFullname());
                startActivity(intent);
            }
        });

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

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
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(ReservationFile.class).getResRenterCode().equalsIgnoreCase(uID) &&
                            snapshot.getValue(ReservationFile.class).getResStatus().equalsIgnoreCase("Pending")) {
                        btnBook.setText("Request Pending");
                        btnBook.setClickable(false);
                        break;
                    }
                    else if(snapshot.getValue(ReservationFile.class).getResRenterCode().equalsIgnoreCase(uID) &&
                            snapshot.getValue(ReservationFile.class).getResStatus().equalsIgnoreCase("Approved")) {
                        bookingLayout.setVisibility(View.GONE);
                        viewDetailsLayout.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CarBookingDetailActivity.class);
                intent.putExtra("carcode", carCodeHolder);
                intent.putExtra("ownercode", ownerCodeHolder);
                startActivity(intent);
            }
        });


    }

//    public class MyTimerTask extends TimerTask{
//
//        @Override
//        public void run() {
//
//            CarDetailsActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    if(viewPager.getCurrentItem() == 0){
//                        viewPager.setCurrentItem(1);
//                    } else if(viewPager.getCurrentItem() == 1){
//                        viewPager.setCurrentItem(2);
//                    } else {
//                        viewPager.setCurrentItem(0);
//                    }
//
//                }
//            });
//
//        }
//    }

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
