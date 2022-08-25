package com.nikola.user.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AbstractClient {
	

	public String web(String urlEntrada, String message) throws IOException
    {
        
		String data = "";
        URL url = new URL(urlEntrada);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        System.out.println("url web " + urlEntrada);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        String valor = message;
        connection.setDoOutput(true);
        connection.getOutputStream().write(valor.getBytes());   
        connection.setConnectTimeout(10000);
        connection.connect();
        System.out.println();
        if (connection.getResponseCode() != 200) {
        	throw new RuntimeException("Failed : HTTP error code : "
        	    + connection.getResponseCode());
        }
       
        InputStream in = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(in);

        int inputStreamData = inputStreamReader.read();
        while (inputStreamData != -1) {
            char current = (char) inputStreamData;
            inputStreamData = inputStreamReader.read();
            data += current;

        }
        
        connection.disconnect();
        return data;
       
    }
	

}
