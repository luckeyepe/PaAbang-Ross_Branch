package capstone.abang.com.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import capstone.abang.com.Models.CategoryFile;
import capstone.abang.com.R;



public class CustomAdapter extends BaseAdapter {
    private static final String TAG = "Custom Adapter";

    Context mContext;
    ArrayList<CategoryFile> categories;


    public CustomAdapter(Context mContext, ArrayList<CategoryFile> categories) {
        this.mContext = mContext;
        this.categories = categories;
    }


    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(mContext).inflate(R.layout.snippet_listview_category,viewGroup, false);

        TextView textView = view.findViewById(R.id.txtcategoryname);
        final CategoryFile cat = (CategoryFile) this.getItem(i);
        textView.setText(cat.getCatDesc());
        Log.d(TAG, "getView: HHAHAHAHAHA");

        Log.d(TAG, "getViewasdasdasd: " + view);
        return view;
    }


}
