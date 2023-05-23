package fr.melanoxy.go4lunch.ui.ChatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import fr.melanoxy.go4lunch.databinding.ActivityChatBinding;
import fr.melanoxy.go4lunch.utils.ViewModelFactory;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding mChatBinding;
    private ChatActivityViewModel mChatViewModel;

    public static Intent navigate(Context context) {
        return new Intent(context, ChatActivity.class);//return intent
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ViewBinding
        mChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = mChatBinding.getRoot();
        setContentView(view);

        //Associating ViewModel with the Activity
        mChatViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance())
                        .get(ChatActivityViewModel.class);

        //BINDING
        bindRecyclerview();
        //TODO liveData "gone/visible" send button

        //LISTENER for Send message button
        mChatBinding.sendButton.setOnClickListener(v -> {
            mChatViewModel.onSendMessageClicked(mChatBinding.chatEditText.getText().toString().trim());
            // Reset text field
            mChatBinding.chatEditText.setText("");
        });

    }

    private void bindRecyclerview() {
        //Init RecyclerView
        ChatAdapter adapter = new ChatAdapter();
        mChatBinding.chatRv.setAdapter(adapter);
        mChatViewModel.getMessageStateItemsLiveData().observe(this, adapter::submitList);
    }


}//END of chat Activity