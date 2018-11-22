package capstone.abang.com.Car_Owner.Endorse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import capstone.abang.com.Models.CategoryFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.CustomAdapter;
import capstone.abang.com.Utils.FirebaseHelper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vlk.multimager.adapters.GalleryImagesAdapter;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;
import com.vlk.multimager.utils.Utils;

import java.util.ArrayList;

public class SelectCategory extends AppCompatActivity {

    Toolbar toolbar;
    TextView toolbar_title;

    ArrayList<String> list = new ArrayList<>();
    ListView listview;
    DatabaseReference dref;
    ArrayAdapter<String> adapter;


    Integer [] imageId = {R.drawable.imagelogo};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        toolbar =  findViewById(R.id.toolbar);
        toolbar_title =  findViewById(R.id.toolbar_title);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);




       Intent intent = this.getIntent();
        final ArrayList<Image> imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(4, GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(mLayoutManager);
        GalleryImagesAdapter imageAdapter = new GalleryImagesAdapter(this, imagesList, 1, new Params());
        recyclerView.setAdapter(imageAdapter);


        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText("Gallery");



        listview = findViewById(R.id.ListView);
        FirebaseHelper helper = new FirebaseHelper();
        CustomAdapter adapter = new CustomAdapter(SelectCategory.this, helper.retrieve());
        listview.setAdapter(adapter);




        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CategoryFile cat = (CategoryFile) adapterView.getAdapter().getItem(i);


                Intent intent = new Intent(SelectCategory.this,AddDents.class);
                intent.putParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST, imagesList);
               String CodeHolder = cat.getCatCode();
                intent.putExtra("KEY",CodeHolder);
                startActivity(intent);
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_category_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return true;
    }




}
