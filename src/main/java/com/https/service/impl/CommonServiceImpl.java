/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.https.service.impl;

import com.https.service.CommonService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {

    @Override
    public String sendRequest(String httpsUrl) {
        String result = "";
        if (httpsUrl != null && httpsUrl.startsWith("http://")) {
            result = excuteGetHttpUTF8(httpsUrl);
        } else if (httpsUrl != null && httpsUrl.startsWith("https://")) {
            result = getHttpsContentByJoup(httpsUrl);
        }
        return result;
    }

    public static String excuteGetHttpUTF8(String targetURL) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            connection.setConnectTimeout(60000);
            InputStream is = connection.getInputStream();
            if ("gzip".equals(connection.getContentEncoding())) {
                is = new GZIPInputStream(is);
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String excuteGetHttpsUTF8(String targetURL) {
        HttpsURLConnection connection = null;
        try {
            enableSSLSocket();
            URL url = new URL(targetURL);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(20000);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) coc_coc_browser/63.4.154 Chrome/57.4.2987.154 Safari/537.36");
            int responseCode = connection.getResponseCode();
            System.out.println("responseCode: " + responseCode);
            InputStream is = null;
            if (responseCode == 200 || responseCode == 202) {
                is = connection.getInputStream();
                if ("gzip".equals(connection.getContentEncoding())) {
                    is = new GZIPInputStream(is);
                }
            } else {
                is = connection.getErrorStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String getHttpsContentByJoup(String targetURL) {
        try {
            enableSSLSocket();
            String COCCOC_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) coc_coc_browser/63.4.154 Chrome/57.4.2987.154 Safari/537.36";
            Document document = Jsoup.connect(targetURL).timeout(20000).userAgent(COCCOC_USER_AGENT).get();
            if (document != null) {
                Element bodyElement = document.getElementsByTag("body").first();
                if (bodyElement != null) {
                    return bodyElement.text();
                }
                return document.text();
            }
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {
        TrustManager[] trustAllCertificates = {new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        HostnameVerifier trustAllHostnames = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        try {
            System.setProperty("jsse.enableSNIExtension", "false");
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCertificates, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostnames);
        } catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void main(String[] args) {
        String url = "https://abc.com/sms.jsp?serviceNumber=8185&code=DV&subCode=VTP&mobile=84987654321&info=DV TVP TEST";
        String result = (new CommonServiceImpl()).sendRequest(url);
        System.out.println("result: " + result);
    }
}