package capstone.abang.com.Car_Renter.Home;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vlk.multimager.utils.Utils;

import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.R;

public class ViewCredentialsActivity extends AppCompatActivity {
    private ImageButton btnCompany;
    private ImageButton btnLTFRB;
    private ImageButton btnAirport;
    private Toolbar toolbar;
    private TextView toolbar_title;

    private String carOwnerCode;
    private String carCode;
    private CDFile cdFiles;
    private UDFile udFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_credentials);
        setupWidgets();
        setupToolbar();
        getExtras();
        setupImages();

        btnCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                intent.putExtra("path", udFiles.getUdCompanyID());
                startActivity(intent);
            }
        });

        btnAirport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                intent.putExtra("path", cdFiles.getCdadditionalimgone());
                startActivity(intent);
            }
        });

        btnLTFRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                intent.putExtra("path", cdFiles.getCdgarageimg());
                startActivity(intent);
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText("Owner Credentials");
    }

    private void setupImages() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CDFile");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(CDFile.class).getCDCode().equalsIgnoreCase(carCode)) {
                        setImagesCar(snapshot.getValue(CDFile.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("UDFile");
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(UDFile.class).getUDUserCode().equalsIgnoreCase(carOwnerCode)) {
                        setOwnerID(snapshot.getValue(UDFile.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setOwnerID(UDFile udFile) {
        udFiles = udFile;
        Glide.with(this)
                .load(udFile.getUdCompanyID())
                .into(btnCompany);
    }

    private void setImagesCar(CDFile cdFile) {
        cdFiles = cdFile;
        Glide.with(this)
                .load(cdFile.getCdadditionalimgone())
                .into(btnAirport);

        Glide.with(this)
                .load(cdFile.getCdgarageimg())
                .into(btnLTFRB);
    }

    private void setupWidgets() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        btnCompany = findViewById(R.id.imgViewCompany);
        btnLTFRB = findViewById(R.id.imgViewFranchise);
        btnAirport = findViewById(R.id.imgViewAirportID);
    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        carOwnerCode = bundle.getString("usercode");
        carCode = bundle.getString("carcode");
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
