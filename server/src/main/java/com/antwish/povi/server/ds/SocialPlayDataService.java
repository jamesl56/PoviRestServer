package com.antwish.povi.server.ds;

import com.antwish.povi.server.Account;
import com.antwish.povi.server.ActivationRecord;
import com.antwish.povi.server.Child;
import com.antwish.povi.server.ChildId;
import com.antwish.povi.server.ChildImage;
import com.antwish.povi.server.Comment;
import com.antwish.povi.server.CommentId;
import com.antwish.povi.server.Event;
import com.antwish.povi.server.ParentingComment;
import com.antwish.povi.server.ParentingTip;
import com.antwish.povi.server.ParentingTipId;
import com.antwish.povi.server.PoviEvent;
import com.antwish.povi.server.SampleAnswer;
import com.antwish.povi.server.Settings;
import com.antwish.povi.server.SocialPlayContext;
import com.antwish.povi.server.User;
import com.antwish.povi.server.VoiceComment;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.server.CollectionResult;
import com.linkedin.restli.server.PagingContext;
import com.linkedin.restli.server.annotations.PagingContextParam;
import com.linkedin.restli.server.annotations.QueryParam;

import java.util.Hashtable;
import java.util.List;

public interface SocialPlayDataService {
	public Long insertPoviEvent(PoviEvent event);
	public ParentingTip getTip(ParentingTipId tipId, Long accountId);
	public ParentingTip getRandomTip();
	public ParentingTip[] getRandomDailyTip(String userId, String dateStr, Integer count);
	public ParentingTip[] getTipsSelectedDay(String userId, String dateStr, Integer count);
	public ParentingTip[] getRefreshTips(String userId, Integer count);
	public String getUserEmailFromToken(String token);

	//children
	public Child getChild(String user_id, String childName);
	public List<Child> getChildren(String user_id);
	public Boolean addChild(Child entity);
	public boolean updateChild(String user_id, String childName, Child entity);
	public boolean deleteChild(String user_id, String childName);

	//user
	public User getUser(String token);
	public User getUserFromEmail(String email);
	public boolean addUser(User entry);
	public boolean deleteUser(String token);
	public boolean updateUser(User entry, String email);

	//login related
	public String loginEmail(String email, String hash);
	public boolean updateToken(String email, String newToken);
	public boolean validateToken(String token);
	public boolean logout(String token);
	public String loginFacebook(String facebookToken);

	// comment
	public Long insertComment(Comment entity);
	public Boolean deleteComment(ComplexResourceKey<CommentId, CommentId> key);
	public Boolean updateComment(ComplexResourceKey<CommentId, CommentId> key, Comment entity);
	public Comment getComment(ComplexResourceKey<CommentId, CommentId> key);
	public CollectionResult<Comment,CommentId> getCommentsPaged( PagingContext context, String user_id, String child_name, Long lastTimestamp);
	public CollectionResult<Comment,CommentId> getCommentsLikedPaged( PagingContext context, String user_id, String child_name, Long lastTimestamp);

	// voice comment
	public Long insertVoiceComment(VoiceComment entity);
	public Boolean deleteVoiceComment(ComplexResourceKey<CommentId, CommentId> key);
	public Boolean updateVoiceComment(ComplexResourceKey<CommentId, CommentId> key, VoiceComment entity);
	public VoiceComment getVoiceComment(ComplexResourceKey<CommentId, CommentId> key);

	// child image
	public Long insertChildImage(ChildImage entity);
	public Boolean deleteChildImage(ComplexResourceKey<ChildId, ChildId> key);
	public Boolean updateChildImage(ComplexResourceKey<ChildId, ChildId> key, ChildImage entity);
	public ChildImage getChildImage(ComplexResourceKey<ChildId, ChildId> key);

	// sample answers
	public Hashtable<ParentingTipId, List<SampleAnswer>> populateSampleAnswers();

	// weblink
	public String getWebLink();
	public List<String> getWebLinks();

	// age groups
	public List<ParentingTip> getAllActiveTips();
}
