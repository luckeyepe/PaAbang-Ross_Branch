package capstone.abang.com.Car_Owner.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.Models.USettings;
import capstone.abang.com.R;

public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";
    //firebase
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;

    //widgets
    private EditText editTextUserName;
    private EditText editTextUserFullName;
    private EditText editTextUserAddress;
    private EditText editTextUserContactNumber;
    private EditText editTextUserEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container,false);
        //cast widgets
        editTextUserName = view.findViewById(R.id.eteditusername);
        editTextUserFullName = view.findViewById(R.id.eteditfullname);
        editTextUserAddress = view.findViewById(R.id.eteditaddress);
        editTextUserContactNumber = view.findViewById(R.id.eteditcontactnumber);
        editTextUserEmail = view.findViewById(R.id.eteditcontactemail);
        //charles
        //firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        //Setup Toolbar
        setupToolbar(view);

        //retrieve
        retrieveData();

        return view;
    }

    private void setEditProfileWidgets(USettings uSettings) {
        UDFile udFile = uSettings.getUdFile();
        UHFile uhFile = uSettings.getUhFile();

        editTextUserName.setText(uhFile.getUHUsername());
        editTextUserFullName.setText(udFile.getUDFullname());
        editTextUserAddress.setText(udFile.getUDAddr());
        editTextUserContactNumber.setText(udFile.getUDContact());
        editTextUserEmail.setText(udFile.getUDEmail());
    }
    //charles
    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.editprofiletoolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ImageView backImageView = view.findViewById(R.id.backarrow);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void retrieveData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setEditProfileWidgets(getUserSettings(dataSnapshot));
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
