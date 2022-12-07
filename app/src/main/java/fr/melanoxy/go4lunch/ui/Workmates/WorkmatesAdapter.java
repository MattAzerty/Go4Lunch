package fr.melanoxy.go4lunch.ui.Workmates;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.databinding.WorkmateItemBinding;

public class WorkmatesAdapter extends ListAdapter<WorkmatesStateItem, WorkmatesAdapter.ViewHolder> {

    private final OnWorkmateClickedListener listener;
    private final Context context;

    public WorkmatesAdapter(OnWorkmateClickedListener listener, Context context) {
        super(new ListWorkmatesItemCallback());

        this.listener = listener;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(WorkmateItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener, context );
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //Bind workmates item
        private WorkmateItemBinding binding;

        public ViewHolder(@NonNull WorkmateItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(WorkmatesStateItem item, OnWorkmateClickedListener listener, Context context) {
            itemView.setOnClickListener(v -> listener.onWorkmateClicked(item));
            binding.workmateItemUsername.setText(item.getUsername());
            binding.workmateItemEmail.setText(item.getEmail());
            binding.workmateItemMainfield.setText(item.getMainField());
            if(item.getMainField().contains(context.getResources().getString(R.string.workmates_restaurant_not_set))) {
                binding.workmateItemMainfield.setTextColor(Color.parseColor("#3a3d40"));}
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
