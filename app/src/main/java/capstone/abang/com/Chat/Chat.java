package capstone.abang.com.Chat;



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vlk.multimager.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import capstone.abang.com.Models.BookingFile;
import capstone.abang.com.Models.MessagesFile;
import capstone.abang.com.R;


public class Chat extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbar_title;
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;

    DatabaseReference reference;
    String UID;
    String Name;


    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        mAuth = FirebaseAuth.getInstance();

            Intent intent = getIntent();

        UID = intent.getStringExtra("UID");
        Name = intent.getStringExtra("Name");
        user = mAuth.getCurrentUser();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        setSupportActionBar(toolbar);
        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText(Name);



    /*   reference1 = new Firebase("https://abang-application.firebaseio.com/Messages/" + user.getUid()+ "_" + UID);
        reference2 = new Firebase("https://abang-application.firebaseio.com/Messages/" + UID + "_" + user.getUid());*/

       reference = FirebaseDatabase.getInstance().getReference("Messages");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

               /* if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", user.getUid());
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }*/
               if(!messageText.equals("")){
                    MessagesFile msgFile = new MessagesFile(123,messageText,"Notified",UID,user.getUid(),"read");


                    reference.push().setValue(msgFile);

                    messageArea.setText("");
                }
            }
        });



//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//                for(com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    if(snapshot.getValue(MessagesFile.class).getSender().equalsIgnoreCase(user.getUid())) {
//
//                       // BookingFile bookingFile = snapshot.getValue(BookingFile.class);
//                        MessagesFile msgFile = snapshot.getValue(MessagesFile.class);
//                        addMessageBox("You:\n\n" + dataSnapshot.getValue(MessagesFile.class).getMessage(), 1);
//                      // reference.child(bookingFile.getBCode()).child("brenterStatus").setValue(status);
//                    }
//                    else{
//                        addMessageBox(Name + ":\n\n" + dataSnapshot.getValue(MessagesFile.class).getMessage(), 2);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        reference.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

                    if(dataSnapshot.getValue(MessagesFile.class).getSender().equalsIgnoreCase(user.getUid()) && dataSnapshot.getValue(MessagesFile.class).getReceiver().equalsIgnoreCase(UID)) {

                        // BookingFile bookingFile = snapshot.getValue(BookingFile.class);
                        MessagesFile msgFile = dataSnapshot.getValue(MessagesFile.class);
                        addMessageBox("You:\n\n" + dataSnapshot.getValue(MessagesFile.class).getMessage(), 1);
                        // reference.child(bookingFile.getBCode()).child("brenterStatus").setValue(status);
                    }
                    else if(dataSnapshot.getValue(MessagesFile.class).getSender().equalsIgnoreCase(UID) && dataSnapshot.getValue(MessagesFile.class).getReceiver().equalsIgnoreCase(user.getUid())){
                        addMessageBox(Name + ":\n\n" + dataSnapshot.getValue(MessagesFile.class).getMessage(), 2);
                    }

            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.getValue(MessagesFile.class).getSender().equalsIgnoreCase(user.getUid())){
                    addMessageBox("You:\n\n" + dataSnapshot.getValue(MessagesFile.class).getMessage(), 1);
                }
                else{
                    addMessageBox(Name + ":\n\n" + dataSnapshot.getValue(MessagesFile.class).getMessage(), 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;
        lp2.setMargins(10,10,10,10);

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
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