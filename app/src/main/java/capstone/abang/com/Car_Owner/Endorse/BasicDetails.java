package capstone.abang.com.Car_Owner.Endorse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Utils;

import java.util.ArrayList;

import capstone.abang.com.Models.CDFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.VinValidator;

public class BasicDetails extends AppCompatActivity {

    boolean existingPlate;
    boolean existingChassis;
    Toolbar toolbar;
    TextView toolbar_title;

    EditText etMaker, etModel, etYear, etPlateNo, etMaxCap, etChassisNo, etEngineNo;
    RadioGroup Transmission, ServiceType;
    RadioButton rbManual, rbAutomatic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_details);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);


        etMaker = findViewById(R.id.etMaker);
        etModel = findViewById(R.id.etModel);
        etYear  = findViewById(R.id.etYear);
        etPlateNo = findViewById(R.id.etPlateNumber);
        etMaxCap = findViewById(R.id.etCapacity);
        etChassisNo = findViewById(R.id.etChassisNo);
        etEngineNo = findViewById(R.id.etEngineNo);
        Transmission = findViewById(R.id.Transmission);
        rbManual = findViewById(R.id.radioManual);
        rbAutomatic = findViewById(R.id.radioAutomatic);


        etYear.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMaxCap.setInputType(InputType.TYPE_CLASS_NUMBER);


        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText("Add Basic Details");




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.vlk.multimager.R.menu.add_dents_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        else {

            setInit();

            return true;
        }
    }


    private void setInit() {
       String HolderMaker = etMaker.getText().toString().trim();
        String HolderModel = etModel.getText().toString().trim();
        String HolderYear = etYear.getText().toString().trim();
       String HolderCapacity = etMaxCap.getText().toString().trim();
        String HolderPlateNumber = etPlateNo.getText().toString().trim();
        String HolderChassisNo = etChassisNo.getText().toString().trim();
        String HolderEngineNo = etEngineNo.getText().toString().trim();

        String str = null;
        if(validated())  {
            Intent oldIntent = getIntent();
            final ArrayList<Image> imagesList = oldIntent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
            final String CatCodeHolder = oldIntent.getStringExtra("KEY");
              Intent intent = new Intent(BasicDetails.this, AttachDocuments.class);
              intent.putExtra("Maker", HolderMaker);
            intent.putExtra("Model", HolderModel);
            intent.putExtra("Year", HolderYear);
            intent.putExtra("Capacity", HolderCapacity);
            intent.putExtra("PlateNumber", HolderPlateNumber);
            intent.putExtra("ChassisNo", HolderChassisNo);
            intent.putExtra("EngineNo", HolderEngineNo);
            intent.putParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST, imagesList);
            intent.putExtra("KEY",CatCodeHolder);
            String[] dents = oldIntent.getStringArrayExtra("dents");
            intent.putExtra("dents", dents);

            if(rbAutomatic.isChecked()) {
                str = "Automatic";
            } else if(rbManual.isChecked()) {
                str = "Manual";
            }

            intent.putExtra("Transmission", str);

                startActivity(intent);
        }

    }

    private boolean validated() {
        boolean flag = true;
        String HolderMaker = etMaker.getText().toString().trim();
        String HolderModel = etModel.getText().toString().trim();
        String HolderYear = etYear.getText().toString().trim();
        String HolderCapacity = etMaxCap.getText().toString().trim();
        String HolderPlateNumber = etPlateNo.getText().toString().trim();
        String HolderChassisNo = etChassisNo.getText().toString().trim();
        String HolderEngineNo = etEngineNo.getText().toString().trim();

        if(TextUtils.isEmpty(HolderMaker)) {
            etMaker.setError("Please input Car Maker");
            flag = false;
        }
        if(TextUtils.isEmpty(HolderModel)) {
            etModel.setError("Please input Car Model");
            flag = false;
        }
        if(TextUtils.isEmpty(HolderYear)) {
            etYear.setError("Please input Car Year");
            flag = false;
        }
        if(TextUtils.isEmpty(HolderCapacity)) {
            etMaxCap.setError("Please input Car Capacity");
            flag = false;
        }
        if(TextUtils.isEmpty(HolderPlateNumber)) {
            etPlateNo.setError("Please input Plate Number");
            flag = false;
        }
        else {
            if(checkIfExistingPlateNo(HolderPlateNumber)) {
                etPlateNo.setError("Plate Number Already Exists");
                flag = false;
            }
        }
        if(TextUtils.isEmpty(HolderChassisNo)) {
            etChassisNo.setError("Please input Chassis Number");
            flag = false;
        }
        else {
            if(HolderChassisNo.length() != 17) {
                etChassisNo.setError("Please input 17 alpha numeric characters");
                flag = false;
            }
            else {
                if(!VinValidator.isVinValid(HolderChassisNo)) {
                    etChassisNo.setError("Please input valid Chassis Number");
                }
                else {
                    if(checkChassisNumber(HolderChassisNo)) {
                        etChassisNo.setError("Chassis Number Already Exists");
                    }
                }
            }
        }
        if(TextUtils.isEmpty(HolderEngineNo)) {
            etEngineNo.setError("Please input Engine Number");
            flag = false;
        }
        if(Transmission.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select transmission", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        return flag;
    }

    private boolean checkChassisNumber(final String chassisNumber) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CDFile");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(CDFile.class).getCdchassisno().toUpperCase().equals(chassisNumber.toUpperCase())) {
                        existingChassis = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return existingChassis;
    }

    private boolean checkIfExistingPlateNo(final String plateNumber) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CDFile");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(CDFile.class).getCdplateno().toUpperCase().equals(plateNumber.toUpperCase())) {
                        existingPlate = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return existingPlate;
    }
}
