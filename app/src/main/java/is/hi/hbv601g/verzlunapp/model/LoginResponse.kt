package `is`.hi.hbv601g.verzlunapp.model

data class LoginData(
    val userId: String,
    val name: String,
    val email: String,
    val token: String,
    val type: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData,
    val timestamp: String
)