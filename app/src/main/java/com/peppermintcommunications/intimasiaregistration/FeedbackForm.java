package com.peppermintcommunications.intimasiaregistration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedbackForm extends AppCompatActivity {

    String [] QUESTION_INTIMASIA_OPTIONS = {
            "An Invitation received from Organizer",
            "A telephone call from Organizers",
            "An invitation received from Exhibitors",
            "An advert in magazing/newspaper/emailer"
    };

    LinearLayout subscribingDetails;
    LinearLayout suggestForm;
    Spinner questionIntimasiaField; // the first question on the feedback form.
    ArrayList<String> whatBringsYouToIntimasia = new ArrayList<>(); // checkboxes selection
    RadioGroup firstTimeToIntimasia;
    RadioGroup subscribeInnerSecrets;
    RadioGroup suggestIntimasia;

    RadioButton firstTimeToIntimasiaBtn;
    RadioButton subscribeInnerSecretsBtn;
    RadioButton suggestIntimasiaBtn;
    boolean termsAccepted;
    CheckBox termsAndCondition;

    EditText suggestName;
    EditText suggestEmail;
    EditText suggestPhone;

    String firstTimeToIntimasiaAnswer = "no";
    String subscribeInnerSecretsAnswer = "no";
    String suggestIntimasiaAnswer = "no";

    HashMap<String, String> formValues;
    ArrayList<String> intimateClothingChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_form);
        final Intent intent = getIntent();
        formValues = new HashMap<>();

        intimateClothingChoices = (ArrayList<String>) intent.getSerializableExtra("intimate_wear_clothing_options_selected");

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Feedback Form");
        }
        subscribingDetails = (LinearLayout) findViewById(R.id.subscribing_details);
        subscribingDetails.setVisibility(View.GONE);

        suggestForm = (LinearLayout)findViewById(R.id.suggest_form);
        suggestForm.setVisibility(View.GONE);

        Button feedbackFormButton = (Button)findViewById(R.id.feedback_form_submit);
        feedbackFormButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if(isFormInvalid()){ // returns a boolean on the basis of whether there is error
                            Toast.makeText(FeedbackForm.this, "Please correct the errors", Toast.LENGTH_SHORT).show();
                        }else{
                            registerUser();
                            sendRequest();
                            if(intimateClothingChoices != null){
                                Log.i("asdfghjkl", "Intimate wear clothing choices");
                                for (String s: intimateClothingChoices){
                                    Log.i("asdfghjkl", s);
                                }
                                Log.i("asdfghjkl", "\n");
                            }
                            Log.i("asdfghjkl", "What brings yout to intimasia?");
                            for(String s: whatBringsYouToIntimasia){
                                Log.i("asdfghjkl", s);
                            }
                            Log.i("asdfghjkl", "\n");
                            for(String key: formValues.keySet()){
                                Log.i("asdfghjkl", key+" - "+formValues.get(key));
                            }
                            // TODO:set the values to the shared preferences

                            startActivity(new Intent(FeedbackForm.this, RegistrationSuccessfulActivity.class));
                        }
                    }
                }
        );
        // initalizing response to the questions radio boxes.
        ArrayAdapter arrayAdapter = new ArrayAdapter(
                this, android.R.layout.simple_spinner_item, QUESTION_INTIMASIA_OPTIONS);
        questionIntimasiaField = (Spinner)findViewById(R.id.question_intimasia_field);
        questionIntimasiaField.setAdapter(arrayAdapter);
        firstTimeToIntimasia = (RadioGroup)findViewById(R.id.first_time_to_intimasia_radio);

        subscribeInnerSecrets = (RadioGroup)findViewById(R.id.subscribe_inner_secret_radio);
        suggestIntimasia = (RadioGroup)findViewById(R.id.suggest_intimasia_radio);
        termsAndCondition = (CheckBox)findViewById(R.id.terms_and_condition_checkbox);

        // last radio buttons of all the radio groups, used to set error
        firstTimeToIntimasiaBtn = (RadioButton) findViewById(R.id.first_time_question_no);
        subscribeInnerSecretsBtn = (RadioButton) findViewById(R.id.subscribe_question_no);
        suggestIntimasiaBtn = (RadioButton) findViewById(R.id.suggest_no);

        // suggest form fields
        suggestName = (EditText)findViewById(R.id.suggest_form_name);
        suggestEmail = (EditText)findViewById(R.id.suggest_form_email);
        suggestPhone = (EditText)findViewById(R.id.suggest_form_phone_no);
    }

    private void registerUser() {
        formValues.put("user_type", " ");
        // The request stuff goes in here.
        // How did you get to know about intimasia?
        formValues.put("intimasia", questionIntimasiaField.getSelectedItem().toString());
        // Is this your first time to Intimasia?
        formValues.put("first_time", firstTimeToIntimasiaAnswer);
        // Would you like to subscribe to Inner secrets magazine?
        formValues.put("subscribe", subscribeInnerSecretsAnswer);
        // Would you like to suggest initimasia to someone
        formValues.put("yesno", suggestIntimasiaAnswer);
        // if the user wants to suggest intimasia to someone, then take the details of the user
        // to suggest

        String whatBringsToIntimasiaOptions = " ";
        for(String option:whatBringsYouToIntimasia){
            whatBringsToIntimasiaOptions += option + ", ";
        }
        formValues.put("user_type", whatBringsToIntimasiaOptions);

        if(suggestIntimasia.getCheckedRadioButtonId() == R.id.suggest_yes){
            formValues.put("suggest_name", String.valueOf(suggestName.getText()));
            formValues.put("suggest_email", String.valueOf(suggestEmail.getText()));
            formValues.put("suggest_phone", String.valueOf(suggestPhone.getText()));
        }
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );
        String guid = sharedPreferences.getString("guid", null);
        if (guid != null){
            formValues.put("guid", guid);
        }
     }

    private void sendRequest() {
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.intimasia_base_domain))
                .appendPath("app_submit_visitor_registrationnew3.php")
                .build().toString();
        Log.i("asdfghjkl", url);
        CustomRequest registerUserFinal = new CustomRequest(
                Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return formValues;
            }
        };
        Volley.newRequestQueue(this).add(registerUserFinal);
    }

    private boolean isFormInvalid() {
        boolean error = false;
        if(firstTimeToIntimasia.getCheckedRadioButtonId() == -1){
            error = true;
            firstTimeToIntimasiaBtn.setError("This field is reqired");
        }else{
            firstTimeToIntimasiaBtn.setError(null);
        }
        if(subscribeInnerSecrets.getCheckedRadioButtonId() == -1){
            error = true;
            subscribeInnerSecretsBtn.setError("This field is reqired");
        }else{
            subscribeInnerSecretsBtn.setError(null);
        }
        if(suggestIntimasia.getCheckedRadioButtonId() == -1){
            error = true;
            suggestIntimasiaBtn.setError("This field is reqired");
        }else{
            suggestIntimasiaBtn.setError(null);
            if(suggestIntimasia.getCheckedRadioButtonId() == R.id.suggest_yes){
                if(TextUtils.isEmpty(suggestName.getText())){
                    error = true;
                    suggestName.setError("This field is required");
                }else{
                    suggestName.setError(null);
                }
                if(TextUtils.isEmpty(suggestEmail.getText())){
                    error = true;
                    suggestEmail.setError("This field is required");
                }else{
                    if(!Patterns.EMAIL_ADDRESS.matcher(suggestEmail.getText()).matches()){
                        error = true;
                        suggestEmail.setError("Kindly enter a valid email.");
                    }else{
                        suggestEmail.setError(null);
                    }
                }
                if(TextUtils.isEmpty(suggestPhone.getText())){
                    error = true;
                    suggestPhone.setError("This field is required");
                }else {
                    if(suggestPhone.getText().length() != 10){
                        error = true;
                        suggestPhone.setError("10 digit phone number needed");
                    }else{
                        suggestPhone.setError(null);
                    }
                }
            }
        }
        if(!termsAccepted){
            error = true;
            termsAndCondition.setError("Tick this box to proceed");
        }else{
            termsAndCondition.setError(null);
        }
        return error;
    }

    public void onRadioButtonClicked0(View view) { //  Is this your first time to initimasia?
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if(firstTimeToIntimasiaBtn.getError() != null){
            firstTimeToIntimasiaBtn.setError(null);
        }
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.first_time_question_yes:
                if (checked){
                    firstTimeToIntimasiaAnswer = "yes";
                }
                    break;
            case R.id.first_time_question_no:
                if (checked){
                    firstTimeToIntimasiaAnswer = "no";
                }
                    break;
        }
    }
    public void onRadioButtonClicked1(View view) { // Would you like to subscribe to inner Secrets?
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if(subscribeInnerSecretsBtn.getError() != null){
            subscribeInnerSecretsBtn.setError(null);
        }

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.subscribe_question_yes:
                if (checked){
                    // show subscribing information
                    subscribeInnerSecretsAnswer = "yes";
                    subscribingDetails.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.subscribe_question_no:
                if (checked){
                    subscribeInnerSecretsAnswer = "no";
                    subscribingDetails.setVisibility(View.GONE);
                }
                break;
        }
    }
    public void onRadioButtonClicked2(View view) { // Would you like to suggest initimasia to someone?
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if(suggestIntimasiaBtn.getError() != null){
            suggestIntimasiaBtn.setError(null);
        }

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.suggest_yes:
                if (checked) {
                    // show suggestion form
                    suggestIntimasiaAnswer = "yes";
                    suggestForm.setVisibility(View.VISIBLE);
                }
                    break;
            case R.id.suggest_no:
                if (checked){
                    suggestIntimasiaAnswer = "no";
                    suggestForm.setVisibility(View.GONE);
                }
                    break;
        }
    }
    public void onCheckboxClicked1(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.business_checkbox:
                if (checked){
                    whatBringsYouToIntimasia.add("Business Interest");
                } else{
                    whatBringsYouToIntimasia.remove("Business Interest");
                }
                break;
            case R.id.networking_checkbox:
                if (checked){
                    whatBringsYouToIntimasia.add("Networking");
                } else{
                    whatBringsYouToIntimasia.remove("Networking");
                }
                break;
            case R.id.market_survey_checkbox:
                if (checked){
                    whatBringsYouToIntimasia.add("Market Survey");
                } else{
                    whatBringsYouToIntimasia.remove("Market Survey");
                }
                break;
            case R.id.product_knowledge_label:
                if(checked){
                    whatBringsYouToIntimasia.add("Product Knowledge");
                }else{
                    whatBringsYouToIntimasia.remove("Product Knowledge");
                }
                break;
        }
    }
    public void onCheckBoxClicked2(View view){
        boolean checked = ((CheckBox) view).isChecked(); // get the current state of the checkbox

        switch (view.getId()){
            case R.id.terms_and_condition_checkbox:
                termsAccepted = checked;
                break;
        }
    }
}
