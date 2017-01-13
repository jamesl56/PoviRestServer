package com.antwish.povi.server.impl;

import com.antwish.povi.server.ActivationRecord;
import com.antwish.povi.server.Identity;
import com.antwish.povi.server.IdentityType;
import com.antwish.povi.server.ds.SocialPlayDataService;
import com.antwish.povi.server.ds.SocialPlayDataServiceImpl;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.RestLiServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class ServerUtils {

    private static Logger _log = LoggerFactory.getLogger(LoginResource.class);
    public static String SEPERATOR = ":";
    public static String POVI_AUTHORIZATION_HEADER = "povi-authorization";
    public static String ALPHABETES = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789_#?!&*";

    private static String dbName = "socialplay";
    private static String poviDbName = "povi_schema";
    private static String dbUrl = "localhost";
    private static String userName = "socialplay";
    private static String poviUserName = "povi";
    private static String password = "socialplay";
    private static String poviPassword = "povi";

    private static Random random = new Random();

    /**
     * generate a random password with numeric alphabets to a specified length
     * @param length
     * @return
     */
    public static String generateRandomPassword(int length)
    {
        if(length<=0)
            length = 8;

        StringBuilder sb = new StringBuilder(length);

        for(int i=0; i<length; i++){
            sb.append(ALPHABETES.charAt(random.nextInt(ALPHABETES.length())));
        }

        return sb.toString();
    }

    public static String generateToken(String email) {
        String data = email + Long.toString(System.currentTimeMillis());
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.reset();
        try {
            digest.update(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new BigInteger(1, digest.digest()).toString(16);
    }

    /**
     * method to generate hash based on user's email and password
     * @param email
     * @param password
     * @return
     */
    public static String generateHash(String email, String password){
        String data = email + password;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.reset();
        try {
            digest.update(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String hash = new BigInteger(1, digest.digest()).toString(16);

        return hash;
    }
    /**
     * method to make sure a request has the povi-authorization header
     *
     * @param headerMap
     */
    public static String checkHeader(Map<String, String> headerMap) {
        //TODO: may need to evaluate the token passed in with the request
        // represents a valid session
        if (headerMap == null || !headerMap.containsKey(POVI_AUTHORIZATION_HEADER)) {
            _log.warn("missing " + POVI_AUTHORIZATION_HEADER + " in the headers");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST,
                    POVI_AUTHORIZATION_HEADER + " header must present in request!");
        }
        else {
            String token = headerMap.get(POVI_AUTHORIZATION_HEADER);
            _log.debug(POVI_AUTHORIZATION_HEADER + ": " + token);
            return token;
        }
    }

    public static SocialPlayDataService initPoviDataService(SocialPlayDataService ds) {
        if (ds == null) {
            _log.debug("initialize SocialPlayDataService");
            ds = new SocialPlayDataServiceImpl(dbUrl, poviDbName, poviUserName, poviPassword);
        }

        return ds;
    }

    public static Long convertFromDateString(String dateStr) {
        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
        long timestamp = 0;
        try {
            timestamp = fromUser.parse(dateStr).getTime();
        } catch (ParseException pex) {
            return null;
        }

        return timestamp;
    }

    public static String currentTimestamp() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static String[] getInfoFromFacebook(String facebookToken)
    {
        // Define constants
        final String BASE_URL = "https://graph.facebook.com/v2.3/";
        final String CLIENT_ID = "666985220100012";
        final String CLIENT_SECRET = "5f955b6725587337b7e45299a61244fa";

        URL facebookUrl = null;
        String apptoken = null;
        String user_id = null;
        String user_email = null;
        String user_name = null;
        boolean tokenValid = false;

        System.out.println(ServerUtils.currentTimestamp() + " start calling FB graph API");
        try {
            // Get POVI app current access token
            facebookUrl = new URL(BASE_URL + "oauth/access_token?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&grant_type=client_credentials");
            InputStream is = facebookUrl.openStream();
            JsonReader rdr = Json.createReader(is);
            JsonObject obj = rdr.readObject();
            apptoken = obj.getString("access_token");
            System.out.println("apptoken: " + apptoken);

            // Validate user's facebook access token and get user's ID
            facebookUrl = new URL(BASE_URL + "debug_token?input_token=" + facebookToken + "&access_token=" + apptoken);
            is = facebookUrl.openStream();
            rdr = Json.createReader(is);
            obj = rdr.readObject();
            JsonObject data = obj.getJsonObject("data");
            System.out.println("appid: " + data.getString("app_id"));
            System.out.println("appname: " + data.getString("application"));
            System.out.println("isvalid: " + data.getBoolean("is_valid"));
            tokenValid = data.getBoolean("is_valid");
            //if (!tokenValid)
            //return null;
            // TODO: manage expired or invalid access tokens
            user_id = data.getString("user_id");
            System.out.println("user_id: " + user_id);

            // Get user's public profile from data
            facebookUrl = new URL(BASE_URL + user_id + "?access_token=" + apptoken);
            is = facebookUrl.openStream();
            rdr = Json.createReader(is);
            obj = rdr.readObject();
            user_email = obj.getString("email");
            user_name = obj.getString("name");
            System.out.println("user_email: " + user_email );
            System.out.println("user_name: " + user_name );
            String[] results = new String[2];
            results[0] = user_email;
            results[1] = user_name;

            return results;
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
