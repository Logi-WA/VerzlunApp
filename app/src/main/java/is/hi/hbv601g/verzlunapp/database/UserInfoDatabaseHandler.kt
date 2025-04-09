package `is`.hi.hbv601g.verzlunapp.database

import android.app.PendingIntent.getActivity
import android.content.Context
import androidx.room.Room
import `is`.hi.hbv601g.verzlunapp.VerzlunActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserInfoDatabaseHandler(val db : UserInfoDatabase) {
    var uid = db.userInfoDao()

    fun getUserInfo() : UserInfo {
        println("getting user")
        var ui : UserInfo
        runBlocking {
            ui = uid.getUserInfo()
        }
        println(ui.email)
        println(ui.password)
        return ui
    }

    fun insertUserInfo(ui: UserInfo) {
        print("sending user")
        runBlocking { uid.insertUserInfo(ui) }
    }

}