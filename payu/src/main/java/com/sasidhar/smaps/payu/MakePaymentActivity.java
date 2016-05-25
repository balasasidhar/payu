package com.sasidhar.smaps.payu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.payu.india.Model.PayuConfig;
import com.payu.india.Payu.PayuConstants;

public class MakePaymentActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        builder = new AlertDialog.Builder(this)
                .setTitle("Are you sure ?")
                .setMessage("Do you want to cancel your payment process ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        Bundle bundle = getIntent().getExtras();
        PayuConfig payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
        final WebView mWebView = (WebView) findViewById(R.id.webview);

        String url = payuConfig.getEnvironment() == PayuConstants.PRODUCTION_ENV ? PayuConstants.PRODUCTION_PAYMENT_URL : PayuConstants.MOBILE_TEST_PAYMENT_URL;
        byte[] encodedData = payuConfig.getData().getBytes();

        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new PayU(this), "PayU");
        mWebView.postUrl(url, encodedData);
    }

    public class PayU {
        Context mContext;
        Intent intent;

        PayU(Context c) {
            mContext = c;
            intent = new Intent();
        }

        @JavascriptInterface
        public void onSuccess(final String result) {
            intent.putExtra(Pay_U_Constants.PAYMENT_RESULT, "Payment Success");
            setResult(RESULT_OK, intent);
            finish();
        }

        @JavascriptInterface
        public void onFailure(final String result) {
            intent.putExtra(Pay_U_Constants.PAYMENT_RESULT, "Payment Failed");
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
