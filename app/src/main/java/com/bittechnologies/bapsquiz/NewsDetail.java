package com.bittechnologies.bapsquiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bittechnologies.bapsquiz.model.FeedItem;
import com.bittechnologies.bapsquiz.util.ConnectionDetector;
import com.bittechnologies.bapsquiz.util.StaticData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class NewsDetail extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;
    ProgressDialog progressDialog;
    AsyncHttpClient mAsyncHttpClient=new AsyncHttpClient();
    RequestParams params=new RequestParams();
    private List<FeedItem> feedItemList = new ArrayList<FeedItem>();
    public static TextView noData;
    ConnectionDetector mConnectionDetector;
    public static Button goTo;
    int iid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        mConnectionDetector = new ConnectionDetector(this.getApplicationContext());
        goTo = (Button) findViewById(R.id.btn_go_to_calc);
        noData = (TextView) findViewById(R.id.tvNoDataFound);
        goTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadData();
            }
        });
        Intent intent=getIntent();
        iid=intent.getIntExtra("id",0);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        LoadData();



    }

    public void LoadData() {


        if (!mConnectionDetector.isConnectingToInternet()) {
            // StaticData.snackNormalMessage(this,"Not Connected");
            mViewPager.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            goTo.setVisibility(View.VISIBLE);


        }
        else {
            mViewPager.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            goTo.setVisibility(View.GONE);
            params.put("table_name", "news_new");
            mAsyncHttpClient.post("http://baps.bittechnologies.in/bittechnologies/baps_quiz/webservice/all_news.php", params, new AsyncHttpResponseHandler() {


                @Override
                public void onStart() {
                    super.onStart();
                    progressDialog = new ProgressDialog(NewsDetail.this);
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
                            mFeedItem.setNewsDate(jsonObject1.getString("news_date"));
                            Log.d("Name", "+++++++++++++++++++++++++" + jsonObject1.getString("title"));
                            Log.d("Photo Path", "+++++++++++++++++++++++++" + jsonObject1.getString("photo"));
                            feedItemList.add(mFeedItem);
                        }
                    } catch (Exception e) {

                    }
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), feedItemList);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    mViewPager.setCurrentItem(iid);
                    Log.d("iiiiiiiiiiiiiiiiiiiii", "iii" + iid);
                }


                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(FeedItem mFeedItem) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString("title", mFeedItem.getTitle());
            args.putString("photo", mFeedItem.getPhoto());
            args.putString("subtext", mFeedItem.getSubtext());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_news_detail, container, false);
            TextView txtTitle = (TextView) rootView.findViewById(R.id.new_maintxt);
            txtTitle.setText(getArguments().getString("title"));

            // txtTitle.setTypeface(custom_font);
            TextView txtSubtext = (TextView) rootView.findViewById(R.id.new_subtext);
            txtSubtext.setText(getArguments().getString("subtext"));

            ImageView news_picture=(ImageView)rootView.findViewById(R.id.news_img);
            Picasso.with(getContext()).load(getArguments().getString("photo"))
                    .error(R.drawable.baps_logo)
                    .placeholder(R.drawable.baps_logo)
                    .into(news_picture);

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        List<FeedItem> feedItems=new ArrayList<>();
        public SectionsPagerAdapter(FragmentManager fm, List<FeedItem> feedItemList) {
            super(fm);
            feedItems=feedItemList;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(feedItems.get(position));
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return feedItems.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
