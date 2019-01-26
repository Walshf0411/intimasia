package com.peppermintcommunications.intimasiaregistration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextInputEditText phoneNumberEmail;
    TextInputLayout phoneNumberEmailContainer;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );
        boolean loggedIn = sharedPreferences.getBoolean("logged_in", false);
        if(loggedIn){
            startActivity(new Intent(MainActivity.this, HomePage.class));
        }
        progressDialog = new ProgressDialog(this);
        final TextView registrationBtn = findViewById(R.id.register_app_level_button);
        phoneNumberEmail = (TextInputEditText) findViewById(R.id.edit_text2);
        phoneNumberEmailContainer = (TextInputLayout) findViewById(R.id.editText2);
        registrationBtn.setOnClickListener(
            new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, AppLevelRegistration.class);
                    startActivity(intent);
                }
        });
        Button login = (Button) findViewById(R.id.login_app_level_btn);
        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!formIsInvalid()){
                            registerUser();
                        }else{
                            progressDialog.hide();
                            Snackbar.make(findViewById(R.id.login_view), "Kindly correct the errors", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }

    private boolean formIsInvalid() {
        progressDialog.setMessage("Validataing your Email/Phone Number...");
        progressDialog.show();
        boolean error = false;
        if(TextUtils.isEmpty(phoneNumberEmail.getText())){
            error = true;
            phoneNumberEmailContainer.setError("This field is required");
        }else{
            if(!TextUtils.isDigitsOnly(phoneNumberEmail.getText().toString())){
                // if the field has an email address in it.
                if(!Patterns.EMAIL_ADDRESS.matcher(phoneNumberEmail.getText().toString()).matches()){
                    error = true;
                    phoneNumberEmailContainer.setError("Please enter a valid email address");
                }else{
                    phoneNumberEmailContainer.setError(null);
                }
            }else {
                // if the field has a phone number in it
                if (phoneNumberEmail.getText().length() != 10) {
                    error = true;
                    phoneNumberEmailContainer.setError("10 digit phone number required.");
                } else {
                    phoneNumberEmailContainer.setError(null);
                }
            }
        }
        return error;
    }
    private void registerUser(){
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.intimasia_base_domain))
                .appendPath("app_based_visitor_login.php")
                .build().toString();
        CustomRequest customRequest = new CustomRequest(
                Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences.Editor editor = getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE
                        ).edit();
                        try {
                            editor.putString("name", response.getString("name"));
                            editor.putString("email", response.getString("email"));
                            editor.putString("phone_number", response.getString("phone_number"));
                            editor.putBoolean("logged_in", true);
                            editor.apply();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        progressDialog.hide();
                        Toast.makeText(MainActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomePage.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        if (error instanceof NoConnectionError){
                            Toast.makeText(MainActivity.this,
                                    "Please Check your internet connection", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(error instanceof TimeoutError) {
                            Toast.makeText(MainActivity.this, "Request Timed out.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (error instanceof NetworkError) {
                            Toast.makeText(MainActivity.this, "Network Error..", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            String error1 = new String(error.networkResponse.data);
                            JSONObject jsonObject = new JSONObject(error1);
                            phoneNumberEmailContainer.setError(jsonObject.getString("error"));
                        }catch(JSONException e){}
                        catch (NullPointerException e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("value", phoneNumberEmail.getText().toString());
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(customRequest);
    }
}
