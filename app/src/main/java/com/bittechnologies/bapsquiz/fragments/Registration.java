package com.bittechnologies.bapsquiz.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bittechnologies.bapsquiz.R;
import com.bittechnologies.bapsquiz.adapters.CustomChapterAdapter;
import com.bittechnologies.bapsquiz.model.Chapter;
import com.bittechnologies.bapsquiz.model.Mandal;
import com.bittechnologies.bapsquiz.util.ConnectionDetector;
import com.bittechnologies.bapsquiz.util.StaticData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class Registration extends Fragment {

    public Registration() {
        // Required empty public constructor
    }

    //Asynch Lib
    private AsyncHttpClient client;
    private RequestParams params;
    ProgressDialog pdialog;
    String id, name, pass, mandal, ph;
    List<String> mandalList = new ArrayList<>();
//    EditText userId, userName, userPassword, userPhone;
//    private TextInputLayout input_layout_userId, input_layout_Name, input_layout_password, input_layout_Mandal, input_layout_Phone;
    private Button btnSignUp;
    private AutoCompleteTextView actv;
    ImageView logoView;
    public static TextView noData;
    ConnectionDetector mConnectionDetector;
    public static Button goTo;
    private TextView txtContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_registration, container, false);
        mConnectionDetector = new ConnectionDetector(getActivity().getApplicationContext());

//
//        txtContact=(TextView)v.findViewById(R.id.txtContact);
//        txtContact.setText(Html.fromHtml("<html>\n" +
//                "\t<head>\n" +
//                "\t\t<title></title>\n" +
//                "\t</head>\n" +
//                "\t<body>\n" +
//                "\t\t<table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
//                "\t\t\t<tbody>\n" +
//                "\t\t\t\t<tr>\n" +
//                "\t\t\t\t\t<td colspan=\"2\" style=\"width:486px;\">\n" +
//                "\t\t\t\t\t\t<p align=\"center\">\n" +
//                "\t\t\t\t\t\t\tતમારું નામ યાદી માં ઉમેરવા માટે સંપર્ક કરો</p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t</tr>\n" +
//                "\t\t\t\t<tr>\n" +
//                "\t\t\t\t\t<td colspan=\"2\" style=\"width:486px;\">\n" +
//                "\t\t\t\t\t\t<p align=\"center\">\n" +
//                "\t\t\t\t\t\t\tનીચે પ્રમાણે માહિતી સાથે <strong>&nbsp;૯૭૨૬૯૭૨૮૦૬</strong>&nbsp; નંબર પર સંપર્ક કરો</p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t</tr>\n" +
//                "\t\t\t\t<tr>\n" +
//                "\t\t\t\t\t<td style=\"width:162px;\">\n" +
//                "\t\t\t\t\t\t<p>\n" +
//                "\t\t\t\t\t\t\t<strong>નામ</strong></p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t\t<td style=\"width:324px;\">\n" +
//                "\t\t\t\t\t\t<p>\n" +
//                "\t\t\t\t\t\t\t____________________</p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t</tr>\n" +
//                "\t\t\t\t<tr>\n" +
//                "\t\t\t\t\t<td style=\"width:162px;\">\n" +
//                "\t\t\t\t\t\t<p>\n" +
//                "\t\t\t\t\t\t\t<strong>મો</strong></p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t\t<td style=\"width:324px;\">\n" +
//                "\t\t\t\t\t\t<p>\n" +
//                "\t\t\t\t\t\t\t____________________</p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t</tr>\n" +
//                "\t\t\t\t<tr>\n" +
//                "\t\t\t\t\t<td style=\"width:162px;\">\n" +
//                "\t\t\t\t\t\t<p>\n" +
//                "\t\t\t\t\t\t\t<strong>પરીક્ષા નંબર</strong></p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t\t<td style=\"width:324px;\">\n" +
//                "\t\t\t\t\t\t<p>\n" +
//                "\t\t\t\t\t\t\t____________________</p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t</tr>\n" +
//                "\t\t\t\t<tr>\n" +
//                "\t\t\t\t\t<td style=\"width:162px;\">\n" +
//                "\t\t\t\t\t\t<p>\n" +
//                "\t\t\t\t\t\t\t<strong>પરીક્ષા નામ</strong></p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t\t<td style=\"width:324px;\">\n" +
//                "\t\t\t\t\t\t<p>\n" +
//                "\t\t\t\t\t\t\t____________________</p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t</tr>\n" +
//                "\t\t\t\t<tr>\n" +
//                "\t\t\t\t\t<td style=\"width:162px;\">\n" +
//                "\t\t\t\t\t\t<p>\n" +
//                "\t\t\t\t\t\t\t<strong>મંડળ નું નામ</strong></p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t\t<td style=\"width:324px;\">\n" +
//                "\t\t\t\t\t\t<p>\n" +
//                "\t\t\t\t\t\t\t____________________</p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t</tr>\n" +
//                "\t\t\t\t<tr>\n" +
//                "\t\t\t\t\t<td colspan=\"2\" style=\"width:486px;\">\n" +
//                "\t\t\t\t\t\t<p align=\"center\">\n" +
//                "\t\t\t\t\t\t\tઉપર જણાવેલી માહિત <strong>Whatsapp</strong> કરવી</p>\n" +
//                "\t\t\t\t\t</td>\n" +
//                "\t\t\t\t</tr>\n" +
//                "\t\t\t</tbody>\n" +
//                "\t\t</table>a\n" +
//                "\t\t<p>\n" +
//                "\t\t\t&nbsp;</p>\n" +
//                "\t</body>\n" +
//                "</html>\n"));


//        input_layout_userId = (TextInputLayout) v.findViewById(R.id.input_layout_userId);
//        input_layout_Name = (TextInputLayout) v.findViewById(R.id.input_layout_Name);
//        input_layout_password = (TextInputLayout) v.findViewById(R.id.input_layout_password);
//        input_layout_Mandal = (TextInputLayout) v.findViewById(R.id.input_layout_Mandal);
//        input_layout_Phone = (TextInputLayout) v.findViewById(R.id.input_layout_Phone);
//        userId = (EditText) v.findViewById(R.id.userId);
//        userName = (EditText) v.findViewById(R.id.userName);
//        userPassword = (EditText) v.findViewById(R.id.userPassword);
//        actv = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextView1);
//        // userMandal = (EditText) v.findViewById(R.id.userMandal);
//        userPhone = (EditText) v.findViewById(R.id.userPhone);
//        btnSignUp = (Button) v.findViewById(R.id.btn_signup);
//        logoView=(ImageView)v.findViewById(R.id.imageView2);
//        userId.addTextChangedListener(new MyTextWatcher(userId));
//        userName.addTextChangedListener(new MyTextWatcher(userName));
//        userPassword.addTextChangedListener(new MyTextWatcher(userPassword));
//        //   userMandal.addTextChangedListener(new MyTextWatcher(userMandal));
//        userPhone.addTextChangedListener(new MyTextWatcher(userPhone));
//
//        goTo = (Button) v.findViewById(R.id.btn_go_to_calc);
//        noData = (TextView) v.findViewById(R.id.tvNoDataFound);
//        goTo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               getMandal();
//            }
//        });
//        getMandal();
//        btnSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitForm();
//
//            }
//        });


        return v;
    }


    private void getMandal() {
        if (!mConnectionDetector.isConnectingToInternet()) {
            // StaticData.snackNormalMessage(getActivity(),"Not Connected");
//            userId.setVisibility(View.GONE);
//            logoView.setVisibility(View.GONE);
//            userName.setVisibility(View.GONE);
//            btnSignUp.setVisibility(View.GONE);
//            userPassword.setVisibility(View.GONE);
//            actv.setVisibility(View.GONE);
//            userPassword.setVisibility(View.GONE);
//            userPhone.setVisibility(View.GONE);
//            noData.setVisibility(View.VISIBLE);
//            goTo.setVisibility(View.VISIBLE);


        } else {

//            userId.setVisibility(View.VISIBLE);
//            logoView.setVisibility(View.VISIBLE);
//            userName.setVisibility(View.VISIBLE);
//            btnSignUp.setVisibility(View.VISIBLE);
//            userPassword.setVisibility(View.VISIBLE);
//            actv.setVisibility(View.VISIBLE);
//            userPassword.setVisibility(View.VISIBLE);
//            userPhone.setVisibility(View.VISIBLE);
//            noData.setVisibility(View.GONE);
//            goTo.setVisibility(View.GONE);

            params = new RequestParams();
            client = new AsyncHttpClient();
            params.put("table_name", "mandal");
            client.post("http://baps.bittechnologies.in/bittechnologies/baps_quiz/webservice/get_mandal.php", params, new AsyncHttpResponseHandler() {
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
                    String str = new String(responseBody);
                    Log.d("JSON-----", "++++++++++++++++++++++" + str);

                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                       /* Mandal mMandal=new Mandal();
                        mMandal.setName( jsonObject1.getString("name"));
                        mMandal.setId(jsonObject1.getInt("id"));*/
                            mandalList.add(jsonObject1.getString("name"));
                        }
                    } catch (Exception e) {

                    }
                    String[] countries = mandalList.toArray(new String[mandalList.size()]);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, countries);
                    actv.setAdapter(adapter);
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

//    private void submitForm() {
//        if (!validateID() && !validateName() && !validatePassword() && !validateMandal() && !validatePhone()) {
//            return;
//        }
//
//
//        if (!mConnectionDetector.isConnectingToInternet()) {
//            // StaticData.snackNormalMessage(getActivity(),"Not Connected");
////            userId.setVisibility(View.GONE);
////            userPassword.setVisibility(View.GONE);
////            actv.setVisibility(View.GONE);
////            userPassword.setVisibility(View.GONE);
////            userPhone.setVisibility(View.GONE);
////            noData.setVisibility(View.VISIBLE);
////            goTo.setVisibility(View.VISIBLE);
//
//
//        } else {
//
////            userId.setVisibility(View.VISIBLE);
////            userPassword.setVisibility(View.VISIBLE);
////            actv.setVisibility(View.VISIBLE);
////            userPassword.setVisibility(View.VISIBLE);
////            userPhone.setVisibility(View.VISIBLE);
////            noData.setVisibility(View.GONE);
////            goTo.setVisibility(View.GONE);
////
////            params = new RequestParams();
////            client = new AsyncHttpClient();
////
////
////            id = userId.getText().toString();
////            name = userName.getText().toString();
////            pass = userPassword.getText().toString();
////            mandal = actv.getText().toString();
////            ph = userPhone.getText().toString();
//
//
//            params.put("userId", id);
//            params.put("name", name);
//            params.put("password", pass);
//            params.put("mandal", mandal);
//            params.put("phone", ph);
//            client.post("http://baps.bittechnologies.in/bittechnologies/baps_quiz/webservice/add_user.php", params, new AsyncHttpResponseHandler() {
//                @Override
//                public void onStart() {
//                    super.onStart();
//
//                    Log.d("userId", "++++++++++++++++++" + id);
//                    Log.d("name", "==============================" + name);
//                    Log.d("password", "-------------------------------" + pass);
//                    Log.d("mandal", "++++++++++++++++++++++++++++++++" + mandal);
//                    Log.d("phone", "+++++++++++++++++++++++++++++++++" + ph);
//                    pdialog = new ProgressDialog(getActivity());
//                    pdialog.setMessage("Please Wait..");
//                    pdialog.setCancelable(false);
//                    pdialog.show();
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                    if (responseBody != null) {
//
//                        String str = new String(responseBody);
//                        Log.e("Response is Success", str);
//
//                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.mainContainer, new LoginFragment()).addToBackStack("Login")
//                                .commit();
//
//                    }
//                    if (pdialog.isShowing()) pdialog.dismiss();
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                    if (pdialog.isShowing()) pdialog.dismiss();
//                    Log.e("onFailure", "" + responseBody.toString());
//                }
//            });
//        }
//     //   Toast.makeText(getActivity(), "Thank You!", Toast.LENGTH_SHORT).show();
//
//
//    }

//    private boolean validateID() {
//        if (userId.getText().toString().trim().isEmpty()) {
//            input_layout_userId.setError("Please Fill");
//            requestFocus(userId);
//            return false;
//        } else {
//            input_layout_userId.setErrorEnabled(false);
//        }
//
//        return true;
//    }
//
//    private boolean validateName() {
//        if (userName.getText().toString().trim().isEmpty()) {
//            input_layout_Name.setError("Please Fill");
//            requestFocus(userName);
//            return false;
//        } else {
//            input_layout_Name.setErrorEnabled(false);
//        }
//
//        return true;
//    }
//
//    private boolean validatePassword() {
//        if (userPassword.getText().toString().trim().isEmpty()) {
//            input_layout_password.setError("Please Fill");
//            requestFocus(userPassword);
//            return false;
//        } else {
//            input_layout_password.setErrorEnabled(false);
//        }
//
//        return true;
//    }
//
//    private boolean validateMandal() {
//        if (actv.getText().toString().trim().isEmpty()) {
//            input_layout_Mandal.setError("Please Fill");
//            requestFocus(actv);
//            return false;
//        } else {
//            input_layout_Mandal.setErrorEnabled(false);
//        }
//
//        return true;
//    }
//
//    private boolean validatePhone() {
//        if (userPhone.getText().toString().trim().isEmpty()) {
//            input_layout_Phone.setError("Please Fill");
//            requestFocus(userPhone);
//            return false;
//        } else {
//            input_layout_Phone.setErrorEnabled(false);
//        }
//
//        return true;
//    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


//    private class MyTextWatcher implements TextWatcher {
//
//        private View view;
//
//        private MyTextWatcher(View view) {
//            this.view = view;
//        }
//
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        }
//
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        }
//
//        public void afterTextChanged(Editable editable) {
//            switch (view.getId()) {
//                case R.id.userId:
//                    validateID();
//                    break;
//                case R.id.userName:
//                    validateName();
//                    break;
//                case R.id.userPassword:
//                    validatePassword();
//                    break;
//               /* case R.id.userMandal:
//                    validateMandal();
//                    break;*/
//                case R.id.userPhone:
//                    validatePhone();
//                    break;
//            }
//        }
//    }

}
