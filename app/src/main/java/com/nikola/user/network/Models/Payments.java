package com.nikola.user.network.Models;

/**
 * Created by ${Asher} on 11/14/2017.
 */

public class Payments {
    private String payment_id;
    private String payment_name;
    private String paymentMode;

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getPayment_name() {
        return payment_name;
    }

    public void setPayment_name(String payment_name) {
        this.payment_name = payment_name;
    }
}
