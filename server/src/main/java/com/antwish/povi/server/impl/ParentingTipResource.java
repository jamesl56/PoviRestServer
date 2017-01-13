package com.antwish.povi.server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antwish.povi.server.EventType;
import com.antwish.povi.server.PoviEvent;
import com.antwish.povi.server.SampleAnswer;
import com.linkedin.data.template.GetMode;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.ResourceLevel;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.annotations.Action;
import com.linkedin.restli.server.annotations.ActionParam;
import com.linkedin.restli.server.annotations.Optional;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.ComplexKeyResourceTemplate;
import com.antwish.povi.server.Account;
import com.antwish.povi.server.ParentingTip;
import com.antwish.povi.server.ParentingTipId;
import com.antwish.povi.server.ds.SocialPlayDataService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

@RestLiCollection(name = "parentingTip", namespace = "com.antwish.povi.server")
public class ParentingTipResource extends
        ComplexKeyResourceTemplate<ParentingTipId, Account, ParentingTip> {
    Logger _log = LoggerFactory.getLogger(ParentingTipResource.class);

    private static SocialPlayDataService dataService = null;
    private static final String BLANK = " ";
    public static Hashtable<ParentingTipId, List<SampleAnswer>> sampleAnswers = new Hashtable<ParentingTipId, List<SampleAnswer>>();
    public static List<String> weblinks = new ArrayList<String>();
    public static List<ParentingTip> tips = new ArrayList<ParentingTip>();
    public static Set<ParentingTipId> teenSet = new HashSet<ParentingTipId>();
    public static Set<ParentingTipId> five_to_TenSet = new HashSet<ParentingTipId>();
    public static Set<ParentingTipId> belowFiveSet = new HashSet<ParentingTipId>();
    public static final int[] like_offsets = new int[] {98, 99, 89, 89, 30, 52, 40, 83, 110, 69, 128, 37, 103, 83, 79, 79, 113, 89, 41, 118, 122, 89, 94, 73, 89, 50, 40, 114, 70, 43, 105, 42, 113, 95, 124, 82, 106, 56, 61, 47, 124, 126, 92, 97, 51, 59, 122, 48, 101, 81, 55, 71, 116, 76, 103, 31, 72, 85, 51, 109, 39, 36, 49, 128, 119, 112, 85, 114, 98, 56, 56, 117, 33, 50, 71, 125, 62, 75, 96, 129, 128, 92, 35, 71, 86, 50, 36, 38, 44, 74, 67, 103, 110, 40, 94, 80, 98, 70, 31, 58, 80, 104, 60, 117, 30, 36, 64, 106, 122, 84, 32, 68, 124, 107, 92, 92, 124, 113, 105, 92, 38, 30, 42, 80, 82, 103, 35, 64, 103, 50, 58, 33, 68, 68, 54, 99, 89, 76, 87, 91, 112, 58, 108, 99, 84, 97, 51, 31};
    public static final int default_like_offset = 67;
    public static final int[] comment_offsets = new int[] {25, 25, 54, 41, 32, 27, 20, 25, 43, 57, 50, 27, 16, 29, 35, 46, 24, 37, 55, 32, 30, 37, 22, 26, 23, 55, 20, 38, 31, 52, 29, 44, 57, 34, 51, 60, 48, 25, 11, 14, 36, 14, 56, 14, 57, 50, 13, 28, 58, 13, 44, 45, 34, 17, 25, 40, 59, 19, 54, 23, 35, 59, 38, 48, 15, 14, 56, 60, 48, 24, 49, 39, 44, 59, 16, 55, 11, 60, 15, 18, 39, 18, 13, 43, 55, 40, 53, 43, 35, 59, 21, 38, 58, 25, 29, 53, 48, 52, 59, 60, 28, 48, 53, 40, 57, 47, 39, 45, 15, 26, 19, 11, 13, 37, 11, 38, 48, 12, 34, 11, 59, 52, 14, 36, 39, 20, 60, 28, 42, 12, 26, 27, 11, 18, 23, 19, 31, 40, 50, 52, 49, 24, 44, 38, 11, 23, 30, 17};
    public static final int default_comment_offset = 31;


    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
        sampleAnswers = dataService.populateSampleAnswers();
        weblinks = dataService.getWebLinks();
        tips = dataService.getAllActiveTips();

        for(ParentingTip tip:tips){
            if((0x1 & tip.getTipAgeGroups().intValue()) == 0x1 ){
                belowFiveSet.add(tip.getTipId());
            }
            if((0x2 & tip.getTipAgeGroups().intValue()) == 0x2 ){
                five_to_TenSet.add(tip.getTipId());
            }
            if((0x4 & tip.getTipAgeGroups().intValue()) == 0x4 ){
                teenSet.add(tip.getTipId());
            }
        }
    }

    @Override
    public ParentingTip get(ComplexResourceKey<ParentingTipId, Account> tipId) {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        insertTipEvent(token, tipId.getParams().getAccountId() + " get tip: " + tipId.getKey().toString());
        return dataService.getTip(tipId.getKey(), tipId.getParams().getAccountId());
    }

    @Action(name = "getTip", resourceLevel = ResourceLevel.COLLECTION)
    public ParentingTip getTip() {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        ParentingTip parentingTip = dataService.getRandomTip();

        if(parentingTip != null && parentingTip.getTipId(GetMode.NULL) != null) {
            insertTipEvent(token, "get tip: " + parentingTip.getTipId().toString());
        } else {
            _log.error("getTip fails to return anything!");
        }
        return parentingTip;
    }

    @Action(name = "getTips", resourceLevel = ResourceLevel.COLLECTION)
    public ParentingTip[] getTips(@ActionParam("userId") String userId,
                                  @ActionParam("dateStr") String dateStr,
                                  @ActionParam("count") @Optional Integer count) {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        if (userId == null || userId.isEmpty()) {
            _log.debug("getTips: null or empty userId!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST,
                    "getTips: null or empty userId!");
        }

        if (ServerUtils.convertFromDateString(dateStr) == null) {
            _log.debug("getTips: Invalid date string in request: " + dateStr);
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST,
                    "Invalid date string in request: " + dateStr);
        }

        ParentingTip[] tips = dataService.getRandomDailyTip(userId, dateStr, count);

        if (tips != null && tips.length > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append(userId);
            sb.append(" retrieved tips: ");
            for (ParentingTip tip : tips) {
                sb.append(tip.getTipId().toString());
                sb.append(BLANK);
            }

            insertTipEvent(token, sb.toString());
        }
        return tips;
    }

    @Action(name = "getRefreshTips", resourceLevel = ResourceLevel.COLLECTION)
    public ParentingTip[] getRefreshTips(@ActionParam("userId") String userId,
                                         @ActionParam("count") @Optional Integer count) {

        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        ParentingTip[] tips = dataService.getRefreshTips(userId, count);

        if (tips != null && tips.length > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append(userId);
            sb.append(" refreshed tips: ");
            for (ParentingTip tip : tips) {
                sb.append(tip.getTipId().toString());
                sb.append(BLANK);
            }

            insertTipEvent(token, sb.toString());
        }
        return tips;
    }

    @Action(name = "getTipsSelectedDay", resourceLevel = ResourceLevel.COLLECTION)
    public ParentingTip[] getTipsSelectedDay(@ActionParam("userId") String userId,
                                             @ActionParam("dateStr") String dateStr,
                                             @ActionParam("count") @Optional Integer count) {

        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        if (userId == null || userId.isEmpty()) {
            _log.error("getTipsSelectedDay: null or empty userId!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST,
                    "getTipsSelectedDay: null or empty userId!");
        }

        if (ServerUtils.convertFromDateString(dateStr) == null) {
            _log.debug("getTipsSelectedDay: Invalid date string in request: " + dateStr);
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST,
                    "Invalid date string in request: " + dateStr);
        }

        ParentingTip[] tips = dataService.getTipsSelectedDay(userId, dateStr, count);

        if (tips != null && tips.length > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append(userId);
            sb.append(" selected date tips: ");
            for (ParentingTip tip : tips) {
                sb.append(tip.getTipId().toString());
                sb.append(BLANK);
            }

            insertTipEvent(token, sb.toString());
        }
        return tips;
    }

    @Action(name = "getWebLink", resourceLevel = ResourceLevel.COLLECTION)
    public String getWebLink() {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders());

        return dataService.getWebLink();
    }

    private void insertTipEvent(String token, String details) {
        String email = dataService.getUserEmailFromToken(token);
        PoviEvent poviEvent = new PoviEvent().setEventType(EventType.TIPOFTHEDAY).setDetails(details).setEmail(email);
        dataService.insertPoviEvent(poviEvent);
    }
}
