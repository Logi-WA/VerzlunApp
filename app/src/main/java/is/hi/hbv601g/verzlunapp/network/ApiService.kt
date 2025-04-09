package `is`.hi.hbv601g.verzlunapp.network

import `is`.hi.hbv601g.verzlunapp.model.CategoryData
import `is`.hi.hbv601g.verzlunapp.model.CategoryListResponse
import `is`.hi.hbv601g.verzlunapp.model.GenericApiResponse
import `is`.hi.hbv601g.verzlunapp.model.LoginRequest
import `is`.hi.hbv601g.verzlunapp.model.LoginResponse
import `is`.hi.hbv601g.verzlunapp.model.ProductData
import `is`.hi.hbv601g.verzlunapp.model.SignupRequest
import `is`.hi.hbv601g.verzlunapp.model.SignupResponse
import `is`.hi.hbv601g.verzlunapp.model.UserData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {

    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("api/users")
    fun signup(@Body signupRequest: SignupRequest): Call<SignupResponse>

    @PATCH("api/users/me")
    fun updateProfile(@Body userDetails: Map<String, String>): Call<GenericApiResponse<UserData>>

    @PATCH("api/users/me/password")
    fun changePassword(@Body passwordDetails: Map<String, String>): Call<ResponseBody>

    @GET("/api/categories")
    fun getCategories(@Header("Authorization") authHeader: String): Call<CategoryListResponse>

    @GET("api/products")
    fun getProducts(
        @Query("category") category: String?
    ): Call<GenericApiResponse<List<ProductData>>>

    @GET("api/categories")
    fun getCategories(): Call<GenericApiResponse<List<CategoryData>>>
}