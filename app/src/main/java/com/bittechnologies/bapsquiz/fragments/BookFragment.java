package com.bittechnologies.bapsquiz.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.bittechnologies.bapsquiz.R;
import com.bittechnologies.bapsquiz.adapters.CustomBookAdapter;
import com.bittechnologies.bapsquiz.custom.GridViewCompat;
import com.bittechnologies.bapsquiz.model.Book;
import com.bittechnologies.bapsquiz.util.ConnectionDetector;
import com.bittechnologies.bapsquiz.util.StaticData;

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
public class BookFragment extends Fragment {

    private AdView mAdView;
    ProgressDialog progressDialog;

    public BookFragment() {
        // Required empty public constructor
    }

    int exam_id;

    @SuppressLint({"NewApi", "ValidFragment"})
    public BookFragment(int exam_id) {
        // Required empty public constructor
        this.exam_id = exam_id;
    }

    AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();
    RequestParams params = new RequestParams();
    private GridViewCompat grid;
    private List<Book> bookList = new ArrayList<>();

    public static TextView noData;
    ConnectionDetector mConnectionDetector;
    public static Button goTo;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book, container, false);
        mConnectionDetector = new ConnectionDetector(getActivity().getApplicationContext());

        mAdView = (AdView) v.findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);

        grid = (GridViewCompat) v.findViewById(R.id.book_grid);
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

//                Book cr = (Book) parent.getItemAtPosition(position);
//                int idd = cr.getId();
//                ChapterFragment mChapterFragment = new ChapterFragment(idd);
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.mainContainer, mChapterFragment).addToBackStack("Book")
//                        .commit();
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.title)
                        .items(R.array.items)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                /**
                                 * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected check box to actually be selected.
                                 * See the limited multi choice dialog example in the sample project for details.
                                 **/
                                return true;
                            }
                        })
                        .positiveText(R.string.choose)
                        .show();
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

            params.put("table_name", "book");
            SharedPreferences  mSharedPreferences=getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);

            //StaticData.snackNormalMessage(getActivity(),"Examid"+ getArguments().getInt("exam_id",0));
            params.put("exam_id", String.valueOf(mSharedPreferences.getString("exam_id","")));
            Log.d("JSONcsdfdsfsdf-----", "++++++++++++++++++++++" + exam_id);
            bookList.clear();
            mAsyncHttpClient.post("http://baps.bittechnologies.in/bittechnologies/baps_quiz/webservice/get_all_book.php", params, new AsyncHttpResponseHandler() {


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
                            Book mBook = new Book();
                            mBook.setName(jsonObject1.getString("name"));
                            mBook.setPhoto(jsonObject1.getString("photo"));
                            mBook.setId(jsonObject1.getInt("id"));
                            Log.d("Name", "+++++++++++++++++++++++++" + jsonObject1.getString("name"));
                            Log.d("Photo Path", "+++++++++++++++++++++++++" + jsonObject1.getString("photo"));
                            bookList.add(mBook);
                        }
                    } catch (Exception e) {

                    }
                    CustomBookAdapter mCustomBookAdapter = new CustomBookAdapter(getActivity(), bookList);
                    Log.d("Sizeeeeebook", "+++++++++++++++++++++++++++" + bookList.size());
                    grid.setAdapter(mCustomBookAdapter);

                }


                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        }


    }

}
