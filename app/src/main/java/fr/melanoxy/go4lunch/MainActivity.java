package fr.melanoxy.go4lunch;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Arrays;
import java.util.List;

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.databinding.ActivityMainBinding;
import fr.melanoxy.go4lunch.utils.ViewModelFactory;

public class MainActivity extends AppCompatActivity {

    // initialize variables
    private ActivityMainBinding mMainActivityBinding;
    private MainActivityViewModel mMainActivityViewModel;

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

        // Setup for Drawer
        setupDrawerContent(mMainActivityBinding.activityMainNavViewDrawer);

        //Setup for NavController with the BottomNavigationView.
        setupBottomNav();

        //setupFAB GPS Location
        setupFab();

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

    private void  bindDrawer(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view_drawer);
        View headerContainer = navigationView.getHeaderView(0); // This returns the container layout from your navigation drawer header layout file (e.g., the parent RelativeLayout/LinearLayout in your my_nav_drawer_header.xml file)

        mMainActivityViewModel.getConnectedUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
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
            }
        });

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
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    // Method that handles response after SignIn Activity close
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            mMainActivityViewModel.onUserLoggedSuccess();
            showSnackBar(getString(R.string.connection_succeed));

        } else {
            // ERRORS
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

    // Show Snack Bar with a message
    private void showSnackBar(String message){
        Snackbar.make(mMainActivityBinding.activityMainDrawerLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar, menu);

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
            case R.id.top_app_bar_search:
//TODO Search BAR
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // ---------------- DRAWER ---------------- //
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        bindDrawer();
    }

    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.drawer_menu_item_yourlunch:
                break;
            case R.id.drawer_menu_item_settings:
                break;
            case R.id.drawer_menu_item_logout:
                mMainActivityViewModel.onSignOut(this).addOnSuccessListener(aVoid -> {
                    startSignInActivity();
                });
                break;
            default:
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mMainActivityBinding.activityMainDrawerLayout.closeDrawers();
    }

    // ---------------- GPS LOCATION PERMISSION FAB ---------------- //
    private void setupFab() {

        mMainActivityBinding.activityMainFabMylocation.setOnClickListener(v ->
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        0
                )
        );
        mMainActivityViewModel.getIsGpsPermissionGrantedLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean permission) {
                if (permission) {
                    mMainActivityBinding.activityMainFabMylocation.setImageResource(R.drawable.ic_gps_fixed_24dp);
                } else {
                    mMainActivityBinding.activityMainFabMylocation.setImageResource(R.drawable.ic_gps_off_24dp);
                }
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
            if(destination.getId() == R.id.nav_menu_item_mapview) {
                mMainActivityBinding.activityMainFabMylocation.show();
            } else {
                mMainActivityBinding.activityMainFabMylocation.hide();
            }

        });
    }

}//END MainActivity