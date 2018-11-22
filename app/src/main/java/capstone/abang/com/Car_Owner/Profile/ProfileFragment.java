package capstone.abang.com.Car_Owner.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.USettings;
import capstone.abang.com.R;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.Utils.BottomNavigationViewHelper;
import capstone.abang.com.Utils.UniversalImageLoader;


public class ProfileFragment extends Fragment {
    private static final String TAG ="ProfileFragment";
    //lists
    List<UHFile> uhFiles = new ArrayList<UHFile>();

    //widgets
    private ImageView imgViewProfilePicture;
    private TextView textViewName;
    private TextView textViewDateJoined;
    private TextView textViewEmail;
    private TextView textViewContact;
    private TextView textViewAddress;
    private TextView textViewTransactions;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private android.support.v7.widget.Toolbar toolbar;
    private Button viewCalendarBtn;


    //firebase
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference recyclerRef;
    private FirebaseRecyclerAdapter<CDFile, ShowHolder> firebaseRecyclerAdapter;
    private FirebaseAuth mAuth;

    //vars
    private String uID;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //casting widgets
        imgViewProfilePicture = view.findViewById(R.id.imgviewprofilepic);
        textViewName = view.findViewById(R.id.txtuserfullname);
        textViewTransactions = view.findViewById(R.id.txtprofileusertransactions);
        textViewAddress = view.findViewById(R.id.txtprofileuseraddress);
        textViewEmail = view.findViewById(R.id.txtprofileuseremail);
        textViewContact = view.findViewById(R.id.txtprofileusercontact);
        textViewDateJoined = view.findViewById(R.id.txtprofileuserdatejoined);
        linearLayout = view.findViewById(R.id.loader);
        viewCalendarBtn = view.findViewById(R.id.viewcalendarbtn);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        //setupfirebase for recyclerView
        recyclerRef = firebaseDatabase.getReference("CDFile");
        setupRecyclerView(view);
        populateRecyclerView();

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

    private void populateRecyclerView() {
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CDFile, ShowHolder>
                (CDFile.class, R.layout.layout_model_cars, ShowHolder.class, recyclerRef) {
            @Override
            protected void populateViewHolder(ShowHolder viewHolder, CDFile model, int position) {
                if(uID.equals(model.getCdowner())) {
                    viewHolder.setCarName(model.getCDMaker() + " " + model.getCDModel() + " " + model.getCdcaryear());
                    viewHolder.setCarImage(model.getCDPhoto());
                    viewHolder.carRented.setText(model.getCdtransactions() + " transaction(s)");
                    if(model.getCdlikes() != 0) {
                        viewHolder.heartRed.setVisibility(View.VISIBLE);
                        viewHolder.heartWhite.setVisibility(View.GONE);
                        viewHolder.carLikes.setText(model.getCdlikes() + "");
                    } else {
                        viewHolder.heartRed.setVisibility(View.GONE);
                        viewHolder.heartWhite.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    viewHolder.cardView.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.show_data_recycler_view);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void  setupToolbar(View view) {
       toolbar = view.findViewById(R.id.profiletoolbar);
        /*((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);*/
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
        textViewTransactions.setText(String.valueOf(udFile.getUDTransactions()));
        linearLayout.setVisibility(View.GONE);
        UniversalImageLoader.setImage(udFile.getUDImageProfile(), imgViewProfilePicture, null, "");
        imgViewProfilePicture.setBackgroundResource(0);
        textViewDateJoined.setText(uhFile.getUhdatecreated());
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public static class ShowHolder extends RecyclerView.ViewHolder {
        private final TextView carName;
        private final ImageView carPhoto;
        private final CardView cardView;
        private final ImageView heartWhite;
        private final ImageView heartRed;
        private final TextView carLikes;
        private final TextView carRented;
        public ShowHolder(View itemView) {
            super(itemView);
            heartWhite = itemView.findViewById(R.id.imgviewwhiteheart);
            heartRed = itemView.findViewById(R.id.imgviewredheart);
            carName = itemView.findViewById(R.id.textviewcarname);
            carPhoto = itemView.findViewById(R.id.imageviewcar);
            cardView = itemView.findViewById(R.id.cardviewcars);
            carLikes = itemView.findViewById(R.id.textviewfavourites);
            carRented = itemView.findViewById(R.id.textviewrented);
        }

        private void setCarName(String title) {
            carName.setText(title);
        }
        private void setCarImage(String title) {
            //UniversalImageLoader.setImage(title, carPhoto, null,"");

            Glide.with(itemView.getContext())
                    .load(title)
                    .apply(new RequestOptions()
                            .placeholder(com.vlk.multimager.R.drawable.image_processing)
                            .centerCrop()
                    )
                    .into(carPhoto);

        }
    }
}