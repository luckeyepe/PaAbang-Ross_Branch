package capstone.abang.com.Register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import capstone.abang.com.Car_Owner.car_owner;
import capstone.abang.com.Car_Renter.Car_Renter;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.R;
import capstone.abang.com.Register.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private ImageView imageView;
    //firebase
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private ValueEventListener myRefListener;
    private FirebaseUser user;
    private String userLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = findViewById(R.id.imgViewLogo);
        Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        imageView.startAnimation(myAnimation);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            myRefListener = myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userID = user.getUid();
                    UDFile udFile = new UDFile();
                    Log.d(TAG, "retrieveData: navigating to set user level");
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        if(ds.getKey().equals("UDFile")) {
                            udFile = ds.child(userID).getValue(UDFile.class);
                        }
                    }
                    assert udFile != null;
                    userLevel = udFile.getUDUserType();
                    if(userLevel.equalsIgnoreCase("Owner") && udFile.getUdDocuStatus().equalsIgnoreCase("Validated")) {
                        final Intent intent = new Intent(getApplicationContext(), car_owner.class);
                        Thread timer = new Thread() {
                            public void run() {
                                try {
                                    sleep(2500);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                finally {
                                    startActivity(intent);
                                    myRef.removeEventListener(myRefListener);
                                    finish();
                                }
                            }
                        };
                        timer.start();
                    }
                    else if(userLevel.equalsIgnoreCase("Owner") && udFile.getUdDocuStatus().equalsIgnoreCase("Pending")) {
                        final Intent intent = new Intent(getApplicationContext(), OwnerSplashActivity.class);
                        Thread timer = new Thread() {
                            public void run() {
                                try {
                                    sleep(2500);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                finally {
                                    startActivity(intent);
                                    myRef.removeEventListener(myRefListener);
                                    finish();
                                }
                            }
                        };
                        timer.start();
                    }
                    else {
                        final Intent intent = new Intent(getApplicationContext(), Car_Renter.class);
                        Thread timer = new Thread() {
                            public void run() {
                                try {
                                    sleep(2500);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                finally {
                                    startActivity(intent);
                                    myRef.removeEventListener(myRefListener);
                                    finish();
                                }
                            }
                        };
                        timer.start();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            final Intent intent = new Intent(this, LoginActivity.class);
            Thread timer = new Thread() {
                public void run() {
                    try {
                        sleep(2500);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
                        startActivity(intent);
                        finish();
                    }
                }
            };
            timer.start();
        }


    }
}
