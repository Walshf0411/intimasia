package com.peppermintcommunications.intimasiaregistration;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QRCodeScanner extends AppCompatActivity {

    IntentIntegrator qrScanner;
    int CAMERA_REQUEST_CODE = 1000; // this code is used to track our request.
    // a code is required as the multiple permsissions can be requested from a single activity.
    // to identify them uniquely we use a request_code
    ConstraintLayout exhibitorDetails;
    TextView exhibitorName;
    TextView exhibitorBrand;
    TextView exhibitorCity;
    TextView exhibitorStall;
    TextView exhibitorAddr;
    TextView exhibitorProducts;
    TextInputEditText scanNotes;
    Button scanQrBtn;
    TextView exhibitorHall;
    int insertIDForNotes;
    ProgressDialog progressDialog;
    Button saveBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        intializeComponents();
        exhibitorDetails.setVisibility(View.GONE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        saveBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveNotes();
                    }
                }
        );
        scanQrBtn = (Button) findViewById(R.id.scan_qr_code);
        scanQrBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Build.VERSION.SDK_INT >= 23){
                            // Android version 23 and above require the user to grant the permissions.
                            // all other lower os do not need this hence we check the sdk version.
                            if(ContextCompat.checkSelfPermission(QRCodeScanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                                // we check if the permssion for camera is not granted.
                                // As persmission is not granted we ask for the permssions.
                                ActivityCompat.requestPermissions(QRCodeScanner.this,
                                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                            }else{
                                // request already granted.
                                qrScanner = new IntentIntegrator(QRCodeScanner.this);
                                qrScanner.initiateScan();
                            }
                        }else{
                            qrScanner = new IntentIntegrator(QRCodeScanner.this);
                            qrScanner.initiateScan();
                        }
                    }
                }
        );
    }

    private void saveNotes() {
        if (TextUtils.isEmpty(scanNotes.getText())){
            return;
        }
        progressDialog.setMessage("Saving notes...");
        progressDialog.show();
        if (insertIDForNotes != 0){
            String url = new Uri.Builder()
                    .scheme("http")
                    .encodedAuthority(getString(R.string.intimasia_base_domain))
                    .appendPath("save_scan_notes.php")
                    .build().toString();
            CustomRequest addNotes = new CustomRequest(
                    Request.Method.POST, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.hide();
                            Toast.makeText(QRCodeScanner.this, "Notes saved successfully.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.hide();
                            if (error instanceof NoConnectionError) {
                                Toast.makeText(QRCodeScanner.this, "Kindly check your internet connection", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(error instanceof NetworkError) {
                                Toast.makeText(QRCodeScanner.this, "A network error occured", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(error instanceof TimeoutError) {
                                Toast.makeText(QRCodeScanner.this, "Request Timed Out.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(QRCodeScanner.this, "Some Error occured.", Toast.LENGTH_SHORT).show();
                        }
                    }
            ){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("notes", scanNotes.getText().toString());
                    params.put("insert_id", String.valueOf(insertIDForNotes));
                    return params;
                }
            };
            Volley.newRequestQueue(this).add(addNotes);
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
        progressDialog = new ProgressDialog(this);
        saveBtn = (Button) findViewById(R.id.save_scan_history);
        scanNotes = (TextInputEditText) findViewById(R.id.scan_notes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null){
            if(result.getContents() != null){
                String exhibitorId = result.getContents();
                Log.i("asdfghjkl", "Scanned Content: " + exhibitorId);
                scanExhibitor(exhibitorId);
            }else{
                Toast.makeText(this, "No QR Code found.", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanExhibitor(final String exhibitorId) {
        progressDialog.setMessage("Fetching Exhibitor Details...");
        progressDialog.show();
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );
        String guid = sharedPreferences.getString("guid", "null");
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.intimasia_base_domain))
                .appendPath("scan_exhibitor.php")
                .appendQueryParameter("scanner_guid", guid)
                .appendQueryParameter("exhibitor_id", exhibitorId)
                .build().toString();
        CustomRequest scanExhibitor = new CustomRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();
                        exhibitorDetails.setVisibility(View.VISIBLE);
                        try {
                            if(response.has("exhibitor")) {
                                Log.i("asdfghjkl", response.toString());
                                JSONObject exhibitor = response.getJSONObject("exhibitor");
                                exhibitorName.setText(exhibitor.getString("exhibitor_name"));
                                exhibitorBrand.setText(exhibitor.getString("brands"));
                                exhibitorCity.setText(exhibitor.getString("city"));
                                exhibitorStall.setText(exhibitor.getString("exhibitor_name"));
                                exhibitorProducts.setText(exhibitor.getString("product"));
                                exhibitorAddr.setText(exhibitor.getString("company_addr"));
                                exhibitorHall.setText(exhibitor.getString("hall"));
                            }
                            if(response.has("message")){
                                Toast.makeText(QRCodeScanner.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (response.has("scan_details")) {
                                JSONObject responseObj = response.getJSONObject("scan_details");
                                insertIDForNotes = Integer.parseInt(responseObj.getString("id"));
                            }
                            if(response.has("scan_details_id")){
                                insertIDForNotes = Integer.parseInt(response.getString("scan_details_id"));
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(QRCodeScanner.this, "Kindly check your internet connection", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(error instanceof NetworkError) {
                            Toast.makeText(QRCodeScanner.this, "A network error occured", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(error instanceof TimeoutError) {
                            Toast.makeText(QRCodeScanner.this, "Request Timed Out.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try{
                            String s = new String(error.networkResponse.data);
                            JSONObject errorInJson = new JSONObject(s);
                            Toast.makeText(QRCodeScanner.this, errorInJson.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (NullPointerException e) {
                            Toast.makeText(QRCodeScanner.this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e){
                            Toast.makeText(QRCodeScanner.this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );
        Volley.newRequestQueue(this).add(scanExhibitor);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // if the request is the camera request and the permission is granted
            qrScanner = new IntentIntegrator(this);
            qrScanner.initiateScan();
        }else{
            Toast.makeText(this, "You did not grant the app camera permission.", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}
