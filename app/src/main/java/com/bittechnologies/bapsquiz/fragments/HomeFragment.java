package com.bittechnologies.bapsquiz.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bittechnologies.bapsquiz.R;
import com.bittechnologies.bapsquiz.adapters.CustomGridAdapter;
import com.bittechnologies.bapsquiz.custom.GridViewCompat;
import com.bittechnologies.bapsquiz.model.Exam;
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
public class HomeFragment extends Fragment {


    ProgressDialog progressDialog;

    public HomeFragment() {
        // Required empty public constructor
    }

    AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();
    RequestParams params = new RequestParams();
    private GridViewCompat grid;
    private final String[] items = new String[]{"પ્રારંભ", "પ્રવેશ", "પરિચય", "પ્રવીણ- ૧", "પ્રવીણ-૨ ", "પ્રાજ્ઞ૧", "પ્રાજ્ઞ૨"};
    private List<Exam> examList = new ArrayList<>();
    public static TextView noData;
    ConnectionDetector mConnectionDetector;
    public static Button goTo;

    private AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mAdView = (AdView) v.findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
        mConnectionDetector = new ConnectionDetector(getActivity().getApplicationContext());
        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        grid = (GridViewCompat) v.findViewById(R.id.library_grid);
        grid.setLayoutAnimation(new GridLayoutAnimationController(AnimationUtils.loadAnimation(getActivity(), R.anim.grid_item_fadein), LayoutAnimationController.ORDER_NORMAL, GridLayoutAnimationController.PRIORITY_ROW));
        grid.setLayoutAnimation(new GridLayoutAnimationController(AnimationUtils.loadAnimation(this.getActivity(), R.anim.grid_item_fadein), 0.2f, 0.2f));

        goTo=(Button)v.findViewById(R.id.btn_go_to_calc);
        noData=(TextView)v.findViewById(R.id.tvNoDataFound);
        goTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // StaticData.snackNormalMessage(getActivity(),"Button Clicked");
                LoadData();
            }
        });
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Exam cr = (Exam) parent.getItemAtPosition(position);
                int idd = cr.getId();
                Bundle mBundle = new Bundle();
                mBundle.putInt("exam_id", idd);
                //  Toast.makeText(getActivity(),"Id   +++"+idd,Toast.LENGTH_SHORT).show();
                BookFragment mBookFragment = new BookFragment(idd);
                mBookFragment.setArguments(mBundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, mBookFragment).addToBackStack(null)
                        .commit();
            }
        });

            LoadData();



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

            params.put("table_name", "exam");
            examList.clear();
            mAsyncHttpClient.post("http://baps.bittechnologies.in/bittechnologies/baps_quiz/webservice/get_all_exam.php", params, new AsyncHttpResponseHandler() {

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
                            Exam mExam = new Exam();
                            mExam.setName(jsonObject1.getString("name"));
                            mExam.setPhoto(jsonObject1.getString("photo"));
                            mExam.setId(jsonObject1.getInt("id"));

                            Log.d("Name", "+++++++++++++++++++++++++" + jsonObject1.getString("name"));
                            Log.d("Photo Path", "+++++++++++++++++++++++++" + jsonObject1.getString("photo"));
                            examList.add(mExam);
                        }
                    } catch (Exception e) {

                    }
                    CustomGridAdapter mCustomGridAdapter = new CustomGridAdapter(getActivity(), examList);
                    Log.d("Sizeeeee", "+++++++++++++++++++++++++++" + examList.size());
                    grid.setAdapter(mCustomGridAdapter);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });

        }
    }


}
