package `is`.hi.hbv601g.verzlunapp.network

import `is`.hi.hbv601g.verzlunapp.model.SignupRequest
import `is`.hi.hbv601g.verzlunapp.model.SignupResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ApiService {

    @POST("api/users")
    fun signup(@Body signupRequest: SignupRequest): Call<SignupResponse>

    @PATCH("api/users/me")
    fun updateProfile(@Body userDetails: Map<String, String>): Call<Any>

    @PATCH("api/users/me/password")
    fun changePassword(@Body passwordDetails: Map<String, String>): Call<ResponseBody>
}
