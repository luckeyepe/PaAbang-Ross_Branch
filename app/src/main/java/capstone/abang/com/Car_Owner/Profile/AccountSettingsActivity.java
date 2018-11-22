package capstone.abang.com.Car_Owner.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import capstone.abang.com.R;

/**
 * Created by Pc-user on 16/01/2018.
 */

public class AccountSettingsActivity extends AppCompatActivity{
    private ImageView backBtn;
//    private SectionsStatePagerAdapter pagerAdapter;
//    private ViewPager mViewPager;
    private RelativeLayout mContainer;
    private RelativeLayout mRelativeLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        //cast
        //mViewPager = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.rellayout1);
        mContainer = findViewById(R.id.container);

        //set up the list of the options
        setupSettingsList();

        //setUpFragments();

        //setup the back arrow in the toolbar
        backBtn = findViewById(R.id.backarrow);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

//    private void setUpFragments() {
//        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
//        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment));
//        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment));
//    }
//
//    private void setViewPager(int fragmentNumber) {
//        mRelativeLayout.setVisibility(View.GONE);
//        mViewPager.setAdapter(pagerAdapter);
//        mViewPager.setCurrentItem(fragmentNumber);
//    }

    private void setupSettingsList() {
        ListView listView = findViewById(R.id.listviewaccountsettings);
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment));
        options.add(getString(R.string.sign_out_fragment));

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (i) {
                    case 0:
                        transaction.replace(R.id.container, new EditProfileFragment()).commit();
                        mRelativeLayout.setVisibility(View.GONE);
                        break;
                    case 1:
                        transaction.replace(R.id.container, new SignOutFragment()).commit();
                        mRelativeLayout.setVisibility(View.GONE);
                }
            }
        });
    }
}
