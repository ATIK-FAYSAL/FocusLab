package services;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


import com.example.atik_faysal.focuslab.HomePage;
import com.example.atik_faysal.focuslab.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by USER on 2/14/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {
                showNotification(remoteMessage.getData().get("message"));
        }

        private void showNotification(String message) {

                Intent i = new Intent(this,HomePage.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setContentTitle("Focus color lab")
                        .setContentText(message)
                        .setSmallIcon(R.drawable.app_logo)
                        .setContentIntent(pendingIntent);

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                manager.notify(0,builder.build());
        }
}
