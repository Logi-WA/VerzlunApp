package is.hi.hbv601g.verzlunapp.database;

import android.app.Application;

import androidx.room.Room;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.security.auth.callback.Callback;

public class UserInfoRepository {
    private UserInfoDao userInfoDao;
    private UserInfoDatabase db;
    private ExecutorService executorService;
    private UserInfoCallback uICallback;

    public UserInfoRepository(Application application) {
        db = Room.databaseBuilder(application, UserInfoDatabase.class, "user_info_db").build();
        userInfoDao = db.userInfoDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void setUserInfoCallback(UserInfoCallback userInfoCallback) {
        this.uICallback = userInfoCallback;
    }

    public void insertUserInfo(UserInfo ui/*, Callback callback*/) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(ui.email);
                System.out.println(ui.password);
                userInfoDao.insertUserInfo(ui);
            }
        });
    }

    public void getUserInfo(/*Callback callback*/) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                UserInfo ui = userInfoDao.getUserInfo();
                if (ui != null) {
                    System.out.println("Retrieved user!");
                    System.out.println(ui.email);
                    System.out.println(ui.password);
//                    uICallback.returnUI(ui);
                }
                else {
                    System.out.println("empty");
                }
            }
            //runonuithread
        });
    }

    public interface UserInfoCallback {
        void returnUI(UserInfo ui);
    }
}
