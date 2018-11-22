package capstone.abang.com.Car_Renter.Home;

import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.ReservationFile;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.UniversalImageLoader;

public class RenterNotifActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseRecyclerAdapter<ReservationFile, RenterNotifActivity.RenterNotifViewHolder> mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private String userID;
    private String message;
    private Date date;
    private String formattedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_notif);

        initImageLoader();
        date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy EEE");
        formattedDate = df.format(date);

        recyclerView = findViewById(R.id.notif_recycler_view);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("ReservationFile");
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mFirebaseDatabase = new FirebaseRecyclerAdapter<ReservationFile, RenterNotifActivity.RenterNotifViewHolder>
                (ReservationFile.class, R.layout.layout_model_notification, RenterNotifActivity.RenterNotifViewHolder.class, myRef) {
            @Override
            protected void populateViewHolder(RenterNotifActivity.RenterNotifViewHolder viewHolder, final ReservationFile model, int position) {
                if(userID.equals(model.getResRenterCode())) {
                    if(model.getResNotifyRenter().equalsIgnoreCase("Unseen")) {
                        viewHolder.cardView.setCardBackgroundColor(getResources().getColor(R.color.notifcard));
                        viewHolder.setSeen(model.getResNotifyRenter());
                    }
                    if (model.getResStatus().equalsIgnoreCase("Pending")) {
                        viewHolder.cardView.setVisibility(View.GONE);
                    }
                    else if(model.getResStatus().equalsIgnoreCase("Approved")) {
                        message = "responded to your request.";
                        setOwnerName(viewHolder, model, message);
                        setCarImage(viewHolder, model);
                        viewHolder.setSeen(model.getResNotifyRenter() + " " + model.getResNotifyRenterTime());
                        viewHolder.setNotifdate(formattedDate.toString());
                    }
                    else if(model.getResStatus().equalsIgnoreCase("Declined")) {
                        message = "responded to your request.";
                        setOwnerName(viewHolder, model, message);
                        setCarImage(viewHolder, model);
                        viewHolder.setSeen(model.getResNotifyRenter() + " " + model.getResNotifyRenterTime());
                        viewHolder.setNotifdate(formattedDate.toString());
                    }
                    else if(model.getResStatus().equalsIgnoreCase("Cancelled")){
                        message = "cancelled the booking request!";
                        setOwnerName(viewHolder, model, message);
                        setCarImage(viewHolder, model);
                        viewHolder.setSeen(model.getResNotifyRenter() + " " + model.getResNotifyRenterTime());
                        viewHolder.setNotifdate(formattedDate.toString());
                    }
                    else if (model.getResStatus().equalsIgnoreCase("Done")) {
                        viewHolder.setNotifmessage("Booking has ended");
                        setCarImage(viewHolder, model);
                        viewHolder.setNotifdate(formattedDate.toString());
                        viewHolder.setSeen(model.getResNotifyRenter() + " " + model.getResNotifyRenterTime());
                        viewHolder.setNotifdate(model.getResDateStart());
                    }
                } else viewHolder.cardView.setVisibility(View.GONE);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar cal = Calendar.getInstance();
                        int hour = cal.get(Calendar.HOUR_OF_DAY);
                        int minute = cal.get(Calendar.MINUTE);
                        boolean isPM = (hour >= 12);
                        String holder = String.format("%02d:%02d %s", (hour == 12 || hour == 0) ? 12 : hour % 12, minute, isPM ? "PM" : "AM");

                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ReservationFile");
                        if(model.getResNotifyRenter().equalsIgnoreCase("Unseen")) {
                            databaseReference.child(model.getResCode()).child("resNotifyRenter").setValue("Seen");
                            databaseReference.child(model.getResCode()).child("resNotifyRenterTime").setValue(holder);
                        }
                        Intent intent = new Intent(getApplicationContext(), RenterNotifDetailsActivity.class);
                        intent.putExtra("carcode", model.getResCarCode());
                        intent.putExtra("ownercode", model.getResOwnerCode());
                        intent.putExtra("reservationcode", model.getResCode());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(mFirebaseDatabase);
    }


    private void setOwnerName(final RenterNotifViewHolder viewHolder, final ReservationFile model, final String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UDFile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(model.getResStatus().equalsIgnoreCase("Cancelled")){
                        viewHolder.setNotifmessage("You " + message);
                        break;
                    }
                    else {
                        if (model.getResOwnerCode().equalsIgnoreCase(ds.getValue(UDFile.class).getUDUserCode())) {
                            viewHolder.setNotifmessage(ds.getValue(UDFile.class).getUDFullname() + " " + message);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setCarImage(final RenterNotifViewHolder viewHolder, final ReservationFile model){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CDFile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(model.getResCarCode().equalsIgnoreCase(ds.getValue(CDFile.class).getCDCode())){
                        viewHolder.setImage(ds.getValue(CDFile.class).getCDPhoto());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getApplicationContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public static class RenterNotifViewHolder extends RecyclerView.ViewHolder {
        private final ImageView carimage;
        private final TextView notifmessage;
        private final TextView notifdate;
        private final TextView notifseen;
        private final CardView cardView;
        private LinearLayout modelLayout;

        public RenterNotifViewHolder(final View itemView) {
            super(itemView);
            carimage = itemView.findViewById(R.id.notif_car_image);
            notifmessage = itemView.findViewById(R.id.notif_message);
            notifdate = itemView.findViewById(R.id.notif_date);
            notifseen = itemView.findViewById(R.id.notif_seen);
            cardView = itemView.findViewById(R.id.cardviewnotif);
            modelLayout = itemView.findViewById(R.id.notif_model_layout);
        }

        private void setNotifmessage(String title) {
            notifmessage.setText(title);
        }

        private void setNotifdate(String date) {
            notifdate.setText(date);
        }

        private void setSeen(String seen) { notifseen.setText(seen); }

        private void setImage(String title) {
            UniversalImageLoader.setImage(title, carimage, null, "");
        }
    }
}
