package fr.melanoxy.go4lunch;

import static android.content.ContentValues.TAG;

import static fr.melanoxy.go4lunch.BuildConfig.MAPS_API_KEY;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.databinding.ActivityMainBinding;
import fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity.RestaurantDetailsActivity;
import fr.melanoxy.go4lunch.utils.NotifyWorker;
import fr.melanoxy.go4lunch.utils.ViewModelFactory;

public class MainActivity extends AppCompatActivity {

    // initialize variables
    private ActivityMainBinding mMainActivityBinding;
    private MainActivityViewModel mMainActivityViewModel;
    private WorkManager mWorkManager;
    private final String mTagUniqueWork ="notifyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding ActivityMain layout
        mMainActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mMainActivityBinding.getRoot();
        setContentView(view);

        //Associating ViewModel with the Activity
        initMainActivityViewModel();

        checkIfUserIsAuthenticated();

        //Configure the action bar
        setSupportActionBar(mMainActivityBinding.activityMainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        //Configure Drawer
        setupDrawer(mMainActivityBinding.activityMainNavViewDrawer);

        //Configure NavController with the BottomNavigationView.
        setupBottomNav();

        //configure GPS NearbyPlaceLocation button (top-right)
        setupFab();

        //setup Notification
        setupNotify();

    }

   private void setupNotify() {

       mMainActivityViewModel.getNotifyStateLiveData().observe(this, user -> {

           if(user.getRestaurant_for_today_name()!=null) {

//Delay in minutes for next lunch/noon time:
               LocalDateTime now = LocalDateTime.now();
               LocalDateTime lunchDateTimeForToday = LocalDateTime.of(LocalDate.now(), LocalTime.NOON);

               Duration duration = Duration.between(now,//and next lunchtime.
                       (now.isAfter(lunchDateTimeForToday)) ? LocalDateTime.of(LocalDate.from(now.plusDays(1)), LocalTime.NOON) : lunchDateTimeForToday);
               long delay = Math.abs(duration.toMinutes());

//Workmanager for notification
               mWorkManager = WorkManager.getInstance(getApplicationContext());


               Data data = new Data.Builder()
                       .putString(NotifyWorker.EXTRA_USER_ID, user.getUid())
                       .build();

//need internet
               Constraints constraints = new Constraints.Builder()
                       .setRequiredNetworkType(NetworkType.CONNECTED)
                       .build();

//'run only one' time request
               OneTimeWorkRequest uploadWorkRequest =
                       new OneTimeWorkRequest.Builder(NotifyWorker.class)
                               .setInputData(data)//Restaurant for today info
                               .setConstraints(constraints)//Internet required
                               .setInitialDelay(delay, TimeUnit.MINUTES)
                               .addTag(mTagUniqueWork)//tag for canceling a simple work request
                               .build();

            /*with 'ExistingPeriodicWorkPolicy.KEEP'.
                It will run the new PeriodicWorkRequest only
                if there is no pending work labelled with uniqueWorkName*/

               mWorkManager.enqueueUniqueWork(mTagUniqueWork, ExistingWorkPolicy.KEEP, uploadWorkRequest);
           }else{mWorkManager.cancelUniqueWork(mTagUniqueWork);}
       });
       }


    @Override
    public void onResume() {
        super.onResume();
        //CHECK if gps location is still granted
        mMainActivityViewModel.refresh();

    }

    private void initMainActivityViewModel() {
        mMainActivityViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance())
                .get(MainActivityViewModel.class);
    }

    private void  checkIfUserIsAuthenticated(){
        if (!mMainActivityViewModel.isUserAuthenticated()){startSignInActivity();}
    }

    private void startSignInActivity(){

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder().setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.logo_png_format)
                .build();

        signInLauncher.launch(signInIntent);

    }
    //Create an ActivityResultLauncher which registers a callback for the FirebaseUI Activity result contract
    //See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    // Method that handles response after SignIn Activity close
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            mMainActivityViewModel.onUserLoggedSuccess();//the system will know that the user is log.
            showSnackBar(getString(R.string.connection_succeed));//send welcome message.
            setupPermissions();//ask gps location permission

        } else {
            // ERRORS (Toast because we will leave this activity after)
            if (response == null) {
                Toast.makeText(getApplicationContext(),R.string.error_authentication_canceled,Toast.LENGTH_SHORT).show();
                //showSnackBar(getString(R.string.error_authentication_canceled));
                finish();
            } else if (response.getError()!= null) {
                if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    Toast.makeText(getApplicationContext(),R.string.error_no_internet,Toast.LENGTH_SHORT).show();
                    startSignInActivity();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(getApplicationContext(),R.string.error_unknown_error,Toast.LENGTH_SHORT).show();
                    startSignInActivity();
                }
            }
        }
    }

    private void setupPermissions() {
        //GPS LOCATION PERMISSIONS
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                0
        );
    }

    // Show Snack Bar with a message (bg/text color custom)
    public void showSnackBar(String message){
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.CustomSnackbarTheme);
        Snackbar.make(ctw, mMainActivityBinding.activityMainDrawerLayout, message, Snackbar.LENGTH_LONG)
                .setTextColor(Color.WHITE)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        //Listener for searchfield
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//              if (searchView.isExpanded() && TextUtils.isEmpty(newText)) {
                //callSearch(newText);
//              }
                return true;
            }

            public void callSearch(String query) {
                //Do searching
                mMainActivityViewModel.onSearchQueryCall(query);
                //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                //searchView.setIconified(true);
                //searchView.onActionViewCollapsed();
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mMainActivityViewModel.onSearchQueryCall(null);
                return false;
            }
        });

        // To show icons in the actionbar's overflow menu:
        //if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "onMenuOpened", e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mMainActivityBinding.activityMainDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
//TODO Search BAR
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ---------------- DRAWER ---------------- //
    private void setupDrawer(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });

        //SingleLiveEvent to launch restaurant details activity
        mMainActivityViewModel.getRestaurantDetailsActivitySingleLiveEvent().observe(this, item -> {
            startActivity(RestaurantDetailsActivity.navigate(this, item));
        });

        mMainActivityViewModel.getConnectedUserLiveData().observe(this, user -> {

            //setup: name, mail and pfp in drawer

            //NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view_drawer);
            View headerContainer = navigationView.getHeaderView(0); // This returns the container layout from your navigation drawer header layout file (e.g., the parent RelativeLayout/LinearLayout in your my_nav_drawer_header.xml file)

            TextView username = (TextView)headerContainer.findViewById(R.id.drawer_header_username);
            username.setText(user.username);
            TextView email = (TextView)headerContainer.findViewById(R.id.drawer_header_email);
            email.setText(user.email);

            if (user.getUrlPicture()!=null)   {
                Glide.with(navigationView.getContext())
                        .load(user.getUrlPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into((ImageView) headerContainer.findViewById(R.id.drawer_header_pfp));
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.drawer_menu_item_yourlunch:
                mMainActivityViewModel.onYourLunchClicked();
                break;
            case R.id.drawer_menu_item_settings:
                //TODO launch settings details
                break;
            case R.id.drawer_menu_item_logout:
                mMainActivityViewModel.onSignOut(this).addOnSuccessListener(aVoid -> {
                    mWorkManager.cancelUniqueWork(mTagUniqueWork);
                    startSignInActivity();
                });
                break;
            default:
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Close the navigation drawer
        mMainActivityBinding.activityMainDrawerLayout.closeDrawers();
    }

    // ---------------- GPS LOCATION PERMISSION FAB ---------------- //
    private void setupFab() {

        mMainActivityBinding.activityMainFabMylocation.setOnClickListener(v ->
                setupPermissions()
        );

        mMainActivityViewModel.getIsGpsPermissionGrantedLiveData().observe(this, permission -> {
            if (permission) {
                mMainActivityBinding.activityMainFabMylocation.setImageResource(R.drawable.ic_gps_fixed_24dp);
            } else {
                mMainActivityBinding.activityMainFabMylocation.setImageResource(R.drawable.ic_gps_off_24dp);
            }
        });


        //Observe userLocation then ask for NearbyRestaurants if location is not null or changed
        mMainActivityViewModel.getUserLocationLiveData().observe(this, userLocation -> {
            if (userLocation!=null){
                mMainActivityViewModel.searchNearbyRestaurant(
                        userLocation,
                        "2000",//in meters
                        "restaurant",//keyword see list here https://developers.google.com/maps/documentation/places/web-service/supported_types
                        MAPS_API_KEY
                );
            }});


    }

    // ---------------- BOTTOM NAV ---------------- //
    private void setupBottomNav() {

        NavController mNavController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment);
        NavigationUI.setupWithNavController(mMainActivityBinding.activityMainBottomNavigationView, mNavController);
        //fab remove when not in mapview
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            MainActivity.this.getSupportActionBar().setTitle(destination.getLabel());

            //Change icon on fab according to gps access permission
            switch (destination.getId()) {
                case R.id.nav_menu_item_mapview:
                case R.id.nav_menu_item_listview:
                    mMainActivityBinding.activityMainFabMylocation.show();
                    break;
                default: mMainActivityBinding.activityMainFabMylocation.hide();
                    break;
            }
        });
    }

}//END MainActivity