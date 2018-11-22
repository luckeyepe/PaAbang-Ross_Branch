package capstone.abang.com.Car_Renter.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vlk.multimager.utils.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.Like;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.FilterDialog;
import capstone.abang.com.Utils.Heart;
import capstone.abang.com.Utils.UniversalImageLoader;

public class CarsActivity extends AppCompatActivity implements FilterDialog.FilterDialogListener {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private android.support.v7.widget.Toolbar toolbar;
    private TextView toolbar_title;
    private DatabaseReference myRef;
    private TextView startDate;
    private TextView endDate;
    private TextView filter;
    private UHFile uhFile;
    private FirebaseRecyclerAdapter<CDFile, ShowViewHolder> firebaseRecyclerAdapter;
    private DatePickerDialog.OnDateSetListener startDateListener, endDateListener;
    private String catCode;
    private String catDesc;
    private CDFile cdFile;
    private LinearLayout linearLayout;
    private Boolean mLikedByCurrentUser;
    private StringBuilder mUsers;
    private UDFile udFile;
    private int start_year, start_month, start_day, end_year, end_month, end_day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
//        endDate = findViewById(R.id.endDate);
//        startDate = findViewById(R.id.startDate);
//        filter = findViewById(R.id.filter);
        Bundle bundle = getIntent().getExtras();
        catCode = bundle.getString("code");
        catDesc = bundle.getString("category");
//        Calendar startDate = Calendar.getInstance();
//        start_year = startDate.get(Calendar.YEAR);
//        start_month = startDate.get(Calendar.MONTH);
//        start_day = startDate.get(Calendar.DAY_OF_MONTH);
        initImageLoader();
        setupFirebase();
        setupRecyclerView();
        populateRecyclerView();
        setupToolbar();
        init();
    }

    private void init() {
//        filter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openDialog();
//            }
//        });
//
//        startDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog dpd = DatePickerDialog.newInstance(startDateListener,
//                        start_year,
//                        start_month,
//                        start_day);
//                dpd.show(getFragmentManager(), "Date picker dialog");
//            }
//        });
//
//        startDateListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar c = Calendar.getInstance();
//                c.set(Calendar.YEAR, year);
//                c.set(Calendar.MONTH, monthOfYear);
//                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                start_year = year;
//                start_month = monthOfYear;
//                start_day = dayOfMonth;
//                String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
//
//                startDate.setText(currentDateString);
//            }
//        };
//
//        endDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog dpd = DatePickerDialog.newInstance(endDateListener,
//                        start_year,
//                        start_month,
//                        start_day);
//                dpd.show(getFragmentManager(), "Date picker dialog");
//            }
//        });
//
//        endDateListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar c = Calendar.getInstance();
//                c.set(Calendar.YEAR, year);
//                c.set(Calendar.MONTH, monthOfYear);
//                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                end_year = year;
//                end_month = monthOfYear;
//                end_day = dayOfMonth;
//                String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
//
//                endDate.setText(currentDateString);
//            }
//        };
    }

//    private void openDialog() {
//        FilterDialog filterDialog = new FilterDialog();
//        filterDialog.show(getSupportFragmentManager(), "filter dialog");
//    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText(catDesc);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchfile, menu);
        final MenuItem myActionMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        changeSearchViewTextColor(searchView);
        ((EditText) searchView.findViewById(
                android.support.v7.appcompat.R.id.search_src_text)).
                setHintTextColor(getResources().getColor(R.color.white));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                populateRecyclerView();
                return false;
            }
        });


        return true;

    }

    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    @Override
    public void applyText(String sort, final String transmission) {
        Toast.makeText(this, "" + sort + " " + transmission, Toast.LENGTH_SHORT).show();
    }

    private void search(final String searchText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CDFile");
//        Query query = reference.orderByChild("cdmaker").startAt(searchText).endAt("\uf8ff");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CDFile, ShowViewHolder>
                (CDFile.class, R.layout.layout_model_cars_caractivity, ShowViewHolder.class, reference) {
            @Override
            protected void populateViewHolder(final ShowViewHolder viewHolder, final CDFile model, int position) {
                if(catCode.equals(model.getCdcatcode())) {
                    if(model.getCDMaker().toLowerCase().contains(searchText.toLowerCase()) ||
                            model.getCDModel().toLowerCase().contains(searchText.toLowerCase())) {
                        viewHolder.setCarName(model.getCDMaker() + " " + model.getCDModel() + " " + model.getCdcaryear());
                        viewHolder.setCarImage(model.getCDPhoto());
                        String date = dateUtils(model);
                        getOwner(model, viewHolder);
                        viewHolder.carPostedOn.setText(date);
                        viewHolder.setLikes(model.getCdlikes());
                        viewHolder.carRented.setText(model.getCdtransactions() + " transaction(s)");
                        final Heart mHeart = viewHolder.viewHeart();
                        viewHolder.heartWhite.setVisibility(View.VISIBLE);
                        viewHolder.heartRed.setVisibility(View.GONE);
                        viewHolder.heartWhite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addNewLike(mHeart, model, viewHolder);
                            }
                        });
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child("CDFile")
                                .child(model.getCDCode())
                                .child("likes");

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if(snapshot.getValue(Like.class).getUHuserCode().equalsIgnoreCase(uID)) {
                                        final String keyID = snapshot.getKey();
                                        viewHolder.heartWhite.setVisibility(View.GONE);
                                        viewHolder.heartRed.setVisibility(View.VISIBLE);
                                        viewHolder.heartRed.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                ref.child("CDFile").child(model.getCDCode()).child("likes")
                                                        .child(keyID).removeValue();
                                                int len = model.getCdlikes() - 1;
                                                ref.child("CDFile").child(model.getCDCode()).child("cdlikes").setValue(len);
                                                viewHolder.setLikes(len);
                                            }
                                        });
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), CarDetailsActivity.class);
                                intent.putExtra("carcode", model.getCDCode());
                                intent.putExtra("usercode", model.getCdowner());
                                intent.putExtra("startdate", startDate.getText().toString());
                                intent.putExtra("enddate", endDate.getText().toString());
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        viewHolder.cardView.setVisibility(View.GONE);
                    }
                }
                else {
                    viewHolder.cardView.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void populateRecyclerView() {
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CDFile, ShowViewHolder>
                (CDFile.class, R.layout.layout_model_cars_caractivity, ShowViewHolder.class, myRef) {
            @Override
            protected void populateViewHolder(final ShowViewHolder viewHolder, final CDFile model, int position) {
                if(catCode.equals(model.getCdcatcode()) && model.getCdapplicationstatus().equalsIgnoreCase("Approved")) {
                    viewHolder.setCarName(model.getCDMaker() + " " + model.getCDModel() + " " + model.getCdcaryear());
                    viewHolder.setCarImage(model.getCDPhoto());
                    String date = dateUtils(model);
                    getOwner(model, viewHolder);
                    viewHolder.carPostedOn.setText(date);
                    viewHolder.setLikes(model.getCdlikes());
                    viewHolder.carRented.setText(model.getCdtransactions() + " transaction(s)");
                    viewHolder.cardView.setVisibility(View.VISIBLE);
                    final Heart mHeart = viewHolder.viewHeart();
                    viewHolder.heartWhite.setVisibility(View.VISIBLE);
                    viewHolder.heartRed.setVisibility(View.GONE);
                    viewHolder.heartWhite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addNewLike(mHeart, model, viewHolder);
                        }
                    });

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child("CDFile")
                            .child(model.getCDCode())
                            .child("likes");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(snapshot.getValue(Like.class).getUHuserCode().equalsIgnoreCase(uID)) {
                                    final String keyID = snapshot.getKey();
                                    viewHolder.heartWhite.setVisibility(View.GONE);
                                    viewHolder.heartRed.setVisibility(View.VISIBLE);
                                    viewHolder.heartRed.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                            ref.child("CDFile").child(model.getCDCode()).child("likes")
                                                    .child(keyID).removeValue();
                                            int len = model.getCdlikes() - 1;
                                            ref.child("CDFile").child(model.getCDCode()).child("cdlikes").setValue(len);
                                            viewHolder.setLikes(len);
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), CarDetailsActivity.class);
                                intent.putExtra("carcode", model.getCDCode());
                                intent.putExtra("usercode", model.getCdowner());
                                startActivity(intent);
                        }
                    });
                }
                else {
                    viewHolder.cardView.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void addNewLike(Heart mHeart, CDFile model, ShowViewHolder viewHolder) {
        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUHuserCode(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.child(model.getCDCode()).child("likes").child(newLikeID)
                .setValue(like);
        myRef.child(model.getCDCode()).child("cdlikes").setValue(model.getCdlikes()+1);
        mHeart.toggleLike();
    }


    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.show_data_recycler_view);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("CDFile");
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private String dateUtils(CDFile model) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
        String holder = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss", Locale.getDefault()).format(new Date());
        Date datePosted = null;
        try {
            datePosted = simpleDateFormat.parse(model.getCdpostedon());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date currentDate = null;
        try {
            currentDate = simpleDateFormat.parse(holder);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
                    date = w + " wk ago";
                } else {
                    date = w + " wks ago";
                }
            }
            else {
                date = elapseDays + " ds ago";
            }
        } else if(elapseHourse >= 1){
            if(elapseHourse < 2) {
                date = elapseHourse + " hr ago";
            }
            else {
                date = elapseHourse + " hrs ago";
            }
        } else if(elapseMinutes >= 1) {
            if(elapseMinutes < 2) {
                date = elapseMinutes + " min ago";
            } else {
                date = elapseMinutes + " mins ago";
            }
        } else {
            if(elapseSeconds < 2) {
                date = elapseSeconds + " sec ago";
            } else {
                date = elapseSeconds + " secs ago";
            }
        }
        return date;
    }

    private void getOwner(final CDFile model, final ShowViewHolder viewHolder) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().equals("UDFile")) {
                        udFile = ds.child(model.getCdowner()).getValue(UDFile.class);
                    }
                }
                viewHolder.carOwner.setText(udFile.getUDFullname());
               // UniversalImageLoader.setImage(udFile.getUDImageProfile(), viewHolder.carOwnerPhoto, null, "");


                    Glide.with(getApplicationContext())
                            .load(udFile.getUDImageProfile())
                            .apply(new RequestOptions()
                                            .dontAnimate()
                                      )
                            .into(viewHolder.carOwnerPhoto);

                viewHolder.carOwnerPhoto.setBackgroundResource(0);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class ShowViewHolder extends RecyclerView.ViewHolder {
        private final TextView carName;
        private final ImageView carPhoto;
        private final CardView cardView;
        private final ImageView heartWhite;
        private final ImageView heartRed;
        private final TextView carLikes;
        private final TextView carPostedOn;
        private final TextView carOwner;
        private final ImageView carOwnerPhoto;
        private final TextView carRented;
        private Heart mHeart;
        public ShowViewHolder(View itemView) {
            super(itemView);
            heartWhite = itemView.findViewById(R.id.imgviewwhiteheart);
            heartRed = itemView.findViewById(R.id.imgviewredheart);
            carName = itemView.findViewById(R.id.textviewcarname);
            carPhoto = itemView.findViewById(R.id.imageviewcar);
            cardView = itemView.findViewById(R.id.cardviewcars);
            mHeart = new Heart(heartWhite, heartRed);
            carLikes = itemView.findViewById(R.id.textviewfavourites);
            carPostedOn = itemView.findViewById(R.id.textviewpost);
            carOwner = itemView.findViewById(R.id.textviewcarowner);
            carOwnerPhoto = itemView.findViewById(R.id.imgviewprofilepic);
            carRented = itemView.findViewById(R.id.textviewrented);
        }
        private void setLikes(int holder) {
            carLikes.setText("" + holder);
        }
        private Heart viewHeart() {
            return mHeart;
        }
        private void setCarName(String title) {
            carName.setText(title);
        }
        private void setCarImage(String title) {
           UniversalImageLoader.setImage(title, carPhoto, null,"");


        }
    }
}