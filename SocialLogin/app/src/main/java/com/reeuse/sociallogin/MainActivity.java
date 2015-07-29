package com.reeuse.sociallogin;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    private static final int FACEBOOK_FRAGMENT = 0;
    private static final int GOOGLE_PLUS_FRAGMENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * Displaying fragment view for selected navigation drawer list item
         */
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_facebook){
            displayView(FACEBOOK_FRAGMENT);
            return true;
        }else if (id == R.id.action_google_plus){
            displayView(GOOGLE_PLUS_FRAGMENT);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
        private void displayView(int position) {
            // update the main content by replacing fragments
            Fragment fragment = null;
            String fragmentName = null;
            switch (position) {
                case FACEBOOK_FRAGMENT:
                    fragment = new FacebookFragment();
                    fragmentName = FacebookFragment.class.getSimpleName();
                    break;
                case GOOGLE_PLUS_FRAGMENT:
                    fragment = new GooglePlusFragment();
                    fragmentName = GooglePlusFragment.class.getSimpleName();
                    break;
                default:
                    break;
            }
            if (fragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
            }
        }


        @Override
        public void onBackPressed() {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
}
