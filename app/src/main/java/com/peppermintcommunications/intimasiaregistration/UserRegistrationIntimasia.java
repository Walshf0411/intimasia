package com.peppermintcommunications.intimasiaregistration;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.view.LayoutInflater;

import java.util.HashMap;


public class UserRegistrationIntimasia extends AppCompatActivity {
    private static final String USER_CATEGORIES[] = {"Select Business / Profession Type", "Retailer", "Distributor", "Agent", "Exporter", "Importer", "Online Selling", "Trading Company", "Franchisee Investor", "Brand", "Manufacturer", "Supplier", "OEM/ODM Enterprise", "Media / Press", "Assosiation / Trading Body", "Intimate Apparel Proffessional", "Fashion Institute/ Student"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration_intimasia);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Visitor Registration");
        }
        final HashMap<String, String> formValues = (HashMap<String, String>) getIntent().getSerializableExtra("form_values");

        Spinner userCategorySpinner = (Spinner)findViewById(R.id.user_category_spinner);
        ArrayAdapter<String> userCategorySpinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, USER_CATEGORIES);
        userCategorySpinner.setAdapter(userCategorySpinnerAdapter);

        userCategorySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                        // start new activity.
                        String user_category = parent.getItemAtPosition(position).toString();
                        if(user_category.equals("Select Business / Profession Type"))
                           return; // if the user category is the default category, then don't start another activity

                        Intent intent = new Intent(UserRegistrationIntimasia.this, UserRegistrationIntimasia2.class);
                        intent.putExtra("user_type", user_category);
                        intent.putExtra("form_values", formValues);
                        startActivity(intent);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );
    }
}
