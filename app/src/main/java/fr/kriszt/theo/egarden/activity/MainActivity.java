package fr.kriszt.theo.egarden.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

//import fr.kriszt.theo.egarden.fragment.DownloadClientPlantsImgsFragment;
import fr.kriszt.theo.egarden.R;
import fr.kriszt.theo.egarden.fragment.DownloadClientPlantsImgsFragment;
//import fr.kriszt.theo.egarden.fragment.GardenImgs;
import fr.kriszt.theo.egarden.fragment.PlantDetailsFragment;
import fr.kriszt.theo.egarden.fragment.PlantsListFragment;
import fr.kriszt.theo.egarden.fragment.DashboardFragment;
import fr.kriszt.theo.egarden.fragment.NotificationsFragment;
import fr.kriszt.theo.egarden.fragment.RequestImagePlantFragment;
import fr.kriszt.theo.egarden.fragment.SettingsFragment;
import fr.kriszt.theo.egarden.fragment.StatsFragment;
import fr.kriszt.theo.egarden.fragment.TimelapseViewFragment;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "eGardenDashBoard";


    private static boolean TEST = true;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private Handler mHandler;


    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_DASHBOARD = "dashboard";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SHOOTINGS = "shootings";
    private static final String TAG_GARDEN = "garden_details";
    private static final String TAG_STATS = "stats";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_STREAM_IMGS ="stream_imgs" ;
    private static final String TAG_DWNLOAD ="download_imgs" ;
    private static final String TAG_TIMELAPSE ="timelapse" ;

    public static String CURRENT_TAG = TAG_DASHBOARD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_DASHBOARD;
            loadHomeFragment();
        }

    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
//                        android.R.anim.fade_out);

                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return new DashboardFragment();
            case 1:
                return new NotificationsFragment();
            case 2:
                return new RequestImagePlantFragment();

            case 3:
                return new PlantsListFragment();

            case 4:
                return null;//new GardenImgs();

            case 5:
                return new DownloadClientPlantsImgsFragment();

            case 6:
                return new StatsFragment();

            case 7:
                return new SettingsFragment();

            case 8 :
                return new TimelapseViewFragment();

            default:
                return new DashboardFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

//                Log.w(TAG, "onNavigationItemSelected: old tag : " + CURRENT_TAG);


                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_DASHBOARD;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_shooting:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SHOOTINGS;
                        break;
                    case R.id.nav_stream_imgs :
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_STREAM_IMGS;
                        break;
                    case R.id.garden:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_GARDEN;
                        break;
                    case R.id.dwn_imgs:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_DWNLOAD;
                        break;
                    case R.id.nav_statistics:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_STATS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_timelapse:
                        navItemIndex = 8;
                        CURRENT_TAG = TAG_TIMELAPSE;
                        break;

//                    case R.id.nav_about_us:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
//                        drawer.closeDrawers();
//                        return true;
//                    case R.id.nav_privacy_policy:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
//                        drawer.closeDrawers();
//                        return true;
                    default:
//                        Log.e(TAG, "onNavigationItemSelected: CAS DEFAUT");
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

//                Log.w(TAG, "onNavigationItemSelected() returned: " + CURRENT_TAG);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle); // deprecated
//        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        boolean shouldLoadHomeFragOnBackPress = true;


        //Toast.makeText(this, "BackPressed from " + CURRENT_TAG, Toast.LENGTH_SHORT).show();
        if (CURRENT_TAG.equals(PlantDetailsFragment.TAG)){
            navItemIndex = 3;
            CURRENT_TAG = TAG_STREAM_IMGS;
            loadHomeFragment();
            return;
        }

        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_DASHBOARD;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        /*
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_logout) {
//            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
//            return true;
//        }
//
//        // user is in notifications fragment
//        // and selected 'Mark all as Read'
//        if (id == R.id.action_mark_all_read) {
//            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
//        }
//
//        // user is in notifications fragment
//        // and selected 'Clear All'
//        if (id == R.id.action_clear_notifications) {
//            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
//        }
        return super.onOptionsItemSelected(item);
    }

}
