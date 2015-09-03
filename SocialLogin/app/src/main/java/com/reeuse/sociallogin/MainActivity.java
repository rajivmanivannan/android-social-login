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
import com.reeuse.sociallogin.helper.FbConnectHelper;
import com.reeuse.sociallogin.helper.GooglePlusSignInHelper;
import com.reeuse.sociallogin.utils.KeyHashGenerator;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements FbConnectHelper.OnFbSignInListener, GooglePlusSignInHelper.OnGoogleSignInListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    //--------------------------------Facebook login--------------------------------------//
    private FbConnectHelper fbConnectHelper;
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
    private GooglePlusSignInHelper gSignInHelper;
    private Button gSignInButton;
    private TextView gplusName;
    private TextView gplusEmail;

    private ProgressBar progressBar;
    private boolean isFbLogin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // Facebook SDK Initialization
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //--------------------------------Facebook login--------------------------------------//
        KeyHashGenerator.generateKey(this);
        fbConnectHelper = new FbConnectHelper(this,this);

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
                FbConnectHelper fbConnectHelper = new FbConnectHelper(MainActivity.this);
                fbConnectHelper.shareOnFBWall("Social Login","Android Facebook and Google+ Login",PLUS_ONE_URL);
            }
        });

        //----------------------------------Google +Sign in-----------------------------------//
        gSignInHelper = new GooglePlusSignInHelper(this, this);

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
        if(isFbLogin) {
            progressBar.setVisibility(View.VISIBLE);
            isFbLogin = false;
        }
    }

    @Override
    public void OnFbSuccess(GraphResponse graphResponse) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject jsonObject = graphResponse.getJSONObject();
            fbName.setText(jsonObject.getString("name"));
            fbEmail.setText(jsonObject.getString("email"));
            String id = jsonObject.getString("id");
            String profileImg = "http://graph.facebook.com/"+ id+ "/picture?type=large";
            Log.i(TAG,profileImg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnFbError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        Log.e(TAG, errorMessage);
    }


    @Override
    public void OnGSignSuccess(Person mPerson,String emailAddress) {
        progressBar.setVisibility(View.GONE);
        gplusName.setText(mPerson.getDisplayName());
        gplusEmail.setText(emailAddress);
    }

    @Override
    public void OnGSignError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
    }
}
