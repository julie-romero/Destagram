package com.pauphilet_romero.destagram.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Requête HTTP
 * Created by Jimmy on 25/11/2014.
 */
public class HttpRequest {

    private String response = "";

    /**
     * Constructeur pour requête GET
     * @param
     */
    public HttpRequest(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = null;

        try {
            HttpGet httpGet = new HttpGet(url);
            httpResponse = httpClient.execute(httpGet);

            // on exécute et on récupère la réponse
            this.response = EntityUtils.toString(httpResponse.getEntity());

        } catch (ClientProtocolException c) {
            c.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Constructeur pour requête POST
     * @param url
     * @param nameValuePairs
     */
    public HttpRequest(String url, ArrayList<NameValuePair> nameValuePairs ) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = null;

        try {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            httpResponse = httpClient.execute(httpPost);

            // on exécute et on récupère la réponse
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
