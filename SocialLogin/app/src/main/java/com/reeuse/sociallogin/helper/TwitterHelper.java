package com.reeuse.sociallogin.helper;

import android.app.Activity;
import android.content.Intent;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import io.fabric.sdk.android.Fabric;

/**
 * TwitterHelper.java
 */
public class TwitterHelper {
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "3MBGCX634TmyzeNFS5kMn7GIS";
    private static final String TWITTER_SECRET = "AUqvdi6sYbwth6p8nVpL14jMTMYgP2LOfhrzPCuueRnuxgM7uU";
    private UserDetails userDetails = new UserDetails();
    /**
     * To get Email Address need to submit a request in the below link
     * https://support.twitter.com/forms/platform
     */
    private Activity activity;
    private OnTwitterSignInListener mOnTwitterSignInListener;
    private TwitterAuthClient mTwitterAuthClient;
    private TwitterAuthConfig mTwitterAuthConfig;
    private TwitterSession mTwitterSession;
    private TwitterAuthToken mTwitterAuthToken;

    /**
     * Interface to listen the Twitter fabric login
     */
    public interface OnTwitterSignInListener {
        void OnTwitterSignInComplete(UserDetails userDetails, String error);

        void OnTweetPostComplete(Result<Tweet> result, String error);
    }


    public TwitterHelper(Activity activity, OnTwitterSignInListener mOnTwitterSignInListener) {
        this.activity = activity;
        this.mOnTwitterSignInListener = mOnTwitterSignInListener;
        mTwitterAuthConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(activity, new Twitter(mTwitterAuthConfig));
        mTwitterAuthClient = new TwitterAuthClient();
    }

    public void connect() {
        mTwitterAuthClient.authorize(activity, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                mTwitterSession = Twitter.getSessionManager().getActiveSession();
                mTwitterAuthToken = mTwitterSession.getAuthToken();
                userDetails.setSecret(mTwitterAuthToken.token);
                userDetails.setToken(mTwitterAuthToken.secret);
                userDetails.setUserId(result.data.getUserId());
                userDetails.setUserName(result.data.getUserName());
                getEmailAddress();
            }

            @Override
            public void failure(TwitterException e) {
                mOnTwitterSignInListener.OnTwitterSignInComplete(null, e.getMessage());
            }
        });
    }

    private void getEmailAddress() {
        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(mTwitterSession, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                userDetails.setUserEmail(result.data);
                mOnTwitterSignInListener.OnTwitterSignInComplete(userDetails, null);
            }

            @Override
            public void failure(TwitterException exception) {
                mOnTwitterSignInListener.OnTwitterSignInComplete(userDetails, exception.getMessage());
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mTwitterAuthClient != null)
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * To post the tweet in background.
     * @param status
     * @param replyStatusId
     * @param isSensitive
     * @param latitude
     * @param longitude
     * @param placeId
     * @param isShowcooridnates
     * @param isTrimUser
     * @param mediaId
     */
    public void postTweet(String status, Long replyStatusId, Boolean isSensitive, Double latitude, Double longitude, String placeId, Boolean isShowcooridnates, Boolean isTrimUser, String mediaId) {
        StatusesService statusesService = Twitter.getApiClient(mTwitterSession).getStatusesService();
        statusesService.update(status, replyStatusId, isSensitive, latitude, longitude, placeId, isShowcooridnates, isTrimUser, mediaId, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                mOnTwitterSignInListener.OnTweetPostComplete(result, null);
            }

            public void failure(TwitterException exception) {
                mOnTwitterSignInListener.OnTweetPostComplete(null, exception.getMessage());
            }
        });
    }

    /**
     * UserDetails model class to hold the response values.
     */
    public class UserDetails {

        private String userName;
        private long userId;
        private String token;
        private String secret;
        private String userEmail;

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}