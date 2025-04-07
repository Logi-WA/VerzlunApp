package is.hi.hbv601g.verzlunapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userInfo")
public class UserInfo {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password")
    public String password;

    public UserInfo(int uid, String email, String password) {
        this.uid = uid;
        this.email = email;
        this.password = password;
    }
}
