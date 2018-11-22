package capstone.abang.com.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.ChildEventListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import capstone.abang.com.Car_Owner.Endorse.SelectCategory;
import capstone.abang.com.Models.CategoryFile;

public class FirebaseHelper {
    private static final String TAG = "TAGS";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRefssss;
    private ArrayList<CategoryFile> categories = new ArrayList<>();


    public FirebaseHelper() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRefssss = firebaseDatabase.getReference("CategoryFile");
    }

    public void fetchData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds: dataSnapshot.getChildren()) {

                CategoryFile category = ds.getValue(CategoryFile.class);
                if(category.getCatStatus().equals("AC")) {
                    Log.d(TAG, "fetchData: ROSSOSDOOSDSDSDSDSDSD");
                    categories.add(category);
                }

        }
    }

    public ArrayList<CategoryFile> retrieve() {
        myRefssss.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                fetchData(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return categories;
    }


}
