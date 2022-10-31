package fr.melanoxy.go4lunch;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Method;

import fr.melanoxy.go4lunch.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // initialize variables
    private ActivityMainBinding mMainActivityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding ActivityMain layout
        mMainActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mMainActivityBinding.getRoot();
        setContentView(view);

        //Configure the action bar
        setSupportActionBar(mMainActivityBinding.activityMainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        // Setup for Drawer
       setupDrawerContent(mMainActivityBinding.activityMainNavViewDrawer);

        //Setup for NavController with the BottomNavigationView.
        NavController navController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment);
        NavigationUI.setupWithNavController(mMainActivityBinding.activityMainBottomNavigationView, navController);
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
                /*if (binding.activityMainViewpager.getCurrentItem() == 0) {
                    binding.activityMainViewpager.setCurrentItem(1);
                } else {
                    onBackPressed();
                }*/
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
    }

    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.drawer_menu_item_yourlunch:
                break;
            case R.id.drawer_menu_item_settings:
                break;
            case R.id.drawer_menu_item_logout:
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


}//END