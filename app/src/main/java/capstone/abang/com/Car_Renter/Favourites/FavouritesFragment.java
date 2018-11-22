package capstone.abang.com.Car_Renter.Favourites;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import capstone.abang.com.Car_Renter.Home.CarDetailsActivity;
import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.Like;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.Heart;
import capstone.abang.com.Utils.UniversalImageLoader;

/**
 * Created by Pc-user on 26/01/2018.
 */

public class FavouritesFragment extends Fragment {
    //widgets
    private RecyclerView recyclerView;

    //firebasedatabase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<CDFile, ShowHolderView> firebaseRecyclerAdapter;

    //vars
    private String uID;
    private StringBuilder mUsers;
    private UDFile udFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_renter_favourites, container, false);

        initImageLoader();
        setupFirebase();
        setupRecyclerView(view);
        populateRecyclerView();
        return view;
    }

    private void populateRecyclerView() {
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CDFile, ShowHolderView>
                (CDFile.class, R.layout.layout_model_car_fav, ShowHolderView.class, databaseReference) {
            @Override
            protected void populateViewHolder(final ShowHolderView viewHolder, final CDFile model, int position) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query query = reference
                        .child("CDFile")
                        .child(model.getCDCode())
                        .child("likes");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                            if(uID.equals(singleDataSnapshot.getValue(Like.class).getUHuserCode())) {
                                final String keyID = singleDataSnapshot.getKey();
                                viewHolder.setCarName(model.getCDMaker() + " " + model.getCDModel() + " " + model.getCdcaryear());
                                viewHolder.setCarImage(model.getCDPhoto());
                                viewHolder.cardView.setVisibility(View.VISIBLE);
                                String date = dateUtils(model);
                                getOwner(model, viewHolder);
                                viewHolder.setLikes(model.getCdlikes());
                                viewHolder.carRented.setText(model.getCdtransactions() + " transaction(s)");
                                viewHolder.carPostedOn.setText(date);


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
                                        viewHolder.mHeart.toggleLike();
                                    }
                                });

                            }
                            else {
                                viewHolder.cardView.setVisibility(View.GONE);
                            }
                        }
                        if(!dataSnapshot.exists()) {
                            viewHolder.cardView.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CarDetailsActivity.class);
                        intent.putExtra("carcode", model.getCDCode());
                        intent.putExtra("usercode", model.getCdowner());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.show_recycler_view);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("CDFile");
        uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getContext());
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
        if(elapseDays > 1) {
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
        } else if(elapseHourse > 1){
            if(elapseHourse < 1) {
                date = elapseHourse + " hr ago";
            }
            else {
                date = elapseHourse + " hrs ago";
            }
        } else if(elapseMinutes > 1) {
            if(elapseMinutes < 1) {
                date = elapseMinutes + " min ago";
            } else {
                date = elapseMinutes + " mins ago";
            }
        } else {
            if(elapseSeconds < 1) {
                date = elapseSeconds + " sec ago";
            } else {
                date = elapseSeconds + " secs ago";
            }
        }
        return date;
    }

    private void getOwner(final CDFile model, final ShowHolderView viewHolder) {
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
                UniversalImageLoader.setImage(udFile.getUDImageProfile(), viewHolder.carOwnerPhoto, null, "");
                viewHolder.carOwnerPhoto.setBackgroundResource(0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class ShowHolderView extends RecyclerView.ViewHolder {
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
        public ShowHolderView(View itemView) {
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
