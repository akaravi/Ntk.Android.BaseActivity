package ntk.android.base.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ntk.android.base.BaseNtkApplication;
import ntk.android.base.model.NotificationModel;


@Database(entities = {NotificationModel.class}, version = 1, exportSchema = false)
public abstract class RoomDb extends RoomDatabase {

    public abstract NotificationDoa NotificationDoa();

    private static RoomDb Instance;

    public static RoomDb getRoomDb(Context context) {
        if (Instance == null) {
            Instance = Room.databaseBuilder(context.getApplicationContext(), RoomDb.class, BaseNtkApplication.get().getApplicationParameter().APPLICATION_ID())
                    .allowMainThreadQueries()
                    .build();
        }
        return Instance;
    }

    public static void DestroyInstance() {
        Instance = null;
    }
}
