package `is`.hi.hbv601g.verzlunapp.model

data class SignupResponse(
    val success: Boolean,
    val message: String?,
    val data: UserData?,
    val timestamp: String?
)