package com.sasidhar.smaps.payu;

import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;

/**
 * Created by SASi on 24-May-16.
 */
public class Utils {


    public static synchronized PayuHashes generateHashFromSDK(PaymentParams mPaymentParams, String salt) {

        PayuHashes payuHashes = new PayuHashes();
        PostData postData = new PostData();

        // payment Hash;
        PayUChecksum checksum = new PayUChecksum();

        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();

        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }

        postData = calculateHash(mPaymentParams.getKey(),
                PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt);

        if (postData != null) {
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        }

        return payuHashes;
    }

    private static synchronized PostData calculateHash(String key, String command, String var, String salt) {
        PayUChecksum checksum = new PayUChecksum();
        checksum.setKey(key);
        checksum.setCommand(command);
        checksum.setVar1(var);
        checksum.setSalt(salt);
        return checksum.getHash();
    }
}
