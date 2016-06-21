package com.facticoapp.nuup.httpconnection;

import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class HttpConnection {

    private static final String BASE_URL = "http://104.210.150.140/";

    public static final String DEVICES = "devices.json";

    public static String GET(String relativeUrl) {
        String result = null;
        HttpURLConnection urlConnection = null;
        try {
            URL u = new URL(getAbsoluteUrl(relativeUrl));
            urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(40000);
            urlConnection.setReadTimeout(40000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = convertInputStreamToString(in);
            } else {
                result = null;
            }
        } catch (IOException e) {
            result = null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return result;
    }

    public static String POST(String relativeUrl, String json) {
        String result = null;
        HttpURLConnection urlConnection = null;
        try {
            URL u = new URL(getAbsoluteUrl(relativeUrl));
            urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setConnectTimeout(40000);
            urlConnection.setReadTimeout(40000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setChunkedStreamingMode(1024);
            // urlConnection.setFixedLengthStreamingMode(json.getBytes().length);

            // Authorization
            //String authorizationString = getB64Auth(USER, PWD);
            //urlConnection.setRequestProperty("Authorization", authorizationString);

            // Make some HTTP header nicety
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            urlConnection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            char[] buffer = new char[1024];
            int bufferLength = 0;
            StringReader stringReader = new StringReader(json);
            while ((bufferLength = stringReader.read(buffer)) > 0) {
                writer.write(buffer, 0, bufferLength);
            }
            writer.flush();

            //OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            //os.write(json.getBytes());
            //clean up
            //os.flush();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK ||
                    urlConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED ||
                    urlConnection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                InputStream inOk = new BufferedInputStream(urlConnection.getInputStream());
                result = convertInputStreamToString(inOk);
            } else {
                InputStream inError = new BufferedInputStream(urlConnection.getErrorStream());
                result = convertInputStreamToString(inError);
            }

        } catch (IOException e) {
            result = null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return result;
    }

    public static String PUT(String relativeUrl, String json) {
        String result = null;
        HttpURLConnection urlConnection = null;
        try {
            URL u = new URL(getAbsoluteUrl(relativeUrl));
            urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setConnectTimeout(40000);
            urlConnection.setReadTimeout(40000);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setFixedLengthStreamingMode(json.getBytes().length);

            // Authorization
            //String authorizationString = getB64Auth(USER, PWD);
            //urlConnection.setRequestProperty("Authorization", authorizationString);

            // Make some HTTP header nicety
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            urlConnection.connect();

            //setup send
            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            os.write(json.getBytes());
            //clean up
            os.flush();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK ||
                    urlConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED ||
                    urlConnection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                InputStream inOk = new BufferedInputStream(urlConnection.getInputStream());
                result = convertInputStreamToString(inOk);
            } else {
                InputStream inError = new BufferedInputStream(urlConnection.getErrorStream());
                result = convertInputStreamToString(inError);
            }

        } catch (IOException e) {
            result = null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return result;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static String getB64Auth(String user, String pass) {
        String source = user + ":" + pass;
        return "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
