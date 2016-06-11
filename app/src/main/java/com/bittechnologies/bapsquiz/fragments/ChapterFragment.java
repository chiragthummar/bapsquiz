package com.bittechnologies.bapsquiz.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.bittechnologies.bapsquiz.R;
import com.bittechnologies.bapsquiz.adapters.CustomChapterAdapter;
import com.bittechnologies.bapsquiz.custom.GridViewCompat;
import com.bittechnologies.bapsquiz.model.Chapter;
import com.bittechnologies.bapsquiz.util.ConnectionDetector;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChapterFragment extends Fragment {

    private AdView mAdView;
    public ChapterFragment() {
        // Required empty public constructor
    }

    ProgressDialog progressDialog;
    int book_id;

    @SuppressLint({"NewApi", "ValidFragment"})
    public ChapterFragment(int book_id) {
        // Required empty public constructor
        this.book_id = book_id;
    }

    AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();
    RequestParams params = new RequestParams();
    private GridViewCompat grid;
    private List<Chapter> chapterList = new ArrayList<>();


    public static TextView noData;
    ConnectionDetector mConnectionDetector;
    public static Button goTo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chapter, container, false);
        mAdView = (AdView) v.findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
        mConnectionDetector = new ConnectionDetector(getActivity().getApplicationContext());
        grid = (GridViewCompat) v.findViewById(R.id.grid_chapter);
        grid.setLayoutAnimation(new GridLayoutAnimationController(AnimationUtils.loadAnimation(getActivity(), R.anim.grid_item_fadein), LayoutAnimationController.ORDER_NORMAL, GridLayoutAnimationController.PRIORITY_ROW));
        grid.setLayoutAnimation(new GridLayoutAnimationController(AnimationUtils.loadAnimation(this.getActivity(), R.anim.grid_item_fadein), 0.2f, 0.2f));
        goTo = (Button) v.findViewById(R.id.btn_go_to_calc);
        noData = (TextView) v.findViewById(R.id.tvNoDataFound);
        goTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadData();
            }
        });
        LoadData();

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Chapter cr = (Chapter) parent.getItemAtPosition(position);
                int idd = cr.getId();
//                QuizFragment mQuizFragment = new QuizFragment(book_id, idd);
//                //   Toast.makeText(getActivity(),"Iddd"+book_id,Toast.LENGTH_SHORT).show();
//                //  Toast.makeText(getActivity(),"Chapter"+idd,Toast.LENGTH_SHORT).show();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.mainContainer, mQuizFragment).addToBackStack(null)
//                        .commit();
            }
        });


        return v;
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
    public void LoadData() {


        if (!mConnectionDetector.isConnectingToInternet()) {
            // StaticData.snackNormalMessage(getActivity(),"Not Connected");
            grid.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            goTo.setVisibility(View.VISIBLE);


        } else {


            grid.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            goTo.setVisibility(View.GONE);


            params.put("table_name", "chapter");
            params.put("book_id", String.valueOf(book_id));
            Log.d("JSONcsdfdsfsdf-----", "++++++++++++++++++++++" + book_id);
            chapterList.clear();
            mAsyncHttpClient.post("http://baps.bittechnologies.in/bittechnologies/baps_quiz/webservice/get_all_chapter.php", params, new AsyncHttpResponseHandler() {


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

                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Chapter mChapter = new Chapter();
                            mChapter.setName(jsonObject1.getString("name"));
                            mChapter.setPhoto(jsonObject1.getString("photo"));
                            mChapter.setId(jsonObject1.getInt("id"));
                            Log.d("Name", "+++++++++++++++++++++++++" + jsonObject1.getString("name"));
                            Log.d("Photo Path", "+++++++++++++++++++++++++" + jsonObject1.getString("photo"));
                            chapterList.add(mChapter);
                        }
                    } catch (Exception e) {

                    }
                    CustomChapterAdapter customChapterAdapter = new CustomChapterAdapter(getActivity(), chapterList);
                    Log.d("Sizeeeeebook", "+++++++++++++++++++++++++++" + chapterList.size());
                    grid.setAdapter(customChapterAdapter);

                }


                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });

        }
    }
}
