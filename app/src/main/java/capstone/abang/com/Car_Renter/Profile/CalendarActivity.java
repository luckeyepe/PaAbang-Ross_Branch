package capstone.abang.com.Car_Renter.Profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.vlk.multimager.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import capstone.abang.com.Car_Renter.Home.CarBookingDetailActivity;
import capstone.abang.com.Models.BookingFile;
import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.UniversalImageLoader;

public class CalendarActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private Toolbar toolbar;
    private TextView toolbar_title;
    private RecyclerView recyclerView;

    //Firebase
    private FirebaseRecyclerAdapter<BookingFile, ShowEvents> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);
        castWidgets();
        setupToolbar();
        retrieveAllReservations();
        setupRecyclerView();
        initImageLoader();
        init();


    }
    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.event_recycler_view);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(mLayoutManager);
    }
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText("Calendar");
    }
    private void init() {
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                String date1 = dateFormat.format(date.getDate());
                String holder;
                char[] dateHolder = date1.toCharArray();
                if(dateHolder[4] == '0') {
                    holder = date1.substring(0, 4) + date1.substring(5);
                    populateRecyclerView(holder);
                }
                else {
                    populateRecyclerView(date1);
                }

            }});
    }
    private void populateRecyclerView(final String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BookingFile");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BookingFile, ShowEvents>
                (BookingFile.class, R.layout.layout_model_events, ShowEvents.class, databaseReference) {
            @Override
            protected void populateViewHolder(ShowEvents viewHolder, final BookingFile model, int position) {
                String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(model.getBRenterCode().equalsIgnoreCase(uID) && model.getBStatus().equalsIgnoreCase("Booked")) {
                    if(model.getBServiceType().equalsIgnoreCase("Self Drive")) {
                        if(insideRange(model.getBDateStart(), model.getBDateEnd(), date)) {
                            setCar(viewHolder, model);
                            setRenter(viewHolder, model);
                            viewHolder.layout.setVisibility(View.VISIBLE);
                            viewHolder.setcarService(model.getBServiceType());
                        }
                        else {
                            viewHolder.layout.setVisibility(View.GONE);
                        }
                    }
                    else {
                        if(model.getBSchedDate().equalsIgnoreCase(date)) {
                            setCar(viewHolder, model);
                            setRenter(viewHolder, model);
                            viewHolder.layout.setVisibility(View.VISIBLE);
                            viewHolder.setcarService(model.getBServiceType());
                        }
                        else {
                            viewHolder.layout.setVisibility(View.GONE);
                        }
                    }
                }
                else {
                    viewHolder.layout.setVisibility(View.GONE);
                }

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), CarBookingDetailActivity.class);
                        intent.putExtra("carcode", model.getBCarCode());
                        intent.putExtra("ownercode", model.getBOwnerCode());
                        startActivity(intent);
                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    private void setRenter(final ShowEvents viewHolder, final BookingFile model) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UDFile");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(model.getBOwnerCode().equalsIgnoreCase(snapshot.getValue(UDFile.class).getUDUserCode())) {
                        UDFile udFile = snapshot.getValue(UDFile.class);
                        viewHolder.setcarRentee(udFile.getUDFullname());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setCar(final ShowEvents viewHolder, final BookingFile model) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CDFile");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(model.getBCarCode().equalsIgnoreCase(snapshot.getValue(CDFile.class).getCDCode())) {
                        CDFile cdFile = snapshot.getValue(CDFile.class);
                        if(cdFile != null) {
                            viewHolder.setcarName(cdFile.getCDMaker() + " " + cdFile.getCDModel() + " " + cdFile.getCdcaryear());
                            Glide.with(getApplicationContext())
                                    .load(cdFile.getCDPhoto())
                                    .apply(new RequestOptions()
                                            .dontAnimate()
                                    )
                                    .into(viewHolder.imageCar);
                            viewHolder.imageCar.setBackgroundResource(0);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private boolean insideRange(String dateStart, String dateEnd, String date) {
        boolean flag = false;

        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date date1 = null;
        Date date2 = null;
        Date date3 = null;

        try {
            date1 = dateFormat.parse(dateStart);
            date2 = dateFormat.parse(dateEnd);
            date3 = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(date3);

        while(!calendar1.after(calendar2))
        {
            if(calendar3.getTime().toString().equalsIgnoreCase(calendar1.getTime().toString())) {
                flag = true;
                break;
            }
            calendar1.add(Calendar.DATE, 1);
        }

        return flag;
    }
    private void retrieveAllReservations() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BookingFile");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(BookingFile.class).getBRenterCode().equalsIgnoreCase(uID) && snapshot.getValue(BookingFile.class).getBStatus().equalsIgnoreCase("Booked")) {
                        setBookedDays(snapshot.getValue(BookingFile.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setBookedDays(BookingFile bookingFile) {
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        if(bookingFile.getBServiceType().equalsIgnoreCase("Self Drive")) {
            Date date1 = null;
            Date date2 = null;

            try {
                date1 = dateFormat.parse(bookingFile.getBDateStart());
                date2 = dateFormat.parse(bookingFile.getBDateEnd());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(date1);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(date2);

            HashSet<CalendarDay> setDays = getCalendarDaysSet(calendar1, calendar2);
            int myColor = R.color.google_red;
            calendarView.addDecorator(new BookingDecorator(myColor, setDays));
        }
        else {
            Date date1 = null;

            try {
                date1 = dateFormat.parse(bookingFile.getBSchedDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(date1);
            HashSet<CalendarDay> setDays = new HashSet<>();
            CalendarDay calendarDay = CalendarDay.from(calendar1);
            setDays.add(calendarDay);
            int myColor = R.color.google_red;
            calendarView.addDecorator(new BookingDecorator(myColor, setDays));
        }
    }
    private HashSet<CalendarDay> getCalendarDaysSet(Calendar cal1, Calendar cal2) {
        HashSet<CalendarDay> setDays = new HashSet<>();
        while (!cal1.after(cal2)) {
            CalendarDay calDay = CalendarDay.from(cal1);
            setDays.add(calDay);
            cal1.add(Calendar.DATE, 1);
        }
        return setDays;
    }
    private class BookingDecorator implements DayViewDecorator {
        private int mColor;
        private HashSet<CalendarDay> mCallendarDayCollection;
        public BookingDecorator(int color, HashSet<CalendarDay> calendarDayCollection) {
            mColor = color;
            mCallendarDayCollection = calendarDayCollection;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return mCallendarDayCollection.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(mColor));
            view.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
        }
    }
    private void castWidgets() {
        calendarView = findViewById(R.id.calendarView);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
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

    private static class ShowEvents extends RecyclerView.ViewHolder {
        private final TextView carName;
        private final TextView carRentee;
        private final TextView carService;
        private final LinearLayout layout;
        private final ImageView imageCar;

        public ShowEvents(View itemView) {
            super(itemView);
            carName = itemView.findViewById(R.id.carname);
            carRentee = itemView.findViewById(R.id.carrentee);
            carService = itemView.findViewById(R.id.carservice);
            layout = itemView.findViewById(R.id.layoutevents);
            imageCar = itemView.findViewById(R.id.imgviewcar);
        }
        private void setcarName(String text) { carName.setText(text); }
        private void setcarRentee(String text) { carRentee.setText(text); }
        private void setcarService(String title) {
            carService.setText(title);
        }
        private void setCarImage(String text) {
            UniversalImageLoader.setImage(text, imageCar, null, "");
        }

    }
}
