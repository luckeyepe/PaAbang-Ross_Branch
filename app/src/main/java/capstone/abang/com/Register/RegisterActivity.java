package capstone.abang.com.Register;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vlk.multimager.activities.SinglePickActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;

import java.util.ArrayList;
import java.util.regex.Pattern;

import capstone.abang.com.Car_Owner.Endorse.AttachDocuments;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.UniversalImageLoader;
import capstone.abang.com.Utils.Utility;

public class RegisterActivity extends AppCompatActivity {
    //Declaring all widgets
    private ImageView imgViewProfile;
    private EditText etPassword;
    private EditText etEmail;
    private EditText etFullname;
    private EditText etAddress;
    private EditText etConactNumber;
    private Button btnNext;
    private Image SelectedImg;
    private LinearLayout layout;

    //Declaring variables
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private int SelectedImage= 0;
    private Uri holderUri;
    private Bitmap bitmapHolder;
    private boolean existingContactNumber;


    private boolean FromCamera = true;

    TextView toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        imgViewProfile = findViewById(R.id.imgViewProfile);

        etPassword = findViewById(R.id.etRegPassword);
        etEmail = findViewById(R.id.etRegEmail);
        etFullname = findViewById(R.id.etRegFullname);
        etAddress = findViewById(R.id.etRegAddress);
        etConactNumber = findViewById(R.id.etRegContactNumber);
        btnNext = findViewById(R.id.btnNext);
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Create Account");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        imgViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
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
                boolean result = Utility.checkPermission(RegisterActivity.this);
                if(result) {
                    cameraIntent();
                    dialog.dismiss();
                }
            }
        });

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Utility.checkPermission(RegisterActivity.this);
                if(result) {
                    cameraIntent();
                    dialog.dismiss();
                }
            }
        });

        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Utility.checkPermission(RegisterActivity.this);
                if(result) {
                    galleryIntent();
                    dialog.dismiss();
                }
            }
        });

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Utility.checkPermission(RegisterActivity.this);
                if(result) {
                    galleryIntent();
                    dialog.dismiss();
                }
            }
        });



        dialog.show();
    }

    private void galleryIntent() {
      /*  Intent intent = new Intent();
        intent.setType("image*//**//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);*/

        int selectedColor;
        selectedColor = 4475389;
        Intent intent = new Intent(this, SinglePickActivity.class);
        Params params = new Params();
        params.setCaptureLimit(1);
        params.setPickerLimit(1);
        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);
        intent.putExtra("activity","first");
        startActivityForResult(intent, SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        startActivityForResult(intent, REQUEST_CAMERA);
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
                    Toast.makeText(getApplicationContext(), "Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
                FromCamera = false;
            }
            else if(requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
                FromCamera = true;
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        holderUri = data.getData();

        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        imgViewProfile.setImageBitmap(bitmap);
        bitmapHolder = bitmap;
        imgViewProfile.setBackgroundResource(0);

    }

    private void onSelectFromGalleryResult(Intent data) {
        final ArrayList<Image> imagesList = data.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);

        final Image entity = imagesList.get(0);
        SelectedImg = imagesList.get(0);
        Glide.with(RegisterActivity.this)
                .load(entity.uri)
                .apply(new RequestOptions()
                        .centerCrop()
                )
                .into(imgViewProfile);
        holderUri = entity.uri;
        imgViewProfile.setBackgroundResource(0);
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
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    validated();
            }
        });
    }

    private void validated() {
        String holderPassword = etPassword.getText().toString().trim();
        String holderEmail = etEmail.getText().toString().trim();
        String holderFullname = etFullname.getText().toString().trim();
        String holderAddress = etAddress.getText().toString().trim();
        String holderContactNumber = etConactNumber.getText().toString().trim();
        boolean valid = true;


        if(holderPassword.isEmpty()) {
            etPassword.setError("Please enter password");
            valid = false;
        }
        if(holderPassword.length() < 8) {
            etPassword.setError("Password must have at least 8 characters");
            valid = false;
        }
        if(holderEmail.isEmpty()) {
            etEmail.setError("Please enter email");
            valid = false;
        }
        else {
            if(!Patterns.EMAIL_ADDRESS.matcher(holderEmail).matches()) {
                etEmail.setError("Please enter valid Email Address");
                valid = false;
            }
        }

        if(holderFullname.isEmpty()) {
            etFullname.setError("Please enter full name");
            valid = false;
        }
        if(holderAddress.isEmpty()) {
            etAddress.setError("Please enter address");
            valid = false;
        }
        if(holderContactNumber.isEmpty()) {
            etConactNumber.setError("Please enter contact number");
            valid = false;
        }
        else {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            boolean isValid = false;
            Phonenumber.PhoneNumber phonenumber;
            try {
                 phonenumber = phoneNumberUtil.parse(holderContactNumber, "PH");
                 isValid = phoneNumberUtil.isValidNumber(phonenumber);
                String string = phoneNumberUtil.format(phonenumber, PhoneNumberUtil.PhoneNumberFormat.E164);;
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
            if(!isValid) {
                valid = false;
                etConactNumber.setError("Please input valid phone number");
            }
        }
        if(!hasImage(imgViewProfile)) {
            Toast.makeText(this, "Please input picture", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if(!checkFace()) {
            Toast.makeText(this, "Please take a valid picture", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if(valid) {
            checkEmail(holderEmail, holderContactNumber);
            moveToContinuation();
        }
    }

    private boolean checkFace() {
        boolean hasFace = true;
        Bitmap profile = null;
        try {
            profile = MediaStore.Images.Media.getBitmap(this.getContentResolver(), holderUri);
        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
        }

        if(profile != null) {

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
                Frame frame = new Frame.Builder().setBitmap(profile).build();
                SparseArray<Face> sparseArray = faceDetector.detect(frame);
                hasFace = sparseArray.size() != 0;
            }
        }

        return hasFace;
    }

    private void checkContactNumber(final String contact) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UDFile");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(UDFile.class).getUDContact().equals(contact)) {
                        etConactNumber.setError("Contact number already exists");
                        existingContactNumber = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkEmail(final String email, final String contact) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UDFile");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(UDFile.class).getUDEmail().equals(email)) {
                        etEmail.setError("Email already exists");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void moveToContinuation() {
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String name = etFullname.getText().toString().trim();
        String addr = etAddress.getText().toString().trim();
        String contact = etConactNumber.getText().toString().trim();

        Intent moveToNext = new Intent(this, RegisterContinuationActivity.class);
        moveToNext.putExtra("password", password);
        moveToNext.putExtra("email", email);
        moveToNext.putExtra("name", name);
        moveToNext.putExtra("addr", addr);
        moveToNext.putExtra("contact", contact);
        imgViewProfile.buildDrawingCache();
        Bitmap image = imgViewProfile.getDrawingCache();
        Bundle extras = new Bundle();
        extras.putParcelable("ProfileImage",image);
        moveToNext.putExtras(extras);




        startActivity(moveToNext);
    }

    //TOAST METHOD
    private void toastMethod(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
