package com.peppermintcommunications.intimasiaregistration;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ExhibitorDetailActivity extends AppCompatActivity {

    ConstraintLayout exhibitorDetails;
    TextView exhibitorName;
    TextView exhibitorBrand;
    TextView exhibitorCity;
    TextView exhibitorStall;
    TextView exhibitorAddr;
    TextView exhibitorProducts;
    TextView exhibitorHall;
    TextView exhibitorNotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibitor_detail);
        intializeComponents();
        Intent intent = getIntent();
        if(intent != null) {
            Exhibitor exhibitor = intent.getParcelableExtra("exhibitor");
            exhibitorName.setText(exhibitor.getName());
            exhibitorBrand.setText(exhibitor.getBrands());
            exhibitorCity.setText(exhibitor.getCity());
            exhibitorStall.setText(exhibitor.getStall());
            exhibitorAddr.setText(exhibitor.getCompany_addr());
            exhibitorProducts.setText(exhibitor.getProduct());
            exhibitorHall.setText(exhibitor.getHall());
            exhibitorNotes.setText(exhibitor.getNotes());
        }

    }
    private void intializeComponents() {
        exhibitorDetails = (ConstraintLayout) findViewById(R.id.exhibitor_details);
        exhibitorName = (TextView) findViewById(R.id.exhibitor_name);
        exhibitorBrand = (TextView) findViewById(R.id.exhibitor_brands);
        exhibitorCity = (TextView) findViewById(R.id.exhibitor_city);
        exhibitorStall = (TextView) findViewById(R.id.exhibitor_stall);
        exhibitorAddr = (TextView) findViewById(R.id.exhibitor_company_address);
        exhibitorProducts = (TextView) findViewById(R.id.exhibitor_products);
        exhibitorHall = (TextView) findViewById(R.id.exhibitor_hall);
        exhibitorNotes = (TextView) findViewById(R.id.exhibitor_notes);
    }
}
