package fr.melanoxy.go4lunch.data;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.LiveDataTestUtils;
import fr.melanoxy.go4lunch.data.models.Message;
import fr.melanoxy.go4lunch.data.repositories.ChatRepository;

@RunWith(MockitoJUnitRunner.class)
public class ChatRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private FirebaseHelper firebaseHelper;

    private ChatRepository chatRepository;
    private MutableLiveData<List<Message>> messagesLiveData;

    @Before
    public void setUp() {

        chatRepository = new ChatRepository();

        // Given
        // MutableLiveData used by the mediator to build for the recyclerview the list of StateItem
        messagesLiveData = new MutableLiveData<>();

        //the livedata value are set here instead of repository
        messagesLiveData.setValue(getDefaultMessages());

        // Mock repositories for needed methods:
        firebaseHelper = mock(FirebaseHelper.class);
        setMock(firebaseHelper);//instance singleton handled
        // given behavior for some methods called
        Mockito.doReturn(messagesLiveData).when(firebaseHelper).getMessagesFromFirestore();

    }

    @After
    public void resetSingleton() throws Exception {
        Field instance = FirebaseHelper.class.getDeclaredField("sFirebaseHelper");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    private void setMock(FirebaseHelper mock) {
        try {
            Field instance = FirebaseHelper.class.getDeclaredField("sFirebaseHelper");
            instance.setAccessible(true);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Message> getDefaultMessages() {
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            messages.add(getDefaultMessage(i));
        }
        return messages;

    }

    private Message getDefaultMessage(int i) {

        return new Message(
                "Hello world"+i,
                "myUsername"+i
        );

    }

    @Test
    public void nominal_case_get_messages() {

        // When
        LiveDataTestUtils.observeForTesting(chatRepository.getMessagesLiveData(), value -> {
            // Then
            assertEquals(3, value.size());
        });
    }

    // For every public method in the ViewModel, we should verify that data passed to the ViewModel
    // is correctly dispatched to the underlying components (the repository, in this instance)
    @Test
    public void verify_on_send_message_clicked() {
        // Given
        String message = "Hello world!";
        String username = "MyUsername";
        // When
        chatRepository.createMessageForChat(message,username);
        // Then
        verify(firebaseHelper).createMessageOnFirestore(message,username);
        verifyNoMoreInteractions(firebaseHelper);
    }

}//END of ChatRepositoryTest
