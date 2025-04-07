package is.hi.hbv601g.verzlunapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserInfo.class}, version = 1)
public abstract class UserInfoDatabase extends RoomDatabase {
    private static final String DB_NAME = "user_info_db";

    public abstract UserInfoDao userInfoDao();

}
