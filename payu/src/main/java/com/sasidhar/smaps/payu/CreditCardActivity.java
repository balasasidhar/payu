package com.sasidhar.smaps.payu;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.Payu.PayuUtils;
import com.payu.india.PostParams.PaymentPostParams;

import java.util.Calendar;
import java.util.Locale;

public class CreditCardActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private EditText cardNumber, month, year, cvv, name;
    private Button makePayment;
    private Intent intent;
    private PaymentParams paymentParams;
    private PayuConfig payuConfig;
    private String cardType;
    private PayuUtils payuUtils;

    private String issuer;
    private Drawable issuerDrawable;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);


        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

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
        cardType = intent.getStringExtra(PayuConstants.CARD_TYPE);

        payuUtils = new PayuUtils();
//        payuHashes = intent.getParcelableExtra(PayuConstants.PAYU_HASHES);

        cardNumber = (EditText) findViewById(R.id.cardNumber);
        month = (EditText) findViewById(R.id.month);
        year = (EditText) findViewById(R.id.year);
        cvv = (EditText) findViewById(R.id.cvv);
        name = (EditText) findViewById(R.id.name);

        cardNumber.addTextChangedListener(this);
        makePayment = (Button) findViewById(R.id.buttonPayment);

        assert makePayment != null;
        makePayment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String _cardNumber = cardNumber.getText().toString().trim();
        String _month = month.getText().toString().trim();
        String _year = year.getText().toString().trim();
        String _cvv = cvv.getText().toString().trim();
        String _name = name.getText().toString().trim();

        int monthNum = Integer.parseInt(_month);

        if (_cardNumber.length() == 0) {
            cardNumber.setError("This field is required");
            return;
        }

        if (month.getVisibility() == View.VISIBLE) {
            if (_month.length() == 0) {
                month.setError("This filed is required");
                return;
            } else {

                if (monthNum > 12) {
                    month.setError("Invalid month");
                    return;
                }
            }
        }

        if (year.getVisibility() == View.VISIBLE) {
            if (_year.length() == 0) {
                year.setError("This filed is required");
                return;
            } else {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                int currentYear = calendar.get(Calendar.YEAR);
                int __year = Integer.parseInt(_year);
                if (__year < currentYear) {
                    year.setError("Invalid year");
                    return;
                }
            }
        }
        if (cvv.getVisibility() == View.VISIBLE && (_cvv.length() == 0 || _cvv.length() < 3)) {
            cvv.setError("This filed is required");
            return;
        }

        if (_name.length() == 0) {
            name.setError("This field is required");
            return;
        }

        paymentParams.setCardName(issuer);
        paymentParams.setCardNumber(_cardNumber);
        paymentParams.setExpiryMonth(String.format(Locale.getDefault(), "%02d", monthNum));
        paymentParams.setExpiryYear(_year);
        paymentParams.setCvv(_cvv);
        paymentParams.setNameOnCard(_name);

        try {
            PostData postData = new PaymentPostParams(paymentParams, PayuConstants.CC).getPaymentPostParams();
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
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        Log.d("TAG", "" + cardNumber.getError());

        if (charSequence.length() == 6) { // to confirm rupay card we need min 6 digit.
            issuer = payuUtils.getIssuer(charSequence.toString());
            if (issuer != null && issuer.length() > 1) {
                switch (issuer) {
                    case PayuConstants.VISA:
                        issuerDrawable = ContextCompat.getDrawable(this, R.drawable.visa);
                        break;
                    case PayuConstants.LASER:
                        issuerDrawable = ContextCompat.getDrawable(this, R.drawable.laser);
                        break;
                    case PayuConstants.DISCOVER:
                        issuerDrawable = ContextCompat.getDrawable(this, R.drawable.discover);
                        break;
                    case PayuConstants.MAES:
                        issuerDrawable = ContextCompat.getDrawable(this, R.drawable.maestro);
                        break;
                    case PayuConstants.MAST:
                        issuerDrawable = ContextCompat.getDrawable(this, R.drawable.mastercard);
                        break;
                    case PayuConstants.AMEX:
                        issuerDrawable = ContextCompat.getDrawable(this, R.drawable.amex);
                        break;
                    case PayuConstants.DINR:
                        issuerDrawable = ContextCompat.getDrawable(this, R.drawable.diners);
                        break;
                    case PayuConstants.JCB:
                        issuerDrawable = ContextCompat.getDrawable(this, R.drawable.jcb);
                        break;
                    case PayuConstants.SMAE:
                        issuerDrawable = ContextCompat.getDrawable(this, R.drawable.maestro);
                        break;
                    case PayuConstants.RUPAY:
                        issuerDrawable = ContextCompat.getDrawable(this, R.drawable.rupay);
                        break;
                    default:
                        issuerDrawable = ContextCompat.getDrawable(this, R.drawable.credit);
                }
                if (issuer.contentEquals(PayuConstants.SMAE)) { // hide cvv and expiry
                    month.setVisibility(View.GONE);
                    year.setVisibility(View.GONE);
                    cvv.setVisibility(View.GONE);
                } else { //show cvv and expiry
                    month.setVisibility(View.VISIBLE);
                    year.setVisibility(View.VISIBLE);
                    cvv.setVisibility(View.VISIBLE);
                }
                cardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, issuerDrawable, null);
            }
        }

        if (charSequence.length() > 11 && !makePayment.isEnabled()) {
            makePayment.setEnabled(true);
        } else if (charSequence.length() < 12 && makePayment.isEnabled()) {
            makePayment.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

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
