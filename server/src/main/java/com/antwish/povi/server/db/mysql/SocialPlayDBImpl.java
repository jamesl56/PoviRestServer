package com.antwish.povi.server.db.mysql;

import com.antwish.povi.server.Account;
import com.antwish.povi.server.ActivationRecord;
import com.antwish.povi.server.Child;
import com.antwish.povi.server.ChildId;
import com.antwish.povi.server.ChildImage;
import com.antwish.povi.server.Comment;
import com.antwish.povi.server.CommentId;
import com.antwish.povi.server.DaySchedule;
import com.antwish.povi.server.DayScheduleArray;
import com.antwish.povi.server.Event;
import com.antwish.povi.server.IdentityType;
import com.antwish.povi.server.ParentingComment;
import com.antwish.povi.server.ParentingTip;
import com.antwish.povi.server.ParentingTipId;
import com.antwish.povi.server.PoviEvent;
import com.antwish.povi.server.SampleAnswer;
import com.antwish.povi.server.SampleAnswerArray;
import com.antwish.povi.server.Settings;
import com.antwish.povi.server.SocialPlayContext;
import com.antwish.povi.server.User;
import com.antwish.povi.server.VoiceComment;
import com.antwish.povi.server.db.SocialPlayDB;
import com.antwish.povi.server.impl.ParentingTipResource;
import com.antwish.povi.server.impl.ServerUtils;
import com.linkedin.data.template.GetMode;
import com.linkedin.data.template.SetMode;
import com.linkedin.data.template.StringArray;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.server.CollectionResult;
import com.linkedin.restli.server.PagingContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SocialPlayDBImpl implements SocialPlayDB {

    private static Logger _log = LoggerFactory.getLogger(SocialPlayDBImpl.class);
    private Connection conn;
    private final String SELECT_LAST_ID = "SELECT LAST_INSERT_ID()";
    private final String SELECT_ROW_COUNT = "SELECT ROW_COUNT();";
    private static final int TOTAL_TIPS = 144;
    private static Random random = new Random();
    private static final int TIPS_COUNT = 3;
    private static final long MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000l;
    private static final int DEFAULT_COUNT = 10;
    private static final String ROOTDIR = "/private/tmp";
    private static final String DIR_SEPARATOR = "/";
    private static final String USERS = "users";
    private static final String CHILDREN = "children";
    private static final long FIVE_YEARS = 5 * 60 * 60 * 24 * 365 * 1000l;
    private static final long TEN_YEARS = 10 * 60 * 60 * 24 * 365 * 1000l;

    public SocialPlayDBImpl(String dbUrl, String dbName, String userName,
                            String password) {
        this.conn = DBUtilities
                .getConnection(dbUrl, dbName, userName, password);
    }

    @Override
    public Long insertPoviEvent(PoviEvent event) {
        PreparedStatement insertUser = null;

        String insertString = "insert into events (user_id, event_type, timestamp, duration, eventdetails) values (?, ?, utc_timestamp(), ?, ?)";

        try {
            conn.setAutoCommit(false);
            insertUser = conn.prepareStatement(insertString);

            insertUser.setString(1, event.getEmail());
            insertUser.setString(2,
                    DBUtilities.eventTypeToString(event.getEventType()));
            insertUser.setInt(3, event.hasDuration() ? event.getDuration() : 0);
            insertUser.setString(4, event.getDetails());
            insertUser.executeUpdate();
            conn.commit();

            PreparedStatement selectLastId = conn.prepareStatement(SELECT_LAST_ID);
            ResultSet rs = selectLastId.executeQuery();
            while (rs.next()) {
                Long eventId = rs.getLong(1);
                return eventId;
            }
        } catch (SQLException ex) {
            _log.error("insertPoviEvent SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        }catch (Exception ex) {
            _log.error(" insertPoviEvent Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (insertUser != null) {
                    insertUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    private SampleAnswerArray getSampleAnswers(ParentingTipId tipId){
        SampleAnswerArray answerArray = new SampleAnswerArray();
        if(ParentingTipResource.sampleAnswers.containsKey(tipId))
        {
            List<SampleAnswer> sampleAnswers = ParentingTipResource.sampleAnswers.get(tipId);

            int i = 0;
            // only store up to 2 sample answers
            while(i<2 && i<sampleAnswers.size()){
                answerArray.add(sampleAnswers.get(i++));
            }
        }

        return answerArray;
    }

    @Override
    public ParentingTip getTip(ParentingTipId tipId, Long accountId) {
        PreparedStatement selectUser = null;

        String selectString = "select p.content, p.age_groups, p.category, t.count, t.likeCount from parenting_tips p, tip_comment_status t where p.resource_id = ? and p.tip_id = ? and p.tip_id = t.tipId";

        try {
            selectUser = conn.prepareStatement(selectString);

            selectUser.setInt(1, tipId.getTipResourceId());
            selectUser.setInt(2, tipId.getTipSequenceId());
            ResultSet rs = selectUser.executeQuery();
            while (rs.next()) {
                ParentingTip tip = new ParentingTip();
                tip.setTipDetail(rs.getString(1))
                        .setTipId(tipId)
                        .setTipAgeGroups(rs.getInt(2))
                        .setTipCategory(DBUtilities.toParentingTipCategory(rs.getInt(3)))
                        .setCommentCount(rs.getInt(4) + (ParentingTipResource.comment_offsets.length >= tipId.getTipSequenceId() ? ParentingTipResource.comment_offsets[tipId.getTipSequenceId() - 1] : ParentingTipResource.default_comment_offset))
                        .setLikeCount(rs.getInt(5) + (ParentingTipResource.like_offsets.length >= tipId.getTipSequenceId() ? ParentingTipResource.like_offsets[tipId.getTipSequenceId() - 1] : ParentingTipResource.default_like_offset));
                tip.setSampleAnswers(getSampleAnswers(tipId), SetMode.IGNORE_NULL);
                insertTipHistory(tipId, accountId);
                return tip;
            }
        } catch (SQLException ex) {
            _log.error("getTip SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getTip Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return new ParentingTip();
    }

    private boolean insertTipHistory(String userId, String dateStr, int resource_id, int tipId) {
        PreparedStatement insertHistory = null;

        String insertString = "insert into parenting_tips_history (user_id, date, resource_id, tip_id) values (?, ?, ?, ?)";

        Long timestamp = ServerUtils.convertFromDateString(dateStr);
        if (timestamp == null)
            return false;

        try {
            conn.setAutoCommit(false);
            insertHistory = conn.prepareStatement(insertString);

            insertHistory.setString(1, userId);
            insertHistory.setDate(2, new java.sql.Date(timestamp));
            insertHistory.setInt(3, resource_id);
            insertHistory.setInt(4, tipId);
            insertHistory.executeUpdate();
            conn.commit();

            return true;
        } catch (SQLException ex) {
            _log.error("insertTipHistory SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" insertTipHistory Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                conn.setAutoCommit(true);
                if (insertHistory != null) {
                    insertHistory.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    private ParentingTip[] getTipsOfTheDay(String userId, String dateStr, Integer count) {
        List<Child> children = getChildren(userId);
        Set<ParentingTipId> idSet = new HashSet<ParentingTipId>();
        int ageGroup;
        if(children == null || children.size() == 0)
        {
            idSet.addAll(ParentingTipResource.belowFiveSet);
            idSet.addAll(ParentingTipResource.five_to_TenSet);
            idSet.addAll(ParentingTipResource.teenSet);
        }
        else {
            ageGroup = 0;
            long current = new Date().getTime();
            for (Child child : children) {
                if(current - child.getBirthdate() < FIVE_YEARS) {
                    idSet.addAll(ParentingTipResource.belowFiveSet);
                } else if (current - child.getBirthdate() < TEN_YEARS) {
                    idSet.addAll(ParentingTipResource.five_to_TenSet);
                } else {
                    idSet.addAll(ParentingTipResource.teenSet);
                }
            }
        }
        Set<ParentingTipId> tipSet = new HashSet<ParentingTipId>(count);
        ParentingTipId[] tipIds = idSet.toArray(new ParentingTipId[idSet.size()]);
        for (int i = 0; i < count; i++) {
            tipSet = getNewTipIdSet(tipIds, tipSet);
        }

        ParentingTip[] tips = new ParentingTip[count];
        int index = 0;
        for (ParentingTipId tipId : tipSet) {
            // add entries to the history table and also populate the results
            if (insertTipHistory(userId, dateStr, tipId.getTipResourceId(), tipId.getTipSequenceId()))
                tips[index++] = getTip(tipId);
        }

        return tips;
    }

    private Set<ParentingTipId> getNewTipIdSet(ParentingTipId[] tipIds, Set<ParentingTipId> currentIds)
    {
        while (true) {
            int tipId = random.nextInt(tipIds.length);
            if (!currentIds.contains(tipIds[tipId])) {
                currentIds.add(tipIds[tipId]);

                return currentIds;
            }
        }
    }
    private Set<Integer> getNewTipId(Set<Integer> tipSet) {
        while (true) {
            int tipId = random.nextInt(TOTAL_TIPS) + 1;
            if (!tipSet.contains(tipId)) {
                tipSet.add(tipId);

                return tipSet;
            }
        }
    }

    @Override
    public ParentingTip[] getRandomDailyTip(String userId, String dateStr, Integer count) {
        if (count == null)
            count = TIPS_COUNT;

        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
        Long timestamp = ServerUtils.convertFromDateString(dateStr);
        if (timestamp == null)
            return new ParentingTip[0];

        ParentingTip[] tips = new ParentingTip[count];
        PreparedStatement selectUser = null;

        String selectString = "select h.date, h.resource_id, h.tip_id, p.content, p.age_groups, p.category, t.count, t.likeCount from parenting_tips_history h, parenting_tips p, tip_comment_status t where h.user_id=? and h.resource_id = p.resource_id and h.tip_id = p.tip_id and h.tip_id = t.tipId order by h.date desc, h.history_id desc limit " + count;

        try {
            selectUser = conn.prepareStatement(selectString);

            selectUser.setString(1, userId);
            ResultSet rs = selectUser.executeQuery();
            int i = count;
            if (rs.first()) {
                do {
                    java.sql.Date lastdate = rs.getDate(1);
                    if (timestamp - lastdate.getTime() >= MILLISECONDS_IN_A_DAY) {
                        // not yet retrieve the tips of the day yet
                        return getTipsOfTheDay(userId, dateStr, count);
                    }
                    ParentingTip tip = new ParentingTip();
                    ParentingTipId tipId = new ParentingTipId().setTipResourceId(rs.getInt(2)).setTipSequenceId(rs.getInt(3));
                    tip.setTipDetail(rs.getString(4))
                            .setTipAgeGroups(rs.getInt(5))
                            .setTipCategory(DBUtilities.toParentingTipCategory(rs.getInt(6)))
                            .setCommentCount(rs.getInt(7) + (ParentingTipResource.comment_offsets.length >= rs.getInt(3) ? ParentingTipResource.comment_offsets[rs.getInt(3) - 1] : ParentingTipResource.default_comment_offset))
                            .setLikeCount(rs.getInt(8) + (ParentingTipResource.like_offsets.length >= rs.getInt(3) ? ParentingTipResource.like_offsets[rs.getInt(3) - 1] : ParentingTipResource.default_like_offset))
                            .setTipId(tipId)
                            .setSampleAnswers(getSampleAnswers(tipId), SetMode.IGNORE_NULL);
                    tips[--i] = tip;
                    if (i == 0)
                        break;        // extra measure to make sure it doesn't cause out-of-boundry
                } while (rs.next());
            } else {
                // no history for this user yet
                return getTipsOfTheDay(userId, dateStr, count);
            }

            return tips;
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getRandomDailyTip Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return new ParentingTip[0];
    }

    @Override
    public ParentingTip[] getTipsSelectedDay(String userId, String dateStr, Integer count) {
        if (count == null)
            count = TIPS_COUNT;

        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
        Long timestamp = ServerUtils.convertFromDateString(dateStr);
        if (timestamp == null)
            return new ParentingTip[0];

        ParentingTip[] tips = new ParentingTip[count];
        PreparedStatement selectUser = null;

        String selectString = "select h.date, h.resource_id, h.tip_id, p.content, p.age_groups, p.category, t.count, t.likeCount from parenting_tips_history h, parenting_tips p, tip_comment_status t where h.user_id=? and h.date = ? and h.resource_id = p.resource_id and h.tip_id = p.tip_id and p.tip_id = t.tipId order by h.history_id desc limit " + count;

        try {
            selectUser = conn.prepareStatement(selectString);

            selectUser.setString(1, userId);
            selectUser.setDate(2, new java.sql.Date(timestamp));

            ResultSet rs = selectUser.executeQuery();
            int i = count;
            if (rs.first()) {
                do {
                    ParentingTip tip = new ParentingTip();
                    ParentingTipId tipId = new ParentingTipId().setTipResourceId(rs.getInt(2)).setTipSequenceId(rs.getInt(3));
                    tip.setTipDetail(rs.getString(4))
                            .setTipAgeGroups(rs.getInt(5))
                            .setTipCategory(DBUtilities.toParentingTipCategory(rs.getInt(6)))
                            .setCommentCount(rs.getInt(7) + (ParentingTipResource.comment_offsets.length >= rs.getInt(3) ? ParentingTipResource.comment_offsets[rs.getInt(3) - 1] : ParentingTipResource.default_comment_offset))
                            .setLikeCount(rs.getInt(8) + (ParentingTipResource.like_offsets.length >= rs.getInt(3) ? ParentingTipResource.like_offsets[rs.getInt(3) - 1] : ParentingTipResource.default_like_offset))
                            .setTipId(tipId)
                            .setSampleAnswers(getSampleAnswers(tipId), SetMode.IGNORE_NULL);
                    tips[--i] = tip;
                    if (i == 0)
                        break;        // extra measure to make sure it doesn't cause out-of-boundry
                } while (rs.next());
            } else {
                // no history for this user yet
                return getTipsOfTheDay(userId, dateStr, count);
            }

            return tips;
        } catch (SQLException ex) {
            _log.error("getTipsSelectedDay SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getTipsSelectedDay Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return new ParentingTip[0];
    }

    @Override
    public ParentingTip[] getRefreshTips(String userId, Integer count){
        // Allocating default tipCount
        if (count == null)
            count = TIPS_COUNT;

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String currentDate = sdf.format(cal.getTime());

        return getTipsOfTheDay(userId, currentDate, count);
    }

    private ParentingTip getTip(ParentingTipId tipId) {
        PreparedStatement selectUser = null;

        String selectString = "select p.content, p.age_groups, p.category, t.count, t.likeCount from parenting_tips p, tip_comment_status t where p.resource_id = ? and p.tip_id = ? and p.tip_id = t.tipId";

        try {
            selectUser = conn.prepareStatement(selectString);

            selectUser.setInt(1, tipId.getTipResourceId());
            selectUser.setInt(2, tipId.getTipSequenceId());
            ResultSet rs = selectUser.executeQuery();
            while (rs.next()) {
                ParentingTip tip = new ParentingTip();
                tip.setTipDetail(rs.getString(1))
                        .setTipAgeGroups(rs.getInt(2))
                        .setTipCategory(DBUtilities.toParentingTipCategory(rs.getInt(3)))
                        .setTipId(tipId)
                        .setCommentCount(rs.getInt(4) + (ParentingTipResource.comment_offsets.length >= tipId.getTipSequenceId() ? ParentingTipResource.comment_offsets[tipId.getTipSequenceId() - 1] : ParentingTipResource.default_comment_offset))
                        .setLikeCount(rs.getInt(5) + (ParentingTipResource.like_offsets.length >= tipId.getTipSequenceId() ? ParentingTipResource.like_offsets[tipId.getTipSequenceId() - 1] : ParentingTipResource.default_like_offset));
                tip.setSampleAnswers(getSampleAnswers(tipId), SetMode.IGNORE_NULL);
                return tip;
            }
        } catch (SQLException ex) {
            _log.error("getTip SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getTip Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return new ParentingTip();
    }



    @Override
    public ParentingTip getRandomTip() {
        return getTip(new ParentingTipId().setTipResourceId(2).setTipSequenceId(random.nextInt(TOTAL_TIPS) + 1));
    }

    private boolean insertTipHistory(ParentingTipId tipId, Long accountId) {
        PreparedStatement insertUser = null;

        String insertString = "insert into parenting_tips_history (user_id, timestamp, resource_id, tip_id) values (?, utc_timestamp(), ?, ?)";

        try {
            conn.setAutoCommit(false);
            insertUser = conn.prepareStatement(insertString);

            insertUser.setLong(1, accountId);
            insertUser.setInt(2, tipId.getTipResourceId());
            insertUser.setInt(3, tipId.getTipSequenceId());
            insertUser.executeUpdate();
            conn.commit();
            return true;
        } catch (SQLException ex) {
            _log.error("insertTipHistory SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error("insertTipHistory encountered problem: " + ex.getStackTrace());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }
            finally
        {
            try {
                conn.setAutoCommit(true);
                if (insertUser != null) {
                    insertUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }
        return false;
    }

    @Override
    public String getUserEmailFromToken(String token) {
        PreparedStatement selectSettings = null;

        String selectString = "select user_id from povi_schema.sessions where token_povi=?;";

        try {
            selectSettings = conn.prepareStatement(selectString);

            selectSettings.setString(1, token);
            ResultSet rs = selectSettings.executeQuery();
            while (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getUserEmailFromToken Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                if (selectSettings != null) {
                    selectSettings.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public Child getChild(String user_id, String childName) {
        PreparedStatement selectSettings = null;

        String selectString = "select gender, birthdate from children where user_id=? and name=?;";

        try {
            selectSettings = conn.prepareStatement(selectString);

            selectSettings.setString(1, user_id);
            selectSettings.setString(2, childName);
            ResultSet rs = selectSettings.executeQuery();
            if (rs.first()) {
                Child child = new Child().setName(childName).setUser_id(user_id);
                child.setGender(rs.getString(1)).setBirthdate(rs.getDate(2).getTime());

                return child;
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getChild Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                if (selectSettings != null) {
                    selectSettings.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public List<Child> getChildren(String user_id) {
        PreparedStatement selectSettings = null;

        String selectString = "select gender, birthdate, name from children where user_id=?;";

        try {
            selectSettings = conn.prepareStatement(selectString);

            selectSettings.setString(1, user_id);
            ResultSet rs = selectSettings.executeQuery();
            List<Child> children = new ArrayList<Child>();
            while (rs.next()) {
                Child child = new Child().setUser_id(user_id);
                child.setGender(rs.getString(1)).setBirthdate(rs.getDate(2).getTime()).setName(rs.getString(3));
                children.add(child);
            }

            return children;
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getChildren Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                if (selectSettings != null) {
                    selectSettings.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public Boolean addChild(Child entity) {
        PreparedStatement insertchild = null;

        String insertString = "insert into children (user_id, name, gender, birthdate, lastupdatetime) values (?, ?, ?, ?, utc_timestamp())";

        try {
            conn.setAutoCommit(false);
            insertchild = conn.prepareStatement(insertString);

            insertchild.setString(1, entity.getUser_id());
            insertchild.setString(2, entity.getName());
            insertchild.setString(3, entity.getGender());
            insertchild.setDate(4, new java.sql.Date(entity.getBirthdate()));
            insertchild.executeUpdate();
            conn.commit();

            return true;
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" addChild Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                conn.setAutoCommit(true);
                if (insertchild != null) {
                    insertchild.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public boolean updateChild(String user_id, String childName, Child entity) {
        PreparedStatement updateChild = null;

        String updateString = "update children set name=?, gender=?, birthdate=?, lastupdatetime=utc_timestamp(), localImageFile=?, remoteImageFile=? where user_id=? and name=?";

        try {
            updateChild = conn.prepareStatement(updateString);

            updateChild.setString(1, entity.getName());
            updateChild.setString(2, entity.getGender());
            updateChild.setDate(3, new java.sql.Date(entity.getBirthdate()));
            updateChild.setString(4, entity.getLocalImageFile(GetMode.NULL));
            updateChild.setString(5, entity.getRemoteImageFile(GetMode.NULL));
            updateChild.setString(6, entity.getUser_id());
            updateChild.setString(7, childName);

            updateChild.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            if (rs.first() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" updateChild Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                conn.setAutoCommit(true);
                if (updateChild != null) {
                    updateChild.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public boolean deleteChild(String user_id, String childName) {
        PreparedStatement deleteChild = null;

        String deleteString = "delete from children where user_id=? and name=?";

        try {
            conn.setAutoCommit(false);
            deleteChild = conn.prepareStatement(deleteString);

            deleteChild.setString(1, user_id);
            deleteChild.setString(2, childName);
            deleteChild.executeUpdate();
            conn.commit();

            return true;
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" deleteChild Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                conn.setAutoCommit(true);
                if (deleteChild != null) {
                    deleteChild.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public User getUser(String token) {
        PreparedStatement selectUser = null;

        String selectString = "select u.email, u.phone, u.name, u.address, u.birthdate, u.hash, u.gender from users u, sessions s where u.email=s.user_id and s.token_povi=?;";

        try {
            selectUser = conn.prepareStatement(selectString);

            selectUser.setString(1, token);
            ResultSet rs = selectUser.executeQuery();
            if (rs.first()) {
                User user = new User();
                user.setEmail(rs.getString(1))
                        .setPhone(rs.getString(2), SetMode.IGNORE_NULL)
                        .setName(rs.getString(3), SetMode.IGNORE_NULL)
                        .setAddress(rs.getString(4), SetMode.IGNORE_NULL)
                        .setBirthdate(rs.getDate(5).getTime(), SetMode.IGNORE_NULL)
                        .setHash(rs.getString(6), SetMode.IGNORE_NULL)
                        .setGender(rs.getString(7));

                return user;
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public User getUserFromEmail(String email) {
        PreparedStatement selectUser = null;

        String selectString = "select email, phone, name, address, birthdate, hash from users where email=?;";

        try {
            selectUser = conn.prepareStatement(selectString);

            selectUser.setString(1, email);
            ResultSet rs = selectUser.executeQuery();
            if (rs.first()) {
                User user = new User();
                user.setEmail(rs.getString(1))
                        .setPhone(rs.getString(2), SetMode.IGNORE_NULL)
                        .setName(rs.getString(3), SetMode.IGNORE_NULL)
                        .setAddress(rs.getString(4), SetMode.IGNORE_NULL)
                        .setHash(rs.getString(6), SetMode.IGNORE_NULL)
                        .setBirthdate(rs.getDate(5).getTime(), SetMode.IGNORE_NULL);

                return user;
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getUserFromEmail Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public boolean addUser(User entry) {
        PreparedStatement insertUser = null;

        String insertString = "insert into users (email, hash, phone, name, address, birthdate, lastupdatetime, gender) values (?, ?, ?, ?, ?, ?, utc_timestamp(), ?)";

        try {
            conn.setAutoCommit(false);
            insertUser = conn.prepareStatement(insertString);

            insertUser.setString(1, entry.getEmail());
            insertUser.setString(2, entry.getHash());
            insertUser.setString(3, entry.getPhone(GetMode.NULL) == null ? "" : entry.getPhone());
            insertUser.setString(4, entry.getName(GetMode.NULL) == null ? "" : entry.getName());
            insertUser.setString(5, entry.getAddress());
            insertUser.setDate(6, new java.sql.Date(entry.getBirthdate(GetMode.NULL) == null ? 0 : entry.getBirthdate()));
            insertUser.setString(7, entry.getGender(GetMode.NULL) == null ? "unspecified" : entry.getGender());
            insertUser.executeUpdate();
            conn.commit();

            return true;
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" addUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }finally {
            try {
                conn.setAutoCommit(true);
                if (insertUser != null) {
                    insertUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public boolean deleteUser(String token) {
        PreparedStatement deleteUser = null;

        String deleteString = "delete from users where email = (select user_id from sessions where token_povi=?)";

        try {
            conn.setAutoCommit(false);
            deleteUser = conn.prepareStatement(deleteString);

            deleteUser.setString(1, token);
            deleteUser.executeUpdate();
            conn.commit();

            return true;
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" deleteUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (deleteUser != null) {
                    deleteUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public boolean updateUser(User entry, String email) {
        PreparedStatement updateUser = null;

        String updateString = "update users set hash=?, phone=?, name=?, address=?, birthdate=?, lastupdatetime=utc_timestamp(), email=?, gender=? where email=?";

        try {
            conn.setAutoCommit(false);
            updateUser = conn.prepareStatement(updateString);

            updateUser.setString(1, entry.getHash());
            updateUser.setString(2, entry.getPhone(GetMode.NULL) == null ? "" : entry.getPhone());    // add extra guard as this is NOT nullable
            updateUser.setString(3, entry.getName(GetMode.NULL) == null ? "" : entry.getName()); // add extra guard as this is NOT nullable
            updateUser.setString(4, entry.getAddress(GetMode.NULL));
            updateUser.setDate(5, new java.sql.Date(entry.getBirthdate(GetMode.NULL) == null ? 0 : entry.getBirthdate()));
            updateUser.setString(6, entry.getEmail());
            updateUser.setString(7, entry.getGender(GetMode.NULL) == null ? "unspecified" : entry.getGender());
            updateUser.setString(8, email);
            updateUser.executeUpdate();
            conn.commit();

            return true;
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" updateUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (updateUser != null) {
                    updateUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    private String getHash(String email) {
        PreparedStatement selectUser = null;

        String selectString = "select hash from users where email=?";

        try {
            selectUser = conn.prepareStatement(selectString);

            selectUser.setString(1, email);
            ResultSet rs = selectUser.executeQuery();
            if (rs.first()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getHash Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public String loginEmail(String email, String hash) {
        String queryHash = getHash(email);

        String newToken = null;
        if (hash.compareTo(queryHash) == 0) {
            // The hash matches
            // Generate a new session token
            newToken = ServerUtils.generateToken(email);

            // Update token
            if (!updateToken(email, newToken))
                return null;
        }

        return newToken;
    }

    @Override
    public boolean updateToken(String email, String newToken) {
        PreparedStatement selectUser = null;

        String selectString = "select * from sessions where user_id=?";

        String queryHash = null;
        try {
            selectUser = conn.prepareStatement(selectString);

            selectUser.setString(1, email);
            ResultSet rs = selectUser.executeQuery();
            if (rs.first()) {
                return updateSessions(email, newToken);
            } else {
                return insertIntoSessions(email, newToken);
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" updateToken Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    public boolean insertIntoSessions(String email, String token) {
        PreparedStatement insertSessions = null;

        String insertString = "INSERT INTO sessions (user_id, token_povi, token_fb, login_time, lastupdatetime) VALUE (?, ?, ?, ?, utc_timestamp())";

        try {
            conn.setAutoCommit(false);
            insertSessions = conn.prepareStatement(insertString);

            insertSessions.setString(1, email);
            insertSessions.setString(2, token);
            insertSessions.setString(3, null);
            insertSessions.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            insertSessions.executeUpdate();
            conn.commit();

            return true;
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" insertIntoSessions Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (insertSessions != null) {
                    insertSessions.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    public boolean updateSessions(String email, String token) {
        PreparedStatement updateSessions = null;

        String updateString = "UPDATE sessions SET token_povi=?, login_time=?, lastupdatetime=utc_timestamp() WHERE user_id=?;";

        try {
            conn.setAutoCommit(false);
            updateSessions = conn.prepareStatement(updateString);

            updateSessions.setString(1, token);
            updateSessions.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            updateSessions.setString(3, email);

            updateSessions.executeUpdate();
            conn.commit();

            return true;
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" updateSessions Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (updateSessions != null) {
                    updateSessions.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public boolean validateToken(String token) {
        PreparedStatement selectUser = null;

        String selectString = "select * from sessions where token_povi=?;";

        String queryHash = null;
        try {
            selectUser = conn.prepareStatement(selectString);

            selectUser.setString(1, token);
            ResultSet rs = selectUser.executeQuery();
            if (rs.first()) {
                return true;
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" validateToken Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public boolean logout(String token) {
        PreparedStatement deleteSessions = null;

        String deleteString = "delete from sessions where token_povi=?";

        try {
            deleteSessions = conn.prepareStatement(deleteString);

            deleteSessions.setString(1, token);
            deleteSessions.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) > 0)
                    return true;
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" logout Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (deleteSessions != null) {
                    deleteSessions.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public String loginFacebook(String facebookToken) {
        String[] facebookInfo = ServerUtils.getInfoFromFacebook(facebookToken);

        if (facebookInfo == null || facebookInfo.length == 0)
            return null;
        String hash = getHash(facebookInfo[0]);

        if (hash == null) {
            // new user
            User user = new User().setEmail(facebookInfo[0]).setName(facebookInfo[1]).setHash("pwd").setAddress("0").setPhone("0").setBirthdate(System.currentTimeMillis());
            if (addUser(user))
                return null;
        }

        String newToken = ServerUtils.generateToken(facebookInfo[0]);

        if (!updateToken(facebookInfo[0], newToken))
            return null;

        return newToken;

    }

    @Override
    public Long insertComment(Comment entity) {
        PreparedStatement insertComment = null;

        String insertString = "insert into comments (userId, tipId, tipString, timestamp, commentText, likeStatus, childName, localVoiceFile, remoteVoiceFile, resourceId)" +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);
            insertComment = conn.prepareStatement(insertString);

            insertComment.setString(1, entity.getUserId());
            insertComment.setInt(2, entity.getTipId());
            insertComment.setString(3, entity.getTipString());
            insertComment.setLong(4, entity.getTimestamp());
            insertComment.setString(5, entity.getCommentText());
            insertComment.setBoolean(6, entity.isLikeStatus());
            insertComment.setString(7, entity.getChildName());
            insertComment.setString(8, entity.getLocalFileName(GetMode.NULL));
            insertComment.setString(9, entity.getRemoteFileName(GetMode.NULL));
            insertComment.setInt(10, entity.getResourceId());
            insertComment.executeUpdate();
            conn.commit();

            PreparedStatement selectLastId = conn.prepareStatement(SELECT_LAST_ID);
            ResultSet rs = selectLastId.executeQuery();
            while (rs.next()) {
                Long commentId = rs.getLong(1);
                return commentId;
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" insertComment Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (insertComment != null) {
                    insertComment.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public Boolean deleteComment(ComplexResourceKey<CommentId, CommentId> key) {
        PreparedStatement deleteComment = null;

        String deleteString = "delete from comments where userId=? AND childName=? AND timestamp=?";

        try {
            deleteComment = conn.prepareStatement(deleteString);

            deleteComment.setString(1, key.getKey().getUser_id());
            deleteComment.setString(2, key.getKey().getChild_Id());
            deleteComment.setLong(3, key.getKey().getTimestamp());
            deleteComment.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) > 0)
                    return true;
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" deleteComment Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (deleteComment != null) {
                    deleteComment.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public Boolean updateComment(ComplexResourceKey<CommentId, CommentId> key, Comment entity) {
        PreparedStatement updateComment = null;

        String updateString = "update comments set commentText=?, likeStatus=?, localVoiceFile=?, remoteVoiceFile=? where userId=? AND childName=? AND timestamp=?";

        try {
            updateComment = conn.prepareStatement(updateString);
            updateComment.setString(1, entity.getCommentText());
            updateComment.setBoolean(2, entity.isLikeStatus());
            updateComment.setString(3, entity.getLocalFileName(GetMode.NULL));
            updateComment.setString(4, entity.getRemoteFileName(GetMode.NULL));
            updateComment.setString(5, key.getKey().getUser_id());
            updateComment.setString(6, key.getKey().getChild_Id());
            updateComment.setLong(7, key.getKey().getTimestamp());
            updateComment.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            if (rs.first() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" updateComment Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (updateComment != null) {
                    updateComment.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public Comment getComment(ComplexResourceKey<CommentId, CommentId> key) {
        PreparedStatement selectComment = null;

        String selectString = "select * from comments where userId=? AND childName=? AND timestamp=?;";

        try {
            selectComment = conn.prepareStatement(selectString);

            selectComment.setString(1, key.getKey().getUser_id());
            selectComment.setString(2, key.getKey().getChild_Id());
            selectComment.setLong(3, key.getKey().getTimestamp());
            ResultSet resultSet = selectComment.executeQuery();

            if (resultSet.first()) {
                Comment comment = new Comment().setUserId(resultSet.getString("userId"))
                        .setTipId(resultSet.getInt("tipId"))
                        .setTipString(resultSet.getString("tipString"))
                        .setResourceId(resultSet.getInt("resourceId"))
                        .setTimestamp(resultSet.getLong("timestamp"))
                        .setCommentText(resultSet.getString("commentText"))
                        .setLikeStatus(resultSet.getBoolean("likeStatus"))
                        .setChildName(resultSet.getString("childName"))
                        .setLocalFileName(resultSet.getString("localVoiceFile"), SetMode.IGNORE_NULL)
                        .setRemoteFileName(resultSet.getString("remoteVoiceFile"), SetMode.IGNORE_NULL);

                return comment;
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getComment Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectComment != null) {
                    selectComment.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public CollectionResult<Comment, CommentId> getCommentsPaged(PagingContext context, String user_id, String child_name, Long lastTimestamp) {
        ArrayList<Comment> pageofcomments = null;
        int idx = 0;

        PreparedStatement selectComment = null;

        // set the number of rolls to be retrieved
        int count = context.getCount();
        if (count <= 0)
            count = DEFAULT_COUNT;
        String selectString = null;
        if (lastTimestamp == null) {
            selectString = "select * from comments where userId=? AND childName=? order by timestamp DESC limit " + count;
        } else {
            selectString = "select * from comments where userId=? AND childName=? AND timestamp < ? order by timestamp DESC limit " + count;
        }
        try {
            selectComment = conn.prepareStatement(selectString);
            if (lastTimestamp == null) {
                selectComment.setString(1, user_id);
                selectComment.setString(2, child_name);
            } else {
                selectComment.setString(1, user_id);
                selectComment.setString(2, child_name);
                selectComment.setLong(3, lastTimestamp);
            }
            ResultSet resultSet = selectComment.executeQuery();

            pageofcomments = new ArrayList<Comment>();

            while (resultSet.next()) {
                //Comment found
                pageofcomments.add(new Comment().setUserId(resultSet.getString("userId")).
                        setTipId(resultSet.getInt("tipId")).
                        setResourceId(resultSet.getInt("resourceId")).
                        setTipString(resultSet.getString("tipString")).
                        setTimestamp(resultSet.getLong("timestamp")).
                        setCommentText(resultSet.getString("commentText")).
                        setLikeStatus(resultSet.getBoolean("likeStatus")).
                        setChildName(resultSet.getString("childName")).
                        setLocalFileName(resultSet.getString("localVoiceFile"), SetMode.IGNORE_NULL).
                        setRemoteFileName(resultSet.getString("remoteVoiceFile"), SetMode.IGNORE_NULL));
                idx++;
                lastTimestamp = resultSet.getLong("timestamp");
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getCommentsPaged Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectComment != null) {
                    selectComment.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return new CollectionResult<Comment, CommentId>(pageofcomments, idx, idx == 0 ? null : new CommentId().setTimestamp(lastTimestamp));
    }

    @Override
    public CollectionResult<Comment, CommentId> getCommentsLikedPaged(PagingContext context, String user_id, String child_name, Long lastTimestamp) {
        ArrayList<Comment> pageofcomments = null;
        int idx = 0;

        PreparedStatement selectComment = null;

        // set the number of rolls to be retrieved
        int count = context.getCount();
        if (count <= 0)
            count = DEFAULT_COUNT;
        String selectString = null;
        if (lastTimestamp == null) {
            selectString = "select * from comments where userId=? AND childName=? AND likeStatus=? order by timestamp DESC limit " + count;
        } else {
            selectString = "select * from comments where userId=? AND childName=? AND likeStatus=? AND timestamp < ? order by timestamp DESC limit " + count;
        }
        try {
            selectComment = conn.prepareStatement(selectString);
            if (lastTimestamp == null) {
                selectComment.setString(1, user_id);
                selectComment.setString(2, child_name);
                selectComment.setBoolean(3, true);
            } else {
                selectComment.setString(1, user_id);
                selectComment.setString(2, child_name);
                selectComment.setBoolean(3, true);
                selectComment.setLong(4, lastTimestamp);
            }
            ResultSet resultSet = selectComment.executeQuery();

            pageofcomments = new ArrayList<Comment>();

            while (resultSet.next()) {
                //Comment found
                pageofcomments.add(new Comment().setUserId(resultSet.getString("userId")).
                        setTipId(resultSet.getInt("tipId")).
                        setResourceId(resultSet.getInt("resourceId")).
                        setTipString(resultSet.getString("tipString")).
                        setTimestamp(resultSet.getLong("timestamp")).
                        setCommentText(resultSet.getString("commentText")).
                        setLikeStatus(resultSet.getBoolean("likeStatus")).
                        setChildName(resultSet.getString("childName")).
                        setLocalFileName(resultSet.getString("localVoiceFile"), SetMode.IGNORE_NULL).
                        setRemoteFileName(resultSet.getString("remoteVoiceFile"), SetMode.IGNORE_NULL));
                idx++;
                lastTimestamp = resultSet.getLong("timestamp");
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getCommentsLikedPaged Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectComment != null) {
                    selectComment.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return new CollectionResult<Comment, CommentId>(pageofcomments, idx, idx == 0 ? null : new CommentId().setTimestamp(lastTimestamp));
    }


    @Override
    public Long insertVoiceComment(VoiceComment entity) {
        String remoteFileName = flushDataToDisk(entity);
        if (remoteFileName == null)
            return null;

        CommentId commentId = new CommentId().setUser_id(entity.getEmail()).setChild_Id(entity.getChildName()).setTimestamp(entity.getTimestamp());
        ComplexResourceKey<CommentId, CommentId> key = new ComplexResourceKey<CommentId, CommentId>(commentId, commentId);
        if (updateComment(key, getComment(key).setLocalFileName(entity.getFileName()).setRemoteFileName(remoteFileName)))
            return entity.getTimestamp();

        return null;
    }

    private String flushDataToDisk(Object entity) {
        String fileName = generateFileName(entity);

        if (fileName != null) {
            byte[] fileContent = null;
            if (entity instanceof VoiceComment) {
                fileContent = ((VoiceComment) entity).getFileContent().copyBytes();
            } else if (entity instanceof ChildImage) {
                fileContent = ((ChildImage) entity).getFileContent().copyBytes();
            } else
                return null;

            if (writeToFile(fileContent, fileName))
                return fileName;
        }

        return null;
    }

    private String generateFileName(Object entity) {
        String folderName = null;
        String fileName = null;
        if (entity instanceof VoiceComment) {
            folderName = ROOTDIR + DIR_SEPARATOR + USERS + DIR_SEPARATOR + ((VoiceComment) entity).getEmail();
            fileName = ((VoiceComment) entity).getFileName();
        } else if (entity instanceof ChildImage) {
            folderName = ROOTDIR + DIR_SEPARATOR + CHILDREN + DIR_SEPARATOR + ((ChildImage) entity).getEmail();
            fileName = ((ChildImage) entity).getFileName();
        }

        File folder = new File(folderName);
        if (!folder.exists()) {
            if (!folder.mkdirs())
                return null;
        }

        return folderName + DIR_SEPARATOR + fileName;
    }

    private boolean writeToFile(byte[] fileContent, String fileName) {
        final File tempFile = new File(fileName);
        FileOutputStream tmpOutputStream = null;
        try {
            tmpOutputStream = new FileOutputStream(tempFile);
            tmpOutputStream.write(fileContent);

            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (tmpOutputStream != null)
                try {
                    tmpOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public Boolean deleteVoiceComment(ComplexResourceKey<CommentId, CommentId> key) {
        return null;
    }

    @Override
    public Boolean updateVoiceComment(ComplexResourceKey<CommentId, CommentId> key, VoiceComment entity) {
        return null;
    }

    @Override
    public VoiceComment getVoiceComment(ComplexResourceKey<CommentId, CommentId> key) {
        return null;
    }

    @Override
    public Long insertChildImage(ChildImage entity) {
        String remoteImageName = flushDataToDisk(entity);
        if (remoteImageName == null)
            return null;

        // update the children table
        if (updateChild(entity.getEmail(), entity.getChildName(), getChild(entity.getEmail(), entity.getChildName()).setLocalImageFile(entity.getFileName()).setRemoteImageFile(remoteImageName)))
            return new Date().getTime();

        return null;
    }

    @Override
    public Boolean deleteChildImage(ComplexResourceKey<ChildId, ChildId> key) {
        return null;
    }

    @Override
    public Boolean updateChildImage(ComplexResourceKey<ChildId, ChildId> key, ChildImage entity) {
        return null;
    }

    @Override
    public ChildImage getChildImage(ComplexResourceKey<ChildId, ChildId> key) {
        return null;
    }

    @Override
    public Hashtable<ParentingTipId, List<SampleAnswer>> populateSampleAnswers() {
        PreparedStatement selectComment = null;

        Hashtable<ParentingTipId, List<SampleAnswer>> parentingTipIdListHashtable = new Hashtable<ParentingTipId, List<SampleAnswer>>();
        String selectString = "select * from sample_answers;";

        try {
            selectComment = conn.prepareStatement(selectString);

            ResultSet resultSet = selectComment.executeQuery();

            while (resultSet.next()) {
                ParentingTipId tipId = new ParentingTipId().setTipResourceId(resultSet.getInt("resource_id")).setTipSequenceId(resultSet.getInt("tip_id"));
                List<SampleAnswer> sampleAnswers;
                if(parentingTipIdListHashtable.containsKey(tipId)){
                    sampleAnswers = parentingTipIdListHashtable.get(tipId);
                } else {
                    sampleAnswers = new ArrayList<SampleAnswer>();
                }

                SampleAnswer sampleAnswer = new SampleAnswer().setAnswerString(resultSet.getString("sample_answer")).setContributorFirstName("contributor_firstname").setTipId(tipId);
                sampleAnswers.add(sampleAnswer);

                parentingTipIdListHashtable.put(tipId, sampleAnswers);
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" populateSampleAnswers Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectComment != null) {
                    selectComment.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return parentingTipIdListHashtable;
    }

    @Override
    public String getWebLink() {
        int index = random.nextInt(ParentingTipResource.weblinks.size());
        return ParentingTipResource.weblinks.get(index);
    }

    @Override
    public List<String> getWebLinks() {
        PreparedStatement selectComment = null;

        List<String> weblinks = new ArrayList<String>();
        String selectString = "select * from weblinks;";

        try {
            selectComment = conn.prepareStatement(selectString);

            ResultSet resultSet = selectComment.executeQuery();

            while (resultSet.next()) {
                weblinks.add(resultSet.getString("link"));
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getWebLinks Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectComment != null) {
                    selectComment.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return weblinks;
    }

    @Override
    public List<ParentingTip> getAllActiveTips() {
        PreparedStatement selectComment = null;

        List<ParentingTip> tips = new ArrayList<ParentingTip>();
        String selectString = "select * from parenting_tips where tip_status='y';";

        try {
            selectComment = conn.prepareStatement(selectString);

            ResultSet resultSet = selectComment.executeQuery();

            while (resultSet.next()) {
                tips.add(new ParentingTip().setTipAgeGroups(resultSet.getInt("age_groups")).setTipId(new ParentingTipId().setTipSequenceId(resultSet.getInt("tip_id")).setTipResourceId(resultSet.getInt("resource_id"))));
            }
        } catch (SQLException ex) {
            _log.error("SQLException: " + ex.getMessage());
            _log.error("SQLState: " + ex.getSQLState());
            _log.error("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(" getAllActiveTips Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectComment != null) {
                    selectComment.close();
                }
            } catch (SQLException ex) {
                _log.error("SQLException: " + ex.getMessage());
                _log.error("SQLState: " + ex.getSQLState());
                _log.error("VendorError: " + ex.getErrorCode());
            }
        }

        return tips;
    }
}
