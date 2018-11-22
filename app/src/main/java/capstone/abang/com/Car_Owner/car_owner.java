package capstone.abang.com.Car_Owner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import capstone.abang.com.Car_Owner.History.HistoryFragment;
import capstone.abang.com.Car_Owner.Home.HomeFragment;
import capstone.abang.com.Car_Owner.Profile.ProfileFragment;
import capstone.abang.com.Car_Owner.Notifications.NotificationFragment;
import capstone.abang.com.Models.ReservationFile;
import capstone.abang.com.Utils.BottomNavigationViewHelper;
import capstone.abang.com.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vlk.multimager.activities.BaseActivity;
import com.vlk.multimager.activities.GalleryActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Params;


import butterknife.ButterKnife;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class car_owner extends BaseActivity implements NotificationFragment.OnDataPass {
    BottomNavigationView navigation;
    FirebaseAuth mAuth;
    private String uId;
    int selectedColor;
    int lastPosition = 1;
    int badgeCount;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    lastPosition = 0;
                    transaction.replace(R.id.container,new HomeFragment()).commit();
                    return true;
                case R.id.navigation_notifications:
                    lastPosition = 1;
                    transaction.replace(R.id.container,new NotificationFragment()).commit();
                    return true;

                case R.id.navigation_history:
                    lastPosition = 3;
                    transaction.replace(R.id.container, new HistoryFragment()).commit();

                    return true;
                case R.id.navigation_profile:
                    lastPosition = 4;
                    transaction.replace(R.id.container, new ProfileFragment()).commit();
                    return true;


            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_owner);

        ButterKnife.bind(this);
        selectedColor = 4475389;


        navigation = findViewById(R.id.navigation);

        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container,new HomeFragment()).commit();

        FloatingActionButton endorse = findViewById(R.id.endorseFab);

        endorse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateMultiPicker();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReservationFile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(uId.equalsIgnoreCase(ds.getValue(ReservationFile.class).getResOwnerCode())){
                        if(ds.getValue(ReservationFile.class).getResNotify().equalsIgnoreCase("Unseen") &&
                                ds.getValue(ReservationFile.class).getResNotifType().equalsIgnoreCase("Booking")){
                            count++;
                        }
                        if("Endorsement".equalsIgnoreCase(ds.getValue(ReservationFile.class).getResNotifType())){
                            if("Approved".equalsIgnoreCase(ds.getValue(ReservationFile.class).getResStatus()) ||
                                    "Decline".equalsIgnoreCase(ds.getValue(ReservationFile.class).getResStatus())){
                                if ("Unseen".equalsIgnoreCase(ds.getValue(ReservationFile.class).getResNotify())){
                                    count++;
                                }
                            }
                        }
                    }
                        addBadgeAt(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDataPass(int data) {
        Log.d("TAG", "hello" + data);
        Log.d("BADGE", "badge" + badgeCount);

        int holder = badgeCount - 1;
        Log.d("BADGE", "holder" + holder);
        addBadgeAt(holder);
    }


    private Badge addBadgeAt( int number) {
            badgeCount = number;
            BottomNavigationMenuView bottomNavigationMenuView =
                    (BottomNavigationMenuView) navigation.getChildAt(0);
            View view = bottomNavigationMenuView.getChildAt(1);
            return new QBadgeView(this)
                    .setBadgeNumber(number)
                    .setGravityOffset(12, 2, true)
                    .bindTarget(view)
                    .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                        @Override
                        public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                        }
                    });
    }


    private void initiateMultiPicker() {
        Intent intent = new Intent(this, GalleryActivity.class);
        Params params = new Params();
        params.setCaptureLimit(4);
        params.setPickerLimit(4);
        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);
        startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);
    }

}