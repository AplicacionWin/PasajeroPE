package com.nikola.user.login;

import com.nikola.user.Utils.Const;

import org.threeten.bp.Instant;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;


public class GetAddress {

    private Instant instant = Instant.now();
    private long epochValue = instant.getEpochSecond();
    private long finalTime = epochValue + 300;
    private String urlFinal;
    private String code = Const.CODE;
    private byte[] hmacSha256;

    public String getUrl(String method) {
        try {
            String urlInicial = Const.URL_OV + method + "?expires=" + finalTime;
            hmacSha256 = HMAC.calcHmacSha256(code.getBytes("UTF-8"), urlInicial.getBytes("UTF-8"));
            urlFinal = urlInicial + String.format("&signature=%032x", new BigInteger(1, hmacSha256));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return urlFinal;
    }


}