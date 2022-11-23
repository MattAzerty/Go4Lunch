package fr.melanoxy.go4lunch.ui.MapView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

//This class exists only for easier unit testing the MainViewModel

public class PermissionChecker {

    @NonNull
    private final Application application;

    public PermissionChecker(@NonNull Application application) {
        this.application = application;
    }

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(application, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public boolean hasNotificationPermission() {
        return ContextCompat.checkSelfPermission(application, POST_NOTIFICATIONS) == PERMISSION_GRANTED;
    }

}
