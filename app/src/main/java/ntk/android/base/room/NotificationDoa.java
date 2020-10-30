package ntk.android.base.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ntk.android.base.model.NotificationModel;

@Dao
public interface NotificationDoa {

    @Query("SELECT * FROM Notification ORDER BY ID DESC")
    List<NotificationModel> All();

    @Query("SELECT * FROM Notification WHERE IsRead == 0 ORDER BY ID DESC")
    List<NotificationModel> AllUnRead();

    @Insert
    void Insert(NotificationModel notificationModel);

    @Update
    void Update(NotificationModel notificationModel);

    @Delete
    void Delete(NotificationModel notificationModel);

}
