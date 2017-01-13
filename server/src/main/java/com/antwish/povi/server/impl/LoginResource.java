package com.antwish.povi.server.impl;

import com.antwish.povi.server.EventType;
import com.antwish.povi.server.PoviEvent;
import com.antwish.povi.server.User;
import com.antwish.povi.server.db.mysql.DBUtilities;
import com.antwish.povi.server.ds.SocialPlayDataService;
import com.antwish.povi.server.utils.aws.AwsEmailClient;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.annotations.Action;
import com.linkedin.restli.server.annotations.ActionParam;
import com.linkedin.restli.server.annotations.RestLiActions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@RestLiActions(name = "poviActions", namespace = "com.antwish.povi.server")
public class LoginResource {
    Logger _log = LoggerFactory.getLogger(LoginResource.class);
    private static SocialPlayDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @Action(name = "loginEmail")
    public String loginEmail(@ActionParam("email") String email, @ActionParam("hash") String hash) {
        if(email == null || email.isEmpty()) {
            _log.warn("null or empty email in request");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "null or empty email in request");
        }

        // get user record via email
        User user = dataService.getUserFromEmail(email);

        if(user == null) {
            _log.warn(email + " is not a registered email!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, email + " is not a registered email!");
        }

        String token = dataService.loginEmail(email, hash);

        if(token != null)
        {
            dataService.insertPoviEvent(new PoviEvent().setEventType(EventType.LOGIN).setDetails(email + " successfully logged in").setEmail(email));
        } else {
            _log.warn("login with email failed: " + email + " and hash: " + hash);
        }

        return token;
    }

    @Action(name = "validateToken")
    public boolean validateToken(@ActionParam("token") String token) {
        boolean ret = dataService.validateToken(token);

        if(ret) {
            String email = dataService.getUserEmailFromToken(token);
            dataService.insertPoviEvent(new PoviEvent().setEventType(EventType.LOGIN).setDetails("validated token for " + email).setEmail(email));
        } else {
            _log.warn("validate token failed: " + token);
        }

        return ret;
    }

    @Action(name = "logout")
    public boolean logout(@ActionParam("token") String token) {
        String email = dataService.getUserEmailFromToken(token);
        if( dataService.logout(token))
        {
            dataService.insertPoviEvent(new PoviEvent().setEventType(EventType.LOGOUT).setDetails(email + " successfully logged out").setEmail(email));
            return true;
        } else {
            _log.warn("logout failed: " + token);
        }

        return false;
    }

    @Action(name = "loginFacebook")
    public String loginFacebook(@ActionParam("facebookToken") String facebookToken) {
        String token = dataService.loginFacebook(facebookToken);

        if(token != null){
            String email = dataService.getUserEmailFromToken(token);
            dataService.insertPoviEvent(new PoviEvent().setEventType(EventType.LOGIN).setDetails("successfully logged in with FB").setEmail(email));
        } else {
            _log.warn("facebook login failed: " + facebookToken);
        }

        return token;
    }

    @Action(name = "resetPassword")
    public boolean resetPassword(@ActionParam("email") String email) {
        if(email == null || email.isEmpty()) {
            _log.warn("null or empty email in request");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "null or empty email in request");
        }

        // get user record via email
        User user = dataService.getUserFromEmail(email);

        if(user == null) {
            _log.warn(email + " is not a registered email!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, email + " is not a registered email!");
        }
        // generate a random password
        String newPassword = ServerUtils.generateRandomPassword(8);

        // TODO: send email to user's registered email address
        try {
            if(!AwsEmailClient.sendEmail(email, "password reset", "Please DON'T reply to this email.\n Your new temporary password is: " + newPassword))
                return false;
        }catch (Exception ex){
            _log.error("Failed to send password reset email!");
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
            return false;
        }

        // generate new hash based on user's email and new password
        String newHash = ServerUtils.generateHash(email, newPassword);
        user.setHash(newHash);

        // update the User record
        return dataService.updateUser(user, email);
    }
}
