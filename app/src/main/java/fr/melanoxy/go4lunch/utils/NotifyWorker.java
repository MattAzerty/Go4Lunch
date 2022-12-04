package fr.melanoxy.go4lunch.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;


public class NotifyWorker extends Worker {

    public static final String EXTRA_USER_ID= "userId";
    public static final String EXTRA_OUTPUT_MESSAGE = "output_message";
    @NonNull
    private final UserRepository userRepository;
    private User mUser;

    public NotifyWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
        this.userRepository = new UserRepository();
    }

    @NonNull
    @Override
    public Result doWork() {
        Result result;
        String names;

        List<String> lunchmates = new ArrayList<>();

        try {
            mUser = userRepository.getDataUser(getInputData().getString(EXTRA_USER_ID));
            lunchmates = userRepository.getLunchmates(mUser.getRestaurant_for_today_id());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(mUser!=null &&lunchmates!=null){
            names= TextUtils.join("\n",lunchmates);

            Data output = new Data.Builder()
                    .putString(EXTRA_OUTPUT_MESSAGE, "I have come from MyWorker!")
                    .build();

            // Method to trigger an instant notification
            sendNotification(mUser.getRestaurant_for_today_name(),mUser.getRestaurant_for_today_address(),names);
            result = Result.success(output);

        }else {
            result = Result.failure();
        }

        return result;
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }

    public void sendNotification(String title, String address, String lunchmates) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title)
                .setContentText(address)//TextLong
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(address+"\n"+lunchmates))
                .setSmallIcon(R.drawable.ic_yourlunch_white_24dp);

        notificationManager.notify(1, notification.build());
    }


}//END