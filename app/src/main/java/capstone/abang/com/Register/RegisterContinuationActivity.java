package capstone.abang.com.Register;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.UniversalImageLoader;
import capstone.abang.com.Utils.Utility;

public class RegisterContinuationActivity extends AppCompatActivity {
    //Declaring all widgets
    private ImageView imgViewNBI;
    private ImageView imgViewSecondaryID;
    private ImageButton imgBtnNbi;
    private ImageButton imgBtnSecondID;
    private Button btnRegister;
    private ProgressDialog progressDialog;
   TextView toolbar_title;

    //Declaring all holders
    private String name = null;
    private String pass = null;
    private String email = null;
    private String username = null;
    private String addr = null;
    private String type = null;
    private String contact = null;
    private Uri image = null;
    private String profileImage;
    private String nbiImage;
    private String secondaryImage;
    private String date = null;
    private String imgQr;
    private String nbiHolder;
    private String driversLicenseHolder;
    private String qrNbi;
    private String qrDriversLicense;


    //Responsible for photos
    private String userChoosenTask;
    private Uri holder1 = null;
    private Uri holder2 = null;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private int holder = 0;
    private Bitmap ProfilePicture;
    private String mCurrentPhotoPath;

    //Firebase things
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseUserHeader;
    private DatabaseReference mDatabaseUserDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiser_continuation);
        //Referencing all widgets
        imgViewNBI = findViewById(R.id.imgViewNbi);
        imgViewSecondaryID = findViewById(R.id.imgViewSecondId);
        imgBtnNbi = findViewById(R.id.btnNBI);
        imgBtnSecondID = findViewById(R.id.btnSecondaryID);
        btnRegister = findViewById(R.id.btnRegContinuation);
        progressDialog = new ProgressDialog(this);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Create Account");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Firebase things
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUserHeader = FirebaseDatabase.getInstance().getReference("UHFile");
        mDatabaseUserDetail = FirebaseDatabase.getInstance().getReference("UDFile");
        mStorage = FirebaseStorage.getInstance().getReference("Photos");


        //Methods
        setInit();
        setImage();
        initImageLoader();


    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setImage() {
        imgBtnNbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();
                holder = 1;
            }
        });
        imgBtnSecondID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                holder = 2;
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterContinuationActivity.this);
        builder.setTitle("Choose picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(RegisterContinuationActivity.this);

                if(items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if(result) {
                        cameraIntent();
                    }
                }
                else if(items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    if(result) {
                        galleryIntent();
                    }
                } else if(items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        startActivityForResult(intent, REQUEST_CAMERA);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if(requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Uri uri = data.getData();
        if(holder == 1) {
            UniversalImageLoader.setImage(uri.toString(), imgViewNBI, null,"");
            imgViewNBI.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder1 = uri;
            imgViewNBI.setBackgroundResource(0);
        }
        else {
            UniversalImageLoader.setImage(uri.toString(), imgViewSecondaryID, null,"");
            imgViewSecondaryID.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder2 = uri;
            imgViewSecondaryID.setBackgroundResource(0);
        }

    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri uri = data.getData();
        if(holder == 1) {
            UniversalImageLoader.setImage(uri.toString(), imgViewNBI, null,"");
            imgViewNBI.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder1 = uri;
            imgViewNBI.setBackgroundResource(0);
        }
        else {
            UniversalImageLoader.setImage(uri.toString(), imgViewSecondaryID, null,"");
            imgViewSecondaryID.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder2 = uri;
            imgViewSecondaryID.setBackgroundResource(0);
        }
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable!=null);

        if(hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }
        return hasImage;
    }

    private void setInit() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasImage(imgViewNBI) || !hasImage(imgViewSecondaryID)) {
                    toastMethod("Fill necessary info");
                }
                else {
                    getTextFromImage();
                    getQRCodes();
                    Toast.makeText(RegisterContinuationActivity.this, "" + nbiHolder, Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisterContinuationActivity.this, "" + qrNbi, Toast.LENGTH_SHORT).show();
                    Bundle bundle = getIntent().getExtras();
                    name = bundle.getString("name");
                    if(nbiHolder.contains(name) || nbiHolder.contains(qrNbi) || driversLicenseHolder.contains(name) || driversLicenseHolder.contains(qrDriversLicense)) {
                        Toast.makeText(RegisterContinuationActivity.this, "Lodii", Toast.LENGTH_SHORT).show();
                        registerAccount();
                    }
                }
            }
        });
    }

    private void getQRCodes() {
        Bitmap bitmapNbi = null;
        Bitmap bitmapDriversLicense = null;
        try {
            bitmapNbi = MediaStore.Images.Media.getBitmap(this.getContentResolver(), holder1);
            bitmapDriversLicense = MediaStore.Images.Media.getBitmap(this.getContentResolver(), holder2);

        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }

        //QR DECODE
        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext()).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        if(!detector.isOperational()) {
            Toast.makeText(this, "Could not get the QR code", Toast.LENGTH_SHORT).show();
        }
        else {
            if(bitmapNbi != null && bitmapDriversLicense != null) {
                Frame qrCodeFrame = new Frame.Builder().setBitmap(bitmapNbi).build();
                SparseArray<Barcode> barcodes = detector.detect(qrCodeFrame);
                for(int i = 0; i < barcodes.size(); i++) {
                    Barcode barcode = barcodes.valueAt(i);
                }
                if(barcodes.size() != 0) {
                    qrNbi = barcodes.valueAt(0).rawValue;
                }

                Frame driversFrame = new Frame.Builder().setBitmap(bitmapDriversLicense).build();
                SparseArray<Barcode> barcodeSparseArray = detector.detect(driversFrame);
                for(int i = 0; i < barcodeSparseArray.size(); i++) {
                    Barcode barcode = barcodeSparseArray.valueAt(i);
                }
                if(barcodeSparseArray.size() != 0) {
                    qrDriversLicense = barcodeSparseArray.valueAt(0).rawValue;
                }
            }
        }
    }

    private void getTextFromImage() {
        Bitmap bitmapNbi = null;
        Bitmap bitmapDriversLicense = null;
        try {
            bitmapNbi = MediaStore.Images.Media.getBitmap(this.getContentResolver(), holder1);
            bitmapDriversLicense = MediaStore.Images.Media.getBitmap(this.getContentResolver(), holder2);
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }

        //OCR
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()) {
            Toast.makeText(this, "Could not get the text", Toast.LENGTH_SHORT).show();
        }
        else {
            Frame frame = new Frame.Builder().setBitmap(bitmapNbi).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < items.size(); ++i) {
                TextBlock myItems = items.valueAt(i);
                stringBuilder.append(myItems.getValue());
            }
            nbiHolder = stringBuilder.toString();

            Frame frame1 = new Frame.Builder().setBitmap(bitmapDriversLicense).build();
            SparseArray<TextBlock> items1 = textRecognizer.detect(frame1);
            StringBuilder stringBuilder1 = new StringBuilder();
            for(int i = 0; i < items1.size(); ++i) {
                TextBlock myItems = items1.valueAt(i);
                stringBuilder1.append(myItems.getValue());
            }
            driversLicenseHolder = stringBuilder1.toString();
        }
    }

    private void registerAccount() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            name = bundle.getString("name");
            pass = bundle.getString("password");
            email = bundle.getString("email");
          /*  username = bundle.getString("username");*/
            addr = bundle.getString("addr");
            contact = bundle.getString("contact");
            String holderImage = bundle.getString("image");

            date = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(new Date());

            Bundle extras = getIntent().getExtras();
             ProfilePicture = extras.getParcelable("ProfileImage");

        }

        progressDialog.setMessage("Registering user...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            sendVerificationEmail();
                            FirebaseUser user = mAuth.getCurrentUser();
                            final String id = user.getUid();

                                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                try {
                                    BitMatrix bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 200, 200);
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();
                                    StorageReference myRef = mStorage.child(id).child("QR"+id);
                                    myRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            imgQr = downloadUrl.toString();
                                            UHFile newUserHeader = new UHFile(id, username, pass, "AC", date, imgQr);
                                            mDatabaseUserHeader.child(id).setValue(newUserHeader);
                                        }
                                    });
                                }
                                catch (WriterException e) {
                                    e.printStackTrace();
                                }

                            //Insert data to UHFile
                            final StorageReference myRef = mStorage.child(id).child(holder1.getLastPathSegment());
                            myRef.putFile(holder1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final Uri nbiUri = taskSnapshot.getDownloadUrl();
                                    nbiImage = nbiUri.toString();

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    ProfilePicture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();
                                    StorageReference myRef = mStorage.child(id).child("ProfilePicture"+id);
                                    myRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                                    {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Uri profile = taskSnapshot.getDownloadUrl();
                                            profileImage = profile.toString();
                                            StorageReference myRef = mStorage.child(id).child(holder2.getLastPathSegment());
                                            myRef.putFile(holder2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Uri secondary = taskSnapshot.getDownloadUrl();
                                                    secondaryImage = secondary.toString();
                                                    UDFile newUserDetail = new UDFile(id, name, addr, email, "AC", "Renter", contact, nbiImage,profileImage,secondaryImage, "PENDING", 0);
                                                    Log.d("MGA PATH KUYA", "TARA AY: " + nbiImage + " " + profileImage + " " + secondaryImage);
                                                    mDatabaseUserDetail.child(id).setValue(newUserDetail);
                                                    progressDialog.hide();
                                                    toastMethod("Email verification has been sent. Please verify");
                                                    mAuth.signOut();
                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        }
                        else {
                            toastMethod("Technical difficulties");
                            progressDialog.hide();
                        }
                    }
                });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                            }
                            else {
                                toastMethod("Couldn't send verification email. Please try again!");
                            }
                        }
                    });
        }
    }


    private void toastMethod(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //PERMISSION FOR CAMERA AND GALLERY
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo")) {
                        cameraIntent();
                    } else if(userChoosenTask.equals("Choose from Gallery")) {
                        galleryIntent();
                    }
                } else {
                    toastMethod("Denied!");
                }
                break;
        }
    }

}
