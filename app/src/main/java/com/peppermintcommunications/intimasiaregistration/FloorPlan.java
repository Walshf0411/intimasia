package com.peppermintcommunications.intimasiaregistration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class FloorPlan extends AppCompatActivity {

    WebView floorPlanWebView;
    ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_plan);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Please wait...");

        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.intimasia_images_link))
                .appendPath("floor_plan.jpg").build().toString();
        floorPlanWebView = (WebView) findViewById(R.id.floor_plan_webview);
        floorPlanWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.hide();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                progressBar.hide();
                Toast.makeText(FloorPlan.this, "Some Error occured.", Toast.LENGTH_SHORT).show();
            }
        });
        floorPlanWebView.getSettings().setLoadWithOverviewMode(true);
        floorPlanWebView.getSettings().setUseWideViewPort(true);
        floorPlanWebView.getSettings().setBuiltInZoomControls(true);
        floorPlanWebView.loadUrl(url);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}
