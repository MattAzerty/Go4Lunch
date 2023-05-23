package fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

import fr.melanoxy.go4lunch.databinding.WorkmateRestaurantDetailsItemBinding;


public class LunchmatesAdapter extends ListAdapter<LunchmateStateItem, LunchmatesAdapter.ViewHolder> {

    private final OnLunchmateClickedListener listener;
    Context context;

    public LunchmatesAdapter(OnLunchmateClickedListener listener) {
        super(new LunchmatesAdapter.ListLunchmatesItemCallback());

        this.listener = listener;
    }

    @NonNull
    @Override
    public LunchmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LunchmatesAdapter.ViewHolder(WorkmateRestaurantDetailsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LunchmatesAdapter.ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //Bind workmates item
        private final WorkmateRestaurantDetailsItemBinding binding;

        public ViewHolder(@NonNull WorkmateRestaurantDetailsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(LunchmateStateItem item, OnLunchmateClickedListener listener) {

            itemView.setOnClickListener(v -> listener.onLunchmateClicked(item.getUid()));
            binding.workmateRestaurantDetailsItemTvUsername.setText(item.getUsername());
            Glide.with(binding.workmateRestaurantDetailsItemIvPfp)
                    .load(item.getAvatarUrl())
                    .apply(RequestOptions.centerInsideTransform())
                    .into(binding.workmateRestaurantDetailsItemIvPfp);
        }
    }

    private static class ListLunchmatesItemCallback extends DiffUtil.ItemCallback<LunchmateStateItem> {
        @Override
        public boolean areItemsTheSame(@NonNull LunchmateStateItem oldItem, @NonNull LunchmateStateItem newItem) {
            return Objects.equals(oldItem.getUid(), newItem.getUid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull LunchmateStateItem oldItem, @NonNull LunchmateStateItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}
