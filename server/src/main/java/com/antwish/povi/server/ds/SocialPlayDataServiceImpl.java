package com.antwish.povi.server.ds;

import com.antwish.povi.server.Child;
import com.antwish.povi.server.ChildId;
import com.antwish.povi.server.ChildImage;
import com.antwish.povi.server.Comment;
import com.antwish.povi.server.CommentId;
import com.antwish.povi.server.PoviEvent;
import com.antwish.povi.server.SampleAnswer;
import com.antwish.povi.server.Settings;
import com.antwish.povi.server.User;
import com.antwish.povi.server.VoiceComment;
import com.antwish.povi.server.db.SocialPlayDB;
import com.antwish.povi.server.Account;
import com.antwish.povi.server.ActivationRecord;
import com.antwish.povi.server.Event;
import com.antwish.povi.server.ParentingComment;
import com.antwish.povi.server.ParentingTip;
import com.antwish.povi.server.ParentingTipId;
import com.antwish.povi.server.SocialPlayContext;
import com.antwish.povi.server.db.mysql.SocialPlayDBImpl;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.server.CollectionResult;
import com.linkedin.restli.server.PagingContext;

import java.util.Hashtable;
import java.util.List;

public class SocialPlayDataServiceImpl implements SocialPlayDataService{

	private String dbUrl;
	private String dbName;
	private String userName;
	private String password;
	private SocialPlayDB db;

	public SocialPlayDataServiceImpl(String url, String name, String uName, String pwd)
	{
		this.dbName = name;
		this.dbUrl = url;
		this.password = pwd;
		this.userName = uName;
		
		this.db = new SocialPlayDBImpl(dbUrl, dbName, userName, password);
	}

	@Override
	public Long insertPoviEvent(PoviEvent event) {
		return db.insertPoviEvent(event);
	}

	@Override
	public ParentingTip getTip(ParentingTipId tipId, Long accountId) {
		return db.getTip(tipId, accountId);
	}

	@Override
	public ParentingTip[] getRandomDailyTip(String userId, String dateStr, Integer count) {
		return db.getRandomDailyTip(userId, dateStr, count);
	}

	@Override
	public ParentingTip[] getRefreshTips(String userId, Integer count) {
		return db.getRefreshTips(userId, count);
	}

	@Override
	public ParentingTip[] getTipsSelectedDay(String userId, String dateStr, Integer count) {
		return db.getTipsSelectedDay(userId, dateStr, count);
	}

	@Override
	public ParentingTip getRandomTip()
	{
		return db.getRandomTip();
	}

	@Override
	public String getUserEmailFromToken(String token) {
		return db.getUserEmailFromToken(token);
	}

	@Override
	public Child getChild(String user_id, String childName) {
		return db.getChild(user_id, childName);
	}

	@Override
	public List<Child> getChildren(String user_id) {
		return db.getChildren(user_id);
	}

	@Override
	public Boolean addChild(Child entity) {
		return db.addChild(entity);
	}

	@Override
	public boolean updateChild(String user_id, String childName, Child entity) {
		return db.updateChild(user_id, childName, entity);
	}

	@Override
	public boolean deleteChild(String user_id, String childName) {
		return db.deleteChild(user_id, childName);
	}

	@Override
	public User getUser(String token) {
		return db.getUser(token);
	}

	@Override
	public User getUserFromEmail(String email) {
		return db.getUserFromEmail(email);
	}

	@Override
	public boolean addUser(User entry) {
		return db.addUser(entry);
	}

	@Override
	public boolean deleteUser(String token) {
		return db.deleteUser(token);
	}

	@Override
	public boolean updateUser(User entry, String email) {
		return db.updateUser(entry, email);
	}

	@Override
	public String loginEmail(String email, String hash) {
		return db.loginEmail(email, hash);
	}

	@Override
	public boolean updateToken(String email, String newToken) {
		return db.updateToken(email, newToken);
	}

	@Override
	public boolean validateToken(String token) {
		return db.validateToken(token);
	}

	@Override
	public boolean logout(String token) {
		return db.logout(token);
	}

	@Override
	public String loginFacebook(String facebookToken) {
		return db.loginFacebook(facebookToken);
	}

	@Override
	public Long insertComment(Comment entity) {
		return db.insertComment(entity);
	}

	@Override
	public Boolean deleteComment(ComplexResourceKey<CommentId, CommentId> key) {
		return db.deleteComment(key);
	}

	@Override
	public Boolean updateComment(ComplexResourceKey<CommentId, CommentId> key, Comment entity) {
		return db.updateComment(key, entity);
	}

	@Override
	public Comment getComment(ComplexResourceKey<CommentId, CommentId> key) {
		return db.getComment(key);
	}

	@Override
	public CollectionResult<Comment, CommentId> getCommentsPaged(PagingContext context, String user_id, String child_name, Long lastTimestamp) {
		return db.getCommentsPaged(context, user_id, child_name, lastTimestamp);
	}

	@Override
	public CollectionResult<Comment, CommentId> getCommentsLikedPaged(PagingContext context, String user_id, String child_name, Long lastTimestamp) {
		return db.getCommentsLikedPaged(context, user_id, child_name, lastTimestamp);
	}

	@Override
	public Long insertVoiceComment(VoiceComment entity) {
		return db.insertVoiceComment(entity);
	}

	@Override
	public Boolean deleteVoiceComment(ComplexResourceKey<CommentId, CommentId> key) {
		return db.deleteVoiceComment(key);
	}

	@Override
	public Boolean updateVoiceComment(ComplexResourceKey<CommentId, CommentId> key, VoiceComment entity) {
		return db.updateVoiceComment(key, entity);
	}

	@Override
	public VoiceComment getVoiceComment(ComplexResourceKey<CommentId, CommentId> key) {
		return db.getVoiceComment(key);
	}

	@Override
	public Long insertChildImage(ChildImage entity) {
		return db.insertChildImage(entity);
	}

	@Override
	public Boolean deleteChildImage(ComplexResourceKey<ChildId, ChildId> key) {
		return db.deleteChildImage(key);
	}

	@Override
	public Boolean updateChildImage(ComplexResourceKey<ChildId, ChildId> key, ChildImage entity) {
		return db.updateChildImage(key, entity);
	}

	@Override
	public ChildImage getChildImage(ComplexResourceKey<ChildId, ChildId> key) {
		return db.getChildImage(key);
	}

	@Override
	public Hashtable<ParentingTipId, List<SampleAnswer>> populateSampleAnswers() {
		return db.populateSampleAnswers();
	}

	@Override
	public String getWebLink() {
		return db.getWebLink();
	}

	@Override
	public List<String> getWebLinks() {
		return db.getWebLinks();
	}

	@Override
	public List<ParentingTip> getAllActiveTips() {
		return db.getAllActiveTips();
	}
}
