package com.bittechnologies.bapsquiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bittechnologies.bapsquiz.model.Question;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.impl.execchain.MainClientExec;

public class WrongAnswerActivity extends AppCompatActivity {


    TextView txtQuestion;
    Button btnA, btnB, btnC, btnD;

    int score = 0;
    int qid = 0;
    Question currentQ = new Question();
    int counter;
    int total_question;

    ArrayList<Question> wrongList = new ArrayList<>();
    ArrayList<String> wronganswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answer);
        wrongList = (ArrayList<Question>) getIntent().getSerializableExtra("wrong");
        wronganswers = getIntent().getStringArrayListExtra("wronganswer");
        //  addQuestions();

        txtQuestion = (TextView) findViewById(R.id.txtQ);
        btnA = (Button) findViewById(R.id.quiz_button_mc_option_1);
        btnB = (Button) findViewById(R.id.quiz_button_mc_option_2);
        btnC = (Button) findViewById(R.id.quiz_button_mc_option_3);
        btnC.setText("Next Wrong Answer");
        btnD = (Button) findViewById(R.id.quiz_button_mc_option_4);
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WrongAnswerActivity.this, MainClientExec.class));
            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQuestionView();
            }
        });

        setQuestionView();

    }

    private void setQuestionView() {

        if (qid < wrongList.size()) {
            currentQ = wrongList.get(qid);
            Log.e("Arrraylist SIxeeeee", wrongList.size() + "");
            total_question = wrongList.size();

            txtQuestion.setText(wrongList.get(qid).getQUESTION());

            btnA.setText(wrongList.get(qid).getANSWER());
            btnB.setText(wronganswers.get(qid));

            btnB.setBackgroundColor(Color.RED);
            btnA.setBackgroundColor(Color.GREEN);

            qid++;
        } else {
            txtQuestion.setVisibility(View.GONE);
            btnA.setVisibility(View.GONE);
            btnB.setVisibility(View.GONE);
            btnC.setVisibility(View.GONE);
            btnD.setText("GO To Home");
            btnD.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(WrongAnswerActivity.this,MainActivity.class));
    }
    /*public void addQuestions() {



        currentQ = wrongList.get(qid);
        Log.e("Arrraylist SIxeeeee",wrongList.size()+"");
        total_question = wrongList.size();

        txtQuestion.setText(wrongList.get(qid).getQUESTION());

        btnA.setText(wrongList.get(qid).getANSWER());
        btnB.setText(wronganswers.get(qid));

        btnA.setBackgroundResource(R.drawable.button_multiple_choice_background);
        btnB.setBackgroundResource(R.drawable.button_multiple_choice_background);

        qid++;

    }*/
}
