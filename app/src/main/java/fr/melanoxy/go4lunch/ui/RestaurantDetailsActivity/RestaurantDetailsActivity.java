package fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity;

import static fr.melanoxy.go4lunch.BuildConfig.MAPS_API_KEY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;

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

    public static Intent navigate(Context context, RestaurantStateItem item) {
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra("item_key", item);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //viewBinding
        mRestaurantDetailsBinding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = mRestaurantDetailsBinding.getRoot();
        setContentView(view);

        //Associating ViewModel with the Activity
        RestaurantDetailsViewModel viewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance())
                        .get(RestaurantDetailsViewModel.class);

        setSupportActionBar(mRestaurantDetailsBinding.restaurantDetailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //MainActivity.this.getSupportActionBar().setTitle(destination.getLabel());

        Intent intent = getIntent();
        RestaurantStateItem item = (RestaurantStateItem) intent.getSerializableExtra("item_key");
        //Ask for restaurant details
        viewModel.searchPlaceIdDetails(item.getPlace_id(),"opening_hours,website,formatted_phone_number",MAPS_API_KEY);

        //BINDING
        bindLayout(viewModel, item);
        //bindRecyclerview();

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
            viewModel.onRestaurantForTodayClicked(
                    item.getPlace_id(),item.getPlace_name(),item.getPlace_address());
        });
// phone listener
        //TODO like button
        mRestaurantDetailsBinding.restaurantDetailCall.setOnClickListener(v -> {
            if(phone!=null) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(phoneIntent);
            }else{}//TODO snakbar
        });


        mRestaurantDetailsBinding.restaurantDetailWebsite.setOnClickListener(v -> {
            if(webUrl!=null) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
        startActivity(browserIntent);
            }else{}//TODO snakbar
            });

    }

    private void bindLayout(RestaurantDetailsViewModel viewModel, RestaurantStateItem item) {

        //GENERIC INFO (NearbyRequest + photo)
        mRestaurantDetailsBinding.restaurantDetailTvName.setText(item.getPlace_name());
        mRestaurantDetailsBinding.restaurantDetailTvAddress.setText(item.getPlace_address());

        viewModel.getUserLiveData().observe(this, user -> {

                    if ((Objects.equals(user.restaurant_for_today_id, item.getPlace_id()))) {
                        mRestaurantDetailsBinding.restaurantDetailFabToday.setImageResource(R.drawable.ic_bookmark_added_white_24dp);
                    } else {
                        mRestaurantDetailsBinding.restaurantDetailFabToday.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                    }
                }
        );

        Glide.with(mRestaurantDetailsBinding.restaurantDetailIvPreview)
                .load(item.getPlace_preview_pic_url())
                .into(mRestaurantDetailsBinding.restaurantDetailIvPreview);

        //DETAILS INFO (Restaurant details)
        viewModel.getRestaurantDetailsResults().observe(this, restaurantDetails -> {
            //Opening hours
            List<String> list = restaurantDetails.getOpeningHours().getWeekdayText();
            mRestaurantDetailsBinding.restaurantDetailOpeningHours.setText(TextUtils.join("\n",list));

            //TODO Rating / Price level / Distance

            //Phone Number of the restaurant
            phone = restaurantDetails.getFormattedPhoneNumber();
            //WEB browser
            webUrl = restaurantDetails.getWebsite();
        }
        );

        //FIRESTORE INFOs
        //TODO rv


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}