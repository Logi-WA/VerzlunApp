package `is`.hi.hbv601g.verzlunapp.network

import `is`.hi.hbv601g.verzlunapp.model.LoginRequest
import `is`.hi.hbv601g.verzlunapp.model.LoginResponse
import `is`.hi.hbv601g.verzlunapp.model.SignupRequest
import `is`.hi.hbv601g.verzlunapp.model.SignupResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun signup(@Body signupRequest: SignupRequest): Response<SignupResponse>

    @PATCH("api/users/me")
    fun updateProfile(@Body userDetails: Map<String, String>): Call<Any>

    @PATCH("api/users/me/password")
    fun changePassword(@Body passwordDetails: Map<String, String>): Call<ResponseBody>
}
