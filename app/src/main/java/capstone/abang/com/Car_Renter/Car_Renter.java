package capstone.abang.com.Car_Renter;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


import capstone.abang.com.Car_Renter.Favourites.FavouritesFragment;
import capstone.abang.com.Car_Renter.History.HistoryFragment;
import capstone.abang.com.Car_Renter.Home.HomeFragment;
import capstone.abang.com.Car_Renter.Location.LocationFragment;
import capstone.abang.com.Car_Renter.Profile.RenterProfileFragment;
import capstone.abang.com.R;
import capstone.abang.com.Utils.BottomNavigationViewHelper;

public class Car_Renter extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.replace(R.id.container, new HomeFragment()).commit();
                    return true;
                case R.id.navigation_favourites:
                    transaction.replace(R.id.container, new FavouritesFragment()).commit();
                    return true;
                case R.id.navigation_map:
                    transaction.replace(R.id.container,new LocationFragment()).commit();
                    return true;
                case R.id.navigation_history:
                    transaction.replace(R.id.container, new HistoryFragment()).commit();
                    return true;
                case R.id.navigation_profile:
                    transaction.replace(R.id.container, new RenterProfileFragment()).commit();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_renter);

        BottomNavigationView navigation =  findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container,new HomeFragment()).commit();
    }
}
