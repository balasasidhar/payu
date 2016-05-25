package com.sasidhar.smaps.payu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.payu.india.Model.PaymentDetails;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.PostParams.PaymentPostParams;
import com.sasidhar.smaps.payu.adapters.NetBankingListAdapter;

import java.util.ArrayList;

public class NetBankingActivity extends AppCompatActivity implements NetBankingListAdapter.OnRecyclerItemClickListener, TextWatcher {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private EditText editTextSearch;

    private ArrayList<PaymentDetails> netBankingDetails = new ArrayList<>();
    private Intent intent;
    private PaymentParams paymentParams;
    private PayuConfig payuConfig;
    private AlertDialog.Builder builder;
    private NetBankingListAdapter netBankingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_banking);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        editTextSearch = (EditText) findViewById(R.id.action_search);
        editTextSearch.addTextChangedListener(this);

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

        intent = getIntent();

        paymentParams = intent.getParcelableExtra(PayuConstants.PAYMENT_PARAMS);
        payuConfig = intent.getParcelableExtra(PayuConstants.PAYU_CONFIG);

        recyclerView = (RecyclerView) findViewById(R.id.netBankingList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        init();
    }

    private synchronized void init() {
        netBankingDetails = intent.getParcelableArrayListExtra(PayuConstants.NETBANKING);
        netBankingListAdapter = new NetBankingListAdapter(this, netBankingDetails);

        netBankingListAdapter.setOnRecyclerItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(netBankingListAdapter);

        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRecyclerItemClicked(View view, int position) {
        PaymentDetails details = netBankingListAdapter.getItem(position);
        paymentParams.setBankCode(details.getBankCode());

        try {
            PostData postData = new PaymentPostParams(paymentParams, PayuConstants.NB).getPaymentPostParams();
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuConfig.setData(postData.getResult());
                Intent intent = new Intent(this, MakePaymentActivity.class);
                intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                finish();
            } else {
                // something went wrong
                Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        netBankingListAdapter.getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
