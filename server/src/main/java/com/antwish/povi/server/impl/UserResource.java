/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.antwish.povi.server.impl;

import com.antwish.povi.server.EventType;
import com.antwish.povi.server.PoviEvent;
import com.antwish.povi.server.User;
import com.antwish.povi.server.ds.SocialPlayDataService;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.CreateResponse;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestLiCollection(name = "user", namespace = "com.antwish.povi.server")

public class UserResource extends CollectionResourceTemplate<String, User> {
    Logger _log = LoggerFactory.getLogger(UserResource.class);
    private static SocialPlayDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @Override
    public User get(String token) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders());

        return dataService.getUser(token);
    }

    @Override
    public CreateResponse create(User account) {
        // create is the first it register for a user so meanwhile a token is not yet issued
        // don't need to check the token

        if(!account.hasEmail()){
            _log.warn("an email has to be provided!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing email in the request");
        }

        if(!account.hasHash()){
            _log.warn("hash has to be provided!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing hash in the request");
        }

        User existingUser = dataService.getUserFromEmail(account.getEmail());
        if(existingUser != null)
        {
            _log.warn(account.getEmail() + " is already registered!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, account.getEmail() + " is already registered!");
        }

        if(!dataService.addUser(account)) {
            _log.error("failed to add user: " + account);
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, "failed to add user");
        }

        String newToken = ServerUtils.generateToken(account.getEmail());
        if(!dataService.updateToken(account.getEmail(), newToken)) {
            _log.error("failed to update token: " + newToken + " for user: " + account.getEmail());
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, "failed to generate token");
        }

        insertUserEvent(newToken, "registered user: " + account.getEmail());
        return new CreateResponse(newToken);
    }

    @Override
    public UpdateResponse delete(String token) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders());

        if(!dataService.deleteUser(token)) {
            _log.error("failed to delete user via token: " + token);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        // since the email is the foreign key in the events table so there's no way to insert an event
        // related to the user deletion. In fact there's no real use case from the client to remove an existing user
        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    @Override
    public UpdateResponse update(String email, User account) {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        if(!dataService.updateUser(account, email)) {
            _log.error("failed to update user: " + email + " with details: " + account);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        insertUserEvent(token, "updated user: " + email);
        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    private void insertUserEvent(String token, String details) {
        String email = dataService.getUserEmailFromToken(token);
        PoviEvent poviEvent = new PoviEvent().setEventType(EventType.REGISTRATION).setDetails(details).setEmail(email);
        dataService.insertPoviEvent(poviEvent);
    }
}
