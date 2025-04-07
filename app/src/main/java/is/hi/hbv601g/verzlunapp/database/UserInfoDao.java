package is.hi.hbv601g.verzlunapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserInfoDao {
    @Query("SELECT * FROM userInfo WHERE uid = 1")
    UserInfo getUserInfo();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserInfo(UserInfo ui);

    @Delete()
    void deleteUserInfo(UserInfo ui);
}
