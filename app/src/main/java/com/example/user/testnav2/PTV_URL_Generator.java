package com.example.user.testnav2;

import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class PTV_URL_Generator {

    String varBaseURL = "http://timetableapi.ptv.vic.gov.au";
    String varPrivateKey = "bed02fe3-ba2f-46c7-acfb-e922e272f032";
    int varDeveloperID = 3000827;


    public String healthCheck() {
        String tempURI = "/v2/healthcheck?timestamp=" + generateTimeSig();
        try {
            return buildTTAPIURL(varBaseURL,varPrivateKey,tempURI,varDeveloperID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "[]";
    }

    public String buildURL(String s) {
        String results = "[]";
        try {
            results = buildTTAPIURL(varBaseURL,varPrivateKey,s,varDeveloperID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }


    //THIS FUNCTION IS PROVIDED FROM PTV'S EXAMPLE CODE FOUND HERE - https://static.ptv.vic.gov.au/PTV/PTV%20docs/API/1475462320/PTV-Timetable-API-key-and-signature-document.RTF
    //This function takes in the destination URL and required parameters, and then returns a HMAC-SHA1 hash of the URL in the form of a 128bit GUID
    public String buildTTAPIURL(final String baseURL, final String privateKey, final String uri, final int developerId) throws Exception {
        String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        StringBuffer uriWithDeveloperID = new StringBuffer().append(uri).append(uri.contains("?") ? "&" : "?").append("devid=" + developerId);
        byte[] keyBytes = privateKey.getBytes();
        byte[] uriBytes = uriWithDeveloperID.toString().getBytes();
        Key signingKey = new SecretKeySpec(keyBytes, HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        byte[] signatureBytes = mac.doFinal(uriBytes);
        StringBuffer signature = new StringBuffer(signatureBytes.length * 2);
        for (byte signatureByte : signatureBytes) {
            int intVal = signatureByte & 0xff;
            if (intVal < 0x10) {
                signature.append("0");
            }
            signature.append(Integer.toHexString(intVal));
        }
        StringBuffer url = new StringBuffer(baseURL).append(uri).append(uri.contains("?") ? "&" : "?").append("devid=" + developerId).append("&signature=" + signature.toString().toUpperCase());
        return url.toString();
    }

    //Generates Timestamp in UTC. Needed for PTV API requests
    public String generateTimeSig() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        String result = df.format(new Date());
        return result;
    }
}
