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

import com.antwish.povi.server.Child;
import com.antwish.povi.server.ChildId;
import com.antwish.povi.server.EventType;
import com.antwish.povi.server.PoviEvent;
import com.antwish.povi.server.ds.SocialPlayDataService;
import com.linkedin.data.template.SetMode;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.PagingContext;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.AssocKeyParam;
import com.linkedin.restli.server.annotations.Context;
import com.linkedin.restli.server.annotations.Finder;
import com.linkedin.restli.server.annotations.QueryParam;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;
import com.linkedin.restli.server.CreateResponse;
import com.linkedin.restli.server.resources.ComplexKeyResource;
import com.linkedin.restli.server.resources.ComplexKeyResourceTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestLiCollection(name = "child", namespace = "com.antwish.povi.server")

public class ChildResource extends ComplexKeyResourceTemplate<ChildId, ChildId, Child> {
    Logger _log = LoggerFactory.getLogger(ChildResource.class);

    private static SocialPlayDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @Finder("getChildren")
    public List<Child> getChildren(@QueryParam("user_id") String user_id){
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders());

        if (user_id == null || user_id.isEmpty()) {
            _log.debug("getChildren: null or empty userId!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST,
                    "getChildren: null or empty userId!");
        }

        return dataService.getChildren(user_id);
    }

    @Override
    public Child get(ComplexResourceKey<ChildId, ChildId> childId)
    {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders());

        return dataService.getChild(childId.getKey().getUser_id(), childId.getKey().getChild_Id());
    }

    @Override
    public CreateResponse create(Child child){
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        if(!dataService.addChild(child)) {
            _log.error("failed to add child: " + child);
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, "failed to add child");
        }

        insertChildrenEvent(token, child.getUser_id() + " added child: " + child.getName());

        return new CreateResponse(child.getUser_id());
    }

    @Override
    public UpdateResponse update(final ComplexResourceKey<ChildId, ChildId> key, final Child entity){
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        if(!dataService.updateChild(key.getKey().getUser_id(), key.getKey().getChild_Id(), entity)) {
            _log.error("failed to update a child with " + key + " and " + entity);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        insertChildrenEvent(token, key.getKey().getUser_id() + " updated child: " + key.getKey().getChild_Id());
        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    @Override
    public UpdateResponse delete(ComplexResourceKey<ChildId, ChildId> childId){
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        if(!dataService.deleteChild(childId.getKey().getUser_id(), childId.getKey().getChild_Id())) {
            _log.error("delete a child failed on: " + childId);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        insertChildrenEvent(token, childId.getKey().getUser_id()  + " deleted child: " + childId.getKey().getChild_Id());
        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    private void insertChildrenEvent(String token, String details){
        String email = dataService.getUserEmailFromToken(token);
        PoviEvent poviEvent = new PoviEvent().setEventType(EventType.CHILD).setDetails(details).setEmail(email);
        dataService.insertPoviEvent(poviEvent);
    }
}
