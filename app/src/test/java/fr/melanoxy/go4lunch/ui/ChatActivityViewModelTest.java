package fr.melanoxy.go4lunch.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.LiveDataTestUtils;
import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.data.models.Message;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.ChatRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.ui.ChatActivity.ChatActivityViewModel;
import fr.melanoxy.go4lunch.ui.ChatActivity.ChatStateItem;

@RunWith(MockitoJUnitRunner.class)
public class ChatActivityViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRepository chatRepository;

    private MutableLiveData<List<Message>> messagesMutableLiveData;
    private ChatActivityViewModel viewModel;
    private static final String DEFAULT_USERNAME = "DEFAULT_USERNAME";

    @Before
    public void setUp() {
        // Reinitialize LiveData every test
        messagesMutableLiveData = new MutableLiveData<>();
        // Mock LiveData returned from Repository
        given(userRepository.getUser()).willReturn(new User(
                "uid",
                DEFAULT_USERNAME,
                "urlpicture",
                "email",
                "placeId",
                "restaurant_for_today_name",
                "restaurant_for_today_address",
                "restaurant_for_today_pic_url",
                null,
                true
        ));
        given(chatRepository.getMessagesLiveData()).willReturn(messagesMutableLiveData);
        // Set default values to LiveData
        List<Message> messages = getDefaultMessages();
        messagesMutableLiveData.setValue(messages);

        viewModel = new ChatActivityViewModel(userRepository,chatRepository);

    }

    @Test
    public void nominal_case() {
        // When
        LiveDataTestUtils.observeForTesting(viewModel.getMessageStateItemsLiveData(), value -> {
            // Then
            // Step 1 for Then : assertions...
            assertEquals(getDefaultChatStateItems(), value);

            // ... Step 2 for Then : verify !
            verify(chatRepository).getMessagesLiveData();
            verifyNoMoreInteractions(chatRepository);
        });
    }
    // For every public method in the ViewModel, we should verify that data passed to the ViewModel
    // is correctly dispatched to the underlying components (the repository, in this instance)
    @Test
    public void verify_on_send_message_clicked() {
        // Given
        String message = "Hello world!";

        // When
        viewModel.onSendMessageClicked(message);
        // Then
        verify(chatRepository).createMessageForChat(message,DEFAULT_USERNAME);
        verifyNoMoreInteractions(chatRepository);
    }

    // region IN (this is the default values that "enters" the ViewModel) : Messages
    private List<Message> getDefaultMessages() {
        List<Message> messages = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            messages.add(getDefaultMessage(i));
        }

        return messages;
    }

    @NonNull
    private Message getDefaultMessage(int index) {
        return new Message(
                "message"+ index,
                DEFAULT_USERNAME + index
        );
    }
    // endregion IN



    // region OUT (this is the default values that are exposed by the ViewModel) : State Item
    private List<ChatStateItem> getDefaultChatStateItems() {
        List<ChatStateItem> chatStateItems = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            chatStateItems.add(getDefaultChatStateItem(i));
        }

        return chatStateItems;
    }

    @NonNull
    private ChatStateItem getDefaultChatStateItem(int index) {
        return new ChatStateItem(
                "message"+ index,
                DEFAULT_USERNAME + index
        );
    }
    // endregion OUT



}//END of ChatActivityViewModelTest
