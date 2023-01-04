package fr.melanoxy.go4lunch.ui.ChatActivity;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;
import java.util.Random;

import fr.melanoxy.go4lunch.databinding.ChatItemBinding;
import fr.melanoxy.go4lunch.utils.WorkmatesUtils;

public class ChatAdapter extends ListAdapter<ChatStateItem, ChatAdapter.ViewHolder> {


    protected ChatAdapter() {
        super(new ChatAdapter.ListChatItemCallback());
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatAdapter.ViewHolder(ChatItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //Bind workmates item
        private final ChatItemBinding binding;

        public ViewHolder(@NonNull ChatItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public void bind(ChatStateItem item) {
            binding.chatItemTvUsername.setText(item.getUserName());
            binding.chatItemTvUsername.setTextColor(
                    WorkmatesUtils.getInstance().convertStringToAssignedRandomColor(item.getUserName()));
            binding.chatItemTvMessage.setText(item.getMessage());
        }


    }

    private static class ListChatItemCallback extends DiffUtil.ItemCallback<ChatStateItem> {
        @Override
        public boolean areItemsTheSame(@NonNull ChatStateItem oldItem, @NonNull ChatStateItem newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatStateItem oldItem, @NonNull ChatStateItem newItem) {
            return oldItem.equals(newItem);
        }
    }

}//END of ChatAdapter
