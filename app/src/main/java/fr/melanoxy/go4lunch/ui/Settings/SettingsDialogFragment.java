package fr.melanoxy.go4lunch.ui.Settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

import fr.melanoxy.go4lunch.MainActivity;
import fr.melanoxy.go4lunch.MainActivityViewModel;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.databinding.FragmentDialogSettingsBinding;

public class SettingsDialogFragment extends DialogFragment {

    private FragmentDialogSettingsBinding mBinding;
    private Uri selectedImageUri=null;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        //binding FragmentWorkmatesBinding layout
        mBinding = FragmentDialogSettingsBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        //DialogFragment on the top
        Objects.requireNonNull(getDialog()).getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        WindowManager.LayoutParams param = getDialog().getWindow().getAttributes();
        param.width = ViewGroup.LayoutParams.MATCH_PARENT;
        param.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        //param.x = 300;
        param.y = 10;

        getDialog().getWindow().setAttributes(param);


        //ViewModel of MainActivity associated here
        MainActivity mMainActivity = (MainActivity) getActivity();
        MainActivityViewModel viewModel = new ViewModelProvider(mMainActivity).get(MainActivityViewModel.class);

        //Set existing values
        viewModel.getConnectedUserLiveData().observe(mMainActivity, user -> {

            mBinding.checkBox.setChecked(user.getNotified());

            Glide.with(mBinding.drawerHeaderPfp)
                    .load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mBinding.drawerHeaderPfp);

            mBinding.username.setText(user.getUsername());

        });
//CHANGE AVATAR BUTTON
        mBinding.buttonpfp.setOnClickListener(
                v -> selectPicture());
//SAVE BUTTON
        mBinding.actionSave.setOnClickListener(
                v -> {
                    //Close dialog
                    viewModel.OnSettingsSaved(
                            mBinding.checkBox.isChecked(),
                            selectedImageUri,
                            mBinding.username.getText().toString().trim());
                    getDialog().dismiss();
                });

        return view;
    }

    private void selectPicture() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // Access selected image from here...
                    if (data != null
                            && data.getData() != null) {
                        selectedImageUri = data.getData();

                        Glide.with(mBinding.drawerHeaderPfp)
                                .load(selectedImageUri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(mBinding.drawerHeaderPfp);
                    }
                }
            });


}//END of SettingsDialogFragment
