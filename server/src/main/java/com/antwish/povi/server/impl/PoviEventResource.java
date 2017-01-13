package com.antwish.povi.server.impl;

import com.antwish.povi.server.PoviEvent;
import com.antwish.povi.server.ds.SocialPlayDataService;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.CreateResponse;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoviEventResource extends
        CollectionResourceTemplate<Long, PoviEvent> {
    Logger _log = LoggerFactory.getLogger(PoviEventResource.class);
    private static SocialPlayDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @Override
    public CreateResponse create(PoviEvent entity) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders());

        if (entity.hasEventId()) {
            _log.warn("Event ID must NOT present in request");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST,
                    "Event ID must NOT present in request");
        }
        if (!entity.hasEmail()) {
            _log.warn("Email must NOT present in request");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST,
                    "Email must present in request");
        }

        Long eventId = dataService.insertPoviEvent(entity);
        if (eventId == null) {
            _log.error("failed to insert an event: " + entity);
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR,
                    "failed to insert an event");
        }
        return new CreateResponse(eventId);
    }

}
