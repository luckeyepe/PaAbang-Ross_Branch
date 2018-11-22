package capstone.abang.com.Car_Renter.Home;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.journeyapps.barcodescanner.ViewfinderView;
import com.vlk.multimager.utils.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import capstone.abang.com.Car_Renter.Car_Renter;
import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.CarPhotos;
import capstone.abang.com.Models.CarSettings;
import capstone.abang.com.Models.HourlyRates;
import capstone.abang.com.Models.ReservationFile;
import capstone.abang.com.Models.SelfDriveRates;
import capstone.abang.com.Models.Services;
import capstone.abang.com.Models.StartPoint;
import capstone.abang.com.Models.Terms;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.Models.WithDriverRates;
import capstone.abang.com.R;
import capstone.abang.com.Utils.PlaceAutocompleteAdapter;
import capstone.abang.com.Utils.ViewPagerAdapter;

public class CarBookingActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    //widgets
    private Toolbar toolbar;
    private TextView toolbar_title;
    private ViewPager viewPager;
    private LinearLayout sliderDotsPanel;
    private ValueEventListener listener;
    private TextView textViewCarName;
    private TextView textViewPostedOn;
    private TextView textViewContacts;
    private TextView startDate;
    private TextView endDate;
    private TextView schedDate;
    private TextView schedTime;
    private AutoCompleteTextView editTextSearch;
    private RadioButton roundTripBtn;
    private RadioButton dropOffBtn;
    private RadioButton pickupBtn;
    private RadioButton deliverBtn;
    private RadioGroup serviceMode;
    private RadioGroup deliveryMode;
    private LinearLayout serviceModeLayout;
    private LinearLayout deliveryModeLayout;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private DatePickerDialog.OnDateSetListener startDateListener, endDateListener, resDateListner;
    private TimePickerDialog.OnTimeSetListener resTimeListener;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    private Button requestButton;
    private Spinner spinnerStartDestination;
    private Spinner spinnerServices;
    private LinearLayout dateHeaderLayout;
    private LinearLayout scheduleDetail;
    private LinearLayout scheduleHeader;
    private LinearLayout layoutFromTo;
    private LinearLayout layoutSpan;
    private LinearLayout layoutTours;
    private LinearLayout layoutTotal;
    private LinearLayout layoutDelivery;
    private LinearLayout layoutServiceMode;
    private TextView textTotal;
    private TextView textPrice;
    private TextView textExcess;
    private LinearLayout layoutRequirements;
    private LinearLayout layoutOneWay;
    private Spinner spinnerPackages;
    private Spinner spinnerOneWay;
    private Spinner spinnerOneWayEnd;
    private TextView textfrom;
    private TextView textto;
    private Button viewTermsBtn;
    private Button downloadTermsBtn;
    private DownloadManager downloadManager;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));


    //Firebase
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;

    //Variables
    private Calendar[] days;
    private List<Calendar> blockedDays = new ArrayList<>();
    private String carCodeHolder;
    private String ownerCodeHolder;
    private String startDateHolder;
    private String endDateHolder;
    private CDFile carFile;
    private String destination;
    private String from;
    private int dotscount;
    private int hourOfDay;
    private int minOfDay;
    private int secOfDay;
    private int numOfDays;
    private ImageView[] dots;
    private StringBuilder startPointBuilder;
    private StringBuilder servicesBuilder;
    private StringBuilder onewayDestinationsStart;
    private StringBuilder onewayDestinationsEnd;
    private String service;


    private StringBuilder stringBuilder;
    private int start_year, start_month, start_day, end_year, end_month, end_day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_booking);




        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        Calendar startDate1 = Calendar.getInstance();
        start_year = startDate1.get(Calendar.YEAR);
        start_month = startDate1.get(Calendar.MONTH);
        start_day = startDate1.get(Calendar.DAY_OF_MONTH);


        Calendar calendar = Calendar.getInstance();
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        minOfDay = calendar.get(Calendar.MINUTE);
        secOfDay = calendar.get(Calendar.SECOND);


        Bundle b = getIntent().getExtras();
        carCodeHolder = b.getString("carcode");
        ownerCodeHolder = b.getString("ownercode");
        startDateHolder = b.getString("startdate");
        endDateHolder = b.getString("enddate");
        if(startDateHolder != null && endDateHolder != null) {
            startDate.setText(startDateHolder);
            endDate.setText(endDateHolder);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        castWidgets();
        setPackages();
        init();
        retrieveData();
        retriveAllReservations();
        retrieveStartPoints();
        retrieveServices();
        retrieveOneWay();


        days = blockedDays.toArray(new Calendar[blockedDays.size()]);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destination = editTextSearch.getText().toString();
                from = (String) spinnerStartDestination.getSelectedItem();
                service = (String) spinnerServices.getSelectedItem();

                if(service.equalsIgnoreCase("Self Drive")) {
                    if(selfDriveValid()) {
                        book();
                    }
                }
                else if (service.equalsIgnoreCase("Out of Town")) {
                    if(withDriverValid()) {
                        book();
                    }
                }
                else if (service.equalsIgnoreCase("City Tour") || service.equalsIgnoreCase("Top Hills Tour")) {
                    if(hourlyValid()) {
                        book();
                    }
                }
                else if(service.equalsIgnoreCase("Drop-off") || service.equalsIgnoreCase("Pick-up")) {
                    if(hourlyValid()) {
                        book();
                    }
                }
            }
        });

        viewTermsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarBookingActivity.this, ViewTermsActivity.class);
                startActivity(intent);
            }
        });

        downloadTermsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDownloadTerms();

            }
        });
    }

    private void performDownloadTerms() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Terms");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(Terms.class).getTosStatus().equalsIgnoreCase("AC")) {
                        download(snapshot.getValue(Terms.class).getTosFilePath());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void download(String path) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(path));
        request.setTitle("Terms and Agreements Download");
        request.setDescription("File is being downloaded...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String nameOfFile = URLUtil.guessFileName(path, null,
                MimeTypeMap.getFileExtensionFromUrl(path));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, nameOfFile);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }


    private boolean hourlyValid() {
        boolean flag = true;
        if(schedDate.getText().toString().equalsIgnoreCase("Start Date")) {
            schedDate.setError("Please input date");
            flag = false;
        }
        if(schedTime.getText().toString().equalsIgnoreCase("Time")) {
            schedTime.setError("Please input time");
            flag = false;
        }

        return flag;
    }

    private void setPackages() {
        String[] packages = {"Three hours", "Eight hours", "Ten hours", "Twelve hours"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, packages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPackages.setAdapter(adapter);
    }

    private boolean withDriverValid() {
        boolean valid = true;
        if(TextUtils.isEmpty(destination)) {
            editTextSearch.setError("Please specify your destination");
            valid = false;

        }
        if(schedDate.getText().toString().equalsIgnoreCase("Start Date")) {
            schedDate.setError("Please specify date");
            valid = false;

        }
        if(schedTime.getText().toString().equalsIgnoreCase("Time")) {
            schedTime.setError("Please specify time");
            valid = false;

        }
        if(serviceMode.getCheckedRadioButtonId() == -1) {
            Toast.makeText(CarBookingActivity.this, "Please input necessary fields!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    private boolean selfDriveValid() {
        boolean valid = true;
        if(startDate.getText().toString().equalsIgnoreCase("Start Date")) {
            startDate.setError("Please specify start date");
            valid = false;
        }
        if(endDate.getText().toString().equalsIgnoreCase("End Date")) {
            endDate.setError("Please specify end date");
            valid = false;
        }
        if(deliveryMode.getCheckedRadioButtonId() == -1 ) {
            Toast.makeText(CarBookingActivity.this, "Please input necessary fields!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    private void retrieveOneWay() {
        onewayDestinationsStart = new StringBuilder();
        onewayDestinationsEnd = new StringBuilder();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rates").child("One Way");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(!onewayDestinationsStart.toString().contains(snapshot.getValue(WithDriverRates.class).getStartingPoint())
                            && snapshot.getValue(WithDriverRates.class).getCategoryCode().equalsIgnoreCase(carFile.getCdcatcode())) {
                        onewayDestinationsStart.append(snapshot.getValue(WithDriverRates.class).getStartingPoint()).
                                append("/");
                    }
                    if(snapshot.getValue(WithDriverRates.class).getCategoryCode().equalsIgnoreCase(carFile.getCdcatcode())) {
                        onewayDestinationsEnd.append(snapshot.getValue(WithDriverRates.class).getEndPoint()).
                                append("/");
                    }
                }
                String[] holder = onewayDestinationsStart.toString().split("/");
                String[] holder2 = onewayDestinationsEnd.toString().split("/");
                setupOneWayDestinations(holder, holder2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void retrieveStartPoints() {
        startPointBuilder = new StringBuilder();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("StartPoint");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] aw;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    startPointBuilder.append(snapshot.getValue(StartPoint.class).getStartPoint());
                    startPointBuilder.append("/");
                }
                String[] holder = startPointBuilder.toString().split("/");
                setupStartingPoint(holder);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void retrieveServices() {
        servicesBuilder = new StringBuilder();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Services");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(Services.class).getServiceStatus().equalsIgnoreCase("AC")) {
                        servicesBuilder.append(snapshot.getValue(Services.class).getServiceName());
                        servicesBuilder.append("/");
                    }
                }
                String[] holder = servicesBuilder.toString().split("/");
                setupServices(holder);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupOneWayDestinations(String[] holder, String[] endPoints) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, holder);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOneWay.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, endPoints);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOneWayEnd.setAdapter(adapter1);
    }

    private void setupServices(String[] holder) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, holder);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServices.setAdapter(adapter);
    }

    private void setupStartingPoint(String[] holder) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, holder);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartDestination.setAdapter(adapter);
    }

    private void book() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String renterCode = user.getUid();
        String start = startDate.getText().toString();
        String end = endDate.getText().toString();
        String resDate = schedDate.getText().toString();
        String resTime = schedTime.getText().toString();
        float total = Float.parseFloat(textTotal.getText().toString());
        String services;
        String type = null;
        String mode = null;

        if(roundTripBtn.isChecked()) {
            type = "Round Trip";
        } else if(dropOffBtn.isChecked()) {
            type = "Drop Off";
        }

        if(pickupBtn.isChecked()) {
            mode = "Pick Up";
        } else if(deliverBtn.isChecked()) {
            mode = "Deliver";
        }
        service = (String) spinnerServices.getSelectedItem();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String reservationID = databaseReference.push().getKey();
        if(service.equalsIgnoreCase("Self Drive")) {
            ReservationFile reservationFile = new ReservationFile(reservationID, start, end, null, service, null, mode, total, ownerCodeHolder, renterCode, "Pending", carCodeHolder, null, null, null, null, null, null, null, "Booking");
            ReservationFile reservationFile2 = new ReservationFile(reservationID, start, end, null, service, null, mode, total, ownerCodeHolder, renterCode, "Pending", carCodeHolder, null, null, null, "Unseen", "0:00","Unseen", "0:00","Booking");
            databaseReference.child("CDFile").child(carCodeHolder).child("reservations").child(reservationID).setValue(reservationFile);
            databaseReference.child("ReservationFile").child(reservationID).setValue(reservationFile2);
        }
        else if(service.equalsIgnoreCase("Out of Town")) {
            ReservationFile reservationFile = new ReservationFile(reservationID, null, null, destination, service, type, null, total, ownerCodeHolder, renterCode, "Pending", carCodeHolder, from, resDate, resTime, null, null, null, null,"Booking");
            ReservationFile reservationFile3 = new ReservationFile(reservationID, null, null, destination, service, type, null, total, ownerCodeHolder, renterCode, "Pending", carCodeHolder, from, resDate, resTime, "Unseen", "0:00", "Unseen", "0:00","Booking");
            databaseReference.child("CDFile").child(carCodeHolder).child("reservations").child(reservationID).setValue(reservationFile);
            databaseReference.child("ReservationFile").child(reservationID).setValue(reservationFile3);
        }
        else if(service.equalsIgnoreCase("City Tour") || service.equalsIgnoreCase("Top Hills Tour")) {
            String pack = (String) spinnerPackages.getSelectedItem();
            String price = textPrice.getText().toString();
            String excessrate = textExcess.getText().toString();
            ReservationFile reservationFile = new ReservationFile(reservationID, ownerCodeHolder, renterCode, "Pending", carCodeHolder, resDate, resTime, null, null, null, null, price, pack, excessrate, service,"Booking");
            ReservationFile reservationFile1 = new ReservationFile(reservationID, ownerCodeHolder, renterCode, "Pending", carCodeHolder, resDate, resTime, "Unseen", "0:00", "Unseen", "0:00", price, pack, excessrate, service,"Booking");
            databaseReference.child("CDFile").child(carCodeHolder).child("reservations").child(reservationID).setValue(reservationFile);
            databaseReference.child("ReservationFile").child(reservationID).setValue(reservationFile1);
        }
        else if(service.equalsIgnoreCase("Drop-off") || service.equalsIgnoreCase("Pick-up")) {
            String from = (String) spinnerOneWay.getSelectedItem();
            String to = (String) spinnerOneWayEnd.getSelectedItem();
            if(service.equalsIgnoreCase("Drop-off")) {
                ReservationFile reservationFile = new ReservationFile(reservationID, null, null, to, service, type, null, total, ownerCodeHolder, renterCode, "Pending", carCodeHolder, from, resDate, resTime, null, null, null, null, "Booking");
                ReservationFile reservationFile3 = new ReservationFile(reservationID, null, null, to, service, type, null, total, ownerCodeHolder, renterCode, "Pending", carCodeHolder, from, resDate, resTime, "Unseen", "0:00", "Unseen", "0:00","Booking");
                databaseReference.child("CDFile").child(carCodeHolder).child("reservations").child(reservationID).setValue(reservationFile);
                databaseReference.child("ReservationFile").child(reservationID).setValue(reservationFile3);
            }
            else {
                ReservationFile reservationFile = new ReservationFile(reservationID, null, null, from, service, type, null, total, ownerCodeHolder, renterCode, "Pending", carCodeHolder, to, resDate, resTime, null, null, null, null, "Booking");
                ReservationFile reservationFile3 = new ReservationFile(reservationID, null, null, from, service, type, null, total, ownerCodeHolder, renterCode, "Pending", carCodeHolder, to, resDate, resTime, "Unseen", "0:00", "Unseen", "0:00","Booking");
                databaseReference.child("CDFile").child(carCodeHolder).child("reservations").child(reservationID).setValue(reservationFile);
                databaseReference.child("ReservationFile").child(reservationID).setValue(reservationFile3);
            }

        }

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Book request")
                .setMessage("Successfully requested" + "\n" + "Please wait for the owner's approval")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), Car_Renter.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(false)
                .show();
    }

    private void init() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);



        schedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dpd = TimePickerDialog.newInstance(resTimeListener,
                        hourOfDay,
                        minOfDay,
                        0,
                        false);
                String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
                if(currentDateString.equalsIgnoreCase(schedDate.getText().toString())) {
                    dpd.setMinTime(hourOfDay, minOfDay, 0);
                }
                dpd.show(getFragmentManager(), "Time picker dialog");
            }
        });

        resTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                boolean isPM = (hourOfDay >= 12);
                schedTime.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
            }
        };



        schedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(resDateListner,
                        start_year,
                        start_month,
                        start_day);
                retriveAllReservations();
                dpd.setMinDate(c);
                days = blockedDays.toArray(new Calendar[blockedDays.size()]);
                dpd.setDisabledDays(days);
                dpd.show(getFragmentManager(), "Date picker dialog");
            }
        });

        resDateListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                start_year = year;
                start_month = monthOfYear;
                start_day = dayOfMonth;
                String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());

                schedDate.setText(currentDateString);
            }
        };


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(startDateListener,
                        start_year,
                        start_month,
                        start_day);
                retriveAllReservations();
                dpd.setMinDate(c);
                days = blockedDays.toArray(new Calendar[blockedDays.size()]);
                dpd.setDisabledDays(days);
                dpd.show(getFragmentManager(), "Date picker dialog");
            }
        });

        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                start_year = year;
                start_month = monthOfYear;
                start_day = dayOfMonth;
                String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());

                startDate.setText(currentDateString);

            }
        };

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog dpd = DatePickerDialog.newInstance(endDateListener,
                        start_year,
                        start_month,
                        start_day);

                retriveAllReservations();
                if(startDate.getText().toString().equalsIgnoreCase("Start Date")) {
                    Toast.makeText(CarBookingActivity.this, "Please specify start date!", Toast.LENGTH_SHORT).show();
                } else {
                    days = blockedDays.toArray(new Calendar[blockedDays.size()]);
                    dpd.setMinDate(c);
                    dpd.setDisabledDays(days);
                    dpd.show(getFragmentManager(), "Date picker dialog");
                }
            }
        });

        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                end_year = year;
                end_month = monthOfYear;
                end_day = dayOfMonth;
                String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
                String start = startDate.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                Date startDate = null;
                Date enddDate = null;
                try {
                    startDate = dateFormat.parse(start);
                    enddDate = dateFormat.parse(currentDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
                String finalStartDate = timeFormat.format(startDate);
                String finalEndDate = timeFormat.format(enddDate);
                if(isValid(finalStartDate, finalEndDate)) {
                    endDate.setText(currentDateString);
                    getTotalDays(finalStartDate, finalEndDate);
                    calculateTotal();
                } else {
                    Toast.makeText(CarBookingActivity.this, "Please enter valid range of dates!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        serviceMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.RoundTrip) {
                    if(!editTextSearch.getText().toString().equalsIgnoreCase("")) {
                        calculateWithDriver("Round Trip");
                    }
                }
                else if(checkedId == R.id.DropOff) {
                    if(!editTextSearch.getText().toString().equalsIgnoreCase("")) {
                        calculateWithDriver("Drop Off");
                    }
                }
            }
        });
        spinnerPackages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if(selected.equalsIgnoreCase("Three hours")) {
                    setPrice("Three hours");
                }
                else if(selected.equalsIgnoreCase("Eight hours")) {
                    setPrice("Eight hours");
                }
                else if(selected.equalsIgnoreCase("Ten hours")) {
                    setPrice("Ten hours");
                }
                else if(selected.equalsIgnoreCase("Twelve hours")) {
                    setPrice("Twelve hours");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerOneWay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String From = parent.getItemAtPosition(position).toString();
                String To = (String) spinnerOneWayEnd.getSelectedItem();
                setOneWayTotal(From, To);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerOneWayEnd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String To = parent.getItemAtPosition(position).toString();
                String From = (String) spinnerOneWay.getSelectedItem();
                setOneWayTotal(From, To);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equalsIgnoreCase("Self Drive")) {
//                    serviceModeLayout.setVisibility(View.GONE);
//                    deliveryModeLayout.setVisibility(View.VISIBLE);
                    spinnerStartDestination.setVisibility(View.GONE);
                    dateHeaderLayout.setVisibility(View.VISIBLE);
                    editTextSearch.setVisibility(View.GONE);
                    layoutSpan.setVisibility(View.VISIBLE);
                    startDate.setVisibility(View.VISIBLE);
                    layoutRequirements.setVisibility(View.VISIBLE);
                    endDate.setVisibility(View.VISIBLE);
                    scheduleHeader.setVisibility(View.GONE);
                    scheduleDetail.setVisibility(View.GONE);
                    layoutFromTo.setVisibility(View.GONE);
                    layoutTotal.setVisibility(View.VISIBLE);
                    layoutTours.setVisibility(View.GONE);
                    layoutServiceMode.setVisibility(View.GONE);
                    layoutDelivery.setVisibility(View.VISIBLE);
                    layoutOneWay.setVisibility(View.GONE);
                }
                else if(selectedItem.equalsIgnoreCase("Out of Town")) {
//                    serviceModeLayout.setVisibility(View.VISIBLE);
//                    deliveryModeLayout.setVisibility(View.GONE);
                    spinnerStartDestination.setVisibility(View.VISIBLE);
                    dateHeaderLayout.setVisibility(View.GONE);
                    editTextSearch.setVisibility(View.VISIBLE);
                    layoutSpan.setVisibility(View.GONE);
                    layoutRequirements.setVisibility(View.GONE);
                    startDate.setVisibility(View.GONE);
                    endDate.setVisibility(View.GONE);
                    scheduleHeader.setVisibility(View.VISIBLE);
                    scheduleDetail.setVisibility(View.VISIBLE);
                    layoutFromTo.setVisibility(View.VISIBLE);
                    layoutTotal.setVisibility(View.VISIBLE);
                    layoutTours.setVisibility(View.GONE);
                    layoutServiceMode.setVisibility(View.VISIBLE);
                    layoutDelivery.setVisibility(View.GONE);
                    layoutOneWay.setVisibility(View.GONE);
                }
                else if(selectedItem.equalsIgnoreCase("City Tour") || selectedItem.equalsIgnoreCase("Top Hills Tour")) {
//                    serviceModeLayout.setVisibility(View.GONE);
//                    deliveryModeLayout.setVisibility(View.GONE);
                    spinnerStartDestination.setVisibility(View.GONE);
                    dateHeaderLayout.setVisibility(View.GONE);
                    editTextSearch.setVisibility(View.GONE);
                    layoutSpan.setVisibility(View.GONE);
                    startDate.setVisibility(View.GONE);
                    layoutRequirements.setVisibility(View.GONE);
                    endDate.setVisibility(View.GONE);
                    scheduleHeader.setVisibility(View.VISIBLE);
                    scheduleDetail.setVisibility(View.VISIBLE);
                    layoutFromTo.setVisibility(View.GONE);
                    layoutTours.setVisibility(View.VISIBLE);
                    layoutTotal.setVisibility(View.GONE);
                    layoutDelivery.setVisibility(View.GONE);
                    layoutServiceMode.setVisibility(View.GONE);
                    layoutOneWay.setVisibility(View.GONE);
                }
                else if(selectedItem.equalsIgnoreCase("Drop-off") || selectedItem.equalsIgnoreCase("Pick-up")) {
                    if(selectedItem.equalsIgnoreCase("Drop-off")) {
                        textto.setText("* To");
                        textfrom.setText("* From");
                    }
                    else {
                        textfrom.setText("* To");
                        textto.setText("* From");
                    }
                    spinnerStartDestination.setVisibility(View.GONE);
                    layoutOneWay.setVisibility(View.VISIBLE);
                    dateHeaderLayout.setVisibility(View.GONE);
                    editTextSearch.setVisibility(View.GONE);
                    layoutSpan.setVisibility(View.GONE);
                    startDate.setVisibility(View.GONE);
                    layoutRequirements.setVisibility(View.GONE);
                    endDate.setVisibility(View.GONE);
                    scheduleHeader.setVisibility(View.VISIBLE);
                    scheduleDetail.setVisibility(View.VISIBLE);
                    layoutFromTo.setVisibility(View.GONE);
                    layoutTours.setVisibility(View.GONE);
                    layoutTotal.setVisibility(View.VISIBLE);
                    layoutDelivery.setVisibility(View.GONE);
                    layoutServiceMode.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("PH")
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, autocompleteFilter);
        editTextSearch.setAdapter(mPlaceAutocompleteAdapter);


        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //execute method for searching
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void setOneWayTotal(final String from, final String to) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rates").child("One Way");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WithDriverRates withDriverRates;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    withDriverRates = snapshot.getValue((WithDriverRates.class));
                    if(withDriverRates.getStartingPoint().equalsIgnoreCase(from) && withDriverRates.getEndPoint().equalsIgnoreCase(to)
                            && withDriverRates.getCategoryCode().equalsIgnoreCase(carFile.getCdcatcode())) {
                        textTotal.setText(withDriverRates.getPrice());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setPrice(final String packageName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rates").child("Hourly");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String service = (String) spinnerServices.getSelectedItem();
                String serviceHolder = service.replaceAll("\\s","");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HourlyRates hourlyRates = snapshot.getValue(HourlyRates.class);
                    if(hourlyRates.getServiceCode().equalsIgnoreCase(serviceHolder)
                            && hourlyRates.getCategoryCode().equalsIgnoreCase(carFile.getCdcatcode())) {
                        textExcess.setText(hourlyRates.getPackageExcessHr());
                        if(packageName.equalsIgnoreCase("Three hours")) {
                            textPrice.setText(hourlyRates.getPackageThreeHrs());
                        }
                        else if(packageName.equalsIgnoreCase("Eight hours")) {
                            textPrice.setText(hourlyRates.getPackageEightHrs());
                        }
                        else if(packageName.equalsIgnoreCase("Ten hours")) {
                            textPrice.setText(hourlyRates.getPackageTenHrs());
                        }
                        else if(packageName.equalsIgnoreCase("Twelve hours")) {
                            textPrice.setText(hourlyRates.getPackageTwelveHrs());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void calculateWithDriver(final String service) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rates").child("Out of Town");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WithDriverRates withDriverRates;
                destination = editTextSearch.getText().toString();
                from = (String) spinnerStartDestination.getSelectedItem();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    withDriverRates = snapshot.getValue(WithDriverRates.class);
                    if(withDriverRates.getCategoryCode().equalsIgnoreCase(carFile.getCdcatcode())
                            && destination.contains(withDriverRates.getEndPoint())
                            && withDriverRates.getStartingPoint().equalsIgnoreCase(from)) {
                        double total = 0;
                        if(service.equalsIgnoreCase("Drop off")) {
                            total = Double.parseDouble(withDriverRates.getDropoffPrice());
                        }
                        else if(service.equalsIgnoreCase("Round Trip")) {
                            total = Double.parseDouble(withDriverRates.getRoundtripPrice());
                        }
                        textTotal.setText(String.valueOf(total));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void calculateTotal() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rates").child("Self Drive");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SelfDriveRates selfDriveRates = new SelfDriveRates();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    selfDriveRates = snapshot.getValue(SelfDriveRates.class);
                        if(carFile.getCDTransmission().equalsIgnoreCase("Automatic") && carFile.getCdcatcode().equalsIgnoreCase(selfDriveRates.getCategoryCode())) {
                            int weeks;
                            int months;
                            int daysRemaining;
                            double total = 0;
                            if(numOfDays >= 7) {
                                weeks = numOfDays / 7;
                                if(weeks >= 4) {
                                    months = weeks / 4;
                                    daysRemaining = numOfDays % 28;
                                    if(daysRemaining >= 7) {
                                        weeks = daysRemaining / 7;
                                        daysRemaining = daysRemaining % 7;
                                        if(weeks > 0) {
                                            total = total + weeks * Double.parseDouble(selfDriveRates.getAutoWeeklyPrice());
                                        }
                                        if(daysRemaining > 0){
                                            total = total + daysRemaining * Double.parseDouble(selfDriveRates.getAutoDailyPrice());
                                        }
                                    }
                                    else {
                                        total = total + daysRemaining * Double.parseDouble(selfDriveRates.getAutoDailyPrice());
                                    }
                                    total = total + months * Double.parseDouble(selfDriveRates.getAutoMonthlyPrice());

                                }
                                else {
                                    daysRemaining = numOfDays % 7;
                                    total = weeks * Double.parseDouble(selfDriveRates.getAutoWeeklyPrice());
                                    total = total + daysRemaining * Double.parseDouble(selfDriveRates.getAutoDailyPrice());
                                }
                            }
                            else {
                                total = numOfDays * Double.parseDouble(selfDriveRates.getAutoDailyPrice());
                            }
                            double totalHolder = total;
                            textTotal.setText(String.valueOf(totalHolder));
                        }
                        else if(carFile.getCDTransmission().equalsIgnoreCase("Manual")) {
                            int weeks;
                            int months;
                            int daysRemaining;
                            double total = 0;
                            if(numOfDays >= 7) {
                                weeks = numOfDays / 7;
                                if(weeks >= 4) {
                                    months = weeks / 4;
                                    daysRemaining = numOfDays % 28;
                                    if(daysRemaining >= 7) {
                                        weeks = daysRemaining / 7;
                                        daysRemaining = daysRemaining % 7;
                                        if(weeks > 0) {
                                            total = total + weeks * Double.parseDouble(selfDriveRates.getManualWeeklyPrice());
                                        }
                                        Toast.makeText(CarBookingActivity.this, "" + daysRemaining, Toast.LENGTH_SHORT).show();
                                        if(daysRemaining > 0){
                                            total = total + daysRemaining * Double.parseDouble(selfDriveRates.getManualDailyPrice());
                                        }
                                    }
                                    else {
                                        total = total + daysRemaining * Double.parseDouble(selfDriveRates.getManualDailyPrice());
                                    }
                                    total = total + months * Double.parseDouble(selfDriveRates.getManualMonthlyPrice());

                                }
                                else {
                                    daysRemaining = numOfDays % 7;
                                    total = weeks * Double.parseDouble(selfDriveRates.getManualWeeklyPrice());
                                    total = total + daysRemaining * Double.parseDouble(selfDriveRates.getManualDailyPrice());
                                }
                            }
                            else {
                                total = numOfDays * Double.parseDouble(selfDriveRates.getManualDailyPrice());
                            }
                            double totalHolder = total;
                            textTotal.setText(String.valueOf(totalHolder));
                        }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isValid(String start, String end) {
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        boolean b = false;
        try {
            b = dfDate.parse(start).before(dfDate.parse(end)) || dfDate.parse(start).equals(dfDate.parse(end));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

    private void getTotalDays(String start, String end) {
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        int holder = 0;
        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(start);
            date2 = df1 .parse(end);
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
        numOfDays = holder;
    }

    private void getDates(String start, String end) {
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(start);
            date2 = df1 .parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        blockedDays.add(cal1);
        while(!cal1.after(cal2))
        {
            Calendar dummmy = Calendar.getInstance();
            dummmy.setTime(cal1.getTime());
            blockedDays.add(dummmy);
            if(dummmy.getTime().toString().equalsIgnoreCase(cal2.getTime().toString())) {
                break;
            }
            cal1.add(Calendar.DATE, 1);
        }
    }

    private void retriveAllReservations() {
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
                    if(!snapshot.getValue(ReservationFile.class).getResStatus().equalsIgnoreCase("Done")) {
                        reservationFile = snapshot.getValue(ReservationFile.class);
                        if(reservationFile.getResServiceType().equalsIgnoreCase("Self Drive")) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                            Date startDate = null;
                            Date endDate = null;
                            try {
                                startDate = dateFormat.parse(reservationFile.getResDateStart());
                                endDate = dateFormat.parse(reservationFile.getResDateEnd());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String finalStartDate = timeFormat.format(startDate);
                            String finalEndDate = timeFormat.format(endDate);
                            getDates(finalStartDate, finalEndDate);
                        }
                        else {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                            Date startDate = null;
                            try {
                                startDate = dateFormat.parse(reservationFile.getResDateSchedule());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String finalStartDate = timeFormat.format(startDate);
                            getDatesVersionTwo(finalStartDate);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDatesVersionTwo(String date) {
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = null;

        try {
            date1 = df1 .parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        blockedDays.add(cal1);
    }

    private void geoLocate() {
        String searchString = editTextSearch.getText().toString();

        Geocoder geocoder = new Geocoder(CarBookingActivity.this);

        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        } catch (IOException e) {
            Log.d("TAG", "geoLocate: IOException" + e.getMessage());
        }

        if(list.size() > 0) {
            Address address = list.get(0);
        }


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

    public void setProfileWidgets(CarSettings uSettings) throws ParseException {
        myRef.removeEventListener(listener);
        carFile = uSettings.getCdFile();
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
        ValueEventListener listener;
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        Query query = databaseReference.child("CDFile").child(cdFile.getCDCode()).child("photos");
        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    stringBuilder.append(singleSnapshot.getValue(CarPhotos.class).getImageUrl());
                    stringBuilder.append(",");
                    i++;
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
        //query.removeEventListener(listener);

        textViewCarName.setText(cdFile.getCDMaker() + " " + cdFile.getCDModel() + " " + cdFile.getCdcaryear());
        textViewPostedOn.setText(date);
        Log.d("TAG", " " + udFile.getUDFullname());
        textViewContacts.setText(udFile.getUDContact());
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        viewPager = findViewById(R.id.viewpager);
        sliderDotsPanel = findViewById(R.id.sliderdots);
        textViewCarName = findViewById(R.id.textviewcarname);
        textViewPostedOn = findViewById(R.id.textviewpostedon);
        textViewContacts = findViewById(R.id.textviewcontact);
        editTextSearch = findViewById(R.id.searchEditText);
        roundTripBtn = findViewById(R.id.RoundTrip);
        dropOffBtn = findViewById(R.id.DropOff);
        serviceMode = findViewById(R.id.ServiceMode);
        deliveryMode = findViewById(R.id.ServiceAdd);
        pickupBtn = findViewById(R.id.PickUp);
        deliverBtn = findViewById(R.id.Deliver);
        endDate = findViewById(R.id.endDate);
        startDate = findViewById(R.id.startDate);
        spinnerStartDestination = findViewById(R.id.spinnerstartdestination);
        spinnerServices = findViewById(R.id.spinnerservices);
        dateHeaderLayout = findViewById(R.id.dateheaderlayout);
        scheduleDetail = findViewById(R.id.scheduledetails);
        scheduleHeader = findViewById(R.id.scheduleheader);
        schedDate = findViewById(R.id.withDriverDate);
        schedTime = findViewById(R.id.withDriverTime);
        layoutFromTo = findViewById(R.id.layoutFromTo);
        textTotal = findViewById(R.id.textTotal);
        layoutSpan = findViewById(R.id.layoutSpan);
        layoutTours = findViewById(R.id.layouttours);
        layoutTotal = findViewById(R.id.layouttotal);
        layoutDelivery = findViewById(R.id.layoutDelivery);
        layoutServiceMode = findViewById(R.id.layoutServiceMode);
        spinnerPackages = findViewById(R.id.spinnertours);
        spinnerOneWay = findViewById(R.id.spinneroneway);
        spinnerOneWayEnd = findViewById(R.id.spinneronewayend);
        textPrice = findViewById(R.id.textPrice);
        textExcess = findViewById(R.id.textExcess);
        layoutRequirements = findViewById(R.id.SelfDriveRequirements);
        layoutOneWay = findViewById(R.id.layoutOnewayServices);
        textfrom = findViewById(R.id.textfromone);
        textto = findViewById(R.id.textto);
        viewTermsBtn = findViewById(R.id.viewTermsBtn);
        downloadTermsBtn = findViewById(R.id.downloadTermsBtn);
        setSupportActionBar(toolbar);
        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText("Hire a Car");

        progressDialog = new ProgressDialog(this);
        requestButton = findViewById(R.id.reservebtn);
    }
}
