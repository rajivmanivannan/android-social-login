package com.reeuse.sociallogin;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reeuse.sociallogin.utils.KeyHashGenerator;

/**
 * Facebook login and share
 */
public class FacebookFragment extends Fragment {

    public FacebookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_facebook, container, false);


        String keyHash = KeyHashGenerator.generateKey(getActivity());
        Log.i("keyHash", keyHash);

        return view;
    }




}
