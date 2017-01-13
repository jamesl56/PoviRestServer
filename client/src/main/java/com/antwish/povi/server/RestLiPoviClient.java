package com.antwish.povi.server;/*
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

import com.linkedin.common.callback.FutureCallback;
import com.linkedin.common.util.None;
import com.linkedin.data.ByteString;
import com.linkedin.data.DataMap;
import com.linkedin.data.template.SetMode;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.ActionRequest;
import com.linkedin.restli.client.CreateIdRequest;
import com.linkedin.restli.client.DeleteRequest;
import com.linkedin.restli.client.FindRequest;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.client.Response;
import com.linkedin.restli.client.ResponseFuture;
import com.linkedin.restli.client.RestClient;
import com.linkedin.restli.client.UpdateRequest;
import com.linkedin.restli.common.CollectionResponse;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.common.IdResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class RestLiPoviClient {
    public static String POVI_AUTHORIZATION_HEADER = "povi-authorization";
    public static String FAKE_POVI_TOKEN = "abc";
    public static String COMMENTID = "comment_id";
    public static String HASH = "abcdefgh";
    public static String KIDS_NAME = "Big Boy";
    public static String KIDS_NAME2 = "Sweet Boy";
    private static final int TOTAL_TIPS = 48;

    private static Random random = new Random();
    private static Connection connect = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    private static List<Comment> comments = new ArrayList<Comment>();

    /**
     * This stand-alone app demos the client-side Pegasus API.
     * To see the demo, run RestLiFortuneServer, then start the client
     */
    public static void main(String[] args) throws Exception {
        // Create an HttpClient and wrap it in an abstraction layer
        final HttpClientFactory http = new HttpClientFactory();
        final Client r2Client = new TransportClientAdapter(
                http.getClient(Collections.<String, String>emptyMap()));

        System.out.println(new User().setEmail("abc@example.com"));
        System.out.println(new Comment());
        System.out.println(new Child());
        System.out.println(new ChildImage());
        System.out.println(new VoiceComment());
        System.out.println(new ParentingTip());
        System.out.println(new ParentingTipId());
        System.out.println(new Event());
        // Create a RestClient to talk to localhost:8080
        RestClient restClient = new RestClient(r2Client, "http://localhost:8080/");
//        RestClient restClient = new RestClient(r2Client, "http://54.183.228.194:8080/");

//        List<Child> children = getChildren(restClient,"5071b9603f3b166335b7d9b1314fd3221b822fd8");
//        List<Child> children = getChildren(restClient,"2c4c1398f11ddd95c4d71ce993ad194eddeabd4d");
        long startTime = System.nanoTime();
        String token = createUser(restClient, String.valueOf(random.nextInt()) + "@email.com");
        long delta = System.nanoTime() - startTime;
        System.out.println("createUser takes " + delta + " nano seconds");
        System.out.println("token returned from new user: " + token);

        startTime = System.nanoTime();
        User user = getUser(restClient, token);
        delta = System.nanoTime() - startTime;
        System.out.println("getUser takes " + delta + " nano seconds");

        createUser(restClient, user.getEmail());
        String name = String.valueOf(random.nextDouble());
//        System.out.println("will update user name: " + name);

        startTime = System.nanoTime();
//        updateProfile(restClient, token, user.getEmail(), user.getEmail(), user.getHash(), name, user.getPhone(), user.getAddress(), new Date().getTime());
        updateProfile(restClient, token, user.getEmail(), (random.nextInt()) + "@email.com", user.getHash(), name, user.getPhone(), user.getAddress(), new Date().getTime());
        delta = System.nanoTime() - startTime;
        System.out.println("updateProfile takes " + delta + " nano seconds");

        startTime = System.nanoTime();
        User newUser = getUser(restClient, token);
        delta = System.nanoTime() - startTime;
        System.out.println("getUser takes " + delta + " nano seconds");
//        System.out.println("old user name: " + user.getName());
//        System.out.println("new user name: " + newUser.getName());

        startTime = System.nanoTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        if (registerNewChild(restClient, newUser.getEmail(), KIDS_NAME, "male", sdf.parse("03/04/2003").getTime(), token)) {
            delta = System.nanoTime() - startTime;
            System.out.println("registerNewChild takes " + delta + " nano seconds");
//            System.out.println("added new child!");
            Date date = new Date();

            Date newdate = sdf.parse("03/04/2010");
            startTime = System.nanoTime();
            if (updateChild(restClient, newUser.getEmail(), KIDS_NAME, KIDS_NAME2, "female", newdate.getTime(), token)){
                delta = System.nanoTime() - startTime;
                System.out.println("updateChild takes " + delta + " nano seconds");
            }
//                System.out.println("updated birthdate to: " + sdf.format(newdate));
            else
                System.out.println("failed to update child profile!");
        } else
            System.out.println("failed to add child");

//        for(int i=145; i<=148;i++){
//            insertComments(restClient,newUser.getEmail(), KIDS_NAME, 1, token, i, 2, "Blah");
//        }
        startTime = System.nanoTime();
        ParentingTip[] tips = getTips(restClient, token, newUser.getEmail());
        delta = System.nanoTime() - startTime;
        System.out.println("getTips takes " + delta + " nano seconds");

        startTime = System.nanoTime();
        logout(restClient, token);
        delta = System.nanoTime() - startTime;
        System.out.println("logout takes " + delta + " nano seconds");

        loginEmail(restClient, user.getEmail(), HASH);
        token = loginEmail(restClient, newUser.getEmail(), HASH);
        delta = System.nanoTime() - startTime;
        System.out.println("loginEmail takes " + delta + " nano seconds");

        System.out.println("weblink: " + getWebLink(restClient, token));
        resetPassword(restClient, user.getEmail());


//        deleteAllComments(USERID);
//        getTips(restClient, FAKE_POVI_TOKEN, USERID);
        if(tips != null && tips.length > 0) {
            System.out.println("getTips returns:..............................");
            for(ParentingTip tip : tips){
                System.out.println("ageGroup: " + tip.getTipAgeGroups() + " resourceId: " + tip.getTipId().getTipResourceId() + ", sequenceId: " + tip.getTipId().getTipSequenceId() + ", content: " + tip.getTipDetail() + ", category: " + tip.getTipCategory() + " , sampleAnswers: " + (tip.getSampleAnswers().size()>0 ? tip.getSampleAnswers().get(0).getAnswerString() : "none sample answer")+ " like count: " + tip.getLikeCount() + " comment count: " + tip.getCommentCount());
            }
//            System.out.println("Will create comment.");
            startTime = System.nanoTime();
            insertComments(restClient, newUser.getEmail(), KIDS_NAME2, 1, token, tips[0].getTipId().getTipSequenceId(), tips[0].getTipId().getTipResourceId(), tips[0].getTipDetail());
            delta = System.nanoTime() - startTime;
            System.out.println("insertComments takes " + delta + " nano seconds");

            startTime = System.nanoTime();
            Integer lastCommentId = getCommentsPaged(restClient, newUser.getEmail(), KIDS_NAME2, token, 0, 5, null);
            delta = System.nanoTime() - startTime;
            System.out.println("getCommentsPaged takes " + delta + " nano seconds");

//            System.out.println("lastCommentId after first page: " + ((lastCommentId == null) ? "null" : lastCommentId));
            Comment comment = comments.get(0);
//        insertVoiceComments(restClient, newUser.getEmail(), comment.getChildName(), readFromFile("/Users/jianli/Documents/mole.png"), comment.getTimestamp(), token);
//        addChildImage(restClient, newUser.getEmail(), comment.getChildName(), readFromFile("/Users/jianli/Documents/mole.png"), comment.getTimestamp(), "myimage.png", token);

            startTime = System.nanoTime();
            updateComment(restClient, comment.getUserId(), comment.getChildName(), comment.getTipId(), comment.getTipString(), comment.getTimestamp(), "new comment", comment.isLikeStatus(), token);
            delta = System.nanoTime() - startTime;
            System.out.println("updateComment takes " + delta + " nano seconds");
        }
//        lastCommentId = getCommentsPaged(restClient, 5, 5, lastCommentId.longValue());
//        System.out.println("lastCommentId after 2nd page: " + ((lastCommentId == null) ? "null" : lastCommentId));
//        lastCommentId = getCommentsPaged(restClient, 10, 5, lastCommentId.longValue());
//        System.out.println("lastCommentId after 3rd page: " + ((lastCommentId == null) ? "null" : lastCommentId));
//        lastCommentId = getCommentsPaged(restClient, 15, 5, lastCommentId.longValue());
//        System.out.println("lastCommentId after 3rd page: " + ((lastCommentId == null) ? "null" : lastCommentId));
//        lastCommentId = getCommentsPaged(restClient, 20, 5, lastCommentId.longValue());
//        System.out.println("lastCommentId after 4th page: " + ((lastCommentId == null) ? "null" : lastCommentId));
        //      getUserProfile(restClient, "abcd");
        // shutdown

        for(int i=0; i<5;i++) {
            tips = getRefreshTips(restClient, token, newUser.getEmail());
            if (tips != null && tips.length > 0) {
                System.out.println("getRefreshTips returns:-----------------------------------");
                for (ParentingTip tip : tips) {
                    System.out.println("ageGroup: " + tip.getTipAgeGroups() + " resourceId: " + tip.getTipId().getTipResourceId() + ", sequenceId: " + tip.getTipId().getTipSequenceId() + ", content: " + tip.getTipDetail() + ", category: " + tip.getTipCategory() + " , sampleAnswers: " + (tip.getSampleAnswers().size() > 0 ? tip.getSampleAnswers().get(0).getAnswerString() : "none sample answer") + " like count: " + tip.getLikeCount() + " comment count: " + tip.getCommentCount());
                }
            }
        }

        tips = getTipsSelectedDay(restClient, token, newUser.getEmail(), "07/15/2015");
        if(tips != null && tips.length > 0) {
            System.out.println("getTipsSelectedDay returns:================================");
            for (ParentingTip tip : tips) {
                System.out.println("ageGroup: " + tip.getTipAgeGroups() + " resourceId: " + tip.getTipId().getTipResourceId() + ", sequenceId: " + tip.getTipId().getTipSequenceId() + ", content: " + tip.getTipDetail() + ", category: " + tip.getTipCategory() + " , sampleAnswers: " + (tip.getSampleAnswers().size() > 0 ? tip.getSampleAnswers().get(0).getAnswerString() : "none sample answer") + " like count: " + tip.getLikeCount() + " comment count: " + tip.getCommentCount());
            }
        }

        tips = getTips(restClient, token, newUser.getEmail());
        if(tips != null && tips.length > 0) {
            System.out.println("call getTips again it returns:*********************");
            for (ParentingTip tip : tips) {
                System.out.println("ageGroup: " + tip.getTipAgeGroups() + " resourceId: " + tip.getTipId().getTipResourceId() + ", sequenceId: " + tip.getTipId().getTipSequenceId() + ", content: " + tip.getTipDetail() + ", category: " + tip.getTipCategory() + " , sampleAnswers: " + (tip.getSampleAnswers().size() > 0 ? tip.getSampleAnswers().get(0).getAnswerString() : "none sample answer") + " like count: " + tip.getLikeCount() + " comment count: " + tip.getCommentCount());
            }
        }
        restClient.shutdown(new FutureCallback<None>());
        http.shutdown(new FutureCallback<None>());
    }

    public static ParentingTip[] getTipsSelectedDay(RestClient restClient, String token, String userId, String dateStr)
    {
        try
        {
            ActionRequest<ParentingTipArray> actionRequest = new ParentingTipRequestBuilders().actionGetTipsSelectedDay().userIdParam(userId).dateStrParam(dateStr).countParam(3).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
            ResponseFuture<ParentingTipArray> responseFuture = restClient.sendRequest(actionRequest);
            Response<ParentingTipArray> response = responseFuture.getResponse();

            return response.getEntity().toArray(new ParentingTip[3]);
        }catch (RemoteInvocationException ex)
        {
            System.out.println("Encountered error when fetching tips from selected days " + ex.getMessage());
            for(StackTraceElement element : ex.getStackTrace()){
                System.out.println(element);
            }
        }

        return new ParentingTip[0];
    }

    public static ParentingTip[] getRefreshTips(RestClient restClient, String token, String userId)
    {
        try
        {
            ActionRequest<ParentingTipArray> actionRequest = new ParentingTipRequestBuilders().actionGetRefreshTips().userIdParam(userId).countParam(3).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
            ResponseFuture<ParentingTipArray> responseFuture = restClient.sendRequest(actionRequest);
            Response<ParentingTipArray> response = responseFuture.getResponse();
            return response.getEntity().toArray(new ParentingTip[3]);
        }catch (RemoteInvocationException ex)
        {
            System.out.println("Encountered error getting refresh tips: " + ex.getMessage());
        }
        return new ParentingTip[0];
    }

    public static String getWebLink(RestClient restClient, String token){
        ParentingTipDoGetWebLinkRequestBuilder requestBuilder = new ParentingTipRequestBuilders().actionGetWebLink();
        ActionRequest<String> actionRequest = requestBuilder.addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        final ResponseFuture<String> responseFuture = restClient.sendRequest(actionRequest);
        try
        {
            return responseFuture.getResponse().getEntity();
        }catch (RemoteInvocationException ex){
            ex.printStackTrace();
        }

        return null;
    }
    public static boolean resetPassword(RestClient restClient, String email){
        PoviActionsDoResetPasswordRequestBuilder poviActionsDoResetPasswordRequestBuilder = new PoviActionsRequestBuilders().actionResetPassword();
        ActionRequest<Boolean> validateReq = poviActionsDoResetPasswordRequestBuilder.emailParam(email).build();
        final ResponseFuture<Boolean> getFuture = restClient.sendRequest(validateReq);
        final Response<Boolean> resp;
        try {
            resp = getFuture.getResponse();
            if(resp.getEntity())
                System.out.println("successfully reset password");
            else
                System.out.println("failed to reset password");

            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ParentingTip getTip(RestClient restClient, ParentingTipId tipId, String token){
        ParentingTipGetRequestBuilder parentingTipGetRequestBuilder = new ParentingTipRequestBuilders().get();
        GetRequest<ParentingTip> getRequest = parentingTipGetRequestBuilder.id(new ComplexResourceKey<ParentingTipId, Account>(tipId, new Account().setAccountId(123).setName("lll").setIdentity(new Identity()))).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        final ResponseFuture<ParentingTip> responseFuture = restClient.sendRequest(getRequest);
        try {
            final Response<ParentingTip> response = responseFuture.getResponse();

            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static byte[] readFromFile(String fileName) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(fileName));
            return bytes;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static boolean registerNewChild(RestClient restClient, final String userEmail, final String childName, final String gender, long birthdate, String token) {
        // Construct a request for the specified fortune
        ChildCreateRequestBuilder rb = new ChildRequestBuilders().create();
        CreateIdRequest<ComplexResourceKey<ChildId, ChildId>, Child> registerReq = rb.input(new Child().setUser_id(userEmail).setName(childName).setBirthdate(birthdate).setGender(gender)).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        // Send the request and wait for a response
        final ResponseFuture<IdResponse<ComplexResourceKey<ChildId, ChildId>>> getFuture = restClient.sendRequest(registerReq);
        final Response<IdResponse<ComplexResourceKey<ChildId, ChildId>>> resp;
        try {
            resp = getFuture.getResponse();
            return true;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateChild(RestClient restClient, final String userEmail, final String origName, final String childName, final String gender, long birthdate, String token) {
        // Construct a request for the specified fortune
        ChildId keyId = new ChildId().setUser_id(userEmail).setChild_Id(origName);
        ComplexResourceKey<ChildId, ChildId> key = new ComplexResourceKey<ChildId, ChildId>(keyId, keyId);
        ChildUpdateRequestBuilder rb = new ChildRequestBuilders().update();
        UpdateRequest<Child> request = rb.id(key)
                .input(new Child().setBirthdate(birthdate).setGender(gender).setUser_id(userEmail).setName(childName)).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        // Send the request and wait for a response
        final ResponseFuture getFuture = restClient.sendRequest(rb);
        final Response resp;
        try {
            resp = getFuture.getResponse();
            return true;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean logout(RestClient restClient, final String token) {
        PoviActionsDoLogoutRequestBuilder rb = new PoviActionsRequestBuilders().actionLogout().tokenParam(token);
        ActionRequest<Boolean> validateReq = rb.addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        final ResponseFuture<Boolean> getFuture = restClient.sendRequest(validateReq);
        final Response<Boolean> resp;
        try {
            resp = getFuture.getResponse();
//            System.out.println("successfully logged out");
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String loginEmail(RestClient restClient, final String email, final String hash) {
        // Construct a request for the specified fortune
        PoviActionsDoLoginEmailRequestBuilder rb = new PoviActionsRequestBuilders().actionLoginEmail().emailParam(email).hashParam(hash);
        ActionRequest<String> registerReq = rb.build();

        // Send the request and wait for a response
        final ResponseFuture<String> getFuture = restClient.sendRequest(registerReq);
        final Response<String> resp;
        try {
            resp = getFuture.getResponse();
//            System.out.println("successfully logged in");
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ParentingTip[] getTips(RestClient restClient, String token, String userId) {
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String dateStr = sdf.format(date);
            ActionRequest<ParentingTipArray> actionRequest = new ParentingTipRequestBuilders().actionGetTips().userIdParam(userId).dateStrParam(dateStr).countParam(3).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
            ResponseFuture<ParentingTipArray> responseFuture = restClient.sendRequest(actionRequest);
            Response<ParentingTipArray> response = responseFuture.getResponse();

            return response.getEntity().toArray(new ParentingTip[3]);
        } catch (RemoteInvocationException ex) {
//            System.out.println("Encountered error doing registerAccount: " + ex.getMessage());
        }

        return new ParentingTip[0];
    }

    //    public static List<Comment> getComments()
    public static void insertComments(RestClient restClient, String email, String childName, int count, String token, int tipId, int resourceId, String tipString) throws Exception {
        for (int i = 0; i < count; i++) {
            createComment(restClient, email, childName, tipId, resourceId, new Date().getTime(), "comment" + i, false, tipString, token);
            Thread.sleep(1);
        }
    }

    public static void insertVoiceComments(RestClient restClient, String email, String childName, byte[] data, long timestamp, String token) throws Exception {
        createVoiceComment(restClient, email, childName, timestamp, "abc", data, token);
    }

    public static boolean addChildImage(RestClient restClient, String email, String childName, byte[] data, long timestamp, String fileName, String token){
        ChildImageCreateRequestBuilder childImageCreateRequestBuilder = new ChildImageRequestBuilders().create();
        CreateIdRequest<ComplexResourceKey<ChildId, ChildId>, ChildImage> createReq = childImageCreateRequestBuilder.input(new ChildImage()
        .setFileName(fileName).setFileContent(ByteString.copy(data)).setEmail(email).setChildName(childName)).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        final ResponseFuture<IdResponse<ComplexResourceKey<ChildId, ChildId>>> getFuture =
                restClient.sendRequest(createReq);

        // If you get some response, then the comment has been entered in the table
        final Response<IdResponse<ComplexResourceKey<ChildId, ChildId>>> resp;
        try {
            resp = getFuture.getResponse();
           System.out.println("Successfully added image!");
            return true;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean updateComment(RestClient restClient, final String userEmail, final String childName,
                                        final int tipId, final String tipString,
                                        final long timestamp, final String commentText,
                                        final boolean likeStatus, final String token) {

        // Setting up the commentId key which is used to update the record
        CommentId keyId = new CommentId().setUser_id(userEmail).
                setChild_Id(childName).
                setTimestamp(timestamp);

        ComplexResourceKey<CommentId, CommentId> key = new ComplexResourceKey<CommentId, CommentId>(keyId, keyId);

        // Creating the Comment Delete request builder
        TipCommentUpdateRequestBuilder update_requestBuilder =
                new TipCommentRequestBuilders().update();

        UpdateRequest updateReq = update_requestBuilder.id(key).input(new Comment().
                setUserId(userEmail).
                setTipId(tipId).
                setTipString(tipString).
                setTimestamp(timestamp).
                setCommentText(commentText).
                setLikeStatus(likeStatus).
                setChildName(childName)).
                addHeader(POVI_AUTHORIZATION_HEADER,
                        token).build();

        // Send the request and wait for a response
        final ResponseFuture getFuture = restClient.sendRequest(updateReq);

        // If you get an OK response, then the comment has been updated in the table
        final Response resp;
        try {
            resp = getFuture.getResponse();

//            System.out.println("status: " + resp.getStatus());
            if (resp.getStatus() == HttpStatus.S_200_OK.getCode()) {
                return true;
            }
        } catch (RemoteInvocationException e) {
            e.printStackTrace();

        }
        return false;
    }

    public static void deleteAllComments(String userId) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Setup the connection with the DB
        try {
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/povi_schema?"
                            + "user=povi&password=povi");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preparedStatement = connect.prepareStatement("select `hash` from `povi_schema`.`users` where `email`=?;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            preparedStatement.setString(1, userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Close connection
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connect != null) {
            try {
                connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static Integer getCommentsPaged(RestClient restClient, String email, String childName, String token, int start, int count, Long lastTimestamp) {
        TipCommentFindByGetCommentsPagedRequestBuilder tipCommentFindByGetCommentsPagedRequestBuilder = new TipCommentRequestBuilders().findByGetCommentsPaged();

        if (lastTimestamp != null)
            tipCommentFindByGetCommentsPagedRequestBuilder.lastTimestampParam(lastTimestamp);

        FindRequest<Comment> findRequest = tipCommentFindByGetCommentsPagedRequestBuilder.paginate(start, count).userIdParam(email).childNameParam(childName).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        ResponseFuture<CollectionResponse<Comment>> getFutureComments = restClient.
                sendRequest(findRequest);

        // Start collecting the results of the response in commentResp
        Response<CollectionResponse<Comment>> commentResp;
        try {
            commentResp = getFutureComments.getResponse();
            CollectionResponse<Comment> response = commentResp.getEntity();
            DataMap dataMap = response.getMetadataRaw();
            comments = response.getElements();
//            for (Comment comment : comments)
//                System.out.println(comment.toString());

            if (dataMap != null && dataMap.containsKey(COMMENTID))
                return dataMap.getInteger(COMMENTID);
            else
                return null;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean createComment(RestClient restClient, final String userEmail, final String childName,
                                        final int tipId, final int resourceId, final long timestamp,
                                        final String commentText, final boolean likeStatus, final String tipString, final String token) {

        // Initialize a create comment request builder
        TipCommentCreateRequestBuilder create_requestBuilder =
                new TipCommentRequestBuilders().create();


        // Initialize a create request
        CreateIdRequest<ComplexResourceKey<CommentId, CommentId>, Comment> createReq =
                create_requestBuilder.input(new Comment().setTipString(tipString).
                        setUserId(userEmail).
                        setTipId(tipId).
                        setResourceId(resourceId).
                        setTimestamp(timestamp).
                        setCommentText(commentText).
                        setLikeStatus(likeStatus).
                        setChildName(childName)).
                        addHeader(POVI_AUTHORIZATION_HEADER,
                                token).build();

        // Send the request and wait for a response
        final ResponseFuture<IdResponse<ComplexResourceKey<CommentId, CommentId>>> getFuture =
                restClient.sendRequest(createReq);

        // If you get some response, then the comment has been entered in the table
        final Response<IdResponse<ComplexResourceKey<CommentId, CommentId>>> resp;
        try {
            resp = getFuture.getResponse();
//            System.out.println("commentId: " + resp.getEntity().getId().getKey().getComment_id());
            return true;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createVoiceComment(RestClient restClient, final String userEmail, final String childName,
                                        final long timestamp,
                                        final String fileName, final byte[] content, final String token) {

        VoiceCommentCreateRequestBuilder voiceCommentCreateRequestBuilder = new VoiceCommentRequestBuilders().create();

        // Initialize a create request
        CreateIdRequest<ComplexResourceKey<CommentId, CommentId>, VoiceComment> createReq =
                voiceCommentCreateRequestBuilder.input(new VoiceComment().
                        setEmail(userEmail).
                        setTimestamp(timestamp).
                        setFileContent(ByteString.copy(content)).
                        setChildName(childName).setFileName("1.png")).
                        addHeader(POVI_AUTHORIZATION_HEADER,
                                token).build();

        // Send the request and wait for a response
        final ResponseFuture<IdResponse<ComplexResourceKey<CommentId, CommentId>>> getFuture =
                restClient.sendRequest(createReq);

        // If you get some response, then the comment has been entered in the table
        final Response<IdResponse<ComplexResourceKey<CommentId, CommentId>>> resp;
        try {
            resp = getFuture.getResponse();
//            System.out.println("Successfully added file!");
            return true;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String createUser(RestClient restClient, String email) {
        try {
            // Construct a request for the specified fortune
            UserCreateRequestBuilder rb = new UserRequestBuilders().create();
            CreateIdRequest<String, User> registerReq = rb.input(new User().setEmail(email).setHash(HASH).setName("Bepi Caena").setPhone("555555555").setBirthdate(0)).build();

            // Send the request and wait for a response
            final ResponseFuture<IdResponse<String>> getFuture = restClient.sendRequest(registerReq);
            final Response<IdResponse<String>> resp = getFuture.getResponse();

            // Print the response
//            System.out.println(resp.getEntity().getId());
            return resp.getEntity().getId();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static User getUser(RestClient restClient, String token) {
        GetRequest<User> getReq = new UserRequestBuilders().get().id(token).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        // Send the request and wait for a response
        final ResponseFuture<User> getFuture = restClient.sendRequest(getReq);
        final Response<User> resp;
        try {
            resp = getFuture.getResponse();
            User user = resp.getEntity();
//            System.out.println("email: " + user.getEmail());
//            System.out.println("hash: " + user.getHash());

            return user;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updateProfile(RestClient restClient, final String token, final String oldEmail, final String email, final String hash, final String name, final String phone, final String address, long birthdate) {
        // Creating the profile update request builder
        UserUpdateRequestBuilder updateRequestBuilder = new UserRequestBuilders().update();

        UpdateRequest updateReq = updateRequestBuilder.id(oldEmail).input(new User().
                setEmail(email)
                .setHash(hash)
                .setName(name)
                .setPhone(phone)
                .setAddress(address, SetMode.IGNORE_NULL)
                .setBirthdate(birthdate, SetMode.IGNORE_NULL))
                .addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        // Send the request and wait for a response
        final ResponseFuture getFuture = restClient.sendRequest(updateReq);

        // If you get an OK response, then the comment has been updated in the table
        final Response resp;
        try {
            resp = getFuture.getResponse();
            if (resp.getStatus() == HttpStatus.S_200_OK.getCode()) {
                return true;
            }
        } catch (RemoteInvocationException e) {
            e.printStackTrace();

        }
        return false;
    }

    public static void deleteUser(RestClient restClient, String token) {
        try {
            UserDeleteRequestBuilder rb = new UserRequestBuilders().delete();
            DeleteRequest<User> deleteRequest = rb.id(token).build();

            final ResponseFuture<EmptyRecord> responseFuture = restClient.sendRequest(deleteRequest);
            final Response<EmptyRecord> response = responseFuture.getResponse();

            System.out.println(response.getStatus());
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
        }
    }

    public static User getUserProfile(RestClient restClient, final String token) {
        // Construct a request for the specified fortune
        UserGetRequestBuilder rb = new UserRequestBuilders().get().id(token);
        GetRequest<User> getReq = rb.build();

        // Send the request and wait for a response
        final ResponseFuture<User> getFuture = restClient.sendRequest(getReq);
        final Response<User> resp;
        try {
            resp = getFuture.getResponse();
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Child> getChildren(RestClient restClient, final String token) {
        if (token == null)
            return null;

        // Get current user from token
        // Construct a request for the specified fortune
        UserGetRequestBuilder rb = new UserRequestBuilders().get().id(token);
        GetRequest<User> getReq = rb.addHeader(POVI_AUTHORIZATION_HEADER, FAKE_POVI_TOKEN).build();

        // Send the request and wait for a response
        final ResponseFuture<User> getFuture = restClient.sendRequest(getReq);
        final Response<User> resp;
        try {
            resp = getFuture.getResponse();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }

        User user = resp.getEntity();

        ChildFindByGetChildrenRequestBuilder crb = new ChildRequestBuilders().findByGetChildren().user_idParam(user.getEmail());
        FindRequest<Child> findReq = crb.addHeader(POVI_AUTHORIZATION_HEADER, FAKE_POVI_TOKEN).build();
        ResponseFuture<CollectionResponse<Child>> getFutureChildren = restClient.sendRequest(findReq);
        Response<CollectionResponse<Child>> childResp;
        try {
            childResp = getFutureChildren.getResponse();
            return childResp.getEntity().getElements();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }
}

