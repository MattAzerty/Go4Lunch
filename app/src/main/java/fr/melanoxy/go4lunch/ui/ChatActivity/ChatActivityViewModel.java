package fr.melanoxy.go4lunch.ui.ChatActivity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.data.models.Message;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.ChatRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity.LunchmateStateItem;
import fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity.SingleLiveEvent;

public class ChatActivityViewModel extends ViewModel {

    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final ChatRepository chatRepository;


    public ChatActivityViewModel(
            @NonNull UserRepository userRepository,
            @NonNull ChatRepository chatRepository
    ) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    public LiveData<List<ChatStateItem>> getMessageStateItemsLiveData() {
        return Transformations.map(chatRepository.getMessagesLiveData(), messages -> {
            List<ChatStateItem> messageStateItems = new ArrayList<>();

            // mapping
            for (Message message : messages) {
                messageStateItems.add(
                        new ChatStateItem(
                                message.getMessage(),
                                message.getUserName()
                        )
                );
            }
            return messageStateItems;
        });
    }


    public void onSendMessageClicked(String message) {
        chatRepository.createMessageForChat(message, userRepository.mUser.getUsername());
    }
}//END of ChatActivityViewModel
