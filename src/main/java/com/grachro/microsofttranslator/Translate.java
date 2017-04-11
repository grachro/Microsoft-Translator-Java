package com.grachro.microsofttranslator;

import java.net.URI;

import org.apache.commons.codec.net.URLCodec;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;


public class Translate {

    public static void main(String[] args) {
        String subscriptionKey = args[0];
        String token = getToken(subscriptionKey);
        String text = "こんにちは世界";
        String fromLang = "ja";
        String toLang = "en-US";

        String translatedText = translate(token, fromLang, toLang, text);
        System.out.println(translatedText);
    }

    private static URLCodec CODEC = new URLCodec("UTF-8");

    public static String getToken(String subscriptionKey) {
        HttpClient httpclient = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder("https://api.cognitive.microsoft.com/sts/v1.0/issueToken");
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            return EntityUtils.toString(entity);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String translate(String accessToken, String fromLang, String toLang, String text) {

        try {

            String env = CODEC.encode(text, "UTF-8");

            String translateUrl = "https://api.microsofttranslator.com/v2/Ajax.svc/Translate?text=%1s&from=%2s&to=%3s";
            String envUrl = String.format(translateUrl, env, fromLang, toLang);
            HttpGet request = new HttpGet(envUrl);

            String authorization = "Bearer " + accessToken;
            request.setHeader("Authorization", authorization);

            HttpClient httpclient = HttpClients.createDefault();
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            String resultJson = EntityUtils.toString(entity);
            Gson gson = new Gson();
            
            return gson.fromJson(resultJson, String.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
