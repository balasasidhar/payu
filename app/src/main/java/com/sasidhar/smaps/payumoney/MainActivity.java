package com.sasidhar.smaps.payumoney;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.math.BigDecimal;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText inputEditTextName, inputEditTextEmail, inputEditTextMobile,
            inputEditTextProduct, inputEditTextAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEditTextName = (TextInputEditText) findViewById(R.id.name);
        inputEditTextEmail = (TextInputEditText) findViewById(R.id.email);
        inputEditTextMobile = (TextInputEditText) findViewById(R.id.mobile);
        inputEditTextProduct = (TextInputEditText) findViewById(R.id.product);
        inputEditTextAmount = (TextInputEditText) findViewById(R.id.amount);

        inputEditTextName.setText("Sasidhar");
        inputEditTextEmail.setText("sasidhar.678@gmail.com");
        inputEditTextMobile.setText("9959582678");
        inputEditTextProduct.setText("Pen");
        inputEditTextAmount.setText("10");


        Button button = (Button) findViewById(R.id.buttonContinue);
        assert button != null;
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = inputEditTextName.getText().toString().trim();
        String email = inputEditTextEmail.getText().toString().trim();
        String mobile = inputEditTextMobile.getText().toString().trim();
        String product = inputEditTextProduct.getText().toString().trim();
        String amount = inputEditTextAmount.getText().toString().trim();

        if (name.length() == 0) {
            inputEditTextName.setError("This field is required");
        } else if (email.length() == 0) {
            inputEditTextEmail.setError("This field is required");
        } else if (mobile.length() == 0 || mobile.length() < 10) {
            inputEditTextMobile.setError("This field is required");
        } else if (product.length() == 0) {
            inputEditTextProduct.setError("This field is required");
        } else if (amount.length() == 0) {
            inputEditTextAmount.setError("This field is required");
        } else {

            double _amount = Double.parseDouble(amount);
            amount = String.format(Locale.getDefault(), "%.2f", new BigDecimal(_amount));


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
