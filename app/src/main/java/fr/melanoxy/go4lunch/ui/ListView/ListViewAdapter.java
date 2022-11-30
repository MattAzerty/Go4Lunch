package fr.melanoxy.go4lunch.ui.ListView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import fr.melanoxy.go4lunch.databinding.RestaurantItemBinding;

public class ListViewAdapter extends ListAdapter<RestaurantStateItem, ListViewAdapter.ViewHolder> {

    private final OnRestaurantClickedListener listener;

    public ListViewAdapter(OnRestaurantClickedListener listener) {
        super(new ListRestaurantsItemCallback());

        this.listener = listener;
    }

    @NonNull
    @Override
    public ListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListViewAdapter.ViewHolder(RestaurantItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //Bind workmates item
        private RestaurantItemBinding binding;

        public ViewHolder(@NonNull RestaurantItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(RestaurantStateItem item, OnRestaurantClickedListener listener) {
            itemView.setOnClickListener(v -> listener.onRestaurantClicked(item));
            binding.restaurantItemName.setText(item.getPlace_name());
            binding.restaurantItemAddress.setText(item.getPlace_address());
            binding.restaurantItemOpenhours.setText(item.getPlace_is_open());
            binding.restaurantItemDistance.setText(item.getPlace_distance()+"km");
            Glide.with(binding.restaurantItemSmallThumbnail)
                    .load(item.getPlace_preview_pic_url())
                    .apply(RequestOptions.centerCropTransform())
                    .into(binding.restaurantItemSmallThumbnail);
            binding.restaurantItemTvBookmarks.setText(item.getNumberOfLunchmates().toString());
            binding.restaurantItemTvBookmarks.setVisibility(item.getNumberOfLunchmates()==0? View.INVISIBLE: View.VISIBLE);
            binding.restaurantItemStar1.setVisibility(item.getPlace_rating()>=1? View.VISIBLE : View.INVISIBLE);
            binding.restaurantItemStar2.setVisibility(item.getPlace_rating()>=1.5? View.VISIBLE : View.INVISIBLE);
            binding.restaurantItemStar3.setVisibility(item.getPlace_rating()>=2.5? View.VISIBLE : View.INVISIBLE);
        }
    }

    private static class ListRestaurantsItemCallback extends DiffUtil.ItemCallback<RestaurantStateItem> {
        @Override
        public boolean areItemsTheSame(@NonNull RestaurantStateItem oldItem, @NonNull RestaurantStateItem newItem) {
            return oldItem.getPlace_id().equals(newItem.getPlace_id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull RestaurantStateItem oldItem, @NonNull RestaurantStateItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}