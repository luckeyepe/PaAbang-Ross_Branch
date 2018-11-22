package capstone.abang.com.Car_Renter.Home;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Timer;
import java.util.TimerTask;

import capstone.abang.com.Car_Owner.InboxActivity;
import capstone.abang.com.Models.AdsFile;
import capstone.abang.com.Models.CategoryFile;
import capstone.abang.com.Models.ReservationFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.AdsPagerAdapter;
import capstone.abang.com.Utils.UniversalImageLoader;


public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    Timer timer;
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseRecyclerAdapter<CategoryFile, ShowDataViewHolder> mFirebaseDatabase;
    //viewpager
    private FirebaseDatabase fdb;
    private ViewPager adsPager;
    private StringBuilder stringBuilder;
    private int dotscount;
    private ImageView[] dots;
    private ValueEventListener listener;
    private LinearLayout sliderDotsPanel;
    private int current = 0;
    Query checkQuery;
    private DatabaseReference reference;
    FirebaseAuth mAuth;
    private String uId;

    private Toolbar toolbar;
    ImageView btnNotif;
    TextView badgeNotif;
    private ImageView btnInbox;

    private SwipeRefreshLayout swipeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_renter_home, container, false);

        swipeLayout = view.findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        setupToolbar(view);
        badge();
        initImageLoader();
        adsPager = view.findViewById(R.id.ads_pager);

        timer = new Timer();
        timer.scheduleAtFixedRate(new AdsTimerTask(dotscount), 0, 2000);

        sliderDotsPanel = view.findViewById(R.id.sliderdots);
        stringBuilder = new StringBuilder();
        fdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fdb.getReference();
        Query query = dbref.child("AdsFile");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getValue(AdsFile.class).getAdStatus().equalsIgnoreCase("AC")) {
                        stringBuilder.append(snapshot.getValue(AdsFile.class).getAdImage());
                        stringBuilder.append(",");
                        i++;
                    }
                }
                if(dataSnapshot.exists()) {
                }
                String[] array = stringBuilder.toString().split(",");
                AdsPagerAdapter adsPagerAdapter = new AdsPagerAdapter(getContext(), array);
                adsPager.setAdapter(adsPagerAdapter);
                dotscount = adsPagerAdapter.getCount();
                dots = new ImageView[dotscount];

                for(int k = 0; k < dotscount; k++) {
                    dots[k] = new ImageView(getContext());
                    dots[k].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 0, 8, 0);
                    sliderDotsPanel.addView(dots[k], params);
                }

                dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));
                adsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        for(int i = 0; i < dotscount; i++) {
                            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));
                        }
                        dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));
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





//        RECYCLER VIEW OF CAR CATEGORIES
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("CategoryFile");
        recyclerView = view.findViewById(R.id.show_data_recycler_view);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(mLayoutManager);
        mFirebaseDatabase = new FirebaseRecyclerAdapter<CategoryFile, ShowDataViewHolder>
                (CategoryFile.class, R.layout.layout_model_categories, ShowDataViewHolder.class, myRef) {
            //POPULATE RECYCLER VIEW
            public void populateViewHolder(final ShowDataViewHolder viewHolder, final CategoryFile model, final int position) {
                if(model.getCatStatus().equalsIgnoreCase("ac")) {
                    viewHolder.setImage(model.getCatImage());
                    viewHolder.setDescription(model.getCatDesc());
                    viewHolder.catCode = model.getCatCode();
                }
                else {
                    viewHolder.cardView.setVisibility(View.GONE);
                }
                //OnClick Item
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        //PUT EXTRA AND CATCODE FOR VERIFICATION SA SUNOD
                        String temp = viewHolder.catCode;
                        Intent intent = new Intent(getActivity(), CarsActivity.class);
                        intent.putExtra("code", temp);
                        intent.putExtra("category", model.getCatDesc());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mFirebaseDatabase);

        return view;

    }




    public class AdsTimerTask extends TimerTask{
        final int count;
        public AdsTimerTask(int count) {
            this.count = count;
        }

        @Override
        public void run() {
            if(count == 0) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adsPager.setCurrentItem(current);
                            current++;
                        }
                    });
                }
            }
            if (current == dotscount){
                current = 0;
            }
        }
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    @Override
    public void onRefresh() {
        recyclerView.setAdapter(mFirebaseDatabase);
        swipeLayout.setRefreshing(false);
    }

    //View Holder For Recycler View
    public static class ShowDataViewHolder extends RecyclerView.ViewHolder {
        private final TextView image_title;
        private final ImageView image_url;
        private final CardView cardView;
        //        private final ViewPager viewPager;
        private String catCode="";

        public ShowDataViewHolder(final View itemView) {
            super(itemView);
            image_url = itemView.findViewById(R.id.fetch_image);
            image_title = itemView.findViewById(R.id.fetch_image_title);
            cardView = itemView.findViewById(R.id.card_view2);
        }

        private void setDescription(String title) {
            image_title.setText(title);
        }

        private void setImage(String title) {
            // image_url.setImageResource(R.drawable.loading);
            UniversalImageLoader.setImage(title, image_url, null, "");
        }
    }
    private void setupToolbar(View view) {
//        reference.removeEventListener(listener);
        toolbar = view.findViewById(R.id.profiletoolbar);
        btnNotif = view.findViewById(R.id.btnNotif);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RenterNotifActivity.class);
                startActivity(intent);
            }
        });
        badgeNotif = view.findViewById(R.id.notif_badge);
        btnInbox = view.findViewById(R.id.inboxBtn);
        btnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InboxActivity.class);
                startActivity(intent);
            }
        });

    }
    private void badge(){
        reference = FirebaseDatabase.getInstance().getReference("ReservationFile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(uId.equalsIgnoreCase(ds.getValue(ReservationFile.class).getResRenterCode())) {
                        if (ds.getValue(ReservationFile.class).getResNotifyRenter().equalsIgnoreCase("Unseen") &&
                                ds.getValue(ReservationFile.class).getResStatus() != "Pending") {
                            count++;
                        }
                    }
                }
                if(count == 0){
                    badgeNotif.setVisibility(View.GONE);
                }
                else{
                    badgeNotif.setText(count + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}