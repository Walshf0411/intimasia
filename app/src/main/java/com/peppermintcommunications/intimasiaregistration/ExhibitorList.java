package com.peppermintcommunications.intimasiaregistration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExhibitorList extends AppCompatActivity implements ExhibitorListAdapter.ExhibitorItemClickListener {
    private String[] EXHIBITORS_LIST = {"Groversons Apparel Pvt. Ltd."
            ,"Bodycare Creations Pvt. Ltd."
            ,"Bodycare International Ltd."
            ,"Belle Wears Pvt. Ltd."
            ,"Sagar Products"
            ,"Eves Apparel"
            ,"Jigna Enterprise"
            ,"Makhija Intimate"
            ,"Sidharth Enterprise"
            ,"KD Garments"
            ,"Little Lacy (I) Pvt. Ltd."
            ,"Kim Sim Creations"
            ,"Sherry Apparels Pvt. Ltd."
            ,"SD Retail Pvt. Ltd."
            ,"Naidu Hall"
            ,"Pepe Jeans Inner Fashion Pvt. Ltd."
            ,"Omtex Healthwear Pvt. Ltd."
            ,"Mackly Clothing Pvt. Ltd."
            ,"Sahil Enterprises"
            ,"Ebell fashions Pvt.Ltd."
            ,"Dixcy Textiles Pvt. Ltd."
            ,"Kalp Clothing Pvt. Ltd."
            ,"Sakshi Lingeries Pvt. Ltd."
            ,"Lux Industries Ltd."
            ,"A.R Hosiery"
            ,"Benetton India Pvt. Ltd."
            ,"Shalibhadra Creations Pvt Ltd"
            ,"M/S. Abhinandan Creations"
            ,"Kamra Lingeries"
            ,"J.G Hosiery Pvt. Ltd."
            ,"Zonac Knitting Machines Pvt. Ltd."
            ,"MTC Retail Pvt. Ltd."
            ,"A.R Fashions Pvt. Ltd."
            ,"D.S Creations"
            ,"Superknit Industries"
            ,"Sweet Night"
            ,"Yash Hosiery India Pvt Ltd"
            ,"Actoserba Active Wholesale Pvt. Ltd."
            ,"Truelan Textiles Pvt. Ltd."
            ,"Kiran Enterprise"
            ,"Global Clothing Company"
            ,"Mustang Enterprises"
            ,"Purple Panda Fashions Pvt. Ltd."
            ,"Triumph International (India) Pvt. Ltd."
            ,"Dollar Industries Ltd."
            ,"Pearl Clothing"
            ,"Digsel Cool Cotton"
            ,"VIP Clothing Pvt. Ltd."
            ,"Orange Fashion Village"
            ,"Vardhman Sleepwear & Lingerie Pvt. Ltd."
            ,"RVK"
            ,"Inticede BSD PVT LTD"
            ,"Resolute Retail Pvt. Ltd."
            ,"Ravechi Clothing","T.T Limited"
    };
    RecyclerView exhibitorList;
    ProgressDialog progressDialog;
    ArrayList<Exhibitor> exhibitors = new ArrayList<>();
    ExhibitorListAdapter exhibitorListAdapter;
    private static final int HALL_A = 58;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_exhibitor_list);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Exhibitors List");
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        exhibitorList = (RecyclerView) findViewById(R.id.hall_a_exhibitors_recycler_view);
        exhibitorList.setLayoutManager(new LinearLayoutManager(this));
        exhibitorListAdapter = new ExhibitorListAdapter(this, exhibitors);
        makeRequest();
    }

    private void makeRequest() {
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.intimasia_base_domain))
                .appendPath("all_exhibitors.php")
                .build().toString();

        CustomRequest getHistoryHallA = new CustomRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            progressDialog.hide();
                            if(response.has("exhibitors")){
                                JSONArray exhibitors = response.getJSONArray("exhibitors");
                                ExhibitorList.this.exhibitors = new ArrayList<>();
                                for (int i=0; i<exhibitors.length(); i++){
                                    JSONObject exhibitor = exhibitors.getJSONObject(i);
                                    ExhibitorList.this.exhibitors.add(new Exhibitor(
                                                    0,
                                                    exhibitor.getString("exhibitor_name"),
                                                    exhibitor.getString("brands"),
                                                    exhibitor.getString("city"),
                                                    exhibitor.getString("stall"),
                                                    exhibitor.getString("product"),
                                                    exhibitor.getString("company_addr"),
                                                    null,
                                                null
                                            )
                                    );
                                };
                                exhibitorListAdapter = new ExhibitorListAdapter(ExhibitorList.this, ExhibitorList.this.exhibitors);
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
                        progressDialog.hide();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(ExhibitorList.this, "Kindly check your internt connection", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (error instanceof TimeoutError) {
                            Toast.makeText(ExhibitorList.this, "Request Timed out.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (error instanceof NetworkError) {
                            Toast.makeText(ExhibitorList.this, "Network Error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
        );
        Volley.newRequestQueue(this).add(getHistoryHallA);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
    @Override
    public void onExhibitorItemClick(Exhibitor exhibitor) {
        Intent intent = new Intent(ExhibitorList.this, ExhibitorDetailActivity.class);
        intent.putExtra("exhibitor", exhibitor);
        startActivity(intent);
    }
}
