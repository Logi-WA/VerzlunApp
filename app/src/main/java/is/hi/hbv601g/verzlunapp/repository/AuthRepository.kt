package `is`.hi.hbv601g.verzlunapp.repository

import `is`.hi.hbv601g.verzlunapp.model.LoginRequest
import `is`.hi.hbv601g.verzlunapp.model.LoginResponse
import `is`.hi.hbv601g.verzlunapp.model.SignupRequest
import `is`.hi.hbv601g.verzlunapp.model.SignupResponse
import `is`.hi.hbv601g.verzlunapp.network.RetrofitClient
import retrofit2.Response

class AuthRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return apiService.login(LoginRequest(username, password))
    }

    suspend fun signup(
        username: String,
        email: String,
        password: String
    ): Response<SignupResponse> {
        return apiService.signup(SignupRequest(username, email, password))
    }
}
