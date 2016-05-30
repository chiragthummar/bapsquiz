package com.bittechnologies.bapsquiz.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bittechnologies.bapsquiz.R;
import com.bittechnologies.bapsquiz.ResultActivity;
import com.bittechnologies.bapsquiz.model.Question;
import com.bittechnologies.bapsquiz.util.ConnectionDetector;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuizFragment extends Fragment {


    private static double TENSION = 800;
    private static double DAMPER = 20; //friction
    private SpringSystem mSpringSystem;
    private Spring mSpring;

    ArrayList<String> wronganswername=new ArrayList<>();
    ArrayList<Question> wrongAnswer=new ArrayList<>();

    LinearLayout qLinearLayout;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 500;
    List<Question> quesList = new ArrayList<>();
    int score = 0;
    int qid = 0;
    Question currentQ = new Question();
    TextView txtQuestion;
    RadioButton rda, rdb, rdc;
    Button butNext;
    Button btnA, btnB, btnC, btnD;
    String ans;
    int counter;
    int total_question;

    public QuizFragment() {
        // Required empty public constructor
    }

    InterstitialAd mInterstitialAd;
    public static TextView noData;
    ConnectionDetector mConnectionDetector;
    public static Button goTo;

    @SuppressLint({"NewApi", "ValidFragment"})
    public QuizFragment(int book_id, int chapter_id) {
        // Required empty public constructor
        this.book_id = book_id;
        this.chapter_id = chapter_id;
    }

    int book_id, chapter_id;
    AsyncHttpClient mAsyncHttpClient;
    RequestParams params;
    ProgressDialog progressDialog;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_quiz, container, false);


        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId("ca-app-pub-8785923025546886/6876258653");
        AdRequest adRequest = new AdRequest.Builder()

                .build();
        mInterstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){
                displayInterstitial();
            }
        });

        mInterstitialAd.loadAd(adRequest);
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }

        mConnectionDetector = new ConnectionDetector(getActivity().getApplicationContext());

        goTo=(Button)v.findViewById(R.id.btn_go_to_calc);
        noData=(TextView)v.findViewById(R.id.tvNoDataFound);
        goTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestions();
            }
        });


        txtQuestion = (TextView) v.findViewById(R.id.txtQ);
        btnA = (Button) v.findViewById(R.id.quiz_button_mc_option_1);
        btnB = (Button) v.findViewById(R.id.quiz_button_mc_option_2);
        btnC = (Button) v.findViewById(R.id.quiz_button_mc_option_3);
        btnD = (Button) v.findViewById(R.id.quiz_button_mc_option_4);
        final Animation animAlpha = AnimationUtils.loadAnimation(getActivity(), R.anim.grid_item_fadein);
        addQuestions();

        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (btnA.getText().equals(currentQ.getANSWER().toString())) {
                    counter++;
                    btnA.setBackgroundColor(Color.GREEN);
                    btnA.startAnimation(animAlpha);
                } else {
                    wrongAnswer.add(currentQ);
                    wronganswername.add(btnA.getText().toString());
                    btnA.setBackgroundColor(Color.RED);
                    rightAnswer();
                    btnA.startAnimation(animAlpha);


                }
                if (qid < total_question) {
                    currentQ = quesList.get(qid);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setQuestionView();
                        }
                    }, SPLASH_TIME_OUT);

                } else {
                    for(int i=0;i<wrongAnswer.size();i++)
                    {
                        Log.e("Id",wrongAnswer.get(i).getQUESTION());
                    }
                    Log.e("Array List",wrongAnswer.toString());
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Do you want to see result ?")
                            .setConfirmText("Yes,See it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    Intent intent=new Intent(getActivity(),ResultActivity.class);
                                    intent.putExtra("counter",counter);
                                    intent.putExtra("total_question",total_question);
                                    intent.putExtra("book_id",book_id);
                                    intent.putExtra("chapter_id",chapter_id);
                                    intent.putExtra("wrong", wrongAnswer);
                                    intent.putStringArrayListExtra("wronganswer", wronganswername);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }
        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnB.getText().equals(currentQ.getANSWER().toString())) {
                    counter++;
                    //  Toast.makeText(getActivity(),"Counter"+counter,Toast.LENGTH_SHORT).show();
                    btnB.setBackgroundColor(Color.GREEN);
                    btnB.startAnimation(animAlpha);

                } else {
                    wrongAnswer.add(currentQ);
                    wronganswername.add(btnB.getText().toString());
                    btnB.setBackgroundColor(Color.RED);
                    rightAnswer();
                    btnB.startAnimation(animAlpha);
                }
                if (qid < total_question) {
                    currentQ = quesList.get(qid);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setQuestionView();
                        }
                    }, SPLASH_TIME_OUT);
                } else {
                    for(int i=0;i<wrongAnswer.size();i++)
                    {
                        Log.e("Id",wrongAnswer.get(i).getQUESTION());
                    }
                    Log.e("Array List",wrongAnswer.toString());
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Do you want to see result ?")
                            .setConfirmText("Yes,See it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    Intent intent=new Intent(getActivity(),ResultActivity.class);
                                    intent.putExtra("counter",counter);
                                    intent.putExtra("total_question",total_question);
                                    intent.putExtra("book_id",book_id);
                                    intent.putExtra("chapter_id",chapter_id);
                                    intent.putExtra("wrong", wrongAnswer);
                                    intent.putStringArrayListExtra("wronganswer", wronganswername);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnC.getText().equals(currentQ.getANSWER().toString())) {
                    counter++;
                    //   Toast.makeText(getActivity(),"Counter"+counter,Toast.LENGTH_SHORT).show();
                    btnC.setBackgroundColor(Color.GREEN);
                    btnC.startAnimation(animAlpha);
                } else {
                    wrongAnswer.add(currentQ);
                    wronganswername.add(btnC.getText().toString());
                    btnC.setBackgroundColor(Color.RED);
                    rightAnswer();
                    btnC.startAnimation(animAlpha);
                }
                if (qid < total_question) {
                    currentQ = quesList.get(qid);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setQuestionView();
                        }
                    }, SPLASH_TIME_OUT);
                } else {
                    for(int i=0;i<wrongAnswer.size();i++)
                    {
                        Log.e("Id",wrongAnswer.get(i).getQUESTION());
                    }
                    Log.e("Array List",wrongAnswer.toString());
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Do you want to see result ?")
                            .setConfirmText("Yes,See it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    Intent intent=new Intent(getActivity(),ResultActivity.class);
                                    intent.putExtra("counter",counter);
                                    intent.putExtra("total_question",total_question);
                                    intent.putExtra("book_id",book_id);
                                    intent.putExtra("chapter_id",chapter_id);
                                    intent.putExtra("wrong", wrongAnswer);
                                    intent.putStringArrayListExtra("wronganswer", wronganswername);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }
        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnD.getText().equals(currentQ.getANSWER().toString())) {
                    counter++;
                    //  Toast.makeText(getActivity(),"Counter"+counter,Toast.LENGTH_SHORT).show();
                    btnD.setBackgroundColor(Color.GREEN);
                    btnD.startAnimation(animAlpha);
                } else {
                    wrongAnswer.add(currentQ);
                    wronganswername.add(btnD.getText().toString());
                    btnD.setBackgroundColor(Color.RED);
                    rightAnswer();
                    btnD.startAnimation(animAlpha);
                }
                if (qid < total_question) {
                    currentQ = quesList.get(qid);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setQuestionView();
                        }
                    }, SPLASH_TIME_OUT);
                } else {
                    for(int i=0;i<wrongAnswer.size();i++)
                    {
                        Log.e("Id",wrongAnswer.get(i).getQUESTION());
                    }
                    Log.e("Array List",wrongAnswer.toString());
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Do you want to see result ?")
                            .setConfirmText("Yes,See it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    Intent intent=new Intent(getActivity(),ResultActivity.class);
                                    intent.putExtra("counter",counter);
                                    intent.putExtra("total_question",total_question);
                                    intent.putExtra("book_id",book_id);
                                    intent.putExtra("chapter_id",chapter_id);
                                    intent.putExtra("wrong", wrongAnswer);
                                    intent.putStringArrayListExtra("wronganswer", wronganswername);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }
        });
        setQuestionView();

        return v;
    }


    public String rightAnswer() {
        String anss = currentQ.getANSWER();
        if (btnA.getText().equals(anss)) {
            btnA.setBackgroundColor(Color.GREEN);
        } else if (btnB.getText().equals(anss)) {
            btnB.setBackgroundColor(Color.GREEN);
        } else if (btnC.getText().equals(anss)) {
            btnC.setBackgroundColor(Color.GREEN);
        } else if (btnD.getText().equals(anss)) {
            btnD.setBackgroundColor(Color.GREEN);
        }
        return null;
    }

    private void setQuestionView() {


        txtQuestion.setText(currentQ.getQUESTION());

        btnA.setText(currentQ.getOPTA());
        btnB.setText(currentQ.getOPTB());
        btnC.setText(currentQ.getOPTC());
        btnD.setText(currentQ.getOPTD());

        btnA.setBackgroundResource(R.drawable.button_multiple_choice_background);
        btnB.setBackgroundResource(R.drawable.button_multiple_choice_background);
        btnC.setBackgroundResource(R.drawable.button_multiple_choice_background);
        btnD.setBackgroundResource(R.drawable.button_multiple_choice_background);
        qid++;
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

    public void addQuestions() {

        if (!mConnectionDetector.isConnectingToInternet()) {
            // StaticData.snackNormalMessage(getActivity(),"Not Connected");
            txtQuestion.setVisibility(View.GONE);
            btnA.setVisibility(View.GONE);
            btnB.setVisibility(View.GONE);
            btnC.setVisibility(View.GONE);
            btnD.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            goTo.setVisibility(View.VISIBLE);

       /*     qid--;
            currentQ = quesList.get(qid);
            total_question = quesList.size();
            // setQuestionView();
            total_question = quesList.size();
            txtQuestion.setText(currentQ.getQUESTION());
            Log.d("Qidddddddddddddddddd", "----------------------------------------" + qid);
            btnA.setText(currentQ.getOPTA());
            btnB.setText(currentQ.getOPTB());
            btnC.setText(currentQ.getOPTC());
            btnD.setText(currentQ.getOPTD());

            btnA.setBackgroundResource(R.drawable.button_multiple_choice_background);
            btnB.setBackgroundResource(R.drawable.button_multiple_choice_background);
            btnC.setBackgroundResource(R.drawable.button_multiple_choice_background);
            btnD.setBackgroundResource(R.drawable.button_multiple_choice_background);
            qid++;
            Log.d("Question LIst sizeeeee", "+++++++++++++++++++++++++" + quesList.size());*/


        } else {

            txtQuestion.setVisibility(View.VISIBLE);
            btnA.setVisibility(View.VISIBLE);
            btnB.setVisibility(View.VISIBLE);
            btnC.setVisibility(View.VISIBLE);
            btnD.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            goTo.setVisibility(View.GONE);


            params = new RequestParams();
            mAsyncHttpClient = new AsyncHttpClient();

            params.put("table_name", "question");
            params.put("book_id", String.valueOf(book_id));
            params.put("chapter_id", String.valueOf(chapter_id));
            Log.d("Quizzzzzzzzz Book-----", "++++++++++++++++++++++" + book_id);
            Log.d("Quizzzzzzzzz-----", "++++++++++++++++++++++" + chapter_id);
            mAsyncHttpClient.post("http://baps.bittechnologies.in/bittechnologies/baps_quiz/webservice/get_question.php", params, new AsyncHttpResponseHandler() {

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


                    try {
                        Log.d("JSON-----", "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + str);
                        JSONObject jsonObject = new JSONObject(str);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");

                        if (jsonArray.length() <= 0) {

                            Snackbar.make(getActivity().findViewById(android.R.id.content), "No Data Found", Snackbar.LENGTH_LONG).show();
                        } else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                Question mQuestion = new Question();
                                mQuestion.setQUESTION(jsonObject1.getString("question"));
                                mQuestion.setOPTA(jsonObject1.getString("option1"));
                                mQuestion.setOPTB(jsonObject1.getString("option2"));
                                mQuestion.setOPTC(jsonObject1.getString("option3"));
                                mQuestion.setOPTD(jsonObject1.getString("option4"));
                                mQuestion.setBook_id(jsonObject1.getInt("book_id"));
                                mQuestion.setChapter_id(jsonObject1.getInt("chapter_id"));
                                mQuestion.setID(jsonObject1.getInt("id"));
                                mQuestion.setANSWER(jsonObject1.getString("answer"));
                                quesList.add(mQuestion);
                            }
                            Log.d("Question LIst sizeeeee", "+++++++++++++++++++++++++" + quesList.size());
                            qid--;
                            currentQ = quesList.get(qid);
                            total_question = quesList.size();
                            // setQuestionView();
                            total_question = quesList.size();
                            txtQuestion.setText(currentQ.getQUESTION());
                            Log.d("Qidddddddddddddddddd", "----------------------------------------" + qid);
                            btnA.setText(currentQ.getOPTA());
                            btnB.setText(currentQ.getOPTB());
                            btnC.setText(currentQ.getOPTC());
                            btnD.setText(currentQ.getOPTD());

                            btnA.setBackgroundResource(R.drawable.button_multiple_choice_background);
                            btnB.setBackgroundResource(R.drawable.button_multiple_choice_background);
                            btnC.setBackgroundResource(R.drawable.button_multiple_choice_background);
                            btnD.setBackgroundResource(R.drawable.button_multiple_choice_background);
                            qid++;
                            Log.d("Question LIst sizeeeee", "+++++++++++++++++++++++++" + quesList.size());
                        }
                    } catch (Exception e) {

                    }


                }


                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d("Erorrrr ", "+++++++++++++++++++++++++" + new String(responseBody));
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }


            });
        }
    }

}
