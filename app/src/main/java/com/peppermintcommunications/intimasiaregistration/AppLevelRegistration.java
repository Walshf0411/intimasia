package com.peppermintcommunications.intimasiaregistration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class AppLevelRegistration extends AppCompatActivity implements TextWatcher {

    EditText name;
    EditText email;
    EditText phoneNumber;
    HashMap<String, TextView> mappingOfComponentsAndNames;
    HashMap<String, String> formValues;
    String OTP;
    Button otpSubmit;
    LinearLayout otpContainer;
    EditText otpField;
    TextInputLayout otpFieldHolder;
    TextView emailLabel;
    TextView nameLabel;
    String userPhoneNumber;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_level_registration);

        name = (EditText) findViewById(R.id.app_level_registration_name);
        email = (EditText) findViewById(R.id.app_level_registration_email);
        phoneNumber = (EditText) findViewById(R.id.app_level_registration_phone_number);
        otpSubmit = (Button) findViewById(R.id.otp_submit);
        otpContainer = (LinearLayout) findViewById(R.id.otp_container);
        otpField = (EditText) findViewById(R.id.otp_field);
        otpFieldHolder = (TextInputLayout) findViewById(R.id.otp_field_holder);
        emailLabel = (TextView) findViewById(R.id.email_label);
        nameLabel = (TextView) findViewById(R.id.name_label);

        phoneNumber.addTextChangedListener(this);
        otpContainer.setVisibility(View.GONE);
        mappingOfComponentsAndNames = new HashMap<>();
        mappingOfComponentsAndNames.put("name", name);
        mappingOfComponentsAndNames.put("email", email);
        mappingOfComponentsAndNames.put("phone_number", phoneNumber);

        progressDialog = new ProgressDialog(this);
        email.addTextChangedListener(new EmailListener());
        final Button registerBtn = (Button) findViewById(R.id.app_level_registration_button);
        registerBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isFormInvalid()) {
                            progressDialog.hide();
                            Toast.makeText(AppLevelRegistration.this, "Please correct the errors.", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.hide();
                            userPhoneNumber = phoneNumber.getText().toString();
                            registerUser();
                        }
                    }
                }
        );
        otpSubmit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(verifyOTP()){
                            // otp correct.
                            progressDialog.hide();
                            registerUser();
                        }else {
                            progressDialog.hide();
                            Toast.makeText(AppLevelRegistration.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }

    private boolean verifyOTPInput(){
        boolean error = false;
        if(TextUtils.isEmpty(String.valueOf(otpField.getText()))){
            error = true;
            otpFieldHolder.setError("This field cannot be blank.");
        }else{
            if(otpField.getText().length() != 4){
                error = true;
                otpFieldHolder.setError("The OTP should be 4 characters in length");
            }else{
                otpFieldHolder.setError(null);
            }
        }
        return !error;
    }

    private boolean verifyOTP() {
        progressDialog.setMessage("Verifing entererd OTP...");
        progressDialog.show();
        if(verifyOTPInput()){
            // if there is no error in the otp field then we check whether the entered OTP is correct
            String enteredOTP = otpField.getText().toString();
            if(!enteredOTP.equals(OTP)){
                otpFieldHolder.setError("The OTP you entered is incorrect");
                return false;
            }else{
                otpFieldHolder.setError(null);
                return true;
            }
        }else{
            // the input is not proper
            return false;
        }
    }

    private void sendOTP(){
        progressDialog.setMessage("An OTP is being sent to your mobile number...");
        progressDialog.show();
        OTP = getOTP(4);
        Log.i("asdfghjkl", "generated OTP " + OTP);
        String message = "Your verification code is " + OTP;
        //message = message.replace(" ", "%20");
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.sms_service_base_domain))
                .appendQueryParameter("username", "BYTESCOOKIES")
                .appendQueryParameter("pass", "123456")
                .appendQueryParameter("senderid", "INASIA")
                .appendQueryParameter("dest_mobileno", phoneNumber.getText().toString())
                .appendQueryParameter("message", message)
                .appendQueryParameter("response", "Y").build().toString();

        Toast.makeText(AppLevelRegistration.this,
                "An OTP has been sent to your mobile number", Toast.LENGTH_SHORT).show();

        CustomRequest sendOTP = new CustomRequest(
                Request.Method.GET, url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                    }
                }
        );
        Volley.newRequestQueue(this).add(sendOTP);
    }

    private String getOTP(int length) {
        String otp = "";
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int number = secureRandom.nextInt(9); // get a secure random number between 0 and 9
            if (number == 0 && i == 0) { // to prevent the Zero to be the first number as then it will reduce the length of generated pin to three or even more if the second or third number came as zeros
                i = -1;
                continue;
            }
            otp = otp + number;
        }
        return otp;
    }

    private void registerUser() {
        Intent intent = new Intent(AppLevelRegistration.this, HomePage.class);
        /*TODO: put all these details in the shared preferences and then fill in any form the
        user has to fill in */
        formValues = new HashMap<>();
        formValues.put("name", name.getText().toString());
        formValues.put("email", email.getText().toString());
        formValues.put("phone_number", userPhoneNumber);

        intent.putExtra("form_values", formValues);
        registerOnline(formValues, intent);
    }

    private void registerOnline(final HashMap<String, String> formValues, Intent intent) {
        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .encodedAuthority(getString(R.string.intimasia_base_domain))
                .appendPath("app_based_visitor_registration.php")
                .build().toString();

        CustomRequest customRequest = new CustomRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("logged_in", true);
                        editor.putString("name", name.getText().toString());
                        editor.putString("email", email.getText().toString());
                        editor.putString("phone_number", userPhoneNumber);
                        editor.apply();
                        Toast.makeText(AppLevelRegistration.this, "Registered", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AppLevelRegistration.this, HomePage.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /* Error response format.
                         * {
                         *   "field_name1": "error_message1",
                         *   "field_name2": "error_message2",
                         *
                         *   .
                         *   .
                         *   "field_name n": "error_message n"
                         * }
                         *
                         */
                        if (error instanceof NoConnectionError){
                            Toast.makeText(AppLevelRegistration.this,
                                    "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (error instanceof NetworkError){
                            Toast.makeText(AppLevelRegistration.this, "Network Error.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (error instanceof TimeoutError){
                            Toast.makeText(AppLevelRegistration.this, "Request Timed out.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            String s = new String(error.networkResponse.data);
                            Log.i("asdfghjkl", "" + error.networkResponse.statusCode);
                            Log.i("asdfghjkl", " Error response " + s);
                            JSONObject jsonObject = new JSONObject(s);
                            iterateAndCheckForError(jsonObject);
                        } catch (JSONException e) {
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                return formValues;
            }
        };
        Volley.newRequestQueue(this).add(customRequest);
    }

    private void iterateAndCheckForError(JSONObject jsonObject) {
        for (HashMap.Entry<String, TextView> obj : mappingOfComponentsAndNames.entrySet()) {
            try {
                String error = jsonObject.getString(obj.getKey());
                Log.i("asdfghjkl", error);
                obj.getValue().setError(error);
            } catch (JSONException e) {
            }
        }
    }

    private boolean isFormInvalid() {
        progressDialog.setMessage("Registering user...");
        progressDialog.show();
        boolean error = false;
        if (TextUtils.isEmpty(name.getText())) {
            error = true;
            name.setError("This field is required");
        } else {
            name.setError(null);
        }

        // check if email is empty
        if (TextUtils.isEmpty(email.getText())) {
            error = true;
            email.setError("This field is required");
        } else {
            // check if email contains @ and . and @ comes before
            if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                error = true;
                email.setError("Please Enter a valid email address.");
            } else {
                email.setError(null);
            }
        }

        // Check if phone number is empty
        if (TextUtils.isEmpty(phoneNumber.getText())) {
            error = true;
            phoneNumber.setError("This field is required");
        } else {
            // check if phone number has 10 digits.
            if (phoneNumber.getText().length() != 10) {
                error = true;
                phoneNumber.setError("10 digit Phone Number needed");
            } else {
                phoneNumber.setError(null);
            }
        }
        return error;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(!TextUtils.isEmpty(charSequence)){
            if(TextUtils.isDigitsOnly(charSequence) && charSequence.length() == 10){
                // if the input contains only digits and the sequence is 10 in length.
                String url = new Uri.Builder()
                        .scheme("http")
                        .encodedAuthority(getString(R.string.intimasia_base_domain))
                        .appendPath("unique_phone_number_app_level_registration.php")
                        .appendQueryParameter("phone_number", charSequence.toString())
                        .build().toString();

                CustomRequest checkPhoneNumber = new CustomRequest(
                        Request.Method.POST, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try{
                                    if(response.has("message")){
                                        Drawable myIcon = getResources().getDrawable(R.drawable.ic_check_circle_black_24dp);
                                        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                                        phoneNumber.setError(response.getString("message"),
                                                myIcon);
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                try{
                                    JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data));
                                    if (errorJSON.has("message")){
                                        phoneNumber.setError(errorJSON.getString("message"));
                                    }
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                );
                Volley.newRequestQueue(this).add(checkPhoneNumber);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public class EmailListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(!TextUtils.isEmpty(charSequence)){
                if(Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()){
                    // if the input contains only digits and the sequence is 10 in length.
                    String url = new Uri.Builder()
                            .scheme("http")
                            .encodedAuthority(getString(R.string.intimasia_base_domain))
                            .appendPath("unique_email_app_level_registration.php")
                            .appendQueryParameter("email", charSequence.toString())
                            .build().toString();

                    CustomRequest checkPhoneNumber = new CustomRequest(
                            Request.Method.POST, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try{
                                        if(response.has("message")){
                                            Drawable myIcon = getResources().getDrawable(R.drawable.ic_check_circle_black_24dp);
                                            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                                            email.setError(response.getString("message"),
                                                    myIcon);
                                        }
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error){
                                    try{
                                        JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data));
                                        if (errorJSON.has("message")){
                                            email.setError(errorJSON.getString("message"));
                                        }
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    } catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                    );
                    Volley.newRequestQueue(AppLevelRegistration.this).add(checkPhoneNumber);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}