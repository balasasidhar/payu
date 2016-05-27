package com.sasidhar.smaps.payu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.payu.india.Interfaces.PaymentRelatedDetailsListener;
import com.payu.india.Model.MerchantWebService;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PayuResponse;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.PostParams.MerchantWebServicePostParams;
import com.payu.india.PostParams.PaymentPostParams;
import com.payu.india.Tasks.GetPaymentRelatedDetailsTask;
import com.sasidhar.smaps.payu.adapters.PaymentOptionsAdapter;
import com.sasidhar.smaps.payu.models.PaymentOptionModel;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity implements PaymentOptionsAdapter.OnRecyclerItemClickListener, PaymentRelatedDetailsListener {
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private PaymentOptionsAdapter paymentOptionsAdapter;
    private ArrayList<PaymentOptionModel> paymentOptionModels = new ArrayList<>();
    private Intent intent;
    private TextView amountPayableTextView;
    private PaymentParams paymentParams;
    private PayuConfig payuConfig;
    private PayuHashes payuHashes;
    private MerchantWebService merchantWebService;
    private PayuResponse mPayuResponse = null;
    private PostData postData;
    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Payu.setInstance(this);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        intent = getIntent();

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

        paymentParams = intent.getParcelableExtra(PayuConstants.PAYMENT_PARAMS);
        payuConfig = intent.getParcelableExtra(PayuConstants.PAYU_CONFIG);
        payuHashes = intent.getParcelableExtra(PayuConstants.PAYU_HASHES);

        amountPayableTextView = (TextView) findViewById(R.id.textViewAmountPayable);
        assert amountPayableTextView != null;
        amountPayableTextView.append(paymentParams.getAmount());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.payment_options);

        init();

    }

    private synchronized void init() {
        merchantWebService = new MerchantWebService();
        merchantWebService.setKey(paymentParams.getKey());
        merchantWebService.setCommand(PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK);
        merchantWebService.setVar1(paymentParams.getUserCredentials() == null ? "default" :
                paymentParams.getUserCredentials());

        merchantWebService.setHash(payuHashes.getPaymentRelatedDetailsForMobileSdkHash());
        PostData postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(postData.getResult());
            GetPaymentRelatedDetailsTask paymentRelatedDetailsForMobileSdkTask = new GetPaymentRelatedDetailsTask(this);
            paymentRelatedDetailsForMobileSdkTask.execute(payuConfig);
        } else {
            Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
        }
    }

    private void createPaymentOptions() {

        paymentOptionsAdapter = new PaymentOptionsAdapter(this, paymentOptionModels);
        paymentOptionsAdapter.setOnRecyclerItemClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(paymentOptionsAdapter);

        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRecyclerItemClicked(View view, int position) {
        PaymentOptionModel paymentOption = paymentOptionModels.get(position);
        switch (paymentOption.getName()) {
            case Pay_U_Constants.DEBIT_CARD:
                Intent dcIntent = new Intent(this, CreditCardActivity.class);
                dcIntent.putExtra(PayuConstants.PAYMENT_PARAMS, paymentParams);
                dcIntent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                dcIntent.putExtra(PayuConstants.CARD_TYPE, PayuConstants.DEBITCARD);
                dcIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(dcIntent);
                finish();
                break;
            case Pay_U_Constants.CREDIT_CARD:
                Intent ccIntent = new Intent(this, CreditCardActivity.class);
                ccIntent.putExtra(PayuConstants.PAYMENT_PARAMS, paymentParams);
                ccIntent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                ccIntent.putExtra(PayuConstants.CARD_TYPE, PayuConstants.CREDITCARD);
                ccIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(ccIntent);
                finish();
                break;
            case Pay_U_Constants.CASH_CARD:
                break;
            case Pay_U_Constants.EMI:
                break;
            case Pay_U_Constants.NET_BANKING:
                Intent nbIntent = new Intent(this, NetBankingActivity.class);
                nbIntent.putExtra(PayuConstants.PAYMENT_PARAMS, paymentParams);
                nbIntent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                nbIntent.putExtra(PayuConstants.NETBANKING, mPayuResponse.getNetBanks());
                nbIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(nbIntent);
                finish();
                break;
            case Pay_U_Constants.PAY_U_MONEY:
                try {
                    postData = new PaymentPostParams(paymentParams, PayuConstants.PAYU_MONEY).getPaymentPostParams();
                    if (postData.getCode() == PayuErrors.NO_ERROR) {
                        // launch webview
                        payuConfig.setData(postData.getResult());
                        Intent payUWalletIntent = new Intent(this, MakePaymentActivity.class);
                        payUWalletIntent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                        payUWalletIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        startActivity(payUWalletIntent);
                        finish();
                    } else {
                        Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onPaymentRelatedDetailsResponse(PayuResponse payuResponse) {
        mPayuResponse = payuResponse;
        if (payuResponse.isResponseAvailable() &&
                payuResponse.getResponseStatus().getCode() == PayuErrors.NO_ERROR) {

            //Toast.makeText(this, payuResponse.getResponseStatus().getResult(), Toast.LENGTH_LONG).show();

            if (payuResponse.isDebitCardAvailable() && PaymentOptions.isDebitCardEnabled) {
                paymentOptionModels.add(new PaymentOptionModel(R.drawable.ic_debit_card, Pay_U_Constants.DEBIT_CARD));
            }
            if (payuResponse.isCreditCardAvailable() && PaymentOptions.isCreditCardEnabled) {
                paymentOptionModels.add(new PaymentOptionModel(R.drawable.ic_credit_card, Pay_U_Constants.CREDIT_CARD));
            }
            if (payuResponse.isCashCardAvailable() && PaymentOptions.isCashCardEnabled) {
                paymentOptionModels.add(new PaymentOptionModel(R.drawable.ic_cash_card, Pay_U_Constants.CASH_CARD));
            }

            if (payuResponse.isEmiAvailable() && PaymentOptions.isEMIEnabled) {
                paymentOptionModels.add(new PaymentOptionModel(R.drawable.ic_emi, Pay_U_Constants.EMI));
            }

            if (payuResponse.isNetBanksAvailable() && PaymentOptions.isNetBankingEnabled) {
                paymentOptionModels.add(new PaymentOptionModel(R.drawable.ic_net_banking, Pay_U_Constants.NET_BANKING));
            }

            if (payuResponse.isPaisaWalletAvailable() && PaymentOptions.isPayUMoneyWalletEnabled &&
                    payuResponse.getPaisaWallet().get(0).getBankCode().contains(PayuConstants.PAYUW)) {
                paymentOptionModels.add(new PaymentOptionModel(R.drawable.ic_payu_money, Pay_U_Constants.PAY_U_MONEY));
            }

            createPaymentOptions();
        } else {
            Toast.makeText(this, "SOME THING WENT WRONG : " + payuResponse.getResponseStatus(), Toast.LENGTH_SHORT).show();
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
