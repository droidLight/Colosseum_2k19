package colosseum19.a300dpi.colosseum2k19;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;
import butterknife.ButterKnife;
import colosseum19.a300dpi.colosseum2k19.API.BackupApi;
import colosseum19.a300dpi.colosseum2k19.Fragments.AboutFragment;
import colosseum19.a300dpi.colosseum2k19.Fragments.EventsFragment;
import colosseum19.a300dpi.colosseum2k19.Fragments.FixturesFragment;
import colosseum19.a300dpi.colosseum2k19.Fragments.ScoresFragment;
import colosseum19.a300dpi.colosseum2k19.Interfaces.DateInterface;
import colosseum19.a300dpi.colosseum2k19.Utilities.ConstantsStorage;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener, DateInterface {

    //Keys for Fragments
    private static String EVENTS_FRAG = "events_frag";
    private static String FIXTURES_FRAG = "fixtures_frag";
    private static String SCORES_FRAG = "scores_frag";
    private static String ABOUT_FRAG = "about_frag";

    //Keys for SavedInstanceState
    private static String BUNDLE_KEY_EVENTS_FRAG = "events_frag_bundle";
    private static String BUNDLE_KEY_FIXTURES_FRAG = "refixtures_frag_bundle";
    private static String BUNDLE_KEY_SCORES_FRAG = "scores_frag_bundle";
    private static String BUNDLE_KEY_ABOUT_FRAG = "about_frag_bundle";
    private static String BUNDLE_KEY_CURRENTLY_SELECTED_BOTTOM_NAV = "currently_selected_bottom_navigation_view";

    private static boolean isBackDoubledPressed = false;

    private FragmentManager fragmentManager;
    private EventsFragment eventsFragment;
    private FixturesFragment fixturesFragment;
    private ScoresFragment scoresFragment;
    private AboutFragment aboutFragment;
    private BackupApi backupApi;

    private String currentlySelected = EVENTS_FRAG;



    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_navi_view)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        backupApi = new BackupApi();
        backupApi.getDate(getApplicationContext(), this);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);

        FirebaseMessaging.getInstance().subscribeToTopic("participants").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("subscribeToTopic: ", "Success");
            }
        });

        if (savedInstanceState == null){

            eventsFragment = new EventsFragment();
            fixturesFragment = new FixturesFragment();
            scoresFragment = new ScoresFragment();
            aboutFragment = new AboutFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.frame_layout_home_activity, aboutFragment, ABOUT_FRAG)
                    .add(R.id.frame_layout_home_activity, fixturesFragment, FIXTURES_FRAG)
                    .add(R.id.frame_layout_home_activity, scoresFragment, SCORES_FRAG)
                    .replace(R.id.frame_layout_home_activity, eventsFragment, EVENTS_FRAG)
                    .commit();
        }else {
            currentlySelected = savedInstanceState.getString(BUNDLE_KEY_CURRENTLY_SELECTED_BOTTOM_NAV);

            if (currentlySelected.equals(EVENTS_FRAG)){

                eventsFragment = (EventsFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_KEY_EVENTS_FRAG);
                if (eventsFragment == null) {
                    eventsFragment = new EventsFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_home_activity, eventsFragment, EVENTS_FRAG)
                        .commit();
            }else if (currentlySelected.equals(FIXTURES_FRAG)){

                fixturesFragment = (FixturesFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_KEY_FIXTURES_FRAG);
                if (fixturesFragment == null) {
                    fixturesFragment = new FixturesFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_home_activity, fixturesFragment, FIXTURES_FRAG)
                        .commit();
            }else if (currentlySelected.equals(SCORES_FRAG)){

                scoresFragment = (ScoresFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_KEY_SCORES_FRAG);
                if (scoresFragment == null) {
                    scoresFragment = new ScoresFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_home_activity, scoresFragment, SCORES_FRAG)
                        .commit();
            }else if (currentlySelected.equals(ABOUT_FRAG)){

                aboutFragment = (AboutFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_KEY_ABOUT_FRAG);
                if (aboutFragment == null) {
                    aboutFragment = new AboutFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_home_activity, aboutFragment, ABOUT_FRAG)
                        .commit();
            }
        }

    }

    @Override
    public void setDate(String date, boolean isSuccess) {
        if(isSuccess){
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.day_key), date);
            editor.apply();
        }else{
            Toast.makeText(this, "Failed to get current date", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY_CURRENTLY_SELECTED_BOTTOM_NAV, currentlySelected);
        if (currentlySelected.equals(EVENTS_FRAG)){
            if (eventsFragment != null && getSupportFragmentManager().findFragmentByTag(EVENTS_FRAG).isAdded()) {
                getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_EVENTS_FRAG, eventsFragment);
            }
        }else if (currentlySelected.equals(FIXTURES_FRAG)){
            if (fixturesFragment != null && getSupportFragmentManager().findFragmentByTag(FIXTURES_FRAG).isAdded()) {
                getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_FIXTURES_FRAG, fixturesFragment);
            }
        }else if (currentlySelected.equals(SCORES_FRAG)){
            if (scoresFragment != null && getSupportFragmentManager().findFragmentByTag(SCORES_FRAG).isAdded()) {
                getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_SCORES_FRAG, scoresFragment);
            }
        }else if (currentlySelected.equals(ABOUT_FRAG)){
            if (aboutFragment != null && getSupportFragmentManager().findFragmentByTag(ABOUT_FRAG).isAdded()) {
                getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_ABOUT_FRAG, aboutFragment);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_feedback) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:colosseum@sastra.ac.in"));
            startActivity(intent);
            return true;
        }else if(id == R.id.action_share){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String url = ConstantsStorage.APP_URL;
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        }else if (id == R.id.action_sign_out) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){

            case R.id.bottom_nav_events:
                currentlySelected = EVENTS_FRAG;
                if (fragmentManager != null){
                    eventsFragment = new EventsFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layout_home_activity, eventsFragment, EVENTS_FRAG)
                            .commit();
                }
                return true;

            case R.id.bottom_nav_fixtures:
                currentlySelected = FIXTURES_FRAG;
                fixturesFragment = new FixturesFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_home_activity, fixturesFragment, FIXTURES_FRAG)
                        .commit();
                return true;

            case R.id.bottom_nav_scores:
                currentlySelected = SCORES_FRAG;
                scoresFragment = new ScoresFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_home_activity, scoresFragment, SCORES_FRAG)
                        .commit();
                return true;

            case R.id.bottom_nav_about:
                currentlySelected = ABOUT_FRAG;
                aboutFragment = new AboutFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_home_activity, aboutFragment, ABOUT_FRAG)
                        .commit();
                return true;
        }
        return false;
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem menuItem) {

    }

    @Override
    public void onBackPressed() {
        if (isBackDoubledPressed){
            super.onBackPressed();
            exitFromApplication();
        }
        isBackDoubledPressed = true;
        Toast.makeText(this, "Please click again to exit", Toast.LENGTH_SHORT).show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isBackDoubledPressed = false;
            }
        },2000);
    }
    private void exitFromApplication(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
