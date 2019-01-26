package com.peppermintcommunications.intimasiaregistration;

import android.content.Intent;
import android.graphics.PorterDuff;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class FirstStepToRegisteration extends AppCompatActivity {

    EditText emailOrPhone;
    Button button;
    TextInputLayout emailOrPhoneContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_step_to_registeration);
        emailOrPhone = (EditText) findViewById(R.id.phone_number_email);
        button = (Button) findViewById(R.id.register_first_step);
        emailOrPhoneContainer = (TextInputLayout) findViewById(R.id.phone_number_email_layout);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!isFormInvalid()){
                            checkIfUserExists();
                        }
                    }
                }
        );
    }

    private void checkIfUserExists() {
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.intimasia_base_domain))
                .appendPath("unique_email_or_phone.php")
                .appendQueryParameter("value", emailOrPhone.getText().toString())
                .build().toString();

        CustomRequest checkUserExists = new CustomRequest(
                Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 200 Ok
                        try {
                            Log.i("asdfghjkl", response.toString());
                            String success_message = response.getString("message");
                            Toast.makeText(FirstStepToRegisteration.this, success_message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(FirstStepToRegisteration.this, RegisterIntimasia.class);
                            intent.putExtra("value", emailOrPhone.getText().toString());
                            startActivity(intent);
                        }catch (JSONException e){
                            Log.i("asdfghjkl", "JSONException in successful response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            String errorResponse = new String(error.networkResponse.data);
                            Log.i("asdfghjkl", errorResponse);
                            JSONObject errorInJson = new JSONObject(errorResponse);
                            String error_message = errorInJson.getString("message");
                            emailOrPhone.getBackground().clearColorFilter();
                            emailOrPhoneContainer.setError(error_message);
                        }catch(NullPointerException e){
                            Log.i("asdfghjkl", "Null pointer exception in networkResponse.data");
                        }catch(JSONException e){
                            Log.i("asdfghjkl", "JSON exception in parsing network error");
                        }
                    }
                }
        );
        Volley.newRequestQueue(this).add(checkUserExists);
    }

    private boolean isFormInvalid() {
        boolean error = false;

        if(TextUtils.isEmpty(emailOrPhone.getText())){
            error = true;
            emailOrPhoneContainer.setError("This field is required");
        }else{
            if(!TextUtils.isDigitsOnly(emailOrPhone.getText().toString())){
                // if the field has an email address in it.
                if(!Patterns.EMAIL_ADDRESS.matcher(emailOrPhone.getText().toString()).matches()){
                    error = true;
                    emailOrPhone.getBackground().clearColorFilter();
                    emailOrPhoneContainer.setError("Please enter a valid email address");
                }else{
                    emailOrPhoneContainer.setError(null);
                }
            }else {
                // if the field has a phone number in it
                if (emailOrPhone.getText().length() != 10) {
                    error = true;
                    emailOrPhone.getBackground().clearColorFilter();
                    emailOrPhoneContainer.setError("10 digit phone number required.");
                } else {
                    emailOrPhoneContainer.setError(null);
                }
            }
        }

        return error;
    }
}
