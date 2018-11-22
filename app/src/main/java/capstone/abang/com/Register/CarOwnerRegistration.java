package capstone.abang.com.Register;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vlk.multimager.activities.SinglePickActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;

import java.util.ArrayList;

import butterknife.ButterKnife;
import capstone.abang.com.Car_Owner.car_owner;
import capstone.abang.com.Models.CDFile;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.Utility;

public class CarOwnerRegistration extends AppCompatActivity {
    private ImageButton btnCompanyID;
    private ImageButton btnLicenseID;
    private Image companyImage;
    private Image licenseImage;
    private Uri companyURI;
    private Uri licenseURI;
    private Button btnUploadFiles;
    int selectedColor;
    private UDFile ownerDetails;

    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private int holder = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_owner_registration);
        setupWidgets();
        getUserDetails();
        btnCompanyID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder = 1;
                selectImage();
            }
        });
        btnLicenseID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder = 2;
                selectImage();
            }
        });
        btnUploadFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String companyHolder = getOCRText(companyURI).toLowerCase();
                String licenseHolder = getOCRText(licenseURI).toLowerCase();
                //temporarily allow owner to continue registration
                updateUserStatus();
                if(companyHolder.contains(ownerDetails.getUDFullname().toLowerCase()) || licenseHolder.contains(ownerDetails.getUDFullname().toLowerCase())
                        || licenseHolder.contains("land transportation office")) {
                    if(hasFace(companyURI) && hasFace(licenseURI)) {
                        updateUserStatus();
                    }
                    else {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(CarOwnerRegistration.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(CarOwnerRegistration.this);
                        }
                        builder.setTitle("Failed")
                                .setMessage("Please upload correct IDs")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setCancelable(false)
                                .show();
                    }
                }
                else {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(CarOwnerRegistration.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(CarOwnerRegistration.this);
                    }
                    builder.setTitle("Failed")
                            .setMessage("Please upload correct IDs")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setCancelable(false)
                            .show();
                }
            }
        });
    }

    private boolean hasFace(Uri uri) {
        boolean hasFace = false;
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
        }
        if(bitmap != null) {
            FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                    .setTrackingEnabled(false)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .setMode(FaceDetector.FAST_MODE)
                    .build();
            if(!faceDetector.isOperational()) {
                Log.d("FACE DETECTION", "Is not operational");
                hasFace = false;
            }
            else {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<Face> sparseArray = faceDetector.detect(frame);
                hasFace = sparseArray.size() != 0 && sparseArray.size() == 1;
                if(hasFace) {
                    Face face = sparseArray.valueAt(0);
                    float x1 = face.getPosition().x;
                    float y1 = face.getPosition().y;
                    float x2 = x1 + face.getWidth();
                    float y2 = y1 + face.getHeight();
                    RectF rectF = new RectF(x1, y1, x2, y2);
                }
            }
        }
        return hasFace;
    }

    private void updateUserStatus() {
        final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UDFile");
        reference.child(uID).child("udDocuStatus").setValue("Validated");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Photos").child(uID).child(companyURI.getLastPathSegment());
        storageReference.putFile(companyURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String path = taskSnapshot.getDownloadUrl().toString();
                        reference.child(uID).child("udCompanyID").setValue(path);
                    }
                });
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference("Photos").child(uID).child(licenseURI.getLastPathSegment());
        storageReference1.putFile(licenseURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String path = taskSnapshot.getDownloadUrl().toString();
                        reference.child(uID).child("udlicenseID").setValue(path);
                    }
                });


        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(CarOwnerRegistration.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(CarOwnerRegistration.this);
        }
        builder.setTitle("Success")
                .setMessage("You are now complete with your registration!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CarOwnerRegistration.this, car_owner.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(false)
                .show();



    }

    private void getUserDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UDFile");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(UDFile.class).getUDUserCode().equalsIgnoreCase(uID)) {
                        setUDFile(snapshot.getValue(UDFile.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setUDFile(UDFile udFile) {
        ownerDetails = udFile;
    }
    private String getOCRText(Uri uri) {
        Bitmap bitmap = null;
        String holder = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
        }

        //OCR
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()) {
            Toast.makeText(this, "Could not get the text", Toast.LENGTH_SHORT).show();
        }
        else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
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
    private void selectImage() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_custom_dialog);

        TextView tvCamera = dialog.findViewById(R.id.tvCamera);
        ImageView ivCamera = dialog.findViewById(R.id.iconCamera);

        TextView tvGallery = dialog.findViewById(R.id.tvGallery);
        ImageView ivGallery = dialog.findViewById(R.id.iconGallery);


        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Utility.checkPermission(CarOwnerRegistration.this);
                if(result) {
                    cameraIntent();
                    dialog.dismiss();
                }
            }
        });

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Utility.checkPermission(CarOwnerRegistration.this);
                if(result) {
                    cameraIntent();
                    dialog.dismiss();
                }
            }
        });

        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Utility.checkPermission(CarOwnerRegistration.this);
                if(result) {
                    galleryIntent();
                    dialog.dismiss();
                }
            }
        });

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Utility.checkPermission(CarOwnerRegistration.this);
                if(result) {
                    galleryIntent();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void galleryIntent() {
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

        startActivityForResult(intent,SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == SELECT_FILE) {
                onGalleryIntent(intent);
            }
            else if(requestCode == REQUEST_CAMERA){
                onCaptureImageResult(intent);
            }
        }
    }

    private void onGalleryIntent(Intent intent){
        final ArrayList<Image> imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
        if(holder == 1) {
            final Image entity = imagesList.get(0);
            companyImage = imagesList.get(0);
            Glide.with(CarOwnerRegistration.this)
                    .load(entity.uri)
                    .apply(new RequestOptions()
                            .placeholder(com.vlk.multimager.R.drawable.image_processing)
                            .centerCrop()
                    )
                    .into(btnCompanyID);

            companyURI = entity.uri;
        }
        else {
            final Image entity = imagesList.get(0);
            licenseImage = imagesList.get(0);
            Glide.with(CarOwnerRegistration.this)
                    .load(entity.uri)
                    .apply(new RequestOptions()
                            .placeholder(com.vlk.multimager.R.drawable.image_processing)
                            .centerCrop()
                    )
                    .into(btnLicenseID);

            licenseURI = entity.uri;
        }

    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        if(holder == 1) {
            btnCompanyID.setImageBitmap(bitmap);
            btnCompanyID.setScaleType(ImageView.ScaleType.CENTER_CROP);
            btnCompanyID.setBackgroundResource(0);
            companyURI = data.getData();
        }
        else {
            btnLicenseID.setImageBitmap(bitmap);
            btnLicenseID.setScaleType(ImageView.ScaleType.CENTER_CROP);
            btnLicenseID.setBackgroundResource(0);
            licenseURI = data.getData();
        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void setupWidgets() {
        btnLicenseID = findViewById(R.id.imgViewLicenseID);
        btnCompanyID = findViewById(R.id.imgViewCoopID);
        btnUploadFiles = findViewById(R.id.btnUploadFiles);
    }
}
