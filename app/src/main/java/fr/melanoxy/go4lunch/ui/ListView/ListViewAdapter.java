package fr.melanoxy.go4lunch.ui.ListView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import fr.melanoxy.go4lunch.databinding.RestaurantItemBinding;

public class ListViewAdapter extends ListAdapter<ListViewStateItem, ListViewAdapter.ViewHolder> {

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

        public void bind(ListViewStateItem item, OnRestaurantClickedListener listener) {
            itemView.setOnClickListener(v -> listener.onRestaurantClicked(item.getPlace_name()));
            binding.restaurantItemName.setText(item.getPlace_name());
            binding.restaurantItemAdress.setText(item.getPlace_address());
            binding.restaurantItemOpenhours.setText(item.getPlace_openhour());
            /*Glide.with(binding.restaurantItemSmallThumbnail)
                    .load(item.getAvatarUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.workmateItemAvatar);*/
        }
    }

    private static class ListRestaurantsItemCallback extends DiffUtil.ItemCallback<ListViewStateItem> {
        @Override
        public boolean areItemsTheSame(@NonNull ListViewStateItem oldItem, @NonNull ListViewStateItem newItem) {
            return oldItem.getPlace_id() == newItem.getPlace_id();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ListViewStateItem oldItem, @NonNull ListViewStateItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}