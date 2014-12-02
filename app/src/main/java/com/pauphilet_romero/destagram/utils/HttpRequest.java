package com.pauphilet_romero.destagram.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Jimmy on 25/11/2014.
 */
public class HttpRequest {

    private String response = "";

    /**
     * Requête HTTP
     * @param
     */
    public HttpRequest(String url) {
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = null;

        try {
            // on exécute et on récupère la réponse
            httpResponse = httpClient.execute(httpGet);
            this.response = EntityUtils.toString(httpResponse.getEntity());

        } catch (ClientProtocolException c) {
            c.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public String getResponse() {
        return this.response;
    }
}
