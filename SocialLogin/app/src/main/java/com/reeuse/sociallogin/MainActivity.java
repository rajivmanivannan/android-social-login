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
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.model.people.Person;
import com.reeuse.sociallogin.helper.FacebookHelper;
import com.reeuse.sociallogin.helper.GooglePlusHelper;
import com.reeuse.sociallogin.helper.TwitterHelper;
import com.reeuse.sociallogin.utils.KeyHashGenerator;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements FacebookHelper.OnFbSignInListener, GooglePlusHelper.OnGoogleSignInListener, TwitterHelper.OnTwitterSignInListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    //--------------------------------Facebook login--------------------------------------//
    private FacebookHelper fbConnectHelper;
    private Button fbSignInButton;
    private Button fbShareButton;
    private TextView fbName;
    private TextView fbEmail;
    //--------------------------------Google One Plus--------------------------------------//
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "https://github.com/RajivManivannan/Android-Social-Login";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    //Google 1+ button
    private PlusOneButton mPlusOneButton;
    //----------------------------------Google +Sign in-----------------------------------//
    //Google plus sign-in button
    private GooglePlusHelper gSignInHelper;
    private Button gSignInButton;
    private TextView gplusName;
    private TextView gplusEmail;
    //-----------------------------------Twitter Sign In -----------------------------------//
    private TwitterHelper mTwitterHelper;
    private Button tSignInButton;
    private TextView twitterName;
    private TextView twitterEmail;

    private ProgressBar progressBar;
    private boolean isFbLogin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTwitterHelper = new TwitterHelper(this, this); // Twitter Initialization
        FacebookSdk.sdkInitialize(getApplicationContext()); // Facebook SDK Initialization
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //--------------------------------Facebook login--------------------------------------//
        KeyHashGenerator.generateKey(this);
        fbConnectHelper = new FacebookHelper(this, this);

        fbName = (TextView) findViewById(R.id.main_fb_name_txt);
        fbEmail = (TextView) findViewById(R.id.main_fb_email_txt);
        fbSignInButton = (Button) findViewById(R.id.fb_sign_in_button);
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
                fbConnectHelper.shareOnFBWall("Social Login", "Android Facebook and Google+ Login", PLUS_ONE_URL);
            }
        });

        //----------------------------------Google +Sign in-----------------------------------//
        gSignInHelper = new GooglePlusHelper(this, this);

        gplusName = (TextView) findViewById(R.id.main_g_name_txt);
        gplusEmail = (TextView) findViewById(R.id.main_g_email_txt);
        gSignInButton = (Button) findViewById(R.id.main_g_sign_in_button);
        gSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                gSignInHelper.connect();
                isFbLogin = false;
            }
        });
        //----------------------------------For  the +1 button-----------------------------------//
        mPlusOneButton = (PlusOneButton) findViewById(R.id.plus_one_button);

        //----------------------------------Twitter Sign in button ------------------------------//
        twitterName = (TextView) findViewById(R.id.main_twitter_name_txt);
        twitterEmail = (TextView) findViewById(R.id.main_twitter_email_txt);
        tSignInButton = (Button) findViewById(R.id.main_twitter_sign_in_button);
        tSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mTwitterHelper.connect();
                isFbLogin = false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the state of the +1 button each time the activity receives focus.
        mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gSignInHelper.onActivityResult(requestCode, resultCode, data);
        fbConnectHelper.onActivityResult(requestCode, resultCode, data);
        mTwitterHelper.onActivityResult(requestCode, resultCode, data);
        if (isFbLogin) {
            progressBar.setVisibility(View.VISIBLE);
            isFbLogin = false;
        }
    }

    @Override
    public void OnTwitterSignInComplete(TwitterHelper.UserDetails userDetails, String error) {
        progressBar.setVisibility(View.GONE);
        if (userDetails != null)
            twitterName.setText(userDetails.getUserName());
        if (userDetails.getUserEmail() != null)
            twitterEmail.setText(userDetails.getUserEmail());
    }

    @Override
    public void OnFbSignInComplete(GraphResponse graphResponse, String error) {
        progressBar.setVisibility(View.GONE);
        if (error != null) {
            try {
                JSONObject jsonObject = graphResponse.getJSONObject();
                fbName.setText(jsonObject.getString("name"));
                fbEmail.setText(jsonObject.getString("email"));
                String id = jsonObject.getString("id");
                String profileImg = "http://graph.facebook.com/" + id + "/picture?type=large";
                Log.i(TAG, profileImg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnGSignInComplete(Person mPerson, String emailAddress, String error) {
        progressBar.setVisibility(View.GONE);
        if (mPerson != null)
            gplusName.setText(mPerson.getDisplayName());
        if (emailAddress != null)
            gplusEmail.setText(emailAddress);
    }
}
