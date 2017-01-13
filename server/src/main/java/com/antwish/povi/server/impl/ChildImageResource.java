package com.antwish.povi.server.impl;

import com.antwish.povi.server.Comment;
import com.antwish.povi.server.ChildId;
import com.antwish.povi.server.EventType;
import com.antwish.povi.server.PoviEvent;
import com.antwish.povi.server.ChildImage;
import com.antwish.povi.server.ds.SocialPlayDataService;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.CollectionResult;
import com.linkedin.restli.server.CreateResponse;
import com.linkedin.restli.server.PagingContext;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.Finder;
import com.linkedin.restli.server.annotations.Optional;
import com.linkedin.restli.server.annotations.PagingContextParam;
import com.linkedin.restli.server.annotations.QueryParam;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.annotations.RestMethod;
import com.linkedin.restli.server.resources.ComplexKeyResourceTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


@RestLiCollection(name = "ChildImage", namespace = "com.antwish.povi.server")
public class ChildImageResource extends ComplexKeyResourceTemplate<ChildId, ChildId, ChildImage> {

    Logger _log = LoggerFactory.getLogger(ChildImageResource.class);
    private static SocialPlayDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @RestMethod.Create
    public CreateResponse createEntry(ChildImage entity) {

        // Check whether a proper token is attached to to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        if(!entity.hasFileContent()){
            _log.warn("Expect to have file content when creating a child image");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing child image in the request");
        }

        if(!entity.hasChildName()){
            _log.warn("Expect to have child name when creating a child image");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing child name in the request");
        }

        Long retVal = dataService.insertChildImage(entity);

        if(retVal == null){
            _log.error("failed to insert the child image!");
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, "failed to insert the child image");
        }

        return new CreateResponse(retVal);
    }

    @RestMethod.Delete
    public UpdateResponse deleteEntry(ComplexResourceKey<ChildId, ChildId> key) {

        // Check whether a proper token is attached to to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        boolean deleted = dataService.deleteChildImage(key);

        if(deleted) {
            return new UpdateResponse(HttpStatus.S_200_OK);
        } else {
            _log.error("failed to delete the child image: " + key);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }
    }

    @RestMethod.Update
    public UpdateResponse updateEntry(ComplexResourceKey<ChildId, ChildId> key, ChildImage entity) {
        // Check whether a proper token is attached to to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        boolean updated = dataService.updateChildImage(key, entity);

        if(updated) {
            return new UpdateResponse(HttpStatus.S_200_OK);
        } else {
            _log.error("failed to update the child image: " + key);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }
    }

    @RestMethod.Get
    public ChildImage getImage(ComplexResourceKey<ChildId, ChildId> key) {

        // Check whether a proper token is attached to to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders());

        return dataService.getChildImage(key);
    }
}
