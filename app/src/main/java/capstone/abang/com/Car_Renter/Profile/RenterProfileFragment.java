package capstone.abang.com.Car_Renter.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import capstone.abang.com.Car_Owner.Profile.AccountSettingsActivity;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.Models.USettings;
import capstone.abang.com.R;
import capstone.abang.com.Utils.UniversalImageLoader;

/**
 * Created by Pc-user on 17/01/2018.
 */

public class RenterProfileFragment extends Fragment {
    //widgets
    private ImageView imgViewProfilePicture;
    private TextView textViewName;
    private TextView textViewDateJoined;
    private TextView textViewEmail;
    private TextView textViewContact;
    private TextView textViewAddress;
    private TextView textViewTransactions;
    private LinearLayout linearLayout;
    private ImageView imgViewQrCode;
    private Button viewCalendarBtn;

    //firebase
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_renter_profile, container, false);

        //casting widgets
        imgViewProfilePicture = view.findViewById(R.id.imgviewprofilepic);
        textViewName = view.findViewById(R.id.txtuserfullname);
        textViewTransactions = view.findViewById(R.id.txtprofileusertransactions);
        textViewAddress = view.findViewById(R.id.txtprofileuseraddress);
        textViewEmail = view.findViewById(R.id.txtprofileuseremail);
        textViewContact = view.findViewById(R.id.txtprofileusercontact);
        textViewDateJoined = view.findViewById(R.id.txtprofileuserdatejoined);
        linearLayout = view.findViewById(R.id.loader);
        imgViewQrCode = view.findViewById(R.id.imageqr);
        viewCalendarBtn = view.findViewById(R.id.viewcalendarbtn);



        //firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        linearLayout.setVisibility(View.VISIBLE);

        //setup toolbar
        setupToolbar(view);

        initImageLoader();

        //retrieve
        retrieveData();

        viewCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void  setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.profiletoolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ImageView profileMenu = view.findViewById(R.id.profilemenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setProfileWidgets(USettings uSettings) {
        UDFile udFile = uSettings.getUdFile();
        UHFile uhFile = uSettings.getUhFile();

        textViewName.setText(udFile.getUDFullname());
        textViewAddress.setText(udFile.getUDAddr());
        textViewContact.setText(udFile.getUDContact());
        textViewEmail.setText(udFile.getUDEmail());
        textViewDateJoined.setText(uhFile.getUhdatecreated());
        textViewTransactions.setText(String.valueOf(udFile.getUDTransactions()));
        linearLayout.setVisibility(View.GONE);
        // UniversalImageLoader.setImage(udFile.getUDImageProfile(), imgViewProfilePicture, null, "");

        if (getContext() != null) {
            Glide.with(getContext())
                    .load(udFile.getUDImageProfile())
                    .into(imgViewProfilePicture);
            imgViewProfilePicture.setBackgroundResource(0);
            // UniversalImageLoader.setImage(uhFile.getUhimageqr(), imgViewQrCode, null, "");

            Glide.with(getContext())
                    .load(uhFile.getUhimageqr())
                    .apply(new RequestOptions()
                            .centerCrop()
                    )
                    .into(imgViewQrCode);
        }
    }

    public void retrieveData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setProfileWidgets(getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private USettings getUserSettings(DataSnapshot dataSnapshot) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        UDFile udFile = new UDFile();
        UHFile uhFile = new UHFile();

        for (DataSnapshot ds: dataSnapshot.getChildren()) {
            if(ds.getKey().equals("UDFile")) {
                udFile = ds.child(userID).getValue(UDFile.class);
            }
            if(ds.getKey().equals("UHFile")) {
                uhFile = ds.child(userID).getValue(UHFile.class);
            }
        }
        return new USettings(udFile, uhFile);
    }
}
