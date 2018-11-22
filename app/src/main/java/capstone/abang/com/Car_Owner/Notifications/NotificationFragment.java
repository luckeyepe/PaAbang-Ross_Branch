package capstone.abang.com.Car_Owner.Notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import capstone.abang.com.Car_Owner.car_owner;
import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.ReservationFile;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.UniversalImageLoader;


public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseRecyclerAdapter<ReservationFile, NotifViewHolder>mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private String userID;
    private String message;
    private OnDataPass dataPasser;
    private Date date;
    private String formattedDate;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);

        initImageLoader();
        date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy EEE");
        formattedDate = df.format(date);

        recyclerView = view.findViewById(R.id.notif_recycler_view);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("ReservationFile");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mFirebaseDatabase = new FirebaseRecyclerAdapter<ReservationFile, NotifViewHolder>
                (ReservationFile.class, R.layout.layout_model_notification, NotificationFragment.NotifViewHolder.class, myRef) {
            protected void populateViewHolder(final NotifViewHolder viewHolder,final ReservationFile model, int position) {
                if(model.getResOwnerCode().equals(userID)) {
                    if (model.getResNotifType().equalsIgnoreCase("Booking")) {
                        if (model.getResNotify().equalsIgnoreCase("Unseen")) {
                            viewHolder.cardView.setCardBackgroundColor(getResources().getColor(R.color.notifcard));
                            viewHolder.setSeen(model.getResNotify());
                        }
                        if (model.getResStatus().equalsIgnoreCase("Pending")) {
                            message = "sent the booking request!";
                            viewHolder.codeHolder = model.getResRenterCode();
                            setRenterName(viewHolder, model, message);
                            setCarImage(viewHolder, model);
                            viewHolder.setNotifdate(formattedDate.toString());
                            viewHolder.setSeen(model.getResNotify() + " " + model.getResNotifyTime());
                            viewHolder.notiftype = model.getResNotifType();
                        } else if (model.getResStatus().equalsIgnoreCase("Approved")) {
                            message = "sent the booking request!";
                            viewHolder.codeHolder = model.getResRenterCode();
                            setRenterName(viewHolder, model, message);
                            setCarImage(viewHolder, model);
                            viewHolder.setSeen(model.getResNotify() + " " + model.getResNotifyTime());
                            viewHolder.setNotifdate(formattedDate.toString());
                            viewHolder.notiftype = model.getResNotifType();
                        } else if (model.getResStatus().equalsIgnoreCase("Declined")) {
                            message = "sent the booking request!";
                            viewHolder.codeHolder = model.getResRenterCode();
                            setRenterName(viewHolder, model, message);
                            setCarImage(viewHolder, model);
                            viewHolder.setSeen(model.getResNotify() + " " + model.getResNotifyTime());
                            viewHolder.setNotifdate(formattedDate.toString());
                            viewHolder.notiftype = model.getResNotifType();
                        } else if (model.getResStatus().equalsIgnoreCase("Cancelled")) {
                            message = "cancelled the booking request!";
                            viewHolder.codeHolder = model.getResRenterCode();
                            setRenterName(viewHolder, model, message);
                            setCarImage(viewHolder, model);
                            viewHolder.setSeen(model.getResNotify() + " " + model.getResNotifyTime());
                            viewHolder.setNotifdate(formattedDate.toString());
                            viewHolder.notiftype = model.getResNotifType();
                        } else if (model.getResStatus().equalsIgnoreCase("Done")) {
                            viewHolder.setNotifmessage("Booking has ended");
                            viewHolder.codeHolder = model.getResRenterCode();
                            setCarImage(viewHolder, model);
                            viewHolder.setSeen(model.getResNotify() + " " + model.getResNotifyTime());
                            viewHolder.setNotifdate(formattedDate.toString());
                            viewHolder.notiftype = model.getResNotifType();
                        }
                    } else if (model.getResNotifType().equalsIgnoreCase("Endorsement")){
                        if (model.getResNotify().equalsIgnoreCase("Unseen")) {
                            viewHolder.cardView.setCardBackgroundColor(getResources().getColor(R.color.notifcard));
                            setCarImage(viewHolder, model);
                            viewHolder.setSeen(model.getResNotify());
                        }
                        if(model.getResStatus().equalsIgnoreCase("Pending")){
                            viewHolder.cardView.setVisibility(View.GONE);
                        }
                        else if (model.getResStatus().equalsIgnoreCase("Approved")) {
                            setCarImage(viewHolder, model);
                            viewHolder.setNotifmessage("Endorsement feedback from the admin");
                            viewHolder.setSeen(model.getResNotify() + " " + model.getResNotifyTime());
                            viewHolder.setNotifdate(formattedDate.toString());
                            viewHolder.codeHolder = model.getResOwnerCode();
                            viewHolder.notiftype = model.getResNotifType();
                        } else if (model.getResStatus().equalsIgnoreCase("Declined")) {
                            setCarImage(viewHolder, model);
                            viewHolder.setNotifmessage("Endorsement feedback from the admin");
                            viewHolder.setSeen(model.getResNotify() + " " + model.getResNotifyTime());
                            viewHolder.setNotifdate(formattedDate.toString());
                            viewHolder.codeHolder = model.getResOwnerCode();
                            viewHolder.notiftype = model.getResNotifType();
                        }
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
                        if(model.getResNotify().equalsIgnoreCase("Unseen")) {
                            dataPasser.onDataPass(1);
                            databaseReference.child(model.getResCode()).child("resNotify").setValue("Seen");
                            databaseReference.child(model.getResCode()).child("resNotifyTime").setValue(holder);

                        }
                        Intent intent = new Intent(getActivity(), NotificationdetailsActivity.class);
                        intent.putExtra("carcode", model.getResCarCode());
                        intent.putExtra("ownercode", viewHolder.codeHolder);
                        intent.putExtra("reservationcode", model.getResCode());
                        intent.putExtra("notiftype", viewHolder.notiftype);
                        startActivity(intent);
                    }
                });
            }

        };
        recyclerView.setAdapter(mFirebaseDatabase);
        return view;
    }

    private void setRenterName(final NotifViewHolder viewHolder, final ReservationFile model, final String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UDFile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getValue(UDFile.class).getUDUserCode().equalsIgnoreCase(model.getResRenterCode())) {
                        viewHolder.setNotifmessage(ds.getValue(UDFile.class).getUDFullname() + " " + message);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setCarImage(final NotifViewHolder viewHolder, final ReservationFile model){
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
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public interface OnDataPass {
        public void onDataPass(int data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    public static class NotifViewHolder extends RecyclerView.ViewHolder {
        private final ImageView carimage;
        private final TextView notifmessage;
        private final TextView notifdate;
        private final TextView notifseen;
        private final CardView cardView;
        private String codeHolder;
        private String notiftype;

        public NotifViewHolder(final View itemView) {
            super(itemView);
            carimage = itemView.findViewById(R.id.notif_car_image);
            notifmessage = itemView.findViewById(R.id.notif_message);
            notifdate = itemView.findViewById(R.id.notif_date);
            notifseen = itemView.findViewById(R.id.notif_seen);
            cardView = itemView.findViewById(R.id.cardviewnotif);
        }

        private void setNotifmessage(String title) {
            notifmessage.setText(title);
        }

        private void setNotifdate(String date) {
            notifdate.setText(date);
        }

        private void setSeen(String seen) { notifseen.setText(seen); }

        private void setImage(String title) {
            // image_url.setImageResource(R.drawable.loading);
            UniversalImageLoader.setImage(title, carimage, null, "");
        }
    }
}