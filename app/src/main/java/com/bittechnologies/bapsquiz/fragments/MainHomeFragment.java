package com.bittechnologies.bapsquiz.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bittechnologies.bapsquiz.MainActivity;
import com.bittechnologies.bapsquiz.R;
import com.bittechnologies.bapsquiz.adapters.MyRecyclerAdapter;
import com.bittechnologies.bapsquiz.model.FeedItem;
import com.bittechnologies.bapsquiz.util.ConnectionDetector;
import com.bittechnologies.bapsquiz.util.StaticData;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainHomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener,ViewPagerEx.OnPageChangeListener{


    public MainHomeFragment() {
        // Required empty public constructor
    }
    private RecyclerView mRecyclerView;
    ProgressDialog progressDialog;
    PagerIndicator mPagerIndicator,mPagerIndicator2;
    private MyRecyclerAdapter adapter;
    private List<FeedItem> feedItemList = new ArrayList<FeedItem>();
    private SliderLayout mDemoSlider;
    AsyncHttpClient mAsyncHttpClient=new AsyncHttpClient();
    RequestParams params=new RequestParams();
    public static TextView noData;
    ConnectionDetector mConnectionDetector;
    public  static Button goTo;
    MainActivity mainActivity;
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_main_home, container, false);
        mainActivity=new MainActivity();
         mConnectionDetector=new ConnectionDetector(getActivity().getApplicationContext());
        mDemoSlider = (SliderLayout)v.findViewById(R.id.slider);
  /* Initialize recyclerview */
        mPagerIndicator=(PagerIndicator)v.findViewById(R.id.app_indicator);
        mPagerIndicator2=(PagerIndicator)v.findViewById(R.id.app_indicator2);
        goTo=(Button)v.findViewById(R.id.btn_go_to_calc);
        noData=(TextView)v.findViewById(R.id.tvNoDataFound);
        mRecyclerView = (RecyclerView)v. findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        goTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // StaticData.snackNormalMessage(getActivity(),"Button Clicked");
                LoadData();
            }
        });
        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Jay Swaminarayan", R.drawable.sl1);
        file_maps.put("Hari Krishna Maharaj", R.drawable.sl2);
        file_maps.put("Swami Bapa", R.drawable.sl3);
        file_maps.put("Pramukh Swami Maharaj", R.drawable.sl4);

        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
       // HashMap<String,String> url_maps = new HashMap<String, String>();
        /*FeedItem mFeedItem=new FeedItem();
        mFeedItem.setPhoto("http://192.168.0.4:8081/baps_quiz/gui/images/bg.jpg");
        mFeedItem.setTitle("Hello Recycler View");
        mFeedItem.setSubtext("dlfjdlflsdhfhdsfhdhfhdsjhfjkshdhfjsdfhshdfkjhsdjkfhjkdshfjhsdjkfhksdhfdj");
        feedItemList.add(mFeedItem);*/
       /* adapter = new MyRecyclerAdapter(getActivity(), feedItemList);
        mRecyclerView.setAdapter(adapter);*/
      /*  url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");*/
        params.put("table_name","news_new");
        LoadData();

        v.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {


                    if (doubleBackToExitPressedOnce) {
                        return true;
                    }
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(getActivity(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce=false;
                        }
                    }, 2000);
                    return true;
                }
                return false;
            }
        } );

        return v;
    }



    public  void LoadData()
    {


        if(!mConnectionDetector.isConnectingToInternet())
        {
           // StaticData.snackNormalMessage(getActivity(),"Not Connected");
            mDemoSlider.setVisibility(View.GONE);
            mPagerIndicator.setVisibility(View.GONE);
            mPagerIndicator2.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            goTo.setVisibility(View.VISIBLE);


        }
        else {


            mDemoSlider.setVisibility(View.VISIBLE);
            mPagerIndicator.setVisibility(View.VISIBLE);
            mPagerIndicator2.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            goTo.setVisibility(View.GONE);

            mAsyncHttpClient.post("http://baps.bittechnologies.in/bittechnologies/baps_quiz/webservice/all_news.php", params, new AsyncHttpResponseHandler() {


                @Override
                public void onStart() {
                    super.onStart();
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Loading Data.......");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }


                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String str = new String(responseBody);
                    Log.d("JSON-----", "++++++++++++++++++++++" + str);
                    feedItemList.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            FeedItem mFeedItem = new FeedItem();
                            mFeedItem.setTitle(jsonObject1.getString("title"));
                            mFeedItem.setSubtext(jsonObject1.getString("subtext"));
                            mFeedItem.setPhoto(jsonObject1.getString("photo"));
                            Log.d("Name", "+++++++++++++++++++++++++" + jsonObject1.getString("title"));
                            Log.d("Photo Path", "+++++++++++++++++++++++++" + jsonObject1.getString("photo"));
                            feedItemList.add(mFeedItem);
                        }
                    } catch (Exception e) {

                    }
                /*CustomChapterAdapter customChapterAdapter=new CustomChapterAdapter(getActivity(),chapterList);
                Log.d("Sizeeeeebook","+++++++++++++++++++++++++++"+chapterList.size());
                grid.setAdapter(customChapterAdapter);*/
                    adapter = new MyRecyclerAdapter(getActivity(), feedItemList);
                    mRecyclerView.setAdapter(adapter);

                }


                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });


        }

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getActivity(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

}
