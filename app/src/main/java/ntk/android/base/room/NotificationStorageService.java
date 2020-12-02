package ntk.android.base.room;

import android.content.Context;

import java.util.List;

import ntk.android.base.model.NotificationModel;

public class NotificationStorageService {

    public List<NotificationModel> getAllUnread(Context c) {
        return RoomDb.getNotificationDao(c).AllUnRead();
    }
}
