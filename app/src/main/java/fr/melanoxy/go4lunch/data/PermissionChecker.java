package fr.melanoxy.go4lunch.data;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
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

    public boolean hasNotificationPermission() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(application);
        notificationManagerCompat.areNotificationsEnabled();
        return notificationManagerCompat.areNotificationsEnabled();
    }
}
