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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserRegistrationIntimasia2 extends AppCompatActivity {

    private static final String[] PRODUCT_CATEGORIES = {
            "Intimate Wear Clothing", "Outwear Clothing", "Fashion Accessories", "Footwear"
            , "Mobile/Electronics", "Pharmacy", "Others"
    };
    private static final String USER_CATEGORIES[] = {"Select Business / Profession Type",
            "Retailer", "Distributor", "Agent", "Exporter", "Importer", "Online Selling",
            "Trading Company", "Franchisee Investor", "Brand", "Manufacturer", "Supplier",
            "OEM/ODM Enterprise", "Media / Press", "Assosiation / Trading Body", "Intimate Apparel Proffessional",
            "Fashion Institute/ Student"
    };
    private static final String PUBLICATION_FOCUS[] = {
            "Digital Media", "Magazine", "Newspaper", "Others"
    };

    private static final String STATE_LIST[] = {
            "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands", "Chandigarh","Dadra and Nagar Haveli","Daman and Diu","Lakshadweep","National Capital Territory of Delhi", "Puducherry"
    };
    // component objects.
    EditText name;
    EditText email;
    EditText phoneNumber;
    EditText companyName;
    EditText designation;
    EditText website;
    Switch dontHaveAWebsite;
    EditText address;
    EditText street;
    EditText city;
    Spinner stateSpinner;
    EditText zipcode;
    Spinner productCategoriesSpinner;
    EditText brands;
    EditText others;
    Spinner publicationFocusSpinner;
    EditText publicationCovered;
    EditText course;
    EditText specialization;
    EditText completionYear;
    ArrayList<String> intimateWearClothing = new ArrayList<>();
    String user_category;
    CheckBox intimateWearClothingCheckbox;
    HashMap<String, String> formValues;
    Intent intent;
    TextView brandsLabel;
    LinearLayout courseLayout;
    LinearLayout specializationLayout;
    LinearLayout completionYearLayout;
    TextView compnanyNameLabel;
    TextView companyURLLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration_intimasia2);
        user_category = getIntent().getStringExtra("user_type");
        formValues = (HashMap<String, String>)getIntent().getSerializableExtra("form_values");

        initializeFormValues();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(user_category + " Registration");
        }
        // Initialization of components
        initializeComponents();

        // product categories spinner value flooding
        productCategoriesSpinner = (Spinner) findViewById(R.id.event_registration_product_categories_spinner);
        ArrayAdapter<String> productCategoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, PRODUCT_CATEGORIES);

        productCategoriesSpinner.setAdapter(productCategoriesAdapter);

        // state spinner value flooding.
        stateSpinner = (Spinner) findViewById(R.id.event_registration_state_select_spinner);
        ArrayAdapter<String> stateSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, STATE_LIST);

        stateSpinner.setAdapter(stateSpinnerAdapter);
        stateSpinner.setSelection(stateSpinnerAdapter.getPosition(formValues.get("state")));
        city.setText(formValues.get("city"));

        // Hiding the View that should not be seen for all the product categories
        final LinearLayout intimateWearCheckBox = findViewById(R.id.intimate_wear_checkbox);
        intimateWearCheckBox.setVisibility(View.GONE);

        final LinearLayout brands = (LinearLayout)findViewById(R.id.brands);
        brands.setVisibility(View.VISIBLE);

        // to be visible only when the others is selected in the product categories.
        final LinearLayout othersField = (LinearLayout) findViewById(R.id.others_field_layout);
        othersField.setVisibility(View.GONE);

        courseLayout = (LinearLayout) findViewById(R.id.course_field_layout);
        specializationLayout = (LinearLayout) findViewById(R.id.specialization_field_layout);
        completionYearLayout = (LinearLayout) findViewById(R.id.year_completion_field_layout);

        courseLayout.setVisibility(View.GONE);
        specializationLayout.setVisibility(View.GONE);
        completionYearLayout.setVisibility(View.GONE);

        course = (EditText) findViewById(R.id.event_registration_course);
        specialization = (EditText) findViewById(R.id.event_registration_specialization);
        completionYear = (EditText) findViewById(R.id.event_registration_year_completion);

        // changing the label according to the user
        TextView productsCategoryLabel = (TextView) findViewById(R.id.products_category_label);
        brandsLabel = (TextView) findViewById(R.id.brands_label);
        final TextView intimateCheckBoxLabel = (TextView) findViewById(R.id.intimate_checkbox_label);

        // Media/Press publication focus Spinner.
        LinearLayout publicationFocus = (LinearLayout)findViewById(R.id.publication_focus);
        publicationFocusSpinner = (Spinner) findViewById(R.id.event_registration_publication_focus_spinner);
        ArrayAdapter<String> publicationFoucsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, PUBLICATION_FOCUS);
        publicationFocusSpinner.setAdapter(publicationFoucsAdapter);
        publicationFocus.setVisibility(View.GONE);

        if(user_category.equals(USER_CATEGORIES[1])){ // Retailer
            productsCategoryLabel.setText("Products Category");
            brandsLabel.setText("Brands Retailed");
        }else if(user_category.equals(USER_CATEGORIES[2])){ // Distributor
            productsCategoryLabel.setText("Distribution Category");
            brandsLabel.setText("Brands distributed");
        }else if(user_category.equals(USER_CATEGORIES[3])){ // Agent
            productsCategoryLabel.setText("Products Category");
            brandsLabel.setText("Brands Serviced");
        }else if(user_category.equals(USER_CATEGORIES[4])){ // Exporter
            productsCategoryLabel.setText("Products Exported");
            brandsLabel.setText("Brands Exported");
        }else if(user_category.equals(USER_CATEGORIES[5])){ //Importer
            productsCategoryLabel.setText("Products Imported");
            brandsLabel.setText("Brands Imported");
        }else if(user_category.equals(USER_CATEGORIES[6])){ //Online Seller
            productsCategoryLabel.setText("Products Category");
            brandsLabel.setText("Brands Available");
        }else if(user_category.equals(USER_CATEGORIES[7])){ // Trading Company
            productsCategoryLabel.setText("Products Category");
            brandsLabel.setText("Brands Stocked");
        }else if(user_category.equals(USER_CATEGORIES[8])){ // Franchisee Investor
            // dont show the products category here, instead show
            // all the options of intimate Wear.
            productCategoriesSpinner.setVisibility(View.GONE);
            productsCategoryLabel.setVisibility(View.GONE);
            intimateCheckBoxLabel.setText("Categories of Intimate wear");
            intimateWearCheckBox.setVisibility(View.VISIBLE); // sets the entire checkbox thing visible
            brandsLabel.setText("Brands to Display");
        }else if(user_category.equals(USER_CATEGORIES[9])){ // Brand Category
            productsCategoryLabel.setText("Brand Category"); //only products category seen
            brands.setVisibility(View.GONE); // brands fild not to be shown
        }else if(user_category.equals(USER_CATEGORIES[10])){ // manufacturer
            productsCategoryLabel.setText("Products Manufactured");
            brandsLabel.setText("Manufactured Brands");
        }else if(user_category.equals(USER_CATEGORIES[11])){ // Supplier
            productsCategoryLabel.setText("Products supplied");
            brandsLabel.setText("Brands Supplied");
        }else if(user_category.equals(USER_CATEGORIES[12])){ // OEM/ODM Enterprise
            productsCategoryLabel.setText("Products Category");
            brands.setVisibility(View.GONE);
        }else if(user_category.equals(USER_CATEGORIES[13])){ // Media/ Press
            productsCategoryLabel.setText("Products Category"); // one more field to be built here
            brandsLabel.setText("Brands Covered");
            publicationFocus.setVisibility(View.VISIBLE);
        }else if(user_category.equals(USER_CATEGORIES[14])){ //Association
            productsCategoryLabel.setText("Intrest Area of Association");
            brands.setVisibility(View.GONE);
        }else if(user_category.equals(USER_CATEGORIES[15])){ // Intimate Apparel professional
            productCategoriesSpinner.setVisibility(View.GONE);
            productsCategoryLabel.setVisibility(View.GONE);
            intimateCheckBoxLabel.setText("Categories of Intimate wear");
            intimateWearCheckBox.setVisibility(View.VISIBLE); // sets the entire checkbox thing visible
            brandsLabel.setText("Brands to Explore:");
        }else if (user_category.equals(USER_CATEGORIES[16])) { // Fashion Institute/Student
            companyURLLabel.setText("*Institute URL:");
            compnanyNameLabel.setText("*Name of the institute:");
            productCategoriesSpinner.setVisibility(View.GONE);
            productsCategoryLabel.setVisibility(View.GONE);
            intimateWearCheckBox.setVisibility(View.GONE);
            brands.setVisibility(View.GONE);
            brandsLabel.setVisibility(View.GONE);
            courseLayout.setVisibility(View.VISIBLE);
            specializationLayout.setVisibility(View.VISIBLE);
            completionYearLayout.setVisibility(View.VISIBLE);
        }
        productCategoriesSpinner.setSelection(2);
        productCategoriesSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                        String valueSelected = parent.getItemAtPosition(position).toString();
                        if(valueSelected.equals("Intimate Wear Clothing") || user_category.equals(USER_CATEGORIES[8])
                                || user_category.equals(USER_CATEGORIES[15])){
                            intimateWearCheckBox.setVisibility(View.VISIBLE);
                            intimateWearClothing.add(getString(R.string.leggings_label));
                            intimateWearClothingCheckbox.setChecked(true);
                        }else{
                            intimateWearClothingCheckbox.setChecked(false);
                            intimateWearCheckBox.setVisibility(View.GONE);
                            intimateWearClothing.remove(getString(R.string.leggings_label));
                        }
                        if(valueSelected.equals("Others")){
                            othersField.setVisibility(View.VISIBLE);
                        }else{
                            othersField.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );
        final EditText urlField = (EditText) findViewById(R.id.event_registration_website);
        // if switch enabled then url field disabled
        final Switch switchWidget = (Switch) findViewById(R.id.event_registration_no_url);
        switchWidget.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(switchWidget.isChecked()){
                            urlField.setEnabled(false);
                            urlField.setFocusable(false);
                        }else{
                            urlField.setEnabled(true);
                            urlField.setFocusable(true);
                        }
                    }
                }
        );

        // the clcking stuff for the registration button
        Button registerButton = (Button) findViewById(R.id.event_registration_button);
        registerButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        if(isFormInvalid()){
                            Toast.makeText(UserRegistrationIntimasia2.this, "Please correct the errors.", Toast.LENGTH_SHORT).show();
                        }else{
                            registerUser();
                        }
                    }
                }
        );
        if(formValues != null){
            name.setText(formValues.get("name"));
            email.setText(formValues.get("email"));
            phoneNumber.setText(formValues.get("phone_number").substring(4));
        }

    }

    private void initializeFormValues() {
        // these values are set as empty, so that when these views are not visible
        // we get blank values in the database.
        formValues.put("others", " ");
        formValues.put("brand", " ");
        formValues.put("publication", " "); // publication focus field
        formValues.put("pubs", " "); // publication covered field
        formValues.put("intimate_category", "");
        formValues.put("course", " "); // course field
        formValues.put("specialization", " "); // specialization field
        formValues.put("year_completion", " "); // year of completion field
        formValues.put("brand", "Brand");// Brand is not compulsory
        formValues.put("street", " "); // street is not compulsory
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );
        String guid = sharedPreferences.getString("guid", null);

        formValues.put("guid", guid);
        formValues.put("user_category", getIndexOfCategory());
    }

    private String getIndexOfCategory() {
        Integer index = 0;
        String searchValue = user_category;
        for (int i=0; i < USER_CATEGORIES.length; i++){
            if(USER_CATEGORIES[i].equals(searchValue)){
                index = i;
                break;
            }
        }

        return index.toString();
    }

    private void initializeComponents() {
        // this method sets up all the components for the form validation
        name = (EditText) findViewById(R.id.event_registration_name);
        email = (EditText) findViewById(R.id.event_registration_email);
        phoneNumber = (EditText) findViewById(R.id.event_registration_phone_no);
        companyName = (EditText) findViewById(R.id.event_registration_company_name);
        designation = (EditText) findViewById(R.id.event_registration_designation);
        website = (EditText) findViewById(R.id.event_registration_website);
        dontHaveAWebsite = (Switch) findViewById(R.id.event_registration_no_url);
        address = (EditText) findViewById(R.id.event_registration_address);
        street = (EditText) findViewById(R.id.event_registration_street);
        city = (EditText) findViewById(R.id.event_registration_city);
        zipcode = (EditText) findViewById(R.id.event_registration_zipcode);
        brands = (EditText) findViewById(R.id.events_registration_brands);
        others = (EditText) findViewById(R.id.event_registration_others);
        publicationCovered = (EditText) findViewById(R.id.event_registration_publication_covered);
        intimateWearClothingCheckbox = (CheckBox) findViewById(R.id.intimate_wear_checkbox_item11);
        companyURLLabel = (TextView) findViewById(R.id.company_url_label);
        compnanyNameLabel = (TextView) findViewById(R.id.company_name_label);
    }

    private void registerUser() {
        // get the data from the form and set it up for the passing to the next page.
        intent = new Intent(UserRegistrationIntimasia2.this, FeedbackForm.class);

        // ================compulsory fields begin here=============
        // not needed fields ...
        formValues.put("name", name.getText().toString());
        formValues.put("email", email.getText().toString());
        formValues.put("phone_number", phoneNumber.getText().toString());

        // common fields
        formValues.put("company_name", companyName.getText().toString());
        formValues.put("designation", designation.getText().toString());
        String websiteName = dontHaveAWebsite.isChecked()? " ": website.getText().toString();
        formValues.put("website", websiteName);
        formValues.put("address", address.getText().toString());
        formValues.put("street", String.valueOf(street.getText()));

        // city, state not needed as well
        formValues.put("city", city.getText().toString());
        formValues.put("state", stateSpinner.getSelectedItem().toString());
        formValues.put("pincode", zipcode.getText().toString());

        formValues.put("product_category", productCategoriesSpinner.getSelectedItem().toString());
        // ===========compulsory fields in the form end here=========

        // dependent fields begin here.
        if(!user_category.equals(USER_CATEGORIES[9]) && !user_category.equals(USER_CATEGORIES[12])
                && !user_category.equals(USER_CATEGORIES[14])){
            formValues.put("brand", String.valueOf(brands.getText()));
        }
        if(productCategoriesSpinner.getSelectedItem().toString().equals(PRODUCT_CATEGORIES[6])){
            formValues.put("others", others.getText().toString());
        }
        if(user_category.equals(USER_CATEGORIES[13])){
            formValues.put("publication", publicationFocusSpinner.getSelectedItem().toString());
            formValues.put("pubs", publicationCovered.getText().toString());
        }
        if(user_category.equals(USER_CATEGORIES[8]) || user_category.equals(USER_CATEGORIES[15])
                || productCategoriesSpinner.getSelectedItem().toString().equals(PRODUCT_CATEGORIES[0])){
            //  put the entire Array list of intimate wear clothing in the intent
            String intimateWearClothingOptions = "";
            for(String intimateWearOption: intimateWearClothing){
                intimateWearClothingOptions += intimateWearOption + ", ";
            }
            formValues.put("intimate_category", intimateWearClothingOptions.substring(0, intimateWearClothingOptions.length()-2  ));
        }
        if(user_category.equals(USER_CATEGORIES[8]) || user_category.equals(USER_CATEGORIES[15])){
            // user categories, franchisee investor and intimate apparent professiona
            // have only intimate categories enabled hence we set the product cateory to intimate wear.
            formValues.put("product_category", PRODUCT_CATEGORIES[0]);
        }

        if (user_category.equals(USER_CATEGORIES[16])){
            formValues.put("course", course.getText().toString()); // course field
            formValues.put("specialization", specialization.getText().toString()); // specialization field
            formValues.put("year_completion", completionYear.getText().toString()); // year of completion field
        }
        intent.putExtra("form_values", formValues);
        // dependent fields end here.
        SharedPreferences.Editor editor = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
        ).edit();

        editor.putString("intimasia_registered_name", name.getText().toString());
        editor.putString("intimasia_registered_store", companyName.getText().toString());
        editor.putString("intimasia_registered_city", city.getText().toString());
        editor.putString("intimasia_registered_state", stateSpinner.getSelectedItem().toString());
        editor.putBoolean("logged_in_to_event", true);
        editor.apply();
        makeRequest();
    }

    private void makeRequest() {
        for(HashMap.Entry<String, String> values: formValues.entrySet()){
            Log.i("asdfghjkl", values.getKey()+"-"+values.getValue());
        }

        // This method will send a request to the server and then take appropiate actions.
        String url = new Uri.Builder()
                .encodedAuthority(getString(R.string.intimasia_base_domain))
                .scheme("http")
                .appendPath("app_submit_visitor_registrationnew2.php")
                .build().toString();

        CustomRequest insertDetails = new CustomRequest(
                Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        startActivity(intent);
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
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
                        }
                        try{
                            Log.i("asdfghjkl", " status code"+error.networkResponse.statusCode);
                            JSONObject errorResponse = new JSONObject(new String(error.networkResponse.data));
                            Log.i("asdfghjkl", errorResponse.getString("error"));
                        }catch (JSONException e) {
                            Log.i("asdfghjkl", "JSONException in parsing network Response.");
                        }catch (NullPointerException e){
                            Log.i("asdfghjkl", "NullPointerException in parsing network data");
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                return formValues;
            }
        };
        Volley.newRequestQueue(this).add(insertDetails);
    }

    private boolean isFormInvalid() {
        // basic form validation to be done here.
        boolean error = false;

        // Check if name is empty
        if(TextUtils.isEmpty(name.getText())){
            error = true;
            name.setError("This field is required");
        } else{
            name.setError(null);
        }

        // check if email is empty
        if(TextUtils.isEmpty(email.getText())){
            error = true;
            email.setError("This field is required");
        } else{
            // check if email contains @ and . and @ comes before .
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

        // Check if company name is empty
        if(TextUtils.isEmpty(companyName.getText())){
            error = true;
            companyName.setError("This field is required");
        }else{
            companyName.setError(null);
        }

        // Check if designation is empty
        if(TextUtils.isEmpty(designation.getText())){
            error = true;
            designation.setError("This field is required");
        }else{
            designation.setError(null);
        }

        // if the dont have a website is not checked, it means that the user's company
        // has a website and hence we check whether the website field is empty.
        if(!dontHaveAWebsite.isChecked()){
            if(TextUtils.isEmpty(website.getText())){
                error = true;
                website.setError("This field is required");
            }else{
                website.setError(null);
            }

        }else{// the user does not have a website so clear all the errors of website.
            website.setError(null);
        }

        // check if address is empty.
        if(TextUtils.isEmpty(address.getText())){
            error = true;
            address.setError("This field is required");
        }else{
            address.setError(null);
        }

        // check if city is empty
        if(TextUtils.isEmpty(city.getText())){
            error = true;
            city.setError("This field is required");
        }else{
            city.setError(null);
        }

        // check if zipcode is empty
        if(TextUtils.isEmpty(zipcode.getText())){
            error = true;
            zipcode.setError("This field is required");
        } else{
            if(zipcode.getText().length() != 6){
                error = true;
                zipcode.setError("Zipcode has to be 6 digits long");
            }else{
                zipcode.setError(null);
            }
        }
        // find whether the other fields that are not common to all have errors or no.

        return error || validateIndividualCategory();
    }
    private boolean validateIndividualCategory() {
        // This method checks for the errors in the fields that are dependent to the indivual user categories
        boolean error = false;

        // product category validation
        // user category having product category
        // Retailer, Distributor, Agent, Exporter, Importer, online seller,
        // trading company, manufacturer, supplier, brand, OEM/ODM Enterprise,
        // products category does not need validation as it is a Spinner and a value would be chosen by default

        // brands validation
        // user category having brands field
        //  all except brand, OEM/ ODM Enterprise and association/trade body
        if(!user_category.equals(USER_CATEGORIES[9]) && !user_category.equals(USER_CATEGORIES[12])
                && !user_category.equals(USER_CATEGORIES[14]) && !user_category.equals(USER_CATEGORIES[16])){

        }
        // publication validation
        // user category Media/ Press
        if(user_category.equals(USER_CATEGORIES[13])){
            if(TextUtils.isEmpty(publicationCovered.getText())){
                error = true;
                publicationCovered.setError("This field is required");
            }else{
                publicationCovered.setError(null);
            }
        }
        // others field validation
        // to be done when the product category has been set to others.
        if(productCategoriesSpinner.getSelectedItem().toString().equals(PRODUCT_CATEGORIES[6])){
            // This means that the others item is selected in the product categories spinner
            if(TextUtils.isEmpty(others.getText())){
                error = true;
                others.setError("This field is required");
            }else{
                others.setError(null);
            }
        }
        if (user_category.equals(USER_CATEGORIES[16])) { // Fashion Institute/ Student
            if(TextUtils.isEmpty(course.getText())){
                error = true;
                course.setError("This field is required");
            }else{
                course.setError(null);
            }

            if(TextUtils.isEmpty(specialization.getText())){
                error = true;
                specialization.setError("This field is required");
            }else{
                specialization.setError(null);
            }

            if(TextUtils.isEmpty(completionYear.getText())){
                error = true;
                completionYear.setError("This field is required");
            }else{
                completionYear.setError(null);
            }
        }


        return error;
    }

    public void onCheckboxClicked(View view){
        boolean checked = ((CheckBox)view).isChecked();

        switch (view.getId()){
            case R.id.intimate_wear_checkbox_item1: // Lingere
                if(checked){
                    intimateWearClothing.add(getString(R.string.lingere_label));
                }else{
                    intimateWearClothing.remove(getString(R.string.lingere_label));
                }
                break;
            case R.id.intimate_wear_checkbox_item2: // Sleepwear
                if(checked){
                    intimateWearClothing.add(getString(R.string.sleepwear_label));
                }else{
                    intimateWearClothing.remove(getString(R.string.sleepwear_label));
                }
                break;
            case R.id.intimate_wear_checkbox_item3: // Socks and stockings
                if(checked){
                    intimateWearClothing.add(getString(R.string.socks_stockings_label));
                }else{
                    intimateWearClothing.remove(getString(R.string.socks_stockings_label));
                }
                break;
            case R.id.intimate_wear_checkbox_item4: // Undergarments
                if(checked){
                    intimateWearClothing.add(getString(R.string.undergarments_label));
                }else{
                    intimateWearClothing.remove(getString(R.string.undergarments_label));
                }
                break;
            case R.id.intimate_wear_checkbox_item5: // Loungewear
                if(checked){
                    intimateWearClothing.add(getString(R.string.loungewear_label));
                }else{
                    intimateWearClothing.remove(getString(R.string.loungewear_label));
                }
                break;
            case R.id.intimate_wear_checkbox_item6: //  Maternity wear
                if(checked){
                    intimateWearClothing.add(getString(R.string.maternity_wear_label));
                }else{
                    intimateWearClothing.remove(getString(R.string.maternity_wear_label));
                }
                break;
            case R.id.intimate_wear_checkbox_item7: // Swimwear
                if(checked){
                    intimateWearClothing.add(getString(R.string.swimwear_label));
                }else{
                    intimateWearClothing.remove(getString(R.string.swimwear_label));
                }
                break;
            case R.id.intimate_wear_checkbox_item8: //Active Wear
                if(checked){
                    intimateWearClothing.add(getString(R.string.active_wear_label));
                }else{
                    intimateWearClothing.remove(getString(R.string.active_wear_label));
                }
                break;
            case R.id.intimate_wear_checkbox_item9: // Thermals
                if(checked){
                    intimateWearClothing.add(getString(R.string.thermals_label));
                }else{
                    intimateWearClothing.remove(getString(R.string.thermals_label));
                }
                break;
            case R.id.intimate_wear_checkbox_item10: // Shapwear
                if(checked){
                    intimateWearClothing.add(getString(R.string.shape_wear_label));
                }else{
                    intimateWearClothing.remove(getString(R.string.shape_wear_label));
                }
                break;
            case R.id.intimate_wear_checkbox_item11: //leggings
                if(checked){
                    intimateWearClothing.add(getString(R.string.leggings_label));
                }else{
                    intimateWearClothing.remove(getString(R.string.leggings_label));
                }
                break;
        }
    }
}
