package com.peppermintcommunications.intimasiaregistration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.security.Key;

public class RegistrationSuccessfulActivity extends AppCompatActivity {

    TextView userName;
    TextView location;
    TextView storeName;
    ImageView QRCode;
    ImageView barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_successful);

        userName = findViewById(R.id.user_name);
        location = findViewById(R.id.location);
        storeName = findViewById(R.id.store_name);

        QRCode = findViewById(R.id.qrcode);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );
        String name = sharedPreferences.getString("intimasia_registered_name", "name");
        String companyName = sharedPreferences.getString("intimasia_registered_store", "company_name");
        String city = sharedPreferences.getString("intimasia_registered_city", "city");
        String state = sharedPreferences.getString("intimasia_registered_state", "state");
        String guid = sharedPreferences.getString("guid", null);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Your Badge");
        }

        userName.setText(name);
        location.setText(city + ", " + state);
        storeName.setText(companyName);

        String url = "https://chart.googleapis.com/chart?chs=200x200&cht=qr&chl=http://intimasia.co.in?id=" + guid + "&choe=UTF-8";
        final ProgressBar progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(url).fit().into(QRCode,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegistrationSuccessfulActivity.this, "Some error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    public void onBackPressed(){
        Intent intent = new Intent(RegistrationSuccessfulActivity.this, HomePage.class);
        startActivity(intent);
    }
}
