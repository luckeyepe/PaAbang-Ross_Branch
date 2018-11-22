package capstone.abang.com.Car_Owner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vlk.multimager.utils.Utils;

import capstone.abang.com.Chat.Chat;
import capstone.abang.com.Models.MessagesFile;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.R;

public class InboxActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbar_title;
    private String uID;
    private FirebaseRecyclerAdapter<MessagesFile, ShowMessages> firebaseRecyclerAdapter;
    private RecyclerView recyclerView;
    private int holder = 0;
    private UDFile udFiles;
    private StringBuilder stringBuilder = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        recyclerView = findViewById(R.id.inboxRecyclerView);
        setupToolbar();
        setupRecyclerView();
        populateRecyclerView();

    }

    private void populateRecyclerView() {
        uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference recyclerRef = FirebaseDatabase.getInstance().getReference("Messages");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MessagesFile, ShowMessages>
                (MessagesFile.class, R.layout.layout_model_inbox, ShowMessages.class, recyclerRef) {
            @Override
            protected void populateViewHolder(ShowMessages viewHolder, final MessagesFile model, int position) {
                if(model.getReceiver().equalsIgnoreCase(uID)) {
                    if(stringBuilder.toString().contains(model.getSender())) {
                        viewHolder.linearLayout.setVisibility(View.GONE);
                    }
                    else {
                        viewHolder.linearLayout.setVisibility(View.VISIBLE);
                    }
                    if(stringBuilder.toString().equalsIgnoreCase("")) {
                        stringBuilder.append(model.getSender());
                    }
                    if(!stringBuilder.toString().contains(model.getSender())) {
                        stringBuilder.append(model.getSender());
                    }
                    getUser(model.getSender(), viewHolder);
                }
                else {
                    viewHolder.linearLayout.setVisibility(View.GONE);
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), Chat.class);
                        intent.putExtra("UID",model.getSender());
                        intent.putExtra("Name",udFiles.getUDFullname());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void getUser(final String userCode, final ShowMessages viewHolder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UDFile");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(UDFile.class).getUDUserCode().equalsIgnoreCase(userCode)) {
                        setUser(snapshot.getValue(UDFile.class), viewHolder);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUser(UDFile udFile, ShowMessages viewHolder) {
        viewHolder.setUserFullName(udFile.getUDFullname());
        viewHolder.setUserPhoto(udFile.getUDImageProfile());
        viewHolder.setHistoryDate(udFile.getUDContact());
        viewHolder.setHistorySummary(udFile.getUDEmail());
        udFiles = udFile;
    }

    private void setupRecyclerView() {
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private static class ShowMessages extends RecyclerView.ViewHolder {
        private final TextView messageSummary;
        private final TextView userFullName;
        private final TextView historyDate;
        private final ImageView carUserPhoto;
        private final LinearLayout linearLayout;

        public ShowMessages(View itemView) {
            super(itemView);
            messageSummary = itemView.findViewById(R.id.messagesummary);
            userFullName = itemView.findViewById(R.id.userFullName);
            historyDate = itemView.findViewById(R.id.historydate);
            carUserPhoto = itemView.findViewById(R.id.userProfile);
            linearLayout = itemView.findViewById(R.id.layoutinbox);
        }
        private void setHistoryDate(String text) { historyDate.setText(text); }
        private void setUserFullName(String text) { userFullName.setText(text); }
        private void setHistorySummary(String title) {
            messageSummary.setText(title);
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

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText("Inbox");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        else {
            return true;
        }
    }
}
