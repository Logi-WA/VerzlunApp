package `is`.hi.hbv601g.verzlunapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM userInfo WHERE uid = 1")
    suspend fun getUserInfo(): UserInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(vararg ui: UserInfo)

    @Delete
    suspend fun deleteUserInfo(vararg ui: UserInfo)

}