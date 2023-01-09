package fr.melanoxy.go4lunch.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import fr.melanoxy.go4lunch.LiveDataTestUtils;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private UserRepository userRepository;
    private final String placeId ="PLACE_ID";
    @Mock
    private FirebaseHelper firebaseHelper;
    @Mock
    private FirebaseUser firebaseUser;

    @Mock
    private User user;

    @Mock
    private Observer<User> mockConnectedUserObserver;

    private OnCompleteListener onCompleteListener;

    private OnSuccessListener onSuccessListener;

    private OnFailureListener onFailureListener;

    private EventListener eventListener;

    @Before
    public void setUp() {
        setMock(firebaseHelper);//instance singleton handled

        userRepository = new UserRepository();
        userRepository.getConnectedUserLiveData().observeForever(mockConnectedUserObserver);
    }

    @Test
    public void testGetCurrentUser() {
        when(firebaseHelper.getCurrentUser()).thenReturn(firebaseUser);

        assertEquals(firebaseUser, userRepository.getCurrentUser());

        verify(firebaseHelper).getCurrentUser();
    }


    @Test
    public void testCreateUser_on_userDocument_exist() {
        when(firebaseUser.getUid()).thenReturn("USER_UID");
        when(userRepository.getCurrentUser()).thenReturn(firebaseUser);

        DocumentReference documentReference = mock(DocumentReference.class);
        when(firebaseHelper.getUserDocumentReferenceOnFirestore("USER_UID")).thenReturn(documentReference);

        Task taskGet = mock(Task.class);
        when(documentReference.get()).thenReturn(taskGet);

        Task taskComplete = mock(Task.class);
        when(taskGet.addOnCompleteListener(any())).thenAnswer(new Answer<Task<DocumentSnapshot>>() {
            @Override
            public Task<DocumentSnapshot> answer(InvocationOnMock invocation) throws Throwable {
                onCompleteListener = invocation.getArgument(0);
                return taskComplete;
            }
        });

        when(taskComplete.isSuccessful()).thenReturn(true);
        DocumentSnapshot userDocument = mock(DocumentSnapshot.class);
        when(taskComplete.getResult()).thenReturn(userDocument);
        when(userDocument.exists()).thenReturn(true);//Case userDocument already exist on Firebase

        user.uid = "USER_UID";
        List<String> iniList = new ArrayList<>();
        iniList.add(placeId);
        user.my_favorite_restaurants = iniList;
        when(userDocument.toObject(any())).thenReturn(user);


        userRepository.createUser();
        onCompleteListener.onComplete(taskComplete);//the Task is triggered manually

        assertEquals(user, userRepository.getConnectedUserLiveData().getValue());

        verify(taskComplete).isSuccessful();
        verify(mockConnectedUserObserver, times(1 )).onChanged(user);
        verifyNoMoreInteractions(mockConnectedUserObserver);
    }

    @Test
    public void testCreateUser_on_userDocument_do_not_exist() {
        when(userRepository.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("USER_UID");
        //when(firebaseUser.getDisplayName()).thenReturn("USER_NAME");

        DocumentReference documentReference = mock(DocumentReference.class);
        when(firebaseHelper.getUserDocumentReferenceOnFirestore("USER_UID")).thenReturn(documentReference);

        Task taskGet = mock(Task.class);
        when(documentReference.get()).thenReturn(taskGet);

        Task taskComplete = mock(Task.class);
        when(taskGet.addOnCompleteListener(any())).thenAnswer(new Answer<Task<DocumentSnapshot>>() {
            @Override
            public Task<DocumentSnapshot> answer(InvocationOnMock invocation) throws Throwable {
                onCompleteListener = invocation.getArgument(0);
                return taskComplete;
            }
        });

        when(taskComplete.isSuccessful()).thenReturn(true);
        DocumentSnapshot userDocument = mock(DocumentSnapshot.class);
        when(taskComplete.getResult()).thenReturn(userDocument);
        when(userDocument.exists()).thenReturn(false);//Case userDocument don't exist on Firebase

        userRepository.createUser();
        onCompleteListener.onComplete(taskComplete);//the Task is triggered manually

        assertEquals("USER_UID", userRepository.getConnectedUserLiveData().getValue().getUid());

        verify(taskComplete).isSuccessful();
        verify(firebaseHelper).storeUserOnFirestore(any());//check if this method is called
        verify(mockConnectedUserObserver, times(1 )).onChanged(any());
        verifyNoMoreInteractions(mockConnectedUserObserver);
    }

    @Test
    public void testUpdateTodayRestaurantUser() {
        when(firebaseUser.getUid()).thenReturn("USER_UID");
        when(userRepository.getCurrentUser()).thenReturn(firebaseUser);
        testCreateUser_on_userDocument_exist();

        DocumentReference documentReference = mock(DocumentReference.class);
        when(firebaseHelper.getUserDocumentReferenceOnFirestore("USER_UID")).thenReturn(documentReference);

        Task taskMock = mock(Task.class);
        when(documentReference.update(   "restaurant_for_today_id", "id",
                "restaurant_for_today_name", "name",
                "restaurant_for_today_address", "address",
                "restaurant_for_today_pic_url", "url")).thenReturn(taskMock);

        Task taskSuccess = mock(Task.class);
        when(taskMock.addOnSuccessListener(any())).thenAnswer(new Answer<Task>() {
            @Override
            public Task answer(InvocationOnMock invocation) throws Throwable {
                onSuccessListener = invocation.getArgument(0);
                return taskSuccess;
            }
        });

        Task taskFailure = mock(Task.class);
        when(taskSuccess.addOnFailureListener(any())).thenAnswer(new Answer<Task>() {
            @Override
            public Task answer(InvocationOnMock invocation) throws Throwable {
                return taskFailure;
            }
        });

        userRepository.updateTodayRestaurantUser("id", "name",
                "address", "url");
        onSuccessListener.onSuccess(null);

        assertEquals(user, userRepository.getConnectedUserLiveData().getValue());

        verify(mockConnectedUserObserver, times(2)).onChanged(user);
        verifyNoMoreInteractions(mockConnectedUserObserver);
    }

    @Test
    public void testUpdateFavList_when_placeId_already_favorite() {

        String FIELD_NAME_FAV_RESTAURANTS = "my_favorite_restaurants";

        when(userRepository.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("USER_UID");
        testCreateUser_on_userDocument_exist();

        DocumentReference documentReference = mock(DocumentReference.class);
        when(firebaseHelper.getUserDocumentReferenceOnFirestore("USER_UID")).thenReturn(documentReference);

        //When..
        userRepository.updateFavList(placeId);
        // Then
        verify(documentReference).update(anyString(), any());

        LiveDataTestUtils.observeForTesting(userRepository.getConnectedUserLiveData(), value -> {
            // Then
            assertEquals(0, value.my_favorite_restaurants.size());
        });

    }

    @Test
    public void testUpdateFavList_when_placeId_not_on_favorite_list() {

        String FIELD_NAME_FAV_RESTAURANTS = "my_favorite_restaurants";

        when(userRepository.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("USER_UID");
        testCreateUser_on_userDocument_exist();

        DocumentReference documentReference = mock(DocumentReference.class);
        when(firebaseHelper.getUserDocumentReferenceOnFirestore("USER_UID")).thenReturn(documentReference);

        //When..
        userRepository.updateFavList("newPlaceId");
        // Then
        verify(documentReference).update(anyString(), any());

        LiveDataTestUtils.observeForTesting(userRepository.getConnectedUserLiveData(), value -> {
            // Then
            assertEquals(2, value.my_favorite_restaurants.size());
        });

    }

    @Test
    public void testIsUserAuthenticatedInFirebase() {

        when(userRepository.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("USER_UID");

        DocumentReference documentReference = mock(DocumentReference.class);
        when(firebaseHelper.getUserDocumentReferenceOnFirestore("USER_UID")).thenReturn(documentReference);

        Task taskGet = mock(Task.class);
        when(documentReference.get()).thenReturn(taskGet);

        Task taskComplete = mock(Task.class);
        when(taskGet.addOnCompleteListener(any())).thenAnswer(new Answer<Task<DocumentSnapshot>>() {
            @Override
            public Task<DocumentSnapshot> answer(InvocationOnMock invocation) throws Throwable {
                onCompleteListener = invocation.getArgument(0);
                return taskComplete;
            }
        });

        when(taskComplete.isSuccessful()).thenReturn(true);
        DocumentSnapshot userDocument = mock(DocumentSnapshot.class);
        when(taskComplete.getResult()).thenReturn(userDocument);
        when(userDocument.exists()).thenReturn(true);//Case userDocument already exist on Firebase

        assertTrue(userRepository.isUserAuthenticatedInFirebase());
        when(userDocument.toObject(any())).thenReturn(user);
        onCompleteListener.onComplete(taskComplete);//the Task is triggered manually
        user.uid = "USER_UID";
        assertEquals(user, userRepository.getConnectedUserLiveData().getValue());

    }

    @Test
    public void testGetLunchmatesLiveData() {

        String placeId = "PLACE_ID";

        CollectionReference collectionReference = mock(CollectionReference.class);
        when(firebaseHelper.getWorkmateCollection()).thenReturn(collectionReference);

        Query mockedQuery = mock(Query.class);
        when(collectionReference.whereEqualTo("restaurant_for_today_id", placeId)).thenReturn(mockedQuery);

        QuerySnapshot mockedQuerySnapshot = mock(QuerySnapshot.class);

        ListenerRegistration mockedRegistration = mock(ListenerRegistration.class);
        when(mockedQuery.addSnapshotListener(any())).thenAnswer(new Answer<ListenerRegistration>() {
            @Override
            public ListenerRegistration answer(InvocationOnMock invocation) throws Throwable {
                eventListener = invocation.getArgument(0);
                return mockedRegistration;
            }
        });
        //How to mock forEach behavior with Mockito
        //https://stackoverflow.com/questions/49406075/how-to-mock-foreach-behavior-with-mockito
        doCallRealMethod().when(mockedQuerySnapshot).forEach(any(Consumer.class));
        Iterator mockIterator = mock(Iterator.class);
        when((mockedQuerySnapshot).iterator()).thenReturn(mockIterator);
        when(mockIterator.hasNext()).thenReturn(true, false);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);
        when(mockIterator.next()).thenReturn(queryDocumentSnapshot);

        user.uid = "USER_UID";
        when(queryDocumentSnapshot.toObject(any())).thenReturn(user);

        userRepository.getLunchmatesLiveData(placeId);
        eventListener.onEvent(mockedQuerySnapshot,null);//the Task is triggered manually
        LiveDataTestUtils.observeForTesting(userRepository.getLunchmatesLiveData(placeId), value -> {
            // Then
            assertEquals(user, value.get(0));
        });

    }


    @Test
    public void testGetLunchmatesLiveData_withError() {

        String placeId = "PLACE_ID";

        CollectionReference collectionReference = mock(CollectionReference.class);
        when(firebaseHelper.getWorkmateCollection()).thenReturn(collectionReference);

        Query mockedQuery = mock(Query.class);
        when(collectionReference.whereEqualTo("restaurant_for_today_id", placeId)).thenReturn(mockedQuery);

        QuerySnapshot mockedQuerySnapshot = mock(QuerySnapshot.class);
        FirebaseFirestoreException mockedException = mock(FirebaseFirestoreException.class);

        ListenerRegistration mockedRegistration = mock(ListenerRegistration.class);
        when(mockedQuery.addSnapshotListener(any())).thenAnswer(new Answer<ListenerRegistration>() {
            @Override
            public ListenerRegistration answer(InvocationOnMock invocation) throws Throwable {
                eventListener = invocation.getArgument(0);
                return mockedRegistration;
            }
        });

        userRepository.getLunchmatesLiveData(placeId);
        eventListener.onEvent(mockedQuerySnapshot,mockedException);//the Task is triggered manually
        LiveDataTestUtils.observeForTesting(userRepository.getLunchmatesLiveData(placeId), value -> {
            // Then
            assertTrue(value.isEmpty());
        });

    }

    @Test
    public void testUpdateUserSettings() {

        when(firebaseUser.getUid()).thenReturn("USER_UID");
        when(userRepository.getCurrentUser()).thenReturn(firebaseUser);

        testCreateUser_on_userDocument_exist();

        DocumentReference documentReference = mock(DocumentReference.class);
        when(firebaseHelper.getUserDocumentReferenceOnFirestore("USER_UID")).thenReturn(documentReference);

        Task taskMock = mock(Task.class);
        when(documentReference.update(
                "notified", true,
                "urlPicture", "urlPicture",
                "username", "username")).thenReturn(taskMock);

        Task taskSuccess = mock(Task.class);
        when(taskMock.addOnSuccessListener(any())).thenAnswer(new Answer<Task>() {
            @Override
            public Task answer(InvocationOnMock invocation) throws Throwable {
                onSuccessListener = invocation.getArgument(0);
                return taskSuccess;
            }
        });

        Task taskFailure = mock(Task.class);
        when(taskSuccess.addOnFailureListener(any())).thenAnswer(new Answer<Task>() {
            @Override
            public Task answer(InvocationOnMock invocation) throws Throwable {
                return taskFailure;
            }
        });

        userRepository.updateUserSettings(true,"urlPicture","username");
        onSuccessListener.onSuccess(null);

        assertEquals(user, userRepository.getConnectedUserLiveData().getValue());

        verify(mockConnectedUserObserver, times(2)).onChanged(user);
        verifyNoMoreInteractions(mockConnectedUserObserver);
    }

    @Test
    public void testOnEndOfDetailsActivity() {

        testGetLunchmatesLiveData();//Add a lunchmate
        userRepository.onEndOfDetailsActivity();
        LiveDataTestUtils.observeForTesting(userRepository.getLunchmatesLiveData(placeId), value -> {
            // Then check list of lunchmates empty
            assertTrue(value.isEmpty());
        });

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

}//END of UserRepositoryTest
