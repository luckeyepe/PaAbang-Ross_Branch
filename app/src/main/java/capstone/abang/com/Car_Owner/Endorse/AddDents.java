package capstone.abang.com.Car_Owner.Endorse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Utils;

import java.util.ArrayList;

import capstone.abang.com.R;

public class AddDents extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbar_title;
    private ListView listViewDents;
    private String[] listcontent = {
            "Front Bumper",
            "Back Bumper",
            "Left Headlights",
            "Right Headlights",
            "Left Tail Lights",
            "Right Tail Lights",
            "Doors",
            "Hoods",
            "Left Step Bumpers",
            "Left Step Bumpers"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dents);
        setupWidgets();
        setupToolbar();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_multiple_choice,
                        listcontent);
        listViewDents.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listViewDents.setAdapter(adapter);
    }

    private void setupToolbar() {
        Utils.initToolBar(this, toolbar, true);
        toolbar_title.setText("Add Dents");
    }

    private void setupWidgets() {
        toolbar =  findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        listViewDents = findViewById(R.id.listviewdents);
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
        else{

            Intent intent = getIntent();
            final ArrayList<Image> imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
            final String CatCodeHolder = intent.getStringExtra("KEY");

            intent = new Intent(AddDents.this,BasicDetails.class);
            intent.putParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST, imagesList);
            intent.putExtra("KEY",CatCodeHolder);

            StringBuilder builder = new StringBuilder();
            int cntChoice = listViewDents.getCount();
            SparseBooleanArray sparseBooleanArray = listViewDents.getCheckedItemPositions();
            for(int i = 0; i < cntChoice; i++) {
                if(sparseBooleanArray.get(i)) {
                    builder.append(listViewDents.getItemAtPosition(i).toString());
                    builder.append("/");
                }
            }
            String[] dents = builder.toString().split("/");
            intent.putExtra("dents", dents);
            startActivity(intent);
            return true;
        }


    }


}
