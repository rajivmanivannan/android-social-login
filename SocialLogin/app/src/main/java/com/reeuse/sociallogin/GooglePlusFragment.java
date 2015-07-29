package com.reeuse.sociallogin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.model.people.Person;
import com.reeuse.sociallogin.helper.GooglePlusSignInHelper;

/**
 * A fragment with a Google+ sign in and +1 button.
 */
public class GooglePlusFragment extends Fragment  implements GooglePlusSignInHelper.OnGoogleSignInListener {
    private static final String TAG = GooglePlusFragment.class.getSimpleName();

    //--------------------------------Google One Plus--------------------------------------//
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com/";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    //Google 1+ button
    private PlusOneButton mPlusOneButton;
    TextView  ownerName;

    //----------------------------------Google +Sign in-----------------------------------//
    //Google plus sign-in button
    private SignInButton mSignInButton;
    private GooglePlusSignInHelper mSignInHelper;
    public GooglePlusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_google_plus, container, false);

        mSignInHelper = new GooglePlusSignInHelper(getActivity(),this);
        //For  the +1 button
        mPlusOneButton = (PlusOneButton) view.findViewById(R.id.plus_one_button);
          ownerName = (TextView)view.findViewById(R.id.name_txt);

        //----------------------------------below implementation for Google +Sign in-----------------------------------//

        mSignInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);// wide button style
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignInHelper.connect();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the state of the +1 button each time the activity receives focus.
        mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);

    }

    @Override
    public void onStart() {
        super.onStart();
        mSignInHelper.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mSignInHelper.onStop();
    }

    @Override
    public void OnSuccess(Person mPerson) {
        ownerName.setText(mPerson.getDisplayName());
        Log.i(TAG,mPerson.getDisplayName());
    }

    @Override
    public void OnError(String errorMessage) {
        Log.i(TAG,errorMessage);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSignInHelper.onActivityResult(requestCode,resultCode,data);
    }
}