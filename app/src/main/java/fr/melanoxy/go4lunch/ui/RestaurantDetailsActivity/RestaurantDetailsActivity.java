package fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity;

import static fr.melanoxy.go4lunch.BuildConfig.MAPS_API_KEY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import fr.melanoxy.go4lunch.ui.ListView.RestaurantStateItem;
import fr.melanoxy.go4lunch.utils.ViewModelFactory;

public class RestaurantDetailsActivity extends AppCompatActivity {

    // Initialize variables
    private ActivityRestaurantDetailsBinding mRestaurantDetailsBinding;
    private String phone = null;
    private String webUrl = null;
    private RestaurantDetailsViewModel mViewModel;
    private RestaurantStateItem mItem = null;
    public static Intent navigate(Context context, RestaurantStateItem item) {
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra("item_key", item);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //viewBinding
        mRestaurantDetailsBinding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = mRestaurantDetailsBinding.getRoot();
        setContentView(view);

        //Associating ViewModel with the Activity
        mViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance())
                        .get(RestaurantDetailsViewModel.class);

        setSupportActionBar(mRestaurantDetailsBinding.restaurantDetailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //retrieve the item restaurant
        Intent intent = getIntent();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            mItem = intent.getSerializableExtra("item_key",RestaurantStateItem.class);
        }else{mItem = (RestaurantStateItem) intent.getSerializableExtra("item_key");}
        //Ask for restaurant details
        mViewModel.searchPlaceIdDetails(mItem.getPlace_id(),"opening_hours,website,formatted_phone_number,rating",MAPS_API_KEY);

        //BINDING
        bindLayout(mItem);
        bindRecyclerview(mItem);

        //EXPANDABLE CARDVIEW
        mRestaurantDetailsBinding.restaurantDetailArrowButton.setOnClickListener(v -> {
            // If the CardView is already expanded, set its visibility
            // to gone and change the expand less icon to expand more.
            if (mRestaurantDetailsBinding.restaurantDetailRlHidden.getVisibility() == View.VISIBLE) {
                // The transition of the hiddenView is carried out by the TransitionManager class.
                // Here we use an object of the AutoTransition Class to create a default transition
                TransitionManager.beginDelayedTransition(mRestaurantDetailsBinding.restaurantDetailCvButtons, new AutoTransition());
                mRestaurantDetailsBinding.restaurantDetailRlHidden.setVisibility(View.GONE);
                mRestaurantDetailsBinding.restaurantDetailArrowButton.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
            }

            // If the CardView is not expanded, set its visibility to
            // visible and change the expand more icon to expand less.
            else {
                TransitionManager.beginDelayedTransition(mRestaurantDetailsBinding.restaurantDetailCvButtons, new AutoTransition());
                mRestaurantDetailsBinding.restaurantDetailRlHidden.setVisibility(View.VISIBLE);
                mRestaurantDetailsBinding.restaurantDetailArrowButton.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
            }
        });
//FAB listener onRestaurantForToday
        mRestaurantDetailsBinding.restaurantDetailFabToday.setOnClickListener(v -> {
            mViewModel.onRestaurantForTodayClicked(
                    mItem.getPlace_id(),mItem.getPlace_name(),mItem.getPlace_address(), mItem.getPlace_preview_pic_url());
//Ask notification permission
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    0
            );
        });
// phone button listener
        mRestaurantDetailsBinding.restaurantDetailCall.setOnClickListener(v -> {
            if(phone!=null) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(phoneIntent);
            }else{showSnackBar(getString(R.string.no_phone_number));}
        });
// like button listener
        mRestaurantDetailsBinding.restaurantDetailLike.setOnClickListener(v ->
                mViewModel.onFavClicked(mItem.getPlace_id()));
// website button listener
        mRestaurantDetailsBinding.restaurantDetailWebsite.setOnClickListener(v -> {
            if(webUrl!=null) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
        startActivity(browserIntent);
            }else{showSnackBar(getString(R.string.no_web_url));}
            });
    }

    private void bindRecyclerview(RestaurantStateItem item) {
        //Init RecyclerView
        LunchmatesAdapter adapter = new LunchmatesAdapter(uid -> {
        });

        mRestaurantDetailsBinding.restaurantDetailsWorkmatesRv.setAdapter(adapter);

        mViewModel.getLunchmateStateItemsLiveData(item.getPlace_id()).observe(this, adapter::submitList
        );
    }

    private void bindLayout(RestaurantStateItem item) {

        //GENERIC INFO (NearbyRequest + photo)
        mRestaurantDetailsBinding.restaurantDetailTvName.setText(item.getPlace_name());
        mRestaurantDetailsBinding.restaurantDetailTvAddress.setText(item.getPlace_address());

        Glide.with(mRestaurantDetailsBinding.restaurantDetailIvPreview)
                .load(item.getPlace_preview_pic_url())
                .into(mRestaurantDetailsBinding.restaurantDetailIvPreview);

        //UserLiveData INFO
        mViewModel.getUserLiveData().observe(this, user -> {
            //Bookmark icon

                    if ((Objects.equals(user.restaurant_for_today_id, item.getPlace_id()))) {
                        mRestaurantDetailsBinding.restaurantDetailFabToday.setImageResource(R.drawable.ic_bookmark_added_white_24dp);
                    } else {
                        mRestaurantDetailsBinding.restaurantDetailFabToday.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                    }

            //thumb icon
            if (Objects.requireNonNull(user.my_favorite_restaurants).contains(item.getPlace_id())) {
                mRestaurantDetailsBinding.restaurantDetailLike
                        .setCompoundDrawablesWithIntrinsicBounds(
                                null, AppCompatResources.getDrawable(this,R.drawable.ic_favorite_primary_24dp), null, null) ;
            } else {
                mRestaurantDetailsBinding.restaurantDetailLike
                        .setCompoundDrawablesWithIntrinsicBounds(
                                null, AppCompatResources.getDrawable(this,R.drawable.ic_favorite_border_primary_24dp), null, null) ;
            }
                }
        );

        //DETAILS INFO (Restaurant details)
        mViewModel.getRestaurantDetailsResults().observe(this, restaurantDetails -> {
            //Opening hours
            //List<String> list = restaurantDetails.getOpeningHours().getWeekdayText();
            mRestaurantDetailsBinding.restaurantDetailOpeningHours.setText(restaurantDetails.getOpeningHours());

            //Rating
            mRestaurantDetailsBinding.restaurantDetailsStar1.setVisibility(item.getPlace_rating()>=1? View.VISIBLE : View.INVISIBLE);
            mRestaurantDetailsBinding.restaurantDetailsStar2.setVisibility(item.getPlace_rating()>=1.5? View.VISIBLE : View.INVISIBLE);
            mRestaurantDetailsBinding.restaurantDetailsStar3.setVisibility(item.getPlace_rating()>=2.5? View.VISIBLE : View.INVISIBLE);

            //Phone Number of the restaurant
            phone = restaurantDetails.getFormattedPhoneNumber();
            //WEB browser
            webUrl = restaurantDetails.getWebsite();
        }
        );

    }

    // Show Snack Bar with a message
    private void showSnackBar(String message){
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.CustomSnackbarTheme);
        Snackbar.make(ctw, mRestaurantDetailsBinding.restaurantDetailClMain, message, Snackbar.LENGTH_LONG)
                .setTextColor(Color.WHITE)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mViewModel.onBackPressed();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mViewModel.onBackPressed();
        finish();
    }

}