package com.peppermintcommunications.intimasiaregistration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView userName;
    TextView userEmail;
    TextView userPhoneNumber;
    View userActionsContainer;
    ImageView intimasiaDate;
    ImageView intimasiaLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        // get the intent from the previous activity for passing it to the next page.

        Intent intent = getIntent();

        final HashMap<String, String>formValues = (HashMap<String, String>) intent.getSerializableExtra("form_values");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userActionsContainer = (View) findViewById(R.id.user_actions_container);
        intimasiaDate = (ImageView) findViewById(R.id.intimasia_date);
        intimasiaLogo = (ImageView) findViewById(R.id.intimasia_logo);
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.intimasia_images_link))
                .appendPath("intimasia_date.png").build().toString();
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.intimasia_data)
                .fit()
                .into(intimasiaDate);

        String url1 = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.intimasia_images_link))
                .appendPath("intimasia_logo.png").build().toString();
        Picasso.get()
                .load(url1)
                .placeholder(R.drawable.intimasia_logo)
                .fit()
                .into(intimasiaLogo);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        Menu menu = navigationView.getMenu();

        View headerView = navigationView.getHeaderView(0);

        userName = (TextView) headerView.findViewById(R.id.user_name);
        userEmail = (TextView) headerView.findViewById(R.id.user_email);
        userPhoneNumber = (TextView) headerView.findViewById(R.id.user_phone_number);
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );
        boolean loggedInToEvent = sharedPreferences.getBoolean("logged_in_to_event", false);
        Log.i("asdfghjkl","" +loggedInToEvent);

        TextView alreadyRegistered1 = (TextView) findViewById(R.id.already_registered_intimasia);
        TextView alreadyRegistered2 = (TextView) findViewById(R.id.already_registered_intimasia_2);
        if(loggedInToEvent){
            menu.findItem(R.id.nav_scan_qr_code).setEnabled(true);
            menu.findItem(R.id.nav_badge).setEnabled(true);
            menu.findItem(R.id.nav_scan_history_hall_a).setEnabled(true);
            userActionsContainer.setVisibility(View.GONE);
            alreadyRegistered1.setVisibility(View.VISIBLE);
            alreadyRegistered2.setVisibility(View.VISIBLE);

        }else{
            menu.findItem(R.id.nav_scan_qr_code).setEnabled(false);
            menu.findItem(R.id.nav_badge).setEnabled(false);
            menu.findItem(R.id.nav_scan_history_hall_a).setEnabled(false);
            userActionsContainer.setVisibility(View.VISIBLE);
            alreadyRegistered1.setVisibility(View.GONE);
            alreadyRegistered2.setVisibility(View.GONE);
        }
        userName.setText(sharedPreferences.getString("name", "Intimasia Visitor"));
        userEmail.setText(sharedPreferences.getString("email", "visitor@intimasia.co.in"));
        userPhoneNumber.setText(sharedPreferences.getString("phone_number", "Phone Number"));

        Button registerIntimasia = (Button) findViewById(R.id.register_intimasia);
        registerIntimasia.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), RegisterAsVisitorOrExhibitor.class);
                        intent.putExtra("form_values", formValues);
                        startActivity(intent);
                    }
                }
        );

        Button loginIntimasia = (Button) findViewById(R.id.login_intimasia);
        loginIntimasia.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        Intent intent = new Intent(getApplicationContext(), LoginIntimasia.class);
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // exit the application
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_floor_plan) {
            // Handle the camera action
            // create the qr code activity
            Intent intent = new Intent(HomePage.this, FloorPlan.class);
            startActivity(intent);
        } else if (id == R.id.nav_list) {
            // Exhibitors list
            Intent intent = new Intent(HomePage.this, ExhibitorList.class);
            startActivity(intent);
        } else if (id == R.id.nav_show_agenda) {
            Intent intent = new Intent(HomePage.this, ShowAgenda.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            boolean logged_in = sharedPreferences.getBoolean("logged_in", false);

            if(logged_in){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("logged_in");
                editor.remove("name");
                editor.remove("email");
                editor.remove("phone_number");
                editor.remove("intimasia_registered_name");
                editor.remove("intimasia_registered_store");
                editor.remove("intimasia_registered_city");
                editor.remove("intimasia_registered_state");
                editor.remove("guid");
                editor.remove("logged_in_to_event");
                editor.apply();
                Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        } else if (id == R.id.nav_scan_qr_code) {
            Intent intent = new Intent(HomePage.this, QRCodeScanner.class);
            startActivity(intent);
        } else if (id == R.id.nav_badge) {
            // display the badge activity.
            Intent intent =  new Intent(HomePage.this, RegistrationSuccessfulActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_home){
            Intent intent = new Intent(HomePage.this, HomePage.class);
            startActivity(intent);
        }else if (id == R.id.nav_scan_history_hall_a){
            Intent intent = new Intent(HomePage.this, ScanHistory.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
