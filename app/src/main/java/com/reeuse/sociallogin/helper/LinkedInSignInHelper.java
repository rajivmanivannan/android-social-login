package com.reeuse.sociallogin.helper;

import android.app.Activity;
import android.content.Intent;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

public class LinkedInSignInHelper implements AuthListener, ApiListener {

  private static final String GET_CURRENT_USER_PROFILE = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address)";
  public static final String GET_CURRENT_USER_CONNECTIONS = "https://api.linkedin.com/v1/people/~/connections";

  // Activity instance
  private Activity activity;

  /**
   * LinkedIn sign in Listener
   */
  private OnLinkedInSignInListener onLinkedInSignInListener;
  private LISessionManager liSessionManager;

  public LinkedInSignInHelper(Activity activity, OnLinkedInSignInListener onLinkedInSignInListener) {
    this.activity = activity;
    this.onLinkedInSignInListener = onLinkedInSignInListener;
  }

  public void connect(){
    liSessionManager = LISessionManager.getInstance(activity.getApplicationContext());
  }

  // Build the list of member permissions our LinkedIn session requires
  private static Scope buildScope() {
    return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
  }

  public void signIn() {
    liSessionManager.init(activity, buildScope(), this, true);
  }

  /**
   * To signOut from the application.
   */
  public void signOut() {
    if (liSessionManager != null) {
      liSessionManager.clearSession();
    }
  }


  private void callLinkedInEndpoint() {
    APIHelper apiHelper = APIHelper.getInstance(activity.getApplicationContext());
    apiHelper.getRequest(activity, GET_CURRENT_USER_PROFILE, this);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Add this line to your existing onActivityResult() method
    liSessionManager.onActivityResult(activity, requestCode, resultCode, data);
  }


  @Override
  public void onAuthSuccess() {
    //Call the api and get the required details.
    callLinkedInEndpoint();
    if (onLinkedInSignInListener != null)
      onLinkedInSignInListener.onAPICallStarted();
  }

  @Override
  public void onAuthError(LIAuthError error) {
    if (onLinkedInSignInListener != null)
      onLinkedInSignInListener.onLinkedInSignError(error.toString());
  }

  @Override
  public void onApiSuccess(ApiResponse apiResponse) {
    if (onLinkedInSignInListener != null)
      onLinkedInSignInListener.OnLinkedInSignInSuccess(apiResponse.getResponseDataAsString());
  }

  @Override
  public void onApiError(LIApiError LIApiError) {
    if (onLinkedInSignInListener != null)
      onLinkedInSignInListener.onLinkedInSignError(LIApiError.getMessage());
  }

  /**
   * Interface to listen the LinkedIn sign in
   */
  public interface OnLinkedInSignInListener {
    void onAPICallStarted();

    void OnLinkedInSignInSuccess(String response);

    void onLinkedInSignError(String error);
  }

}