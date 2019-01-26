package com.peppermintcommunications.intimasiaregistration;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterIntimasia extends AppCompatActivity {
    private static final String stateList[] = {"Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands", "Chandigarh","Dadra and Nagar Haveli","Daman and Diu","Lakshadweep","National Capital Territory of Delhi", "Puducherry"};

    Spinner countriesSpinner;
    Spinner stateSpinners;
    EditText name;
    EditText email;
    EditText phoneNumber;
    EditText whatsappNumber;
    EditText dob;
    EditText city;
    Calendar myCalendar;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_intimasia);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle("Visitor Registration");
        }

        countriesSpinner = (Spinner)findViewById(R.id.countries_spinner);
        stateSpinners = (Spinner)findViewById(R.id.state_spinner);

        ArrayAdapter<String> countriesSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getCountries());
        countriesSpinner.setAdapter(countriesSpinnerAdapter);
        countriesSpinner.setSelection(countriesSpinnerAdapter.getPosition("India"));

        ArrayAdapter<String> statesSpinnerAdapater = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stateList);
        stateSpinners.setAdapter(statesSpinnerAdapater);

        Button registerButton = findViewById(R.id.intimasia_registration_button);
        registerButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isFormInValid()){
                            Toast.makeText(RegisterIntimasia.this, "Please correct the errors", Toast.LENGTH_SHORT).show();
                        }else {
                            registerUser();
                        }
                    }
                }
        );
        name = (EditText) findViewById(R.id.registration_name);
        email = (EditText) findViewById(R.id.registration_email);
        phoneNumber = (EditText) findViewById(R.id.registration_phone_number);
        whatsappNumber = (EditText)findViewById(R.id.registration_whatsapp_number);
        dob = (EditText)findViewById(R.id.registration_dob);
        dob.setError("Double click to enable date picker");
        city = (EditText) findViewById(R.id.registration_city);

        // date picker for dob
        // this value is marked final as the listener method is accessing it.
        // local variables tend to leave the stack on completion of the method, but the listener method,
        // would still have to access the local variables of the method. The solution to this problem is
        // to have a copy of the local variables in the scope of the outer class. And hence a copy is maintained
        // at each class scope that uses the local variable. However having two copies of the same object would
        // also lead to a problem, that is the two variables could have the same name but would have different
        // values. Therefore since JDK 1.8 local variables are marked as final so that they cannot be changed
        // and eventually all the copies and the original instance variable would have the same value.

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                // this method will be called when a date is selected from the data picker dialog
                dob.setError(null);
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        dob.setOnFocusChangeListener(
                new View.OnFocusChangeListener(){

                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if(hasFocus){
                            DatePickerDialog dialog = new DatePickerDialog(RegisterIntimasia.this, onDateSetListener,
                            myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                            dialog.show();
                        }
                    }
                }
        );
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        name.setText(sharedPreferences.getString("name", null));
        email.setText(sharedPreferences.getString("email", null));
        phoneNumber.setText(sharedPreferences.getString("phone_number", null));
        whatsappNumber.setText(sharedPreferences.getString("phone_number", null));
        String emailOrPhone = getIntent().getStringExtra("value");
        if (emailOrPhone.contains("@")){
            email.setText(emailOrPhone);
            email.setEnabled(false);
            phoneNumber.setEnabled(true);
        }else{
            phoneNumber.setText(emailOrPhone);
            phoneNumber.setEnabled(false);
            email.setEnabled(true);
        }
    }

    private void updateLabel() {
        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob.setText(sdf.format(myCalendar.getTime()));
    }
    private void registerUser() {
        intent = new Intent(RegisterIntimasia.this, UserRegistrationIntimasia.class);
        HashMap<String, String> formValues = new HashMap<>();
        formValues.put("name", name.getText().toString());
        formValues.put("email", email.getText().toString());
        formValues.put("phone_number", "+91-" + phoneNumber.getText().toString());
        formValues.put("whatsapp_number", whatsappNumber.getText().toString());
        formValues.put("dob", dob.getText().toString().replace("/", "-"));
        formValues.put("country", countriesSpinner.getSelectedItem().toString());
        formValues.put("state", stateSpinners.getSelectedItem().toString());
        formValues.put("city", city.getText().toString());

        makeRequest(formValues);
        intent.putExtra("form_values", formValues);
    }

    private boolean isFormInValid(){
        boolean error = false;

        if(TextUtils.isEmpty(name.getText())){
            error = true;
            name.setError("This field is required");
        } else{
            name.setError(null);
        }

        // check if email is empty
        if(TextUtils.isEmpty(email.getText())){
            error = true;
            email.setError("This is field is required.");
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
                error = true;
                email.setError("Kindly enter a valid email.");
            }else{
                email.setError(null);
            }
        }

        // Check if phone number is empty
        if(TextUtils.isEmpty(phoneNumber.getText())){
            error = true;
            phoneNumber.setError("This field is required");
        }else{
            // check if phone number has 10 digits.
            if(phoneNumber.getText().length() != 10){
                error = true;
                phoneNumber.setError("10 digit Phone Number needed");
            }else{
                phoneNumber.setError(null);
            }
        }
        // Check if whatsapp number is empty.
        if(TextUtils.isEmpty(whatsappNumber.getText())){
            error = true;
            whatsappNumber.setError("This field is required");
        }else{
            // check if phone number has 10 digits.
            if(whatsappNumber.getText().length() != 10){
                error = true;
                whatsappNumber.setError("10 digit Phone Number needed");
            }else{
                whatsappNumber.setError(null);
            }
        }
        // check if dob is empty
        if(TextUtils.isEmpty(dob.getText())){
            error = true;
            dob.setError("This field is required");
        } else{
            if(dob.getText().length() < 10){
                dob.setError("Invalid Date format");
            }else{
                dob.setError(null);
            }
        }
        //check if city is empty
        if(TextUtils.isEmpty(city.getText())){
            error = true;
            city.setError("This field is required");
        } else{
            city.setError(null);
        }

        return error;
    }
    public ArrayList<String> getCountries(){
        ArrayList<String> countries = new ArrayList<>();
        Locale[] locales = Locale.getAvailableLocales();
        for(Locale locale: locales){
            String country = locale.getDisplayCountry();
            if(country.length()>0 && !countries.contains(country)){
                countries.add(country);
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        return countries;
    }
    private void makeRequest(final HashMap<String,String> formValues) {
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.intimasia_base_domain))
                .appendPath("app_submit_visitor_registration.php")
                .build().toString();

        CustomRequest customRequest = new CustomRequest(
                Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String guid = response.getString("guid");
                            SharedPreferences.Editor editor = getSharedPreferences(
                                    getString(R.string.preference_file_key), Context.MODE_PRIVATE
                            ).edit();
                            editor.putString("guid", guid);
                            editor.apply();
                            Log.i("asdfghjkl", guid);
                            startActivity(intent);
                        }catch (JSONException e){
                            Log.i("asdfghjkl", "JSONException in form1 response handler");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            //This indicates that the reuest has either time out or there is no connection
                            Toast.makeText(getApplicationContext(), "Server Timed out", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NoConnectionError) {
                            //Error indicating that there was an Authentication Failure while performing the request
                            Toast.makeText(getApplicationContext(), "Check your internet connection.", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
                        }
                        try{
                            Toast.makeText(getApplicationContext(), "Kindly correct the errors", Toast.LENGTH_LONG).show();
                            JSONObject errorInJson = new JSONObject(new String(error.networkResponse.data));
                            if(errorInJson.has("phone_number")){
                                phoneNumber.setError(errorInJson.getString("phone_number"));
                            }else {
                                phoneNumber.setError(null);
                            }

                            if(errorInJson.has("email")){
                                email.setError(errorInJson.getString("email"));
                            }else{
                                email.setError(null);
                            }

                        }catch(NullPointerException e){
                            Log.i("asdfghjkl", "Null pointer exception in form1 error handler");
                        }catch (JSONException e){
                            Log.i("asdfghjkl", "JSONException in form1 error handler - networkReponse.data");
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                params.putAll(formValues);
                if(params.containsKey("phone_number")){
                    params.put("mobile", params.get("phone_number"));
                    params.remove("phone_number");
                }
                if(params.containsKey("whatsapp_number")){
                    params.put("whats_app", params.get("whatsapp_number"));
                    params.remove("whatsapp_number");
                }
                return params;
            }
        };
        Volley.newRequestQueue(this).add(customRequest);
    }

}
