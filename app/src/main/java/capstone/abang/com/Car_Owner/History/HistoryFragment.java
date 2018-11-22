package capstone.abang.com.Car_Owner.History;

/**
 * Created by Pc-user on 14/02/2018.
 */

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

import capstone.abang.com.Car_Owner.Profile.ProfileFragment;
import capstone.abang.com.Models.BookingFile;
import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.ReservationFile;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.USettings;
import capstone.abang.com.R;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.Utils.BottomNavigationViewHelper;
import capstone.abang.com.Utils.UniversalImageLoader;


public class HistoryFragment extends Fragment {
    private static final String TAG ="ProfileFragment";
    //widgets
    private RecyclerView recyclerView;

    //firebase
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseRecyclerAdapter<BookingFile, ShowHistory> firebaseRecyclerAdapter;
    private DatabaseReference recyclerRef;
    private FirebaseAuth mAuth;

    //vars
    private String uID;
    private UDFile udFile;
    private CDFile cdFile;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        setupFirebase();
        setupRecyclerView(view);
        populateRecyclerView();
        return view;
    }
    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        recyclerRef = firebaseDatabase.getReference("BookingFile");
    }
    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.show_history_recycler);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void populateRecyclerView() {
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BookingFile, ShowHistory>
                (BookingFile.class, R.layout.layout_model_history, ShowHistory.class, recyclerRef) {
            @Override
            protected void populateViewHolder(ShowHistory viewHolder, final BookingFile model, int position) {
                if(uID.equalsIgnoreCase(model.getBOwnerCode()) && model.getBStatus().equalsIgnoreCase("Done")) {
                    getRenter(model, viewHolder);
                    getCar(model, viewHolder);
                }
                else {
                    viewHolder.layout.setVisibility(View.GONE);
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), HistoryDetailsActivity.class);
                        intent.putExtra("rentercode", model.getBRenterCode());
                        intent.putExtra("carcode", model.getBCarCode());
                        intent.putExtra("historycode", model.getBCode());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void getCar(final BookingFile model, final ShowHistory viewHolder) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().equals("CDFile")) {
                        cdFile = ds.child(model.getBCarCode()).getValue(CDFile.class);
                    }
                }
                viewHolder.setHistoryDate(model.getBDateStart());
                viewHolder.historySummary.setText("rented your " + cdFile.getCDMaker() + " " + cdFile.getCDModel() + " " + cdFile.getCdcaryear());
                Glide.with(getActivity())
                        .load(cdFile.getCDPhoto())
                        .apply(new RequestOptions()
                                .dontAnimate()
                        )
                        .into(viewHolder.carPhoto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getRenter(final BookingFile model, final ShowHistory viewHolder) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().equals("UDFile")) {
                        udFile = ds.child(model.getBRenterCode()).getValue(UDFile.class);
                    }
                }
                viewHolder.setUserFullName(udFile.getUDFullname());
                // UniversalImageLoader.setImage(udFile.getUDImageProfile(), viewHolder.carOwnerPhoto, null, "");

                if(getActivity()!=null) {
                    Glide.with(getActivity())
                            .load(udFile.getUDImageProfile())
                            .apply(new RequestOptions()
                                    .dontAnimate()
                            )
                            .into(viewHolder.carUserPhoto);

                    viewHolder.carUserPhoto.setBackgroundResource(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static class ShowHistory extends RecyclerView.ViewHolder {
        private final TextView historySummary;
        private final TextView userFullName;
        private final TextView historyDate;
        private final ImageView carPhoto;
        private final ImageView carUserPhoto;
        private final LinearLayout layout;

        public ShowHistory(View itemView) {
            super(itemView);
            historySummary = itemView.findViewById(R.id.historysum);
            userFullName = itemView.findViewById(R.id.userFullName);
            historyDate = itemView.findViewById(R.id.historydate);
            carPhoto = itemView.findViewById(R.id.carpic);
            carUserPhoto = itemView.findViewById(R.id.userProfile);
            layout = itemView.findViewById(R.id.layouthist);
        }
        private void setHistoryDate(String text) { historyDate.setText(text); }
        private void setUserFullName(String text) { userFullName.setText(text); }
        private void setHistorySummary(String title) {
            historySummary.setText(title);
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
        private void setUserPhoto(String title) {
            //UniversalImageLoader.setImage(title, carPhoto, null,"");

            Glide.with(itemView.getContext())
                    .load(title)
                    .apply(new RequestOptions()
                            .placeholder(com.vlk.multimager.R.drawable.image_processing)
                            .centerCrop()
                    )
                    .into(carUserPhoto);
        }
    }

}