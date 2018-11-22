package capstone.abang.com.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import capstone.abang.com.R;

/**
 * Created by Salarda on 09/02/2018.
 */

public class AdsPagerAdapter extends PagerAdapter {

    private String[] list;
    private LayoutInflater layoutInflater;
    private Context ctx;

    public AdsPagerAdapter(Context ctx, String[] list) {
        this.ctx = ctx;
        this.list = list;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.layout_model_ads,container,false);
        ImageView adImage = itemView.findViewById(R.id.fetch_image_ads);

        Glide.with(itemView.getContext())
                .load(list[position])
                .into(adImage);

        ViewPager pager = (ViewPager) container;
        pager.addView(itemView, 0);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager pager = (ViewPager) container;
        View view = (View) object;
        pager.removeView(view);
    }
}