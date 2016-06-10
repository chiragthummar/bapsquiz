package com.bittechnologies.bapsquiz.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bittechnologies.bapsquiz.MainActivity;
import com.bittechnologies.bapsquiz.R;
import com.bittechnologies.bapsquiz.util.ConnectionDetector;
import com.bittechnologies.bapsquiz.util.StaticData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements Validator.ValidationListener {


    public LoginFragment() {
        // Required empty public constructor
    }

    //public String userName;
    ProgressDialog pdialog;
    String id, name, pass, mandal, ph;
    Validator mValidator;
    private AsyncHttpClient client;
    private RequestParams params;
    @NotEmpty
    EditText userId, userPassword;
    TextView txtSignup;
    private TextInputLayout input_layout_userId, input_layout_Name, input_layout_password, input_layout_Mandal, input_layout_Phone;
    private Button btnLogin;
    SharedPreferences mSharedPreferences;
    public static TextView noData;
    ConnectionDetector mConnectionDetector;
    public static Button goTo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mConnectionDetector = new ConnectionDetector(getActivity().getApplicationContext());
        userId = (EditText) v.findViewById(R.id.userId);
        userPassword = (EditText) v.findViewById(R.id.userPassword);
        txtSignup = (TextView) v.findViewById(R.id.txtSignup);

       /* SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        int flag = mSharedPreferences.getInt("Logged", 0);
        if (flag == 1) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, new HomeFragment()).addToBackStack("Home")
                    .commit();
        } else {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, new LoginFragment()).addToBackStack("Login")
                    .commit();
        }*/
        goTo = (Button) v.findViewById(R.id.btn_go_to_calc);
        noData = (TextView) v.findViewById(R.id.tvNoDataFound);
        goTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValidator.validate();
            }
        });
        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, new Registration()).addToBackStack("SignUp")
                        .commit();
            }
        });

        btnLogin = (Button) v.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValidator.validate();
            }
        });
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);


        return v;
    }



    @Override
    public void onValidationSucceeded() {
        LoadData();
    }


    public void LoadData() {

        if (!mConnectionDetector.isConnectingToInternet()) {
            // StaticData.snackNormalMessage(getActivity(),"Not Connected");
            userId.setVisibility(View.GONE);
            userPassword.setVisibility(View.GONE);
            txtSignup.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            goTo.setVisibility(View.VISIBLE);


        } else {


            userId.setVisibility(View.VISIBLE);
            userPassword.setVisibility(View.VISIBLE);
            txtSignup.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            goTo.setVisibility(View.GONE);

            params = new RequestParams();
            client = new AsyncHttpClient();

            id = userId.getText().toString();
            pass = userPassword.getText().toString();

            Log.e("Response is Success", id);
            Log.e("Response is Success", pass);
            params.put("table_name", "user");
            params.put("userId", id);
            params.put("password", pass);
            client.post("http://baps.bittechnologies.in/bittechnologies/baps_quiz/webservice/check_login.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    pdialog = new ProgressDialog(getActivity());
                    pdialog.setMessage("Please Wait..");
                    pdialog.setCancelable(false);
                    pdialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    mSharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);

                    String str = new String(responseBody);

                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        String temp = jsonObject.getString("results");
                        JSONArray userData = jsonObject.getJSONArray("data");

                        JSONObject jo = userData.getJSONObject(0);

                        Log.e("User id -----", jo.getString("name"));

                        Log.e("Json result ----", userData.toString());
                        if (temp.equals("Success")) {
                            //userName=jo.getString("name");
                            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                            mEditor.clear();
                            mEditor.putString("userId", jo.getString("userId"));
                            mEditor.putString("exma_id",jo.getString("exam_id"));
                            mEditor.putInt("Logged", 1);
                            mEditor.putString("userName", jo.getString("name"));
                            mEditor.commit();
/*

                    NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                    View v=navigationView.getHeaderView(0);
                    TextView imageView=(TextView)v.findViewById(R.id.txtUserNameHeader);
                    Log.e("Username","dsfdsfsdf"+mSharedPreferences.getString("userName","Jay Swaminarayan"));
                    imageView.setText(mSharedPreferences.getString("userName","Jay Swaminarayan"));
*/


                    /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.mainContainer, new MainHomeFragment()).addToBackStack("Login")
                            .commit();*/
                            Intent intent = getActivity().getIntent();
                            getActivity().finish();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "User Is Not Registered With US ", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Answerrrrrrrrrr", "" + jsonObject.getString("results"));
                        Log.d("String ", "sdfsdfsd" + jsonObject.getString("data"));

                    } catch (Exception e) {

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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fmenu, menu);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

}
