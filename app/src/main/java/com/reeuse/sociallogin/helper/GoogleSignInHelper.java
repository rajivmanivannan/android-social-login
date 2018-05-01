package com.reeuse.sociallogin.helper;

import android.app.Activity;
import android.content.Intent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleSignInHelper {
  public static final int RC_SIGN_IN = 1008;
  // GoogleSignInClient
  private GoogleSignInClient googleSignInClient;

  // Activity instance
  private Activity activity;
  /**
   * Google sign in Listener
   */
  private OnGoogleSignInListener onGoogleSignInListener;

  public GoogleSignInHelper(Activity activity, OnGoogleSignInListener onGoogleSignInListener) {
    this.activity = activity;
    this.onGoogleSignInListener = onGoogleSignInListener;
  }

  /**
   * Connect to google
   */
  public void connect() {
    //Mention the GoogleSignInOptions to get the user profile and email.
    // Instantiate Google SignIn Client.
    googleSignInClient = GoogleSignIn.getClient(activity,
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build());
  }

  /**
   * Call this method in your onStart().If user have already signed in it will provide result directly.
   */
  public void onStart() {
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
    if (account != null && onGoogleSignInListener != null) {
      onGoogleSignInListener.OnGSignInSuccess(account);
    }
  }

  /**
   * To Init the sign in process.
   */
  public void signIn() {
    Intent signInIntent = googleSignInClient.getSignInIntent();
    activity.startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  /**
   * To signOut from the application.
   */
  public void signOut() {
    if (googleSignInClient != null) {
      googleSignInClient.signOut();
    }
  }

  private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
    try {
      GoogleSignInAccount account = completedTask.getResult(ApiException.class);
      // Signed in successfully
      if (onGoogleSignInListener != null) {
        onGoogleSignInListener.OnGSignInSuccess(account);
      }
    } catch (ApiException e) {
      if (onGoogleSignInListener != null) {
        onGoogleSignInListener.OnGSignInError(
            GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
      }
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
      // The Task returned from this call is always completed, no need to attach a listener.
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      handleSignInResult(task);
    }
  }

  /**
   * Interface to listen the Google sign in
   */
  public interface OnGoogleSignInListener {
    void OnGSignInSuccess(GoogleSignInAccount googleSignInAccount);

    void OnGSignInError(String error);
  }
}
