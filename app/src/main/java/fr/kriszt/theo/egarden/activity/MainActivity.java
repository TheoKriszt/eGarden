package fr.kriszt.theo.egarden.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

//import fr.kriszt.theo.egarden.fragment.DownloadClientPlantsImgsFragment;
import fr.kriszt.theo.egarden.R;
import fr.kriszt.theo.egarden.fragment.DownloadClientPlantsImgsFragment;
//import fr.kriszt.theo.egarden.fragment.GardenImgs;
import fr.kriszt.theo.egarden.fragment.GardenImgs;
import fr.kriszt.theo.egarden.fragment.PlantDetailsFragment;
import fr.kriszt.theo.egarden.fragment.PlantsListFragment;
import fr.kriszt.theo.egarden.fragment.DashboardFragment;
import fr.kriszt.theo.egarden.fragment.NotificationsFragment;
import fr.kriszt.theo.egarden.fragment.SettingsFragment;
import fr.kriszt.theo.egarden.fragment.TimelapseViewFragment;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "eGardenDashBoard";


//    private static boolean TEST = true;
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
    private static final String TAG_PLANTS_STATE = "garden_details";
    private static final String TAG_STREAM_IMGS ="stream_imgs" ;
//    private static final String TAG_DWNLOAD ="download_imgs" ;
    private static final String TAG_TIMELAPSE ="timelapse" ;
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_DASHBOARD;

//    private static final String TAG_STATS = "stats";
//    private static final String TAG_SHOOTINGS = "shootings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
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

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

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

//            case 2:
//                return new RequestImagePlantFragment();

            case 1:
                return new PlantsListFragment();

            case 2:
                return new GardenImgs();


//            case 3:
//                return new DownloadClientPlantsImgsFragment();

//            case 6:
//                return new StatsFragment();

            case 3 :
                return new TimelapseViewFragment();

            case 4:
                return new NotificationsFragment();

            case 5:
                return new SettingsFragment();



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
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_DASHBOARD;
                        break;


//                    case R.id.nav_shooting:
//                        navItemIndex = 2;
//                        CURRENT_TAG = TAG_SHOOTINGS;
//                        break;

                    case R.id.nav_plants_state:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PLANTS_STATE;
                        break;

                    case R.id.nav_stream_imgs :
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_STREAM_IMGS;
                        break;

//                    case R.id.nav_download_imgs:
//                        navItemIndex = 3;
//                        CURRENT_TAG = TAG_DWNLOAD;
//                        break;
//                    case R.id.nav_statistics:
//                        navItemIndex = 6;
//                        CURRENT_TAG = TAG_STATS;
//                        break;

                    case R.id.nav_timelapse:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_TIMELAPSE;
                        break;

                    case R.id.nav_notifications:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;


                    case R.id.nav_settings:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);


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

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }else if (CURRENT_TAG.equals(PlantDetailsFragment.TAG)){
            navItemIndex = 1;
//            CURRENT_TAG = TAG_STREAM_IMGS;
            CURRENT_TAG = TAG_PLANTS_STATE;
            loadHomeFragment();
            return;
        }else
        // This code loads home fragment when back key is pressed
        if (navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_DASHBOARD;
            loadHomeFragment();
            return;
        }

        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        switch (item.getItemId()) {
//            case android.R.id.home:
//                drawer.openDrawer(GravityCompat.START);
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

}


