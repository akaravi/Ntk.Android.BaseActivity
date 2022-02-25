package ntk.android.base.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import ntk.android.base.model.NotificationModel;
import ntk.android.base.room.RoomDb;
import ntk.android.base.utill.PugPush;
import ntk.android.base.utill.prefrense.Preferences;

/**
 * Created by Mehrdad Safari on 18-Jan-17.
 */

public class FirebaseService extends FirebaseMessagingService {


    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Preferences.with(this).appVariableInfo().setNotificationId(mToken);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        NotificationModel notification = new NotificationModel();
        notification.Title = remoteMessage.getData().get("Title");
        if (remoteMessage.getData().get("ContentType") != null) {
            notification.ContentType = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("ContentType")));
        }
        notification.Content = remoteMessage.getData().get("Content");

        if (remoteMessage.getData().get("BigImageSrc") != null) {
            notification.BigImageSrc = remoteMessage.getData().get("BigImageSrc");
        } else {
        }
        notification.IsRead = 0;
        RoomDb.getRoomDb(getApplicationContext()).NotificationDoa().Insert(notification);
        RoomDb.DestroyInstance();
        PugPush.ShowNotification(getApplicationContext(), notification);
    }
}
