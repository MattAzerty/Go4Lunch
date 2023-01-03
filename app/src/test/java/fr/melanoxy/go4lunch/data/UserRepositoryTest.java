package fr.melanoxy.go4lunch.data;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import fr.melanoxy.go4lunch.data.repositories.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private FirebaseHelper firebaseHelper;
    @Mock
    private DocumentSnapshot mockDocumentSnapshot;
    @Mock
    private DocumentReference userDocumentReference;

    private UserRepository userRepository;

    @Before
    public void setUp() {

        // Mock firebaseHelper for needed methods:
        firebaseHelper = mock(FirebaseHelper.class);
        setMock(firebaseHelper);//instance singleton handled

        // Mock FirebaseUser
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        String userUid = "USER_UID";
        when(firebaseUser.getUid()).thenReturn(userUid);
        when(firebaseUser.getDisplayName()).thenReturn("fake-name");
        when(firebaseUser.getEmail()).thenReturn("fake@email.com");
        when(firebaseUser.getPhotoUrl()).thenReturn(Uri.parse("https://fake-photo-url.com"));

        // Mock DocumentReference
        //DocumentReference userDocumentReference = mock(DocumentReference.class);

        // Mock DocumentSnapshot
        //when(mockDocumentSnapshot.exists()).thenReturn(true);

        Mockito.doReturn(firebaseUser).when(firebaseHelper).getCurrentUser();
        Mockito.doReturn(userDocumentReference).when(firebaseHelper).getUserDocumentReferenceOnFirestore(userUid);
        //Mockito.doReturn(mockTask).when(firebaseHelper).getUserDocumentReferenceOnFirestore(userUid).get();
        when(mockedTaskDocumentSnapshot.isSuccessful()).thenReturn(true);
        when(mockedTaskDocumentSnapshot.getResult()).thenReturn(mockDocumentSnapshot);

        userRepository = new UserRepository();

    }

    @After
    public void resetSingleton() throws Exception {
        Field instance = FirebaseHelper.class.getDeclaredField("sFirebaseHelper");
        instance.setAccessible(true);
        instance.set(null, null);
    }
    //Use reflection to set a Value of a Non-Public Field
    private void setMock(FirebaseHelper mock) {
        try {
            Field instance = FirebaseHelper.class.getDeclaredField("sFirebaseHelper");
            instance.setAccessible(true);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateUser_existingUser() {
        
        // Call createUser
        userRepository.createUser();

        verify(userDocumentReference.get())
                .addOnCompleteListener(onCompleteListenerArgumentCaptor.capture());

        onCompleteListenerArgumentCaptor.getValue().onComplete(mockedTaskDocumentSnapshot);
    }

    // region IN
    @Captor
    private ArgumentCaptor<OnCompleteListener<DocumentSnapshot>> onCompleteListenerArgumentCaptor;
    @Mock
    private Task<DocumentSnapshot> mockedTaskDocumentSnapshot;
}//END of UserRepositoryTest
