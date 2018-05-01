package com.reeuse.sociallogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.FacebookSdk;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.reeuse.sociallogin.helper.FacebookHelper;
import com.reeuse.sociallogin.helper.GoogleSignInHelper;
import com.reeuse.sociallogin.helper.LinkedInSignInHelper;
import com.reeuse.sociallogin.helper.TwitterHelper;
import com.reeuse.sociallogin.utils.KeyHashGenerator;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.models.Tweet;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
    implements FacebookHelper.OnFbSignInListener, GoogleSignInHelper.OnGoogleSignInListener,
    TwitterHelper.OnTwitterSignInListener, LinkedInSignInHelper.OnLinkedInSignInListener {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final String URL = "https://github.com/rajivmanivannan/Android-Social-Login";
  //--------------------------------Facebook login--------------------------------------//
  private FacebookHelper fbConnectHelper;
  private Button fbSignInButton;
  private Button fbShareButton;
  //----------------------------------Google +Sign in-----------------------------------//
  //Google plus sign-in button
  private GoogleSignInHelper googleSignInHelper;
  private Button gSignInButton;
  //-----------------------------------Twitter Sign In -----------------------------------//
  private TwitterHelper twitterHelper;
  private Button tSignInButton;
  //-----------------------------------LinkedIn Sign In-----------------------------------//
  private LinkedInSignInHelper linkedInSignInHelper;
  private Button lSignInButton;

  private TextView userName;
  private TextView email;
  private ProgressBar progressBar;
  private boolean isFbLogin = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    twitterHelper = new TwitterHelper(this, this); // Twitter Initialization
    FacebookSdk.sdkInitialize(getApplicationContext()); // Facebook SDK Initialization
    setContentView(R.layout.activity_main);
    progressBar = (ProgressBar) findViewById(R.id.progress_bar);

    userName = findViewById(R.id.main_name_txt);
    email = findViewById(R.id.main_email_txt);

    //--------------------------------Facebook login--------------------------------------//
    KeyHashGenerator.generateKey(this);
    fbConnectHelper = new FacebookHelper(this, this);
    fbSignInButton = findViewById(R.id.fb_sign_in_button);
    fbSignInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        fbConnectHelper.connect();
        isFbLogin = true;
      }
    });

    fbShareButton = (Button) findViewById(R.id.main_fb_share_button);
    fbShareButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        FacebookHelper fbConnectHelper = new FacebookHelper(MainActivity.this);
        fbConnectHelper.shareOnFBWall("Social Login", "Android Facebook and Google+ Login",
            URL);
      }
    });

    //----------------------------------Google +Sign in-----------------------------------//
    googleSignInHelper = new GoogleSignInHelper(this, this);
    googleSignInHelper.connect();
    gSignInButton = (Button) findViewById(R.id.main_g_sign_in_button);
    gSignInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        googleSignInHelper.signIn();
        isFbLogin = false;
      }
    });

    //----------------------------------Twitter Sign in button ------------------------------//
    tSignInButton = (Button) findViewById(R.id.main_twitter_sign_in_button);
    tSignInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        twitterHelper.connect();
        isFbLogin = false;
      }
    });

    //----------------------------------Linked In Sign In Button ------------------------------//
    linkedInSignInHelper = new LinkedInSignInHelper(this, this);
    linkedInSignInHelper.connect();
    lSignInButton = (Button) findViewById(R.id.main_linked_in_sign_in_button);
    lSignInButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        linkedInSignInHelper.signIn();
      }
    });
  }

  @Override protected void onStart() {
    super.onStart();
    googleSignInHelper.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    googleSignInHelper.onActivityResult(requestCode, resultCode, data);
    fbConnectHelper.onActivityResult(requestCode, resultCode, data);
    twitterHelper.onActivityResult(requestCode, resultCode, data);
    linkedInSignInHelper.onActivityResult(requestCode, resultCode, data);
    if (isFbLogin) {
      progressBar.setVisibility(View.VISIBLE);
      isFbLogin = false;
    }
  }

  @Override
  public void OnTwitterSignInComplete(TwitterHelper.UserDetails userDetails, String error) {
    progressBar.setVisibility(View.GONE);
    if (userDetails != null) {
      userName.setText(userDetails.getUserName());
      if (userDetails.getUserEmail() != null) {
        email.setText(userDetails.getUserEmail());
      }
    }
    twitterHelper.postTweet(URL, null, false, null, null, null, false, false, null);
  }

  @Override
  public void OnTweetPostComplete(Result<Tweet> result, String error) {

  }

  @Override
  public void OnFbSignInComplete(GraphResponse graphResponse, String error) {
    progressBar.setVisibility(View.GONE);
    if (error == null) {
      try {
        JSONObject jsonObject = graphResponse.getJSONObject();
        userName.setText(jsonObject.getString("name"));
        email.setText(jsonObject.getString("email"));
        String id = jsonObject.getString("id");
        String profileImg = "http://graph.facebook.com/" + id + "/picture?type=large";
      } catch (JSONException e) {
        Log.i(TAG, e.getMessage());
      }
    }
  }

  @Override public void OnGSignInSuccess(GoogleSignInAccount googleSignInAccount) {
    if (googleSignInAccount != null) {
      userName.setText(googleSignInAccount.getGivenName() + googleSignInAccount.getFamilyName());
      email.setText(googleSignInAccount.getEmail());
    }
  }

  @Override public void OnGSignInError(String error) {
    Log.e(TAG, error);
  }

  @Override public void onAPICallStarted() {
    Log.i(TAG, "onAPICallStarted");
  }

  @Override public void OnLinkedInSignInSuccess(String response) {
    try {
      JSONObject object = new JSONObject(response);
      String firstName = object.has("firstName") ? object.getString("firstName") : "";
      String lastName = object.has("lastName") ? object.getString("lastName") : "";
      String emailAddress = object.has("emailAddress") ? object.getString("emailAddress") : "";
      userName.setText(firstName + lastName);
      email.setText(emailAddress);
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage());
    }
  }

  @Override public void onLinkedInSignError(String error) {
    Log.e(TAG, error);
  }
}
