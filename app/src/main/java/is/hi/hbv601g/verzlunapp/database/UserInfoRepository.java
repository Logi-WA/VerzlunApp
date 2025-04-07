package is.hi.hbv601g.verzlunapp.database;

import android.app.Application;

import androidx.room.Room;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserInfoRepository {
    private UserInfoDao userInfoDao;
    private UserInfoDatabase db;
    private ExecutorService executorService;

    public UserInfoRepository(Application application) {
        db = Room.databaseBuilder(application, UserInfoDatabase.class, "user_info_db").build();
        userInfoDao = db.userInfoDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insertUserInfo(UserInfo ui) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(ui.email);
                System.out.println(ui.password);
                userInfoDao.insertUserInfo(ui);
            }
        });
    }

    public void getUserInfo() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                UserInfo ui = userInfoDao.getUserInfo();
                if (ui != null) {
                    System.out.println("Retrieved user!");
                    System.out.println(ui.email);
                    System.out.println(ui.password);
                }
                else {
                    System.out.println("empty");
                }
            }
        });
    }
}
