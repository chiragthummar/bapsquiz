package com.bittechnologies.bapsquiz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bittechnologies.bapsquiz.R;
import com.bittechnologies.bapsquiz.model.Exam;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by chira on 1/21/2016.
 */
public class CustomGridAdapter extends BaseAdapter implements View.OnTouchListener{

    private Context context;
    private String[] items;
    LayoutInflater inflater;

    private static double TENSION = 800;
    private static double DAMPER = 20; //friction

    private SpringSystem mSpringSystem;
    private Spring mSpring;


    private List<Exam> examList=new ArrayList<>();

    public CustomGridAdapter(Context context, List<Exam> examList) {
        this.context = context;
        this.examList=examList;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        mSpringSystem = SpringSystem.create();

        mSpring = mSpringSystem.createSpring();
        //mSpring.addListener(this);

        SpringConfig config = new SpringConfig(TENSION, DAMPER);
        mSpring.setSpringConfig(config);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.library_grid_item, null);
        }
        ImageView mImageView=(ImageView)convertView.findViewById(R.id.library_grid_quiz_image) ;
        TextView mTextView = (TextView) convertView.findViewById(R.id.library_grid_quiz_name);
        mTextView.setText(examList.get(position).getName());

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.logo_load_fail)
                .showImageOnFail(R.drawable.logo_load_fail)
                .showImageOnLoading(R.drawable.logo_load_fail).build();

//download and display image from url
        imageLoader.displayImage(examList.get(position).getPhoto(), mImageView, options);

     //   mImageView.setImageResource(icons[position]);

        final View cView=convertView;
        mSpring.removeAllListeners();
        mSpring.addListener(new SpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                cView.setScaleX(scale);
                cView.setScaleY(scale);
            }

            @Override
            public void onSpringAtRest(Spring spring) {

            }

            @Override
            public void onSpringActivate(Spring spring) {

            }

            @Override
            public void onSpringEndStateChange(Spring spring) {

            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return examList.size();
    }

    @Override
    public Object getItem(int position) {
        return examList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mSpring.setEndValue(1f);
                return true;
            case MotionEvent.ACTION_UP:
                mSpring.setEndValue(0f);
                return true;
        }

        return false;
    }
}