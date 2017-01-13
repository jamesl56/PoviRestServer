package com.antwish.povi.server.impl;

import com.antwish.povi.server.Comment;
import com.antwish.povi.server.CommentId;
import com.antwish.povi.server.EventType;
import com.antwish.povi.server.PoviEvent;
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


@RestLiCollection(name = "tipComment", namespace = "com.antwish.povi.server")
public class CommentResource extends ComplexKeyResourceTemplate<CommentId, CommentId, Comment> {
    Logger _log = LoggerFactory.getLogger(CommentResource.class);
    private static SocialPlayDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @RestMethod.Create
    public CreateResponse createEntry(Comment entity) {

        // Check whether a proper token is attached to to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        if(!entity.hasChildName())
        {
            _log.warn("Missing child name in request");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing child name in request");
        }

        if(!entity.hasCommentText())
        {
            _log.warn("Missing comment text in request");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing comment text in request");
        }

        if(!entity.hasTipId() || !entity.hasResourceId())
        {
            _log.warn("Missing tip ID or resource ID in request");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing tip ID or resource ID in request");
        }

        Long commentId = dataService.insertComment(entity);

        if(commentId != null) {
            String details = "inserted #" + commentId + " " + entity.getCommentText();
            insertCommentEvent(token, details);
            return new CreateResponse(commentId);
        }
        else {
            _log.error("failed to create comment: " + entity);
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, "failed to create comment!");
        }
    }

    @RestMethod.Delete
    public UpdateResponse deleteEntry(ComplexResourceKey<CommentId, CommentId> key) {

        // Check whether a proper token is attached to to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        boolean deleted = dataService.deleteComment(key);

        if(deleted) {
            // TODO: use commentId instead to make it more accurate
            String details = "deleted #" + key.getKey().getTimestamp();
            insertCommentEvent(token, details);
            return new UpdateResponse(HttpStatus.S_200_OK);
        } else {
            _log.error("failed to delete a comment: " + key);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }
    }
    
    @RestMethod.Update
    public UpdateResponse updateEntry(ComplexResourceKey<CommentId, CommentId> key, Comment entity) {
        // Check whether a proper token is attached to to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        boolean updated = dataService.updateComment(key, entity);

        if(updated) {
            String details = "updated #" + key.getKey().getTimestamp();
            insertCommentEvent(token, details);
            return new UpdateResponse(HttpStatus.S_200_OK);
        } else {
            _log.error("failed to update a comment: " + key + " and " + entity);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }
    }

    @RestMethod.Get
    public Comment getComment(ComplexResourceKey<CommentId, CommentId> key) {

        // Check whether a proper token is attached to to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders());

        return dataService.getComment(key);
    }

    @Finder("getCommentsPaged")
    public CollectionResult<Comment,CommentId> getCommentsPaged( @PagingContextParam PagingContext context,
                                                            @QueryParam("userId") String user_id,
                                                            @QueryParam("childName") String child_name,
                                                            @QueryParam("lastTimestamp") @Optional Long lastTimestamp)
    {

        // Check whether a proper token is attached to to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders());

        return dataService.getCommentsPaged(context, user_id, child_name, lastTimestamp);
    }

    @Finder("getCommentsLikedPaged")
    public CollectionResult<Comment,CommentId> getCommentsLikedPaged( @PagingContextParam PagingContext context,
                                                                 @QueryParam("userId") String user_id,
                                                                 @QueryParam("childName") String child_name,
                                                                 @QueryParam("lastTimestamp") @Optional Long lastTimestamp)
    {

        // Check whether a proper token is attached to to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders());

        return dataService.getCommentsLikedPaged(context, user_id, child_name, lastTimestamp);
    }

    private void insertCommentEvent(String token, String details){
        String email = dataService.getUserEmailFromToken(token);
        PoviEvent poviEvent = new PoviEvent().setEventType(EventType.COMMENT).setDetails(details).setEmail(email);
        dataService.insertPoviEvent(poviEvent);
    }
}