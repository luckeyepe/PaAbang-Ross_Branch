package capstone.abang.com.Car_Owner.Endorse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vlk.multimager.activities.SinglePickActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;
import com.vlk.multimager.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import capstone.abang.com.Car_Owner.car_owner;
import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.CarPhotos;
import capstone.abang.com.Models.ReservationFile;
import capstone.abang.com.R;

public class AttachDocuments extends AppCompatActivity {


    ImageButton btnLTFRB;
    ImageButton btnORCR,btnAirport;
    Button Enroll;

    int selectedColor;

    private int OR_CR = 0;
    private int GARAGE = 1;
    private int SUPP_IMG_1 = 2;
    private int SUPP_IMG_2 = 3;
    private DatabaseReference mDatabaseCarDetail;
    private StorageReference storageReference;

    //vars
    private String uID;
    private String DownloadPath;
    private String ORCR;
    private String garage;
    private String imageone;
    private Image orcrImage;
    private Image garageImage;
    private Image add1Image;
    private String date;
    private int j;
    private ProgressDialog progressDialog;
    private Uri uriORCR;
    private Uri uriLTFRB;
    private Uri uriAIRPORTID;


    Toolbar toolbar;
    TextView toolbar_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_documents);

        mDatabaseCarDetail = FirebaseDatabase.getInstance().getReference("CDFile");

        toolbar =  findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        btnLTFRB = findViewById(R.id.btnLTFRB);
        btnORCR = findViewById(R.id.btnORCR);
        btnAirport = findViewById(R.id.btnAirport);
        Enroll = findViewById(R.id.btnEnroll);
        progressDialog = new ProgressDialog(this);

        //Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference("CarPhotos");

        //Current User ID
        uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btnORCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              intentORCR();
            }
        });
        btnLTFRB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                intentGarage();
            }
        });
        btnAirport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentAddDoc1();
            }
        });

        Enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasImage(btnORCR) || !hasImage(btnLTFRB)) {
                  Toast.makeText(AttachDocuments.this,"Fill in the Required Documents",Toast.LENGTH_SHORT).show();
                }
                else{
                    enlistCar();
                    boolean valid = true;
                    String holderLTFRB = getORCRtext(uriLTFRB).toLowerCase();
                    String holderAIRPORTID = getORCRtext(uriAIRPORTID).toLowerCase();
                    String holderORCR = getORCRtext(uriORCR).toLowerCase();
                    if(!holderORCR.contains("land transportation office")) {
                        valid = false;
                    }
                    if(!holderLTFRB.contains("land transportation franchising and regulation board")) {
                        valid = false;
                    }
                    if(!holderAIRPORTID.contains("accredited rent-a-car")) {
                        valid = false;
                    }
                    if(!valid) {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(AttachDocuments.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(AttachDocuments.this);
                        }
                        builder.setTitle("Failed")
                                .setMessage("Please upload correct documents")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setCancelable(false)
                                .show();
                    }
                    if(valid) {
                        Intent intent = getIntent();
                        final String Maker = intent.getStringExtra("Maker").toLowerCase();
                        final String Model = intent.getStringExtra("Model").toLowerCase();
                        final String Year = intent.getStringExtra("Year").toLowerCase();
                        final String PlateNumber = intent.getStringExtra("PlateNumber").toLowerCase();
                        final String ChassisNo= intent.getStringExtra("ChassisNo").toLowerCase();
                        final String EngineNo= intent.getStringExtra("EngineNo").toLowerCase();
                        if(holderORCR.contains(Maker) || holderORCR.contains(Model)
                                || holderORCR.contains(Year)
                                || holderORCR.contains(PlateNumber)
                                || holderORCR.contains(ChassisNo)
                                || holderORCR.contains(EngineNo)
                                || holderLTFRB.contains(Maker)
                                || holderLTFRB.contains(ChassisNo)
                                || holderLTFRB.contains(PlateNumber)
                                || holderLTFRB.contains(EngineNo)
                                || holderAIRPORTID.contains(PlateNumber)) {
                            enlistCar();
                        }
                    }
                }
            }
        });

        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText("Attach Documents");

    }

    private void enlistCar() {
        Intent intent = getIntent();
        final ArrayList<Image> imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
        final String[] dents = intent.getStringArrayExtra("dents");
        final String CatCodeHolder = intent.getStringExtra("KEY");
        final String Maker = intent.getStringExtra("Maker");
        final String Model = intent.getStringExtra("Model");
        final String Year = intent.getStringExtra("Year");
        final int year = Integer.parseInt(Year);
        final String Capacity = intent.getStringExtra("Capacity");
        final int capacity = Integer.parseInt(Capacity);
        final String PlateNumber = intent.getStringExtra("PlateNumber");
        final String ChassisNo= intent.getStringExtra("ChassisNo");
        final String EngineNo= intent.getStringExtra("EngineNo");
        final String Transmission = intent.getStringExtra("Transmission");
        date = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss", Locale.getDefault()).format(new Date());

        final Image entity = imagesList.get(0);
        final String id = mDatabaseCarDetail.push().getKey();

        progressDialog.setMessage("Enrolling car...");
        progressDialog.show();
        progressDialog.setCancelable(false);


        StorageReference ref = storageReference.child(id).child(entity.uri.getLastPathSegment());
        ref.putFile(entity.uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri holder = taskSnapshot.getDownloadUrl();
                DownloadPath = holder.toString();
                StorageReference ref2 = storageReference.child(id).child(orcrImage.uri.getLastPathSegment());
                ref2.putFile(orcrImage.uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri holder = taskSnapshot.getDownloadUrl();
                        ORCR = holder.toString();
                        StorageReference ref5 = storageReference.child(id).child(garageImage.uri.getLastPathSegment());
                        ref5.putFile(garageImage.uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri holder = taskSnapshot.getDownloadUrl();
                                garage = holder.toString();
                                if(!Uri.EMPTY.equals(add1Image.uri)) {
                                    StorageReference ref3 = storageReference.child(id).child(add1Image.uri.getLastPathSegment());
                                    ref3.putFile(add1Image.uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Uri holder = taskSnapshot.getDownloadUrl();
                                            imageone = holder.toString();
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CDFile");
                                                    CDFile cdFile = new CDFile(id, Model, year, Maker, ChassisNo, DownloadPath, capacity, Transmission, PlateNumber, EngineNo, CatCodeHolder, uID, "Available", 0, ORCR, garage, 0, imageone, null,date, null, null, null,"Pending", "no", 0);
                                                    reference.child(id).setValue(cdFile);

                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ReservationFile");
                                                    String reservationID = databaseReference.push().getKey();
                                                    ReservationFile reservationFile = new ReservationFile(reservationID,  uID, "Pending", id, "Unseen", "0:00","Endorsement");
                                                    databaseReference.child(reservationID).setValue(reservationFile);
                                                    Uri[] uri = new Uri[imagesList.size()];
                                                    for(int i = 0; i < imagesList.size(); i++) {
                                                        uri[i] = Uri.parse("file://"+imagesList.get(i).imagePath);
                                                        StorageReference myRef = storageReference.child(id).child(uri[i].getLastPathSegment());
                                                        myRef.putFile(uri[i]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                                                String content = downloadUrl.toString();
                                                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("CDFile");
                                                                String photoID = dbRef.push().getKey();
                                                                CarPhotos photos = new CarPhotos();
                                                                photos.setImageUrl(content);
                                                                dbRef.child(id).child("photos").child(photoID).setValue(photos);
                                                            }
                                                        });
                                                    }

                                                    for(int i = 0; i < dents.length; i++) {
                                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("CDFile");
                                                        String dentsID = databaseReference1.push().getKey();
                                                        databaseReference1.child(id).child("dents").child(dentsID).child("part").setValue(dents[i]);
                                                    }

                                                    Intent intent = new Intent(getApplicationContext(), car_owner.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    progressDialog.dismiss();
                                                    finish();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private String getORCRtext(Uri uri) {
        Bitmap bitmapOrCR = null;
        String holder = null;
        try {
            bitmapOrCR = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }

        //OCR
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()) {
            Toast.makeText(this, "Could not get the text", Toast.LENGTH_SHORT).show();
        }
        else {
            Frame frame = new Frame.Builder().setBitmap(bitmapOrCR).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < items.size(); ++i) {
                TextBlock myItems = items.valueAt(i);
                stringBuilder.append(myItems.getValue());
            }
            holder = stringBuilder.toString();
        }

        return holder;
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable!=null);

        if(hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }
        return hasImage;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();

        }
        return  true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == OR_CR) {
                onSelectFromOR_CRresult(intent);
            }
            else if(requestCode == GARAGE){
                onSelectFromGarage_Result(intent);
            }
            else if(requestCode == SUPP_IMG_1){
                onResultFromIMG1(intent);
            }
        }
    }

    private void intentORCR() {

        ButterKnife.bind(this);
        selectedColor = 4475389;
        Intent intent = new Intent(this, SinglePickActivity.class);
        Params params = new Params();
        params.setCaptureLimit(1);
        params.setPickerLimit(1);
        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);
        intent.putExtra("activity","AD");

        startActivityForResult(intent,OR_CR);
    }
    private void intentGarage() {

        ButterKnife.bind(this);
        selectedColor = 4475389;
        Intent intent = new Intent(this, SinglePickActivity.class);
        Params params = new Params();
        params.setCaptureLimit(1);
        params.setPickerLimit(1);
        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);
        intent.putExtra("activity","AD");
        startActivityForResult(intent,GARAGE);
    }
    private void intentAddDoc1() {

        ButterKnife.bind(this);
        selectedColor = 4475389;
        Intent intent = new Intent(this, SinglePickActivity.class);
        Params params = new Params();
        params.setCaptureLimit(1);
        params.setPickerLimit(1);
        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);
        intent.putExtra("activity","AD");
        startActivityForResult(intent,SUPP_IMG_1);
    }


    private void onSelectFromOR_CRresult(Intent intent){


        final ArrayList<Image> imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);

        final Image entity = imagesList.get(0);
        orcrImage = imagesList.get(0);
        Glide.with(AttachDocuments.this)
                .load(entity.uri)
                .apply(new RequestOptions()
                        .placeholder(com.vlk.multimager.R.drawable.image_processing)
                        .centerCrop()
                       )
                .into(btnORCR);

        uriORCR = entity.uri;
    }

    private void onSelectFromGarage_Result(Intent intent){

        final ArrayList<Image> imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);

        final Image entity = imagesList.get(0);
        garageImage = imagesList.get(0);
        Glide.with(AttachDocuments.this)
                .load(entity.uri)
                .apply(new RequestOptions()
                        .placeholder(com.vlk.multimager.R.drawable.image_processing)
                        .centerCrop()
                )
                .into(btnLTFRB);
        uriLTFRB = entity.uri;

    }
    private void  onResultFromIMG1(Intent intent){


        final ArrayList<Image> imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);

        final Image entity = imagesList.get(0);
        add1Image = imagesList.get(0);
        Glide.with(AttachDocuments.this)
                .load(entity.uri)
                .apply(new RequestOptions()
                        .placeholder(com.vlk.multimager.R.drawable.image_processing)
                        .centerCrop()
                )
                .into(btnAirport);

        uriAIRPORTID = entity.uri;
    }
}
