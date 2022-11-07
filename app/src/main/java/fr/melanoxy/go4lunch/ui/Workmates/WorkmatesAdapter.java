package fr.melanoxy.go4lunch.ui.Workmates;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import fr.melanoxy.go4lunch.databinding.WorkmatesItemBinding;

public class WorkmatesAdapter extends ListAdapter<WorkmatesStateItem, WorkmatesAdapter.ViewHolder> {

    private final OnWorkmateClickedListener listener;

    public WorkmatesAdapter(OnWorkmateClickedListener listener) {
        super(new ListWorkmatesItemCallback());

        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(WorkmatesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //Bind workmates item
        private WorkmatesItemBinding binding;

        public ViewHolder(@NonNull WorkmatesItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(WorkmatesStateItem item, OnWorkmateClickedListener listener) {
            itemView.setOnClickListener(v -> listener.onWorkmateClicked(item.getAvatarUrl()));
            binding.workmateItemUsername.setText(item.getUsername());
            binding.workmateItemEmail.setText(item.getEmail());
            binding.workmateItemMainfield.setText(item.getMainfield());
            Glide.with(binding.workmateItemAvatar)
                    .load(item.getAvatarUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.workmateItemAvatar);
        }
    }

    private static class ListWorkmatesItemCallback extends DiffUtil.ItemCallback<WorkmatesStateItem> {
        @Override
        public boolean areItemsTheSame(@NonNull WorkmatesStateItem oldItem, @NonNull WorkmatesStateItem newItem) {
            return oldItem.getUid() == newItem.getUid();
        }

        @Override
        public boolean areContentsTheSame(@NonNull WorkmatesStateItem oldItem, @NonNull WorkmatesStateItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}
