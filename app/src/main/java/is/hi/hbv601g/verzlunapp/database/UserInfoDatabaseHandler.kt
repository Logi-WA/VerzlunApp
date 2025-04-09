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
        var ui : UserInfo
        runBlocking {
            ui = uid.getUserInfo()
        }
        return ui
    }

    fun insertUserInfo(ui: UserInfo) {
        println("sending user")
        println(ui.email)
        println(ui.password)
        runBlocking { uid.insertUserInfo(ui) }
    }

}