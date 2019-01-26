package com.peppermintcommunications.intimasiaregistration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginIntimasia extends AppCompatActivity {

    EditText phoneNumberEmail;
    Button loginBtn;
    TextInputLayout phoneNumberEmailContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_intimasia);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Login to Event");
        }
        phoneNumberEmailContainer = (TextInputLayout) findViewById(R.id.login_to_event_layout);
        phoneNumberEmail = (EditText) findViewById(R.id.login_to_event);
        loginBtn = (Button) findViewById(R.id.login_to_event_btn);
        loginBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!isFormInvalid()){
                            makeRequest();
                        }else{
                            Toast.makeText(LoginIntimasia.this, "Kindly correct the errors.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void makeRequest() {
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.intimasia_base_domain))
                .appendPath("unique_email_or_phone.php")
                .appendQueryParameter("value", phoneNumberEmail.getText().toString())
                .build().toString();

        CustomRequest checkUserExists = new CustomRequest(
                Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 200 Ok
                        Toast.makeText(LoginIntimasia.this, "Kindly correct the errors.", Toast.LENGTH_SHORT).show();
                        phoneNumberEmailContainer.setError("User with phone number or email does not exist.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            Log.i("asdfghjkl", new String(error.networkResponse.data));
                            JSONObject errorResponse = new JSONObject(new String(error.networkResponse.data));
                            if(errorResponse.has("user")){
                                JSONObject user = errorResponse.getJSONObject("user");
                                Log.i("asdfghjkl", user.toString());
                                SharedPreferences.Editor editor = getSharedPreferences(
                                        getString(R.string.preference_file_key), Context.MODE_PRIVATE
                                ).edit();
                                editor.putString("intimasia_registered_name", user.getString("name"));
                                editor.putString("intimasia_registered_store", user.getString("company"));
                                editor.putString("intimasia_registered_city", user.getString("city"));
                                editor.putString("intimasia_registered_state", user.getString("state"));
                                editor.putString("guid", user.getString("guid"));
                                editor.putBoolean("logged_in_to_event", true);
                                editor.apply();
                                Toast.makeText(LoginIntimasia.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginIntimasia.this, RegistrationSuccessfulActivity.class));
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
        );
        Volley.newRequestQueue(this).add(checkUserExists);


    }

    private boolean isFormInvalid(){
        boolean error = false;
        if (TextUtils.isEmpty(phoneNumberEmail.getText())){
            error = true;
            phoneNumberEmailContainer.setError("This field is required");
        }else{
            if(TextUtils.isDigitsOnly(phoneNumberEmail.getText())){
                // if the input is a phone number
                if(phoneNumberEmail.length() != 10){
                    error = true;
                    phoneNumberEmailContainer.setError("10 digit phone number required.");
                }else{
                    phoneNumberEmailContainer.setError(null);
                }
            }else{
                // if the input is an email addrress.
                if(!Patterns.EMAIL_ADDRESS.matcher(phoneNumberEmail.getText()).matches()){
                    error = true;
                    phoneNumberEmailContainer.setError("Enter a valid email address.");
                }else{
                    phoneNumberEmailContainer.setError(null);
                }
            }
        }

        return error;
    }
}
