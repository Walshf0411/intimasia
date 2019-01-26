package com.peppermintcommunications.intimasiaregistration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScanHistory1 extends AppCompatActivity implements ExhibitorListAdapter.ExhibitorItemClickListener {

    RecyclerView exhibitorList;
    ArrayList<Exhibitor> exhibitors = new ArrayList<>();
    ArrayList<Exhibitor> allExhibitors = new ArrayList<>();
    ExhibitorListAdapter exhibitorListAdapter;
    TextView exhibitorCount;
    private static final int HALL_B = 21;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_history);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Hall B Scanned Exhibitors");
        }
        requestQueue = Volley.newRequestQueue(this);
        exhibitorList = (RecyclerView) findViewById(R.id.hall_a_exhibitors_recycler_view);
        exhibitorList.setLayoutManager(new LinearLayoutManager(this));
        exhibitorListAdapter = new ExhibitorListAdapter(this, exhibitors);
        exhibitorCount = (TextView) findViewById(R.id.number_of_exhibitors);
        exhibitorCount.setText("0/" + HALL_B);
        makeRequest();
    }

    private void makeRequest() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );
        String guid = sharedPreferences.getString("guid", null);
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.intimasia_base_domain))
                .appendPath("user_scan_history.php")
                .appendQueryParameter("scanner_guid", guid)
                .appendQueryParameter("hall", "B")
                .build().toString();
        CustomRequest getHistoryHallA = new CustomRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.has("exhibitors")){
                                JSONArray exhibitors = response.getJSONArray("exhibitors");
                                ScanHistory1.this.exhibitors = new ArrayList<>();
                                for (int i=0; i<exhibitors.length(); i++){
                                    JSONObject exhibitor = exhibitors.getJSONObject(i);
                                    ScanHistory1.this.exhibitors.add(new Exhibitor(
                                                    exhibitor.getInt("exhibitor_id"),
                                                    exhibitor.getString("exhibitor_name"),
                                                    exhibitor.getString("brands"),
                                                    exhibitor.getString("city"),
                                                    exhibitor.getString("stall"),
                                                    exhibitor.getString("product"),
                                                    exhibitor.getString("company_addr"),
                                                    exhibitor.getString("hall"),
                                                    exhibitor.getString("notes")
                                            )
                                    );
                                };
                                exhibitorCount.setText(exhibitors.length() + "/" + HALL_B);
                                exhibitorListAdapter = new ExhibitorListAdapter(ScanHistory1.this, ScanHistory1.this.exhibitors);
                                exhibitorList.setAdapter(exhibitorListAdapter);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(ScanHistory1.this, "Kindly check your internt connection", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (error instanceof TimeoutError) {
                            Toast.makeText(ScanHistory1.this, "Request Timed out.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (error instanceof NetworkError) {
                            Toast.makeText(ScanHistory1.this, "Network Error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
        );
        requestQueue.add(getHistoryHallA);
    }
    @Override
    public void onExhibitorItemClick(Exhibitor exhibitor) {
        Intent intent = new Intent(ScanHistory1.this, ExhibitorDetailActivity.class);
        intent.putExtra("exhibitor", exhibitor);
        startActivity(intent);
    }
}
