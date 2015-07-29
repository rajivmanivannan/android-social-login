package com.reeuse.sociallogin;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




public class FacebookFragment extends Fragment {



    public FacebookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_facebook, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }






}
