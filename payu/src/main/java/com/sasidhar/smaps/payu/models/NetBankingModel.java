package com.sasidhar.smaps.payu.models;

/**
 * Created by SASi on 25-May-16.
 */
public class NetBankingModel {
    private String bankId;
    private String bankName;
    private String bankCode;
    private String pgId;
    private int logo;

    public NetBankingModel() {
    }

    public NetBankingModel(String bankId, String bankName, String bankCode, String pgId, int logo) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.pgId = pgId;
        this.logo = logo;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getPgId() {
        return pgId;
    }

    public void setPgId(String pgId) {
        this.pgId = pgId;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }
}
