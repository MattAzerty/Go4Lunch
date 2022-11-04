package fr.melanoxy.go4lunch.ui.Workmates;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Collections;

import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.melanoxy.go4lunch.utils.ViewModelFactory;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding mBinding;
    private WorkmatesViewModel mViewModel;
    private WorkmatesAdapter mAdapter;
    private Query mQuery;
    //private WorkmatesAdapter.OnWorkmateSelectedListener mListener;

    //private static final int LIMIT = 50;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //binding FragmentWorkmatesBinding layout
        mBinding = FragmentWorkmatesBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


//ViewModel used for this fragment
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance())
                .get(WorkmatesViewModel.class);

//Init RecyclerView

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        // Get ${LIMIT} collection
        mQuery = mFirestore.collection("users");
                //.orderBy("avgRating", Query.Direction.DESCENDING)
                //.limit(LIMIT);

        // RecyclerView
        mAdapter = new WorkmatesAdapter(mQuery, new OnWorkmateSelectedListener() {
            @Override
            public void onWorkmateSelected(DocumentSnapshot workmate) {

                User user = workmate.toObject(User.class);
                Snackbar.make(mBinding.getRoot(),
                        user.username, Snackbar.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected void onDataChanged() {// TODO ViewEmpty
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.workmatesRv.setVisibility(View.VISIBLE);
                    //mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.workmatesRv.setVisibility(View.VISIBLE);
                    //mBinding.viewEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mBinding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mBinding.workmatesRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.workmatesRv.setAdapter(mAdapter);


    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }


    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        mViewModel.setIsSigningIn(false);

        if (result.getResultCode() != Activity.RESULT_OK) {
            if (response == null) {
                // User pressed the back button.
                requireActivity().finish();
            } else if (response.getError() != null
                    && response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSignInErrorDialog(R.string.error_no_internet);
            } else {
                showSignInErrorDialog(R.string.error_unknown_error);
            }
        }
    }


    /*@Override
    public void onWorkmateSelected(DocumentSnapshot workmate) {
        // Go to the details page for the selected workmate

        Snackbar.make(mBinding.getRoot(),
                workmate.toString(), Snackbar.LENGTH_LONG).show();

        MainFragmentDirections.ActionMainFragmentToRestaurantDetailFragment action = MainFragmentDirections
                .actionMainFragmentToRestaurantDetailFragment(restaurant.getId());

        NavHostFragment.findNavController(this)
                .navigate(action);
    }*/



    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        ActivityResultLauncher<Intent> signinLauncher = requireActivity()
                .registerForActivityResult(new FirebaseAuthUIActivityResultContract(),
                        this::onSignInResult
                );

        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        signinLauncher.launch(intent);
        mViewModel.setIsSigningIn(true);
    }


    private void showSignInErrorDialog(@StringRes int message) {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.error_authentication_canceled)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.option_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startSignIn();
                    }
                })
                .setNegativeButton(R.string.option_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requireActivity().finish();
                    }
                }).create();

        dialog.show();
    }

}