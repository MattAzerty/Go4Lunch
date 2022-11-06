package fr.melanoxy.go4lunch.ui.Workmates;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.databinding.WorkmatesItemBinding;

public class WorkmatesAdapter extends FirestoreAdapter<WorkmatesAdapter.ViewHolder> {

    private final OnWorkmateSelectedListener mListener;

    public WorkmatesAdapter(Query query, OnWorkmateSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(WorkmatesItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private WorkmatesItemBinding binding;

        public ViewHolder(WorkmatesItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnWorkmateSelectedListener listener) {

            User workmate = snapshot.toObject(User.class);

            binding.workmateItemUsername.setText("- "+workmate.getUsername()+" ");
            binding.workmateItemEmail.setText("."+workmate.getEmail());
            binding.workmateItemMainfield.setText(" Hey! Je mange @LaFourchette");
            Glide.with(binding.workmateItemAvatar)
                    .load(workmate.urlPicture)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.workmateItemAvatar);

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onWorkmateSelected(snapshot);
                    }
                }
            });
        }

    }
}

