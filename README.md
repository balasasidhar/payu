# payu

<h4> PayU Money SDK & UI integration (beta) </h4>
<p> A simple & Easiest way to integrate PayU Money Payment Gateway with your Android Application </p>
<img src="https://dl.dropbox.com/s/prj2gv928sgjh8u/device-2016-05-25-032533.png" alt="PayU Money" width="33%">

<h5>Step by step guide to integrate PayU Money SDK with your Android Application </h5>
<ol>

<li> Add Maven repository and compile dependency in <b> build.gradle </b> file.
<pre>
apply plugin: 'com.android.application'

android {
    repositories {
        maven {
            url  "http://dl.bintray.com/sasidhar-678/maven"
        }
    }
}

dependencies {
    compile 'com.sasidhar.smaps.payu:payu:0.0.3'
}
</pre>
</li>
<li> Add Internt Permissions in your Android Application Manifest file
<pre>
    &lt; uses-permission android:name="android.permission.INTERNET" /&gt;
</pre>
</li>
<li> Create an object for <b> PayuConfig </b> Class and Configure your environement (test/dev) 
<pre>
PayuConfig payuConfig = new PayuConfig();

// for testing
payuConfig.setEnvironment(PayuConstants.MOBILE_DEV_ENV);

// for production 
payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);
</pre>
</li> Create an object for <b> PaymentParams </b> and obtain all the required parameters
<li> 
<pre>
PaymentParams paymentParams = new PaymentParams();
paymentParams.setKey("merchant_key"); // Get Merchant Key from PayU Money Merchant Account
paymentParams.setFirstName("name"); // User Name
paymentParams.setEmail("email"); // User Email Address
paymentParams.setPhone("phone"); // User Mobile Number
paymentParams.setProductInfo("productinfo"); // Product info
paymentParams.setAmount("amount"); // Amout 
paymentParams.setTxnId(""); // Transaction ID
paymentParams.setSurl("SURL"); // Success URL
paymentParams.setFurl("FURL"); // Failure URL

// User defined fields are optional (pass empty string)

paymentParams.setUdf1(""); 
paymentParams.setUdf2("");
paymentParams.setUdf3("");
paymentParams.setUdf4("");
paymentParams.setUdf5("");

</pre>
</li>
<li> Generate <b> HASH </b>  by with PaymentParams object & SALT and add it to PaymentParams object.
<pre>
PayuHashes payuHashes = Utils.generateHashFromSDK(paymentParams, "SALT"); // Get SALT from PayU Money Merchant Account
paymentParams.setHash(payuHashes.getPaymentHash());
</pre>
<p style="color: #F44336"> Note: It is recommanded to generate <b> HASH </b> from your own server instea of here.</p>
</li>
<li>Create an Intent to a <b> PaymentActivity </b> and pass PayuConfig, PaymentParams and PayuHashes objects that we are create in the above steps as intent extras.
<pre>
Intent intent = new Intent(this, PaymentActivity.class);
intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
intent.putExtra(PayuConstants.PAYMENT_PARAMS, paymentParams);
intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
</pre>
</li>
<li> Start Activity for Result
<pre>
startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
</pre>
</li>
<li> Handle response at onActivityResult
<pre>
if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
    switch (resultCode) {
        case RESULT_OK:
            Toast.makeText(MainActivity.this, "Payment Success.", Toast.LENGTH_SHORT).show();
            break;

        case RESULT_CANCELED:
            Toast.makeText(MainActivity.this, "Payment Cancelled | Failed.", Toast.LENGTH_SHORT).show();
            break;
    }
}
</pre>
</li>
<li> Optionally you can alos configure Payment Methods to enable or disable by setting boolean value (true/false) before calling <b> startActivityForResult </b>
<pre>
PaymentOptions.DebitCardEnabled = true;
PaymentOptions.CreditCardEnabled = true;
PaymentOptions.isNetBankingEnabled = true;
PaymentOptions.isEMIEnabled = false;
PaymentOptions.isPayUMoneyWalletEnabled = true;
PaymentOptions.isCashCardEnabled = false;
</pre>
</li>
</ol>
