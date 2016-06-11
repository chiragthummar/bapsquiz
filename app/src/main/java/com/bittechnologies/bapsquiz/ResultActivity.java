package com.bittechnologies.bapsquiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bittechnologies.bapsquiz.model.Question;
import com.bittechnologies.bapsquiz.util.ConnectionDetector;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thummar.hololib.PieGraph;
import com.thummar.hololib.PieSlice;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ResultActivity extends AppCompatActivity {

    static PieGraph pg;
    static ScrollView mScrollView;
    static DecimalFormat f = new DecimalFormat("#####.00");
    static TextView mPercentageTextView;
    static TextView mTotalInterestTextView;
    static TextView mTotalInterstAmountTextView;
    static TextView mTotalLoanAmountTextView;
    static TextView mTotalLoanAmountValueTextView;
    static TextView mTotalTotalPaymentTextView;
    static TextView mTotalTotalPaymentValueTextView;
    static Button btnGoToCalc;
    static TextView tvNodata;
    int counter, total_question;
    SharedPreferences mSharedPreferences;
    String userId;
    Button btnWrongAnswer;
    InterstitialAd mInterstitialAd;

    private AsyncHttpClient client;
    private RequestParams params;
    int book_id, chapter_id;

    public static TextView noData;
    ConnectionDetector mConnectionDetector;
    public static Button goTo;

    ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("caa-app-pub-8785923025546886/6876258653");
        AdRequest adRequest = new AdRequest.Builder()

                .build();
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                displayInterstitial();
            }
        });

        mInterstitialAd.loadAd(adRequest);
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        mConnectionDetector = new ConnectionDetector(getApplicationContext());
        Intent intent = getIntent();
        counter = intent.getIntExtra("counter", 0);
        total_question = intent.getIntExtra("total_question", 0);
        book_id = intent.getIntExtra("book_id", 0);
        chapter_id = intent.getIntExtra("chapter_id", 0);
        btnWrongAnswer = (Button) findViewById(R.id.showWro);

        tvNodata = (TextView) findViewById(R.id.tvNoDataFound);
        btnGoToCalc = (Button) findViewById(R.id.btn_go_to_calc);
        mScrollView = (ScrollView) findViewById(R.id.sv_piegraph);
        mPercentageTextView = (TextView) findViewById(R.id.percentage);
        mTotalInterestTextView = (TextView) findViewById(R.id.total_interest_percent);
        mTotalInterstAmountTextView = (TextView) findViewById(R.id.total_interest_value);
        mTotalLoanAmountTextView = (TextView) findViewById(R.id.loan_amount_percent);
        mTotalLoanAmountValueTextView = (TextView) findViewById(R.id.loan_amount_value);
        mTotalTotalPaymentTextView = (TextView) findViewById(R.id.total_payment_percent);
        mTotalTotalPaymentValueTextView = (TextView) findViewById(R.id.total_payment_value);

        pg = (PieGraph) findViewById(R.id.piegraph);

        mSharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        userId = mSharedPreferences.getString("userId", "");

        params = new RequestParams();
        client = new AsyncHttpClient();

        goTo = (Button) findViewById(R.id.btn_go_to_calc);
        noData = (TextView) findViewById(R.id.tvNoDataFound);
        goTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateResult();
                setData();
            }
        });
        generateResult();
        setData();


        ArrayList<Question> wrongList = (ArrayList<Question>) getIntent().getSerializableExtra("wrong");
        for (int i = 0; i < wrongList.size(); i++) {
            Log.e("Question", wrongList.get(i).getQUESTION());
        }
        ArrayList<String> wronganswers = getIntent().getStringArrayListExtra("wronganswer");
        for (int i = 0; i < wronganswers.size(); i++) {
            Log.e("Wrong Question", wronganswers.get(i).toString());
        }
        Log.e("Wrong Question", wronganswers.size() + "s");


        btnWrongAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, WrongAnswerActivity.class);
                ArrayList<Question> wrongList = (ArrayList<Question>) getIntent().getSerializableExtra("wrong");
                ArrayList<String> wronganswers = getIntent().getStringArrayListExtra("wronganswer");
                intent.putExtra("wrong", wrongList);
                intent.putStringArrayListExtra("wronganswer", wronganswers);
                finish();
                startActivity(intent);
            }
        });

    }

    public void displayInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(ResultActivity.this,MainActivity.class));
    }

    public void generateResult() {


        if (!mConnectionDetector.isConnectingToInternet()) {
            // StaticData.snackNormalMessage(getActivity(),"Not Connected");
            mScrollView.setVisibility(View.GONE);
            btnGoToCalc.setVisibility(View.VISIBLE);
            tvNodata.setVisibility(View.VISIBLE);


        } else {


            mScrollView.setVisibility(View.VISIBLE);
            btnGoToCalc.setVisibility(View.GONE);
            tvNodata.setVisibility(View.GONE);

            params.put("name", userId);
            params.put("book_id", book_id);
            params.put("total_question", total_question);
            params.put("true_answer", counter);
            params.put("false_answer", (total_question - counter));
            params.put("chapter_id", chapter_id);

            Log.d("Chape", "" + chapter_id);
            Log.d("Book   ", "" + book_id);
            Log.d("userrrrrrrr   ", "" + userId);

            client.post("http://baps.bittechnologies.in/bittechnologies/baps_quiz/webservice/add_result.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    pdialog = new ProgressDialog(ResultActivity.this);
                    pdialog.setMessage("Please Wait..");
                    pdialog.setCancelable(false);
                    pdialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String str = new String(responseBody);
                    Log.d("sreeeeeeeeeeeeeeeeeeeee", "" + str);
                    if (str.equals("Success")) {
                        Log.d("sreeeeeeeeeeeeeeeeeeeee", "" + "Called");

//                       Toast.makeText(getApplicationContext(),"Result Generated Successfully",Toast.LENGTH_SHORT).show();
                    }
                    if (pdialog.isShowing()) pdialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (pdialog.isShowing()) pdialog.dismiss();
                    Log.e("onFailure", "" + responseBody.toString());
                }
            });
        }
    }

    public void setData() {
        mScrollView.setVisibility(View.VISIBLE);
        btnGoToCalc.setVisibility(View.GONE);
        tvNodata.setVisibility(View.GONE);
        mPercentageTextView.setText(String.valueOf(total_question - counter));

        double fa = (double) counter / total_question * 100;
        double tr = (double) (total_question - counter) / total_question * 100;
        mTotalInterestTextView.setText(String.valueOf(f.format(tr)) + "%");

        mTotalInterstAmountTextView.setText(String.valueOf(total_question - counter) + " ખોટા જવાબ");

        // Toast.makeText(getApplicationContext(),"fa"+fa,Toast.LENGTH_SHORT).show();

        mTotalLoanAmountTextView.setText(String.valueOf(f.format(fa)) + "%");
        mTotalLoanAmountValueTextView.setText(String.valueOf(counter) + " સાચા જવાબ");

        mTotalTotalPaymentTextView.setText("100%");
        mTotalTotalPaymentValueTextView.setText(String.valueOf(total_question) + " સવાલ");

        PieSlice slice = new PieSlice();
        slice.setColor(Color.parseColor("#ef333a"));
        slice.setValue((float) (total_question - counter));
        pg.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#8edd48"));
        slice.setValue((float) (counter));
        pg.addSlice(slice);
    }
}
