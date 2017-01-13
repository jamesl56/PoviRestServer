package com.antwish.povi.server.impl;

import com.antwish.povi.server.Comment;
import com.antwish.povi.server.CommentId;
import com.antwish.povi.server.EventType;
import com.antwish.povi.server.PoviEvent;
import com.antwish.povi.server.VoiceComment;
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


@RestLiCollection(name = "voiceComment", namespace = "com.antwish.povi.server")
public class VoiceCommentResource extends ComplexKeyResourceTemplate<CommentId, CommentId, VoiceComment> {

    Logger _log = LoggerFactory.getLogger(VoiceCommentResource.class);
    private static SocialPlayDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @RestMethod.Create
    public CreateResponse createEntry(VoiceComment entity) {

        // Check whether a proper token is attached to to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        if(!entity.hasChildName()){
            _log.warn("missing child name");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing child name in request");
        }

        if(!entity.hasFileContent()){
            _log.warn("missing voice comment content");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing voice comment content in request");
        }

        Long retVal = dataService.insertVoiceComment(entity);

        if(retVal == null){
            _log.error("failed to insert the voice comment: " + entity);
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, "failed to insert the voice comment");
        }

        return new CreateResponse(retVal);
    }

    @RestMethod.Delete
    public UpdateResponse deleteEntry(ComplexResourceKey<CommentId, CommentId> key) {

        // Check whether a proper token is attached to to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        boolean deleted = dataService.deleteVoiceComment(key);

        if(deleted) {
            return new UpdateResponse(HttpStatus.S_200_OK);
        } else {
            _log.error("failed to delete voice comment: " + key);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }
    }

    @RestMethod.Update
    public UpdateResponse updateEntry(ComplexResourceKey<CommentId, CommentId> key, VoiceComment entity) {
        // Check whether a proper token is attached to to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        boolean updated = dataService.updateVoiceComment(key, entity);

        if(updated) {
            return new UpdateResponse(HttpStatus.S_200_OK);
        } else {
            _log.error("failed to update voice comment: " + key);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }
    }

    @RestMethod.Get
    public VoiceComment getComment(ComplexResourceKey<CommentId, CommentId> key) {

        // Check whether a proper token is attached to to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders());

        return dataService.getVoiceComment(key);
    }
}